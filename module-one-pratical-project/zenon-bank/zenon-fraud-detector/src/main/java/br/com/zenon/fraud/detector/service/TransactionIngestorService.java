package br.com.zenon.fraud.detector.service;

import br.com.zenon.fraud.detector.model.Customer;
import br.com.zenon.fraud.detector.model.Transaction;
import br.com.zenon.fraud.detector.model.TypeTransaction;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionIngestorService implements CommandLineRunner {
    private static final Path PAYSIM_PATH = Path.of("data", "paysim_with_bad_data.csv");
    private static final Path MODULE_RELATIVE_PAYSIM_PATH = Path.of("zenon-fraud-detector").resolve(PAYSIM_PATH);
    private static final String PAYSIM_DIVISOR = ",";
    private static final int EXPECTED_COLUMNS = 11;
    private final List<Transaction> transactions = new ArrayList<>();

    @Override
    public void run(String... args) throws Exception {
        transactions.clear();
        try (BufferedReader br = Files.newBufferedReader(resolvePaysimPath(), StandardCharsets.UTF_8)) {
            br.readLine(); // cabeçalho
            String line;
            while ((line = br.readLine()) != null) {
                try {
                    transactions.add(newTransaction(line.split(PAYSIM_DIVISOR, -1)));
                } catch (RuntimeException e) {
                    System.err.printf("Erro %s: %s%n", line, e.getMessage());
                }
            }
            System.out.println("Quantidade de transações: " + transactions.size());
            transactions.forEach(System.out::println);

        } catch (IOException e) {
            System.err.println("Erro ao ler arquivo: " + e.getMessage());
        }
    }

    private Path resolvePaysimPath() throws IOException {
        if (Files.isRegularFile(PAYSIM_PATH)) {
            return PAYSIM_PATH;
        }
        if (Files.isRegularFile(MODULE_RELATIVE_PAYSIM_PATH)) {
            return MODULE_RELATIVE_PAYSIM_PATH;
        }
        throw new FileNotFoundException("Arquivo PaySim não encontrado. Verificados: "
                + PAYSIM_PATH + " e " + MODULE_RELATIVE_PAYSIM_PATH);
    }

    private Transaction newTransaction(String[] column) {
        if (column.length != EXPECTED_COLUMNS) {
            throw new IllegalArgumentException("quantidade de colunas inválida: " + column.length);
        }

        Customer customerOrig = new Customer(
                required(column, 3, "nameOrig"),
                nonNegativeDecimal(column, 4, "oldbalanceOrg"),
                nonNegativeDecimal(column, 5, "newbalanceOrig"));
        Customer customerDest = new Customer(
                required(column, 6, "nameDest"),
                nonNegativeDecimal(column, 7, "oldbalanceDest"),
                nonNegativeDecimal(column, 8, "newbalanceDest"));

        return new Transaction(
                positiveStep(column, 0),
                getTypeTransaction(required(column, 1, "type")),
                nonNegativeDecimal(column, 2, "amount"),
                customerOrig,
                customerDest,
                parseFlag(column, 9, "isFraud"),
                parseFlag(column, 10, "isFlaggedFraud"));
    }

    private Long positiveStep(String[] column, int index) {
        long step = Long.parseLong(required(column, index, "step"));
        if (step < 1) {
            throw new IllegalArgumentException("step deve ser maior ou igual a 1");
        }
        return step;
    }

    private BigDecimal nonNegativeDecimal(String[] column, int index, String field) {
        BigDecimal value = new BigDecimal(required(column, index, field));
        if (value.signum() < 0) {
            throw new IllegalArgumentException(field + " não pode ser negativo");
        }
        return value;
    }

    private boolean parseFlag(String[] column, int index, String field) {
        return switch (required(column, index, field)) {
            case "0" -> false;
            case "1" -> true;
            default -> throw new IllegalArgumentException(field + " deve ser 0 ou 1");
        };
    }

    private String required(String[] column, int index, String field) {
        return Optional.ofNullable(column)
                .filter(values -> index < values.length)
                .map(values -> values[index])
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .orElseThrow(() -> new IllegalArgumentException(field + " não pode ser nulo ou vazio"));
    }

    private TypeTransaction getTypeTransaction(String value) {
        return Optional.ofNullable(value)
                .flatMap(type -> java.util.Arrays.stream(TypeTransaction.values())
                        .filter(candidate -> candidate.getValue().equals(type))
                        .findFirst())
                .orElseThrow(() -> new IllegalArgumentException("type inválido: " + value));
    }
}
