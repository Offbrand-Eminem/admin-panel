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

public class GodModeCommand {
    private static final Map<UUID, Boolean> godModePlayers = new HashMap<>();
    
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("god")
            .requires(source -> CommandUtils.hasAdminPermission(source))
            .executes(GodModeCommand::toggleGodModeSelf)
            .then(argument("player", StringArgumentType.word())
                .executes(GodModeCommand::toggleGodModeOther)
            )
        );
        
        dispatcher.register(literal("godon")
            .requires(source -> CommandUtils.hasAdminPermission(source))
            .executes(GodModeCommand::enableGodModeSelf)
            .then(argument("player", StringArgumentType.word())
                .executes(GodModeCommand::enableGodModeOther)
            )
        );
        
        dispatcher.register(literal("godoff")
            .requires(source -> CommandUtils.hasAdminPermission(source))
            .executes(GodModeCommand::disableGodModeSelf)
            .then(argument("player", StringArgumentType.word())
                .executes(GodModeCommand::disableGodModeOther)
            )
        );
    }
    
    private static int toggleGodModeSelf(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        
        if (source.getPlayer() == null) {
            CommandUtils.sendError(source, "This command can only be used by players");
            return 0;
        }
        
        return toggleGodMode(context, source.getPlayer());
    }
    
    private static int toggleGodModeOther(CommandContext<ServerCommandSource> context) {
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
        
        return toggleGodMode(context, target);
    }
    
    private static int toggleGodMode(CommandContext<ServerCommandSource> context, ServerPlayerEntity player) {
        UUID uuid = player.getUuid();
        boolean isGodMode = godModePlayers.getOrDefault(uuid, false);
        boolean newGodModeState = !isGodMode;
        
        setGodModeState(player, newGodModeState);
        
        String status = newGodModeState ? "enabled" : "disabled";
        CommandUtils.sendSuccess(context.getSource(), "God mode " + status + " for " + player.getName().getString());
        
        if (context.getSource().getPlayer() != null && !context.getSource().getPlayer().equals(player)) {
            player.sendMessage(Text.literal("§aGod mode " + status + " by " + context.getSource().getPlayer().getName().getString()));
        }
        
        return 1;
    }
    
    private static int enableGodModeSelf(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        if (source.getPlayer() == null) {
            CommandUtils.sendError(source, "This command can only be used by players");
            return 0;
        }
        setGodModeState(source.getPlayer(), true);
        CommandUtils.sendSuccess(source, "God mode enabled");
        return 1;
    }
    
    private static int enableGodModeOther(CommandContext<ServerCommandSource> context) {
        String playerName = StringArgumentType.getString(context, "player");
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity target = CommandUtils.getPlayer(source, playerName);
        if (target == null) {
            CommandUtils.sendError(source, "Player not found: " + playerName);
            return 0;
        }
        setGodModeState(target, true);
        CommandUtils.sendSuccess(source, "God mode enabled for " + playerName);
        return 1;
    }
    
    private static int disableGodModeSelf(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        if (source.getPlayer() == null) {
            CommandUtils.sendError(source, "This command can only be used by players");
            return 0;
        }
        setGodModeState(source.getPlayer(), false);
        CommandUtils.sendSuccess(source, "God mode disabled");
        return 1;
    }
    
    private static int disableGodModeOther(CommandContext<ServerCommandSource> context) {
        String playerName = StringArgumentType.getString(context, "player");
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity target = CommandUtils.getPlayer(source, playerName);
        if (target == null) {
            CommandUtils.sendError(source, "Player not found: " + playerName);
            return 0;
        }
        setGodModeState(target, false);
        CommandUtils.sendSuccess(source, "God mode disabled for " + playerName);
        return 1;
    }
    
    private static void setGodModeState(ServerPlayerEntity player, boolean enabled) {
        UUID uuid = player.getUuid();
        godModePlayers.put(uuid, enabled);
        player.getAbilities().invulnerable = enabled;
        player.sendAbilitiesUpdate();
    }
    
    public static boolean isGodMode(UUID playerUuid) {
        return godModePlayers.getOrDefault(playerUuid, false);
    }
    
    public static Map<UUID, Boolean> getAllGodModePlayers() {
        return new HashMap<>(godModePlayers);
    }
}
