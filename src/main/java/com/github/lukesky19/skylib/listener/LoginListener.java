package com.github.lukesky19.skylib.listener;

import com.github.lukesky19.skylib.player.PlayerUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class LoginListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        PlayerUtil.cachePlayerProfile(event.getPlayer().getUniqueId(), event.getPlayer().getPlayerProfile());
    }
}
