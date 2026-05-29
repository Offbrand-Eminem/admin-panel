package com.adminpanel.commands;

import com.adminpanel.AdminPanelMod;
import com.adminpanel.permission.PermissionManager;
import com.adminpanel.utils.CommandUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class SpeedCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("speed")
            .requires(source -> CommandUtils.hasAdminPermission(source))
            .then(argument("type", StringArgumentType.word())
                .then(argument("value", FloatArgumentType.floatArg(0.1f, 10.0f))
                    .executes(SpeedCommand::setSpeed)
                )
            )
        );
        
        dispatcher.register(literal("speed")
            .requires(source -> CommandUtils.hasAdminPermission(source))
            .then(argument("type", StringArgumentType.word())
                .then(argument("value", FloatArgumentType.floatArg(0.1f, 10.0f))
                    .then(argument("player", StringArgumentType.word())
                        .executes(SpeedCommand::setSpeedOther)
                    )
                )
            )
        );
        
        dispatcher.register(literal("resetspeed")
            .requires(source -> CommandUtils.hasAdminPermission(source))
            .executes(SpeedCommand::resetSpeedSelf)
            .then(argument("player", StringArgumentType.word())
                .executes(SpeedCommand::resetSpeedOther)
            )
        );
    }
    
    private static int setSpeed(CommandContext<ServerCommandSource> context) {
        String type = StringArgumentType.getString(context, "type");
        float value = FloatArgumentType.getFloat(context, "value");
        ServerCommandSource source = context.getSource();
        
        if (source.getPlayer() == null) {
            CommandUtils.sendError(source, "This command can only be used by players");
            return 0;
        }
        
        return setSpeedForPlayer(context, source.getPlayer(), type, value);
    }
    
    private static int setSpeedOther(CommandContext<ServerCommandSource> context) {
        String type = StringArgumentType.getString(context, "type");
        float value = FloatArgumentType.getFloat(context, "value");
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
        
        return setSpeedForPlayer(context, target, type, value);
    }
    
    private static int setSpeedForPlayer(CommandContext<ServerCommandSource> context, ServerPlayerEntity player, String type, float value) {
        switch (type.toLowerCase()) {
            case "walk":
            case "w":
                player.getAbilities().setWalkSpeed(value / 10.0f);
                player.sendAbilitiesUpdate();
                CommandUtils.sendSuccess(context.getSource(), "Set " + player.getName().getString() + "'s walk speed to " + value);
                return 1;
            case "fly":
            case "f":
                player.getAbilities().setFlySpeed(value / 10.0f);
                player.sendAbilitiesUpdate();
                CommandUtils.sendSuccess(context.getSource(), "Set " + player.getName().getString() + "'s fly speed to " + value);
                return 1;
            default:
                CommandUtils.sendError(context.getSource(), "Invalid type. Use: walk(w) or fly(f)");
                return 0;
        }
    }
    
    private static int resetSpeedSelf(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        if (source.getPlayer() == null) {
            CommandUtils.sendError(source, "This command can only be used by players");
            return 0;
        }
        return resetSpeed(context, source.getPlayer());
    }
    
    private static int resetSpeedOther(CommandContext<ServerCommandSource> context) {
        String playerName = StringArgumentType.getString(context, "player");
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity target = CommandUtils.getPlayer(source, playerName);
        if (target == null) {
            CommandUtils.sendError(source, "Player not found: " + playerName);
            return 0;
        }
        return resetSpeed(context, target);
    }
    
    private static int resetSpeed(CommandContext<ServerCommandSource> context, ServerPlayerEntity player) {
        player.getAbilities().setWalkSpeed(0.1f);
        player.getAbilities().setFlySpeed(0.05f);
        player.sendAbilitiesUpdate();
        CommandUtils.sendSuccess(context.getSource(), "Reset " + player.getName().getString() + "'s speeds to default");
        return 1;
    }
}
