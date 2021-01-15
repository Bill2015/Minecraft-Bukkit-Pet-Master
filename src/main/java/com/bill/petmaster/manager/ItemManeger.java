package com.bill.petmaster.manager;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemManeger {

    public final static String CLAIM_STICK = "PetStick";
    public final static String CHECKER_COMPASS = "PetChecker";

    private ItemStack petStick;     //use this to claim a pet or open pet's inventory
    private ItemStack petChecker;   //use this to check all the pets

    public ItemManeger(){
        //-----------------------------------------------
        petStick = new ItemStack( Material.STICK );
        ItemMeta itemMeta1 = petStick.getItemMeta();
        itemMeta1.setDisplayName("寵物認養棒");
        itemMeta1.setLore( Arrays.asList("對貓點擊右鍵，即可確認貓貓！") );
        petStick.setItemMeta( itemMeta1 );
        //-----------------------------------------------
        petChecker = new ItemStack( Material.COMPASS );
        ItemMeta itemMeta2 = petChecker.getItemMeta();
        itemMeta2.setDisplayName("寵物監控盤");
        itemMeta2.setLore( Arrays.asList("點擊右鍵，即可查看貓貓！") );
        petChecker.setItemMeta( itemMeta2 );
    }
    public ItemStack getPetStick() {
        return petStick;
    }
    public ItemStack getPetChecker() {
        return petChecker;
    }
}
