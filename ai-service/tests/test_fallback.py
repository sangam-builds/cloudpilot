import pytest
from app.classify import classify


@pytest.mark.asyncio
async def test_classify_graceful_fallback():
    # Without OPENAI_API_KEY configured, classify() must return valid response from rule-based fallback
    res = await classify("Outage on EU production database cluster", "504 gateway timeout on all endpoints")
    assert res.category is not None
    assert res.priority == "HIGH"
    assert res.confidence > 0.0
