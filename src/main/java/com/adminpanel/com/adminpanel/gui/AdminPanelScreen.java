package com.adminpanel.gui;

import com.adminpanel.AdminPanelMod;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class AdminPanelScreen extends Screen {
    private static final int WINDOW_WIDTH = 390;
    private static final int WINDOW_HEIGHT = 286;
    private static final int BUTTON_WIDTH = 86;
    private static final int BUTTON_HEIGHT = 20;

    private int windowX;
    private int windowY;
    private EditBox targetInput;

    public AdminPanelScreen() {
        super(Component.literal("Admin Panel"));
    }

    @Override
    protected void init() {
        super.init();

        windowX = Math.max(8, (this.width - WINDOW_WIDTH) / 2);
        windowY = Math.max(8, (this.height - WINDOW_HEIGHT) / 2);

        targetInput = new EditBox(this.font, windowX + 84, windowY + 28, 150, 20, Component.literal("Target player"));
        targetInput.setMaxLength(16);
        targetInput.setHint(Component.literal("player name"));
        addRenderableWidget(targetInput);

        addButton(244, 28, 74, "List", "adminpanel list");
        addButton(322, 28, 50, "Reload", "reload");

        addButton(12, 62, "Heal", "heal");
        addButton(106, 62, "Fly", "fly");
        addButton(200, 62, "God", "god");
        addButton(294, 62, "Vanish", "vanish");

        addButton(12, 104, "Kick", targetCommand("remove", "UI action"));
        addButton(106, 104, "Mute 30m", targetCommand("mute", "30 UI mute"));
        addButton(200, 104, "Clear Inv", targetCommand("clearinv", ""));
        addButton(294, 104, "Kill", targetCommand("kill", ""));

        addButton(12, 146, "Freeze", targetCommand("freeze", ""));
        addButton(106, 146, "Unfreeze", targetCommand("unfreeze", ""));
        addButton(200, 146, "Spectate", targetCommand("spectate", ""));
        addButton(294, 146, "Stop Spec", "stopspectate");

        addButton(12, 188, "Day", "time day");
        addButton(106, 188, "Night", "time night");
        addButton(200, 188, "Clear Sky", "weather clear");
        addButton(294, 188, "Set Spawn", "setspawn");

        addButton(12, 230, "GMS", "gms");
        addButton(106, 230, "GMC", "gmc");
        addButton(200, 230, "Walk 5", "speed walk 5");
        addButton(294, 230, "Reset Speed", "resetspeed");
    }

    private void addButton(int x, int y, String label, String command) {
        addButton(x, y, BUTTON_WIDTH, label, command);
    }

    private void addButton(int x, int y, int width, String label, String command) {
        addRenderableWidget(Button.builder(Component.literal(label), btn -> sendCommand(resolveCommand(command)))
            .bounds(windowX + x, windowY + y, width, BUTTON_HEIGHT)
            .build());
    }

    private String targetCommand(String command, String suffix) {
        return command + " {target}" + (suffix.isBlank() ? "" : " " + suffix);
    }

    private String resolveCommand(String command) {
        if (!command.contains("{target}")) {
            return command;
        }

        String target = targetInput.getValue().trim();
        if (target.isEmpty()) {
            return "";
        }

        return command.replace("{target}", target);
    }

    private void sendCommand(String command) {
        if (command == null || command.isBlank()) {
            return;
        }
        if (this.minecraft != null && this.minecraft.player != null) {
            this.minecraft.player.connection.sendCommand(command);
        }
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, this.width, this.height, 0x80000000);
        context.fill(windowX, windowY, windowX + WINDOW_WIDTH, windowY + WINDOW_HEIGHT, 0xFF1A1A1A);
        context.fill(windowX, windowY, windowX + WINDOW_WIDTH, windowY + 1, 0xFF4A4A4A);
        context.fill(windowX, windowY, windowX + 1, windowY + WINDOW_HEIGHT, 0xFF4A4A4A);
        context.fill(windowX + WINDOW_WIDTH - 1, windowY, windowX + WINDOW_WIDTH, windowY + WINDOW_HEIGHT, 0xFF4A4A4A);
        context.fill(windowX, windowY + WINDOW_HEIGHT - 1, windowX + WINDOW_WIDTH, windowY + WINDOW_HEIGHT, 0xFF4A4A4A);
        context.fill(windowX + 1, windowY + 1, windowX + WINDOW_WIDTH - 1, windowY + 20, 0xFF2A2A2A);

        context.drawString(this.font, Component.literal("Admin Panel v" + AdminPanelMod.VERSION), windowX + 8, windowY + 8, 0xF6C85F, false);
        context.drawString(this.font, Component.literal("Target:"), windowX + 12, windowY + 34, 0xE6E6E6, false);
        context.drawString(this.font, Component.literal("Self"), windowX + 12, windowY + 52, 0xF6C85F, false);
        context.drawString(this.font, Component.literal("Target Player"), windowX + 12, windowY + 94, 0xF6C85F, false);
        context.drawString(this.font, Component.literal("Control"), windowX + 12, windowY + 136, 0xF6C85F, false);
        context.drawString(this.font, Component.literal("World"), windowX + 12, windowY + 178, 0xF6C85F, false);
        context.drawString(this.font, Component.literal("Mode / Speed"), windowX + 12, windowY + 220, 0xF6C85F, false);

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
