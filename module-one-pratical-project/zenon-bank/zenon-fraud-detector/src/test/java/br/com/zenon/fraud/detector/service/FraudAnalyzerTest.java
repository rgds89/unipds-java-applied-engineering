package br.com.zenon.fraud.detector.service;

import br.com.zenon.fraud.detector.model.Customer;
import br.com.zenon.fraud.detector.model.Transaction;
import br.com.zenon.fraud.detector.model.TypeTransaction;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FraudAnalyzerTest {
    private final FraudAnalyzer analyzer = new FraudAnalyzer();

    @Test
    void analyzesFraudsByAmountCustomerLossAndType() {
        List<Transaction> transactions = List.of(
                transaction("C-low", "20.00", TypeTransaction.CASH_OUT, true),
                transaction("C-high", "100.00", TypeTransaction.TRANSFER, true),
                transaction("C-high", "90.00", TypeTransaction.CASH_OUT, true),
                transaction("C-middle", "50.00", TypeTransaction.TRANSFER, true),
                transaction("C-normal", "999.00", TypeTransaction.PAYMENT, false));

        assertEquals(4, analyzer.totalFrauds(transactions));
        assertEquals(List.of(new BigDecimal("100.00"), new BigDecimal("90.00"), new BigDecimal("50.00")),
                analyzer.topFrauds(transactions).stream().map(Transaction::amount).toList());
        assertEquals(List.of("C-high", "C-middle", "C-low"), analyzer.suspiciousCustomerNames(transactions));
        assertEquals(new BigDecimal("260.00"), analyzer.totalFraudLoss(transactions));
        assertEquals(Map.of(TypeTransaction.CASH_OUT, 2L, TypeTransaction.TRANSFER, 2L),
                analyzer.fraudCountsByType(transactions));
    }

    @Test
    void returnsAvailableResultsWhenThereAreFewerThanThreeFrauds() {
        List<Transaction> transactions = List.of(
                transaction("C1", "10.00", TypeTransaction.CASH_OUT, true),
                transaction("C2", "5.00", TypeTransaction.PAYMENT, false));

        assertEquals(List.of(new BigDecimal("10.00")),
                analyzer.topFrauds(transactions).stream().map(Transaction::amount).toList());
        assertEquals(List.of("C1"), analyzer.suspiciousCustomerNames(transactions));
        assertEquals(new BigDecimal("10.00"), analyzer.totalFraudLoss(transactions));
        assertEquals(Map.of(TypeTransaction.CASH_OUT, 1L), analyzer.fraudCountsByType(transactions));
    }

    @Test
    void returnsEmptyFraudResultsWhenNoTransactionIsFraudulent() {
        List<Transaction> transactions = List.of(
                transaction("C1", "10.00", TypeTransaction.PAYMENT, false));

        assertEquals(0, analyzer.totalFrauds(transactions));
        assertEquals(List.of(), analyzer.topFrauds(transactions));
        assertEquals(List.of(), analyzer.suspiciousCustomerNames(transactions));
        assertEquals(BigDecimal.ZERO, analyzer.totalFraudLoss(transactions));
        assertEquals(Map.of(), analyzer.fraudCountsByType(transactions));
    }

    private Transaction transaction(String customerName, String amount, TypeTransaction type, boolean isFraud) {
        Customer customer = new Customer(customerName, BigDecimal.ZERO, BigDecimal.ZERO);
        return new Transaction(1L, type, new BigDecimal(amount), customer, customer, isFraud, false);
    }
}
