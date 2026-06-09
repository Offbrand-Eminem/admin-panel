package com.adminpanel.commands;

import com.adminpanel.utils.CommandUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import java.util.Set;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class SpawnCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("spawn")
            .requires(source -> CommandUtils.hasAdminPermission(source))
            .executes(SpawnCommand::teleportToSpawnSelf)
            .then(argument("player", StringArgumentType.word())
                .executes(SpawnCommand::teleportToSpawnOther)
            )
        );
    }

    private static int teleportToSpawnSelf(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        if (source.getPlayer() == null) {
            CommandUtils.sendError(source, "This command can only be used by players");
            return 0;
        }
        return teleportToSpawn(context, source.getPlayer());
    }

    private static int teleportToSpawnOther(CommandContext<CommandSourceStack> context) {
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

        return teleportToSpawn(context, target);
    }

    private static int teleportToSpawn(CommandContext<CommandSourceStack> context, ServerPlayer player) {
        ServerLevel overworld = context.getSource().getServer().overworld();
        var spawnPoint = overworld.getRespawnData();
        BlockPos spawnPos = spawnPoint.pos();

        Vec3 spawnVec = Vec3.atBottomCenterOf(spawnPos);
        TeleportTransition target = new TeleportTransition(
            overworld,
            spawnVec,
            spawnVec,
            0.0f,
            0.0f,
            TeleportTransition.DO_NOTHING
        );
        player.teleport(target);

        CommandUtils.sendSuccess(context.getSource(), "Teleported " + player.getName().getString() + " to spawn");

        if (context.getSource().getPlayer() != null && !context.getSource().getPlayer().equals(player)) {
            player.sendSystemMessage(Component.literal("§aYou were teleported to spawn by " + context.getSource().getPlayer().getName().getString()));
        }
        return 1;
    }
}
