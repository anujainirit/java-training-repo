package com.javatraining.m02.concurrency;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;

/**
 * EXERCISE M02-T4: Async Task Executor with CompletableFuture
 *
 * Implement a generic async pipeline that:
 *  1. Accepts a list of inputs
 *  2. Applies an async "fetch" function to each (simulates I/O, e.g. HTTP call)
 *  3. Applies a sync "transform" function to each fetched result
 *  4. Collects all results — failed tasks are captured as errors, NOT thrown
 *  5. Returns a ProcessingResult containing successes + failures
 *
 * Constraints:
 *  - Use CompletableFuture — no raw Thread or ExecutorService.submit loops
 *  - Must execute fetches in parallel (use the provided executor)
 *  - Timeout: individual tasks that exceed timeoutMs must be recorded as failures
 *  - The method must return even if some tasks fail/timeout
 */
public class AsyncPipeline<I, O> {

    private final Executor executor;
    private final long timeoutMs;

    public AsyncPipeline(Executor executor, long timeoutMs) {
        this.executor = executor;
        this.timeoutMs = timeoutMs;
    }

    /**
     * Execute the pipeline.
     *
     * @param inputs    items to process
     * @param fetcher   async function: input → CompletableFuture<intermediate>
     * @param transform sync function: intermediate → output
     * @return ProcessingResult with all successes and failures
     */
    public <M> ProcessingResult<I, O> execute(
            List<I> inputs,
            Function<I, CompletableFuture<M>> fetcher,
            Function<M, O> transform) {
        // TODO: implement
        throw new UnsupportedOperationException("Not implemented");
    }
}

/**
 * Holds the results of a pipeline run.
 */
class ProcessingResult<I, O> {

    // TODO: store successes and failures appropriately

    /**
     * Return successful outputs in the same order as inputs (nulls where failed).
     * Actually — use a Map<I, O> keyed by input.
     */
    public Map<I, O> successes() {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Return a map of input → error message for every failed/timed-out task.
     */
    public Map<I, String> failures() {
        throw new UnsupportedOperationException("Not implemented");
    }

    public int successCount() { return successes().size(); }
    public int failureCount() { return failures().size(); }
    public boolean hasFailures() { return failureCount() > 0; }
}
