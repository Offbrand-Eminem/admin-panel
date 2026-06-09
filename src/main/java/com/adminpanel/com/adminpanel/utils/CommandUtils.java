package com.adminpanel.utils;

import com.adminpanel.AdminPanelMod;
import com.adminpanel.permission.PermissionManager;
import java.util.UUID;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class CommandUtils {
    
    public static boolean hasAdminPermission(CommandSourceStack source) {
        if (source.getPlayer() == null) return true; // Console has all permissions
        return AdminPanelMod.permissionManager.hasPermission(
            source.getPlayer(),
            PermissionManager.PermissionLevel.ADMIN
        );
    }
    
    public static boolean hasOwnerPermission(CommandSourceStack source) {
        if (source.getPlayer() == null) return true; // Console has all permissions
        return AdminPanelMod.permissionManager.hasPermission(
            source.getPlayer(),
            PermissionManager.PermissionLevel.OWNER
        );
    }
    
    public static ServerPlayer getPlayer(CommandSourceStack source, String playerName) {
        ServerPlayer target = source.getServer().getPlayerList().getPlayerByName(playerName);
        if (target == null) {
            source.sendFailure(Component.literal("§cPlayer not found: " + playerName));
        }
        return target;
    }
    
    public static boolean isPlayerOnline(CommandSourceStack source, String playerName) {
        return source.getServer().getPlayerList().getPlayerByName(playerName) != null;
    }
    
    public static void sendSuccess(CommandSourceStack source, String message) {
        source.sendSuccess(() -> Component.literal("§a" + message), true);
    }
    
    public static void sendError(CommandSourceStack source, String message) {
        source.sendFailure(Component.literal("§c" + message));
    }
    
    public static void sendWarning(CommandSourceStack source, String message) {
        source.sendSuccess(() -> Component.literal("§e" + message), false);
    }
    
    public static void sendInfo(CommandSourceStack source, String message) {
        source.sendSuccess(() -> Component.literal("§b" + message), false);
    }
    
    public static UUID getPlayerUUID(String playerName, CommandSourceStack source) {
        ServerPlayer player = source.getServer().getPlayerList().getPlayerByName(playerName);
        return player != null ? player.getUUID() : null;
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
