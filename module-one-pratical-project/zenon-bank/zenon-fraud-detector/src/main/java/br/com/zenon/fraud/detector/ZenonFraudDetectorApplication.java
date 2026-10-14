package br.com.zenon.fraud.detector;

import br.com.zenon.fraud.detector.model.Transaction;
import br.com.zenon.fraud.detector.model.TypeTransaction;
import br.com.zenon.fraud.detector.service.FraudAnalyzer;
import br.com.zenon.fraud.detector.service.TransactionIngestorService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

@SpringBootApplication
public class ZenonFraudDetectorApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZenonFraudDetectorApplication.class, args);
    }

    @Bean
    CommandLineRunner fraudAnalysisRunner(TransactionIngestorService ingestor) {
        return args -> printReport(ingestor.ingestTransactions(TransactionIngestorService.MAX_TRANSACTIONS));
    }

    private void printReport(List<Transaction> transactions) {
        FraudAnalyzer analyzer = new FraudAnalyzer();

        System.out.println("1. Total de Fraudes: " + analyzer.totalFrauds(transactions));
        System.out.println("2. Top 3 Fraudes de Maior Valor:");
        analyzer.topFrauds(transactions).forEach(transaction -> System.out.println(formatAmount(transaction.amount())));
        System.out.println("3. Clientes Suspeitos:");
        analyzer.suspiciousCustomerNames(transactions).forEach(System.out::println);
        System.out.println("4. Prejuízo Total: " + formatAmount(analyzer.totalFraudLoss(transactions)));
        System.out.println("5. Fraudes por Tipo:");
        Map<TypeTransaction, Long> counts = analyzer.fraudCountsByType(transactions);
        for (TypeTransaction type : TypeTransaction.values()) {
            Long count = counts.get(type);
            if (count != null) {
                System.out.println(" - " + type.getValue() + ": " + count);
            }
        }
    }

    private String formatAmount(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }
}
