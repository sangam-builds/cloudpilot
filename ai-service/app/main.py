import logging
import time
from fastapi import FastAPI, Request
from fastapi.middleware.cors import CORSMiddleware
from app.config import settings
from app.schemas import (
    ClassifyRequest, ClassifyResponse,
    SuggestReplyRequest, SuggestReplyResponse,
    SummaryRequest, SummaryResponse,
    SimilarTicketsRequest, SimilarTicketsResponse
)
from app.classify import classify
from app.rag import generate_reply, find_similar_tickets
from app.customer_summary import generate_customer_summary

logging.basicConfig(level=logging.INFO, format="%(asctime)s [%(levelname)s] %(name)s: %(message)s")
logger = logging.getLogger("cloudpilot-ai")

app = FastAPI(
    title=settings.APP_NAME,
    version=settings.APP_VERSION,
    description="FastAPI service for ticket triage, NLP categorization, RAG reply generation, and Customer 360 summaries."
)

# CORS
app.add_middleware(
    CORSMiddleware,
    allow_origins=settings.ALLOWED_ORIGINS,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


@app.middleware("http")
async def add_process_time_header(request: Request, call_next):
    start_time = time.time()
    response = await call_next(request)
    process_time = time.time() - start_time
    response.headers["X-Process-Time"] = f"{process_time:.4f}s"
    return response


@app.get("/health", tags=["Health"])
async def health_check():
    return {
        "status": "UP",
        "service": settings.APP_NAME,
        "version": settings.APP_VERSION
    }


@app.post("/classify", response_model=ClassifyResponse, tags=["Classification"])
async def classify_ticket(req: ClassifyRequest):
    logger.info("Classifying ticket subject: '%s'", req.subject)
    return await classify(req.subject, req.description)


@app.post("/suggest-reply", response_model=SuggestReplyResponse, tags=["RAG"])
async def suggest_reply_endpoint(req: SuggestReplyRequest):
    logger.info("Generating suggested reply for ticket: '%s'", req.subject)
    return await generate_reply(req.subject, req.description)


@app.post("/customer-summary", response_model=SummaryResponse, tags=["Customer 360"])
async def customer_summary_endpoint(req: SummaryRequest):
    logger.info("Generating customer summary for: '%s'", req.customer_name)
    return generate_customer_summary(req.customer_name, req.ticket_history, req.order_history)


@app.post("/similar-tickets", response_model=SimilarTicketsResponse, tags=["RAG"])
async def similar_tickets_endpoint(req: SimilarTicketsRequest):
    items = find_similar_tickets(req.ticket_text, req.limit)
    return SimilarTicketsResponse(similar_tickets=items)


if __name__ == "__main__":
    import uvicorn
    uvicorn.run("main:app", host="0.0.0.0", port=8000, reload=True)
