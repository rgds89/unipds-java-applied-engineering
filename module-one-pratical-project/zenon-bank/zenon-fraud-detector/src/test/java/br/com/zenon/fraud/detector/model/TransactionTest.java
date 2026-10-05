package br.com.zenon.fraud.detector.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;

class TransactionTest {
    private static final Customer CUSTOMER = new Customer("C1", BigDecimal.ZERO, BigDecimal.ZERO);

    @Test
    void rejectsNullFields() {
        assertThrows(NullPointerException.class,
                () -> new Transaction(1L, null, BigDecimal.ZERO, CUSTOMER, CUSTOMER, false, false));
        assertThrows(NullPointerException.class,
                () -> new Transaction(1L, TypeTransaction.PAYMENT, BigDecimal.ZERO, null, CUSTOMER, false, false));
    }

    @Test
    void rejectsInvalidStepAndNegativeAmount() {
        assertThrows(IllegalArgumentException.class,
                () -> new Transaction(0L, TypeTransaction.PAYMENT, BigDecimal.ZERO, CUSTOMER, CUSTOMER, false, false));
        assertThrows(IllegalArgumentException.class,
                () -> new Transaction(1L, TypeTransaction.PAYMENT, new BigDecimal("-0.01"), CUSTOMER, CUSTOMER, false, false));
    }

    @Test
    void acceptsZeroValues() {
        new Transaction(1L, TypeTransaction.PAYMENT, BigDecimal.ZERO, CUSTOMER, CUSTOMER, false, false);
    }
}
