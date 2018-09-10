package com.order.model;

import com.order.enumeration.OrderType;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class OrderTest {

    private final static String INSTRUMENT_ID = "CS";
    private final static long ORDER_ID = 1;
    private final static int ORDER_QUANTITY = 100;
    private final static double ORDER_PRICE = 14.35;

    @Test
    public void testConstructionLimitOrder() {
        final Order order = new Order(ORDER_ID, ORDER_QUANTITY, INSTRUMENT_ID, ORDER_PRICE);
        assertThat(order.getOrderType()).isEqualTo(OrderType.LIMIT);
    }

    @Test
    public void testConstructionMarketOrder() {
        final Order order = new Order(ORDER_ID, ORDER_QUANTITY, INSTRUMENT_ID);
        assertThat(order.getOrderType()).isEqualTo(OrderType.MARKET);
    }

    @Test
    public void testExecutionQuantity() {
        final Order order = new Order(ORDER_ID, ORDER_QUANTITY, INSTRUMENT_ID, ORDER_PRICE);
        order.setExecutionQuantity(50);
        assertFalse(order.isExecuted());
        order.setExecutionQuantity(100);
        assertTrue(order.isExecuted());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExecutionQuantityOverExecuted() {
        final Order order = new Order(ORDER_ID, ORDER_QUANTITY, INSTRUMENT_ID, ORDER_PRICE);
        order.setExecutionQuantity(150);
    }

    @Test
    public void testEquality() {
        Order o1 = new Order(1, 100, "CS", 34.12);
        Order o2 = new Order(2, 100, "CS", 34.12);
        Order o3 = new Order(1, 50, "CS", 34.12);
        Order o4 = new Order(1, 100, "CS", 34.12);

        assertThat(o1.equals(o4)).as("Orders should be equal when orderIds are equal").isTrue();
        assertThat(o1.equals(o3)).as("Orders should be equal when orderIds are equal").isTrue();
        assertThat(o3.equals(o4)).as("Orders should be equal when orderIds are equal").isTrue();
        assertThat(o1.equals(o2)).as("Orders are not equal when orderId differ").isFalse();
    }
}

