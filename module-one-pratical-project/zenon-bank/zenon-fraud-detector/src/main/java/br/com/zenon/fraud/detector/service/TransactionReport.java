package br.com.zenon.fraud.detector.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.stream.Stream;

public class TransactionReport {
    private static final int AMOUNT_INDEX = 2;
    private static final int FRAUD_INDEX = 9;
    private static final String AMOUNT_HEADER = "amount";
    private static final String FRAUD_HEADER = "isFraud";

    public Result generate(Path inputPath) throws IOException {
        Objects.requireNonNull(inputPath, "inputPath não pode ser nulo");

        Accumulator accumulator = new Accumulator();
        try (Stream<String> lines = Files.lines(inputPath)) {
            lines.peek(accumulator::validateHeader).skip(1).forEach(accumulator::add);
        }
        accumulator.ensureHeader();
        return accumulator.result();
    }

    public record Result(long totalLines, long totalFrauds, BigDecimal totalAmount) {
    }

    private static final class Accumulator {
        private long totalLines;
        private long totalFrauds;
        private BigDecimal totalAmount = BigDecimal.ZERO;
        private boolean headerValidated;

        void validateHeader(String header) {
            if (headerValidated) {
                return;
            }

            String[] columns = header.split(",", -1);
            if (columns.length <= FRAUD_INDEX
                    || !AMOUNT_HEADER.equals(columns[AMOUNT_INDEX].trim())
                    || !FRAUD_HEADER.equals(columns[FRAUD_INDEX].trim())) {
                throw invalidLine(header, "cabeçalho inválido");
            }
            headerValidated = true;
        }

        void ensureHeader() {
            if (!headerValidated) {
                throw new IllegalArgumentException("cabeçalho ausente");
            }
        }

        void add(String line) {
            String[] columns = line.split(",", -1);
            if (columns.length <= FRAUD_INDEX) {
                throw invalidLine(line, "quantidade de colunas insuficiente");
            }

            try {
                BigDecimal amount = new BigDecimal(columns[AMOUNT_INDEX].trim());
                boolean fraud = switch (columns[FRAUD_INDEX].trim()) {
                    case "0" -> false;
                    case "1" -> true;
                    default -> throw invalidLine(line, "isFraud deve ser 0 ou 1");
                };

                totalLines++;
                totalAmount = totalAmount.add(amount);
                if (fraud) {
                    totalFrauds++;
                }
            } catch (NumberFormatException e) {
                throw invalidLine(line, "amount inválido", e);
            }
        }

        Result result() {
            return new Result(totalLines, totalFrauds, totalAmount);
        }

        private IllegalArgumentException invalidLine(String line, String reason) {
            return invalidLine(line, reason, null);
        }

        private IllegalArgumentException invalidLine(String line, String reason, Exception cause) {
            return new IllegalArgumentException("linha inválida (" + reason + "): " + line, cause);
        }
    }
}
