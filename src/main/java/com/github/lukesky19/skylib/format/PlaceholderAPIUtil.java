/*
    SkyLib is a library that contains shared code for all of my plugins.
    Copyright (C) 2024  lukeskywlker19

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package com.github.lukesky19.skylib.format;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 * Parses PlaceholderAPI Placeholders
 */
public class PlaceholderAPIUtil {
    /**
     * Parses a placeholder for an online player.
     * @param player A Bukkit Player
     * @param str The string containing placeholders to parse.
     * @return A String with PlaceholderAPI placeholders replaced.
     */
    public static String parsePlaceholders(Player player, String str) {
        return PlaceholderAPI.setPlaceholders(player, str);
    }

    /**
     * Parses a placeholder for an offline player.
     * @param player A Bukkit OfflinePlayer
     * @param str The string containing placeholders to parse.
     * @return A String with PlaceholderAPI placeholders replaced.
     */
    public static String parsePlaceholders(OfflinePlayer player, String str) {
        return PlaceholderAPI.setPlaceholders(player, str);
    }

    /**
     * Parses a placeholder for a null player.
     * @param str The string containing placeholders to parse.
     * @return A String with PlaceholderAPI placeholders replaced.
     */
    public static String parsePlaceholders(String str) {
        return PlaceholderAPI.setPlaceholders(null, str);
    }
}
