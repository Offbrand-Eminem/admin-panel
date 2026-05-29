package com.adminpanel.commands;

import com.adminpanel.AdminPanelMod;
import com.adminpanel.permission.PermissionManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.TeleportTarget;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class SpectateCommand {
    private static final Map<UUID, GameMode> previousGameModes = new HashMap<>();
    private static final Map<UUID, ServerWorld> previousWorlds = new HashMap<>();
    private static final Map<UUID, Vec3d> previousPositions = new HashMap<>();
    private static final Map<UUID, Vec2f> previousRotations = new HashMap<>();
    private static final Map<UUID, UUID> spectatingTargets = new HashMap<>();

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("spectate")
            .requires(source -> hasAdminPermission(source))
            .then(argument("player", StringArgumentType.word())
                .executes(SpectateCommand::spectatePlayer)
            )
        );

        dispatcher.register(literal("watch")
            .requires(source -> hasAdminPermission(source))
            .then(argument("player", StringArgumentType.word())
                .executes(SpectateCommand::spectatePlayer)
            )
        );

        dispatcher.register(literal("stopspectate")
            .requires(source -> hasAdminPermission(source))
            .executes(SpectateCommand::stopSpectate)
        );

        dispatcher.register(literal("stopwatch")
            .requires(source -> hasAdminPermission(source))
            .executes(SpectateCommand::stopSpectate)
        );
    }

    private static boolean hasAdminPermission(ServerCommandSource source) {
        // if (source.hasPermissionLevel(4)) return true;
        if (source.getPlayer() == null) return false;
        return AdminPanelMod.permissionManager.hasPermission(
            source.getPlayer(),
            PermissionManager.PermissionLevel.ADMIN
        );
    }

    private static int spectatePlayer(CommandContext<ServerCommandSource> context) {
        String playerName = StringArgumentType.getString(context, "player");
        ServerCommandSource source = context.getSource();

        if (source.getPlayer() == null) {
            source.sendError(Text.literal("§cThis command can only be used by players"));
            return 0;
        }

        ServerPlayerEntity spectator = source.getPlayer();
        ServerPlayerEntity target = source.getServer().getPlayerManager().getPlayer(playerName);

        if (target == null) {
            source.sendError(Text.literal("§cPlayer not found: " + playerName));
            return 0;
        }

        if (target.equals(spectator)) {
            source.sendError(Text.literal("§cYou cannot spectate yourself"));
            return 0;
        }

        UUID uuid = spectator.getUuid();
        previousGameModes.put(uuid, spectator.interactionManager.getGameMode());
        ServerWorld spectatorWorld = source.getServer().getOverworld();
        previousWorlds.put(uuid, spectatorWorld);
        Vec3d spectatorPos = new Vec3d(spectator.getX(), spectator.getY(), spectator.getZ());
        previousPositions.put(uuid, spectatorPos);
        previousRotations.put(uuid, new Vec2f(spectator.getYaw(), spectator.getPitch()));
        spectatingTargets.put(uuid, target.getUuid());

        spectator.changeGameMode(GameMode.SPECTATOR);
        ServerWorld targetWorld = source.getServer().getOverworld();
        Vec3d targetPos = new Vec3d(target.getX(), target.getY(), target.getZ());
        TeleportTarget tp = new TeleportTarget(
            targetWorld,
            targetPos,
            targetPos,
            target.getYaw(),
            target.getPitch(),
            TeleportTarget.NO_OP
        );
        spectator.teleportTo(tp);

        source.sendFeedback(() -> Text.literal("§aNow spectating " + playerName + " - Use /stopspectate to stop"), true);
        return 1;
    }

    private static int stopSpectate(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();

        if (source.getPlayer() == null) {
            source.sendError(Text.literal("§cThis command can only be used by players"));
            return 0;
        }

        ServerPlayerEntity spectator = source.getPlayer();
        UUID uuid = spectator.getUuid();

        if (!spectatingTargets.containsKey(uuid)) {
            source.sendError(Text.literal("§cYou are not currently spectating anyone"));
            return 0;
        }

        GameMode prevMode = previousGameModes.getOrDefault(uuid, GameMode.SURVIVAL);
        ServerWorld prevWorld = previousWorlds.getOrDefault(uuid, source.getServer().getOverworld());
        Vec3d prevPos = previousPositions.getOrDefault(uuid, new Vec3d(spectator.getX(), spectator.getY(), spectator.getZ()));
        Vec2f prevRot = previousRotations.getOrDefault(uuid, Vec2f.ZERO);

        spectator.changeGameMode(prevMode);
        TeleportTarget tp = new TeleportTarget(prevWorld, prevPos, prevPos, prevRot.x, prevRot.y, TeleportTarget.NO_OP);
        spectator.teleportTo(tp);

        spectatingTargets.remove(uuid);
        previousGameModes.remove(uuid);
        previousWorlds.remove(uuid);
        previousPositions.remove(uuid);
        previousRotations.remove(uuid);

        source.sendFeedback(() -> Text.literal("§aStopped spectating"), true);
        return 1;
    }

    public static boolean isSpectating(UUID playerUuid) {
        return spectatingTargets.containsKey(playerUuid);
    }
}
