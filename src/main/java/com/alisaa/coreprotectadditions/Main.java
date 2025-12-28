package com.alisaa.coreprotectadditions;

import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    @Override
    public void onEnable() {
        saveDefaultConfig();
        Plugin depend = Bukkit.getPluginManager().getPlugin("CoreProtect");
        if (depend == null) {
            getLogger().warning("CoreProtect was not found, disabling plugin");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        CoreProtectAPI api = ((CoreProtect) depend).getAPI();

        Bukkit.getPluginManager().registerEvents(new CreeperLogger(api), this);
        Bukkit.getPluginManager().registerEvents(new LeashLogger(api), this);
        Bukkit.getPluginManager().registerEvents(new BedLogger(api), this);
        Bukkit.getPluginManager().registerEvents(new TntLogger(api), this);
        Bukkit.getPluginManager().registerEvents(new VehicleLogger(api), this);
    }
}
