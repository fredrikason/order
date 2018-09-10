package com.order.service;

import com.order.common.OrderBookException;
import com.order.model.Execution;
import com.order.model.Order;
import com.order.model.OrderBook;
import com.order.enumeration.OrderBookState;
import com.order.repository.ExecutionRepository;
import com.order.repository.OrderBookRepository;
import com.order.repository.OrderRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * Order book service implementation.
 */
@Service("orderBookService")
public class OrderBookServiceImpl implements OrderBookService {

    private final Log logger = LogFactory.getLog(getClass());

    private OrderBookRepository orderBookRepository;

    private OrderRepository orderRepository;

    private ExecutionRepository executionRepository;

    @Autowired
    public OrderBookServiceImpl(OrderBookRepository orderBookRepository, OrderRepository orderRepository, ExecutionRepository executionRepository) {
        this.orderBookRepository = orderBookRepository;
        this.orderRepository = orderRepository;
        this.executionRepository = executionRepository;
    }

    /**
     * Opens an order book. Adds all the orders in the order repository.
     * @param instrumentId
     */
    @Override
    public void open(String instrumentId) {
        Objects.requireNonNull(instrumentId, "Instrument cannot not be null!");

        OrderBook orderBook = orderBookRepository.findByInstrumentId(instrumentId);
        if (orderBook == null) {
            orderBook = new OrderBook(instrumentId);
        }

        if (orderBook.getState() == OrderBookState.OPEN) {
            String msg = String.format("Order book is already open: %s", instrumentId);
            logger.warn(msg);
            throw new OrderBookException(msg);

        }

        orderBook.open();

        //the order book is now open and accepts order
        List<Order> orders = orderRepository.findByInstrumentId(instrumentId);
        for (Order order : orders) {
            if (!orderBook.containsOrder(order.getOrderId()))
                orderBook.addOrder(order);
        }

        orderBookRepository.store(orderBook);
    }

    /**
     * Closed and order book. Adds all the executions in the execution repository.
     * @param instrumentId
     */
    @Override
    public void close(String instrumentId) {
        Objects.requireNonNull(instrumentId, "Instrument cannot not be null!");

        OrderBook orderBook = orderBookRepository.findByInstrumentId(instrumentId);
        if (orderBook == null) {
            String msg = String.format("Can't close non-existing order book: %s", instrumentId);
            logger.warn(msg);
            throw new OrderBookException(msg);
        }

        if (orderBook.getState() == OrderBookState.CLOSED) {
            String msg = String.format("Order book is already closed: %s", instrumentId);
            logger.warn(msg);
            throw new OrderBookException(msg);

        }

        orderBook.close();

        //the order book is now closed and accepts executions
        List<Execution> executions = executionRepository.findByInstrumentId(instrumentId);
        for (Execution execution : executions) {
            if (!orderBook.containsExecution(execution.getExecutionId()))
                orderBook.addExecution(execution);
        }

        orderBookRepository.store(orderBook);
    }

    /**
     * Adds an order to this book if its open.
     * @param order
     */
    @Override
    public void addOrder(Order order) {
        Objects.requireNonNull(order, "Order cannot not be null!");

        OrderBook orderBook = orderBookRepository.findByInstrumentId(order.getInstrumentId());
        if (orderBook != null && orderBook.getState() == OrderBookState.OPEN) {
            orderBook.addOrder(order);
        }
    }

    /**
     * Adds an execution to this book if its closed.
     * @param execution
     */
    @Override
    public void addExecution(Execution execution) {
        Objects.requireNonNull(execution, "Execution cannot not be null!");

        OrderBook orderBook = orderBookRepository.findByInstrumentId(execution.getInstrumentId());
        if (orderBook != null && orderBook.getState() == OrderBookState.CLOSED && !orderBook.isExecuted()) {
            orderBook.addExecution(execution);
        }
    }

    /**
     *
     * @param instrumentId
     * @return
     */
    @Override
    public OrderBook findByInstrumentId(String instrumentId) {
        return orderBookRepository.findByInstrumentId(instrumentId);
    }
}
