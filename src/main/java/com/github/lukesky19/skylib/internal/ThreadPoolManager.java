package com.github.lukesky19.skylib.internal;

import com.github.lukesky19.skylib.api.database.queue.MultiThreadQueueManager;
import com.github.lukesky19.skylib.plugin.settings.Settings;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.*;

/**
 * This class manages the shared executor service for all {@link MultiThreadQueueManager} instances.
 */
public class ThreadPoolManager {
    private static ScheduledThreadPoolExecutor threadPoolExecutor;

    /**
     * All methods in this class are static so this constructor will throw a runtime exception if used.
     * @throws RuntimeException if the constructor is used.
     */
    public ThreadPoolManager() {
        throw new RuntimeException("This class cannot be instanced. Use the static references to methods instead.");
    }

    /**
     * Initializes the {@link ScheduledThreadPoolExecutor} to use across all {@link MultiThreadQueueManager} instances.
     * @param settings The plugin's {@link Settings}.
     */
    public static void initializeThreadPool(@NotNull Settings settings) {
        threadPoolExecutor = new ScheduledThreadPoolExecutor(settings.corePoolSize());
        threadPoolExecutor.setMaximumPoolSize(settings.maxPoolSize());
        threadPoolExecutor.allowCoreThreadTimeOut(true);
        threadPoolExecutor.setKeepAliveTime(settings.timeoutTimeSeconds(), TimeUnit.SECONDS);
    }

    /**
     * Gets the {@link ScheduledExecutorService}.
     * @return A {@link ScheduledExecutorService}
     */
    public static @NotNull ScheduledExecutorService getThreadPoolExecutor() {
        return threadPoolExecutor;
    }

    /**
     * Waits for the {@link ScheduledExecutorService} to finish any submitted tasks then shuts down.
     * Will forcefully shut down after 60 seconds.
     * @return A {@link CompletableFuture} of type {@link Void} once complete.
     */
    public static @NotNull CompletableFuture<Void> shutdownExecutorService() {
        return CompletableFuture.runAsync(() -> {
            try {
                threadPoolExecutor.shutdown();

                if(!threadPoolExecutor.awaitTermination(60, TimeUnit.SECONDS)) {
                    threadPoolExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                threadPoolExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        });
    }
}
