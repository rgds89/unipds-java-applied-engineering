package br.com.zenon.fraud.detector.service;

import br.com.zenon.fraud.detector.model.Transaction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class TransactionMapRepository implements TransactionRepository {
    private final Map<String, Transaction> transactionsByCustomerOrigName;

    public TransactionMapRepository(List<Transaction> transactions) {
        Objects.requireNonNull(transactions, "transactions não pode ser nulo");
        Map<String, Transaction> indexedTransactions = new HashMap<>();
        for (Transaction transaction : transactions) {
            indexedTransactions.putIfAbsent(transaction.customerOrig().name(), transaction);
        }
        this.transactionsByCustomerOrigName = indexedTransactions;
    }

    @Override
    public void save(Transaction transaction) {
        Transaction nonNullTransaction = Objects.requireNonNull(transaction, "transaction não pode ser nula");
        transactionsByCustomerOrigName.putIfAbsent(nonNullTransaction.customerOrig().name(), nonNullTransaction);
    }

    @Override
    public Optional<Transaction> findByOriginName(String originName) {
        Objects.requireNonNull(originName, "originName não pode ser nulo");
        return Optional.ofNullable(transactionsByCustomerOrigName.get(originName));
    }
}
