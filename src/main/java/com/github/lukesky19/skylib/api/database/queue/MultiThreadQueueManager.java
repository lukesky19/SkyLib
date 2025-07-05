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
import com.github.lukesky19.skylib.api.database.queue.util.RunnableUtil;
import com.github.lukesky19.skylib.internal.ThreadPoolManager;
import com.github.lukesky19.skylib.api.database.parameter.Parameter;
import com.github.lukesky19.skylib.api.database.queue.util.Task;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Function;

/**
 * This abstract class provides a default implementation to asynchronously read and write to a database using multiple threads.
 * The {@link #executorService} is shared across all {@link MultiThreadQueueManager}s.
 * Make sure you properly design your database and tables to properly implement some form of optimistic locking to handle race condition issues.
 * You can use {@link SingleThreadQueueManager} to asynchronously read and write to a database in a synchronous manner.
 */
public abstract class MultiThreadQueueManager implements QueueManager {
    private final @NotNull AbstractConnectionManager connectionManager;
    private final @NotNull ScheduledExecutorService executorService;
    private final @NotNull List<CompletableFuture<?>> submittedTasksResults = new ArrayList<>();
    private boolean pauseQueue = false;
    private final @NotNull List<@NotNull Task> backupTaskQueue = new ArrayList<>();

    /**
     * Constructor that takes a class that extends {@link AbstractConnectionManager}.
     * @param connectionManager A class that extends {@link AbstractConnectionManager} to use.
     */
    public MultiThreadQueueManager(@NotNull AbstractConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
        this.executorService = ThreadPoolManager.getThreadPoolExecutor();
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

        backupQueue.forEach(task -> queueOrScheduleTask(task.runnable(), task.future(), task.time(), task.timeUnit()));
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

    @Override
    public @NotNull CompletableFuture<Void> shutdownQueue() {
        return waitForQueueEmpty();
    }

    /**
     * Takes the provided sql and queues it to be executed.
     * NOTE: If the database is in backup ({@link #pauseQueue} is true) then the task will be submitted for execution
     * after the backup is done ({@link #pauseQueue} is false), which may result in additional delays.
     * @param sql The sql statement to execute.
     * @return A {@link CompletableFuture} containing the number of rows updated if completed successfully. May complete exceptionally.
     */
    public @NotNull CompletableFuture<Integer> queueWriteTransaction(@NotNull String sql) {
        CompletableFuture<Integer> future = new CompletableFuture<>();

        Runnable runnable = RunnableUtil.createRunnableForSingleSqlExecution(connectionManager, sql, future);

        if(pauseQueue) {
            backupTaskQueue.add(new Task(runnable, future, null, null));
        } else {
            queueOrScheduleTask(runnable, future, null, null);
        }

        future.whenComplete((i, t) -> submittedTasksResults.remove(future));

        return future;
    }

    /**
     * Takes the provided sql and a {@link List} of {@link Parameter}s to replace in the sql statement, and then queues it to be executed.
     * NOTE: If the database is in backup ({@link #pauseQueue} is true) then the task will be submitted for execution
     * after the backup is done ({@link #pauseQueue} is false), which may result in additional delays.
     * @param sql The sql statement to execute.
     * @param params A {@link List} of {@link Parameter}s in the order the parameters are written in the sql statement.
     * @return A {@link CompletableFuture} containing the number of rows updated if completed successfully. May complete exceptionally.
     */
    public @NotNull CompletableFuture<Integer> queueWriteTransaction(@NotNull String sql, @NotNull List<Parameter<?>> params) {
        CompletableFuture<Integer> future = new CompletableFuture<>();

        Runnable runnable = RunnableUtil.createRunnableForSingleSqlExecution(connectionManager, sql, params, future);

        if(pauseQueue) {
            backupTaskQueue.add(new Task(runnable, future, null, null));
        } else {
            queueOrScheduleTask(runnable, future, null, null);
        }

        future.whenComplete((i, t) -> submittedTasksResults.remove(future));

        return future;
    }

    /**
     * Takes a {@link List} of {@link String} representing sql statements and queues them to be executed.
     * NOTE: If the database is in backup ({@link #pauseQueue} is true) then the task will be submitted for execution
     * after the backup is done ({@link #pauseQueue} is false), which may result in additional delays.
     * @param sqlList The {@link List} of {@link String} representing sql statements.
     * @return A {@link CompletableFuture} containing a {@link List} of the number of rows updated for each statement. May complete exceptionally.
     */
    public @NotNull CompletableFuture<List<Integer>> queueBulkWriteTransaction(@NotNull List<String> sqlList) {
        CompletableFuture<List<Integer>> future = new CompletableFuture<>();

        Runnable runnable = RunnableUtil.createRunnableForBatchSqlExecution(connectionManager, sqlList, future);

        if(pauseQueue) {
            backupTaskQueue.add(new Task(runnable, future, null, null));
        } else {
            queueOrScheduleTask(runnable, future, null, null);
        }

        future.whenComplete((i, t) -> submittedTasksResults.remove(future));

        return future;
    }

    /**
     * Takes a {@link Map} of sql statements mapped to a {@link List} of {@link Parameter}s to apply to the sql statement.
     * NOTE: If the database is in backup ({@link #pauseQueue} is true) then the task will be submitted for execution
     * after the backup is done ({@link #pauseQueue} is false), which may result in additional delays.
     * @param sqlAndParamsMap The {@link Map} mapping sql statements to a {@link List} of {@link Parameter}s.
     * @return A {@link CompletableFuture} containing a {@link List} of the number of rows updated for each statement. May complete exceptionally.
     */
    public @NotNull CompletableFuture<List<Integer>> queueBulkWriteTransaction(@NotNull Map<String, List<Parameter<?>>> sqlAndParamsMap) {
        CompletableFuture<List<Integer>> future = new CompletableFuture<>();

        Runnable runnable = RunnableUtil.createRunnableForBatchSqlExecution(connectionManager, sqlAndParamsMap, future);

        if(pauseQueue) {
            backupTaskQueue.add(new Task(runnable, future, null, null));
        } else {
            queueOrScheduleTask(runnable, future, null, null);
        }

        future.whenComplete((i, t) -> submittedTasksResults.remove(future));

        return future;
    }

    /**
     * Take the sql statement and execute for the number of parameter lists inside the list of parameter lists provided.
     * NOTE: If the database is in backup ({@link #pauseQueue} is true) then the task will be submitted for execution
     * after the backup is done ({@link #pauseQueue} is false), which may result in additional delays.
     * @param sql The sql statement to execute.
     * @param listOfParameterLists A {@link List} containing a {@link List} of {@link Parameter}s.
     * @return A {@link CompletableFuture} containing a {@link List} of the number of rows updated for each statement. May complete exceptionally.
     */
    public @NotNull CompletableFuture<List<Integer>> queueBulkWriteTransaction(@NotNull String sql, @NotNull List<List<Parameter<?>>> listOfParameterLists) {
        CompletableFuture<List<Integer>> future = new CompletableFuture<>();

        Runnable runnable = RunnableUtil.createRunnableForBatchSqlExecution(connectionManager, sql, listOfParameterLists, future);

        if (pauseQueue) {
            backupTaskQueue.add(new Task(runnable, future, null, null));
        } else {
            queueOrScheduleTask(runnable, future, null, null);
        }

        future.whenComplete((i, t) -> submittedTasksResults.remove(future));

        return future;
    }

    /**
     * Queues the provided sql and executes it after the provided delay.
     * NOTE: If the queue is paused ({@link #pauseQueue} is true) then the task will be scheduled after the
     * queue us unpaused ({@link #pauseQueue} is false) resulting in additional delays.
     * @param sql The sql statement to queue.
     * @param delay The delay before the sql statement should be executed.
     * @param timeUnit The {@link TimeUnit} of the delay.
     * @return A {@link CompletableFuture} containing the number of rows updated if completed successfully. May complete exceptionally.
     */
    public @NotNull CompletableFuture<Integer> scheduleWriteTransaction(@NotNull String sql, int delay, @NotNull TimeUnit timeUnit) {
        CompletableFuture<Integer> future = new CompletableFuture<>();

        Runnable runnable = RunnableUtil.createRunnableForSingleSqlExecution(connectionManager, sql, future);

        if(pauseQueue) {
            backupTaskQueue.add(new Task(runnable, future, delay, timeUnit));
        } else {
            queueOrScheduleTask(runnable, future, delay, timeUnit);
        }

        future.whenComplete((i, t) -> submittedTasksResults.remove(future));

        return future;
    }

    /**
     * Takes the provided sql and a {@link List} of {@link Parameter}s to replace in the sql statement, and then executes it after the provided delay.
     * NOTE: If the queue is paused ({@link #pauseQueue} is true) then the task will be scheduled after the
     * queue us unpaused ({@link #pauseQueue} is false) resulting in additional delays.
     * @param sql The sql statement to execute.
     * @param params A {@link List} of {@link Parameter}s in the order the parameters are written in the sql statement.
     * @param delay The delay before the sql statement should be executed.
     * @param timeUnit The {@link TimeUnit} of the delay.
     * @return A {@link CompletableFuture} containing the number of rows updated if completed successfully. May complete exceptionally.
     */
    public @NotNull CompletableFuture<Integer> scheduleWriteTransaction(@NotNull String sql, @NotNull List<Parameter<?>> params, int delay, @NotNull TimeUnit timeUnit) {
        CompletableFuture<Integer> future = new CompletableFuture<>();

        Runnable runnable = RunnableUtil.createRunnableForSingleSqlExecution(connectionManager, sql, params, future);

        if(pauseQueue) {
            backupTaskQueue.add(new Task(runnable, future, delay, timeUnit));
        } else {
            queueOrScheduleTask(runnable, future, delay, timeUnit);
        }

        future.whenComplete((i, t) -> submittedTasksResults.remove(future));

        return future;
    }

    /**
     * Takes a {@link List} of {@link String} representing sql statements and executes them after the provided delay.
     * NOTE: If the queue is paused ({@link #pauseQueue} is true) then the task will be scheduled after the
     * queue us unpaused ({@link #pauseQueue} is false) resulting in additional delays.
     * @param sqlList The {@link List} of {@link String} representing sql statements.
     * @param delay The delay before the sql statements should be executed.
     * @param timeUnit The {@link TimeUnit} of the delay.
     * @return A {@link CompletableFuture} containing a {@link List} of the number of rows updated for each statement. May complete exceptionally.
     */
    public @NotNull CompletableFuture<List<Integer>> scheduleBulkWriteTransaction(@NotNull List<String> sqlList, int delay, @NotNull TimeUnit timeUnit) {
        CompletableFuture<List<Integer>> future = new CompletableFuture<>();

        Runnable runnable = RunnableUtil.createRunnableForBatchSqlExecution(connectionManager, sqlList, future);

        if(pauseQueue) {
            backupTaskQueue.add(new Task(runnable, future, delay, timeUnit));
        } else {
            queueOrScheduleTask(runnable, future, delay, timeUnit);
        }

        future.whenComplete((i, t) -> submittedTasksResults.remove(future));

        return future;
    }

    /**
     * Takes a {@link Map} of sql statements mapped to a {@link List} of {@link Parameter}s to apply to the sql statement.
     * NOTE: If the queue is paused ({@link #pauseQueue} is true) then the task will be scheduled after the
     * queue us unpaused ({@link #pauseQueue} is false) resulting in additional delays.
     * @param sqlAndParamsMap The {@link Map} mapping sql statements to a {@link List} of {@link Parameter}s.
     * @param delay The delay before the sql statements should be executed.
     * @param timeUnit The {@link TimeUnit} of the delay.
     * @return A {@link CompletableFuture} containing a {@link List} of the number of rows updated for each statement. May complete exceptionally.
     */
    public @NotNull CompletableFuture<List<Integer>> scheduleBulkWriteTransaction(@NotNull Map<String, List<Parameter<?>>> sqlAndParamsMap, int delay, @NotNull TimeUnit timeUnit) {
        CompletableFuture<List<Integer>> future = new CompletableFuture<>();

        Runnable runnable = RunnableUtil.createRunnableForBatchSqlExecution(connectionManager, sqlAndParamsMap, future);

        if(pauseQueue) {
            backupTaskQueue.add(new Task(runnable, future, delay, timeUnit));
        } else {
            queueOrScheduleTask(runnable, future, delay, timeUnit);
        }

        future.whenComplete((i, t) -> submittedTasksResults.remove(future));

        return future;
    }

    /**
     * Take the sql statement and execute it for the number of parameter lists inside the list of parameter lists provided.
     * NOTE: If the database is in backup ({@link #pauseQueue} is true) then the task will be submitted for execution
     * after the backup is done ({@link #pauseQueue} is false), which may result in additional delays.
     * @param sql The sql statement to execute.
     * @param listOfParameterLists A {@link List} containing a {@link List} of {@link Parameter}s.
     * @param delay The delay before the sql statements should be executed.
     * @param timeUnit The {@link TimeUnit} of the delay.
     * @return A {@link CompletableFuture} containing a {@link List} of the number of rows updated for each statement. May complete exceptionally.
     */
    public @NotNull CompletableFuture<List<Integer>> scheduleBulkWriteTransaction(@NotNull String sql, @NotNull List<List<Parameter<?>>> listOfParameterLists, int delay, @NotNull TimeUnit timeUnit) {
        CompletableFuture<List<Integer>> future = new CompletableFuture<>();

        Runnable runnable = RunnableUtil.createRunnableForBatchSqlExecution(connectionManager, sql, listOfParameterLists, future);

        if(pauseQueue) {
            backupTaskQueue.add(new Task(runnable, future, delay, timeUnit));
        } else {
            queueOrScheduleTask(runnable, future, delay, timeUnit);
        }

        future.whenComplete((i, t) -> submittedTasksResults.remove(future));

        return future;
    }

    /**
     * Takes the sql and queues it to be executed, mapping the {@link ResultSet} using the mapper function provided.
     * NOTE: If the database is in backup ({@link #pauseQueue} is true) then the task will be submitted for execution
     * after the backup is done ({@link #pauseQueue} is false), which may result in additional delays.
     * @param sql The sql statement to execute.
     * @param mapper The mapper function that maps the {@link ResultSet} to a desired value.
     * @return A {@link CompletableFuture} containing the desired value {@link T}. May complete exceptionally.
     * @param <T> The desired value to return after the mapping function is applied.
     */
    public <T> @NotNull CompletableFuture<T> queueReadTransaction(@NotNull String sql, @NotNull Function<ResultSet, T> mapper) {
        CompletableFuture<T> future = new CompletableFuture<>();

        Runnable runnable = RunnableUtil.createRunnableForSingleSqlExecution(connectionManager, sql, mapper, future);

        if(pauseQueue) {
            backupTaskQueue.add(new Task(runnable, future, null, null));
        } else {
            queueOrScheduleTask(runnable, future, null, null);
        }

        future.whenComplete((i, t) -> submittedTasksResults.remove(future));

        return future;
    }

    /**
     * Takes the sql, sets the parameters, queues it to be executed, mapping the {@link ResultSet} using the mapper function provided.
     * NOTE: If the database is in backup ({@link #pauseQueue} is true) then the task will be submitted for execution
     * after the backup is done ({@link #pauseQueue} is false), which may result in additional delays.
     * @param sql The sql statement to execute.
     * @param params A {@link List} of {@link Parameter}s in the order the parameters are written in the sql statement.
     * @param mapper The mapper function that maps the {@link ResultSet} to a desired value.
     * @return A {@link CompletableFuture} containing the desired value {@link T}. May complete exceptionally.
     * @param <T> The desired value to return after the mapping function is applied.
     */
    public <T> @NotNull CompletableFuture<T> queueReadTransaction(@NotNull String sql, @NotNull List<Parameter<?>> params, @NotNull Function<ResultSet, T> mapper) {
        CompletableFuture<T> future = new CompletableFuture<>();

        Runnable runnable = RunnableUtil.createRunnableForSingleSqlExecution(connectionManager, sql, params, mapper, future);

        if(pauseQueue) {
            backupTaskQueue.add(new Task(runnable, future, null, null));
        } else {
            queueOrScheduleTask(runnable, future, null, null);
        }

        future.whenComplete((i, t) -> {
            submittedTasksResults.remove(future);
        });

        return future;
    }

    /**
     * Takes the sql and queues it to be executed, mapping the {@link ResultSet} using the mapper function provided.
     * NOTE: If the queue is paused ({@link #pauseQueue} is true) then the task will be scheduled after the
     * queue us unpaused ({@link #pauseQueue} is false) resulting in additional delays.
     * @param sql The sql statement to execute.
     * @param mapper The mapper function that maps the {@link ResultSet} to a desired value.
     * @param delay The delay before the sql statement should be executed.
     * @param timeUnit The {@link TimeUnit} of the delay.
     * @return A {@link CompletableFuture} containing the desired value {@link T}. May complete exceptionally. May complete exceptionally.
     * @param <T> The desired value to return after the mapping function is applied.
     */
    public <T> @NotNull CompletableFuture<T> scheduleReadTransaction(@NotNull String sql, @NotNull Function<ResultSet, T> mapper, int delay, @NotNull TimeUnit timeUnit) {
        CompletableFuture<T> future = new CompletableFuture<>();

        Runnable runnable = RunnableUtil.createRunnableForSingleSqlExecution(connectionManager, sql, mapper, future);

        if(pauseQueue) {
            backupTaskQueue.add(new Task(runnable, future, delay, timeUnit));
        } else {
            queueOrScheduleTask(runnable, future, delay, timeUnit);
        }

        future.whenComplete((i, t) -> submittedTasksResults.remove(future));

        return future;
    }

    /**
     * Takes the sql and queues it to be executed, mapping the {@link ResultSet} using the mapper function provided.
     * NOTE: If the database is in backup ({@link #pauseQueue} is true) then the task will be submitted for execution
     * after the backup is done ({@link #pauseQueue} is false), which may result in additional delays.
     * @param sql The sql statement to execute.
     * @param params A {@link List} of {@link Parameter}s in the order the parameters are written in the sql statement.
     * @param mapper The mapper function that maps the {@link ResultSet} to a desired value.
     * @param delay The delay before the sql statement should be executed.
     * @param timeUnit The {@link TimeUnit} of the delay.
     * @return A {@link CompletableFuture} containing the desired value {@link T}. May complete exceptionally.
     * @param <T> The desired value to return after the mapping function is applied.
     */
    public <T> @NotNull CompletableFuture<T> scheduleReadTransaction(@NotNull String sql, @NotNull List<Parameter<?>> params, @NotNull Function<ResultSet, T> mapper, int delay, @NotNull TimeUnit timeUnit) {
        CompletableFuture<T> future = new CompletableFuture<>();

        Runnable runnable = RunnableUtil.createRunnableForSingleSqlExecution(connectionManager, sql, params, mapper, future);

        if(pauseQueue) {
            backupTaskQueue.add(new Task(runnable, future, delay, timeUnit));
        } else {
            queueOrScheduleTask(runnable, future, delay, timeUnit);
        }

        future.whenComplete((i, t) -> submittedTasksResults.remove(future));

        return future;
    }

    /**
     * Sends a task to the {@link #executorService}
     * @param runnable The {@link Runnable} to pass to the {@link #executorService}.
     * @param future The {@link CompletableFuture} that will hold the result of the task.
     * @param time Used to schedule when the task should be executed. Optional.
     * @param timeUnit Used to schedule when the task should be executed. Optional.
     */
    private void queueOrScheduleTask(@NotNull Runnable runnable, @NotNull CompletableFuture<?> future, @Nullable Integer time, @Nullable TimeUnit timeUnit) {
        if(time != null && timeUnit != null) {
            executorService.schedule(runnable, time, timeUnit);
        } else {
            executorService.submit(runnable);
        }

        submittedTasksResults.add(future);
    }
}
