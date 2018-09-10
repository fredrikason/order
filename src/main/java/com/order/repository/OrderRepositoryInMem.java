package com.order.repository;

import com.order.model.Order;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * In memory order repository.
 */
@Repository("orderRepository")
public class OrderRepositoryInMem implements OrderRepository {

    private Map<Long, Order> orders = new HashMap<>();

    private static AtomicLong currentOrderId = new AtomicLong(0);

    public OrderRepositoryInMem() {
        //orders.put(1, new Order(1, 100, "CS", 14.34));
        //orders.put(2, new Order(2, 50, "CS", 14.31));
    }

    @Override
    public Order findByOrderId(long orderId) {
        return orders.get(orderId);
    }

    @Override
    public long nextOrderId() {
        return currentOrderId.incrementAndGet();
    }

    @Override
    public List<Order> findByInstrumentId(final String instrumentId) {
        return orders.values().stream().filter(o -> o.getInstrumentId().equals(instrumentId)).collect(Collectors.toList());
    }

    @Override
    public List<Order> findAll() {
        return orders.values().stream().collect(Collectors.toList());
    }

    @Override
    public void store(Order order) {
        orders.put(order.getOrderId(), order);
    }
}
