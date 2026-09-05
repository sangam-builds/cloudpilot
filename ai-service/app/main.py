import logging
import time
from fastapi import FastAPI, Request, Response
from fastapi.middleware.cors import CORSMiddleware
from prometheus_client import (
    Counter, Histogram, Gauge, generate_latest, CONTENT_TYPE_LATEST
)
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

# Prometheus Metrics
AI_REQUEST_COUNT = Counter(
    "ai_http_requests_total",
    "Total HTTP requests to AI microservice",
    ["method", "endpoint", "status"]
)
AI_REQUEST_LATENCY = Histogram(
    "ai_http_request_duration_seconds",
    "HTTP request latency for AI microservice",
    ["endpoint"],
    buckets=[0.01, 0.05, 0.1, 0.25, 0.5, 1.0, 2.5, 5.0, 10.0]
)
AI_CLASSIFICATIONS_TOTAL = Counter(
    "ai_classifications_total",
    "Total NLP ticket classifications performed",
    ["category", "priority", "sentiment"]
)
AI_RAG_REPLIES_TOTAL = Counter(
    "ai_rag_replies_total",
    "Total RAG draft responses generated"
)

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
async def add_process_time_and_metrics(request: Request, call_next):
    start_time = time.time()
    response = await call_next(request)
    process_time = time.time() - start_time
    response.headers["X-Process-Time"] = f"{process_time:.4f}s"
    
    # Record metrics (strip dynamic query params for cardinality control)
    path = request.url.path
    if path != "/metrics":
        try:
            AI_REQUEST_COUNT.labels(
                method=request.method,
                endpoint=path,
                status=response.status_code
            ).inc()
            AI_REQUEST_LATENCY.labels(endpoint=path).observe(process_time)
        except Exception:
            pass
            
    return response


@app.get("/health", tags=["Health"])
async def health_check():
    return {
        "status": "UP",
        "service": settings.APP_NAME,
        "version": settings.APP_VERSION
    }


@app.get("/metrics", tags=["Metrics"])
def prometheus_metrics():
    """Exposes Prometheus-formatted metrics for scraping."""
    return Response(content=generate_latest(), media_type=CONTENT_TYPE_LATEST)


@app.post("/classify", response_model=ClassifyResponse, tags=["Classification"])
async def classify_ticket(req: ClassifyRequest):
    logger.info("Classifying ticket subject: '%s'", req.subject)
    result = await classify(req.subject, req.description)
    try:
        AI_CLASSIFICATIONS_TOTAL.labels(
            category=result.category,
            priority=result.priority,
            sentiment=result.sentiment
        ).inc()
    except Exception:
        pass
    return result


@app.post("/suggest-reply", response_model=SuggestReplyResponse, tags=["RAG"])
async def suggest_reply_endpoint(req: SuggestReplyRequest):
    logger.info("Generating suggested reply for ticket: '%s'", req.subject)
    result = await generate_reply(req.subject, req.description)
    try:
        AI_RAG_REPLIES_TOTAL.inc()
    except Exception:
        pass
    return result


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

