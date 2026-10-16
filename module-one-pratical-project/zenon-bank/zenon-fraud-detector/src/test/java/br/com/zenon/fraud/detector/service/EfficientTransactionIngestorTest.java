package br.com.zenon.fraud.detector.service;

import br.com.zenon.fraud.detector.model.Transaction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EfficientTransactionIngestorTest {
    private static final String HEADER =
            "step,type,amount,nameOrig,oldbalanceOrg,newbalanceOrig,nameDest,oldbalanceDest,newbalanceDest,isFraud,isFlaggedFraud";

    @Test
    void streamsValidRowsInOrderAfterSkippingInvalidRows(@TempDir Path tempDir) throws Exception {
        Path csv = writeCsv(tempDir, HEADER + System.lineSeparator()
                + row(1) + System.lineSeparator()
                + "2,PAYMENT,-1,C2,10,0,M2,0,0,0,0" + System.lineSeparator()
                + row(3));
        List<Long> steps = new ArrayList<>();

        new EfficientTransactionIngestor(1, 1).readAsStream(csv.toString(), transaction -> steps.add(transaction.step()));

        assertEquals(List.of(1L, 3L), steps);
    }

    @Test
    void deliversImmutableBatchesOfTenThousandAndTheLastPartialBatch(@TempDir Path tempDir) throws Exception {
        StringBuilder csv = new StringBuilder(900_000).append(HEADER).append(System.lineSeparator());
        for (int step = 1; step <= 10_001; step++) {
            csv.append(row(step)).append(System.lineSeparator());
        }
        List<Integer> batchSizes = new ArrayList<>();
        AtomicInteger immutableBatches = new AtomicInteger();

        new EfficientTransactionIngestor(1, 1).readBatch(writeCsv(tempDir, csv.toString()).toString(), batch -> {
            batchSizes.add(batch.size());
            assertThrows(UnsupportedOperationException.class, () -> batch.add(transaction(99_999)));
            immutableBatches.incrementAndGet();
        });

        assertEquals(List.of(10_000, 1), batchSizes);
        assertEquals(2, immutableBatches.get());
    }

    @Test
    void propagatesConsumerFailuresInsteadOfReportingSuccess(@TempDir Path tempDir) throws Exception {
        Path csv = writeCsv(tempDir, HEADER + System.lineSeparator() + row(1));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> new EfficientTransactionIngestor(1, 1).readBatch(csv.toString(),
                        batch -> { throw new IllegalStateException("database offline"); }));

        assertEquals("database offline", exception.getMessage());
    }

    @Test
    void propagatesStreamConsumerFailures(@TempDir Path tempDir) throws Exception {
        Path csv = writeCsv(tempDir, HEADER + System.lineSeparator() + row(1));

        assertThrows(IllegalStateException.class,
                () -> new EfficientTransactionIngestor(1, 1).readAsStream(csv.toString(),
                        transaction -> { throw new IllegalStateException("consumer failure"); }));
    }

    @Test
    void rejectsNonPositiveWorkerAndBackpressureSettings() {
        assertThrows(IllegalArgumentException.class, () -> new EfficientTransactionIngestor(0, 1));
        assertThrows(IllegalArgumentException.class, () -> new EfficientTransactionIngestor(1, 0));
    }

    private static Path writeCsv(Path tempDir, String content) throws IOException {
        Path csv = tempDir.resolve("transactions.csv");
        return Files.writeString(csv, content, StandardCharsets.UTF_8);
    }

    private static String row(int step) {
        return step + ",PAYMENT,10.00,C" + step + ",10.00,0.00,M" + step + ",0.00,0.00,"
                + (step % 2) + ",0";
    }

    private static Transaction transaction(long step) {
        return new Transaction(step, br.com.zenon.fraud.detector.model.TypeTransaction.PAYMENT, BigDecimal.TEN,
                new br.com.zenon.fraud.detector.model.Customer("C" + step, BigDecimal.ZERO, BigDecimal.ZERO),
                new br.com.zenon.fraud.detector.model.Customer("M" + step, BigDecimal.ZERO, BigDecimal.ZERO),
                false, false);
    }
}
