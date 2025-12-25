package com.spygamingog.spectatorplusplus.listeners;

import com.spygamingog.spectatorplusplus.SpectatorPlusPlus;
import com.spygamingog.spectatorplusplus.utils.SpectatorManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryView;

public class InventoryListener implements Listener {
    private final SpectatorPlusPlus plugin;
    private final SpectatorManager spectatorManager;
    
    public InventoryListener(SpectatorPlusPlus plugin) {
        this.plugin = plugin;
        this.spectatorManager = plugin.getSpectatorManager();
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        Player player = (Player) event.getWhoClicked();
        
        if (spectatorManager.isSpectator(player)) {
            // Allow clicking in player selector GUI
            String title = event.getView().getTitle();
            if (title.contains("Spectate Players")) {
                // Handle player selection from GUI
                if (event.getCurrentItem() != null && 
                    event.getCurrentItem().getType().toString().contains("PLAYER_HEAD")) {
                    String playerName = event.getCurrentItem().getItemMeta().getDisplayName()
                        .replace("§a", "").replace("§2", "").replace("§e", "").replace("§6", "");
                    playerName = org.bukkit.ChatColor.stripColor(playerName);
                    
                    org.bukkit.entity.Player target = plugin.getServer().getPlayerExact(playerName);
                    if (target != null && !spectatorManager.isSpectator(target)) {
                        event.setCancelled(true);
                        player.closeInventory();
                        spectatorManager.spectatePlayer(player, target);
                    }
                }
                return;
            }
            
            // Cancel all other inventory interactions for spectators
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        Player player = (Player) event.getWhoClicked();
        
        if (spectatorManager.isSpectator(player)) {
            // Only allow dragging in player selector GUI
            String title = event.getView().getTitle();
            if (!title.contains("Spectate Players")) {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;
        
        Player player = (Player) event.getPlayer();
        
        if (spectatorManager.isSpectator(player)) {
            // If spectator is spectating someone, sync their inventory view
            if (spectatorManager.isSpectating(player)) {
                Player target = spectatorManager.getSpectatingTarget(player);
                if (target != null && target.getOpenInventory() != null) {
                    // Close current and open target's inventory
                    player.closeInventory();
                    player.openInventory(target.getInventory());
                }
            }
        }
    }
    
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;
        
        Player player = (Player) event.getPlayer();
        
        if (spectatorManager.isSpectator(player) && spectatorManager.isSpectating(player)) {
            // If spectator closes inventory while spectating, reopen target's inventory
            // after a short delay to maintain the spectating view
            Player target = spectatorManager.getSpectatingTarget(player);
            if (target != null && target.getOpenInventory() != null) {
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                    if (player.isOnline() && spectatorManager.isSpectating(player)) {
                        player.openInventory(target.getInventory());
                    }
                }, 1L);
            }
        }
    }
}