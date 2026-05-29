package com.adminpanel.commands;

import com.adminpanel.utils.CommandUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class TeleportCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
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

    private static int teleportToPlayer(CommandContext<ServerCommandSource> context) {
        String targetName = StringArgumentType.getString(context, "target");
        ServerCommandSource source = context.getSource();

        if (source.getPlayer() == null) {
            CommandUtils.sendError(source, "This command can only be used by players");
            return 0;
        }

        if (!CommandUtils.isValidPlayerName(targetName)) {
            CommandUtils.sendError(source, "Invalid player name: " + targetName);
            return 0;
        }

        ServerPlayerEntity target = CommandUtils.getPlayer(source, targetName);
        if (target == null) {
            CommandUtils.sendError(source, "Player not found: " + targetName);
            return 0;
        }

        ServerPlayerEntity player = source.getPlayer();
        ServerWorld world = source.getServer().getOverworld();
        TeleportTarget tp = new TeleportTarget(
            world,
            new Vec3d(target.getX(), target.getY(), target.getZ()),
            new Vec3d(target.getX(), target.getY(), target.getZ()),
            target.getYaw(),
            target.getPitch(),
            TeleportTarget.NO_OP
        );
        player.teleportTo(tp);

        CommandUtils.sendSuccess(source, "Teleported to " + targetName);
        return 1;
    }

    private static int teleportToCoordinates(CommandContext<ServerCommandSource> context) {
        double x = DoubleArgumentType.getDouble(context, "x");
        double y = DoubleArgumentType.getDouble(context, "y");
        double z = DoubleArgumentType.getDouble(context, "z");
        ServerCommandSource source = context.getSource();

        if (source.getPlayer() == null) {
            CommandUtils.sendError(source, "This command can only be used by players");
            return 0;
        }

        ServerPlayerEntity player = source.getPlayer();
        ServerWorld world = source.getServer().getOverworld();
        TeleportTarget tp = new TeleportTarget(
            world,
            new Vec3d(x, y, z),
            new Vec3d(x, y, z),
            player.getYaw(),
            player.getPitch(),
            TeleportTarget.NO_OP
        );
        player.teleportTo(tp);

        CommandUtils.sendSuccess(source, "Teleported to " + x + ", " + y + ", " + z);
        return 1;
    }
}
