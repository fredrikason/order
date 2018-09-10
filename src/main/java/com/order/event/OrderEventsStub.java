package com.order.event;

import com.order.model.Execution;
import com.order.model.Order;
import com.order.service.OrderBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * A synchronous order event stub to enable testing.
 */
@Component
public class OrderEventsStub implements OrderEvents {

    private OrderBookService orderBookService;

    @Autowired
    public OrderEventsStub(OrderBookService orderBookService) {
        this.orderBookService = orderBookService;
    }

    @Override
    public void newOrder(Order order) {
        orderBookService.addOrder(order);
    }

    @Override
    public void newExecution(Execution execution) {
        orderBookService.addExecution(execution);
    }
}
