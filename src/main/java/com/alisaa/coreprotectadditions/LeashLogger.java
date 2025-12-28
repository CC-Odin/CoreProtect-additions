package com.alisaa.coreprotectadditions;

import net.coreprotect.CoreProtectAPI;
import org.bukkit.Material;
import org.bukkit.Location;
import org.bukkit.entity.LeashHitch;
import org.bukkit.entity.Player;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityUnleashEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerUnleashEntityEvent;

import io.papermc.paper.entity.Leashable;

public class LeashLogger implements Listener {
    private CoreProtectAPI api;

    public LeashLogger(CoreProtectAPI api) {
        this.api = api;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerUnleashedEntityEvent(EntityUnleashEvent e) {
        Leashable entity = (Leashable) e.getEntity();

        if (e instanceof PlayerUnleashEntityEvent ep) {
            api.logRemoval(ep.getPlayer().getName(), entity.getLocation(), Material.LEAD, null);
            return;
        }

        // If a player was holding the leash, log as player
        Entity leashHolder = entity.getLeashHolder();
        if (leashHolder instanceof Player) {
            api.logRemoval(leashHolder.getName(), entity.getLocation(), Material.LEAD, null);
            return;
        }

        // Check who was riding the entity and log as player
        if (!entity.isEmpty() && entity.getPassengers().getFirst() instanceof Player passenger) {
            api.logRemoval(passenger.getName(), entity.getLocation(), Material.LEAD, null);
            return;
        }

        // ignore this, will be logged on onLeashHitchBreak
        if (e.getReason().equals(EntityUnleashEvent.UnleashReason.HOLDER_GONE) && leashHolder instanceof LeashHitch) {
            return;
        }

        // if all else fails log as entity breaking own leash
        api.logRemoval("#" + entity.getName().toLowerCase(), entity.getLocation(), Material.LEAD, null);
    }

    @EventHandler(ignoreCancelled = true)
    public void onLeashHitchBreak(HangingBreakEvent e) {
        if (!(e.getEntity() instanceof LeashHitch)) {
            return;
        }

        Location pos = e.getEntity().getLocation();

        if (e instanceof HangingBreakByEntityEvent eb) {
            Entity remover = eb.getRemover();
            if (remover instanceof Player) {
                api.logRemoval(remover.getName(), pos, Material.LEAD, null);
                return;
            }
            api.logRemoval("#" + remover.getName().toLowerCase(), pos, Material.LEAD, null);
            return;
        }

        api.logRemoval("#" + e.getCause().toString().toLowerCase(), pos, Material.LEAD, null);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerLeashEntityEvent(PlayerLeashEntityEvent e) {
        api.logPlacement(e.getPlayer().getName(), e.getEntity().getLocation(), Material.LEAD, null);
    }
}
