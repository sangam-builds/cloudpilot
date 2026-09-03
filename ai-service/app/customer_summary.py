from app.schemas import SummaryResponse
from typing import List


def generate_customer_summary(customer_name: str, ticket_history: List[str], order_history: List[str]) -> SummaryResponse:
    ticket_count = len(ticket_history)
    order_count = len(order_history)

    sentiment = "POSITIVE" if ticket_count <= 1 else ("FRUSTRATED" if ticket_count >= 4 else "NEUTRAL")
    health = "EXCELLENT" if order_count >= 3 and ticket_count <= 1 else ("ATTENTION" if ticket_count >= 3 else "HEALTHY")

    recent_issue = ticket_history[0] if ticket_history else "no recent support tickets"

    summary_text = (
        f"{customer_name} is an enterprise client with {order_count} completed orders and {ticket_count} total tickets. "
        f"Most recent inquiry relates to '{recent_issue}'. "
        f"Overall account status is rated as {health.lower()}."
    )

    return SummaryResponse(
        summary=summary_text,
        sentiment=sentiment,
        account_health=health
    )
