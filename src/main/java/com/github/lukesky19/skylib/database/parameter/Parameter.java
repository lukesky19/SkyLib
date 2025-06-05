package com.github.lukesky19.skylib.database.parameter;

import java.sql.PreparedStatement;

/**
 * Used to create implementations to store values that are used to replace parameters in {@link PreparedStatement}s.
 * @param <R> The object type of the Parameter.
 */
public interface Parameter<R> {
    /**
     * Get the value of {@link R} to replace the parameter with.
     * @return The value {@link R} to replace a parameter with.
     */
    R getValue();
}
