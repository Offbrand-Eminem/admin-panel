package com.adminpanel.commands;

import com.adminpanel.utils.CommandUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class TeleportCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("tp")
            .requires(source -> CommandUtils.hasAdminPermission(source))
            .then(argument("target", StringArgumentType.word())
                .executes(TeleportCommand::teleportToPlayer)
            )
            .then(argument("x", DoubleArgumentType.doubleArg())
                .then(argument("y", DoubleArgumentType.doubleArg())
                    .then(argument("z", DoubleArgumentType.doubleArg())
                        .executes(TeleportCommand::teleportToCoordinates)
                    )
                )
            )
        );
    }

    private static int teleportToPlayer(CommandContext<CommandSourceStack> context) {
        String targetName = StringArgumentType.getString(context, "target");
        CommandSourceStack source = context.getSource();

        if (source.getPlayer() == null) {
            CommandUtils.sendError(source, "This command can only be used by players");
            return 0;
        }

        if (!CommandUtils.isValidPlayerName(targetName)) {
            CommandUtils.sendError(source, "Invalid player name: " + targetName);
            return 0;
        }

        ServerPlayer target = CommandUtils.getPlayer(source, targetName);
        if (target == null) {
            CommandUtils.sendError(source, "Player not found: " + targetName);
            return 0;
        }

        ServerPlayer player = source.getPlayer();
        ServerLevel world = source.getServer().overworld();
        TeleportTransition tp = new TeleportTransition(
            world,
            new Vec3(target.getX(), target.getY(), target.getZ()),
            new Vec3(target.getX(), target.getY(), target.getZ()),
            target.getYRot(),
            target.getXRot(),
            TeleportTransition.DO_NOTHING
        );
        player.teleport(tp);

        CommandUtils.sendSuccess(source, "Teleported to " + targetName);
        return 1;
    }

    private static int teleportToCoordinates(CommandContext<CommandSourceStack> context) {
        double x = DoubleArgumentType.getDouble(context, "x");
        double y = DoubleArgumentType.getDouble(context, "y");
        double z = DoubleArgumentType.getDouble(context, "z");
        CommandSourceStack source = context.getSource();

        if (source.getPlayer() == null) {
            CommandUtils.sendError(source, "This command can only be used by players");
            return 0;
        }

        ServerPlayer player = source.getPlayer();
        ServerLevel world = source.getServer().overworld();
        TeleportTransition tp = new TeleportTransition(
            world,
            new Vec3(x, y, z),
            new Vec3(x, y, z),
            player.getYRot(),
            player.getXRot(),
            TeleportTransition.DO_NOTHING
        );
        player.teleport(tp);

        CommandUtils.sendSuccess(source, "Teleported to " + x + ", " + y + ", " + z);
        return 1;
    }
}
