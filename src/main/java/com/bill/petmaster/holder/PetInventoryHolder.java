package com.bill.petmaster.holder;

import com.bill.petmaster.entity.CustomEntity;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class PetInventoryHolder implements InventoryHolder {
    private int capacity;
    private boolean isOpen;
    /** The inventory for pet */
    private Inventory inventory;
    private final CustomEntity owner;
    private final static int DEFAULT_CAPACITY = 9; 
    public PetInventoryHolder(CustomEntity owner){
        this.owner      = owner;
        this.capacity   = DEFAULT_CAPACITY;
        this.isOpen     = false;
        this.inventory  = Bukkit.createInventory(this, 9, "寵物 << 背包 >>");
    }
    /** set this inventory are opened */
    public void setOpen(boolean isOpen) {
        this.isOpen = isOpen;
    }
    /** get this inventory is opening */
    public boolean isOpen() {
        return isOpen;
    }
    @Override
    public Inventory getInventory() {
        return inventory;
    }
    public CustomEntity getOwner() {
        return owner;
    }
    public int getCapacity() {
        return capacity;
    }
    public static int getDefaultCapacity() {
        return DEFAULT_CAPACITY;
    }
}