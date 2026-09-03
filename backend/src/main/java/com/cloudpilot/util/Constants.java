package com.cloudpilot.util;

public final class Constants {

    private Constants() {}

    public static final String ROLE_CUSTOMER = "ROLE_CUSTOMER";
    public static final String ROLE_AGENT = "ROLE_AGENT";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";

    public static final String ACTOR_SYSTEM = "SYSTEM";

    public static final String METRIC_TICKETS_CREATED = "tickets.created";
    public static final String METRIC_TICKETS_RESOLVED = "tickets.resolved";
    public static final String METRIC_SLA_BREACHES = "sla.breaches";
    public static final String METRIC_AI_REQUESTS = "ai.requests";
    public static final String METRIC_AI_FAILURES = "ai.failures";
}
