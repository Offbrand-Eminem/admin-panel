package com.adminpanel;

import com.adminpanel.config.ConfigManager;
import com.adminpanel.permission.PermissionManager;
import com.adminpanel.commands.*;
import com.adminpanel.network.OpenGuiPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdminPanelMod implements ModInitializer {
    public static final String MOD_ID = "admin-panel";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final String VERSION = "1.0.0";

    public static ConfigManager configManager;
    public static PermissionManager permissionManager;

    @Override
    public void onInitialize() {
        LOGGER.info("Admin Panel Mod v{} is initializing...", VERSION);

        // Register networking payload type
        PayloadTypeRegistry.playS2C().register(OpenGuiPayload.ID, OpenGuiPayload.CODEC);

        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            configManager = new ConfigManager(server);
            configManager.loadConfig();
            permissionManager = new PermissionManager(configManager);
            LOGGER.info("Admin Panel configuration loaded successfully!");
        });

        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            LOGGER.info("Admin Panel Mod is shutting down...");
            if (permissionManager != null) {
                permissionManager.clearTemporaryPermissions();
            }
        });

        registerCommands();
        registerEvents();

        LOGGER.info("Admin Panel Mod v{} has been initialized!", VERSION);
    }

    private void registerCommands() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            // Register setup command only if not completed
            if (AdminPanelMod.configManager != null && !AdminPanelMod.configManager.isSetupCompleted()) {
                SetupCommand.register(dispatcher);
            }
            
            BanCommand.register(dispatcher);
            KickCommand.register(dispatcher);
            MuteCommand.register(dispatcher);
            UnmuteCommand.register(dispatcher);
            GamemodeCommand.register(dispatcher);
            TeleportCommand.register(dispatcher);
            TeleportHereCommand.register(dispatcher);
            GiveCommand.register(dispatcher);
            ClearInventoryCommand.register(dispatcher);
            HealCommand.register(dispatcher);
            FlyCommand.register(dispatcher);
            SpeedCommand.register(dispatcher);
            TimeCommand.register(dispatcher);
            WeatherCommand.register(dispatcher);
            OpCommand.register(dispatcher);
            DeopCommand.register(dispatcher);
            ReloadCommand.register(dispatcher);
            AdminPanelCommand.register(dispatcher);
            VanishCommand.register(dispatcher);
            SpectateCommand.register(dispatcher);
            FreezeCommand.register(dispatcher);
            InvseeCommand.register(dispatcher);
            KillCommand.register(dispatcher);
            BroadcastCommand.register(dispatcher);
            GodModeCommand.register(dispatcher);
            SpawnCommand.register(dispatcher);
            SetSpawnCommand.register(dispatcher);
            AdminUICommand.register(dispatcher);
        });
    }

    private void registerEvents() {
        // Restore vanish state on player join
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            var player = handler.getPlayer();
            if (VanishCommand.isVanished(player.getUuid())) {
                VanishCommand.updateVanishState(player);
                LOGGER.debug("Restored vanish state for player: {}", player.getName().getString());
            }

            // Enforce maintenance mode on join
            if (configManager != null && configManager.isMaintenanceMode()) {
                if (permissionManager != null && !permissionManager.hasPermission(player, PermissionManager.PermissionLevel.ADMIN)) {
                    player.networkHandler.disconnect(
                        net.minecraft.text.Text.literal(configManager.getMaintenanceMessage())
                    );
                }
            }
        });

        // Clean up on player disconnect
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            var player = handler.getPlayer();
            if (permissionManager != null) {
                permissionManager.removeTemporaryPermission(player.getUuid());
            }
        });
    }

    public static net.minecraft.util.Identifier id(String path) {
        return net.minecraft.util.Identifier.of(MOD_ID, path);
    }
}
