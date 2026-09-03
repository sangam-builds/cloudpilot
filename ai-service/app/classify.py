import json
import logging
import re
from typing import Dict, Any, List
from app.schemas import ClassifyResponse
from app.config import settings

logger = logging.getLogger(__name__)

CATEGORY_RULES = {
    "Payments": {
        "keywords": ["invoice", "charge", "refund", "billing", "payment", "credit card", "stripe", "double charge", "receipt", "tax", "vat", "wire transfer"],
        "department": "Payments & Billing"
    },
    "Technical Support": {
        "keywords": ["api", "429", "500", "504", "rate limit", "gateway timeout", "webhook", "hmac", "signature", "bug", "crash", "cluster", "latency", "sdk", "database"],
        "department": "Technical Support"
    },
    "Shipping & Logistics": {
        "keywords": ["shipment", "tracking", "fedex", "dhl", "customs", "delivery", "carrier", "pallet", "warehouse", "delayed", "in transit"],
        "department": "Shipping & Logistics"
    },
    "Account & Security": {
        "keywords": ["sso", "saml", "okta", "2fa", "two-factor", "password", "security", "soc2", "token", "locked out", "phishing", "certificate", "audit"],
        "department": "Account & Security"
    },
    "Returns & Refunds": {
        "keywords": ["rma", "return", "broken", "damaged", "replacement", "warranty", "exchange", "store credit", "license seat"],
        "department": "Returns & Refunds"
    }
}

PRIORITY_KEYWORDS_HIGH = ["urgent", "down", "outage", "504", "double charge", "security breach", "locked out", "immediate", "critical", "breached", "blocker"]
PRIORITY_KEYWORDS_LOW = ["minor", "feedback", "question", "info", "license seats", "inquiry", "how to"]

SENTIMENT_KEYWORDS_FRUSTRATED = ["unacceptable", "terrible", "frustrated", "immediately", "broken", "awful", "horrible", "still waiting", "angry", "disaster"]
SENTIMENT_KEYWORDS_NEGATIVE = ["delayed", "error", "failing", "failed", "damaged", "issue", "problem", "wrong", "missing"]
SENTIMENT_KEYWORDS_POSITIVE = ["thank", "thanks", "great", "awesome", "appreciate", "helpful", "good"]


def rule_based_classify(subject: str, description: str) -> ClassifyResponse:
    combined = f"{subject} {description}".lower()

    # Category scoring
    scores: Dict[str, int] = {}
    matched_words: List[str] = []

    for cat, data in CATEGORY_RULES.items():
        score = 0
        for kw in data["keywords"]:
            if kw in combined:
                score += 2 if kw in subject.lower() else 1
                matched_words.append(kw)
        scores[cat] = score

    best_category = max(scores, key=scores.get) if any(scores.values()) else "Technical Support"
    department = CATEGORY_RULES[best_category]["department"]

    # Priority determination
    priority = "MEDIUM"
    if any(kw in combined for kw in PRIORITY_KEYWORDS_HIGH):
        priority = "HIGH"
    elif any(kw in combined for kw in PRIORITY_KEYWORDS_LOW) and not any(kw in combined for kw in PRIORITY_KEYWORDS_HIGH):
        priority = "LOW"

    # Sentiment determination
    sentiment = "NEUTRAL"
    if any(kw in combined for kw in SENTIMENT_KEYWORDS_FRUSTRATED):
        sentiment = "FRUSTRATED"
    elif any(kw in combined for kw in SENTIMENT_KEYWORDS_NEGATIVE):
        sentiment = "NEGATIVE"
    elif any(kw in combined for kw in SENTIMENT_KEYWORDS_POSITIVE):
        sentiment = "POSITIVE"

    confidence = 0.85 if matched_words else 0.60

    return ClassifyResponse(
        category=best_category,
        priority=priority,
        sentiment=sentiment,
        department=department,
        confidence=confidence,
        extracted_keywords=list(set(matched_words))[:6],
        rationale=f"Rule-based classification matched {len(matched_words)} category keywords."
    )


async def llm_classify(subject: str, description: str) -> ClassifyResponse:
    if not settings.OPENAI_API_KEY:
        raise ValueError("No OPENAI_API_KEY configured")

    import httpx

    prompt = f"""You are an enterprise AI ticket classification engine. Analyze the support ticket below.
Return a STRICT JSON object with these exact keys:
{{
  "category": "Payments" | "Technical Support" | "Shipping & Logistics" | "Account & Security" | "Returns & Refunds",
  "priority": "HIGH" | "MEDIUM" | "LOW",
  "sentiment": "POSITIVE" | "NEUTRAL" | "NEGATIVE" | "FRUSTRATED",
  "department": string matching category name,
  "confidence": float between 0.0 and 1.0,
  "extracted_keywords": array of relevant keywords,
  "rationale": concise 1-sentence reasoning
}}

Ticket Subject: {subject}
Ticket Description: {description}
"""

    headers = {
        "Authorization": f"Bearer {settings.OPENAI_API_KEY}",
        "Content-Type": "application/json"
    }
    payload = {
        "model": settings.LLM_MODEL,
        "messages": [
            {"role": "system", "content": "You are a customer service triage classifier. Respond only in valid JSON."},
            {"role": "user", "content": prompt}
        ],
        "temperature": 0.1,
        "response_format": {"type": "json_object"}
    }

    async with httpx.AsyncClient(timeout=settings.LLM_TIMEOUT_SECONDS) as client:
        response = await client.post("https://api.openai.com/v1/chat/completions", json=payload, headers=headers)
        response.raise_for_status()
        data = response.json()
        content = data["choices"][0]["message"]["content"]
        parsed = json.loads(content)
        return ClassifyResponse(**parsed)


async def classify(subject: str, description: str) -> ClassifyResponse:
    """
    Attempts LLM classification and falls back seamlessly to rule-based keyword classification.
    """
    try:
        if settings.OPENAI_API_KEY:
            return await llm_classify(subject, description)
    except Exception as e:
        logger.info("LLM classification bypassed (%s). Falling back to rule-based engine.", e)

    return rule_based_classify(subject, description)
