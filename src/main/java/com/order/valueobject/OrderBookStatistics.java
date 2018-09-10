package com.order.valueobject;

import com.order.model.Order;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Order book statistics.
 */
public class OrderBookStatistics {

    private String instrumentId;

    private String status;

    private int orderSize;

    private int totalDemand;

    private int invalidDemand;

    private int validDemand;

    private long invalidOrders;

    private long validOrders;

    private Double executionPrice;

    private int executionAmount;

    private boolean executed;

    private Map<String, Order> orderBreakdown = new HashMap<>();

    private Map<Double, Integer> limitBreakdown = new HashMap<>();

    public OrderBookStatistics(String instrumentId, String status, int orderSize, int totalDemand, int invalidDemand, int validDemand, long invalidOrders, long validOrders, int executionAmount, Double executionPrice, boolean executed) {
        this.instrumentId = instrumentId;
        this.status = status;
        this.orderSize = orderSize;
        this.totalDemand = totalDemand;
        this.invalidDemand = invalidDemand;
        this.validDemand = validDemand;
        this.invalidOrders = invalidOrders;
        this.validOrders = validOrders;
        this.executionAmount = executionAmount;
        this.executionPrice = executionPrice;
        this.executed = executed;
    }

    public String getInstrumentId() {
        return instrumentId;
    }

    public void setInstrumentId(String instrumentId) {
        this.instrumentId = instrumentId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getOrderSize() {
        return orderSize;
    }

    public void setOrderSize(int orderSize) {
        this.orderSize = orderSize;
    }

    public int getTotalDemand() {
        return totalDemand;
    }

    public void setTotalDemand(int totalDemand) {
        this.totalDemand = totalDemand;
    }

    public int getInvalidDemand() {
        return invalidDemand;
    }

    public void setInvalidDemand(int invalidDemand) {
        this.invalidDemand = invalidDemand;
    }

    public int getValidDemand() {
        return validDemand;
    }

    public void setValidDemand(int validDemand) {
        this.validDemand = validDemand;
    }

    public long getInvalidOrders() {
        return invalidOrders;
    }

    public void setInvalidOrders(long invalidOrders) {
        this.invalidOrders = invalidOrders;
    }

    public long getValidOrders() {
        return validOrders;
    }

    public void setValidOrders(long validOrders) {
        this.validOrders = validOrders;
    }

    public Map<String, Order> getOrderBreakdown() {
        return orderBreakdown;
    }

    public void setOrderBreakdown(Map<String, Order> orderBreakdown) {
        this.orderBreakdown = orderBreakdown;
    }

    public void addOrderBreakdown(String orderCategory, Order order) {
        orderBreakdown.put(orderCategory, order);
    }

    public Map<Double, Integer> getLimitBreakdown() {
        return limitBreakdown;
    }

    public void addLimitBreakdown(double limitPrice, int demand) {
        limitBreakdown.put(limitPrice, demand);
    }

    public void setLimitBreakdown(Map<Double, Integer> limitBreakdown) {
        this.limitBreakdown = limitBreakdown;
    }

    public Double getExecutionPrice() {
        return executionPrice;
    }

    public void setExecutionPrice(Double executionPrice) {
        this.executionPrice = executionPrice;
    }

    public int getExecutionAmount() {
        return executionAmount;
    }

    public void setExecutionAmount(int executionAmount) {
        this.executionAmount = executionAmount;
    }

    public boolean isExecuted() {
        return executed;
    }

    public void setExecuted(boolean executed) {
        this.executed = executed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderBookStatistics that = (OrderBookStatistics) o;
        return orderSize == that.orderSize &&
                totalDemand == that.totalDemand &&
                invalidDemand == that.invalidDemand &&
                validDemand == that.validDemand &&
                invalidOrders == that.invalidOrders &&
                validOrders == that.validOrders &&
                Double.compare(that.executionPrice, executionPrice) == 0 &&
                executionAmount == that.executionAmount &&
                Objects.equals(instrumentId, that.instrumentId) &&
                Objects.equals(status, that.status) &&
                Objects.equals(orderBreakdown, that.orderBreakdown) &&
                Objects.equals(limitBreakdown, that.limitBreakdown);
    }

    @Override
    public int hashCode() {
        return Objects.hash(instrumentId, status, orderSize, totalDemand, invalidDemand, validDemand, invalidOrders, validOrders, orderBreakdown, limitBreakdown, executionPrice, executionAmount);
    }

    @Override
    public String toString() {
        return "OrderBookStatistics{" +
                "instrumentId='" + instrumentId + '\'' +
                ", status='" + status + '\'' +
                ", orderSize=" + orderSize +
                ", totalDemand=" + totalDemand +
                ", invalidDemand=" + invalidDemand +
                ", validDemand=" + validDemand +
                ", invalidOrders=" + invalidOrders +
                ", validOrders=" + validOrders +
                ", orderBreakdown=" + orderBreakdown +
                ", limitBreakdown=" + limitBreakdown +
                ", executionPrice=" + executionPrice +
                ", executionAmount=" + executionAmount +
                '}';
    }
}
