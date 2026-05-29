package com.adminpanel.commands;

import com.adminpanel.AdminPanelMod;
import com.adminpanel.permission.PermissionManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class InvseeCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("invsee")
            .requires(source -> hasAdminPermission(source))
            .then(argument("player", StringArgumentType.word())
                .executes(InvseeCommand::openInventory)
            )
        );

        dispatcher.register(literal("endersee")
            .requires(source -> hasAdminPermission(source))
            .then(argument("player", StringArgumentType.word())
                .executes(InvseeCommand::openEnderChest)
            )
        );
    }

    private static boolean hasAdminPermission(ServerCommandSource source) {
        // if (source.hasPermissionLevel(4)) return true;
        if (source.getPlayer() == null) return false;
        return AdminPanelMod.permissionManager.hasPermission(
            source.getPlayer(),
            PermissionManager.PermissionLevel.ADMIN
        );
    }

    private static int openInventory(CommandContext<ServerCommandSource> context) {
        String playerName = StringArgumentType.getString(context, "player");
        ServerCommandSource source = context.getSource();

        if (source.getPlayer() == null) {
            source.sendError(Text.literal("§cThis command can only be used by players"));
            return 0;
        }

        ServerPlayerEntity target = source.getServer().getPlayerManager().getPlayer(playerName);
        if (target == null) {
            source.sendError(Text.literal("§cPlayer not found: " + playerName));
            return 0;
        }

        ServerPlayerEntity admin = source.getPlayer();
        admin.openHandledScreen(new NamedScreenHandlerFactory() {
            @Override
            public Text getDisplayName() {
                return Text.literal(target.getName().getString() + "'s Inventory");
            }

            @Override
            public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
                PlayerInventory targetInv = target.getInventory();
                Inventory viewInv = new Inventory() {
                    @Override public int size() { return 36; }
                    @Override public boolean isEmpty() { return false; }
                    @Override public ItemStack getStack(int slot) {
                        return targetInv.getStack(slot);
                    }
                    @Override public ItemStack removeStack(int slot, int amount) { return ItemStack.EMPTY; }
                    @Override public ItemStack removeStack(int slot) { return ItemStack.EMPTY; }
                    @Override public void setStack(int slot, ItemStack stack) {
                        targetInv.setStack(slot, stack);
                    }
                    @Override public void markDirty() {}
                    @Override public boolean canPlayerUse(PlayerEntity p) { return true; }
                    @Override public void clear() {}
                };
                return GenericContainerScreenHandler.createGeneric9x4(syncId, playerInventory);
            }
        });

        source.sendFeedback(() -> Text.literal("§aOpened " + playerName + "'s inventory"), true);
        return 1;
    }

    private static int openEnderChest(CommandContext<ServerCommandSource> context) {
        String playerName = StringArgumentType.getString(context, "player");
        ServerCommandSource source = context.getSource();

        if (source.getPlayer() == null) {
            source.sendError(Text.literal("§cThis command can only be used by players"));
            return 0;
        }

        ServerPlayerEntity target = source.getServer().getPlayerManager().getPlayer(playerName);
        if (target == null) {
            source.sendError(Text.literal("§cPlayer not found: " + playerName));
            return 0;
        }

        ServerPlayerEntity admin = source.getPlayer();
        admin.openHandledScreen(new NamedScreenHandlerFactory() {
            @Override
            public Text getDisplayName() {
                return Text.literal(target.getName().getString() + "'s Ender Chest");
            }

            @Override
            public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
                return GenericContainerScreenHandler.createGeneric9x3(syncId, playerInventory,
                    target.getEnderChestInventory());
            }
        });

        source.sendFeedback(() -> Text.literal("§aOpened " + playerName + "'s ender chest"), true);
        return 1;
    }
}
