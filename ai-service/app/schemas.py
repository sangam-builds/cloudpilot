from pydantic import BaseModel, Field
from typing import List, Optional, Dict, Any


class ClassifyRequest(BaseModel):
    subject: str = Field(..., description="Ticket subject line")
    description: str = Field(..., description="Full ticket body description")


class ClassifyResponse(BaseModel):
    category: str = Field(..., description="Assigned category e.g. Payments, Technical Support")
    priority: str = Field(..., description="HIGH, MEDIUM, or LOW")
    sentiment: str = Field(..., description="POSITIVE, NEUTRAL, NEGATIVE, or FRUSTRATED")
    department: str = Field(..., description="Target department")
    confidence: float = Field(..., ge=0.0, le=1.0, description="Confidence score")
    extracted_keywords: List[str] = Field(default_factory=list, description="Extracted keywords")
    rationale: Optional[str] = Field(None, description="Explanation for classification")


class SuggestReplyRequest(BaseModel):
    subject: str
    description: str
    ticket_id: Optional[int] = None


class FaqMatch(BaseModel):
    id: int
    category: str
    question: str
    answer: str
    similarity_score: float


class SuggestReplyResponse(BaseModel):
    suggested_reply: str
    matched_faqs: List[FaqMatch] = Field(default_factory=list)
    confidence: float = Field(..., ge=0.0, le=1.0)


class SummaryRequest(BaseModel):
    customer_name: str
    ticket_history: List[str] = Field(default_factory=list)
    order_history: List[str] = Field(default_factory=list)


class SummaryResponse(BaseModel):
    summary: str
    sentiment: str = "NEUTRAL"
    account_health: str = "GOOD"


class SimilarTicketsRequest(BaseModel):
    ticket_text: str
    limit: int = 5


class SimilarTicketItem(BaseModel):
    ticket_id: int
    subject: str
    similarity_score: float


class SimilarTicketsResponse(BaseModel):
    similar_tickets: List[SimilarTicketItem] = Field(default_factory=list)
