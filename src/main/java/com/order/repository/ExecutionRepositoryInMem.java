package com.order.repository;

import com.order.model.Execution;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * In memory execution repository.
 */
@Repository("executionRepository")
public class ExecutionRepositoryInMem implements ExecutionRepository {

    private Map<Long, Execution> executions = new HashMap<>();

    private static AtomicLong currentExecutionId = new AtomicLong(0);

    public ExecutionRepositoryInMem() {
    }

    @Override
    public Execution findByExecutionId(long executionId) {
        return executions.get(executionId);
    }

    @Override
    public long nextExecutionId() {
        return currentExecutionId.incrementAndGet();
    }

    @Override
    public List<Execution> findByInstrumentId(final String instrumentId) {
        return executions.values().stream().filter(e -> e.getInstrumentId().equals(instrumentId)).collect(Collectors.toList());
    }

    @Override
    public List<Execution> findAll() {
        return executions.values().stream().collect(Collectors.toList());
    }

    @Override
    public void store(Execution execution) {
        executions.put(execution.getExecutionId(), execution);
    }
}
