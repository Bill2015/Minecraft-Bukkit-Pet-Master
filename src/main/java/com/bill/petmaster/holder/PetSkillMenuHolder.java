package com.bill.petmaster.holder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.bill.petmaster.entity.CustomEntity;
import com.bill.petmaster.util.EnitySkillPoint;
import com.bill.petmaster.util.EntitySkill;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PetSkillMenuHolder implements InventoryHolder {

    /** The inventory for pet */
    private Inventory inventory;

    private final CustomEntity owner;

    public static final int DAMAGE_SLOT = 10;
    public static final int ARMOR_SLOT  = 11;
    public static final int HEALTH_SLOT = 12;
    public static final int SPEED_SLOT  = 13;
    public static final int RESIST_SLOT = 14;
    public static final int FOOD_SLOT   = 15;
    public static final int REGEN_SLOT  = 16;
    public static final int RESET_SLOT  = 22;

    public PetSkillMenuHolder(CustomEntity owner){
        this.owner = owner;
        this.inventory = Bukkit.createInventory(this, 36,  ChatColor.GOLD + "寵物 << 技能點頁面 >>" );
        skillInventoryInital();
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
    public CustomEntity getOwner() {
        return owner;
    }
    private void skillInventoryInital(){
        setSkillPointItem(EnitySkillPoint.DAMAGE, 10, (short)0, 100);
        setSkillPointItem(EnitySkillPoint.ARMOR , 11, (short)0,   0);
        setSkillPointItem(EnitySkillPoint.HEALTH, 12, (short)0, 100);
        setSkillPointItem(EnitySkillPoint.SPEED , 13, (short)0, 100);
        setSkillPointItem(EnitySkillPoint.RESIST, 14, (short)0,   0);
        setSkillPointItem(EnitySkillPoint.FOOD  , 15, (short)0, 100);
        setSkillPointItem(EnitySkillPoint.REGEN , 16, (short)0,   0);
        setFixedItem(Material.BOOK, ChatColor.GOLD + "尚有 " + 0 + " 點未分配的技能點", 22, Arrays.asList("",ChatColor.LIGHT_PURPLE + "點擊我即可重置技能點" , ChatColor.LIGHT_PURPLE + "需消耗 10 金蘋果" ));
    }
    //設定屬性點的書
    private void setSkillPointItem(EnitySkillPoint skill, int slot, short point, int value){
        ItemStack item = new ItemStack(skill.getMaterial());
        item.setAmount(point == 0 ? 1 : point );
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "點擊增加 " + skill.getName());
        itemMeta.setLore( Arrays.asList("", ChatColor.BLUE + "目前 " + skill.getName() + "為 "+ value + skill.getUnit(), ChatColor.BLUE + "總共點了 " + point + " 點"));
        itemMeta.addItemFlags( ItemFlag.HIDE_ATTRIBUTES );
        item.setItemMeta(itemMeta);
        inventory.setItem(slot, item);
    }

    //設定固定物品
    private void setFixedItem(Material material, String text, int slot, List<String> lore){
        ItemStack item = new ItemStack(material);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(text);
        //隱藏物品數值
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        inventory.setItem(slot, item);
    }

    public void updateStatus(){
        int slot = DAMAGE_SLOT;
        EntitySkill skill = owner.getEntityLevel().getEntitySkill();
        for( EnitySkillPoint sk : EnitySkillPoint.values() ){
            setSkillPointItem(sk, slot++, skill.getPoint( sk ), skill.getIncrement( sk ));
        }
        ArrayList<String> lore = new ArrayList<>();
        lore.add("");
        lore.add( ChatColor.LIGHT_PURPLE + "點擊我即可重置屬性點" );
        lore.add( ChatColor.LIGHT_PURPLE + "需消耗 10 金蘋果" );
        lore.add( ChatColor.AQUA + "註 : 金蘋果不用放在生物的背包");
        setFixedItem(Material.BOOK, ChatColor.GOLD + "尚有 " + skill.getUnUsedPoint() + " 點未分配的屬性點", 22, lore);
    }
}
