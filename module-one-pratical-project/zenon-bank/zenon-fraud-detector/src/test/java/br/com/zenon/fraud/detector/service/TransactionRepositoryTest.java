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
                repository.findByCustomerOrigName("C2").orElseThrow()));
    }

    @Test
    void returnsEmptyForAnAbsentCustomer() {
        forEachRepository(repository -> assertFalse(repository.findByCustomerOrigName("C12345").isPresent()));
    }

    @Test
    void preservesTheFirstTransactionForRepeatedCustomerNames() {
        forEachRepository(repository -> assertEquals(FIRST_DUPLICATE,
                repository.findByCustomerOrigName("C1").orElseThrow()));
    }

    @Test
    void findsTheFinalCustomerThroughBothStrategies() {
        Transaction finalTransaction = transaction(4, "C1868032458", "40.00");
        List<Transaction> transactions = List.of(FIRST_DUPLICATE, SECOND_DUPLICATE, OTHER_CUSTOMER, finalTransaction);

        assertEquals(finalTransaction,
                new TransactionListRepository(transactions).findByCustomerOrigName("C1868032458").orElseThrow());
        assertEquals(finalTransaction,
                new TransactionMapRepository(transactions).findByCustomerOrigName("C1868032458").orElseThrow());
    }

    private void forEachRepository(java.util.function.Consumer<TransactionRepository> assertion) {
        List<Transaction> transactions = List.of(FIRST_DUPLICATE, SECOND_DUPLICATE, OTHER_CUSTOMER);
        assertion.accept(new TransactionListRepository(transactions));
        assertion.accept(new TransactionMapRepository(transactions));
    }

    private static Transaction transaction(long step, String customerOrigName, String amount) {
        Customer customerOrig = new Customer(customerOrigName, BigDecimal.ZERO, BigDecimal.ZERO);
        Customer customerDest = new Customer("M" + step, BigDecimal.ZERO, BigDecimal.ZERO);
        return new Transaction(step, TypeTransaction.PAYMENT, new BigDecimal(amount), customerOrig, customerDest, false, false);
    }
}
