package br.com.zenon.fraud.detector.service;

import br.com.zenon.fraud.detector.model.Transaction;

import java.util.Optional;

public interface TransactionRepository {
    void save(Transaction transaction);

    Optional<Transaction> findByOriginName(String originName);
}
