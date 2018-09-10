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
public class OrderBookControllerIntegrationTest {

    private final TestRestTemplate restTemplate = new TestRestTemplate();

    private HttpHeaders headers = new HttpHeaders();

    @LocalServerPort
    private int port;

    @Test
    public void testOpenAndClose() throws Exception {

        HttpEntity<Object> entity = new HttpEntity<Object>(headers);

        ResponseEntity<String> response1 = restTemplate.exchange(
                createURLWithPort("/orderbook/open/UBS"),
                HttpMethod.POST, entity, String.class);

        assertEquals(HttpStatus.CREATED, response1.getStatusCode());

        // test verification using get
        Map<String, Object> response2 = restTemplate.getForObject(
                createURLWithPort("/orderbook/UBS"),
                Map.class);

        assertEquals("UBS", response2.get("instrumentId"));
        assertEquals("OPEN", response2.get("status"));

        ResponseEntity<String> response3 = restTemplate.exchange(
                createURLWithPort("/orderbook/close/UBS"),
                HttpMethod.POST, entity, String.class);

        assertEquals(HttpStatus.CREATED, response3.getStatusCode());

        // test verification using get
        Map<String, Object> response4 = restTemplate.getForObject(
                createURLWithPort("/orderbook/UBS"),
                Map.class);

        assertEquals("UBS", response4.get("instrumentId"));
        assertEquals("CLOSED", response4.get("status"));
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }
}
