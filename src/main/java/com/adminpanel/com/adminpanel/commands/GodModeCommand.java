package com.adminpanel.commands;

import com.adminpanel.AdminPanelMod;
import com.adminpanel.permission.PermissionManager;
import com.adminpanel.utils.CommandUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class GodModeCommand {
    private static final Map<UUID, Boolean> godModePlayers = new HashMap<>();
    
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
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
    
    private static int toggleGodModeSelf(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        
        if (source.getPlayer() == null) {
            CommandUtils.sendError(source, "This command can only be used by players");
            return 0;
        }
        
        return toggleGodMode(context, source.getPlayer());
    }
    
    private static int toggleGodModeOther(CommandContext<CommandSourceStack> context) {
        String playerName = StringArgumentType.getString(context, "player");
        CommandSourceStack source = context.getSource();
        
        if (!CommandUtils.isValidPlayerName(playerName)) {
            CommandUtils.sendError(source, "Invalid player name: " + playerName);
            return 0;
        }
        
        ServerPlayer target = CommandUtils.getPlayer(source, playerName);
        
        if (target == null) {
            CommandUtils.sendError(source, "Player not found: " + playerName);
            return 0;
        }
        
        return toggleGodMode(context, target);
    }
    
    private static int toggleGodMode(CommandContext<CommandSourceStack> context, ServerPlayer player) {
        UUID uuid = player.getUUID();
        boolean isGodMode = godModePlayers.getOrDefault(uuid, false);
        boolean newGodModeState = !isGodMode;
        
        setGodModeState(player, newGodModeState);
        
        String status = newGodModeState ? "enabled" : "disabled";
        CommandUtils.sendSuccess(context.getSource(), "God mode " + status + " for " + player.getName().getString());
        
        if (context.getSource().getPlayer() != null && !context.getSource().getPlayer().equals(player)) {
            player.sendSystemMessage(Component.literal("§aGod mode " + status + " by " + context.getSource().getPlayer().getName().getString()));
        }
        
        return 1;
    }
    
    private static int enableGodModeSelf(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        if (source.getPlayer() == null) {
            CommandUtils.sendError(source, "This command can only be used by players");
            return 0;
        }
        setGodModeState(source.getPlayer(), true);
        CommandUtils.sendSuccess(source, "God mode enabled");
        return 1;
    }
    
    private static int enableGodModeOther(CommandContext<CommandSourceStack> context) {
        String playerName = StringArgumentType.getString(context, "player");
        CommandSourceStack source = context.getSource();
        ServerPlayer target = CommandUtils.getPlayer(source, playerName);
        if (target == null) {
            CommandUtils.sendError(source, "Player not found: " + playerName);
            return 0;
        }
        setGodModeState(target, true);
        CommandUtils.sendSuccess(source, "God mode enabled for " + playerName);
        return 1;
    }
    
    private static int disableGodModeSelf(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        if (source.getPlayer() == null) {
            CommandUtils.sendError(source, "This command can only be used by players");
            return 0;
        }
        setGodModeState(source.getPlayer(), false);
        CommandUtils.sendSuccess(source, "God mode disabled");
        return 1;
    }
    
    private static int disableGodModeOther(CommandContext<CommandSourceStack> context) {
        String playerName = StringArgumentType.getString(context, "player");
        CommandSourceStack source = context.getSource();
        ServerPlayer target = CommandUtils.getPlayer(source, playerName);
        if (target == null) {
            CommandUtils.sendError(source, "Player not found: " + playerName);
            return 0;
        }
        setGodModeState(target, false);
        CommandUtils.sendSuccess(source, "God mode disabled for " + playerName);
        return 1;
    }
    
    private static void setGodModeState(ServerPlayer player, boolean enabled) {
        UUID uuid = player.getUUID();
        godModePlayers.put(uuid, enabled);
        player.getAbilities().invulnerable = enabled;
        player.onUpdateAbilities();
    }
    
    public static boolean isGodMode(UUID playerUuid) {
        return godModePlayers.getOrDefault(playerUuid, false);
    }
    
    public static Map<UUID, Boolean> getAllGodModePlayers() {
        return new HashMap<>(godModePlayers);
    }
}
