package com.adminpanel;

import com.adminpanel.gui.AdminPanelScreen;
import com.adminpanel.network.OpenGuiPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class AdminPanelClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Register packet handler for opening GUI
        ClientPlayNetworking.registerGlobalReceiver(OpenGuiPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                if (context.client().player != null) {
                    context.client().setScreen(new AdminPanelScreen());
                }
            });
        });

        // Register client-side commands as backup
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("adminui")
                .executes(context -> {
                    Minecraft client = context.getSource().getClient();
                    if (client.player != null) {
                        client.setScreen(new AdminPanelScreen());
                        context.getSource().sendFeedback(Component.literal("§aOpening Admin Panel GUI"));
                    }
                    return 1;
                })
            );

            dispatcher.register(ClientCommandManager.literal("gui")
                .executes(context -> {
                    Minecraft client = context.getSource().getClient();
                    if (client.player != null) {
                        client.setScreen(new AdminPanelScreen());
                        context.getSource().sendFeedback(Component.literal("§aOpening Admin Panel GUI"));
                    }
                    return 1;
                })
            );
        });
    }
}
