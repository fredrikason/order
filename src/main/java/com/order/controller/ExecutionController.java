package com.order.controller;

import com.order.event.OrderEvents;
import com.order.model.Execution;
import com.order.repository.ExecutionRepository;
import com.order.valueobject.OrderVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for executions. Stores an execution in the repository and notifies other components.
 */
@RestController
public class ExecutionController {

    private final Log logger = LogFactory.getLog(getClass());

    private ExecutionRepository executionRepository;

    private OrderEvents orderEvents;

    @Autowired
    public ExecutionController(ExecutionRepository executionRepository, OrderEvents orderEvents) {
        this.executionRepository = executionRepository;
        this.orderEvents = orderEvents;
    }

    @GetMapping(value = "/execution/{execution_id}")
    public ResponseEntity<Execution> findByExecutionId(@PathVariable("execution_id") String executionId) {
        logger.info(String.format("Execution service get for executionId: %s", executionId));

        Execution execution;
        try {
            execution = executionRepository.findByExecutionId(Long.parseLong(executionId));
        } catch (Exception ex) {
            logger.warn("Exception raised findByExecutionId REST Call {0}", ex);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return execution != null ? new ResponseEntity<>(execution, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping(value = "/execution")
    public ResponseEntity<Execution> add(@RequestBody OrderVO orderVO) {
        final long executionId = executionRepository.nextExecutionId();
        logger.info(String.format("Execution service add for executionId: %s", executionId));

        final Execution execution = new Execution(executionId, orderVO.getQuantity(), orderVO.getInstrumentId(), orderVO.getPrice());
        try {
            // save the execution
            executionRepository.store(execution);

            //notify other components
            orderEvents.newExecution(execution);
        } catch (Exception ex) {
            logger.warn("Exception raised add execution REST Call {0}", ex);
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping(value = "/execution/all")
    public ResponseEntity<List<Execution>> findAll() {
        logger.info("Execution service get all");

        try {
            return new ResponseEntity<>(executionRepository.findAll(), HttpStatus.OK);
        } catch (Exception ex) {
            logger.warn("Exception raised findAll REST Call {0}", ex);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
