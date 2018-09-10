package com.order.controller;

import com.order.event.OrderEvents;
import com.order.model.Order;
import com.order.factory.OrderFactory;
import com.order.repository.OrderRepository;
import com.order.valueobject.OrderVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for orders. Adds an order in the order repository and notifies other components.
 */
@RestController
public class OrderController {

    private final Log logger = LogFactory.getLog(getClass());

    private OrderRepository orderRepository;

    private OrderEvents orderEvents;

    @Autowired
    public OrderController(OrderRepository orderRepository, OrderEvents orderEvents) {
        this.orderRepository = orderRepository;
        this.orderEvents = orderEvents;
    }

    @GetMapping(value = "/order/{order_id}")
    public ResponseEntity<Order> findByOrderId(@PathVariable("order_id") String orderId) {
        logger.info(String.format("Order service get for orderId: %s", orderId));

        Order order;
        try {
            order = orderRepository.findByOrderId(Long.parseLong(orderId));
        } catch (Exception ex) {
            logger.warn("Exception raised findByOrderId REST Call {0}", ex);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return order != null ? new ResponseEntity<>(order, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping(value = "/order")
    public ResponseEntity<Order> add(@RequestBody OrderVO orderVO) {
        final long orderId = orderRepository.nextOrderId();
        logger.info(String.format("Order service add for orderId: %s", orderId));

        Order order = OrderFactory.createOrder(orderId, orderVO.getQuantity(), orderVO.getInstrumentId(), orderVO.getPrice());
        try {
            // save the order
            orderRepository.store(order);

            //notify other components
            orderEvents.newOrder(order);
        } catch (Exception ex) {
            logger.warn("Exception raised add order REST Call {0}", ex);
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping(value = "/order/all")
    public ResponseEntity<List<Order>> findAll() {
        logger.info("Order service get all");

        try {
            return new ResponseEntity<>(orderRepository.findAll(), HttpStatus.OK);
        } catch (Exception ex) {
            logger.warn("Exception raised findAll REST Call {0}", ex);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
