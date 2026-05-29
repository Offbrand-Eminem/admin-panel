package com.adminpanel.commands;

import com.adminpanel.AdminPanelMod;
import com.adminpanel.permission.PermissionManager;
import com.adminpanel.utils.CommandUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class TimeCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("time")
            .requires(source -> CommandUtils.hasAdminPermission(source))
            .then(argument("time", StringArgumentType.word())
                .executes(TimeCommand::setTime)
            )
        );
        
        dispatcher.register(literal("time")
            .requires(source -> CommandUtils.hasAdminPermission(source))
            .then(argument("action", StringArgumentType.word())
                .then(argument("amount", IntegerArgumentType.integer(1))
                    .executes(TimeCommand::modifyTime)
                )
            )
        );
        
        dispatcher.register(literal("day")
            .requires(source -> CommandUtils.hasAdminPermission(source))
            .executes(TimeCommand::setDay)
        );
        
        dispatcher.register(literal("night")
            .requires(source -> CommandUtils.hasAdminPermission(source))
            .executes(TimeCommand::setNight)
        );
    }
    
    private static int setTime(CommandContext<ServerCommandSource> context) {
        String timeStr = StringArgumentType.getString(context, "time");
        ServerCommandSource source = context.getSource();
        
        ServerWorld world = source.getWorld();
        long time;
        
        switch (timeStr.toLowerCase()) {
            case "day":
            case "morning":
                time = 1000;
                break;
            case "noon":
            case "midday":
                time = 6000;
                break;
            case "night":
            case "evening":
                time = 13000;
                break;
            case "midnight":
                time = 18000;
                break;
            case "sunrise":
                time = 23000;
                break;
            case "sunset":
                time = 12000;
                break;
            default:
                try {
                    time = Long.parseLong(timeStr);
                } catch (NumberFormatException e) {
                    CommandUtils.sendError(source, "Invalid time. Use: day, noon, night, midnight, sunrise, sunset, or a number");
                    return 0;
                }
        }
        
        world.setTimeOfDay(time);
        CommandUtils.sendSuccess(source, "Time set to " + timeStr);
        return 1;
    }
    
    private static int modifyTime(CommandContext<ServerCommandSource> context) {
        String action = StringArgumentType.getString(context, "action");
        int amount = IntegerArgumentType.getInteger(context, "amount");
        ServerCommandSource source = context.getSource();
        
        ServerWorld world = source.getWorld();
        long currentTime = world.getTimeOfDay();
        long newTime;
        
        switch (action.toLowerCase()) {
            case "add":
            case "+":
                newTime = currentTime + amount;
                break;
            case "subtract":
            case "sub":
            case "-":
                newTime = currentTime - amount;
                break;
            default:
                CommandUtils.sendError(source, "Invalid action. Use: add(+) or subtract(-)");
                return 0;
        }
        
        world.setTimeOfDay(newTime);
        CommandUtils.sendSuccess(source, "Time modified by " + action + " " + amount + " (now " + newTime + ")");
        return 1;
    }
    
    private static int setDay(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        ServerWorld world = source.getWorld();
        world.setTimeOfDay(1000);
        CommandUtils.sendSuccess(source, "Time set to day");
        return 1;
    }
    
    private static int setNight(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        ServerWorld world = source.getWorld();
        world.setTimeOfDay(13000);
        CommandUtils.sendSuccess(source, "Time set to night");
        return 1;
    }
}
