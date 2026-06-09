package com.adminpanel;

import com.adminpanel.commands.AdminPanelCommand;
import com.adminpanel.commands.AdminUICommand;
import com.adminpanel.commands.BanCommand;
import com.adminpanel.commands.BroadcastCommand;
import com.adminpanel.commands.ClearInventoryCommand;
import com.adminpanel.commands.DeopCommand;
import com.adminpanel.commands.FlyCommand;
import com.adminpanel.commands.FreezeCommand;
import com.adminpanel.commands.GamemodeCommand;
import com.adminpanel.commands.GiveCommand;
import com.adminpanel.commands.GodModeCommand;
import com.adminpanel.commands.HealCommand;
import com.adminpanel.commands.InvseeCommand;
import com.adminpanel.commands.KickCommand;
import com.adminpanel.commands.KillCommand;
import com.adminpanel.commands.MuteCommand;
import com.adminpanel.commands.OpCommand;
import com.adminpanel.commands.ReloadCommand;
import com.adminpanel.commands.SetSpawnCommand;
import com.adminpanel.commands.SetupCommand;
import com.adminpanel.commands.SpawnCommand;
import com.adminpanel.commands.SpectateCommand;
import com.adminpanel.commands.SpeedCommand;
import com.adminpanel.commands.TeleportCommand;
import com.adminpanel.commands.TeleportHereCommand;
import com.adminpanel.commands.TimeCommand;
import com.adminpanel.commands.UnmuteCommand;
import com.adminpanel.commands.VanishCommand;
import com.adminpanel.commands.WeatherCommand;
import com.adminpanel.config.ConfigManager;
import com.adminpanel.network.ForgeOpenGuiPacket;
import com.adminpanel.permission.PermissionManager;
import de.rawnet.playerfreeze.PlayerFreezePlugin;
import me.declipsonator.chatcontrol.ChatControl;
import me.declipsonator.chatcontrol.command.FilterCommand;
import me.drex.vanish.VanishMod;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(AdminPanelMod.MOD_ID)
public class AdminPanelMod {
    public static final String MOD_ID = "admin_panel";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final String VERSION = "1.0.0";

    public static ConfigManager configManager;
    public static PermissionManager permissionManager;

    public AdminPanelMod() {
        LOGGER.info("Admin Panel Forge Mod v{} is initializing...", VERSION);
        MinecraftForge.EVENT_BUS.register(this);
        ForgeOpenGuiPacket.register();

        new ChatControl().onInitialize();
        PlayerFreezePlugin.initialize();
        VanishMod.initialize();
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        configManager = new ConfigManager(event.getServer());
        configManager.loadConfig();
        permissionManager = new PermissionManager(configManager);
        LOGGER.info("Admin Panel Forge configuration loaded successfully!");
    }

    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        LOGGER.info("Admin Panel Forge Mod is shutting down...");
        if (permissionManager != null) {
            permissionManager.clearTemporaryPermissions();
        }
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        var dispatcher = event.getDispatcher();
        if (configManager != null && !configManager.isSetupCompleted()) {
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
        FilterCommand.register(dispatcher);
        me.declipsonator.chatcontrol.command.MuteCommand.register(dispatcher);
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        if (VanishCommand.isVanished(player.getUUID())) {
            VanishCommand.updateVanishState(player);
            LOGGER.debug("Restored vanish state for player: {}", player.getName().getString());
        }

        if (configManager != null && configManager.isMaintenanceMode()) {
            if (permissionManager != null && !permissionManager.hasPermission(player, PermissionManager.PermissionLevel.ADMIN)) {
                player.connection.disconnect(Component.literal(configManager.getMaintenanceMessage()));
            }
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (permissionManager != null) {
            permissionManager.removeTemporaryPermission(event.getEntity().getUUID());
        }
        PlayerFreezePlugin.clear(event.getEntity().getUUID());
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent.Post event) {
        PlayerFreezePlugin.enforceFrozenPlayers(event.server());
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
