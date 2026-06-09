package de.rawnet.playerfreeze;

import com.adminpanel.commands.FreezeCommand;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PlayerFreezePlugin {
    private static final Logger LOGGER = LoggerFactory.getLogger("playerfreeze");
    private static final Map<UUID, FrozenPosition> LOCKED_POSITIONS = new HashMap<>();

    private PlayerFreezePlugin() {
    }

    public static void initialize() {
        LOGGER.info("PlayerFreeze integrated into admin-panel Forge");
    }

    public static void clear(UUID uuid) {
        LOCKED_POSITIONS.remove(uuid);
    }

    public static void enforceFrozenPlayers(MinecraftServer server) {
        Iterator<Map.Entry<UUID, FrozenPosition>> iterator = LOCKED_POSITIONS.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, FrozenPosition> entry = iterator.next();
            if (!FreezeCommand.isFrozen(entry.getKey()) || server.getPlayerList().getPlayer(entry.getKey()) == null) {
                iterator.remove();
            }
        }

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            UUID uuid = player.getUUID();
            if (!FreezeCommand.isFrozen(uuid)) {
                LOCKED_POSITIONS.remove(uuid);
                continue;
            }

            FrozenPosition position = LOCKED_POSITIONS.computeIfAbsent(uuid, ignored -> FrozenPosition.from(player));
            if (position.hasMoved(player)) {
                TeleportTransition target = new TeleportTransition(
                    player.createCommandSourceStack().getLevel(),
                    new Vec3(position.x(), position.y(), position.z()),
                    Vec3.ZERO,
                    position.yaw(),
                    position.pitch(),
                    TeleportTransition.DO_NOTHING
                );
                player.teleport(target);
                player.displayClientMessage(Component.literal("You are frozen."), true);
            }
        }
    }

    private record FrozenPosition(double x, double y, double z, float yaw, float pitch) {
        static FrozenPosition from(ServerPlayer player) {
            return new FrozenPosition(player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot());
        }

        boolean hasMoved(ServerPlayer player) {
            return Math.abs(player.getX() - x) > 0.01 || Math.abs(player.getY() - y) > 0.01 || Math.abs(player.getZ() - z) > 0.01;
        }
    }
}
