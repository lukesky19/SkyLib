package com.github.lukesky19.skylib.api.database;

import com.github.lukesky19.skylib.api.database.connection.AbstractConnectionManager;
import com.github.lukesky19.skylib.api.database.queue.QueueManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;

/**
 * This class manages access to a database. Also provides the ability to backup a database.
 */
public class AbstractDatabaseManager {
    private final @NotNull AbstractConnectionManager connectionManager;
    private final @NotNull QueueManager queueManager;
    private @Nullable CompletableFuture<Void> backupTask;

    /**
     * Constructor
     * @param connectionManager A class that extends {@link AbstractConnectionManager}.
     * @param queueManager A class that implements {@link QueueManager}.
     */
    public AbstractDatabaseManager(
            @NotNull AbstractConnectionManager connectionManager,
            @NotNull QueueManager queueManager) {
        this.connectionManager = connectionManager;
        this.queueManager = queueManager;
    }

    /**
     * Saves a copy of the database located at the source path to a "database_backups" folder in the parent directory of the source path.
     * The database queue will be temporarily paused until the backup is complete.
     * @param sourcePath The {@link Path} of the database.
     * @return A {@link CompletableFuture} of type {@link Void} that can be used to know when the operation is complete.
     */
    protected @NotNull CompletableFuture<Void> backupDatabase(@NotNull Path sourcePath) {
        queueManager.setQueueStatus(true);

        backupTask = queueManager.waitForQueueEmpty().thenCompose(v1 -> {
            // Get the current timestamp as a  String
            String timestamp = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss-SSS")
                    .withZone(ZoneId.systemDefault())
                    .format(Instant.now());

            // Create the new filename with the timestamp
            String newFileName = sourcePath.getFileName().toString().replaceFirst("(\\.[^.]+)?$", "-" + timestamp + "$1");
            // Create the destination path
            Path destinationPath = sourcePath.getParent().resolve("database_backups" + File.separator + newFileName);

            // Copy the current database to the destination path.
            return CompletableFuture.runAsync(() -> {
                try {
                    // If the source file doesn't exist throw an error
                    if(!Files.exists(sourcePath)) {
                        throw new RuntimeException("Source file does not exist: " + sourcePath);
                    }

                    // If the destination directory doesn't exist, create it
                    Path destinationDir = destinationPath.getParent();
                    if (!Files.exists(destinationDir)) {
                        Files.createDirectories(destinationDir);
                    }

                    Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to copy file: " + e.getMessage(), e);
                }
            });
        }).whenComplete((v, t) -> {
            queueManager.setQueueStatus(false);
            backupTask = null;
        });

        return backupTask;
    }

    /**
     * Shuts down the queue and then closes any connections to the database.
     * If a backup is occurring, it will wait until that is complete and the backlog of scheduled tasks are complete.
     * @return A {@link CompletableFuture} of type {@link Void} that can be used to know when the operation is complete.
     */
    public @NotNull CompletableFuture<Void> handlePluginDisable() {
        if(backupTask != null) {
            return backupTask.thenCompose(v1 -> {
                // Wait for all queued tasks to complete and the queue to shut down.
                return queueManager.shutdownQueue().thenCompose(v2 -> {
                    // Then close connections
                    connectionManager.closeConnections();
                    return null;
                });
            });
        } else {
            // Wait for all queued tasks to complete and the queue to shut down.
            return queueManager.shutdownQueue().thenCompose(v2 -> {
                // Then close connections
                connectionManager.closeConnections();
                return null;
            });
        }
    }
}
