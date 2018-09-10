package com.order.factory;

import com.order.model.Execution;
import com.order.model.Order;
import com.order.model.OrderBook;
import com.order.valueobject.OrderBookStatistics;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class OrderBookStatisticsFactoryTest {

    private final static String INSTRUMENT_ID_1 = "CS";
    private final static long ORDER_ID_1 = 1;
    private final static long ORDER_ID_2 = 2;
    private final static int ORDER_QUANTITY = 100;
    private final static int EXECUTION_QUANTITY = 100;
    private final static long EXECUTION_ID_1 = 1;
    private final static String ORDER_STATUS_CLOSED = "CLOSED";

    private OrderBook orderBook;

    @Before
    public void init() {
        orderBook = new OrderBook(INSTRUMENT_ID_1);
        orderBook.open();
        orderBook.addOrder(new Order(ORDER_ID_1, ORDER_QUANTITY, INSTRUMENT_ID_1, 14.34));
        orderBook.addOrder(new Order(ORDER_ID_2, 50, INSTRUMENT_ID_1, 14.31));
        orderBook.close();
        orderBook.addExecution(new Execution(EXECUTION_ID_1, EXECUTION_QUANTITY, INSTRUMENT_ID_1,14.32));
    }

    @Test(expected = NullPointerException.class)
    public void testCreateWhenNull() {
        OrderBookStatisticsFactory.createOrderBookStatistics(null);
    }

    @Test
    public void testCreateWhenEmpty() {
        OrderBookStatistics stats = OrderBookStatisticsFactory.createOrderBookStatistics(new OrderBook(INSTRUMENT_ID_1));
        assertEquals(0, stats.getOrderSize());
        assertEquals(INSTRUMENT_ID_1, stats.getInstrumentId());
        assertEquals(ORDER_STATUS_CLOSED, stats.getStatus());
        assertEquals(0, stats.getTotalDemand());
        assertEquals(0, stats.getInvalidDemand());
        assertEquals(0, stats.getValidDemand());
        assertEquals(0L, stats.getInvalidOrders());
        assertEquals(0L, stats.getValidOrders());
    }

    @Test
    public void testCreate() {
        OrderBookStatistics stats = OrderBookStatisticsFactory.createOrderBookStatistics(orderBook);
        assertEquals(2, stats.getOrderSize());
        assertEquals(INSTRUMENT_ID_1, stats.getInstrumentId());
        assertEquals(ORDER_STATUS_CLOSED, stats.getStatus());
        assertEquals(150, stats.getTotalDemand());
        assertEquals(50, stats.getInvalidDemand());
        assertEquals(100, stats.getValidDemand());
        assertEquals(1L, stats.getInvalidOrders());
        assertEquals(1L, stats.getValidOrders());
        assertEquals(100, stats.getExecutionAmount());
        assertTrue(stats.isExecuted());
        int lowDemand = stats.getLimitBreakdown().get(14.31);
        assertEquals(50, lowDemand);
        int highDemand = stats.getLimitBreakdown().get(14.34);
        assertEquals(100, highDemand);
        assertEquals(ORDER_ID_2, stats.getOrderBreakdown().get("smallest").getOrderId());
        assertEquals(ORDER_ID_1, stats.getOrderBreakdown().get("largest").getOrderId());
    }

}
