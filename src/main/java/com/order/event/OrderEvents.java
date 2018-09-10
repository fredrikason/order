package com.order.event;

import com.order.model.Execution;
import com.order.model.Order;

/**
 * API for order event notifications. Can be implemented either synchronous or asynchronous.
 * Used to notify other components about incoming order events.
 */
public interface OrderEvents {

    void newOrder(Order order);

    void newExecution(Execution execution);
}
