#A simple order manangement system

REST API specifications:

- http://localhost:8080/swagger-ui.html

Installing: mvn clean install

Running the application: mvn spring-boot:run or simply run com.order.Application class

###Artifacts

- Entities: OrderBook, Order and Execution
- Value objects: OrderVO and OrderBookStatistics
- Services: OrderBookService
- Aggregates: OrderBook is the main aggregate which keeps track of orders and executions
- Repositories: In-memory repositories for OrderBooks, Orders and Executions
which could be easily replaced by other persistent repositories for Hibernate or other in-memory data stores 

###Test cases

- Unit tests: Unit tests for Order, OrderBook and OrderBookStatistics
- Integration tests: Integration tests for OrderController and OrderBookController
  

###Performance considerations

Performance metrics:

- http://localhost:8080/actuator/prometheus

There is a jmeter performance test (/resource/OMS.jmx) which can be used to generate some metrics and form the basis for automated performance tests. 

The current implementation is stateful and synchronous but can easily be extended to handle order evens asynchronously by adding a message bus.
The processing of executions is currently sequential but the concurrency level can be approved by adding more fine grained locking.
The application could easily be separated out into microservices and scaled both vertically and horizontally by adding more servers and load balancing.
   