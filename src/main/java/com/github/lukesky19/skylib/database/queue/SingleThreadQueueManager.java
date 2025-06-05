/*
    SkyLib is a library that contains shared code for all of my plugins.
    Copyright (c) 2024 lukeskywlker19

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.
 */
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
 * This abstract class provides a default implementation to asynchronously read and write to a database using a single thread.
 * For a fully asynchronous reads and writes using multiple threads, see {@link MultiThreadQueueManager}.
 */
public abstract class SingleThreadQueueManager {
    private final AbstractConnectionManager connectionManager;
    private final ExecutorService executorService;

    /**
     * Constructor that takes a class that extends {@link AbstractConnectionManager} and the
     * number of threads to use for the {@link ScheduledExecutorService}.
     * @param connectionManager A class that extends {@link AbstractConnectionManager} to use.
     */
    public SingleThreadQueueManager(@NotNull AbstractConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
        this.executorService = Executors.newFixedThreadPool(1);
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
     * Takes the provided sql and queues it.
     * @param sql The sql statement to queue.
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
     * Takes the provided sql and a {@link List} of {@link Parameter}s to replace in the sql statement, and then queues it.
     * @param sql The sql statement to queue.
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
     * Takes a {@link List} of {@link String} representing sql statements and queues them.
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
     * Takes the sql and queues it, mapping the {@link ResultSet} using the mapper function provided.
     * @param sql The sql statement to queue.
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
     * Takes the sql, sets the parameters, and queues it, mapping the {@link ResultSet} using the mapper function provided.
     * @param sql The sql statement to queue.
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

                    ResultSet resultSet = statement.executeQuery();

                    return mapper.apply(resultSet);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }, executorService);
    }
}
