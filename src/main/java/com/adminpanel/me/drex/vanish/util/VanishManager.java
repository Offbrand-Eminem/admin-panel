package me.drex.vanish.util;

import com.adminpanel.commands.VanishCommand;
import java.util.UUID;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public final class VanishManager {
    private VanishManager() {
    }

    public static boolean isVanished(Entity entity) {
        return entity instanceof ServerPlayer player && isVanished(player);
    }

    public static boolean isVanished(ServerPlayer player) {
        return VanishCommand.isVanished(player.getUUID());
    }

    public static boolean isVanished(UUID uuid) {
        return VanishCommand.isVanished(uuid);
    }
}
