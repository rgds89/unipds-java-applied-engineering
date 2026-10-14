package br.com.zenon.fraud.detector.service;

import br.com.zenon.fraud.detector.model.Transaction;

import java.util.Optional;

public interface TransactionRepository {
    Optional<Transaction> findByCustomerOrigName(String customerOrigName);
}
