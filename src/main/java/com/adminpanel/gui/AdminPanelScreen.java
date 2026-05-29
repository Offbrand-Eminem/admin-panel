package com.adminpanel.gui;

import com.adminpanel.AdminPanelMod;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class AdminPanelScreen extends Screen {
    private int windowX = 50;
    private int windowY = 50;
    private int windowWidth = 360;
    private int windowHeight = 350;

    public AdminPanelScreen() {
        super(Text.literal("Admin Panel"));
    }

    @Override
    protected void init() {
        super.init();

        // Quick Actions
        addDrawableChild(ButtonWidget.builder(Text.literal("Heal Self"), btn -> sendCommand("heal"))
            .dimensions(windowX + 8, windowY + 40, 80, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Fly Toggle"), btn -> sendCommand("fly"))
            .dimensions(windowX + 96, windowY + 40, 80, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("God Mode"), btn -> sendCommand("godmode"))
            .dimensions(windowX + 184, windowY + 40, 80, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Vanish"), btn -> sendCommand("vanish"))
            .dimensions(windowX + 272, windowY + 40, 80, 20).build());

        // Player Management
        addDrawableChild(ButtonWidget.builder(Text.literal("Kick All"), btn -> sendCommand("removeall Restart"))
            .dimensions(windowX + 8, windowY + 80, 80, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Mute All"), btn -> sendCommand("muteall"))
            .dimensions(windowX + 96, windowY + 80, 80, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Clear Inv All"), btn -> sendCommand("clearinv @a"))
            .dimensions(windowX + 184, windowY + 80, 80, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Kill All"), btn -> sendCommand("kill @a"))
            .dimensions(windowX + 272, windowY + 80, 80, 20).build());

        // World Control
        addDrawableChild(ButtonWidget.builder(Text.literal("Day"), btn -> sendCommand("time day"))
            .dimensions(windowX + 8, windowY + 120, 80, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Night"), btn -> sendCommand("time night"))
            .dimensions(windowX + 96, windowY + 120, 80, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Clear Weather"), btn -> sendCommand("weather clear"))
            .dimensions(windowX + 184, windowY + 120, 80, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Set Spawn"), btn -> sendCommand("setspawn"))
            .dimensions(windowX + 272, windowY + 120, 80, 20).build());

        // Gamemode
        addDrawableChild(ButtonWidget.builder(Text.literal("Survival"), btn -> sendCommand("gamemode survival"))
            .dimensions(windowX + 8, windowY + 160, 80, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Creative"), btn -> sendCommand("gamemode creative"))
            .dimensions(windowX + 96, windowY + 160, 80, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Adventure"), btn -> sendCommand("gamemode adventure"))
            .dimensions(windowX + 184, windowY + 160, 80, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Spectator"), btn -> sendCommand("gamemode spectator"))
            .dimensions(windowX + 272, windowY + 160, 80, 20).build());

        // Admin Commands
        addDrawableChild(ButtonWidget.builder(Text.literal("Admin List"), btn -> sendCommand("adminpanel list"))
            .dimensions(windowX + 8, windowY + 200, 100, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Reload Config"), btn -> sendCommand("reload"))
            .dimensions(windowX + 118, windowY + 200, 100, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("To Spawn"), btn -> sendCommand("spawn"))
            .dimensions(windowX + 228, windowY + 200, 100, 20).build());

        // Speed
        addDrawableChild(ButtonWidget.builder(Text.literal("Speed 1"), btn -> sendCommand("speed 1"))
            .dimensions(windowX + 8, windowY + 240, 80, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Speed 5"), btn -> sendCommand("speed 5"))
            .dimensions(windowX + 96, windowY + 240, 80, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Speed 10"), btn -> sendCommand("speed 10"))
            .dimensions(windowX + 184, windowY + 240, 80, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Reset Speed"), btn -> sendCommand("speed 1"))
            .dimensions(windowX + 272, windowY + 240, 80, 20).build());

        // Freeze
        addDrawableChild(ButtonWidget.builder(Text.literal("Freeze All"), btn -> sendCommand("freeze @a"))
            .dimensions(windowX + 8, windowY + 280, 100, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Unfreeze All"), btn -> sendCommand("unfreeze @a"))
            .dimensions(windowX + 118, windowY + 280, 100, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Spectate"), btn -> sendCommand("spectate"))
            .dimensions(windowX + 228, windowY + 280, 100, 20).build());
    }

    private void sendCommand(String command) {
        if (this.client != null && this.client.player != null) {
            this.client.player.networkHandler.sendChatCommand(command);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Draw semi-transparent background
        context.fill(0, 0, this.width, this.height, 0x80000000);

        // Draw window background
        context.fill(windowX, windowY, windowX + windowWidth, windowY + windowHeight, 0xFF1a1a1a);

        // Draw border
        context.fill(windowX, windowY, windowX + windowWidth, windowY + 1, 0xFF4a4a4a);
        context.fill(windowX, windowY, windowX + 1, windowY + windowHeight, 0xFF4a4a4a);
        context.fill(windowX + windowWidth - 1, windowY, windowX + windowWidth, windowY + windowHeight, 0xFF4a4a4a);
        context.fill(windowX, windowY + windowHeight - 1, windowX + windowWidth, windowY + windowHeight, 0xFF4a4a4a);

        // Draw title bar
        context.fill(windowX + 1, windowY + 1, windowX + windowWidth - 1, windowY + 20, 0xFF2a2a2a);

        // Draw title
        context.drawText(this.textRenderer, Text.literal("§6Admin Panel v" + AdminPanelMod.VERSION), windowX + 8, windowY + 8, 0xFFFFFF, false);

        // Draw section labels
        context.drawText(this.textRenderer, Text.literal("§eQuick Actions:"), windowX + 8, windowY + 30, 0xFFFFFF, false);
        context.drawText(this.textRenderer, Text.literal("§ePlayer Management:"), windowX + 8, windowY + 70, 0xFFFFFF, false);
        context.drawText(this.textRenderer, Text.literal("§eWorld Control:"), windowX + 8, windowY + 110, 0xFFFFFF, false);
        context.drawText(this.textRenderer, Text.literal("§eGamemode:"), windowX + 8, windowY + 150, 0xFFFFFF, false);
        context.drawText(this.textRenderer, Text.literal("§eAdmin Commands:"), windowX + 8, windowY + 190, 0xFFFFFF, false);
        context.drawText(this.textRenderer, Text.literal("§eSpeed Control:"), windowX + 8, windowY + 230, 0xFFFFFF, false);
        context.drawText(this.textRenderer, Text.literal("§eFreeze/Spectate:"), windowX + 8, windowY + 270, 0xFFFFFF, false);

        // Draw server info
        if (AdminPanelMod.configManager != null) {
            context.drawText(this.textRenderer, Text.literal("§fOwners: " + AdminPanelMod.configManager.getOwners().size()), windowX + 8, windowY + 315, 0xFFFFFF, false);
            context.drawText(this.textRenderer, Text.literal("§fAdmins: " + AdminPanelMod.configManager.getAdmins().size()), windowX + 8, windowY + 327, 0xFFFFFF, false);
            context.drawText(this.textRenderer, Text.literal("§fMaintenance: " + (AdminPanelMod.configManager.isMaintenanceMode() ? "§cON" : "§aOFF")), windowX + 8, windowY + 339, 0xFFFFFF, false);
        }

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
