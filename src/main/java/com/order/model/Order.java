package com.order.model;

import com.order.enumeration.OrderType;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * An order is defined in terms of an order id, quantity, entry date, price and instrument id.
 */
public class Order {

    private final long orderId;

    private final OrderType orderType;

    private final int orderQuantity;

    private int executionQuantity;

    private final LocalDateTime entryDate = LocalDateTime.now();

    private final String instrumentId;

    private Double orderPrice;

    private double executionPrice;

    private boolean invalid = false;

    /**
     *
     * @param orderId
     * @param orderQuantity
     * @param instrumentId
     */
    public Order(long orderId, int orderQuantity, String instrumentId) {
        this.orderId = orderId;
        this.orderQuantity = orderQuantity;
        this.instrumentId = instrumentId;
        this.orderType = OrderType.MARKET;
    }

    /**
     *
     * @param orderId
     * @param orderQuantity
     * @param instrumentId
     * @param orderPrice
     */
    public Order(long orderId, int orderQuantity, String instrumentId, Double orderPrice) {
        this.orderId = orderId;
        this.orderQuantity = orderQuantity;
        this.instrumentId = instrumentId;
        this.orderPrice = orderPrice;
        this.orderType = orderPrice != null ? OrderType.LIMIT : OrderType.MARKET;
    }

    /**
     *
     * @return
     */
    public synchronized boolean isInvalid() {
        return invalid;
    }

    /**
     *
     * @return
     */
    public boolean isExecuted() {
        return executionQuantity == orderQuantity;
    }

    /**
     *
     * @param invalid
     */
    public synchronized void setInvalid(boolean invalid) {
        this.invalid = invalid;
    }

    /**
     *
     * @param executionQuantity
     */
    public synchronized void setExecutionQuantity(int executionQuantity) {
        if (executionQuantity > orderQuantity) {
            throw new IllegalArgumentException(String.format("Order is over executed! executionQuantity=%d orderQuantity=%d"));
        }

        this.executionQuantity = executionQuantity;
    }

    /**
     *
     * @param executionPrice
     */
    public synchronized void setExecutionPrice(double executionPrice) {
        this.executionPrice = executionPrice;
    }

    /**
     *
     * @return
     */
    public synchronized double getExecutionPrice() {
        return executionPrice;
    }

    /**
     *
     * @return
     */
    public long getOrderId() {
        return orderId;
    }

    /**
     *
     * @return
     */
    public String getInstrumentId() {
        return instrumentId;
    }

    /**
     *
     * @return
     */
    public Double getOrderPrice() {
        return orderPrice;
    }

    /**
     *
     * @return
     */
    public int getOrderQuantity() {
        return orderQuantity;
    }

    /**
     *
     * @return
     */
    public synchronized int getExecutionQuantity() {
        return executionQuantity;
    }

    /**
     *
     * @return
     */
    public OrderType getOrderType() { return orderType; }

    /**
     *
     * @return
     */
    public LocalDateTime getEntryDate() { return entryDate; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(orderId, order.orderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId);
    }

    @Override
    public String toString() {
        return Long.toString(orderId);
    }
}
