package com.adminpanel.commands;

import com.adminpanel.utils.CommandUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class TeleportHereCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("tphere")
            .requires(source -> CommandUtils.hasAdminPermission(source))
            .then(argument("player", StringArgumentType.word())
                .executes(TeleportHereCommand::teleportPlayerHere)
            )
        );
    }

    private static int teleportPlayerHere(CommandContext<CommandSourceStack> context) {
        String playerName = StringArgumentType.getString(context, "player");
        CommandSourceStack source = context.getSource();

        if (source.getPlayer() == null) {
            CommandUtils.sendError(source, "This command can only be used by players");
            return 0;
        }

        if (!CommandUtils.isValidPlayerName(playerName)) {
            CommandUtils.sendError(source, "Invalid player name: " + playerName);
            return 0;
        }

        ServerPlayer target = CommandUtils.getPlayer(source, playerName);
        if (target == null) {
            CommandUtils.sendError(source, "Player not found: " + playerName);
            return 0;
        }

        ServerPlayer player = source.getPlayer();
        ServerLevel world = source.getServer().overworld();
        TeleportTransition tp = new TeleportTransition(
            world,
            new Vec3(player.getX(), player.getY(), player.getZ()),
            new Vec3(player.getX(), player.getY(), player.getZ()),
            player.getYRot(),
            player.getXRot(),
            TeleportTransition.DO_NOTHING
        );
        target.teleport(tp);

        CommandUtils.sendSuccess(source, "Teleported " + playerName + " to you");
        target.sendSystemMessage(Component.literal("§aYou were teleported to " + player.getName().getString()));
        return 1;
    }
}
