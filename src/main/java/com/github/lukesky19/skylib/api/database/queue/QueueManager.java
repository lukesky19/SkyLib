package com.github.lukesky19.skylib.api.database.queue;

import com.github.lukesky19.skylib.api.database.parameter.Parameter;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * This interface is used to create different classes that manages queues for reading from and writing to a database.
 */
public interface QueueManager {
    /**
     * Set the status of the queue.
     * @param status true to pause the queue, false to unpause.
     */
    void setQueueStatus(boolean status);

    /**
     * Process the backup queue when the queue status is set from true to false.
     */
    void processBackupQueue();

    /**
     * Get a {@link CompletableFuture} of type {@link Void} to be notified when the queue is empty.
     * @return A {@link CompletableFuture} of type {@link Void}.
     */
    @NotNull CompletableFuture<Void> waitForQueueEmpty();

    /**
     * Used to shut down the queue.
     * @return A {@link CompletableFuture} of type {@link Void} when the shut-down of the queue is completed.
     */
    @NotNull CompletableFuture<Void> shutdownQueue();

    /**
     * Queue the sql statement to write to the database.
     * @param sql The sql statement as a {@link String}.
     * @return A {@link CompletableFuture} of type {@link Integer} that contains the number of rows updated.
     */
    @NotNull CompletableFuture<Integer> queueWriteTransaction(@NotNull String sql);

    /**
     * Queue the sql statement to write to the database.
     * @param sql The sql statement as a {@link String}.
     * @param params A {@link List} of {@link Parameter} that are used to replace parameters in the sql statement.
     * @return A {@link CompletableFuture} of type {@link Integer} that contains the number of rows updated.
     */
    @NotNull CompletableFuture<Integer> queueWriteTransaction(@NotNull String sql, @NotNull List<Parameter<?>> params);

    /**
     * Queue a {@link List} of {@link String} containing the sql statements to write to the database.
     * @param sqlList The {@link List} of {@link String} containing the sql statements.
     * @return A {@link CompletableFuture} of type {@link List} where the {@link List} contains that contains the number of rows updated for each sql statement.
     */
    @NotNull CompletableFuture<@NotNull List<@NotNull Integer>> queueBulkWriteTransaction(@NotNull List<String> sqlList);

    /**
     * Queue a {@link List} of {@link String} containing the sql statements to write to the database.
     * @param sqlAndParamsMap A {@link Map} that maps a sql statement to a {@link List} of {@link Parameter} that are used to replace parameters in the sql statement.
     * @return A {@link CompletableFuture} of type {@link List} where the {@link List} contains that contains the number of rows updated for each sql statement.
     */
    @NotNull CompletableFuture<@NotNull List<@NotNull Integer>> queueBulkWriteTransaction(@NotNull Map<String, List<Parameter<?>>> sqlAndParamsMap);

    /**
     * Queues a sql statement to read from the database.
     * @param sql The sql statement as a {@link String}.
     * @param mapper The function to map the {@link ResultSet} to the value {@link T}.
     * @return A {@link CompletableFuture} containing the object {@link T}.
     * @param <T> The object {@link T} created using the data from {@link ResultSet}.
     */
    @NotNull <T> CompletableFuture<@NotNull T> queueReadTransaction(@NotNull String sql, @NotNull Function<ResultSet, T> mapper);

    /**
     * Queues a sql statement to read from the database.
     * @param sql The sql statement as a {@link String}.
     * @param params A {@link List} of {@link Parameter} that are used to replace parameters in the sql statement.
     * @param mapper The function to map the {@link ResultSet} to the value {@link T}.
     * @return A {@link CompletableFuture} containing the object {@link T}.
     * @param <T> The object {@link T} created using the data from {@link ResultSet}.
     */
    @NotNull <T> CompletableFuture<@NotNull T> queueReadTransaction(@NotNull String sql, @NotNull List<Parameter<?>> params, @NotNull Function<ResultSet, T> mapper);
}
