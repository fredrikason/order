package com.order.repository;

import com.order.model.OrderBook;

import java.util.List;

/**
 * Order book repository.
 */
public interface OrderBookRepository {

    /**
     *
     * @param instrumentId
     * @return
     */
    OrderBook findByInstrumentId(String instrumentId);

    /**
     *
     * @return
     */
    List<OrderBook> findAll();

    /**
     *
     * @param orderBook
     */
    void store(OrderBook orderBook);
}
