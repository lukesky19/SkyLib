package com.github.lukesky19.skylib.listener;

import com.github.lukesky19.skylib.player.PlayerUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Listens to when a Player joins the server and caches their PlayerProfile.
 */
public class LoginListener implements Listener {
    /**
     * Constructor.
     * Used to initialize a class instance to register the listener.
     */
    public LoginListener() {}

    /**
     * Listens to when a Player joins the server and caches their PlayerProfile.
     * @param playerJoinEvent A PlayerJoinEvent.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent playerJoinEvent) {
        PlayerUtil.cachePlayerProfile(playerJoinEvent.getPlayer().getUniqueId(), playerJoinEvent.getPlayer().getPlayerProfile());
    }
}
