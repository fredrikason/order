package com.order.repository;

import com.order.model.Order;

import java.util.List;

/**
 * Order repository
 */
public interface OrderRepository {

    /**
     *
     * @param orderId
     * @return
     */
    Order findByOrderId(long orderId);

    /**
     *
     * @return
     */
    long nextOrderId();

    /**
     *
     * @param instrumentId
     * @return
     */
    List<Order> findByInstrumentId(String instrumentId);

    /**
     *
     * @return
     */
    List<Order> findAll();

    /**
     *
     * @param order
     */
    void store(Order order);
}
