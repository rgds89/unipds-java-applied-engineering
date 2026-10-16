package br.com.zenon.fraud.detector;

import br.com.zenon.fraud.detector.model.Customer;
import br.com.zenon.fraud.detector.model.Transaction;
import br.com.zenon.fraud.detector.model.TypeTransaction;
import br.com.zenon.fraud.detector.service.EfficientTransactionIngestor;
import br.com.zenon.fraud.detector.service.TransactionSQLRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IngestionMainTest {
    private static final String HEADER =
            "step,type,amount,nameOrig,oldbalanceOrg,newbalanceOrig,nameDest,oldbalanceDest,newbalanceDest,isFraud,isFlaggedFraud";

    @Test
    void clearsBeforeLoadingAndReconcilesAcceptedMetrics(@TempDir Path tempDir) throws Exception {
        Path csv = Files.writeString(tempDir.resolve("transactions.csv"), HEADER + System.lineSeparator()
                + row(1, "10.00", 0) + System.lineSeparator()
                + "invalid" + System.lineSeparator()
                + row(2, "20.00", 1), StandardCharsets.UTF_8);
        RecordingRepository repository = new RecordingRepository(false);
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        IngestionMain.run(new EfficientTransactionIngestor(1, 1), repository, csv.toString(), new PrintStream(output));

        assertEquals(1, repository.clearCalls);
        assertEquals(2, repository.saved.size());
        assertTrue(output.toString(StandardCharsets.UTF_8).contains("quantidade=2, fraudes=1, valor=30.00"));
        assertTrue(output.toString(StandardCharsets.UTF_8).contains("Métricas SQL"));
    }

    @Test
    void rejectsACompletedLoadWhenDatabaseMetricsDoNotMatch(@TempDir Path tempDir) throws Exception {
        Path csv = Files.writeString(tempDir.resolve("transactions.csv"), HEADER + System.lineSeparator()
                + row(1, "10.00", 0), StandardCharsets.UTF_8);

        assertThrows(IllegalStateException.class,
                () -> IngestionMain.run(new EfficientTransactionIngestor(1, 1), new RecordingRepository(true),
                        csv.toString(), System.out));
    }

    private static String row(int step, String amount, int fraud) {
        return step + ",PAYMENT," + amount + ",C" + step + ",10.00,0.00,M" + step + ",0.00,0.00,"
                + fraud + ",0";
    }

    private static final class RecordingRepository extends TransactionSQLRepository {
        private final List<Transaction> saved = new ArrayList<>();
        private final boolean inconsistentMetrics;
        private int clearCalls;

        private RecordingRepository(boolean inconsistentMetrics) {
            super(() -> { throw new SQLException("not used"); });
            this.inconsistentMetrics = inconsistentMetrics;
        }

        @Override
        public synchronized void clearTransactions() {
            clearCalls++;
            saved.clear();
        }

        @Override
        public synchronized void saveAll(List<Transaction> transactions) {
            saved.addAll(transactions);
        }

        @Override
        public synchronized LoadMetrics loadMetrics() {
            long frauds = saved.stream().filter(Transaction::isFraud).count();
            BigDecimal amount = saved.stream().map(Transaction::amount).reduce(BigDecimal.ZERO, BigDecimal::add);
            return new LoadMetrics(inconsistentMetrics ? saved.size() + 1L : saved.size(), frauds, amount);
        }
    }
}
