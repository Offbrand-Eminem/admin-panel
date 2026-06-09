package com.adminpanel.commands;

import com.adminpanel.AdminPanelMod;
import com.adminpanel.permission.PermissionManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class SpectateCommand {
    private static final Map<UUID, GameType> previousGameModes = new HashMap<>();
    private static final Map<UUID, ServerLevel> previousWorlds = new HashMap<>();
    private static final Map<UUID, Vec3> previousPositions = new HashMap<>();
    private static final Map<UUID, Vec2> previousRotations = new HashMap<>();
    private static final Map<UUID, UUID> spectatingTargets = new HashMap<>();

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
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

    private static boolean hasAdminPermission(CommandSourceStack source) {
        // if (source.hasPermissionLevel(4)) return true;
        if (source.getPlayer() == null) return false;
        return AdminPanelMod.permissionManager.hasPermission(
            source.getPlayer(),
            PermissionManager.PermissionLevel.ADMIN
        );
    }

    private static int spectatePlayer(CommandContext<CommandSourceStack> context) {
        String playerName = StringArgumentType.getString(context, "player");
        CommandSourceStack source = context.getSource();

        if (source.getPlayer() == null) {
            source.sendFailure(Component.literal("§cThis command can only be used by players"));
            return 0;
        }

        ServerPlayer spectator = source.getPlayer();
        ServerPlayer target = source.getServer().getPlayerList().getPlayerByName(playerName);

        if (target == null) {
            source.sendFailure(Component.literal("§cPlayer not found: " + playerName));
            return 0;
        }

        if (target.equals(spectator)) {
            source.sendFailure(Component.literal("§cYou cannot spectate yourself"));
            return 0;
        }

        UUID uuid = spectator.getUUID();
        previousGameModes.put(uuid, spectator.gameMode.getGameModeForPlayer());
        ServerLevel spectatorWorld = source.getServer().overworld();
        previousWorlds.put(uuid, spectatorWorld);
        Vec3 spectatorPos = new Vec3(spectator.getX(), spectator.getY(), spectator.getZ());
        previousPositions.put(uuid, spectatorPos);
        previousRotations.put(uuid, new Vec2(spectator.getYRot(), spectator.getXRot()));
        spectatingTargets.put(uuid, target.getUUID());

        spectator.setGameMode(GameType.SPECTATOR);
        ServerLevel targetWorld = source.getServer().overworld();
        Vec3 targetPos = new Vec3(target.getX(), target.getY(), target.getZ());
        TeleportTransition tp = new TeleportTransition(
            targetWorld,
            targetPos,
            targetPos,
            target.getYRot(),
            target.getXRot(),
            TeleportTransition.DO_NOTHING
        );
        spectator.teleport(tp);

        source.sendSuccess(() -> Component.literal("§aNow spectating " + playerName + " - Use /stopspectate to stop"), true);
        return 1;
    }

    private static int stopSpectate(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();

        if (source.getPlayer() == null) {
            source.sendFailure(Component.literal("§cThis command can only be used by players"));
            return 0;
        }

        ServerPlayer spectator = source.getPlayer();
        UUID uuid = spectator.getUUID();

        if (!spectatingTargets.containsKey(uuid)) {
            source.sendFailure(Component.literal("§cYou are not currently spectating anyone"));
            return 0;
        }

        GameType prevMode = previousGameModes.getOrDefault(uuid, GameType.SURVIVAL);
        ServerLevel prevWorld = previousWorlds.getOrDefault(uuid, source.getServer().overworld());
        Vec3 prevPos = previousPositions.getOrDefault(uuid, new Vec3(spectator.getX(), spectator.getY(), spectator.getZ()));
        Vec2 prevRot = previousRotations.getOrDefault(uuid, Vec2.ZERO);

        spectator.setGameMode(prevMode);
        TeleportTransition tp = new TeleportTransition(prevWorld, prevPos, prevPos, prevRot.x, prevRot.y, TeleportTransition.DO_NOTHING);
        spectator.teleport(tp);

        spectatingTargets.remove(uuid);
        previousGameModes.remove(uuid);
        previousWorlds.remove(uuid);
        previousPositions.remove(uuid);
        previousRotations.remove(uuid);

        source.sendSuccess(() -> Component.literal("§aStopped spectating"), true);
        return 1;
    }

    public static boolean isSpectating(UUID playerUuid) {
        return spectatingTargets.containsKey(playerUuid);
    }
}
