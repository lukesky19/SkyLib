package com.github.lukesky19.skylib.database.queue;

import com.github.lukesky19.skylib.database.connection.AbstractConnectionManager;
import com.github.lukesky19.skylib.database.parameter.Parameter;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Function;

/**
 * This abstract class provides a default implementation to asynchronously read and write to a database using multiple threads.
 * Make sure you properly design your database and tables to properly implement some form of optimistic locking to handle race condition issues.
 * You can use {@link SingleThreadQueueManager} to asynchronously read and write to a database in a synchronous manner.
 */
public abstract class MultiThreadQueueManager {
    private final AbstractConnectionManager connectionManager;
    private final ScheduledExecutorService executorService;

    /**
     * Constructor that takes a class that extends {@link AbstractConnectionManager} and the
     * number of threads to use for the {@link ScheduledExecutorService}.
     * @param connectionManager A class that extends {@link AbstractConnectionManager} to use.
     * @param threads The number of threads to use for the {@link ScheduledExecutorService}.
     */
    public MultiThreadQueueManager(@NotNull AbstractConnectionManager connectionManager, int threads) {
        this.connectionManager = connectionManager;
        this.executorService = Executors.newScheduledThreadPool(threads);
    }

    /**
     * Waits 60 seconds to allow any remaining tasks to finish and then shuts down the {@link #executorService}.
     * The {@link #executorService} will be shut down immediately if an {@link InterruptedException} occurs.
     * @return A {@link CompletableFuture} of {@link Void} when the queue has finished shutting down.
     */
    public CompletableFuture<Void> shutdownQueue() {
        return CompletableFuture.runAsync(() -> {
            try {
                if(!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        });
    }

    /**
     * Takes the provided sql and queues it to be executed.
     * @param sql The sql statement to execute.
     * @return A {@link CompletableFuture} containing the number of rows updated if completed successfully. May complete exceptionally.
     */
    public CompletableFuture<Integer> queueWriteTransaction(@NotNull String sql) {
        return CompletableFuture.supplyAsync(() -> {
            try(Connection connection = connectionManager.getConnection()) {
                try(Statement statement = connection.createStatement()) {
                    int rowsUpdated = statement.executeUpdate(sql);

                    if(!connection.getAutoCommit()) connection.commit();;

                    return rowsUpdated;
                } catch (SQLException e) {
                    try {
                        if(!connection.getAutoCommit()) connection.rollback();
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }

                    throw new RuntimeException(e);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }, executorService);
    }

    /**
     * Takes the provided sql and a {@link List} of {@link Parameter}s to replace in the sql statement, and then queues it to be executed.
     * @param sql The sql statement to execute.
     * @param params A {@link List} of {@link Parameter}s in the order the parameters are written in the sql statement.
     * @return A {@link CompletableFuture} containing the number of rows updated if completed successfully. May complete exceptionally.
     */
    public CompletableFuture<Integer> queueWriteTransaction(@NotNull String sql, @NotNull List<Parameter<?>> params) {
        return CompletableFuture.supplyAsync(() -> {
            try(Connection connection = connectionManager.getConnection()) {
                try(PreparedStatement statement = connection.prepareStatement(sql)) {
                    for(int i = 0; i <= params.size() - 1; i++) {
                        Parameter<?> parameter = params.get(i);
                        try {
                            statement.setObject(i + 1, parameter.getValue());
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    int rowsUpdated = statement.executeUpdate();

                    if(!connection.getAutoCommit()) connection.commit();;

                    return rowsUpdated;
                } catch (SQLException e) {
                    try {
                        if(!connection.getAutoCommit()) connection.rollback();
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }

                    throw new RuntimeException(e);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }, executorService);
    }

    /**
     * Takes a {@link List} of {@link String} representing sql statements and queues them to be executed.
     * @param sqlList The {@link List} of {@link String} representing sql statements.
     * @return A {@link CompletableFuture} containing a {@link List} of the number of rows updated for each statement. May complete exceptionally.
     */
    public CompletableFuture<List<Integer>> queueBulkWriteTransaction(@NotNull List<String> sqlList) {
        return CompletableFuture.supplyAsync(() -> {
            List<Integer> updatedRows = new ArrayList<>();

            try(Connection connection = connectionManager.getConnection()) {
                try(Statement statement = connection.createStatement()) {
                    sqlList.forEach(sql -> {
                        try {
                            updatedRows.add(statement.executeUpdate(sql));
                        } catch (SQLException e) {
                            try {
                                if(!connection.getAutoCommit()) connection.rollback();
                            } catch (SQLException ex) {
                                throw new RuntimeException(ex);
                            }

                            throw new RuntimeException(e);
                        }
                    });

                    if(!connection.getAutoCommit()) connection.commit();;

                    return updatedRows;
                } catch (SQLException e) {
                    try {
                        if(!connection.getAutoCommit()) connection.rollback();
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }

                    throw new RuntimeException(e);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }, executorService);
    }

    /**
     * Takes a {@link Map} of sql statements mapped to a {@link List} of {@link Parameter}s to apply to the sql statement.
     * @param sqlAndParamsMap The {@link Map} mapping sql statements to a {@link List} of {@link Parameter}s.
     * @return A {@link CompletableFuture} containing a {@link List} of the number of rows updated for each statement. May complete exceptionally.
     */
    public CompletableFuture<List<Integer>> queueBulkWriteTransaction(@NotNull Map<String, List<Parameter<?>>> sqlAndParamsMap) {
        return CompletableFuture.supplyAsync(() -> {
            List<Integer> updatedRows = new ArrayList<>();

            try(Connection connection = connectionManager.getConnection()) {
                sqlAndParamsMap.forEach((sql, params) -> {
                    try(PreparedStatement statement = connection.prepareStatement(sql)) {
                        for(int i = 0; i <= params.size() - 1; i++) {
                            Parameter<?> parameter = params.get(i);
                            try {
                                statement.setObject(i + 1, parameter.getValue());
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        updatedRows.add(statement.executeUpdate());
                    } catch (SQLException e) {
                        try {
                            if(!connection.getAutoCommit()) connection.rollback();
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        }

                        throw new RuntimeException(e);
                    }
                });

                try {
                    if(!connection.getAutoCommit()) connection.commit();;

                    return updatedRows;
                } catch (SQLException e) {
                    try {
                        if(!connection.getAutoCommit()) connection.rollback();
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }

                    throw new RuntimeException(e);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }, executorService);
    }

    /**
     * Queues the provided sql and executes it after the provided delay.
     * @param sql The sql statement to queue.
     * @param delay The delay before the sql statement should be executed.
     * @param timeUnit The {@link TimeUnit} of the delay.
     * @return A {@link CompletableFuture} containing the number of rows updated if completed successfully. May complete exceptionally.
     */
    public CompletableFuture<Integer> scheduleWriteTransaction(@NotNull String sql, int delay, @NotNull TimeUnit timeUnit) {
        CompletableFuture<Integer> completableFuture = new CompletableFuture<>();

        executorService.schedule(() -> {
            try(Connection connection = connectionManager.getConnection()) {
                try (Statement statement = connectionManager.getConnection().createStatement()) {
                    int rowsUpdated = statement.executeUpdate(sql);

                    if(!connection.getAutoCommit()) connection.commit();;

                    completableFuture.complete(rowsUpdated);
                } catch (SQLException e) {
                    try {
                        if(!connection.getAutoCommit()) connection.rollback();
                    } catch (SQLException ex) {
                        completableFuture.completeExceptionally(ex);
                        return;
                    }

                    completableFuture.completeExceptionally(e);
                }
            } catch (SQLException e) {
                completableFuture.completeExceptionally(e);
            }
        }, delay, timeUnit);

        return completableFuture;
    }

    /**
     * Takes the provided sql and a {@link List} of {@link Parameter}s to replace in the sql statement, and then executes it after the provided delay.
     * @param sql The sql statement to execute.
     * @param params A {@link List} of {@link Parameter}s in the order the parameters are written in the sql statement.
     * @param delay The delay before the sql statement should be executed.
     * @param timeUnit The {@link TimeUnit} of the delay.
     * @return A {@link CompletableFuture} containing the number of rows updated if completed successfully. May complete exceptionally.
     */
    public CompletableFuture<Integer> scheduleWriteTransaction(@NotNull String sql, @NotNull List<Parameter<?>> params, int delay, @NotNull TimeUnit timeUnit) {
        CompletableFuture<Integer> completableFuture = new CompletableFuture<>();

        executorService.schedule(() -> {
            try(Connection connection = connectionManager.getConnection()) {
                try(PreparedStatement statement = connectionManager.getConnection().prepareStatement(sql)) {
                    for(int i = 0; i <= params.size() - 1; i++) {
                        Parameter<?> parameter = params.get(i);
                        try {
                            statement.setObject(i + 1, parameter.getValue());
                        } catch (SQLException e) {
                            completableFuture.completeExceptionally(e);
                            return;
                        }
                    }

                    int rowsUpdated = statement.executeUpdate();

                    if(!connection.getAutoCommit()) connection.commit();;

                    completableFuture.complete(rowsUpdated);
                } catch (SQLException e) {
                    try {
                        if(!connection.getAutoCommit()) connection.rollback();
                    } catch (SQLException ex) {
                        completableFuture.completeExceptionally(ex);
                        return;
                    }

                    completableFuture.completeExceptionally(e);
                }
            } catch (SQLException e) {
                completableFuture.completeExceptionally(e);
            }
        }, delay, timeUnit);

        return completableFuture;
    }

    /**
     * Takes a {@link List} of {@link String} representing sql statements and executes them after the provided delay.
     * @param sqlList The {@link List} of {@link String} representing sql statements.
     * @param delay The delay before the sql statements should be executed.
     * @param timeUnit The {@link TimeUnit} of the delay.
     * @return A {@link CompletableFuture} containing a {@link List} of the number of rows updated for each statement. May complete exceptionally.
     */
    public CompletableFuture<List<Integer>> scheduleBulkWriteTransaction(@NotNull List<String> sqlList, int delay, @NotNull TimeUnit timeUnit) {
        CompletableFuture<List<Integer>> completableFuture = new CompletableFuture<>();

        executorService.schedule(() -> {
            List<Integer> updatedRows = new ArrayList<>();

            try(Connection connection = connectionManager.getConnection()) {
                try(Statement statement = connection.createStatement()) {
                    sqlList.forEach(sql -> {
                        try {
                            updatedRows.add(statement.executeUpdate(sql));
                        } catch (SQLException e) {
                            try {
                                if(!connection.getAutoCommit()) connection.rollback();
                            } catch (SQLException ex) {
                                completableFuture.completeExceptionally(ex);
                                return;
                            }

                            completableFuture.completeExceptionally(e);
                        }
                    });
                } catch (SQLException e) {
                    try {
                        if(!connection.getAutoCommit()) connection.rollback();
                    } catch (SQLException ex) {
                        completableFuture.completeExceptionally(ex);
                        return;
                    }

                    completableFuture.completeExceptionally(e);
                    return;
                }

                if(!connection.getAutoCommit()) connection.commit();;

                completableFuture.complete(updatedRows);
            } catch (SQLException e) {
                completableFuture.completeExceptionally(e);
            }
        }, delay, timeUnit);

        return completableFuture;
    }

    /**
     * Takes a {@link Map} of sql statements mapped to a {@link List} of {@link Parameter}s to apply to the sql statement.
     * All statements are executed after the provided delay.
     * @param sqlAndParamsMap The {@link Map} mapping sql statements to a {@link List} of {@link Parameter}s.
     * @param delay The delay before the sql statements should be executed.
     * @param timeUnit The {@link TimeUnit} of the delay.
     * @return A {@link CompletableFuture} containing a {@link List} of the number of rows updated for each statement. May complete exceptionally.
     */
    public CompletableFuture<List<Integer>> scheduleBulkWriteTransaction(@NotNull Map<String, List<Parameter<?>>> sqlAndParamsMap, int delay, @NotNull TimeUnit timeUnit) {
        CompletableFuture<List<Integer>> completableFuture = new CompletableFuture<>();

        executorService.schedule(() -> {
            List<Integer> updatedRows = new ArrayList<>();

            try(Connection connection = connectionManager.getConnection()) {
                sqlAndParamsMap.forEach((sql, params) -> {
                    try(PreparedStatement statement = connection.prepareStatement(sql)) {
                        for(int i = 0; i <= params.size() - 1; i++) {
                            Parameter<?> parameter = params.get(i);
                            try {
                                statement.setObject(i + 1, parameter.getValue());
                            } catch (SQLException e) {
                                completableFuture.completeExceptionally(e);
                                return;
                            }
                        }

                        updatedRows.add(statement.executeUpdate());
                    } catch (SQLException e) {
                        try {
                            if(!connection.getAutoCommit()) connection.rollback();
                        } catch (SQLException ex) {
                            completableFuture.completeExceptionally(ex);
                            return;
                        }

                        completableFuture.completeExceptionally(e);
                    }
                });

                if(!connection.getAutoCommit()) connection.commit();;

                completableFuture.complete(updatedRows);
            } catch (SQLException e) {
                completableFuture.completeExceptionally(e);
            }
        }, delay, timeUnit);

        return completableFuture;
    }

    /**
     * Takes the sql and queues it to be executed, mapping the {@link ResultSet} using the mapper function provided.
     * @param sql The sql statement to execute.
     * @param mapper The mapper function that maps the {@link ResultSet} to a desired value.
     * @return A {@link CompletableFuture} containing the desired value {@link T}. May complete exceptionally.
     * @param <T> The desired value to return after the mapping function is applied.
     */
    public <T> CompletableFuture<T> queueReadTransaction(@NotNull String sql, @NotNull Function<ResultSet, T> mapper) {
        return CompletableFuture.supplyAsync(() -> {
            try(Connection connection = connectionManager.getConnection()) {
                try(Statement statement = connection.createStatement()) {
                    ResultSet resultSet = statement.executeQuery(sql);

                    return mapper.apply(resultSet);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }, executorService);
    }

    /**
     * Takes the sql, sets the parameters, queues it to be executed, mapping the {@link ResultSet} using the mapper function provided.
     * @param sql The sql statement to execute.
     * @param params A {@link List} of {@link Parameter}s in the order the parameters are written in the sql statement.
     * @param mapper The mapper function that maps the {@link ResultSet} to a desired value.
     * @return A {@link CompletableFuture} containing the desired value {@link T}. May complete exceptionally.
     * @param <T> The desired value to return after the mapping function is applied.
     */
    public <T> CompletableFuture<T> queueReadTransaction(@NotNull String sql, @NotNull List<Parameter<?>> params, @NotNull Function<ResultSet, T> mapper) {
        return CompletableFuture.supplyAsync(() -> {
            try(Connection connection = connectionManager.getConnection()) {
                try(PreparedStatement statement = connection.prepareStatement(sql)) {
                    for(int i = 0; i <= params.size() - 1; i++) {
                        Parameter<?> parameter = params.get(i);
                        try {
                            statement.setObject(i + 1, parameter.getValue());
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    ResultSet resultSet = statement.executeQuery(sql);

                    return mapper.apply(resultSet);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }, executorService);
    }

    /**
     * Takes the sql and queues it to be executed, mapping the {@link ResultSet} using the mapper function provided.
     * The sql will be executed after the provided delay.
     * @param sql The sql statement to execute.
     * @param mapper The mapper function that maps the {@link ResultSet} to a desired value.
     * @param delay The delay before the sql statement should be executed.
     * @param timeUnit The {@link TimeUnit} of the delay.
     * @return A {@link CompletableFuture} containing the desired value {@link T}. May complete exceptionally. May complete exceptionally.
     * @param <T> The desired value to return after the mapping function is applied.
     */
    public <T> CompletableFuture<T> scheduleReadTransaction(@NotNull String sql, @NotNull Function<ResultSet, T> mapper, int delay, @NotNull TimeUnit timeUnit) {
        CompletableFuture<T> completableFuture = new CompletableFuture<>();

        executorService.schedule(() -> {
            try(Connection connection = connectionManager.getConnection()) {
                try(Statement statement = connection.createStatement()) {
                    ResultSet resultSet = statement.executeQuery(sql);

                    T finalResult = mapper.apply(resultSet);

                    completableFuture.complete(finalResult);
                } catch (SQLException e) {
                    completableFuture.completeExceptionally(e);
                }
            } catch (SQLException e) {
                completableFuture.completeExceptionally(e);
            }
        }, delay, timeUnit);

        return completableFuture;
    }

    /**
     * Takes the sql and queues it to be executed, mapping the {@link ResultSet} using the mapper function provided.
     * The sql will be executed after the provided delay.
     * @param sql The sql statement to execute.
     * @param params A {@link List} of {@link Parameter}s in the order the parameters are written in the sql statement.
     * @param mapper The mapper function that maps the {@link ResultSet} to a desired value.
     * @param delay The delay before the sql statement should be executed.
     * @param timeUnit The {@link TimeUnit} of the delay.
     * @return A {@link CompletableFuture} containing the desired value {@link T}. May complete exceptionally.
     * @param <T> The desired value to return after the mapping function is applied.
     */
    public <T> CompletableFuture<T> scheduleReadTransaction(@NotNull String sql, @NotNull List<Parameter<?>> params, @NotNull Function<ResultSet, T> mapper, int delay, @NotNull TimeUnit timeUnit) {
        CompletableFuture<T> completableFuture = new CompletableFuture<>();

        executorService.schedule(() -> {
            try(Connection connection = connectionManager.getConnection()) {
                try(PreparedStatement statement = connection.prepareStatement(sql)) {
                    for(int i = 0; i <= params.size() - 1; i++) {
                        Parameter<?> parameter = params.get(i);
                        try {
                            statement.setObject(i + 1, parameter.getValue());
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    ResultSet resultSet = statement.executeQuery(sql);

                    T finalResult = mapper.apply(resultSet);

                    completableFuture.complete(finalResult);
                } catch (SQLException e) {
                    completableFuture.completeExceptionally(e);
                }
            } catch (SQLException e) {
                completableFuture.completeExceptionally(e);
            }
        }, delay, timeUnit);

        return completableFuture;
    }
}
