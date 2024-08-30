package org.example.pharmaticb.dto.enums;

public enum OrderStatus {
    INITIATED(1),
    ACCEPTED(2),
    ON_THE_WAY(3),
    COMPLETED(4),
    FAILED(5);

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
