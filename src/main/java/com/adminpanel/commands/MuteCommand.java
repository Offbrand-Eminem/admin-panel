package com.adminpanel.commands;

import com.adminpanel.AdminPanelMod;
import com.adminpanel.permission.PermissionManager;
import com.adminpanel.utils.CommandUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class MuteCommand {
    private static final Map<UUID, Long> mutedPlayers = new HashMap<>();
    private static final Map<UUID, String> muteReasons = new HashMap<>();
    
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("mute")
            .requires(source -> CommandUtils.hasAdminPermission(source))
            .then(argument("player", StringArgumentType.word())
                .executes(MuteCommand::mutePlayer)
                .then(argument("duration", StringArgumentType.word())
                    .executes(MuteCommand::mutePlayerWithDuration)
                    .then(argument("reason", StringArgumentType.greedyString())
                        .executes(MuteCommand::mutePlayerWithReason)
                    )
                )
            )
        );
    }
    
    private static int mutePlayer(CommandContext<ServerCommandSource> context) {
        return mutePlayerWithDuration(context, "permanent", "Muted by admin");
    }
    
    private static int mutePlayerWithDuration(CommandContext<ServerCommandSource> context) {
        String duration = StringArgumentType.getString(context, "duration");
        return mutePlayerWithDuration(context, duration, "Muted by admin");
    }
    
    private static int mutePlayerWithReason(CommandContext<ServerCommandSource> context) {
        String duration = StringArgumentType.getString(context, "duration");
        String reason = StringArgumentType.getString(context, "reason");
        return mutePlayerWithDuration(context, duration, reason);
    }
    
    private static int mutePlayerWithDuration(CommandContext<ServerCommandSource> context, String duration, String reason) {
        String playerName = StringArgumentType.getString(context, "player");
        ServerCommandSource source = context.getSource();
        
        if (!CommandUtils.isValidPlayerName(playerName)) {
            CommandUtils.sendError(source, "Invalid player name: " + playerName);
            return 0;
        }
        
        ServerPlayerEntity target = CommandUtils.getPlayer(source, playerName);
        
        if (target == null) {
            CommandUtils.sendError(source, "Player not found: " + playerName);
            return 0;
        }
        
        long muteEndTime;
        String durationDisplay;
        
        if (duration.equalsIgnoreCase("permanent") || duration.equalsIgnoreCase("perm")) {
            muteEndTime = Long.MAX_VALUE;
            durationDisplay = "permanent";
        } else {
            try {
                int minutes = Integer.parseInt(duration);
                muteEndTime = System.currentTimeMillis() + (minutes * 60 * 1000L);
                durationDisplay = minutes + " minutes";
            } catch (NumberFormatException e) {
                CommandUtils.sendError(source, "Invalid duration. Use a number (minutes), 'permanent', or 'perm'");
                return 0;
            }
        }
        
        mutedPlayers.put(target.getUuid(), muteEndTime);
        muteReasons.put(target.getUuid(), reason);
        
        CommandUtils.sendSuccess(source, "Muted player: " + playerName + " for " + durationDisplay + " - Reason: " + reason);
        target.sendMessage(Text.literal("§cYou have been muted for " + durationDisplay + ". Reason: " + reason));
        
        return 1;
    }
    
    public static boolean isMuted(UUID playerUuid) {
        Long muteEndTime = mutedPlayers.get(playerUuid);
        if (muteEndTime == null) return false;
        
        if (System.currentTimeMillis() > muteEndTime) {
            mutedPlayers.remove(playerUuid);
            muteReasons.remove(playerUuid);
            return false;
        }
        
        return true;
    }
    
    public static String getMuteReason(UUID playerUuid) {
        return muteReasons.get(playerUuid);
    }
    
    public static long getMuteEndTime(UUID playerUuid) {
        return mutedPlayers.getOrDefault(playerUuid, 0L);
    }
    
    public static void unmutePlayer(UUID playerUuid) {
        mutedPlayers.remove(playerUuid);
        muteReasons.remove(playerUuid);
    }
    
    public static Map<UUID, Long> getAllMutedPlayers() {
        return new HashMap<>(mutedPlayers);
    }
}
