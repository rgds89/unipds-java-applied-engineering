package br.com.zenon.fraud.detector.service;

import br.com.zenon.fraud.detector.model.Customer;
import br.com.zenon.fraud.detector.model.Transaction;
import br.com.zenon.fraud.detector.model.TypeTransaction;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class TransactionRepositoryTest {
    private static final Transaction FIRST_DUPLICATE = transaction(1, "C1", "10.00");
    private static final Transaction SECOND_DUPLICATE = transaction(2, "C1", "20.00");
    private static final Transaction OTHER_CUSTOMER = transaction(3, "C2", "30.00");

    @Test
    void findsTheTransactionForAnExistingCustomer() {
        forEachRepository(repository -> assertEquals(OTHER_CUSTOMER,
                repository.findByOriginName("C2").orElseThrow()));
    }

    @Test
    void returnsEmptyForAnAbsentCustomer() {
        forEachRepository(repository -> assertFalse(repository.findByOriginName("C12345").isPresent()));
    }

    @Test
    void preservesTheFirstTransactionForRepeatedCustomerNames() {
        forEachRepository(repository -> assertEquals(FIRST_DUPLICATE,
                repository.findByOriginName("C1").orElseThrow()));
    }

    @Test
    void findsTheFinalCustomerThroughBothStrategies() {
        Transaction finalTransaction = transaction(4, "C1868032458", "40.00");
        List<Transaction> transactions = List.of(FIRST_DUPLICATE, SECOND_DUPLICATE, OTHER_CUSTOMER, finalTransaction);

        assertEquals(finalTransaction,
                new TransactionListRepository(transactions).findByOriginName("C1868032458").orElseThrow());
        assertEquals(finalTransaction,
                new TransactionMapRepository(transactions).findByOriginName("C1868032458").orElseThrow());
    }

    @Test
    void savesANewTransactionWithoutExposingInternalState() {
        Transaction saved = transaction(5, "C5", "50.00");

        forEachEmptyRepository(repository -> {
            repository.save(saved);
            assertEquals(saved, repository.findByOriginName("C5").orElseThrow());
        });
    }

    @Test
    void keepsTheFirstSavedTransactionForRepeatedOriginNames() {
        forEachEmptyRepository(repository -> {
            repository.save(FIRST_DUPLICATE);
            repository.save(SECOND_DUPLICATE);
            assertEquals(FIRST_DUPLICATE, repository.findByOriginName("C1").orElseThrow());
        });
    }

    private void forEachRepository(java.util.function.Consumer<TransactionRepository> assertion) {
        List<Transaction> transactions = List.of(FIRST_DUPLICATE, SECOND_DUPLICATE, OTHER_CUSTOMER);
        assertion.accept(new TransactionListRepository(transactions));
        assertion.accept(new TransactionMapRepository(transactions));
    }

    private void forEachEmptyRepository(java.util.function.Consumer<TransactionRepository> assertion) {
        assertion.accept(new TransactionListRepository(List.of()));
        assertion.accept(new TransactionMapRepository(List.of()));
    }

    private static Transaction transaction(long step, String customerOrigName, String amount) {
        Customer customerOrig = new Customer(customerOrigName, BigDecimal.ZERO, BigDecimal.ZERO);
        Customer customerDest = new Customer("M" + step, BigDecimal.ZERO, BigDecimal.ZERO);
        return new Transaction(step, TypeTransaction.PAYMENT, new BigDecimal(amount), customerOrig, customerDest, false, false);
    }
}
