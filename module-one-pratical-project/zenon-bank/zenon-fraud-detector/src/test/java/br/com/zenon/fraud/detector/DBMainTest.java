package br.com.zenon.fraud.detector;

import br.com.zenon.fraud.detector.model.Customer;
import br.com.zenon.fraud.detector.model.Transaction;
import br.com.zenon.fraud.detector.model.TypeTransaction;
import br.com.zenon.fraud.detector.service.TransactionIngestorService;
import br.com.zenon.fraud.detector.service.TransactionSQLRepository;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DBMainTest {
    private static final Transaction EXISTING = transaction("C1231006815");

    @Test
    void loadsExactlyTenThousandTransactionsInOneBatchAndReportsBothSearches() throws IOException {
        RecordingIngestor ingestor = new RecordingIngestor(Collections.nCopies(DBMain.JDBC_BATCH_LIMIT, EXISTING));
        RecordingRepository repository = new RecordingRepository();
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        DBMain.run(ingestor, repository, new PrintStream(output));

        assertEquals(DBMain.JDBC_BATCH_LIMIT, ingestor.requestedLimit);
        assertEquals(DBMain.JDBC_BATCH_LIMIT, repository.savedTransactions.size());
        String report = output.toString();
        assertTrue(report.contains("10000 transações persistidas em lote"));
        assertTrue(report.contains("Transação encontrada para C1231006815"));
        assertTrue(report.contains("Transação não encontrada para C12345"));
    }

    private static Transaction transaction(String originName) {
        return new Transaction(1L, TypeTransaction.PAYMENT, BigDecimal.TEN,
                new Customer(originName, BigDecimal.ZERO, BigDecimal.ZERO),
                new Customer("M1", BigDecimal.ZERO, BigDecimal.ZERO), false, false);
    }

    private static final class RecordingIngestor extends TransactionIngestorService {
        private final List<Transaction> transactions;
        private int requestedLimit;

        private RecordingIngestor(List<Transaction> transactions) {
            this.transactions = transactions;
        }

        @Override
        public List<Transaction> ingestTransactions(int limit) {
            requestedLimit = limit;
            return transactions;
        }
    }

    private static final class RecordingRepository extends TransactionSQLRepository {
        private List<Transaction> savedTransactions = List.of();

        private RecordingRepository() {
            super(() -> {
                throw new SQLException("not used by this test");
            });
        }

        @Override
        public void saveAll(List<Transaction> transactions) {
            savedTransactions = List.copyOf(transactions);
        }

        @Override
        public Optional<Transaction> findByOriginName(String originName) {
            return "C1231006815".equals(originName) ? Optional.of(EXISTING) : Optional.empty();
        }
    }
}
