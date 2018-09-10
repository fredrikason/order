package com.order.controller;

import com.order.model.OrderBook;
import com.order.factory.OrderBookStatisticsFactory;
import com.order.service.OrderBookService;
import com.order.valueobject.OrderBookStatistics;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for order books. Open and close order books using the order book service.
 */
@RestController
public class OrderBookController {

    private final Log logger = LogFactory.getLog(getClass());

    private OrderBookService orderBookService;

    @Autowired
    public OrderBookController(OrderBookService orderBookService) {
        this.orderBookService = orderBookService;
    }

    @GetMapping(value = "/orderbook/{instrument_id}")
    public ResponseEntity<OrderBookStatistics> findByInstrumentId(@PathVariable("instrument_id") String instrumentId) {
        logger.info(String.format("Order book service get for instrumentId: %s", instrumentId));

        OrderBook orderBook;
        try {
            orderBook = orderBookService.findByInstrumentId(instrumentId);
        } catch (Exception ex) {
            logger.warn("Exception raised findByInstrumentId REST Call {0}", ex);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return orderBook != null ? new ResponseEntity<>(OrderBookStatisticsFactory.createOrderBookStatistics(orderBook), HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping(value = "/orderbook/open/{instrument_id}")
    public ResponseEntity<Object> open(@PathVariable("instrument_id") String instrumentId) {
        logger.info(String.format("Order book service open book for instrumentId: %s", instrumentId));

        try {
            orderBookService.open(instrumentId);
        } catch (Exception ex) {
            logger.warn("Exception raised open order book REST Call {0}", ex);
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping(value = "/orderbook/close/{instrument_id}")
    public ResponseEntity<Object> close(@PathVariable("instrument_id") String instrumentId) {
        logger.info(String.format("Order book service close book for instrumentId: %s", instrumentId));

        try {
            orderBookService.close(instrumentId);
        } catch (Exception ex) {
            logger.warn("Exception raised close order book REST Call {0}", ex);
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping(value = "/orderbook/all")
    public ResponseEntity<List<OrderBook>> findAll() {
        logger.info("Order book service get all");

        try {
            return new ResponseEntity<>(orderBookService.findAll(), HttpStatus.OK);
        } catch (Exception ex) {
            logger.warn("Exception raised findByAll REST Call {0}", ex);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
