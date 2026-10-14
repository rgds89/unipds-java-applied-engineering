package br.com.zenon.fraud.detector.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TransactionReportTest {
    private static final String HEADER =
            "step,type,amount,nameOrig,oldbalanceOrg,newbalanceOrig,nameDest,oldbalanceDest,newbalanceDest,isFraud,isFlaggedFraud";

    private final TransactionReport report = new TransactionReport();

    @Test
    void reportsDataLinesFraudsAndExactDecimalAmount(@TempDir Path tempDir) throws Exception {
        Path csv = writeCsv(tempDir, HEADER + System.lineSeparator()
                + "1,PAYMENT,10.10,C1,10.10,0.00,M1,0.00,0.00,0,0" + System.lineSeparator()
                + "2,CASH_OUT,20.00,C2,20.00,0.00,M2,0.00,0.00,1,0" + System.lineSeparator()
                + "3,TRANSFER,6.20,C3,6.20,0.00,M3,0.00,0.00,0,0");

        TransactionReport.Result result = report.generate(csv);

        assertEquals(3, result.totalLines());
        assertEquals(1, result.totalFrauds());
        assertEquals(new BigDecimal("36.30"), result.totalAmount());
    }

    @Test
    void propagatesMissingFile(@TempDir Path tempDir) {
        Path missingCsv = tempDir.resolve("missing.csv");

        assertThrows(IOException.class, () -> report.generate(missingCsv));
    }

    @Test
    void rejectsRowsWithoutRequiredColumns(@TempDir Path tempDir) throws Exception {
        Path csv = writeCsv(tempDir, HEADER + System.lineSeparator() + "1,PAYMENT,10.00");

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () -> report.generate(csv));

        assertTrue(error.getMessage().contains("quantidade de colunas insuficiente"));
    }

    @Test
    void rejectsMalformedHeaderEvenWithoutDataRows(@TempDir Path tempDir) throws Exception {
        Path csv = writeCsv(tempDir, "step,type,amount");

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () -> report.generate(csv));

        assertTrue(error.getMessage().contains("cabeçalho inválido"));
    }

    private Path writeCsv(Path tempDir, String content) throws IOException {
        Path csv = tempDir.resolve("transactions.csv");
        return Files.writeString(csv, content, StandardCharsets.UTF_8);
    }
}
