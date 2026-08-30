package br.com.zenon.fraud.detector.service;

import br.com.zenon.fraud.detector.model.Customer;
import br.com.zenon.fraud.detector.model.Transaction;
import br.com.zenon.fraud.detector.model.TypeTransaction;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class TransactionIngestorService implements CommandLineRunner {
    private static final String PAYSIM_PATH = "data/PS_20174392719_1491204439457_log.csv";
    private static final String PAYSIM_DIVISOR = ",";
    private List<Transaction> transactions = new ArrayList<>();
    int counter = 1;

    @Override
    public void run(String... args) throws Exception {
        try {
            BufferedReader br = new BufferedReader(new FileReader(PAYSIM_PATH));
            while (br.readLine() != null && counter <= 1000) {
                String line = br.readLine();
                String[] column = line.split(PAYSIM_DIVISOR);
                transactions.add(newTransaction(column));
                counter++;
            }
            br.close();
            transactions.stream().limit(10).forEach(System.out::println);

        } catch (IOException e) {
            System.err.println("Erro ao ler arquivo: " + e.getMessage());
        }
    }

    private Transaction newTransaction(String[] column) {
        Customer customerOrig = new Customer(column[3], new BigDecimal(column[4]), new BigDecimal(column[5]));
        Customer customerDest = new Customer(column[6], new BigDecimal(column[7]), new BigDecimal(column[8]));
        return new Transaction(Long.valueOf(column[0]), getTypeTransaction(column[1]), new BigDecimal(column[2]), customerOrig, customerDest, "1".equals(column[9]), "1".equals(column[10]));
    }

    private TypeTransaction getTypeTransaction(String line) {
        return Arrays.stream(TypeTransaction.values()).filter(t -> t.getValue().equals(line)).findFirst().orElse(null);
    }


}
