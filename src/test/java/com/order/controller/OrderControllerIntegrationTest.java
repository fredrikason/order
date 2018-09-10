package com.order.controller;

import com.order.Application;
import com.order.valueobject.OrderVO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrderControllerIntegrationTest {

    private final TestRestTemplate restTemplate = new TestRestTemplate();

    private HttpHeaders headers = new HttpHeaders();

    @LocalServerPort
    private int port;

    @Test
    public void testAddAndGet() throws Exception {

        OrderVO order = new OrderVO();
        order.setQuantity(100);
        order.setInstrumentId("UBS");
        order.setPrice(34.56);

        HttpEntity<OrderVO> entity = new HttpEntity<OrderVO>(order, headers);

        ResponseEntity<String> response1 = restTemplate.exchange(
                createURLWithPort("/order"),
                HttpMethod.POST, entity, String.class);

        assertEquals(HttpStatus.CREATED, response1.getStatusCode());

        // test verification using get
        Map<String, Object> response2 = restTemplate.getForObject(
                createURLWithPort("/order/1"),
                Map.class);

        assertEquals(1, response2.get("orderId"));
        assertEquals(100, response2.get("orderQuantity"));
        assertEquals("UBS", response2.get("instrumentId"));
        assertEquals(34.56, (Double) response2.get("orderPrice"), 0.001);
        assertEquals("LIMIT", response2.get("orderType"));
        assertFalse((Boolean)response2.get("invalid"));
        assertFalse((Boolean)response2.get("executed"));
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }
}
