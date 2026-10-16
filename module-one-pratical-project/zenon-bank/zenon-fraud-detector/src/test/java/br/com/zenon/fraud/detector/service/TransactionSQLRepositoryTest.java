package br.com.zenon.fraud.detector.service;

import br.com.zenon.fraud.detector.model.Customer;
import br.com.zenon.fraud.detector.model.Transaction;
import br.com.zenon.fraud.detector.model.TypeTransaction;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TransactionSQLRepositoryTest {
    private static final Transaction TRANSACTION = transaction(1, "C1231006815");

    @Test
    void savesOneTransactionWithACompletePreparedStatement() {
        RecordingJdbc jdbc = new RecordingJdbc(Optional.empty());

        new TransactionSQLRepository(jdbc::connection).save(TRANSACTION);

        assertEquals(TransactionSQLRepository.INSERT_SQL, jdbc.preparedSql().getFirst());
        assertEquals(List.of(1L, "PAYMENT", new BigDecimal("10.00"), "C1231006815",
                BigDecimal.ZERO, BigDecimal.ZERO, "M1", BigDecimal.ZERO, BigDecimal.ZERO, false, false),
                jdbc.executedUpdateParameters());
    }

    @Test
    void savesEveryTransactionThroughOnePreparedBatch() {
        RecordingJdbc jdbc = new RecordingJdbc(Optional.empty());
        Transaction second = transaction(2, "C2");

        new TransactionSQLRepository(jdbc::connection).saveAll(List.of(TRANSACTION, second));

        assertEquals(1, jdbc.executeBatchCalls());
        assertEquals(2, jdbc.batchParameters().size());
        assertEquals("C1231006815", jdbc.batchParameters().getFirst().get(3));
        assertEquals("C2", jdbc.batchParameters().get(1).get(3));
        assertTrue(jdbc.preparedSql().stream().allMatch(TransactionSQLRepository.INSERT_SQL::equals));
    }

    @Test
    void findsPresentAndAbsentOrigins() {
        RecordingJdbc presentJdbc = new RecordingJdbc(Optional.of(TRANSACTION));
        TransactionSQLRepository presentRepository = new TransactionSQLRepository(presentJdbc::connection);
        RecordingJdbc absentJdbc = new RecordingJdbc(Optional.empty());
        TransactionSQLRepository absentRepository = new TransactionSQLRepository(absentJdbc::connection);

        assertEquals(TRANSACTION, presentRepository.findByOriginName("C1231006815").orElseThrow());
        assertFalse(absentRepository.findByOriginName("C12345").isPresent());
        assertEquals("C1231006815", presentJdbc.queryParameter());
        assertEquals("C12345", absentJdbc.queryParameter());
    }

    @Test
    void wrapsDatabaseFailuresWithTheirSqlCause() {
        TransactionSQLRepository repository = new TransactionSQLRepository(() -> {
            throw new SQLException("database offline");
        });

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> repository.save(TRANSACTION));

        assertInstanceOf(SQLException.class, exception.getCause());
        assertEquals("database offline", exception.getCause().getMessage());
    }

    private static Transaction transaction(long step, String originName) {
        return new Transaction(step, TypeTransaction.PAYMENT, new BigDecimal("10.00"),
                new Customer(originName, BigDecimal.ZERO, BigDecimal.ZERO),
                new Customer("M" + step, BigDecimal.ZERO, BigDecimal.ZERO), false, false);
    }

    private static final class RecordingJdbc {
        private final Optional<Transaction> queryResult;
        private final List<String> preparedSql = new ArrayList<>();
        private final List<List<Object>> batchParameters = new ArrayList<>();
        private List<Object> executedUpdateParameters = List.of();
        private int executeBatchCalls;
        private String queryParameter;

        private RecordingJdbc(Optional<Transaction> queryResult) {
            this.queryResult = queryResult;
        }

        private Connection connection() {
            return proxy(Connection.class, (proxy, method, args) -> {
                if (method.getName().equals("prepareStatement")) {
                    String sql = (String) args[0];
                    preparedSql.add(sql);
                    return statement(sql);
                }
                return defaultValue(method.getReturnType());
            });
        }

        private PreparedStatement statement(String sql) {
            Map<Integer, Object> parameters = new HashMap<>();
            return proxy(PreparedStatement.class, (proxy, method, args) -> {
                switch (method.getName()) {
                    case "setLong", "setString", "setBigDecimal", "setBoolean" -> {
                        parameters.put((Integer) args[0], args[1]);
                        return null;
                    }
                    case "addBatch" -> {
                        batchParameters.add(ordered(parameters));
                        return null;
                    }
                    case "executeBatch" -> {
                        executeBatchCalls++;
                        return new int[batchParameters.size()];
                    }
                    case "executeUpdate" -> {
                        executedUpdateParameters = ordered(parameters);
                        return 1;
                    }
                    case "executeQuery" -> {
                        queryParameter = (String) parameters.get(1);
                        return resultSet();
                    }
                    default -> {
                        return defaultValue(method.getReturnType());
                    }
                }
            });
        }

        private ResultSet resultSet() {
            return proxy(ResultSet.class, new InvocationHandler() {
                private boolean read;

                @Override
                public Object invoke(Object proxy, java.lang.reflect.Method method, Object[] args) {
                    if (method.getName().equals("next")) {
                        if (read || queryResult.isEmpty()) {
                            return false;
                        }
                        read = true;
                        return true;
                    }
                    if (method.getName().startsWith("get")) {
                        return value((String) args[0]);
                    }
                    return defaultValue(method.getReturnType());
                }
            });
        }

        private Object value(String column) {
            Transaction transaction = queryResult.orElseThrow();
            return switch (column) {
                case "step" -> transaction.step();
                case "transaction_type" -> transaction.type().getValue();
                case "amount" -> transaction.amount();
                case "origin_name" -> transaction.customerOrig().name();
                case "origin_old_balance" -> transaction.customerOrig().oldBalance();
                case "origin_new_balance" -> transaction.customerOrig().newBalance();
                case "destination_name" -> transaction.customerDest().name();
                case "destination_old_balance" -> transaction.customerDest().oldBalance();
                case "destination_new_balance" -> transaction.customerDest().newBalance();
                case "is_fraud" -> transaction.isFraud();
                case "is_flagged_fraud" -> transaction.isFlaggedFraud();
                default -> throw new IllegalArgumentException("coluna desconhecida: " + column);
            };
        }

        private List<Object> ordered(Map<Integer, Object> parameters) {
            return java.util.stream.IntStream.rangeClosed(1, 11).mapToObj(parameters::get).toList();
        }

        List<String> preparedSql() {
            return preparedSql;
        }

        List<List<Object>> batchParameters() {
            return batchParameters;
        }

        List<Object> executedUpdateParameters() {
            return executedUpdateParameters;
        }

        int executeBatchCalls() {
            return executeBatchCalls;
        }

        String queryParameter() {
            return queryParameter;
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T proxy(Class<T> type, InvocationHandler handler) {
        return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type}, handler);
    }

    private static Object defaultValue(Class<?> type) {
        if (!type.isPrimitive()) {
            return null;
        }
        if (type == boolean.class) {
            return false;
        }
        if (type == int.class) {
            return 0;
        }
        if (type == long.class) {
            return 0L;
        }
        return 0;
    }
}
