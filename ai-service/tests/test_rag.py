import pytest
from app.rag import retrieve_top_k, generate_reply


def test_retrieve_top_k():
    matches = retrieve_top_k("How do I get a refund for double charge?", k=3)
    assert len(matches) == 3
    assert any(m.category == "Payments" for m in matches)


@pytest.mark.asyncio
async def test_generate_reply():
    res = await generate_reply("Webhook signature failure", "HMAC verification digest is failing on our receiver.")
    assert res.suggested_reply is not None
    assert len(res.suggested_reply) > 20
    assert res.confidence >= 0.4
