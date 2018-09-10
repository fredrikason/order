package com.order.factory;

import com.order.model.Order;

/**
 * A factory object for creating orders.
 */
public class OrderFactory {

    public static Order createOrder(long orderId, int orderQuantity, String instrumentId, Double orderPrice) {
        if (orderPrice == null) {
            return new Order(orderId, orderQuantity, instrumentId);
        } else {
            return new Order(orderId, orderQuantity, instrumentId, orderPrice);
        }
    }

}