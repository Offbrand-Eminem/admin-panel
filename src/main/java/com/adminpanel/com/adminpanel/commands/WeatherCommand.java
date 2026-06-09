package com.adminpanel.commands;

import com.adminpanel.utils.CommandUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerLevel;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class WeatherCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("weather")
            .requires(source -> CommandUtils.hasAdminPermission(source))
            .then(argument("weather", StringArgumentType.word())
                .executes(WeatherCommand::setWeather)
                .then(argument("duration", IntegerArgumentType.integer(1))
                    .executes(WeatherCommand::setWeatherWithDuration)
                )
            )
        );

        dispatcher.register(literal("clear")
            .requires(source -> CommandUtils.hasAdminPermission(source))
            .executes(WeatherCommand::setClear)
        );

        dispatcher.register(literal("rain")
            .requires(source -> CommandUtils.hasAdminPermission(source))
            .executes(WeatherCommand::setRain)
        );

        dispatcher.register(literal("thunder")
            .requires(source -> CommandUtils.hasAdminPermission(source))
            .executes(WeatherCommand::setThunder)
        );
    }

    private static int setWeather(CommandContext<CommandSourceStack> context) {
        String weatherStr = StringArgumentType.getString(context, "weather");
        return setWeatherInternal(context, weatherStr, 6000);
    }

    private static int setWeatherWithDuration(CommandContext<CommandSourceStack> context) {
        String weatherStr = StringArgumentType.getString(context, "weather");
        int duration = IntegerArgumentType.getInteger(context, "duration");
        return setWeatherInternal(context, weatherStr, duration);
    }

    private static int setWeatherInternal(CommandContext<CommandSourceStack> context, String weatherStr, int duration) {
        CommandSourceStack source = context.getSource();
        ServerLevel world = source.getLevel();

        switch (weatherStr.toLowerCase()) {
            case "clear": case "sun": case "sunny":
                // clearDuration, rainDuration, raining, thundering
                world.setWeatherParameters(duration, 0, false, false);
                CommandUtils.sendSuccess(source, "Weather set to clear for " + duration + " ticks");
                return 1;
            case "rain": case "rainy":
                world.setWeatherParameters(0, duration, true, false);
                CommandUtils.sendSuccess(source, "Weather set to rain for " + duration + " ticks");
                return 1;
            case "thunder": case "storm": case "lightning":
                world.setWeatherParameters(0, duration, true, true);
                CommandUtils.sendSuccess(source, "Weather set to thunder for " + duration + " ticks");
                return 1;
            default:
                CommandUtils.sendError(source, "Invalid weather. Use: clear, rain, or thunder");
                return 0;
        }
    }

    private static int setClear(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        source.getLevel().setWeatherParameters(6000, 0, false, false);
        CommandUtils.sendSuccess(source, "Weather set to clear");
        return 1;
    }

    private static int setRain(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        source.getLevel().setWeatherParameters(0, 6000, true, false);
        CommandUtils.sendSuccess(source, "Weather set to rain");
        return 1;
    }

    private static int setThunder(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        source.getLevel().setWeatherParameters(0, 6000, true, true);
        CommandUtils.sendSuccess(source, "Weather set to thunder");
        return 1;
    }
}
