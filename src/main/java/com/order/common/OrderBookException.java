package com.order.common;

/**
 * Order book exception
 */
public class OrderBookException extends RuntimeException {
    public OrderBookException(String message) {
        super(message);
    }
}
