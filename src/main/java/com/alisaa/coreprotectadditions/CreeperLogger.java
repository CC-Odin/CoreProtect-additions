package com.alisaa.coreprotectadditions;

import net.coreprotect.CoreProtectAPI;
import org.bukkit.Material;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LargeFireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.projectiles.ProjectileSource;

public class CreeperLogger implements Listener {
    private CoreProtectAPI api;

    public CreeperLogger(CoreProtectAPI api) {
        this.api = api;
    }

    @EventHandler(ignoreCancelled = true)
    public void onExplosionPrime(ExplosionPrimeEvent e) {
        Entity entity = e.getEntity();
        if (entity instanceof Creeper creeper){
            if (creeper.getIgniter() instanceof Player igniter){
                api.logRemoval(igniter.getName(), creeper.getLocation(), Material.CREEPER_SPAWN_EGG, null);
                return;
            }
            LivingEntity target = creeper.getTarget();
            if (target instanceof Player player){
                api.logRemoval(player.getName(), creeper.getLocation(), Material.CREEPER_SPAWN_EGG, null);
                return;
            }
            if (target != null){
                api.logRemoval("#"+ target.getName().toLowerCase(), creeper.getLocation(), Material.CREEPER_SPAWN_EGG, null);
            }
        }
        if (entity instanceof LargeFireball fireball){
            ProjectileSource shooter = fireball.getShooter();
            if (shooter instanceof Mob mob){
                LivingEntity target = mob.getTarget();
                if (target instanceof Player player){
                    api.logRemoval(player.getName(), fireball.getLocation(), Material.FIRE_CHARGE, null);
                    return;
                }
                if (target != null){
                    api.logRemoval("#"+target.getName().toLowerCase(), fireball.getLocation(), Material.FIRE_CHARGE, null);
                }
                
            }
        }
    }

}
