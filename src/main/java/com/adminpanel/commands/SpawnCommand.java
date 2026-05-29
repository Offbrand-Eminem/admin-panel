package com.adminpanel.commands;

import com.adminpanel.utils.CommandUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;

import java.util.Set;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class SpawnCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("spawn")
            .requires(source -> CommandUtils.hasAdminPermission(source))
            .executes(SpawnCommand::teleportToSpawnSelf)
            .then(argument("player", StringArgumentType.word())
                .executes(SpawnCommand::teleportToSpawnOther)
            )
        );
    }

    private static int teleportToSpawnSelf(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        if (source.getPlayer() == null) {
            CommandUtils.sendError(source, "This command can only be used by players");
            return 0;
        }
        return teleportToSpawn(context, source.getPlayer());
    }

    private static int teleportToSpawnOther(CommandContext<ServerCommandSource> context) {
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

        return teleportToSpawn(context, target);
    }

    private static int teleportToSpawn(CommandContext<ServerCommandSource> context, ServerPlayerEntity player) {
        ServerWorld overworld = context.getSource().getServer().getOverworld();
        var spawnPoint = overworld.getSpawnPoint();
        BlockPos spawnPos = spawnPoint.getPos();

        Vec3d spawnVec = Vec3d.ofBottomCenter(spawnPos);
        TeleportTarget target = new TeleportTarget(
            overworld,
            spawnVec,
            spawnVec,
            0.0f,
            0.0f,
            TeleportTarget.NO_OP
        );
        player.teleportTo(target);

        CommandUtils.sendSuccess(context.getSource(), "Teleported " + player.getName().getString() + " to spawn");

        if (context.getSource().getPlayer() != null && !context.getSource().getPlayer().equals(player)) {
            player.sendMessage(Text.literal("§aYou were teleported to spawn by " + context.getSource().getPlayer().getName().getString()));
        }
        return 1;
    }
}
