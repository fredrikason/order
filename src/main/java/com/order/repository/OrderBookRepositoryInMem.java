package com.order.repository;

import com.order.model.Execution;
import com.order.model.Order;
import com.order.model.OrderBook;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * In memory order book repository.
 */
@Repository("orderBookRepository")
public class OrderBookRepositoryInMem implements OrderBookRepository {

    private Map<String, OrderBook> orderBooks = new HashMap<>();

    public OrderBookRepositoryInMem() {
        OrderBook orderBook = new OrderBook("CS");
        orderBook.open();
        orderBook.addOrder(new Order(1, 100, "CS", 14.34));
        orderBook.addOrder(new Order(2, 50, "CS", 14.31));
        orderBook.close();
        orderBook.addExecution(new Execution(1, 100, "CS", 14.32));
        //orderBooks.put(orderBook.getInstrumentId(), orderBook);
    }

    @Override
    public OrderBook findByInstrumentId(final String instrumentId) {
        return orderBooks.get(instrumentId);
    }

    @Override
    public List<OrderBook> findAll() {
        return orderBooks.values().stream().collect(Collectors.toList());
    }

    @Override
    public void store(OrderBook orderBook) {
        orderBooks.put(orderBook.getInstrumentId(), orderBook);
    }
}
