package br.com.zenon.fraud.detector.service;

import br.com.zenon.fraud.detector.model.Customer;
import br.com.zenon.fraud.detector.model.Transaction;
import br.com.zenon.fraud.detector.model.TypeTransaction;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionIngestorService implements CommandLineRunner {
    private static final String DEFAULT_RESOURCE = "data/paysim_with_bad_data.csv";
    private static final String PATH_PROPERTY = "zenon.paysim.path";
    private static final int EXPECTED_COLUMNS = 11;
    private static final String[] EXPECTED_HEADER = {
            "step", "type", "amount", "nameOrig", "oldbalanceOrg", "newbalanceOrig",
            "nameDest", "oldbalanceDest", "newbalanceDest", "isFraud", "isFlaggedFraud"
    };
    private final Path inputPath;

    public TransactionIngestorService() {
        this(null);
    }

    TransactionIngestorService(Path inputPath) {
        this.inputPath = inputPath;
    }

    @Override
    public void run(String... args) throws Exception {
        try (BufferedReader br = openReader()) {
            validateHeader(br.readLine());
            String line;
            int transactionCount = 0;
            while ((line = br.readLine()) != null) {
                try {
                    System.out.println(newTransaction(parseCsvLine(line)));
                    transactionCount++;
                } catch (IllegalArgumentException e) {
                    System.err.printf("Erro %s: %s%n", line, e.getMessage());
                }
            }
            System.out.println("Quantidade de transações: " + transactionCount);
        }
    }

    private BufferedReader openReader() throws IOException {
        if (inputPath != null) {
            return Files.newBufferedReader(inputPath, StandardCharsets.UTF_8);
        }

        String configuredPath = System.getProperty(PATH_PROPERTY);
        if (configuredPath != null && !configuredPath.isBlank()) {
            return Files.newBufferedReader(Path.of(configuredPath), StandardCharsets.UTF_8);
        }

        InputStream resource = getClass().getClassLoader().getResourceAsStream(DEFAULT_RESOURCE);
        if (resource == null) {
            throw new FileNotFoundException("Recurso PaySim não encontrado: " + DEFAULT_RESOURCE);
        }
        return new BufferedReader(new InputStreamReader(resource, StandardCharsets.UTF_8));
    }

    private void validateHeader(String header) {
        if (header == null) {
            throw new IllegalArgumentException("cabeçalho ausente");
        }
        if (!Arrays.equals(parseCsvLine(header), EXPECTED_HEADER)) {
            throw new IllegalArgumentException("cabeçalho inválido");
        }
    }

    private String[] parseCsvLine(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder value = new StringBuilder();
        boolean inQuotes = false;
        boolean closedQuote = false;

        for (int index = 0; index < line.length(); index++) {
            char current = line.charAt(index);
            if (current == '"') {
                if (inQuotes && index + 1 < line.length() && line.charAt(index + 1) == '"') {
                    value.append('"');
                    index++;
                } else if (!inQuotes && value.isEmpty()) {
                    inQuotes = true;
                } else if (inQuotes) {
                    inQuotes = false;
                    closedQuote = true;
                } else {
                    throw new IllegalArgumentException("aspas inválidas");
                }
            } else if (current == ',' && !inQuotes) {
                values.add(value.toString());
                value.setLength(0);
                closedQuote = false;
            } else {
                if (closedQuote && !Character.isWhitespace(current)) {
                    throw new IllegalArgumentException("conteúdo após campo entre aspas");
                }
                value.append(current);
            }
        }

        if (inQuotes) {
            throw new IllegalArgumentException("aspas não fechadas");
        }
        values.add(value.toString());
        return values.toArray(String[]::new);
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
                .filter(values -> index >= 0 && index < values.length)
                .map(values -> values[index])
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .orElseThrow(() -> new IllegalArgumentException(field + " não pode ser nulo ou vazio"));
    }

    private TypeTransaction getTypeTransaction(String value) {
        return Arrays.stream(TypeTransaction.values())
                .filter(candidate -> candidate.getValue().equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("type inválido: " + value));
    }
}
