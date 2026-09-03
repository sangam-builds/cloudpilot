import logging
from typing import List, Tuple
from app.embeddings import embedding_engine, DEFAULT_FAQS
from app.schemas import FaqMatch, SuggestReplyResponse, SimilarTicketItem
from app.config import settings

logger = logging.getLogger(__name__)


def retrieve_top_k(query_text: str, k: int = 3) -> List[FaqMatch]:
    """
    Computes vector cosine similarity between query and pre-indexed FAQ knowledge base.
    """
    query_vec = embedding_engine.embed_text(query_text)
    scored: List[Tuple[float, dict]] = []

    for idx, faq in enumerate(embedding_engine.faqs):
        faq_vec = embedding_engine.faq_embeddings[idx]
        sim = embedding_engine.cosine_similarity(query_vec, faq_vec)
        scored.append((sim, faq))

    scored.sort(key=lambda x: x[0], reverse=True)
    top_matches = scored[:k]

    return [
        FaqMatch(
            id=item[1]["id"],
            category=item[1]["category"],
            question=item[1]["question"],
            answer=item[1]["answer"],
            similarity_score=round(float(item[0]), 4)
        )
        for item in top_matches
    ]


async def generate_reply(subject: str, description: str) -> SuggestReplyResponse:
    query = f"{subject} {description}"
    matched_faqs = retrieve_top_k(query, k=2)

    top_faq = matched_faqs[0] if matched_faqs else None
    confidence = top_faq.similarity_score if top_faq else 0.5

    # If top FAQ has strong similarity, synthesize grounded draft response
    if top_faq and top_faq.similarity_score >= 0.25:
        draft = (
            f"Hello,\n\n"
            f"Thank you for contacting CloudPilot Support regarding \"{subject}\".\n\n"
            f"Based on our knowledge base for {top_faq.category}:\n"
            f"{top_faq.answer}\n\n"
            f"Please let us know if you have any further questions or if you need additional assistance.\n\n"
            f"Best regards,\nCloudPilot Support Team"
        )
    else:
        draft = (
            f"Hello,\n\n"
            f"Thank you for reaching out to CloudPilot Support. We have received your ticket regarding \"{subject}\" "
            f"and our team is reviewing the technical details. We will provide an update as soon as possible.\n\n"
            f"Best regards,\nCloudPilot Support Team"
        )

    return SuggestReplyResponse(
        suggested_reply=draft,
        matched_faqs=matched_faqs,
        confidence=min(1.0, max(0.4, confidence))
    )


def find_similar_tickets(ticket_text: str, limit: int = 3) -> List[SimilarTicketItem]:
    # Mock similar historical corpus for fast retrieval
    sample_corpus = [
        {"id": 101, "subject": "Double charged on monthly enterprise billing cycle"},
        {"id": 102, "subject": "Webhook HMAC signature mismatch on delivery verification"},
        {"id": 103, "subject": "Customs clearance delay for EU international air shipping"},
        {"id": 104, "subject": "SSO Okta assertion failure on user authentication"},
        {"id": 105, "subject": "Hardware RMA return damaged in transit replacement request"}
    ]

    query_vec = embedding_engine.embed_text(ticket_text)
    scored = []

    for item in sample_corpus:
        item_vec = embedding_engine.embed_text(item["subject"])
        sim = embedding_engine.cosine_similarity(query_vec, item_vec)
        scored.append((sim, item))

    scored.sort(key=lambda x: x[0], reverse=True)
    return [
        SimilarTicketItem(
            ticket_id=item[1]["id"],
            subject=item[1]["subject"],
            similarity_score=round(float(item[0]), 4)
        )
        for item in scored[:limit]
    ]
