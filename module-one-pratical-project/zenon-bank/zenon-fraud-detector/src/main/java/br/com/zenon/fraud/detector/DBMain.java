package br.com.zenon.fraud.detector;

import br.com.zenon.fraud.detector.model.Transaction;
import br.com.zenon.fraud.detector.service.TransactionIngestorService;
import br.com.zenon.fraud.detector.service.TransactionSQLRepository;

import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

/** Runs the requested PaySim JDBC batch benchmark against the local MySQL service. */
public final class DBMain {
    static final int JDBC_BATCH_LIMIT = 10_000;
    private static final String EXISTING_ORIGIN = "C1231006815";
    private static final String MISSING_ORIGIN = "C12345";

    private DBMain() {
    }

    public static void main(String[] args) throws IOException {
        TransactionSQLRepository repository = TransactionSQLRepository.fromConnectionProperties(
                System.getProperty("zenon.jdbc.url", TransactionSQLRepository.DEFAULT_JDBC_URL),
                System.getProperty("zenon.jdbc.user", "zenon"),
                System.getProperty("zenon.jdbc.password", "zenon"));
        run(new TransactionIngestorService(), repository, System.out);
    }

    static void run(TransactionIngestorService ingestor, TransactionSQLRepository repository, PrintStream output)
            throws IOException {
        List<Transaction> transactions = ingestor.ingestTransactions(JDBC_BATCH_LIMIT);

        try {
            repository.clearTransactions();
            long start = System.nanoTime();
            repository.saveAll(transactions);
            long durationMillis = (System.nanoTime() - start) / 1_000_000;

            output.printf("Tabela limpa; %d transações persistidas em lote em %d ms%n", transactions.size(), durationMillis);
            printSearch(repository, EXISTING_ORIGIN, output);
            printSearch(repository, MISSING_ORIGIN, output);
        } catch (IllegalStateException e) {
            System.err.println("Falha na persistência JDBC: " + e.getMessage());
            throw e;
        }
    }

    private static void printSearch(TransactionSQLRepository repository, String originName, PrintStream output) {
        repository.findByOriginName(originName)
                .ifPresentOrElse(
                        transaction -> output.println("Transação encontrada para " + originName + ": " + transaction),
                        () -> output.println("Transação não encontrada para " + originName));
    }
}
