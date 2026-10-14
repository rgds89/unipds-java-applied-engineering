package br.com.zenon.fraud.detector.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TransactionTest {
    private static final Customer CUSTOMER = new Customer("C1", BigDecimal.ZERO, BigDecimal.ZERO);

    @Test
    void rejectsNullFields() {
        assertThrows(NullPointerException.class,
                () -> new Transaction(1L, null, BigDecimal.ZERO, CUSTOMER, CUSTOMER, false, false));
        assertThrows(NullPointerException.class,
                () -> new Transaction(1L, TypeTransaction.PAYMENT, BigDecimal.ZERO, null, CUSTOMER, false, false));
        assertThrows(NullPointerException.class,
                () -> new Transaction(null, TypeTransaction.PAYMENT, BigDecimal.ZERO, CUSTOMER, CUSTOMER, false, false));
        assertThrows(NullPointerException.class,
                () -> new Transaction(1L, TypeTransaction.PAYMENT, null, CUSTOMER, CUSTOMER, false, false));
        assertThrows(NullPointerException.class,
                () -> new Transaction(1L, TypeTransaction.PAYMENT, BigDecimal.ZERO, CUSTOMER, null, false, false));
        assertThrows(NullPointerException.class,
                () -> new Transaction(1L, TypeTransaction.PAYMENT, BigDecimal.ZERO, CUSTOMER, CUSTOMER, null, false));
        assertThrows(NullPointerException.class,
                () -> new Transaction(1L, TypeTransaction.PAYMENT, BigDecimal.ZERO, CUSTOMER, CUSTOMER, false, null));
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

    @Test
    void preservesLegacyStepAccessor() {
        Transaction transaction = new Transaction(1L, TypeTransaction.PAYMENT, BigDecimal.ZERO, CUSTOMER, CUSTOMER, false, false);

        assertEquals(1L, transaction.setp());
    }

    @Test
    void customerRejectsInvalidState() {
        assertThrows(NullPointerException.class,
                () -> new Customer(null, BigDecimal.ZERO, BigDecimal.ZERO));
        assertThrows(NullPointerException.class,
                () -> new Customer("C1", null, BigDecimal.ZERO));
        assertThrows(IllegalArgumentException.class,
                () -> new Customer("C1", new BigDecimal("-0.01"), BigDecimal.ZERO));
    }
}
