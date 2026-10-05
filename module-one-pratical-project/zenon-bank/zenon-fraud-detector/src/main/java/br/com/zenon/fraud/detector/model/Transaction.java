package br.com.zenon.fraud.detector.model;

import java.math.BigDecimal;

public record Transaction(Long setp,
                          TypeTransaction type,
                          BigDecimal amount,
                          Customer customerOrig,
                          Customer customerDest,
                          Boolean isFraud,
                          Boolean isFlaggedFraud
                          ) {
}
