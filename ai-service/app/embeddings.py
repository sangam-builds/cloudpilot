import numpy as np
from typing import List, Dict, Any, Optional
import logging

logger = logging.getLogger(__name__)

# Preloaded default knowledge base
DEFAULT_FAQS = [
    {
        "id": 1,
        "category": "Payments",
        "question": "How do I request a refund for an accidental duplicate transaction?",
        "answer": "To request a duplicate charge refund, navigate to Billing > Invoices, locate the transaction ID, and select Request Refund. Our automated billing engine will verify the authorization and credit your payment method within 3–5 business days."
    },
    {
        "id": 2,
        "category": "Payments",
        "question": "Which payment methods and currencies are supported?",
        "answer": "CloudPilot supports Visa, MasterCard, American Express, ACH direct debits, and SEPA wire transfers in USD, EUR, GBP, CAD, and JPY."
    },
    {
        "id": 3,
        "category": "Payments",
        "question": "How do I update my organization tax ID or VAT exemption certificate?",
        "answer": "Go to Settings > Billing Profile > Tax Configuration. Upload your VAT / GST registration certificate for review. Verification takes up to 24 hours."
    },
    {
        "id": 4,
        "category": "Technical Support",
        "question": "What should I do if I encounter HTTP 429 Too Many Requests?",
        "answer": "HTTP 429 indicates rate limiting. Verify your subscription tier burst limits. Implement exponential backoff retry algorithms with jitter in your client SDK, or contact support to request a quota increase."
    },
    {
        "id": 5,
        "category": "Technical Support",
        "question": "How do I verify webhook cryptographic signatures?",
        "answer": "Each webhook request includes an X-CloudPilot-Signature header containing an HMAC-SHA256 digest computed using your webhook signing secret. Compute the digest over the raw request body and compare using constant-time comparison."
    },
    {
        "id": 6,
        "category": "Technical Support",
        "question": "What are the IP addresses for CloudPilot outbound webhook egress?",
        "answer": "Our outbound webhook traffic originates from 54.210.12.0/24 and 52.88.45.0/24. Ensure your corporate firewall allowlists these CIDR blocks on port 443."
    },
    {
        "id": 7,
        "category": "Shipping & Logistics",
        "question": "How do I track international shipments and customs clearance status?",
        "answer": "Real-time tracking is available in the Logistics Portal under Deliveries. Detailed customs clearance status, commercial invoices, and carrier waybills can be downloaded directly from the shipment summary."
    },
    {
        "id": 8,
        "category": "Shipping & Logistics",
        "question": "What is the procedure for reporting damaged or missing packages?",
        "answer": "Notify CloudPilot Support within 48 hours of delivery scan. Provide high-resolution photos of external packaging, shipping label, and damaged contents to initiate an immediate carrier claim and expedited replacement."
    },
    {
        "id": 9,
        "category": "Account & Security",
        "question": "How do I configure Single Sign-On (SSO) with Okta, Azure AD, or Google Workspace?",
        "answer": "Navigate to Admin Console > Security > SSO. CloudPilot supports SAML 2.0 and OpenID Connect (OIDC). Download our SP metadata XML, configure your Identity Provider, and upload your IdP certificate."
    },
    {
        "id": 10,
        "category": "Account & Security",
        "question": "What happens if a user is locked out after multiple failed 2FA attempts?",
        "answer": "Workspace administrators can issue a temporary one-time bypass token from Admin > Users > Security Settings. Users can also authenticate via emergency recovery codes generated during 2FA setup."
    },
    {
        "id": 11,
        "category": "Returns & Refunds",
        "question": "What is the Return Merchandise Authorization (RMA) process for hardware?",
        "answer": "Submit an RMA request via the Support Center with the hardware serial number. Upon review, a prepaid return shipping label and packing slip will be issued. Replacements are dispatched upon receipt scan."
    },
    {
        "id": 12,
        "category": "Returns & Refunds",
        "question": "Are digital software licenses refundable?",
        "answer": "Digital license subscriptions can be cancelled for a full prorated refund within 14 days of purchase. After 14 days, cancellation takes effect at the end of the current billing cycle."
    }
]


class EmbeddingEngine:
    _instance = None

    def __init__(self):
        self.model = None
        self.faq_embeddings: List[np.ndarray] = []
        self.faqs: List[Dict[str, Any]] = DEFAULT_FAQS

    @classmethod
    def get_instance(cls):
        if cls._instance is None:
            cls._instance = EmbeddingEngine()
        return cls._instance

    def load_model(self):
        if self.model is None:
            from app.config import settings
            if settings.USE_LIGHTWEIGHT_EMBEDDINGS:
                logger.info("USE_LIGHTWEIGHT_EMBEDDINGS=True: Using low-memory vector engine (<50MB RAM).")
                self.model = "FALLBACK"
                self._index_faqs_fallback()
                return

            try:
                import torch
                torch.set_num_threads(1)
                from sentence_transformers import SentenceTransformer
                logger.info("Loading SentenceTransformer model: %s", settings.EMBEDDING_MODEL_NAME)
                self.model = SentenceTransformer(settings.EMBEDDING_MODEL_NAME)
                self._index_faqs()
            except Exception as e:
                logger.warning("Could not initialize SentenceTransformer (%s). Fallback embeddings enabled.", e)
                self.model = "FALLBACK"
                self._index_faqs_fallback()

    def _index_faqs(self):
        if self.model is not None and self.model != "FALLBACK":
            texts = [f"{faq['question']} {faq['answer']}" for faq in self.faqs]
            self.faq_embeddings = self.model.encode(texts, convert_to_numpy=True)
            logger.info("Indexed %d FAQs into vector space.", len(self.faqs))

    def _index_faqs_fallback(self):
        # Deterministic lightweight term frequency vectors for low-memory environments
        self.faq_embeddings = [self._fallback_vector(f"{faq['question']} {faq['answer']}") for faq in self.faqs]

    def _fallback_vector(self, text: str, dim: int = 256) -> np.ndarray:
        vec = np.zeros(dim, dtype=np.float32)
        words = text.lower().replace("-", " ").split()
        for w in words:
            # Unigram hashing
            idx = abs(hash(w)) % dim
            vec[idx] += 1.0
        # Bigram hashing for context preservation
        for i in range(len(words) - 1):
            bigram = f"{words[i]}_{words[i+1]}"
            b_idx = abs(hash(bigram)) % dim
            vec[b_idx] += 1.5
        norm = np.linalg.norm(vec)
        return vec / norm if norm > 0 else vec

    def embed_text(self, text: str) -> np.ndarray:
        self.load_model()
        if self.model != "FALLBACK" and hasattr(self.model, "encode"):
            return self.model.encode(text, convert_to_numpy=True)
        return self._fallback_vector(text)

    def cosine_similarity(self, vec_a: np.ndarray, vec_b: np.ndarray) -> float:
        dot = np.dot(vec_a, vec_b)
        norm_a = np.linalg.norm(vec_a)
        norm_b = np.linalg.norm(vec_b)
        if norm_a == 0 or norm_b == 0:
            return 0.0
        return float(dot / (norm_a * norm_b))


embedding_engine = EmbeddingEngine.get_instance()
