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
        OpenGuiPayload.registerS2C();

        ClientPlayNetworking.registerGlobalReceiver(OpenGuiPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                if (context.client().player != null) {
                    context.client().setScreen(new AdminPanelScreen());
                }
            });
        });

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("adminui")
                .executes(context -> openLocal(context.getSource().getClient()))
            );

            dispatcher.register(ClientCommandManager.literal("gui")
                .executes(context -> openLocal(context.getSource().getClient()))
            );
        });
    }

    private static int openLocal(Minecraft client) {
        if (client.player != null) {
            client.setScreen(new AdminPanelScreen());
            client.player.displayClientMessage(Component.literal("Opening Admin Panel GUI"), false);
        }
        return 1;
    }
}
