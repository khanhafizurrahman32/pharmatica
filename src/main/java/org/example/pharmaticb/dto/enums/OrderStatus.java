package org.example.pharmaticb.dto.enums;

public enum OrderStatus {
    INITIATED(1),
    ACCEPTED(2),
    COMPLETED(3),
    FAILED(4);

    private final int precedence;

    OrderStatus(int precedence) {
        this.precedence = precedence;
    }

    public int getPrecedence() {
        return precedence;
    }

    public boolean canTransitionTo(OrderStatus newStatus) {
        return this.precedence < newStatus.precedence;
    }
}
