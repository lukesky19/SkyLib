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
package com.github.lukesky19.skylib.api.database.queue;

import com.github.lukesky19.skylib.api.database.connection.AbstractConnectionManager;
import com.github.lukesky19.skylib.api.database.parameter.Parameter;
import com.github.lukesky19.skylib.api.database.queue.util.RunnableUtil;
import com.github.lukesky19.skylib.api.database.queue.util.Task;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Function;

/**
 * This abstract class provides a default implementation to asynchronously read and write to a database using a single thread.
 * WARNING: There is the potential to run out of threads using this class as there will be 1 thread dedicated to the {@link #executorService} even when idle.
 * For a fully asynchronous reads and writes using multiple threads, see {@link MultiThreadQueueManager}. Make sure you read the entire documentation for the class.
 */
public abstract class SingleThreadQueueManager implements QueueManager {
    private final AbstractConnectionManager connectionManager;
    private final ExecutorService executorService;
    private final @NotNull List<CompletableFuture<?>> submittedTasksResults = new ArrayList<>();
    private boolean pauseQueue = false;
    private final @NotNull List<@NotNull Task> backupTaskQueue = new ArrayList<>();

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
     * Sets the {@link #pauseQueue} variable to the boolean provided. true will pause the queue, false will not.
     * If the queue status is set from true -> false, the {@link #backupTaskQueue} will have its tasks submitted to the {@link #executorService}.
     * @param status The status to set.
     */
    public void setQueueStatus(boolean status) {
        if(this.pauseQueue && !status) {
            processBackupQueue();
        }

        this.pauseQueue = status;
    }

    /**
     * Takes any tasks submitted to the {@link #backupTaskQueue} and submits them to the {@link #executorService}.
     * You should use {@link #setQueueStatus(boolean)} with the boolean false after this method completes.
     */
    public void processBackupQueue() {
        List<@NotNull Task> backupQueue = new ArrayList<>(backupTaskQueue);
        backupTaskQueue.clear();

        backupQueue.forEach(task -> queueTask(task.runnable(), task.future()));
    }

    /**
     * Get notified by a {@link CompletableFuture} of type {@link Void} when the queue is empty.
     * You should use {@link #setQueueStatus(boolean)} with the boolean true to pause tasks being submitted to the {@link #executorService} before calling this method.
     * @return A {@link CompletableFuture} of type {@link Void}.
     */
    public @NotNull CompletableFuture<Void> waitForQueueEmpty() {
        if(submittedTasksResults.isEmpty()) return CompletableFuture.completedFuture(null);

        return CompletableFuture.allOf(submittedTasksResults.toArray(new CompletableFuture[0]));
    }

    /**
     * Waits 60 seconds to allow any remaining tasks to finish and then shuts down the {@link #executorService}.
     * The {@link #executorService} will be shut down immediately if an {@link InterruptedException} occurs.
     * @return A {@link CompletableFuture} of {@link Void} when the queue has finished shutting down.
     */
    @Override
    public @NotNull CompletableFuture<Void> shutdownQueue() {
        return CompletableFuture.runAsync(() -> {
            try {
                executorService.shutdown();

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
    public @NotNull CompletableFuture<Integer> queueWriteTransaction(@NotNull String sql) {
        CompletableFuture<Integer> future = new CompletableFuture<>();

        Runnable runnable = RunnableUtil.createRunnable(connectionManager, sql, future);

        if(pauseQueue) {
            backupTaskQueue.add(new Task(runnable, future, null, null));
        } else {
            queueTask(runnable, future);
        }

        future.whenComplete((i, t) -> submittedTasksResults.remove(future));

        return future;
    }

    /**
     * Takes the provided sql and a {@link List} of {@link Parameter}s to replace in the sql statement, and then queues it.
     * @param sql The sql statement to queue.
     * @param params A {@link List} of {@link Parameter}s in the order the parameters are written in the sql statement.
     * @return A {@link CompletableFuture} containing the number of rows updated if completed successfully. May complete exceptionally.
     */
    public @NotNull CompletableFuture<Integer> queueWriteTransaction(@NotNull String sql, @NotNull List<Parameter<?>> params) {
        CompletableFuture<Integer> future = new CompletableFuture<>();

        Runnable runnable = RunnableUtil.createRunnable(connectionManager, sql, params, future);

        if(pauseQueue) {
            backupTaskQueue.add(new Task(runnable, future, null, null));
        } else {
            queueTask(runnable, future);
        }

        future.whenComplete((i, t) -> submittedTasksResults.remove(future));

        return future;
    }

    /**
     * Takes a {@link List} of {@link String} representing sql statements and queues them.
     * @param sqlList The {@link List} of {@link String} representing sql statements.
     * @return A {@link CompletableFuture} containing a {@link List} of the number of rows updated for each statement. May complete exceptionally.
     */
    public @NotNull CompletableFuture<List<Integer>> queueBulkWriteTransaction(@NotNull List<String> sqlList) {
        CompletableFuture<List<Integer>> future = new CompletableFuture<>();

        Runnable runnable = RunnableUtil.createRunnable(connectionManager, sqlList, future);

        if(pauseQueue) {
            backupTaskQueue.add(new Task(runnable, future, null, null));
        } else {
            queueTask(runnable, future);
        }

        future.whenComplete((i, t) -> submittedTasksResults.remove(future));

        return future;
    }

    /**
     * Takes a {@link Map} of sql statements mapped to a {@link List} of {@link Parameter}s to apply to the sql statement.
     * @param sqlAndParamsMap The {@link Map} mapping sql statements to a {@link List} of {@link Parameter}s.
     * @return A {@link CompletableFuture} containing a {@link List} of the number of rows updated for each statement. May complete exceptionally.
     */
    public @NotNull CompletableFuture<List<Integer>> queueBulkWriteTransaction(@NotNull Map<String, List<Parameter<?>>> sqlAndParamsMap) {
        CompletableFuture<List<Integer>> future = new CompletableFuture<>();

        Runnable runnable = RunnableUtil.createRunnable(connectionManager, sqlAndParamsMap, future);

        if(pauseQueue) {
            backupTaskQueue.add(new Task(runnable, future, null, null));
        } else {
            queueTask(runnable, future);
        }

        future.whenComplete((i, t) -> submittedTasksResults.remove(future));

        return future;
    }

    /**
     * Takes the sql and queues it, mapping the {@link ResultSet} using the mapper function provided.
     * @param sql The sql statement to queue.
     * @param mapper The mapper function that maps the {@link ResultSet} to a desired value.
     * @return A {@link CompletableFuture} containing the desired value {@link T}. May complete exceptionally.
     * @param <T> The desired value to return after the mapping function is applied.
     */
    public <T> @NotNull CompletableFuture<T> queueReadTransaction(@NotNull String sql, @NotNull Function<ResultSet, T> mapper) {
        CompletableFuture<T> future = new CompletableFuture<>();

        Runnable runnable = RunnableUtil.createRunnable(connectionManager, sql, mapper, future);

        if(pauseQueue) {
            backupTaskQueue.add(new Task(runnable, future, null, null));
        } else {
            queueTask(runnable, future);
        }

        future.whenComplete((i, t) -> submittedTasksResults.remove(future));

        return future;
    }

    /**
     * Takes the sql, sets the parameters, and queues it, mapping the {@link ResultSet} using the mapper function provided.
     * @param sql The sql statement to queue.
     * @param params A {@link List} of {@link Parameter}s in the order the parameters are written in the sql statement.
     * @param mapper The mapper function that maps the {@link ResultSet} to a desired value.
     * @return A {@link CompletableFuture} containing the desired value {@link T}. May complete exceptionally.
     * @param <T> The desired value to return after the mapping function is applied.
     */
    public <T> @NotNull CompletableFuture<T> queueReadTransaction(@NotNull String sql, @NotNull List<Parameter<?>> params, @NotNull Function<ResultSet, T> mapper) {
        CompletableFuture<T> future = new CompletableFuture<>();

        Runnable runnable = RunnableUtil.createRunnable(connectionManager, sql, params, mapper, future);

        if(pauseQueue) {
            backupTaskQueue.add(new Task(runnable, future, null, null));
        } else {
            queueTask(runnable, future);
        }

        future.whenComplete((i, t) -> submittedTasksResults.remove(future));

        return future;
    }

    /**
     * Sends a task to the {@link #executorService}
     * @param runnable The {@link Runnable} to pass to the {@link #executorService}.
     * @param future The {@link CompletableFuture} that will hold the result of the task.
     */
    private void queueTask(@NotNull Runnable runnable, @NotNull CompletableFuture<?> future) {
        executorService.submit(runnable);

        submittedTasksResults.add(future);
    }
}
