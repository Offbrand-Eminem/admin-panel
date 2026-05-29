package com.adminpanel.utils;

import com.adminpanel.AdminPanelMod;
import com.adminpanel.permission.PermissionManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.UUID;

public class CommandUtils {
    
    public static boolean hasAdminPermission(ServerCommandSource source) {
        if (source.getPlayer() == null) return true; // Console has all permissions
        return AdminPanelMod.permissionManager.hasPermission(
            source.getPlayer(),
            PermissionManager.PermissionLevel.ADMIN
        );
    }
    
    public static boolean hasOwnerPermission(ServerCommandSource source) {
        if (source.getPlayer() == null) return true; // Console has all permissions
        return AdminPanelMod.permissionManager.hasPermission(
            source.getPlayer(),
            PermissionManager.PermissionLevel.OWNER
        );
    }
    
    public static ServerPlayerEntity getPlayer(ServerCommandSource source, String playerName) {
        ServerPlayerEntity target = source.getServer().getPlayerManager().getPlayer(playerName);
        if (target == null) {
            source.sendError(Text.literal("§cPlayer not found: " + playerName));
        }
        return target;
    }
    
    public static boolean isPlayerOnline(ServerCommandSource source, String playerName) {
        return source.getServer().getPlayerManager().getPlayer(playerName) != null;
    }
    
    public static void sendSuccess(ServerCommandSource source, String message) {
        source.sendFeedback(() -> Text.literal("§a" + message), true);
    }
    
    public static void sendError(ServerCommandSource source, String message) {
        source.sendError(Text.literal("§c" + message));
    }
    
    public static void sendWarning(ServerCommandSource source, String message) {
        source.sendFeedback(() -> Text.literal("§e" + message), false);
    }
    
    public static void sendInfo(ServerCommandSource source, String message) {
        source.sendFeedback(() -> Text.literal("§b" + message), false);
    }
    
    public static UUID getPlayerUUID(String playerName, ServerCommandSource source) {
        ServerPlayerEntity player = source.getServer().getPlayerManager().getPlayer(playerName);
        return player != null ? player.getUuid() : null;
    }
    
    public static boolean isValidPlayerName(String playerName) {
        return playerName != null && !playerName.trim().isEmpty() && playerName.length() <= 16;
    }
    
    public static String formatPlayerList(java.util.Collection<String> players) {
        if (players.isEmpty()) return "None";
        return String.join(", ", players);
    }
    
    public static String formatTime(long milliseconds) {
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        
        if (days > 0) {
            return String.format("%dd %dh %dm", days, hours % 24, minutes % 60);
        } else if (hours > 0) {
            return String.format("%dh %dm", hours, minutes % 60);
        } else if (minutes > 0) {
            return String.format("%dm %ds", minutes, seconds % 60);
        } else {
            return String.format("%ds", seconds);
        }
    }
}
