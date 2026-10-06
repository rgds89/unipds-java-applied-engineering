package br.com.zenon.fraud.detector.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TransactionIngestorServiceTest {
    private static final String HEADER =
            "step,type,amount,nameOrig,oldbalanceOrg,newbalanceOrig,nameDest,oldbalanceDest,newbalanceDest,isFraud,isFlaggedFraud";

    @Test
    void skipsInvalidLinesAndProcessesAllFollowingLines() throws Exception {
        RunOutput output = capture(() -> new TransactionIngestorService().run());

        assertEquals(8, output.errors().lines().count());
        assertTrue(output.errors().lines().allMatch(line -> line.startsWith("Erro ")));
        assertEquals(8, output.transactions().size());
        assertTrue(output.errors().contains("-1,PAYMENT,1000.00"));
        assertTrue(output.stdout().contains("Quantidade de transações: 8"));
        assertTrue(output.stdout().contains("Transaction[step=744"));
    }

    @Test
    void parsesQuotedFieldsWithoutChangingColumnCount(@TempDir Path tempDir) throws Exception {
        Path csv = writeCsv(tempDir, HEADER + System.lineSeparator()
                + "1,PAYMENT,10.00,\"C,WITH,COMMA\",10.00,0.00,M100,0.00,0.00,0,0");

        RunOutput output = capture(() -> new TransactionIngestorService(csv).run());

        assertTrue(output.errors().isBlank());
        assertTrue(output.stdout().contains("name=C,WITH,COMMA"));
        assertTrue(output.stdout().contains("Quantidade de transações: 1"));
    }

    @Test
    void rejectsMissingOrInvalidHeader(@TempDir Path tempDir) throws Exception {
        Path emptyCsv = writeCsv(tempDir, "");
        Path invalidCsv = writeCsv(tempDir, "wrong,header" + System.lineSeparator());

        assertThrows(IllegalArgumentException.class, () -> new TransactionIngestorService(emptyCsv).run());
        assertThrows(IllegalArgumentException.class, () -> new TransactionIngestorService(invalidCsv).run());
    }

    @Test
    void propagatesMissingFileAsReadFailure(@TempDir Path tempDir) {
        Path missingCsv = tempDir.resolve("missing.csv");

        assertThrows(IOException.class, () -> new TransactionIngestorService(missingCsv).run());
    }

    @Test
    void repeatedRunsDoNotDuplicateTransactions() throws Exception {
        TransactionIngestorService service = new TransactionIngestorService();

        RunOutput first = capture(service::run);
        RunOutput second = capture(service::run);

        assertEquals(8, first.transactions().size());
        assertEquals(8, second.transactions().size());
        assertTrue(second.stdout().contains("Quantidade de transações: 8"));
    }

    private Path writeCsv(Path tempDir, String content) throws IOException {
        Path csv = tempDir.resolve("transactions.csv");
        return Files.writeString(csv, content, StandardCharsets.UTF_8);
    }

    private RunOutput capture(ThrowingRunnable action) throws Exception {
        ByteArrayOutputStream errors = new ByteArrayOutputStream();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalErr = System.err;
        PrintStream originalOut = System.out;
        try {
            System.setErr(new PrintStream(errors));
            System.setOut(new PrintStream(output));
            action.run();
        } finally {
            System.setErr(originalErr);
            System.setOut(originalOut);
        }
        return new RunOutput(output.toString(StandardCharsets.UTF_8), errors.toString(StandardCharsets.UTF_8));
    }

    private interface ThrowingRunnable {
        void run() throws Exception;
    }

    private record RunOutput(String stdout, String errors) {
        List<String> transactions() {
            return stdout.lines()
                    .filter(line -> line.startsWith("Transaction["))
                    .toList();
        }
    }
}
