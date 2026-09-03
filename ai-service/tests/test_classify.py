import pytest
from app.classify import rule_based_classify


def test_classify_payments():
    res = rule_based_classify(
        subject="Double charge on March invoice #INV-9481",
        description="We were billed twice on our credit card for the monthly subscription."
    )
    assert res.category == "Payments"
    assert res.priority == "HIGH"
    assert res.department == "Payments & Billing"


def test_classify_technical():
    res = rule_based_classify(
        subject="HTTP 429 rate limit errors on API batch sync",
        description="Our webhook integration is failing with rate limit errors."
    )
    assert res.category == "Technical Support"
    assert res.department == "Technical Support"


def test_classify_shipping():
    res = rule_based_classify(
        subject="Pallet delivery delayed at Frankfurt customs with DHL tracking",
        description="Shipment tracking has been stuck at customs for 4 days."
    )
    assert res.category == "Shipping & Logistics"


def test_classify_security():
    res = rule_based_classify(
        subject="SSO SAML certificate renewal and 2FA lockout",
        description="Need urgent assistance with Okta SAML certificate."
    )
    assert res.category == "Account & Security"
    assert res.priority == "HIGH"
