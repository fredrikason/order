package com.order.factory;

import com.order.model.Order;
import com.order.model.OrderBook;
import com.order.valueobject.OrderBookStatistics;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A factory object to create order book statistics.
 */
public class OrderBookStatisticsFactory {

    public static OrderBookStatistics createOrderBookStatistics(OrderBook orderBook) {
        Objects.requireNonNull(orderBook, "OrderBook cannot not be null!");

        int totalDemand = orderBook.getTotalDemand();
        int invalidDemand = orderBook.getInvalidDemand();
        int validDemand = orderBook.getValidDemand();
        int executionAmount = orderBook.getExecutionAmount();

        long invalidOrders = orderBook.getInvalidOrderCount();
        long validOrders = orderBook.getValidOrderCount();

        OrderBookStatistics stats = new OrderBookStatistics(orderBook.getInstrumentId(), orderBook.getState().name(),
                orderBook.getOrders().size(), totalDemand, invalidDemand, validDemand, invalidOrders, validOrders, executionAmount, orderBook.getExecutionPrice(), orderBook.isExecuted());

        Map<Double, Integer> limitBreakdown = orderBook.getOrders().stream().collect(Collectors.groupingBy(Order::getOrderPrice, Collectors.summingInt(Order::getOrderQuantity)));
        stats.setLimitBreakdown(limitBreakdown);

        if (orderBook.getOrders().size() > 0) {
            stats.addOrderBreakdown("smallest", orderBook.getMinOrder());
            stats.addOrderBreakdown("largest", orderBook.getMaxOrder());
            stats.addOrderBreakdown("earliest", orderBook.getFirstOrder());
            stats.addOrderBreakdown("latest", orderBook.getLastOrder());
        }
        return stats;
    }

}
