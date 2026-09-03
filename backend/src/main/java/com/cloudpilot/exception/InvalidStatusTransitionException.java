package com.cloudpilot.exception;

public class InvalidStatusTransitionException extends RuntimeException {
    public InvalidStatusTransitionException(String fromStatus, String toStatus) {
        super(String.format("Invalid ticket status transition from '%s' to '%s'", fromStatus, toStatus));
    }
}
