package com.adminpanel.gui;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Map;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class AdminPanelScreenHandler extends AbstractContainerMenu {
    private static final int MENU_SIZE = 54;
    private static final Map<Integer, PanelAction> ACTIONS = Map.ofEntries(
        Map.entry(0, new PanelAction("Heal", Items.POTION, "heal", "heal %target%", false)),
        Map.entry(1, new PanelAction("Toggle Fly", Items.FEATHER, "fly", "fly %target%", false)),
        Map.entry(2, new PanelAction("Toggle God", Items.ENCHANTED_GOLDEN_APPLE, "god", "god %target%", false)),
        Map.entry(3, new PanelAction("Toggle Vanish", Items.ENDER_EYE, "vanish", "vanish %target%", false)),
        Map.entry(4, new PanelAction("Survival", Items.IRON_PICKAXE, "gms", "gms %target%", false)),
        Map.entry(5, new PanelAction("Creative", Items.GRASS_BLOCK, "gmc", "gmc %target%", false)),
        Map.entry(6, new PanelAction("Adventure", Items.MAP, "gma", "gma %target%", false)),
        Map.entry(7, new PanelAction("Spectator", Items.SPYGLASS, "gmsp", "gmsp %target%", false)),
        Map.entry(8, new PanelAction("Stop Spectating", Items.BARRIER, "stopspectate", null, false)),
        Map.entry(10, new PanelAction("Day", Items.SUNFLOWER, "day", null, false)),
        Map.entry(11, new PanelAction("Night", Items.BLACK_BED, "night", null, false)),
        Map.entry(12, new PanelAction("Clear Weather", Items.WATER_BUCKET, "clear", null, false)),
        Map.entry(13, new PanelAction("Rain", Items.BLUE_ICE, "rain", null, false)),
        Map.entry(14, new PanelAction("Thunder", Items.LIGHTNING_ROD, "thunder", null, false)),
        Map.entry(15, new PanelAction("Go To Spawn", Items.COMPASS, "spawn", "spawn %target%", false)),
        Map.entry(16, new PanelAction("Set Spawn", Items.RESPAWN_ANCHOR, "setspawn", null, false)),
        Map.entry(17, new PanelAction("Reset Speed", Items.SUGAR, "resetspeed", "resetspeed %target%", false)),
        Map.entry(19, new PanelAction("Kick Target", Items.IRON_DOOR, null, "remove %target% Kicked from Admin UI", true)),
        Map.entry(20, new PanelAction("Mute Target", Items.WRITABLE_BOOK, null, "mute %target% 30 Muted from Admin UI", true)),
        Map.entry(21, new PanelAction("Clear Target Inventory", Items.CHEST, null, "clearinv %target%", true)),
        Map.entry(22, new PanelAction("Kill Target", Items.DIAMOND_SWORD, null, "kill %target%", true)),
        Map.entry(23, new PanelAction("Freeze Target", Items.PACKED_ICE, null, "freeze %target%", true)),
        Map.entry(24, new PanelAction("Unfreeze Target", Items.TORCH, null, "unfreeze %target%", true)),
        Map.entry(25, new PanelAction("Spectate Target", Items.ENDER_PEARL, null, "spectate %target%", true)),
        Map.entry(26, new PanelAction("Target To Spawn", Items.LODESTONE, null, "spawn %target%", true)),
        Map.entry(31, new PanelAction("Admin List", Items.BOOK, "adminpanel list", null, false)),
        Map.entry(32, new PanelAction("Reload Config", Items.REDSTONE, "adminreload", null, false)),
        Map.entry(33, new PanelAction("Admin Panel Info", Items.PAPER, "adminpanel", null, false)),
        Map.entry(40, new PanelAction("Walk Speed 5", Items.RABBIT_FOOT, "speed walk 5", "speed walk 5 %target%", false)),
        Map.entry(41, new PanelAction("Fly Speed 5", Items.ELYTRA, "speed fly 5", "speed fly 5 %target%", false))
    );

    private final Container inventory;
    private final String targetName;
    
    public AdminPanelScreenHandler(int syncId, Inventory playerInventory) {
        this(syncId, playerInventory, (String) null);
    }

    public AdminPanelScreenHandler(int syncId, Inventory playerInventory, String targetName) {
        this(syncId, playerInventory, new SimpleContainer(MENU_SIZE), targetName);
    }
    
    public AdminPanelScreenHandler(int syncId, Inventory playerInventory, Container inventory) {
        this(syncId, playerInventory, inventory, null);
    }

    public AdminPanelScreenHandler(int syncId, Inventory playerInventory, Container inventory, String targetName) {
        super(MenuType.GENERIC_9x6, syncId);
        this.inventory = inventory;
        this.targetName = targetName;
        
        checkContainerSize(inventory, MENU_SIZE);
        populateMenu();
        
        for (int i = 0; i < 6; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new AdminPanelSlot(inventory, i * 9 + j, 8 + j * 18, 18 + i * 18));
            }
        }
        
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 140 + i * 18));
            }
        }
        
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 198));
        }
    }

    private void populateMenu() {
        ItemStack filler = namedItem(Items.GRAY_STAINED_GLASS_PANE, " ");
        for (int slot = 0; slot < MENU_SIZE; slot++) {
            inventory.setItem(slot, filler.copy());
        }

        inventory.setItem(18, namedItem(Items.NAME_TAG, targetName == null ? "No target selected" : "Target: " + targetName));
        for (Map.Entry<Integer, PanelAction> entry : ACTIONS.entrySet()) {
            inventory.setItem(entry.getKey(), namedItem(entry.getValue().icon(), entry.getValue().name()));
        }
    }

    private static ItemStack namedItem(Item item, String name) {
        ItemStack stack = new ItemStack(item);
        stack.set(DataComponents.CUSTOM_NAME, Component.literal(name));
        return stack;
    }
    
    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clicked(int slotId, int button, ClickType clickType, Player player) {
        if (slotId >= 0 && slotId < MENU_SIZE) {
            PanelAction action = ACTIONS.get(slotId);
            if (action != null && player instanceof ServerPlayer serverPlayer) {
                runAction(serverPlayer, action);
            }
            return;
        }

        super.clicked(slotId, button, clickType, player);
    }
    
    @Override
    public ItemStack quickMoveStack(Player player, int slot) {
        return ItemStack.EMPTY;
    }

    private void runAction(ServerPlayer player, PanelAction action) {
        String command = commandFor(action);
        if (command == null) {
            player.displayClientMessage(Component.literal("Open this menu with /adminui <player> to use target actions."), false);
            return;
        }

        player.closeContainer();
        CommandSourceStack source = player.createCommandSourceStack();
        try {
            source.getServer().getCommands().getDispatcher().execute(command, source);
        } catch (CommandSyntaxException exception) {
            player.displayClientMessage(Component.literal("Admin UI command failed: " + exception.getMessage()), false);
        }
    }

    private String commandFor(PanelAction action) {
        if (targetName != null && action.targetCommand() != null) {
            return action.targetCommand().replace("%target%", targetName);
        }

        if (action.requiresTarget()) {
            return null;
        }

        return action.selfCommand();
    }

    private record PanelAction(String name, Item icon, String selfCommand, String targetCommand, boolean requiresTarget) {
    }
    
    private static class AdminPanelSlot extends Slot {
        public AdminPanelSlot(Container inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }
        
        @Override
        public boolean mayPlace(ItemStack stack) {
            return false; // Prevent inserting items into admin panel slots
        }
        
        @Override
        public boolean mayPickup(Player player) {
            return false; // Prevent taking items from admin panel slots
        }
    }
}
