package br.com.zenon.fraud.detector.service;

import br.com.zenon.fraud.detector.model.Transaction;
import br.com.zenon.fraud.detector.model.TypeTransaction;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Stateless Stream API calculations over a caller-provided transaction collection.
 */
public class FraudAnalyzer {
    private static final int TOP_FRAUD_LIMIT = 3;
    private static final int SUSPICIOUS_CUSTOMER_LIMIT = 5;

    public long totalFrauds(List<Transaction> transactions) {
        return transactions(transactions)
                .filter(Transaction::isFraud)
                .count();
    }

    public List<Transaction> topFrauds(List<Transaction> transactions) {
        return transactions(transactions)
                .filter(Transaction::isFraud)
                .sorted(Comparator.comparing(Transaction::amount).reversed())
                .limit(TOP_FRAUD_LIMIT)
                .toList();
    }

    public List<String> suspiciousCustomerNames(List<Transaction> transactions) {
        return transactions(transactions)
                .filter(Transaction::isFraud)
                .sorted(Comparator.comparing(Transaction::amount).reversed())
                .map(transaction -> transaction.customerOrig().name())
                .distinct()
                .limit(SUSPICIOUS_CUSTOMER_LIMIT)
                .toList();
    }

    public BigDecimal totalFraudLoss(List<Transaction> transactions) {
        return transactions(transactions)
                .filter(Transaction::isFraud)
                .map(Transaction::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Map<TypeTransaction, Long> fraudCountsByType(List<Transaction> transactions) {
        Map<TypeTransaction, Long> counts = transactions(transactions)
                .filter(Transaction::isFraud)
                .collect(Collectors.groupingBy(
                        Transaction::type,
                        () -> new EnumMap<>(TypeTransaction.class),
                        Collectors.counting()));
        return Map.copyOf(counts);
    }

    private java.util.stream.Stream<Transaction> transactions(List<Transaction> transactions) {
        return Objects.requireNonNull(transactions, "transactions não pode ser nulo").stream();
    }
}
