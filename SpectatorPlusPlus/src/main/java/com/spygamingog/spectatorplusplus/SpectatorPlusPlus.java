package com.spygamingog.spectatorplusplus;

import com.spygamingog.spectatorplusplus.commands.CommandManager;
import com.spygamingog.spectatorplusplus.data.ConfigManager;
import com.spygamingog.spectatorplusplus.data.WorldSetManager;
import com.spygamingog.spectatorplusplus.listeners.*;
import com.spygamingog.spectatorplusplus.utils.SpectatorManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class SpectatorPlusPlus extends JavaPlugin {
    private static SpectatorPlusPlus instance;
    private ConfigManager configManager;
    private WorldSetManager worldSetManager;
    private SpectatorManager spectatorManager;
    private CommandManager commandManager;
    
    @Override
    public void onEnable() {
        instance = this;
        
        // Save default config if it doesn't exist
        saveDefaultConfig();
        
        // Initialize managers in correct order
        configManager = new ConfigManager(this);
        
        // Load configuration FIRST
        configManager.loadConfig();
        
        // Initialize WorldSetManager AFTER config
        worldSetManager = new WorldSetManager(this);
        worldSetManager.loadWorldSets();
        
        // Initialize SpectatorManager AFTER config and world sets
        spectatorManager = new SpectatorManager(this);
        
        // Initialize CommandManager
        commandManager = new CommandManager(this);
        
        // Register commands
        commandManager.registerCommands();
        
        // Register listeners
        registerListeners();
        
        getLogger().info("Spectator++ v" + getDescription().getVersion() + " has been enabled!");
        getLogger().info("Plugin by " + getDescription().getAuthors().toString());
    }
    
    @Override
    public void onDisable() {
        // Save any pending data
        if (spectatorManager != null) {
            spectatorManager.disableAllSpectators();
        }
        
        // Save config
        saveConfig();
        
        getLogger().info("Spectator++ has been disabled!");
    }
    
    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);
        Bukkit.getPluginManager().registerEvents(new InventoryListener(this), this);
        Bukkit.getPluginManager().registerEvents(new ChatListener(this), this);
        Bukkit.getPluginManager().registerEvents(new WorldListener(this), this);
        Bukkit.getPluginManager().registerEvents(new EntityListener(this), this);
        Bukkit.getPluginManager().registerEvents(new AdvancementListener(this), this);
    }
    
    public static SpectatorPlusPlus getInstance() {
        return instance;
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    public WorldSetManager getWorldSetManager() {
        return worldSetManager;
    }
    
    public SpectatorManager getSpectatorManager() {
        return spectatorManager;
    }
    
    public CommandManager getCommandManager() {
        return commandManager;
    }
}