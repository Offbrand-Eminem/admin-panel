package com.adminpanel.client;

import com.adminpanel.gui.AdminPanelScreen;
import net.minecraft.client.Minecraft;

public final class ForgeAdminPanelClient {
    private ForgeAdminPanelClient() {
    }

    public static void openAdminPanel() {
        Minecraft client = Minecraft.getInstance();
        if (client.player != null) {
            client.setScreen(new AdminPanelScreen());
        }
    }
}
