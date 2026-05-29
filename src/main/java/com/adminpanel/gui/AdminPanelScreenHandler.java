package com.adminpanel.gui;

import com.adminpanel.AdminPanelMod;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;

public class AdminPanelScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    
    public AdminPanelScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(54));
    }
    
    public AdminPanelScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(ScreenHandlerType.GENERIC_9X6, syncId);
        this.inventory = inventory;
        
        // Admin panel GUI with 54 slots (6 rows of 9)
        checkSize(inventory, 54);
        
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
    public boolean canUse(PlayerEntity player) {
        return true;
    }
    
    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slotObj = this.slots.get(slot);
        
        if (slotObj != null && slotObj.hasStack()) {
            ItemStack itemStack2 = slotObj.getStack();
            itemStack = itemStack2.copy();
            
            if (slot < 54) {
                if (!this.insertItem(itemStack2, 54, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(itemStack2, 0, 54, false)) {
                return ItemStack.EMPTY;
            }
            
            if (itemStack2.isEmpty()) {
                slotObj.setStack(ItemStack.EMPTY);
            } else {
                slotObj.markDirty();
            }
        }
        
        return itemStack;
    }
    
    private static class AdminPanelSlot extends Slot {
        public AdminPanelSlot(Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }
        
        @Override
        public boolean canInsert(ItemStack stack) {
            return false; // Prevent inserting items into admin panel slots
        }
        
        @Override
        public boolean canTakeItems(PlayerEntity player) {
            return false; // Prevent taking items from admin panel slots
        }
    }
}
