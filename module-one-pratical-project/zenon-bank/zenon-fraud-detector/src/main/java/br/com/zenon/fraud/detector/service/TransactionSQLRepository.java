package br.com.zenon.fraud.detector.service;

import br.com.zenon.fraud.detector.model.Customer;
import br.com.zenon.fraud.detector.model.Transaction;
import br.com.zenon.fraud.detector.model.TypeTransaction;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/** JDBC-backed transaction storage with a deliberately explicit batch API. */
public class TransactionSQLRepository implements TransactionRepository {
    static final String INSERT_SQL = """
            INSERT INTO transactions (
                step, transaction_type, amount, origin_name, origin_old_balance, origin_new_balance,
                destination_name, destination_old_balance, destination_new_balance, is_fraud, is_flagged_fraud
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
    private static final String FIND_BY_ORIGIN_SQL = """
            SELECT step, transaction_type, amount, origin_name, origin_old_balance, origin_new_balance,
                   destination_name, destination_old_balance, destination_new_balance, is_fraud, is_flagged_fraud
            FROM transactions
            WHERE origin_name = ?
            ORDER BY id
            LIMIT 1
            """;

    private final ConnectionProvider connectionProvider;

    public TransactionSQLRepository(ConnectionProvider connectionProvider) {
        this.connectionProvider = Objects.requireNonNull(connectionProvider, "connectionProvider não pode ser nulo");
    }

    public static TransactionSQLRepository fromConnectionProperties(String url, String user, String password) {
        Objects.requireNonNull(url, "url não pode ser nula");
        Objects.requireNonNull(user, "user não pode ser nulo");
        Objects.requireNonNull(password, "password não pode ser nula");
        return new TransactionSQLRepository(() -> DriverManager.getConnection(url, user, password));
    }

    @Override
    public void save(Transaction transaction) {
        Transaction nonNullTransaction = Objects.requireNonNull(transaction, "transaction não pode ser nula");
        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_SQL)) {
            bind(statement, nonNullTransaction);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Não foi possível salvar a transação no MySQL", e);
        }
    }

    /** Persists every supplied transaction through one JDBC addBatch/executeBatch operation. */
    public void saveAll(List<Transaction> transactions) {
        Objects.requireNonNull(transactions, "transactions não pode ser nula");
        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_SQL)) {
            for (Transaction transaction : transactions) {
                bind(statement, Objects.requireNonNull(transaction, "transaction não pode ser nula"));
                statement.addBatch();
            }
            statement.executeBatch();
        } catch (SQLException e) {
            throw new IllegalStateException("Não foi possível salvar o lote de transações no MySQL", e);
        }
    }

    @Override
    public Optional<Transaction> findByOriginName(String originName) {
        Objects.requireNonNull(originName, "originName não pode ser nulo");
        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_ORIGIN_SQL)) {
            statement.setString(1, originName);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? Optional.of(readTransaction(resultSet)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Não foi possível buscar a transação no MySQL", e);
        }
    }

    private void bind(PreparedStatement statement, Transaction transaction) throws SQLException {
        statement.setLong(1, transaction.step());
        statement.setString(2, transaction.type().getValue());
        statement.setBigDecimal(3, transaction.amount());
        statement.setString(4, transaction.customerOrig().name());
        statement.setBigDecimal(5, transaction.customerOrig().oldBalance());
        statement.setBigDecimal(6, transaction.customerOrig().newBalance());
        statement.setString(7, transaction.customerDest().name());
        statement.setBigDecimal(8, transaction.customerDest().oldBalance());
        statement.setBigDecimal(9, transaction.customerDest().newBalance());
        statement.setBoolean(10, transaction.isFraud());
        statement.setBoolean(11, transaction.isFlaggedFraud());
    }

    private Transaction readTransaction(ResultSet resultSet) throws SQLException {
        Customer origin = new Customer(
                resultSet.getString("origin_name"),
                resultSet.getBigDecimal("origin_old_balance"),
                resultSet.getBigDecimal("origin_new_balance"));
        Customer destination = new Customer(
                resultSet.getString("destination_name"),
                resultSet.getBigDecimal("destination_old_balance"),
                resultSet.getBigDecimal("destination_new_balance"));
        return new Transaction(
                resultSet.getLong("step"),
                TypeTransaction.valueOf(resultSet.getString("transaction_type")),
                resultSet.getBigDecimal("amount"),
                origin,
                destination,
                resultSet.getBoolean("is_fraud"),
                resultSet.getBoolean("is_flagged_fraud"));
    }

    @FunctionalInterface
    public interface ConnectionProvider {
        Connection getConnection() throws SQLException;
    }
}
