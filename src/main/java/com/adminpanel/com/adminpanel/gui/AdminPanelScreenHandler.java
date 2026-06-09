package com.adminpanel.gui;

import com.adminpanel.AdminPanelMod;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class AdminPanelScreenHandler extends AbstractContainerMenu {
    private final Container inventory;
    
    public AdminPanelScreenHandler(int syncId, Inventory playerInventory) {
        this(syncId, playerInventory, new SimpleContainer(54));
    }
    
    public AdminPanelScreenHandler(int syncId, Inventory playerInventory, Container inventory) {
        super(MenuType.GENERIC_9x6, syncId);
        this.inventory = inventory;
        
        // Admin panel GUI with 54 slots (6 rows of 9)
        checkContainerSize(inventory, 54);
        
        // Add inventory slots
        for (int i = 0; i < 6; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new AdminPanelSlot(inventory, i * 9 + j, 8 + j * 18, 18 + i * 18));
            }
        }
        
        // Add player inventory slots
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 103 + i * 18 + i));
            }
        }
        
        // Add player hotbar
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 161));
        }
    }
    
    @Override
    public boolean stillValid(Player player) {
        return true;
    }
    
    @Override
    public ItemStack quickMoveStack(Player player, int slot) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slotObj = this.slots.get(slot);
        
        if (slotObj != null && slotObj.hasItem()) {
            ItemStack itemStack2 = slotObj.getItem();
            itemStack = itemStack2.copy();
            
            if (slot < 54) {
                if (!this.moveItemStackTo(itemStack2, 54, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemStack2, 0, 54, false)) {
                return ItemStack.EMPTY;
            }
            
            if (itemStack2.isEmpty()) {
                slotObj.setByPlayer(ItemStack.EMPTY);
            } else {
                slotObj.setChanged();
            }
        }
        
        return itemStack;
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
