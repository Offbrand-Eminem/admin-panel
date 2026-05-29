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

public class FlyCommand {
    private static final Map<UUID, Boolean> flyingPlayers = new HashMap<>();
    
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("fly")
            .requires(source -> CommandUtils.hasAdminPermission(source))
            .executes(FlyCommand::toggleFlySelf)
            .then(argument("player", StringArgumentType.word())
                .executes(FlyCommand::toggleFlyOther)
            )
        );
        
        dispatcher.register(literal("flyon")
            .requires(source -> CommandUtils.hasAdminPermission(source))
            .executes(FlyCommand::enableFlySelf)
            .then(argument("player", StringArgumentType.word())
                .executes(FlyCommand::enableFlyOther)
            )
        );
        
        dispatcher.register(literal("flyoff")
            .requires(source -> CommandUtils.hasAdminPermission(source))
            .executes(FlyCommand::disableFlySelf)
            .then(argument("player", StringArgumentType.word())
                .executes(FlyCommand::disableFlyOther)
            )
        );
    }
    
    private static int toggleFlySelf(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        
        if (source.getPlayer() == null) {
            CommandUtils.sendError(source, "This command can only be used by players");
            return 0;
        }
        
        return toggleFly(context, source.getPlayer());
    }
    
    private static int toggleFlyOther(CommandContext<ServerCommandSource> context) {
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
        
        return toggleFly(context, target);
    }
    
    private static int toggleFly(CommandContext<ServerCommandSource> context, ServerPlayerEntity player) {
        UUID uuid = player.getUuid();
        boolean isFlying = flyingPlayers.getOrDefault(uuid, false);
        boolean newFlyingState = !isFlying;
        
        setFlyState(player, newFlyingState);
        
        String status = newFlyingState ? "enabled" : "disabled";
        CommandUtils.sendSuccess(context.getSource(), "Flight " + status + " for " + player.getName().getString());
        
        if (context.getSource().getPlayer() != null && !context.getSource().getPlayer().equals(player)) {
            player.sendMessage(Text.literal("§aFlight " + status + " by " + context.getSource().getPlayer().getName().getString()));
        }
        
        return 1;
    }
    
    private static int enableFlySelf(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        if (source.getPlayer() == null) {
            CommandUtils.sendError(source, "This command can only be used by players");
            return 0;
        }
        setFlyState(source.getPlayer(), true);
        CommandUtils.sendSuccess(source, "Flight enabled");
        return 1;
    }
    
    private static int enableFlyOther(CommandContext<ServerCommandSource> context) {
        String playerName = StringArgumentType.getString(context, "player");
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity target = CommandUtils.getPlayer(source, playerName);
        if (target == null) {
            CommandUtils.sendError(source, "Player not found: " + playerName);
            return 0;
        }
        setFlyState(target, true);
        CommandUtils.sendSuccess(source, "Flight enabled for " + playerName);
        return 1;
    }
    
    private static int disableFlySelf(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        if (source.getPlayer() == null) {
            CommandUtils.sendError(source, "This command can only be used by players");
            return 0;
        }
        setFlyState(source.getPlayer(), false);
        CommandUtils.sendSuccess(source, "Flight disabled");
        return 1;
    }
    
    private static int disableFlyOther(CommandContext<ServerCommandSource> context) {
        String playerName = StringArgumentType.getString(context, "player");
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity target = CommandUtils.getPlayer(source, playerName);
        if (target == null) {
            CommandUtils.sendError(source, "Player not found: " + playerName);
            return 0;
        }
        setFlyState(target, false);
        CommandUtils.sendSuccess(source, "Flight disabled for " + playerName);
        return 1;
    }
    
    private static void setFlyState(ServerPlayerEntity player, boolean enabled) {
        UUID uuid = player.getUuid();
        flyingPlayers.put(uuid, enabled);
        player.getAbilities().allowFlying = enabled;
        player.getAbilities().flying = enabled;
        player.sendAbilitiesUpdate();
    }
    
    public static boolean canFly(UUID playerUuid) {
        return flyingPlayers.getOrDefault(playerUuid, false);
    }
    
    public static Map<UUID, Boolean> getAllFlyingPlayers() {
        return new HashMap<>(flyingPlayers);
    }
}
