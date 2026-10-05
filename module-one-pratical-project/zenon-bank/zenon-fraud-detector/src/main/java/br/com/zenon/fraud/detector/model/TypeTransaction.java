package br.com.zenon.fraud.detector.model;

public enum TypeTransaction {
    CASH_IN("CASH_IN"),
    CASH_OUT("CASH_OUT"),
    DEBIT("DEBIT"),
    PAYMENT("PAYMENT"),
    TRANSFER("TRANSFER");
    private String value;
    TypeTransaction(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
}
