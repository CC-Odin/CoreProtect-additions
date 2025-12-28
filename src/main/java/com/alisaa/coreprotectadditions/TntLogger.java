package com.alisaa.coreprotectadditions;

import net.coreprotect.CoreProtectAPI;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.TNTPrimeEvent;
import org.bukkit.event.block.TNTPrimeEvent.*;
import org.bukkit.event.entity.EntityDamageEvent;

public class TntLogger implements Listener {
    private CoreProtectAPI api;

    public TntLogger(CoreProtectAPI api) {
        this.api = api;
    }

    private boolean logIfPlayer (Object entity, Location location){
        if (entity instanceof Player player){
            api.logInteraction(player.getName(), location);
            return true;
        }
        return false;
    }

    @EventHandler(ignoreCancelled = true)
    public void onIgniteTNT(TNTPrimeEvent e) {
        Location location = e.getBlock().getLocation();
        Entity entity = e.getPrimingEntity();

        if (entity instanceof Player player) {
            api.logInteraction(player.getName(), location);
            return;
        }

        PrimeCause cause = e.getCause();
        switch (cause) {
            case REDSTONE, DISPENSER, FIRE:
                api.logInteraction("#" + cause.toString().toLowerCase(), location);
                return;

            case PROJECTILE:
                if (entity instanceof Projectile projectile) {
                    ProjectileSource shooter = projectile.getShooter();
                    if (logIfPlayer(shooter, location)) {
                        return;
                    }

                    if (shooter instanceof Entity shooterEntity) {
                        api.logInteraction("#" + shooterEntity.getName().toLowerCase(), location);
                        return;
                    }
                    // fallback, in case no shooter exists, simply log the projectile
                    api.logInteraction("#" + entity.getName().toLowerCase(), location);
                }
                return;

            case EXPLOSION:
                if (e.getPrimingBlock() != null) {
                    Material material = e.getPrimingBlock().getType();
                    // most block explosions are just logged as air, since the block
                    // is broken by the explosion, but i'll leave this
                    if (!material.isAir()){
                        api.logInteraction("#" + material.name().toLowerCase(), location);
                    } else {
                        api.logInteraction("#block", location);
                    }
                }
                if (entity == null) {
                    return;
                }
                // if tnt chain, log as tnt cause
                if (entity instanceof TNTPrimed tnt) {
                    Entity igniterEntity = tnt.getSource();
                    if (igniterEntity == null) {
                        return;
                    }
                    if (logIfPlayer(igniterEntity, location)){
                        return;
                    }
                    api.logInteraction("#" + igniterEntity.getName().toLowerCase(), location);
                    return;
                }
                // find last damage if from ender crystal
                if (entity instanceof EnderCrystal enderCrystal){
                    EntityDamageEvent damageEvent = enderCrystal.getLastDamageCause();
                    Entity damager = damageEvent.getDamageSource().getCausingEntity();
                    if (damager == null){
                        return;
                    }
                    if (logIfPlayer(damager, location)){
                        return;
                    }
                    api.logInteraction("#" + damager.getName().toLowerCase(), location);
                    return;
                }
                // Otherisw simply log the entity name
                api.logInteraction("#" + entity.getName().toLowerCase(), location);
                return;
            default:
                break;
        }
    }
}
