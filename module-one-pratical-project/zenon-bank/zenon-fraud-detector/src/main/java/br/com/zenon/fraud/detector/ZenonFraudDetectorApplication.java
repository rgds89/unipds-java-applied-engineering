package br.com.zenon.fraud.detector;

import br.com.zenon.fraud.detector.model.Transaction;
import br.com.zenon.fraud.detector.model.TypeTransaction;
import br.com.zenon.fraud.detector.service.FraudAnalyzer;
import br.com.zenon.fraud.detector.service.TransactionIngestorService;
import br.com.zenon.fraud.detector.service.TransactionListRepository;
import br.com.zenon.fraud.detector.service.TransactionMapRepository;
import br.com.zenon.fraud.detector.service.TransactionRepository;
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
    private static final String EXISTING_CUSTOMER = "C1231006815";
    private static final String MISSING_CUSTOMER = "C12345";
    private static final String LAST_CUSTOMER = "C1868032458";

    public static void main(String[] args) {
        SpringApplication.run(ZenonFraudDetectorApplication.class, args);
    }

    @Bean
    CommandLineRunner fraudAnalysisRunner(TransactionIngestorService ingestor) {
        return args -> {
            List<Transaction> transactions = ingestor.ingestTransactions(TransactionIngestorService.MAX_TRANSACTIONS);
            printReport(transactions);
            demonstrateTransactionSearch(transactions);
        };
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

    private void demonstrateTransactionSearch(List<Transaction> transactions) {
        TransactionRepository listRepository = new TransactionListRepository(transactions);
        TransactionRepository mapRepository = new TransactionMapRepository(transactions);

        System.out.println("Quantidade de transações carregadas: " + transactions.size());
        printSearchResult(listRepository, EXISTING_CUSTOMER);
        printSearchResult(listRepository, MISSING_CUSTOMER);
        printBenchmark("lista", listRepository);
        printBenchmark("mapa", mapRepository);
    }

    private void printSearchResult(TransactionRepository repository, String customerOrigName) {
        repository.findByCustomerOrigName(customerOrigName)
                .ifPresentOrElse(
                        System.out::println,
                        () -> System.out.println("Transação não encontrada para o cliente " + customerOrigName));
    }

    private void printBenchmark(String repositoryName, TransactionRepository repository) {
        long start = System.nanoTime();
        var transaction = repository.findByCustomerOrigName(LAST_CUSTOMER);
        long duration = System.nanoTime() - start;

        printSearchResult(transaction, LAST_CUSTOMER);
        System.out.println("Busca em " + repositoryName + " para " + LAST_CUSTOMER + ": " + duration + " nanos");
    }

    private void printSearchResult(java.util.Optional<Transaction> transaction, String customerOrigName) {
        transaction.ifPresentOrElse(
                System.out::println,
                () -> System.out.println("Transação não encontrada para o cliente " + customerOrigName));
    }
}
