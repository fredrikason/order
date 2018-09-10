package com.order.repository;

import com.order.model.Execution;

import java.util.List;

/**
 * Repository for executions.
 */
public interface ExecutionRepository {

    /**
     *
     * @param executionId
     * @return
     */
    Execution findByExecutionId(long executionId);

    /**
     *
     * @return
     */
    long nextExecutionId();

    /**
     *
     * @param instrumentId
     * @return
     */
    List<Execution> findByInstrumentId(String instrumentId);

    /**
     *
     * @return
     */
    List<Execution> findAll();

    /**
     *
     * @param execution
     */
    void store(Execution execution);
}
