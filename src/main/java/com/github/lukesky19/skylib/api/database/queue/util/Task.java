package com.github.lukesky19.skylib.api.database.queue.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * This record contains a task stored in the backup queue.
 * @param runnable The {@link Runnable} containing the task to execute.
 * @param future The {@link CompletableFuture} that should contain the result.
 * @param time The delay until when the task should be executed.
 * @param timeUnit The {@link TimeUnit} of the delay time above.
 */
public record Task(
        @NotNull Runnable runnable,
        @NotNull CompletableFuture<?> future,
        @Nullable Integer time,
        @Nullable TimeUnit timeUnit) {
}
