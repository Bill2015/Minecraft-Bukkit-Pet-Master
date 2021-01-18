package com.bill.petmaster.holder;

import java.util.Arrays;
import java.util.List;

import com.bill.petmaster.entity.CustomEntity;
import com.bill.petmaster.util.AttributePoint;
import com.bill.petmaster.util.PetAttribute;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class PetMainMenuHolder implements InventoryHolder {

    /** The inventory for pet */
    private Inventory inventory;

    /** function item slot */
    public final static int HEATL_SLOT = 7;
    public final static int ATTRI_SLOT = 16;
    public final static int CHEST_SLOT = 34;
    public final static int QUEST_SLOT = 33;

    private final CustomEntity owner;
    public PetMainMenuHolder(CustomEntity owner){
        this.owner = owner;
        this.inventory = Bukkit.createInventory(this, 36, ChatColor.RED + "寵物 << 基礎控制面板 >>" );
        inventoryInital();
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
    public CustomEntity getOwner() {
        return owner;
    }
    
    public void updateStatus(){
        Inventory inv = getInventory();
        ItemStack item = new ItemStack( Material.TOTEM_OF_UNDYING );
        ItemMeta itemMeta = item.getItemMeta();
        double maxHealth = owner.getEntity().getAttribute( Attribute.GENERIC_MAX_HEALTH ).getValue(); 
        //總共 40 根(單位血量)
        String healthBar    = setBar("生命值", owner.getEntity().getHealth(),  maxHealth, ChatColor.GREEN);
        String foodBar      = setBar("飽食度", owner.getPetHunger().getFoodValue(),  owner.getPetHunger().getMaxFoodValue(), ChatColor.GOLD);
        itemMeta.setDisplayName( ChatColor.RED + owner.getName() + ChatColor.GRAY + " 的狀態");
        PetAttribute petAttribute = owner.getPetLevel().getPetAttribute();
        itemMeta.setLore( Arrays.asList(    healthBar, 
                                            foodBar, 
                                            "", 
                                            ChatColor.GRAY + "屬性 : ",
                                            AttributePoint.DAMAGE.getWhole( petAttribute.getIncrement( AttributePoint.DAMAGE ) ),
                                            AttributePoint.ARMOR.getWhole(  petAttribute.getIncrement( AttributePoint.ARMOR ) ),
                                            AttributePoint.HEALTH.getWhole( petAttribute.getIncrement( AttributePoint.HEALTH ) ),
                                            AttributePoint.SPEED.getWhole(  petAttribute.getIncrement( AttributePoint.SPEED ) ),
                                            AttributePoint.RESIST.getWhole( petAttribute.getIncrement( AttributePoint.RESIST ) ),
                                            AttributePoint.FOOD.getWhole(   petAttribute.getIncrement( AttributePoint.FOOD ) ),
                                            AttributePoint.REGEN.getWhole(  petAttribute.getIncrement( AttributePoint.REGEN ) )));
        item.setItemMeta(itemMeta);
        inv.setItem(7, item);
    }
    private String setBar(String text, double current, double max, ChatColor color){
        String bar = color + text + " [";
        double unit = max / 40;
        for(double i = 0, j = 0; i < 40; i += 1, j += unit){    //單位血量上加 大於目前血量就改紅
            if(j < current)
                bar = bar.concat("|");
            else{
                bar = bar.concat( ChatColor.RED + "|");
                j = -99999;
            } 
        }
        bar = bar.concat( color + "]  " + (int)current + "/" + (int)max);
        return bar;
    }
    //背包初始化
    public void inventoryInital(){
        //設定固定的物品
        setFixedItem(Material.LEATHER_HELMET, null, "⭅ 頭盔", 2);
        setFixedItem(Material.LEATHER_CHESTPLATE, null, "⭅ 胸甲", 11);
        setFixedItem(Material.LEATHER_LEGGINGS, null, "⭅ 護腿", 20);
        setFixedItem(Material.LEATHER_BOOTS, null, "⭅ 鞋子", 29);
        setFixedItem(Material.WOODEN_SWORD, null, "⭅ 主要武器", 4);
        setFixedItem(Material.SHIELD, null, "⭅ 次要武器", 13);
        //------------------------------------------
        setFixedItem(Material.BLACK_STAINED_GLASS_PANE, null, "", 0);
        setFixedItem(Material.BLACK_STAINED_GLASS_PANE, null, "", 9);
        setFixedItem(Material.BLACK_STAINED_GLASS_PANE, null, "", 18);
        setFixedItem(Material.BLACK_STAINED_GLASS_PANE, null, "", 27);
        setFixedItem(Material.BLACK_STAINED_GLASS_PANE, null, "", 8);
        setFixedItem(Material.BLACK_STAINED_GLASS_PANE, null, "", 17);
        setFixedItem(Material.BLACK_STAINED_GLASS_PANE, null, "", 26);
        setFixedItem(Material.BLACK_STAINED_GLASS_PANE, null, "", 5);
        setFixedItem(Material.BLACK_STAINED_GLASS_PANE, null, "", 14);
        setFixedItem(Material.BLACK_STAINED_GLASS_PANE, null, "", 23);
        setFixedItem(Material.BLACK_STAINED_GLASS_PANE, null, "", 32);
        setFixedItem(Material.BLACK_STAINED_GLASS_PANE, null, "", 22);
        setFixedItem(Material.BLACK_STAINED_GLASS_PANE, null, "", 21);
        setFixedItem(Material.BLACK_STAINED_GLASS_PANE, null, "", 35);
        //------------------------------------------
        setFixedItem(Material.TOTEM_OF_UNDYING, ChatColor.RED + "", "血量", 7);
        //------------------------------------------
        setFixedItem(Material.BOOK, ChatColor.LIGHT_PURPLE + "點擊分配技能點", 16, Arrays.asList( "" ) );
        //------------------------------------------
        setFixedItem(Material.EXPERIENCE_BOTTLE, ChatColor.GOLD + "升級清單", 33, Arrays.asList( ChatColor.DARK_AQUA + "點擊查看目前升級所需條件" ) );
        //------------------------------------------
        setFixedItem(Material.CHEST, ChatColor.GOLD + "生物背包", 34, Arrays.asList("", ChatColor.GRAY + "用來存放物品 or 食物", ChatColor.GRAY + "假如裡面放食物 他會自己吃" ) );
    }

    //設定固定物品
    public void setFixedItem(Material material, String text, int slot, List<String> lore){
        ItemStack item = new ItemStack(material);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(text);
        //隱藏物品數值
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        inventory.setItem(slot, item);
    }
    //設定固定物品
    private void setFixedItem(Material material, String color, String text, int slot){
        ItemStack item = new ItemStack(material);
        ItemMeta itemMeta = item.getItemMeta();
        //是皮革就把它染色
        if( material.toString().contains("LEATHER_") ){
            LeatherArmorMeta colored = (LeatherArmorMeta)item.getItemMeta();
            colored.setColor( Color.WHITE );
            colored.setDisplayName( ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + text );
            colored.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta( colored );
            inventory.setItem( slot, item );
            return;
        }
        if(color == null)
            itemMeta.setDisplayName( ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + text);
        else  
            itemMeta.setDisplayName(color + text);
        //隱藏物品數值
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(itemMeta);
        inventory.setItem(slot, item);
    }
    
}
