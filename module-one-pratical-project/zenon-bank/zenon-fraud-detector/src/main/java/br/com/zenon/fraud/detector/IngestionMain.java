package br.com.zenon.fraud.detector;

import br.com.zenon.fraud.detector.model.Transaction;
import br.com.zenon.fraud.detector.service.EfficientTransactionIngestor;
import br.com.zenon.fraud.detector.service.TransactionSQLRepository;

import java.io.IOException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.List;

/** Loads the complete PaySim source in bounded concurrent JDBC batches and reconciles its result. */
public final class IngestionMain {
    private static final String PAY_SIM_PATH_PROPERTY = "zenon.paysim.path";

    private IngestionMain() {
    }

    public static void main(String[] args) throws IOException {
        int threads = positiveProperty("zenon.ingestion.threads", Math.max(1, Runtime.getRuntime().availableProcessors()));
        int pendingBatches = positiveProperty("zenon.ingestion.pending-batches", threads);
        TransactionSQLRepository repository = TransactionSQLRepository.fromConnectionProperties(
                System.getProperty("zenon.jdbc.url", TransactionSQLRepository.DEFAULT_JDBC_URL),
                System.getProperty("zenon.jdbc.user", "zenon"),
                System.getProperty("zenon.jdbc.password", "zenon"));
        run(new EfficientTransactionIngestor(threads, pendingBatches), repository,
                System.getProperty(PAY_SIM_PATH_PROPERTY, ""), System.out);
    }

    static void run(EfficientTransactionIngestor ingestor, TransactionSQLRepository repository,
                    String source, PrintStream output) throws IOException {
        repository.clearTransactions();
        LoadAccumulator loaded = new LoadAccumulator();
        long start = System.nanoTime();
        ingestor.readBatch(source, batch -> {
            repository.saveAll(batch);
            loaded.add(batch);
        });
        long durationMillis = (System.nanoTime() - start) / 1_000_000;

        TransactionSQLRepository.LoadMetrics databaseMetrics = repository.loadMetrics();
        TransactionSQLRepository.LoadMetrics acceptedMetrics = loaded.snapshot();
        if (!sameMetrics(acceptedMetrics, databaseMetrics)) {
            throw new IllegalStateException("Carga parcial detectada: métricas processadas " + acceptedMetrics
                    + " divergem das métricas SQL " + databaseMetrics);
        }

        output.printf("Carga concluída em %d ms (threads=%d, batch=%d, pendentes=%d)%n",
                durationMillis, ingestor.workerCount(), EfficientTransactionIngestor.BATCH_SIZE,
                ingestor.maxPendingBatches());
        output.printf("Métricas processadas: quantidade=%d, fraudes=%d, valor=%s%n",
                acceptedMetrics.transactionCount(), acceptedMetrics.fraudCount(), acceptedMetrics.totalAmount());
        output.printf("Métricas SQL: quantidade=%d, fraudes=%d, valor=%s%n",
                databaseMetrics.transactionCount(), databaseMetrics.fraudCount(), databaseMetrics.totalAmount());
    }

    private static boolean sameMetrics(TransactionSQLRepository.LoadMetrics expected,
                                       TransactionSQLRepository.LoadMetrics actual) {
        return expected.transactionCount() == actual.transactionCount()
                && expected.fraudCount() == actual.fraudCount()
                && expected.totalAmount().compareTo(actual.totalAmount()) == 0;
    }

    private static int positiveProperty(String property, int defaultValue) {
        int value = Integer.getInteger(property, defaultValue);
        if (value < 1) {
            throw new IllegalArgumentException(property + " deve ser maior ou igual a 1");
        }
        return value;
    }

    private static final class LoadAccumulator {
        private long transactionCount;
        private long fraudCount;
        private BigDecimal totalAmount = BigDecimal.ZERO;

        synchronized void add(List<Transaction> batch) {
            for (Transaction transaction : batch) {
                transactionCount++;
                if (transaction.isFraud()) {
                    fraudCount++;
                }
                totalAmount = totalAmount.add(transaction.amount());
            }
        }

        synchronized TransactionSQLRepository.LoadMetrics snapshot() {
            return new TransactionSQLRepository.LoadMetrics(transactionCount, fraudCount, totalAmount);
        }
    }
}
