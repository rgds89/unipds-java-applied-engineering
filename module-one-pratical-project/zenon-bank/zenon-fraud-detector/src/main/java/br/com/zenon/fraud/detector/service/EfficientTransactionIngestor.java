package br.com.zenon.fraud.detector.service;

import br.com.zenon.fraud.detector.model.Transaction;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Streams PaySim rows without retaining the whole file. Persistence consumers run in a bounded
 * worker pool: at most {@code workerCount + maxPendingBatches} immutable batches are submitted.
 */
public final class EfficientTransactionIngestor {
    public static final int BATCH_SIZE = 10_000;
    private static final int DEFAULT_WORKER_COUNT = Math.max(1, Runtime.getRuntime().availableProcessors());
    private static final int DEFAULT_MAX_PENDING_BATCHES = DEFAULT_WORKER_COUNT;

    private final int workerCount;
    private final int maxPendingBatches;

    public EfficientTransactionIngestor() {
        this(DEFAULT_WORKER_COUNT, DEFAULT_MAX_PENDING_BATCHES);
    }

    public EfficientTransactionIngestor(int workerCount, int maxPendingBatches) {
        if (workerCount < 1) {
            throw new IllegalArgumentException("workerCount deve ser maior ou igual a 1");
        }
        if (maxPendingBatches < 1) {
            throw new IllegalArgumentException("maxPendingBatches deve ser maior ou igual a 1");
        }
        this.workerCount = workerCount;
        this.maxPendingBatches = maxPendingBatches;
    }

    public int workerCount() {
        return workerCount;
    }

    public int maxPendingBatches() {
        return maxPendingBatches;
    }

    /** Reads every valid transaction in source order and reports malformed rows without stopping. */
    public void readAsStream(String source, Consumer<Transaction> consumer) throws IOException {
        Objects.requireNonNull(consumer, "consumer não pode ser nulo");
        try (BufferedReader reader = openReader(source)) {
            TransactionIngestorService.validateHeader(reader.readLine());
            String line;
            while ((line = reader.readLine()) != null) {
                Transaction transaction = parseOrReport(line);
                if (transaction != null) {
                    consumer.accept(transaction);
                }
            }
        }
    }

    /**
     * Delivers immutable batches of at most {@value BATCH_SIZE} rows. The consumer is run by a
     * bounded pool, so the CSV reader blocks once the configured pending-batch window is full.
     */
    public void readBatch(String source, Consumer<List<Transaction>> consumer) throws IOException {
        Objects.requireNonNull(consumer, "consumer não pode ser nulo");
        ExecutorService executor = newExecutor();
        List<Future<?>> outstanding = new ArrayList<>(workerCount + maxPendingBatches);
        boolean completed = false;
        try {
            try (BufferedReader reader = openReader(source)) {
                TransactionIngestorService.validateHeader(reader.readLine());
                List<Transaction> batch = new ArrayList<>(BATCH_SIZE);
                String line;
                while ((line = reader.readLine()) != null) {
                    Transaction transaction = parseOrReport(line);
                    if (transaction == null) {
                        continue;
                    }
                    batch.add(transaction);
                    if (batch.size() == BATCH_SIZE) {
                        submit(executor, outstanding, batch, consumer);
                        batch = new ArrayList<>(BATCH_SIZE);
                    }
                }
                if (!batch.isEmpty()) {
                    submit(executor, outstanding, batch, consumer);
                }
            }
            awaitAll(outstanding);
            completed = true;
        } finally {
            if (completed) {
                executor.shutdown();
            } else {
                executor.shutdownNow();
            }
            awaitTermination(executor, completed);
        }
    }

    private void submit(ExecutorService executor, List<Future<?>> outstanding, List<Transaction> batch,
                        Consumer<List<Transaction>> consumer) {
        List<Transaction> immutableBatch = List.copyOf(batch);
        outstanding.add(executor.submit(() -> consumer.accept(immutableBatch)));
        if (outstanding.size() >= workerCount + maxPendingBatches) {
            await(outstanding.removeFirst());
        }
    }

    private void awaitAll(List<Future<?>> outstanding) {
        while (!outstanding.isEmpty()) {
            await(outstanding.removeFirst());
        }
    }

    private void await(Future<?> future) {
        try {
            future.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Aguardando lote interrompido", e);
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException runtimeException) {
                throw runtimeException;
            }
            if (cause instanceof Error error) {
                throw error;
            }
            throw new IllegalStateException("Falha ao processar lote", cause);
        }
    }

    private ExecutorService newExecutor() {
        ThreadFactory factory = Thread.ofPlatform().name("paysim-ingestion-", 0).factory();
        return new ThreadPoolExecutor(workerCount, workerCount, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(maxPendingBatches), factory, new ThreadPoolExecutor.AbortPolicy());
    }

    private BufferedReader openReader(String source) throws IOException {
        return TransactionIngestorService.openReader(source == null || source.isBlank() ? null : Path.of(source));
    }

    private Transaction parseOrReport(String line) {
        try {
            return TransactionIngestorService.parseTransaction(line);
        } catch (IllegalArgumentException e) {
            System.err.printf("Erro %s: %s%n", line, e.getMessage());
            return null;
        }
    }

    private void awaitTermination(ExecutorService executor, boolean successfulCompletion) {
        try {
            if (!executor.awaitTermination(1, TimeUnit.MINUTES) && successfulCompletion) {
                throw new IllegalStateException("Workers de ingestão não finalizaram");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            if (successfulCompletion) {
                throw new IllegalStateException("Encerramento dos workers interrompido", e);
            }
        }
    }
}
