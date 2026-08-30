package br.com.zenon.fraud.detector;

import br.com.zenon.fraud.detector.model.Customer;
import br.com.zenon.fraud.detector.model.Transaction;
import br.com.zenon.fraud.detector.model.TypeTransaction;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;


public class ZenonFraud  implements CommandLineRunner {
    @Override
    public void run(String @NonNull ... args) throws Exception {
        Customer customerOrig1 = new Customer("C1231006815", new BigDecimal("170136.0"), new BigDecimal("160296.36"));
        Customer customerOrig2 = new Customer("C1280323807", new BigDecimal("850002.52"), BigDecimal.ZERO);
        Customer customerDest1 = new Customer("M1979787155", BigDecimal.ZERO, BigDecimal.ZERO);
        Customer customerDest2 = new Customer("C873221189", new BigDecimal("6510099.11"), new BigDecimal("7360101.63"));
        Transaction transaction1 = new Transaction(1L, TypeTransaction.PAYMENT, new BigDecimal("9839.64"), customerOrig1, customerDest1, false, false);
        Transaction transaction2 = new Transaction(743L, TypeTransaction.CASH_OUT, new BigDecimal("9839.64"), customerOrig2, customerDest2, true, false);
        System.out.println("Transação 1:" + transaction1.toString());
        System.out.println("Transação 2:" + transaction2.toString());
    }
}
