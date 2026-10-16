package br.com.zenon.fraud.detector;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReportMainTest {
    private static final String HEADER =
            "step,type,amount,nameOrig,oldbalanceOrg,newbalanceOrig,nameDest,oldbalanceDest,newbalanceDest,isFraud,isFlaggedFraud";
    private static final BigDecimal TOTAL_AMOUNT = new BigDecimal("36.30");

    @Test
    void printsPortugueseLabelsAndBrazilianCurrencyForPortuguese(@TempDir Path tempDir) throws Exception {
        String output = capture(() -> ReportMain.main(new String[]{"--lang=pt", writeCsv(tempDir).toString()}));

        assertReport(output, "Total de linhas", "Total de fraudes", "Valor total transacionado", Locale.of("pt", "BR"));
    }

    @Test
    void printsEnglishLabelsAndUsdCurrencyForEnglish(@TempDir Path tempDir) throws Exception {
        String output = capture(() -> ReportMain.main(new String[]{"--lang=en", writeCsv(tempDir).toString()}));

        assertReport(output, "Total Lines", "Total Frauds", "Total Value", Locale.US);
    }

    @Test
    void defaultsToPortugueseWhenOnlyCsvPathIsProvided(@TempDir Path tempDir) throws Exception {
        String output = capture(() -> ReportMain.main(new String[]{writeCsv(tempDir).toString()}));

        assertReport(output, "Total de linhas", "Total de fraudes", "Valor total transacionado", Locale.of("pt", "BR"));
    }

    @Test
    void rejectsInvalidOrAmbiguousArguments(@TempDir Path tempDir) throws Exception {
        Path csv = writeCsv(tempDir);

        assertArgumentError("--lang=es", csv.toString());
        assertArgumentError("--lang");
        assertArgumentError(csv.toString(), tempDir.resolve("other.csv").toString());
        assertArgumentError("--lang=pt", "--lang=en");
    }

    private void assertReport(String output, String linesLabel, String fraudsLabel, String valueLabel, Locale locale) {
        assertTrue(output.contains(linesLabel + ": 3"));
        assertTrue(output.contains(fraudsLabel + ": 1"));
        assertTrue(output.contains(valueLabel + ": " + NumberFormat.getCurrencyInstance(locale).format(TOTAL_AMOUNT)));
    }

    private void assertArgumentError(String... args) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        try {
            System.setOut(new PrintStream(output));
            IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () -> ReportMain.main(args));

            assertTrue(error.getMessage().contains("--lang") || error.getMessage().contains("caminho"));
        } finally {
            System.setOut(originalOut);
        }
        assertEquals("", output.toString(StandardCharsets.UTF_8));
    }

    private Path writeCsv(Path tempDir) throws IOException {
        Path csv = tempDir.resolve("transactions.csv");
        String content = HEADER + System.lineSeparator()
                + "1,PAYMENT,10.10,C1,10.10,0.00,M1,0.00,0.00,0,0" + System.lineSeparator()
                + "2,CASH_OUT,20.00,C2,20.00,0.00,M2,0.00,0.00,1,0" + System.lineSeparator()
                + "3,TRANSFER,6.20,C3,6.20,0.00,M3,0.00,0.00,0,0";
        return Files.writeString(csv, content, StandardCharsets.UTF_8);
    }

    private String capture(ThrowingRunnable action) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        try {
            System.setOut(new PrintStream(output));
            action.run();
        } finally {
            System.setOut(originalOut);
        }
        return output.toString(StandardCharsets.UTF_8);
    }

    private interface ThrowingRunnable {
        void run() throws Exception;
    }
}
