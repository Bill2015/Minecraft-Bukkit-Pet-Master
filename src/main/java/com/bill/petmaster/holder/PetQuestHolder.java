package com.bill.petmaster.holder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.bill.petmaster.manager.QuestManager;
import com.bill.petmaster.quest.EntityObjective;
import com.bill.petmaster.quest.EntityQuestMap;
import com.bill.petmaster.quest.ItemObjective;
import com.bill.petmaster.quest.ItemQuestMap;
import com.bill.petmaster.quest.PetObjective;
import com.bill.petmaster.quest.PetQuest;
import com.bill.petmaster.util.PetLevel;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PetQuestHolder implements InventoryHolder {
    private Inventory inventory;
    public PetQuestHolder(){
        inventory = Bukkit.createInventory(this, 54, "Pets Quest List");

        int level = 0;
        for (PetQuest petQuest : QuestManager.getPetQuests().values() ) {
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
            Arrays.asList(
                ChatColor.GRAY + "Quest Name : ",
                ChatColor.AQUA + "  " + petQuest.getQuestName(),
                "",
                ChatColor.GRAY   + "Objective : "
            ) );
        for (PetObjective obj : petQuest.getQuestObjective().values()) {
            lore.add(  ChatColor.YELLOW + "  " + obj.getInfo() );
        }

        lore.add( "" );
        lore.add( ChatColor.GRAY + "Reward : " );
        lore.add( ChatColor.GOLD + "  " + petQuest.getPoint() + " Attribute Point" );

        lore.add( "" );
        lore.add( (ChatColor.BLUE + "In progress...") );

        //set item meta
        itemMeta.setLore( lore );
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(itemMeta);
        inventory.setItem(levelSlot, item);
    }

    public void updateItem( PetLevel petLevel, boolean isDone ){
       
        PetQuest quest = petLevel.getNowQuest();
        //set represent material
        ItemStack item = new ItemStack( (isDone == false) ? quest.getRepresent() : quest.getFinished() );
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName( ChatColor.GOLD + "Level " + (petLevel.getLevel()) + " Quest" );

        ArrayList<String> lore = new ArrayList<>( 
            Arrays.asList(  
                ChatColor.GRAY + "Quest Name : ",
                ChatColor.AQUA + "  " + quest.getQuestName(),
                "",
                ChatColor.GRAY   + "Objective : "
            ) );
        // obj name
        for (PetObjective obj : petLevel.getObjectives().values() ) {
            lore.add(  ChatColor.YELLOW  + "  " + (obj.isFinished() ? ChatColor.STRIKETHROUGH : "") + obj.getInfo() );
        }

        lore.add( "" );
        lore.add( ChatColor.GRAY + "Reward : " );
        lore.add( ChatColor.GOLD + "  " + quest.getPoint() + " Attribute Point" );

        lore.add( "" );
        lore.add( (isDone == true) ? (ChatColor.GREEN + "  Complete！") : (ChatColor.BLUE + "  In progress...") );
        // set item meta
        itemMeta.setLore( lore );
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(itemMeta);
        inventory.setItem( petLevel.getLevel() - 1, item);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
