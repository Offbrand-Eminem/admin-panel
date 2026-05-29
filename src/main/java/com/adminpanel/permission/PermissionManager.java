package com.adminpanel.permission;

import com.adminpanel.config.ConfigManager;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PermissionManager {
    private final ConfigManager configManager;
    private final Map<UUID, PermissionLevel> temporaryPermissions = new HashMap<>();
    
    public PermissionManager(ConfigManager configManager) {
        this.configManager = configManager;
    }
    
    public boolean isOwner(ServerPlayerEntity player) {
        if (player == null) return false;
        return configManager.isOwner(player.getName().getString());
    }
    
    public boolean isAdmin(ServerPlayerEntity player) {
        if (player == null) return false;
        return configManager.isAdmin(player.getName().getString());
    }
    
    public boolean hasPermission(ServerPlayerEntity player, PermissionLevel level) {
        if (player == null) return false;
        
        // Check server OP status
        // if (player.hasPermissionLevel(4)) {
        //     return true;
        // }
        
        // Check temporary permissions
        UUID uuid = player.getUuid();
        if (temporaryPermissions.containsKey(uuid)) {
            PermissionLevel tempLevel = temporaryPermissions.get(uuid);
            if (getPermissionLevelValue(tempLevel) >= getPermissionLevelValue(level)) {
                return true;
            }
        }
        
        // Check config-based permissions
        String playerName = player.getName().getString();
        
        switch (level) {
            case OWNER:
                return configManager.isOwner(playerName);
            case ADMIN:
                return configManager.isOwner(playerName) || configManager.isAdmin(playerName);
            case MODERATOR:
                return configManager.isOwner(playerName) || configManager.isAdmin(playerName);
            case VIP:
                return configManager.isWhitelisted(uuid);
            case PLAYER:
                return true;
            default:
                return false;
        }
    }
    
    public boolean hasPermissionByName(String playerName, PermissionLevel level) {
        if (playerName == null) return false;
        
        switch (level) {
            case OWNER:
                return configManager.isOwner(playerName);
            case ADMIN:
                return configManager.isOwner(playerName) || configManager.isAdmin(playerName);
            case MODERATOR:
                return configManager.isOwner(playerName) || configManager.isAdmin(playerName);
            case PLAYER:
                return true;
            default:
                return false;
        }
    }
    
    public void setTemporaryPermission(UUID uuid, PermissionLevel level) {
        temporaryPermissions.put(uuid, level);
    }
    
    public void removeTemporaryPermission(UUID uuid) {
        temporaryPermissions.remove(uuid);
    }
    
    public PermissionLevel getTemporaryPermission(UUID uuid) {
        return temporaryPermissions.get(uuid);
    }
    
    public void clearTemporaryPermissions() {
        temporaryPermissions.clear();
    }
    
    private int getPermissionLevelValue(PermissionLevel level) {
        switch (level) {
            case OWNER: return 4;
            case ADMIN: return 3;
            case MODERATOR: return 2;
            case VIP: return 1;
            case PLAYER: return 0;
            default: return 0;
        }
    }
    
    public PermissionLevel getPlayerPermissionLevel(ServerPlayerEntity player) {
        if (player == null) return PermissionLevel.PLAYER;
        
        // if (player.hasPermissionLevel(4)) return PermissionLevel.OWNER;
        if (isOwner(player)) return PermissionLevel.OWNER;
        if (isAdmin(player)) return PermissionLevel.ADMIN;
        
        UUID uuid = player.getUuid();
        if (temporaryPermissions.containsKey(uuid)) {
            return temporaryPermissions.get(uuid);
        }
        
        if (configManager.isWhitelisted(uuid)) return PermissionLevel.VIP;
        
        return PermissionLevel.PLAYER;
    }
    
    public enum PermissionLevel {
        OWNER,
        ADMIN,
        MODERATOR,
        VIP,
        PLAYER
    }
}
