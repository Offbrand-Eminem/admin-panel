package com.adminpanel.commands;

import com.adminpanel.utils.CommandUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class TeleportHereCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("tphere")
            .requires(source -> CommandUtils.hasAdminPermission(source))
            .then(argument("player", StringArgumentType.word())
                .executes(TeleportHereCommand::teleportPlayerHere)
            )
        );
    }

    private static int teleportPlayerHere(CommandContext<ServerCommandSource> context) {
        String playerName = StringArgumentType.getString(context, "player");
        ServerCommandSource source = context.getSource();

        if (source.getPlayer() == null) {
            CommandUtils.sendError(source, "This command can only be used by players");
            return 0;
        }

        if (!CommandUtils.isValidPlayerName(playerName)) {
            CommandUtils.sendError(source, "Invalid player name: " + playerName);
            return 0;
        }

        ServerPlayerEntity target = CommandUtils.getPlayer(source, playerName);
        if (target == null) {
            CommandUtils.sendError(source, "Player not found: " + playerName);
            return 0;
        }

        ServerPlayerEntity player = source.getPlayer();
        ServerWorld world = source.getServer().getOverworld();
        TeleportTarget tp = new TeleportTarget(
            world,
            new Vec3d(player.getX(), player.getY(), player.getZ()),
            new Vec3d(player.getX(), player.getY(), player.getZ()),
            player.getYaw(),
            player.getPitch(),
            TeleportTarget.NO_OP
        );
        target.teleportTo(tp);

        CommandUtils.sendSuccess(source, "Teleported " + playerName + " to you");
        target.sendMessage(Text.literal("§aYou were teleported to " + player.getName().getString()));
        return 1;
    }
}
