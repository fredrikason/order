package com.order.model;

import com.order.common.OrderBookException;
import com.order.enumeration.OrderBookState;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * An order book is a list of orders for a specific instrument id. When book is open it accepts orders and then closed
 * it accepts executions. Executions are added by matching against open orders and added linearly across all valid orders
 */
public class OrderBook {

    private final double THRESHOLD = .0001;

    private final String instrumentId;

    private OrderBookState state = OrderBookState.CLOSED;

    private Map<Long, Order> orders = new ConcurrentHashMap<>();
    private Map<Long, Execution> executions = new ConcurrentHashMap<>();

    private Double executionPrice;

    /**
     *
     * @param instrumentId
     */
    public OrderBook(String instrumentId) {
        this.instrumentId = instrumentId;
    }

    /**
     *
     */
    public synchronized void open() {
        state = OrderBookState.OPEN;
    }

    /**
     *
     */
    public synchronized void close() {
        state = OrderBookState.CLOSED;
    }

    /**
     * 
     * @param orderId
     * @return
     */
    public boolean containsOrder(Long orderId) {
        return orders.containsKey(orderId);
    }

    /**
     * Adds an order to this book if the book is open.
     * @param order
     */
    public void addOrder(Order order) {
        Objects.requireNonNull(order, "Order cannot not be null!");

        if (state == OrderBookState.CLOSED) {
            throw new OrderBookException("Cannot accept orders if the order book is closed!");
        }

        if (!instrumentId.equals(order.getInstrumentId())) {
            throw new OrderBookException(String.format("Cannot accept orders with different instrumentId! instrumentId=%s", order.getInstrumentId()));
        }

        orders.put(order.getOrderId(), order);

    }

    /**
     * Returns all orders in this book.
     * @return
     */
    public List<Order> getOrders() {
        return Collections.unmodifiableList(orders.values().stream().collect(Collectors.toList()));
    }

    /**
     * Returns an order in this book by order id.
     * @param orderId
     * @return
     */
    public Order getOrder(long orderId) {
        return orders.get(orderId);
    }

    /**
     * Returns an execution in this book by execution id.
     * @param executionId
     * @return
     */
    public Execution getExecution(String executionId) {
        return executions.get(executionId);
    }

    /**
     * An order book is executed if the execution amount = valid order demand.
     * @return
     */
    public boolean isExecuted() {
        return getExecutionAmount() == getValidDemand();
    }

    /**
     * Returns the state for this order book.
     * @return
     */
    public synchronized OrderBookState getState() { return state; }

    /**
     *
     * @param executionPrice
     */
    private synchronized void setExecutionPrice(Double executionPrice) {
        this.executionPrice = executionPrice;
    }

    /**
     *
     * @param executionId
     * @return
     */
    public boolean containsExecution(Long executionId) {
        return orders.containsKey(executionId);
    }

    /**
     * Adds an execution to this book if the book is closed. It only accepts execution for the same instrument and price.
     * @param execution
     */
    public void addExecution(Execution execution) {
        Objects.requireNonNull(execution, "Execution cannot not be null!");

        if (state == OrderBookState.OPEN) {
            throw new OrderBookException("Cannot accept executions if the order book is open!");
        }

        if (!instrumentId.equals(execution.getInstrumentId())) {
            throw new OrderBookException(String.format("Cannot accept executions with different instrumentId! instrumentId=%s", execution.getInstrumentId()));
        }

        if (isExecuted()) {
            throw new OrderBookException("Cannot accept executions if the order book is already executed!");
        }

        if (executionPrice == null) {
            setExecutionPrice(execution.getExecutionPrice());
        }

        if (Math.abs(executionPrice - execution.getExecutionPrice()) >= THRESHOLD) {
            throw new OrderBookException(String.format("Cannot accept executions with different price! price=%s", execution.getExecutionPrice()));
        }

        executions.put(execution.getExecutionId(), execution);

        // try to execute
        execute(execution);

    }

    /**
     * Tries to match en execution against orders in this book.
     * Orders are invalid if it has a price and price is lower than the execution price.
     * All executions are added linearly by calculating the weights proportional to the order quantity
     * @param execution
     */
    private synchronized void execute(Execution execution) {

        // handle invalid orders -> invalid if limit price lower than execution price
        getInvalidOrders(execution.getExecutionPrice()).forEach(o -> o.setInvalid(true));

        // distribute the execution linearly over all valid orders
        List<Order> validOrders = getValidOrders();

        Map<Long, Double> weights = calculateWeights(validOrders);

        for (Order order : validOrders) {
            if (!order.isExecuted()) {
                int weightedQty = Math.toIntExact((Math.round(weights.get(order.getOrderId()) * execution.getExecutionQuantity())));
                int requiredQty = order.getOrderQuantity() - order.getExecutionQuantity();
                int newQty = Math.min(requiredQty, weightedQty);
                order.setExecutionQuantity(newQty + order.getExecutionQuantity());
                order.setExecutionPrice(execution.getExecutionPrice());
                System.out.println(String.format("Execution order %s: %d of %d", order.getOrderId(), order.getExecutionQuantity(), order.getOrderQuantity()));
            }
        }
    }

    /**
     *
     * @param order
     * @param executionPrice
     * @return
     */
    private boolean isInvalid(Order order, double executionPrice) {
        return order.getOrderPrice() != null && order.getOrderPrice() < executionPrice;
    }

    /**
     * Returns all the invalid orders in this book.
     * @param executionPrice
     * @return
     */
    private List<Order> getInvalidOrders(double executionPrice) {
        return orders.values().stream().filter(o -> !o.isInvalid() && isInvalid(o, executionPrice)).collect(Collectors.toList());
    }

    /**
     * Returns all valid orders in this book.
     * @return
     */
    private List<Order> getValidOrders() {
        return orders.values().stream().filter(o -> !o.isInvalid() && !o.isExecuted()).collect(Collectors.toList());
    }

    /**
     * Returns the total demand in this book = the aggregated order quantity in this book.
     * @return
     */
    public int getTotalDemand() {
        return orders.values().stream().mapToInt(Order::getOrderQuantity).sum();
    }

    /**
     * Returns the total invalid demand in this book = the aggregated order quantity in this book for all invalid orders.
     * @return
     */
    public int getInvalidDemand() {
        return orders.values().stream().filter(o -> o.isInvalid()).mapToInt(Order::getOrderQuantity).sum();
    }

    /**
     * Returns the total valid demand in this book = the aggregated order quantity in this book for all valid orders.
     * @return
     */
    public int getValidDemand() {
        return orders.values().stream().filter(o -> !o.isInvalid()).mapToInt(Order::getOrderQuantity).sum();
    }

    /**
     * Returns the execution amount in this book = the aggregated executed order quantity in this book.
     * @return
     */
    public int getExecutionAmount() {
        return orders.values().stream().mapToInt(Order::getExecutionQuantity).sum();
    }

    /**
     * Returns the count of all invalid orders in this book.
     * @return
     */
    public long getInvalidOrderCount() {
        return orders.values().stream().filter(o -> o.isInvalid()).count();
    }

    /**
     * Returns the count of all valid orders in this book.
     * @return
     */
    public long getValidOrderCount() {
        return orders.values().stream().filter(o -> !o.isInvalid()).count();
    }

    /**
     * Returns the smallest order in this book = the order with smallest order quantity.
     * @return
     */
    public Order getMinOrder() {
        return orders.values().stream().min(Comparator.comparing(Order::getOrderQuantity)).orElseThrow(NoSuchElementException::new);
    }

    /**
     * Returns the biggest order in this book = the order with largest order quantity.
     * @return
     */
    public Order getMaxOrder() {
        return orders.values().stream().max(Comparator.comparing(Order::getOrderQuantity)).orElseThrow(NoSuchElementException::new);
    }

    /**
     * Returns the order added first to this book.
     * @return
     */
    public Order getFirstOrder() {
        return orders.values().stream().min(Comparator.comparing(Order::getEntryDate)).orElseThrow(NoSuchElementException::new);
    }

    /**
     * Returns the order added last to this book.
     * @return
     */
    public Order getLastOrder() {
        return orders.values().stream().max(Comparator.comparing(Order::getEntryDate)).orElseThrow(NoSuchElementException::new);
    }

    /**
     * Calculates the order weights for all valid orders in this book.
     * @param orders
     * @return
     */
    private Map<Long, Double> calculateWeights(List<Order> orders) {
        Map<Long, Double> weights = new HashMap<>();

        double sum = orders.stream().mapToInt(Order::getOrderQuantity).sum();

        for (Order order : orders) {
            weights.put(order.getOrderId(), order.getOrderQuantity() / sum);
        }

        return weights;
    }

    /**
     *
     * @return
     */
    public String getInstrumentId() {
        return instrumentId;
    }

    /**
     *
     * @return
     */
    public synchronized Double getExecutionPrice() {
        return executionPrice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderBook orderBook = (OrderBook) o;
        return Objects.equals(instrumentId, orderBook.instrumentId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(instrumentId);
    }

    @Override
    public String toString() {
        return instrumentId;
    }
}
