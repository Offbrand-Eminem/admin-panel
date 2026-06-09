package com.adminpanel.commands;

import com.adminpanel.AdminPanelMod;
import com.adminpanel.permission.PermissionManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class InvseeCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
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

    private static boolean hasAdminPermission(CommandSourceStack source) {
        // if (source.hasPermissionLevel(4)) return true;
        if (source.getPlayer() == null) return false;
        return AdminPanelMod.permissionManager.hasPermission(
            source.getPlayer(),
            PermissionManager.PermissionLevel.ADMIN
        );
    }

    private static int openInventory(CommandContext<CommandSourceStack> context) {
        String playerName = StringArgumentType.getString(context, "player");
        CommandSourceStack source = context.getSource();

        if (source.getPlayer() == null) {
            source.sendFailure(Component.literal("§cThis command can only be used by players"));
            return 0;
        }

        ServerPlayer target = source.getServer().getPlayerList().getPlayerByName(playerName);
        if (target == null) {
            source.sendFailure(Component.literal("§cPlayer not found: " + playerName));
            return 0;
        }

        ServerPlayer admin = source.getPlayer();
        admin.openMenu(new MenuProvider() {
            @Override
            public Component getDisplayName() {
                return Component.literal(target.getName().getString() + "'s Inventory");
            }

            @Override
            public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
                Inventory targetInv = target.getInventory();
                Container viewInv = new Container() {
                    @Override public int getContainerSize() { return 36; }
                    @Override public boolean isEmpty() { return false; }
                    @Override public ItemStack getItem(int slot) {
                        return targetInv.getItem(slot);
                    }
                    @Override public ItemStack removeItem(int slot, int amount) { return ItemStack.EMPTY; }
                    @Override public ItemStack removeItemNoUpdate(int slot) { return ItemStack.EMPTY; }
                    @Override public void setItem(int slot, ItemStack stack) {
                        targetInv.setItem(slot, stack);
                    }
                    @Override public void setChanged() {}
                    @Override public boolean stillValid(Player p) { return true; }
                    @Override public void clearContent() {}
                };
                return ChestMenu.fourRows(syncId, playerInventory);
            }
        });

        source.sendSuccess(() -> Component.literal("§aOpened " + playerName + "'s inventory"), true);
        return 1;
    }

    private static int openEnderChest(CommandContext<CommandSourceStack> context) {
        String playerName = StringArgumentType.getString(context, "player");
        CommandSourceStack source = context.getSource();

        if (source.getPlayer() == null) {
            source.sendFailure(Component.literal("§cThis command can only be used by players"));
            return 0;
        }

        ServerPlayer target = source.getServer().getPlayerList().getPlayerByName(playerName);
        if (target == null) {
            source.sendFailure(Component.literal("§cPlayer not found: " + playerName));
            return 0;
        }

        ServerPlayer admin = source.getPlayer();
        admin.openMenu(new MenuProvider() {
            @Override
            public Component getDisplayName() {
                return Component.literal(target.getName().getString() + "'s Ender Chest");
            }

            @Override
            public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
                return ChestMenu.threeRows(syncId, playerInventory,
                    target.getEnderChestInventory());
            }
        });

        source.sendSuccess(() -> Component.literal("§aOpened " + playerName + "'s ender chest"), true);
        return 1;
    }
}
