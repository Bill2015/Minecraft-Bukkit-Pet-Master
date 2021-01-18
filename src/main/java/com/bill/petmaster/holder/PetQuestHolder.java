package com.bill.petmaster.holder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.bill.petmaster.quest.EntityObjective;
import com.bill.petmaster.quest.EntityQuestMap;
import com.bill.petmaster.quest.ItemObjective;
import com.bill.petmaster.quest.ItemQuestMap;
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
        }
    }
    //設定經驗瓶物品需求
    private void setItem(PetQuest petQuest, int level){
        //set represent material
        ItemStack item = new ItemStack( petQuest.getRepresent() );
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName( ChatColor.GOLD + "Level " + (level + 1) + " Quest" );

        //lore setting
        List<String> lore = Arrays.asList( "", ChatColor.GRAY   + "Objective : ");
        //if is a item quest
        if( petQuest.getQuestType().equals( PetQuest.ITEM ) ){
            //get every objective
            for (ItemObjective itemObjs : ((ItemQuestMap)petQuest.getQuestObjective()).values()) {
                lore.add(  ChatColor.YELLOW + "  " + itemObjs.getInfo() );
            }

        }
        //if is a entity quest
        else if( petQuest.getQuestType().equals( PetQuest.ENTITY ) ){
            //get every objective
            for (EntityObjective entityObjs : ((EntityQuestMap)petQuest.getQuestObjective()).values()) {
                lore.add(  ChatColor.YELLOW + "  " + entityObjs.getInfo() );
            }
        }
        //set item meta
        itemMeta.setLore( lore );
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(itemMeta);
        inventory.setItem(level, item);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
