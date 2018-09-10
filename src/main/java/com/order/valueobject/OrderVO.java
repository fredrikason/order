package com.order.valueobject;

import java.util.Objects;

/**
 * Order value object.
 */
public class OrderVO {

    private int quantity;

    private String instrumentId;

    private Double price;

    public OrderVO() {
    }

    public int getQuantity() {
        return quantity;
    }

    public String getInstrumentId() {
        return instrumentId;
    }

    public Double getPrice() {
        return price;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setInstrumentId(String instrumentId) {
        this.instrumentId = instrumentId;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderVO orderVO = (OrderVO) o;
        return quantity == orderVO.quantity &&
                Double.compare(orderVO.price, price) == 0 &&
                Objects.equals(instrumentId, orderVO.instrumentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(quantity, instrumentId, price);
    }

    @Override
    public String toString() {
        return "OrderVO{" +
                "quantity=" + quantity +
                ", instrumentId='" + instrumentId + '\'' +
                ", price=" + price +
                '}';
    }
}
