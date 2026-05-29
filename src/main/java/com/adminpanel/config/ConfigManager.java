package com.adminpanel.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ConfigManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("AdminPanel/Config");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    
    private final MinecraftServer server;
    private final File configFile;
    private final File backupFile;
    
    private Set<String> owners = new HashSet<>();
    private Set<String> admins = new HashSet<>();
    private Set<UUID> bannedPlayers = new HashSet<>();
    private Set<UUID> whitelistedPlayers = new HashSet<>();
    
    private boolean maintenanceMode = false;
    private String maintenanceMessage = "Server is under maintenance. Please try again later.";
    private boolean setupCompleted = false;
    
    public ConfigManager(MinecraftServer server) {
        this.server = server;
        Path configPath = server.getRunDirectory().resolve("config");
        File configDir = configPath.toFile();
        
        if (!configDir.exists()) {
            configDir.mkdirs();
        }
        
        this.configFile = configPath.resolve("admin-panel.json").toFile();
        this.backupFile = configPath.resolve("admin-panel-backup.json").toFile();
    }
    
    public void loadConfig() {
        if (!configFile.exists()) {
            createDefaultConfig();
            return;
        }
        
        // Create backup before loading
        createBackup();
        
        try (FileReader reader = new FileReader(configFile)) {
            JsonObject json = GSON.fromJson(reader, JsonObject.class);
            
            if (json.has("owners")) {
                List<String> ownerList = GSON.fromJson(json.getAsJsonArray("owners"), List.class);
                owners = new HashSet<>(ownerList != null ? ownerList : new ArrayList<>());
            }
            
            if (json.has("admins")) {
                List<String> adminList = GSON.fromJson(json.getAsJsonArray("admins"), List.class);
                admins = new HashSet<>(adminList != null ? adminList : new ArrayList<>());
            }
            
            if (json.has("maintenance_mode")) {
                maintenanceMode = json.get("maintenance_mode").getAsBoolean();
            }
            
            if (json.has("maintenance_message")) {
                maintenanceMessage = json.get("maintenance_message").getAsString();
            }
            
            if (json.has("setup_completed")) {
                setupCompleted = json.get("setup_completed").getAsBoolean();
            }
            
            LOGGER.info("Configuration loaded: {} owners, {} admins, maintenance: {}, setup: {}", 
                owners.size(), admins.size(), maintenanceMode, setupCompleted);
        } catch (Exception e) {
            LOGGER.error("Failed to load configuration, attempting to restore from backup", e);
            restoreFromBackup();
        }
    }
    
    public void saveConfig() {
        try (FileWriter writer = new FileWriter(configFile)) {
            JsonObject json = new JsonObject();
            json.add("owners", GSON.toJsonTree(new ArrayList<>(owners)));
            json.add("admins", GSON.toJsonTree(new ArrayList<>(admins)));
            json.addProperty("maintenance_mode", maintenanceMode);
            json.addProperty("maintenance_message", maintenanceMessage);
            json.addProperty("setup_completed", setupCompleted);
            
            GSON.toJson(json, writer);
            LOGGER.info("Configuration saved successfully");
        } catch (IOException e) {
            LOGGER.error("Failed to save configuration", e);
        }
    }
    
    private void createBackup() {
        try {
            if (configFile.exists()) {
                Files.copy(configFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                LOGGER.debug("Configuration backup created");
            }
        } catch (IOException e) {
            LOGGER.warn("Failed to create configuration backup", e);
        }
    }
    
    private void restoreFromBackup() {
        try {
            if (backupFile.exists()) {
                Files.copy(backupFile.toPath(), configFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                LOGGER.info("Configuration restored from backup");
                loadConfig();
            } else {
                LOGGER.warn("No backup available, creating default configuration");
                createDefaultConfig();
            }
        } catch (IOException e) {
            LOGGER.error("Failed to restore from backup, creating default configuration", e);
            createDefaultConfig();
        }
    }
    
    private void createDefaultConfig() {
        owners = new HashSet<>();
        admins = new HashSet<>();
        maintenanceMode = false;
        maintenanceMessage = "Server is under maintenance. Please try again later.";
        setupCompleted = false;
        
        saveConfig();
        LOGGER.info("Created default configuration file");
    }
    
    public void reloadConfig() {
        LOGGER.info("Reloading configuration...");
        loadConfig();
    }
    
    public Set<String> getOwners() {
        return new HashSet<>(owners);
    }
    
    public Set<String> getAdmins() {
        return new HashSet<>(admins);
    }
    
    public void addOwner(String playerName) {
        if (playerName != null && !playerName.trim().isEmpty() && !owners.contains(playerName)) {
            owners.add(playerName.trim());
            saveConfig();
            LOGGER.info("Added owner: {}", playerName);
        }
    }
    
    public void removeOwner(String playerName) {
        if (owners.remove(playerName)) {
            saveConfig();
            LOGGER.info("Removed owner: {}", playerName);
        }
    }
    
    public void addAdmin(String playerName) {
        if (playerName != null && !playerName.trim().isEmpty() && !admins.contains(playerName) && !owners.contains(playerName)) {
            admins.add(playerName.trim());
            saveConfig();
            LOGGER.info("Added admin: {}", playerName);
        }
    }
    
    public void removeAdmin(String playerName) {
        if (admins.remove(playerName)) {
            saveConfig();
            LOGGER.info("Removed admin: {}", playerName);
        }
    }
    
    public boolean isOwner(String playerName) {
        return playerName != null && owners.contains(playerName);
    }
    
    public boolean isAdmin(String playerName) {
        return playerName != null && (admins.contains(playerName) || owners.contains(playerName));
    }
    
    public boolean isMaintenanceMode() {
        return maintenanceMode;
    }
    
    public void setMaintenanceMode(boolean enabled) {
        this.maintenanceMode = enabled;
        saveConfig();
        LOGGER.info("Maintenance mode: {}", enabled);
    }
    
    public String getMaintenanceMessage() {
        return maintenanceMessage;
    }
    
    public void setMaintenanceMessage(String message) {
        this.maintenanceMessage = message;
        saveConfig();
    }
    
    public void addBannedPlayer(UUID uuid) {
        bannedPlayers.add(uuid);
    }
    
    public void removeBannedPlayer(UUID uuid) {
        bannedPlayers.remove(uuid);
    }
    
    public boolean isBanned(UUID uuid) {
        return bannedPlayers.contains(uuid);
    }
    
    public void addWhitelistedPlayer(UUID uuid) {
        whitelistedPlayers.add(uuid);
    }
    
    public void removeWhitelistedPlayer(UUID uuid) {
        whitelistedPlayers.remove(uuid);
    }
    
    public boolean isWhitelisted(UUID uuid) {
        return whitelistedPlayers.contains(uuid);
    }
    
    public boolean isWhitelistEnabled() {
        return !whitelistedPlayers.isEmpty();
    }
    
    public boolean isSetupCompleted() {
        return setupCompleted;
    }
    
    public void setSetupCompleted(boolean completed) {
        this.setupCompleted = completed;
        saveConfig();
        LOGGER.info("Setup completed: {}", completed);
    }
}
