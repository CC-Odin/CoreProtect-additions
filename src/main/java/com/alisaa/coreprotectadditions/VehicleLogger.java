package com.alisaa.coreprotectadditions;

import net.coreprotect.CoreProtectAPI;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPlaceEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;

public class VehicleLogger implements Listener {
    private CoreProtectAPI api;
    private static final List<Class<?>> allowedEntities = Arrays.asList(Minecart.class, Boat.class);

    public VehicleLogger(CoreProtectAPI api) {
        this.api = api;
    }

    private boolean validEntity(Entity entity) {
        for (Class<?> class1 : allowedEntities) {
            if (class1.isInstance(entity)) {
                return true;
            }
        }
        return false;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlacement(EntityPlaceEvent e) {
        Player player = e.getPlayer();
        if (player == null || !validEntity(e.getEntity())) {
            return;
        }

        Material item = player.getInventory().getItem(e.getHand()).getType();
        Location location = e.getBlock().getLocation();

        api.logPlacement(player.getName(), location, item, null);
    }

    @EventHandler(ignoreCancelled = true)
    public void onRemoval(VehicleDestroyEvent e) {
        Vehicle entity = e.getVehicle();
        if (!validEntity(entity)) {
            return;
        }
        Entity attacker = e.getAttacker();
        Material item = entity.getPickItemStack().getType();

        if (attacker instanceof Player player) {
            api.logRemoval(player.getName(), entity.getLocation(), item, null);
            return;
        }
        api.logRemoval("#" + entity.getName().toLowerCase(), entity.getLocation(), item, null);
    }

}
