package br.com.zenon.fraud.detector.service;

import br.com.zenon.fraud.detector.model.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class TransactionListRepository implements TransactionRepository {
    private final List<Transaction> transactions;

    public TransactionListRepository(List<Transaction> transactions) {
        Objects.requireNonNull(transactions, "transactions não pode ser nulo");
        this.transactions = new ArrayList<>(transactions);
    }

    @Override
    public void save(Transaction transaction) {
        transactions.add(Objects.requireNonNull(transaction, "transaction não pode ser nula"));
    }

    @Override
    public Optional<Transaction> findByOriginName(String originName) {
        Objects.requireNonNull(originName, "originName não pode ser nulo");
        return transactions.stream()
                .filter(transaction -> transaction.customerOrig().name().equals(originName))
                .findFirst();
    }
}
