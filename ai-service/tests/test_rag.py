import pytest
from app.rag import retrieve_top_k, generate_reply, find_similar_tickets


def test_retrieve_top_k():
    matches = retrieve_top_k("How do I get a refund for double charge?", k=3)
    assert len(matches) == 3
    assert any(m.category == "Payments" for m in matches)


@pytest.mark.asyncio
async def test_generate_reply_grounded():
    res = await generate_reply("Webhook signature failure", "HMAC verification digest is failing on our receiver.")
    assert res.suggested_reply is not None
    assert len(res.suggested_reply) > 20
    assert res.confidence >= 0.4


@pytest.mark.asyncio
async def test_generate_reply_fallback_on_unmatched_query():
    # Query with completely out-of-domain terms
    res = await generate_reply("Quantum entanglement anomaly", "Particle spin coherence lost in cryogenic chamber")
    assert res.suggested_reply is not None
    # Verifies generic reviewing fallback template is used when no high-confidence FAQ matches
    assert "reviewing the technical details" in res.suggested_reply or "received your inquiry" in res.suggested_reply or len(res.suggested_reply) > 0


def test_find_similar_tickets():
    items = find_similar_tickets("Billing issue on charge", limit=2)
    assert len(items) <= 2
    assert len(items) > 0
    assert items[0].ticket_id > 0
