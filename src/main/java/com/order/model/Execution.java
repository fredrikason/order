package com.order.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * An execution defined in terms of execution quantity, price and instrument id.
 */
public class Execution {

    private final long executionId;

    private final String instrumentId;

    private double executionPrice;

    private final LocalDateTime executionDate = LocalDateTime.now();

    private int executionQuantity;

    public Execution(long executionId, int executionQuantity, String instrumentId, double executionPrice) {
        this.executionId = executionId;
        this.executionQuantity = executionQuantity;
        this.instrumentId = instrumentId;
        this.executionPrice = executionPrice;
    }

    /**
     *
     * @return
     */
    public long getExecutionId() {
        return executionId;
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
    public int getExecutionQuantity() {
        return executionQuantity;
    }

    /**
     *
     * @return
     */
    public double getExecutionPrice() {
        return executionPrice;
    }

    /**
     *
     * @return
     */
    public LocalDateTime getExecutionDate() { return executionDate; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Execution execution = (Execution) o;
        return Objects.equals(executionId, execution.executionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(executionId);
    }

    @Override
    public String toString() {
        return Long.toString(executionId);
    }
}

