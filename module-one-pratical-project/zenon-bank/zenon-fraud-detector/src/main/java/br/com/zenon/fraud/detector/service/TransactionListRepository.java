package br.com.zenon.fraud.detector.service;

import br.com.zenon.fraud.detector.model.Transaction;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class TransactionListRepository implements TransactionRepository {
    private final List<Transaction> transactions;

    public TransactionListRepository(List<Transaction> transactions) {
        this.transactions = List.copyOf(transactions);
    }

    @Override
    public Optional<Transaction> findByCustomerOrigName(String customerOrigName) {
        Objects.requireNonNull(customerOrigName, "customerOrigName não pode ser nulo");
        return transactions.stream()
                .filter(transaction -> transaction.customerOrig().name().equals(customerOrigName))
                .findFirst();
    }
}
