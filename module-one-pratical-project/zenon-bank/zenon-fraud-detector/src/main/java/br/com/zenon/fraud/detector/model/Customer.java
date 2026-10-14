package br.com.zenon.fraud.detector.model;

import java.math.BigDecimal;
import java.util.Objects;

public record Customer(String name, BigDecimal oldBalance, BigDecimal newBalance) {
    public Customer {
        Objects.requireNonNull(name, "name não pode ser nulo");
        Objects.requireNonNull(oldBalance, "oldBalance não pode ser nulo");
        Objects.requireNonNull(newBalance, "newBalance não pode ser nulo");

        if (oldBalance.signum() < 0 || newBalance.signum() < 0) {
            throw new IllegalArgumentException("saldos não podem ser negativos");
        }
    }
}
