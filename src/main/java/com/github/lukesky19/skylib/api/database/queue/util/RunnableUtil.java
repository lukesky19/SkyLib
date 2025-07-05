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
package com.github.lukesky19.skylib.api.database.queue.util;

import com.github.lukesky19.skylib.api.database.connection.AbstractConnectionManager;
import com.github.lukesky19.skylib.api.database.parameter.Parameter;
import com.github.lukesky19.skylib.api.database.queue.QueueManager;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * This class is used to create {@link Runnable}s for use with classes that implement {@link QueueManager}.
 */
public class RunnableUtil {
    /**
     * All methods in this class are static so this constructor will throw a runtime exception if used.
     * @throws RuntimeException if the constructor is used.
     */
    public RunnableUtil() {
        throw new RuntimeException("This class cannot be instanced. Use the static references to methods instead.");
    }

    /**
     * Creates the {@link Runnable} that executes the provided sql statement as a {@link String} which returns the
     * rows updated or any exceptions that occur using the provided {@link CompletableFuture}.
     * @param connectionManager The {@link AbstractConnectionManager} that manages {@link Connection}s to the database.
     * @param sql The sql statement as a {@link String}.
     * @param future A {@link CompletableFuture} that will be used to return the number of rows updated or the exception that occurred (if any).
     * @return A {@link Runnable} that contains the task to run to execute the sql statement provided.
     */
    public static @NotNull Runnable createRunnableForSingleSqlExecution(
            @NotNull AbstractConnectionManager connectionManager,
            @NotNull String sql,
            @NotNull CompletableFuture<Integer> future) {
        return () -> {
            try(Connection connection = connectionManager.getConnection()) {
                try(Statement statement = connection.createStatement()) {
                    int rowsUpdated = statement.executeUpdate(sql);

                    if(!connection.getAutoCommit()) connection.commit();

                    future.complete(rowsUpdated);
                } catch (SQLException e) {
                    try {
                        if(!connection.getAutoCommit()) connection.rollback();
                    } catch (SQLException ex) {
                        future.completeExceptionally(ex);
                        return;
                    }

                    future.completeExceptionally(e);
                }
            } catch (SQLException e) {
                future.completeExceptionally(e);
            }
        };
    }

    /**
     * Creates the {@link Runnable} that executes the provided sql statement as a {@link String} which returns the
     * rows updated or any exceptions that occur using the provided {@link CompletableFuture}.
     * @param connectionManager The {@link AbstractConnectionManager} that manages {@link Connection}s to the database.
     * @param sql The sql statement as a {@link String}.
     * @param params A {@link List} of {@link Parameter}s to replace any placeholders in the sql statement.
     * @param future A {@link CompletableFuture} that will be used to return the number of rows updated or the
     * exception that occurred (if any).
     * @return A {@link Runnable} that contains the task to run to execute the sql statement provided.
     */
    public static @NotNull Runnable createRunnableForSingleSqlExecution(
            @NotNull AbstractConnectionManager connectionManager,
            @NotNull String sql,
            @NotNull List<Parameter<?>> params,
            @NotNull CompletableFuture<Integer> future) {
        return () -> {
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

                    if(!connection.getAutoCommit()) connection.commit();

                    future.complete(rowsUpdated);
                } catch (SQLException e) {
                    try {
                        if(!connection.getAutoCommit()) connection.rollback();
                    } catch (SQLException ex) {
                        future.completeExceptionally(ex);
                        return;
                    }

                    future.completeExceptionally(e);
                }
            } catch (SQLException e) {
                future.completeExceptionally(e);
            }
        };
    }

    /**
     * Creates the {@link Runnable} that executes the {@link List} containing {@link String}s of sql statements which
     * returns a {@link List} of {@link Integer} for the rows updated or any exception that occurs using the
     * provided {@link CompletableFuture}.
     * @param connectionManager The {@link AbstractConnectionManager} that manages {@link Connection}s to the database.
     * @param sqlList The {@link List} of {@link String} representing sql statements.
     * @param future A {@link CompletableFuture} that will be used to return a {@link List} containing the number of
     * rows updated or the exception that occurred (if any).
     * @return A {@link Runnable} that contains the task to run to execute the sql statements provided.
     */
    public static @NotNull Runnable createRunnableForBatchSqlExecution(
            @NotNull AbstractConnectionManager connectionManager,
            @NotNull List<String> sqlList,
            @NotNull CompletableFuture<List<Integer>> future) {
        return () -> {
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
                                future.completeExceptionally(ex);
                                return;
                            }

                            future.completeExceptionally(e);
                        }
                    });

                    if(!connection.getAutoCommit()) connection.commit();

                    future.complete(updatedRows);
                } catch (SQLException e) {
                    try {
                        if(!connection.getAutoCommit()) connection.rollback();
                    } catch (SQLException ex) {
                        future.completeExceptionally(ex);
                        return;
                    }

                    future.completeExceptionally(e);
                }
            } catch (SQLException e) {
                future.completeExceptionally(e);
            }
        };
    }

    /**
     * Creates the {@link Runnable} that executes a list of sql statements which returns a {@link List} of
     * {@link Integer} for the rows updated or any exception that occurs using the provided {@link CompletableFuture}.
     * @param connectionManager The {@link AbstractConnectionManager} that manages {@link Connection}s to the database.
     * @param sqlAndParamsMap The sql statements mapped to a {@link List} of {@link Parameter}s to replace any placeholders in the sql statement.
     * @param future A {@link CompletableFuture} that will be used to return a {@link List} containing the number of
     * rows updated or the exception that occurred (if any).
     * @return A {@link Runnable} that contains the task to run to execute the sql statements provided.
     */
    public static @NotNull Runnable createRunnableForBatchSqlExecution(
            @NotNull AbstractConnectionManager connectionManager,
            @NotNull Map<String, List<Parameter<?>>> sqlAndParamsMap,
            @NotNull CompletableFuture<List<Integer>> future) {
        return () -> {
            List<Integer> updatedRows = new ArrayList<>();

            try(Connection connection = connectionManager.getConnection()) {
                sqlAndParamsMap.forEach((sql, params) -> {
                    try(PreparedStatement statement = connection.prepareStatement(sql)) {
                        for(int i = 0; i <= params.size() - 1; i++) {
                            Parameter<?> parameter = params.get(i);
                            try {
                                statement.setObject(i + 1, parameter.getValue());
                            } catch (SQLException e) {
                                future.completeExceptionally(e);
                                return;
                            }
                        }

                        updatedRows.add(statement.executeUpdate());
                    } catch (SQLException e) {
                        try {
                            if(!connection.getAutoCommit()) connection.rollback();
                        } catch (SQLException ex) {
                            future.completeExceptionally(ex);
                            return;
                        }

                        future.completeExceptionally(e);
                    }
                });

                try {
                    if(!connection.getAutoCommit()) connection.commit();

                    future.complete(updatedRows);
                } catch (SQLException e) {
                    try {
                        if(!connection.getAutoCommit()) connection.rollback();
                    } catch (SQLException ex) {
                        future.completeExceptionally(ex);
                        return;
                    }

                    future.completeExceptionally(e);
                }
            } catch (SQLException e) {
                future.completeExceptionally(e);
            }
        };
    }

    /**
     * Creates the {@link Runnable} that executes the sql statement for each {@link List} of {@link Parameter} in the list of parameter lists provided.
     * Completes the future with a {@link List} of {@link Integer} for the rows updated or any exception that occurs using the provided {@link CompletableFuture}.
     * @param connectionManager The {@link AbstractConnectionManager} that manages {@link Connection}s to the database.
     * @param sql The sql statement to execute for each list of parameters.
     * @param listOfParameterLists A {@link List} containing a {@link List} of {@link Parameter}s.
     * @param future A {@link CompletableFuture} that will be used to return a {@link List} containing the number of
     * rows updated or the exception that occurred (if any).
     * @return A {@link Runnable} that contains the task to run to execute the sql statements provided.
     */
    public static @NotNull Runnable createRunnableForBatchSqlExecution(
            @NotNull AbstractConnectionManager connectionManager,
            @NotNull String sql,
            @NotNull List<List<Parameter<?>>> listOfParameterLists,
            @NotNull CompletableFuture<List<Integer>> future) {
        return () -> {
            List<Integer> updatedRows = new ArrayList<>();

            try(Connection connection = connectionManager.getConnection()) {
                listOfParameterLists.forEach(parameterList -> {
                    try(PreparedStatement statement = connection.prepareStatement(sql)) {
                        for(int i = 0; i <= parameterList.size() - 1; i++) {
                            Parameter<?> parameter = parameterList.get(i);
                            try {
                                statement.setObject(i + 1, parameter.getValue());
                            } catch (SQLException e) {
                                future.completeExceptionally(e);
                                return;
                            }
                        }

                        updatedRows.add(statement.executeUpdate());
                    } catch (SQLException e) {
                        try {
                            if(!connection.getAutoCommit()) connection.rollback();
                        } catch (SQLException ex) {
                            future.completeExceptionally(ex);
                            return;
                        }

                        future.completeExceptionally(e);
                    }

                    try {
                        if(!connection.getAutoCommit()) connection.commit();

                        future.complete(updatedRows);
                    } catch (SQLException e) {
                        try {
                            if(!connection.getAutoCommit()) connection.rollback();
                        } catch (SQLException ex) {
                            future.completeExceptionally(ex);
                            return;
                        }

                        future.completeExceptionally(e);
                    }
                });
            } catch (SQLException e) {
                future.completeExceptionally(e);
            }
        };
    }

    /**
     * Creates the {@link Runnable} that executes the provided sql statement as a {@link String} which returns {@link T}
     * or any exception that occurs using the provided {@link CompletableFuture}.
     * @param connectionManager The {@link AbstractConnectionManager} that manages {@link Connection}s to the database.
     * @param sql The sql statement as a {@link String}.
     * @param mapper The function that maps the {@link ResultSet} to the value {@link T}.
     * @param future A {@link CompletableFuture} that will be used to return {@link T} or the exception that occurred (if any).
     * @return A {@link Runnable} that contains the task to run to execute the sql statement provided.
     * @param <T> The object that is created and returned when the mapper function is run.
     */
    public static <T> @NotNull Runnable createRunnableForSingleSqlExecution(
            @NotNull AbstractConnectionManager connectionManager,
            @NotNull String sql,
            @NotNull Function<ResultSet, T> mapper,
            @NotNull CompletableFuture<T> future) {
        return () -> {
            try(Connection connection = connectionManager.getConnection()) {
                try(Statement statement = connection.createStatement()) {
                    ResultSet resultSet = statement.executeQuery(sql);

                    future.complete(mapper.apply(resultSet));
                } catch (SQLException e) {
                    future.completeExceptionally(e);
                }
            } catch (SQLException e) {
                future.completeExceptionally(e);
            }
        };
    }

    /**
     * Creates the {@link Runnable} that executes the provided sql statement as a {@link String} which returns {@link T}
     * or any exception that occurs using the provided {@link CompletableFuture}.
     * @param connectionManager The {@link AbstractConnectionManager} that manages {@link Connection}s to the database.
     * @param sql The sql statement as a {@link String}.
     * @param params A {@link List} of {@link Parameter}s to replace any placeholders in the sql statement.
     * @param mapper The function that maps the {@link ResultSet} to the value {@link T}.
     * @param future A {@link CompletableFuture} that will be used to return {@link T} or the exception that occurred (if any).
     * @return A {@link Runnable} that contains the task to run to execute the sql statement provided.
     * @param <T> The object that is created and returned when the mapper function is run.
     */
    public static <T> @NotNull Runnable createRunnableForSingleSqlExecution(
            @NotNull AbstractConnectionManager connectionManager,
            @NotNull String sql,
            @NotNull List<Parameter<?>> params,
            @NotNull Function<ResultSet, T> mapper,
            @NotNull CompletableFuture<T> future) {
        return () -> {
            try (Connection connection = connectionManager.getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    for (int i = 0; i <= params.size() - 1; i++) {
                        Parameter<?> parameter = params.get(i);
                        try {
                            statement.setObject(i + 1, parameter.getValue());
                        } catch (SQLException e) {
                            future.completeExceptionally(e);
                            return;
                        }
                    }

                    ResultSet resultSet = statement.executeQuery();

                    future.complete(mapper.apply(resultSet));
                } catch (SQLException e) {
                    future.completeExceptionally(e);
                }
            } catch (SQLException e) {
                future.completeExceptionally(e);
            }
        };
    }
}
