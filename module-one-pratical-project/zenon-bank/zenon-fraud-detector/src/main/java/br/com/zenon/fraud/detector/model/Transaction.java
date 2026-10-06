package br.com.zenon.fraud.detector.model;

import java.math.BigDecimal;
import java.util.Objects;

public record Transaction(Long step,
                          TypeTransaction type,
                          BigDecimal amount,
                          Customer customerOrig,
                          Customer customerDest,
                          Boolean isFraud,
                          Boolean isFlaggedFraud
                          ) {
    public Transaction {
        Objects.requireNonNull(step, "step não pode ser nulo");
        Objects.requireNonNull(type, "type não pode ser nulo");
        Objects.requireNonNull(amount, "amount não pode ser nulo");
        Objects.requireNonNull(customerOrig, "customerOrig não pode ser nulo");
        Objects.requireNonNull(customerDest, "customerDest não pode ser nulo");
        Objects.requireNonNull(isFraud, "isFraud não pode ser nulo");
        Objects.requireNonNull(isFlaggedFraud, "isFlaggedFraud não pode ser nulo");

        if (step < 1) {
            throw new IllegalArgumentException("step deve ser maior ou igual a 1");
        }
        if (amount.signum() < 0) {
            throw new IllegalArgumentException("amount não pode ser negativo");
        }
    }

    /**
     * Compatibilidade com consumidores da versão que expunha o componente como setp.
     */
    @Deprecated
    public Long setp() {
        return step;
    }
}
