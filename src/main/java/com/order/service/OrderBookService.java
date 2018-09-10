package com.order.service;

import com.order.model.Execution;
import com.order.model.Order;
import com.order.model.OrderBook;

import java.util.List;

/**
 * Order book service.
 */
public interface OrderBookService {

    /**
     * Opens an order book
     * @param instrumentId
     */
    void open(String instrumentId);

    /**
     * Closes an order book
     * @param instrumentId
     */
    void close(String instrumentId);

    /**
     * Adds an order to this book
     * @param order
     */
    void addOrder(Order order);

    /**
     * Adds an execution to this book
     * @param execution
     */
    void addExecution(Execution execution);

    /**
     *
     * @return
     */
    List<OrderBook> findAll();

    /**
     *
     * @param instrumentId
     * @return
     */
    OrderBook findByInstrumentId(String instrumentId);

}
