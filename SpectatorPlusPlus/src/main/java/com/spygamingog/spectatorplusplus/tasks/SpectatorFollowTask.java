package com.spygamingog.spectatorplusplus.tasks;

import com.spygamingog.spectatorplusplus.SpectatorPlusPlus;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class SpectatorFollowTask extends BukkitRunnable {
    private final SpectatorPlusPlus plugin;
    private final Player spectator;
    private final Player target;
    
    public SpectatorFollowTask(SpectatorPlusPlus plugin, Player spectator, Player target) {
        this.plugin = plugin;
        this.spectator = spectator;
        this.target = target;
    }
    
    @Override
    public void run() {
        // Check if still valid
        if (!spectator.isOnline() || !target.isOnline()) {
            this.cancel();
            return;
        }
        
        // Update spectator position to follow target
        Location targetLoc = target.getEyeLocation();
        spectator.teleport(targetLoc);
        
        // Sync inventory view if target opens inventory
        if (target.getOpenInventory() != null) {
            spectator.openInventory(target.getInventory());
        }
        
        // Update health, food, XP
        spectator.setHealth(target.getHealth());
        spectator.setFoodLevel(target.getFoodLevel());
        spectator.setExp(target.getExp());
        spectator.setLevel(target.getLevel());
    }
}