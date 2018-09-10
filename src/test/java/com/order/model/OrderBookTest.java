package com.order.model;

import com.order.common.OrderBookException;
import com.order.enumeration.OrderBookState;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class OrderBookTest {

    private final static String INSTRUMENT_ID_1 = "CS";
    private final static String INSTRUMENT_ID_2 = "UBS";
    private final static long ORDER_ID_1 = 1;
    private final static long ORDER_ID_2 = 2;
    private final static int ORDER_QUANTITY = 100;
    private final static int EXECUTION_QUANTITY = 100;
    private final static long EXECUTION_ID_1 = 1;
    private final static long EXECUTION_ID_2 = 2;
    private final static double ORDER_PRICE = 14.35;
    private final static double EXECUTION_PRICE = 14.34;

    private OrderBook orderBook;

    @Before
    public void init() {
        orderBook = new OrderBook(INSTRUMENT_ID_1);
    }

    @Test
    public void testOpen() {
        orderBook.open();
        assertEquals(OrderBookState.OPEN, orderBook.getState());
        assertEquals(0, orderBook.getOrders().size());
    }

    @Test
    public void testClose() {
        orderBook.close();
        assertEquals(OrderBookState.CLOSED, orderBook.getState());
        assertEquals(0, orderBook.getOrders().size());
    }

    @Test(expected = OrderBookException.class)
    public void testAddOrderWhenClosed() {
        orderBook.addOrder(new Order(ORDER_ID_1, ORDER_QUANTITY, INSTRUMENT_ID_1, ORDER_PRICE));
    }

    @Test(expected = OrderBookException.class)
    public void testAddOrderWithWrongInstrumentId() {
        orderBook.open();
        orderBook.addOrder(new Order(ORDER_ID_1, ORDER_QUANTITY, INSTRUMENT_ID_2, ORDER_PRICE));
    }

    @Test
    public void testAddOrder() {
        orderBook.open();
        orderBook.addOrder(new Order(ORDER_ID_1, ORDER_QUANTITY, INSTRUMENT_ID_1, ORDER_PRICE));
        assertEquals(1, orderBook.getOrders().size());
    }

    @Test(expected = OrderBookException.class)
    public void testAddExecutionWhenOpen() {
        orderBook.open();
        orderBook.addExecution(new Execution(EXECUTION_ID_1, EXECUTION_QUANTITY, INSTRUMENT_ID_1, EXECUTION_PRICE));
    }

    @Test(expected = OrderBookException.class)
    public void testAddExecutionWithWrongInstrumentId() {
        orderBook.addExecution(new Execution(EXECUTION_ID_1, EXECUTION_QUANTITY, INSTRUMENT_ID_2, EXECUTION_PRICE));
    }

    @Test(expected = OrderBookException.class)
    public void testAddExecutionWithWrongDifferentPrices() {
        orderBook.addExecution(new Execution(EXECUTION_ID_1, EXECUTION_QUANTITY, INSTRUMENT_ID_1, EXECUTION_PRICE));
        orderBook.addExecution(new Execution(EXECUTION_ID_1, EXECUTION_QUANTITY, INSTRUMENT_ID_1, 14.33));
    }

    @Test
    public void testAddExecutionInvalidOrders() {
        orderBook.open();
        orderBook.addOrder(new Order(ORDER_ID_1, 100, INSTRUMENT_ID_1, 14.34));
        orderBook.addOrder(new Order(ORDER_ID_2, 50, INSTRUMENT_ID_1, 14.31));
        assertEquals(2, orderBook.getOrders().size());
        orderBook.close();
        orderBook.addExecution(new Execution(EXECUTION_ID_1, 100, INSTRUMENT_ID_1,14.32));
        assertTrue(orderBook.isExecuted());
        assertFalse(orderBook.getOrder(ORDER_ID_1).isInvalid());
        assertTrue(orderBook.getOrder(ORDER_ID_2).isInvalid());
        assertTrue(orderBook.getOrder(ORDER_ID_1).isExecuted());
        assertFalse(orderBook.getOrder(ORDER_ID_2).isExecuted());
        assertEquals(100, orderBook.getOrder(ORDER_ID_1).getExecutionQuantity());
        assertEquals(0, orderBook.getOrder(ORDER_ID_2).getExecutionQuantity());
    }

    @Test
    public void testAddExecutionPartiallyExecutedInvalidOrders() {
        orderBook.open();
        orderBook.addOrder(new Order(ORDER_ID_1, 100, INSTRUMENT_ID_1, 14.34));
        orderBook.addOrder(new Order(ORDER_ID_2, 50, INSTRUMENT_ID_1, 14.31));
        assertEquals(2, orderBook.getOrders().size());
        orderBook.close();
        orderBook.addExecution(new Execution(EXECUTION_ID_1, 50, INSTRUMENT_ID_1,14.32));
        assertFalse(orderBook.isExecuted());
        assertFalse(orderBook.getOrder(ORDER_ID_1).isInvalid());
        assertTrue(orderBook.getOrder(ORDER_ID_2).isInvalid());
        assertFalse(orderBook.getOrder(ORDER_ID_1).isExecuted());
        assertFalse(orderBook.getOrder(ORDER_ID_2).isExecuted());
        assertEquals(50, orderBook.getOrder(ORDER_ID_1).getExecutionQuantity());
        assertEquals(0, orderBook.getOrder(ORDER_ID_2).getExecutionQuantity());
    }

    @Test
    public void testAddExecutionPartiallyExecuted() {
        orderBook.open();
        orderBook.addOrder(new Order(ORDER_ID_1, 100, INSTRUMENT_ID_1, 14.34));
        orderBook.addOrder(new Order(ORDER_ID_2, 50, INSTRUMENT_ID_1, 14.31));
        assertEquals(2, orderBook.getOrders().size());
        orderBook.close();
        orderBook.addExecution(new Execution(EXECUTION_ID_1, 100, INSTRUMENT_ID_1, 14.30));
        assertFalse(orderBook.isExecuted());
        assertFalse(orderBook.getOrder(ORDER_ID_1).isInvalid());
        assertFalse(orderBook.getOrder(ORDER_ID_2).isInvalid());
        assertFalse(orderBook.getOrder(ORDER_ID_1).isExecuted());
        assertFalse(orderBook.getOrder(ORDER_ID_2).isExecuted());
        assertEquals(67, orderBook.getOrder(ORDER_ID_1).getExecutionQuantity());
        assertEquals(33, orderBook.getOrder(ORDER_ID_2).getExecutionQuantity());
        orderBook.addExecution(new Execution(EXECUTION_ID_2, 50, INSTRUMENT_ID_1, 14.30));
        assertTrue(orderBook.isExecuted());
        assertFalse(orderBook.getOrder(ORDER_ID_1).isInvalid());
        assertFalse(orderBook.getOrder(ORDER_ID_2).isInvalid());
        assertTrue(orderBook.getOrder(ORDER_ID_1).isExecuted());
        assertTrue(orderBook.getOrder(ORDER_ID_2).isExecuted());
        assertEquals(100, orderBook.getOrder(ORDER_ID_1).getExecutionQuantity());
        assertEquals(50, orderBook.getOrder(ORDER_ID_2).getExecutionQuantity());
    }

    @Test
    public void testAddExecution() {
        orderBook.open();
        orderBook.addOrder(new Order(ORDER_ID_1, 100, INSTRUMENT_ID_1, 14.34));
        orderBook.addOrder(new Order(ORDER_ID_2, 50, INSTRUMENT_ID_1, 14.31));
        assertEquals(2, orderBook.getOrders().size());
        orderBook.close();
        orderBook.addExecution(new Execution(EXECUTION_ID_1, 150, INSTRUMENT_ID_1,14.30));
        assertTrue(orderBook.isExecuted());
        assertFalse(orderBook.getOrder(ORDER_ID_1).isInvalid());
        assertFalse(orderBook.getOrder(ORDER_ID_2).isInvalid());
        assertTrue(orderBook.getOrder(ORDER_ID_1).isExecuted());
        assertTrue(orderBook.getOrder(ORDER_ID_2).isExecuted());
        assertEquals(100, orderBook.getOrder(ORDER_ID_1).getExecutionQuantity());
        assertEquals(50, orderBook.getOrder(ORDER_ID_2).getExecutionQuantity());
    }
}
