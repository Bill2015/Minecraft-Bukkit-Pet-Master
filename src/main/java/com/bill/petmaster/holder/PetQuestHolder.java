package com.bill.petmaster.holder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.bill.petmaster.quest.EntityObjective;
import com.bill.petmaster.quest.EntityQuestMap;
import com.bill.petmaster.quest.ItemObjective;
import com.bill.petmaster.quest.ItemQuestMap;
import com.bill.petmaster.quest.PetObjective;
import com.bill.petmaster.quest.PetQuest;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PetQuestHolder implements InventoryHolder {
    private Inventory inventory;
    public PetQuestHolder( List<PetQuest> petQuests ){
        inventory = Bukkit.createInventory(this, 54, "Pets Quest List");

        int level = 0;
        for (PetQuest petQuest : petQuests) {
            setItem( petQuest, level );
            level += 1;
        }
    }
    //設定經驗瓶物品需求
    private void setItem(PetQuest petQuest, int levelSlot){
        //set represent material
        ItemStack item = new ItemStack( petQuest.getRepresent() );
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName( ChatColor.GOLD + "Level " + (levelSlot + 1) + " Quest" );

        //lore setting
        ArrayList<String> lore = new ArrayList<>( 
            Arrays.asList( "", 
                ChatColor.GRAY + "Quest Name : ",
                ChatColor.AQUA + "  " + petQuest.getQuestName(),
                "",
                ChatColor.GRAY   + "Objective : "
            ) );
        for (PetObjective obj : petQuest.getQuestObjective().values()) {
            lore.add(  ChatColor.YELLOW + "  " + obj.getInfo() );
        }
        lore.add( "" );
        lore.add( (ChatColor.BLUE + "In progress...") );

        //set item meta
        itemMeta.setLore( lore );
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(itemMeta);
        inventory.setItem(levelSlot, item);
    }

    public void updateItem( PetQuest petQuest, PetObjective[] objectives, int levelSlot, boolean isDone ){
       
        //set represent material
        ItemStack item = new ItemStack( (isDone == false) ? petQuest.getRepresent() : petQuest.getFinished() );
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName( ChatColor.GOLD + "Level " + (levelSlot) + " Quest" );

        ArrayList<String> lore = new ArrayList<>( 
            Arrays.asList(  
                ChatColor.GRAY + "Quest Name : ",
                ChatColor.AQUA + "  " + petQuest.getQuestName(),
                "",
                ChatColor.GRAY   + "Objective : "
            ) );
        // obj name
        for (PetObjective obj : objectives) {
            lore.add(  ChatColor.YELLOW + "  " + obj.getInfo() );
        }
        lore.add( "" );
        lore.add( (isDone == true) ? (ChatColor.GREEN + "  Complete！") : (ChatColor.BLUE + "  In progress...") );
        // set item meta
        itemMeta.setLore( lore );
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(itemMeta);
        inventory.setItem(levelSlot - 1, item);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
