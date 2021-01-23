package com.bill.petmaster.entity;

import com.bill.petmaster.util.PetAttribute;
import com.bill.petmaster.util.PetHunger;
import com.bill.petmaster.util.PetLevel;

import org.bukkit.entity.Cat;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MasterCat extends CustomEntity {
    public MasterCat( Mob entity, Player owner, String name, boolean isDead, PetAttribute petAttribute, PetLevel petLevel, PetHunger petHunger, ItemStack[] items) {
        super(entity, owner, name, isDead, petAttribute, petLevel, petHunger, items);

    }

    @Override
    public Cat getEntity() {
        return (Cat) entity;
    }



}
