package com.github.lukesky19.skylib.plugin.settings;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * The plugin's settings.
 * @param corePoolSize The core pool size to initialize the {@link ScheduledThreadPoolExecutor} with.
 * @param maxPoolSize The max pool size to initialize the {@link ScheduledThreadPoolExecutor} with.
 * @param timeoutTimeSeconds The time in seconds when a thread should be timed out.
 */
@ConfigSerializable
public record Settings(
        int corePoolSize,
        int maxPoolSize,
        int timeoutTimeSeconds) {}
