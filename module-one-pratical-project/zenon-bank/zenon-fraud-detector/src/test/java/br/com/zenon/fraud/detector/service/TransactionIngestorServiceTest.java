package br.com.zenon.fraud.detector.service;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TransactionIngestorServiceTest {
    @Test
    void skipsInvalidLinesAndProcessesAllFollowingLines() throws Exception {
        ByteArrayOutputStream errors = new ByteArrayOutputStream();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalErr = System.err;
        PrintStream originalOut = System.out;
        try {
            System.setErr(new PrintStream(errors));
            System.setOut(new PrintStream(output));
            new TransactionIngestorService().run();
        } finally {
            System.setErr(originalErr);
            System.setOut(originalOut);
        }

        String errorOutput = errors.toString();
        assertEquals(8, errorOutput.lines().count());
        assertTrue(errorOutput.lines().allMatch(line -> line.startsWith("Erro ")));
        assertTrue(errorOutput.contains("-1,PAYMENT,1000.00"));
        assertTrue(output.toString().contains("Quantidade de transações: 8"));
        assertTrue(output.toString().contains("Transaction[step=744"));
    }
}
