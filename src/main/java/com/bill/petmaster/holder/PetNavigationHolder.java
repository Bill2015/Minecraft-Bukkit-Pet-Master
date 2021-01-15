package com.bill.petmaster.holder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.bill.petmaster.entity.CustomEntity;
import com.bill.petmaster.util.EnitySkillPoint;
import com.bill.petmaster.util.EntitySkill;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class PetNavigationHolder implements InventoryHolder{
    private final Inventory petGUI;
    private final HashMap<Integer, CustomEntity> petSlot = new HashMap<>();
    public PetNavigationHolder( List<CustomEntity> pets ){

        //open a gui for player
        this.petGUI = Bukkit.createInventory( this, 9, ChatColor.BLUE + "" + ChatColor.BOLD + "Pet Checker");
        for (int i = 0, size = pets.size(); i < size; i++) {
            CustomEntity entity = pets.get(i); 
            ItemStack item = new ItemStack( Material.PLAYER_HEAD );
            SkullMeta meta = (SkullMeta)item.getItemMeta();
            meta.setOwner(  "MHF_Ocelot" );
            meta.setDisplayName( String.join("",    ChatColor.GOLD + "<< ", 
                                                    ChatColor.LIGHT_PURPLE + entity.getName(), 
                                                    ChatColor.GOLD + " >>") );
            meta.setLore( getPetDetail( entity ) );
            item.setItemMeta( meta );
            petGUI.setItem(i, item);
            petSlot.put( i, entity );
        }
    }
    /** get which pet are clicked, 
     *  @param slot which slot
     *  @return {@link CustomEntity}, if not found return null */
    public CustomEntity getClickedPet(int slot){
        return petSlot.get( slot );
    }
    /** get the detail of the pet 
     *  @param entity which pet
     *  @return the list of detail */
    private List<String> getPetDetail( CustomEntity entity ){
        List<String> lore   = new ArrayList<>();
        String healthBar    = setBar("HEALTH", entity.getEntity().getHealth(),  entity.getEntity().getAttribute( Attribute.GENERIC_MAX_HEALTH ).getValue() , ChatColor.GREEN);
        String foodBar      = setBar("FOOD  ", entity.getEntityHunger().getFoodValue(),  entity.getEntityHunger().getMaxFoodValue(), ChatColor.GOLD);
        lore.add( healthBar );
        lore.add( foodBar );
        lore.add( "" );
        lore.add(  ChatColor.GRAY + "" + ChatColor.BOLD + "PROPERTY : " );
        EntitySkill skill = entity.getEntityLevel().getEntitySkill();
        lore.add( EnitySkillPoint.DAMAGE.getWhole( skill.getIncrement( EnitySkillPoint.DAMAGE )) );
        lore.add( EnitySkillPoint.ARMOR.getWhole( skill.getIncrement( EnitySkillPoint.ARMOR )) );
        lore.add( EnitySkillPoint.HEALTH.getWhole( skill.getIncrement( EnitySkillPoint.HEALTH )) );
        lore.add( EnitySkillPoint.SPEED.getWhole( skill.getIncrement( EnitySkillPoint.SPEED )) );
        lore.add( EnitySkillPoint.RESIST.getWhole( skill.getIncrement( EnitySkillPoint.RESIST )) );
        lore.add( EnitySkillPoint.FOOD.getWhole( skill.getIncrement( EnitySkillPoint.FOOD )) );
        lore.add( EnitySkillPoint.REGEN.getWhole( skill.getIncrement( EnitySkillPoint.REGEN )) );
        lore.add( "" );
        lore.add( ChatColor.GRAY + "" + ChatColor.BOLD + "STATUS : " );
        if( entity.isDead() ){
            lore.add( ChatColor.RED + "" + ChatColor.BOLD + "Death" );
            lore.add( "" );
        }
        else{
            lore.add( ChatColor.GREEN + "" + ChatColor.BOLD  + "  Alive" );
            lore.add( "" );

            lore.add(  ChatColor.BLUE + "" + ChatColor.BOLD + "POSITION : " );
            Location location = entity.getEntity().getLocation();
            lore.add( String.join("", ChatColor.GREEN + "" + ChatColor.BOLD + "  [ ", 
                        Integer.toString( location.getBlockX() ) , ", ",
                        Integer.toString( location.getBlockY() ), ", ", 
                        Integer.toString( location.getBlockZ() ), " ]" 
                    ) );
        }
                    
        return lore;
    }

    private String setBar(String text, double current, double max, ChatColor color){
        String bar = String.join("",  color + "" + ChatColor.BOLD + text, " [");
        double unit = max / 40;
        for(double i = 0, j = 0; i < 40; i += 1, j += unit){    //單位血量上加 大於目前血量就改紅
            if(j < current)
                bar = bar.concat("|");
            else{
                bar = bar.concat( ChatColor.RED + "" + ChatColor.BOLD + "|");
                j = -99999;
            } 
        }
        bar = bar.concat( color + "]  " + (int)current + "/" + (int)max);
        return bar;
    }

    @Override
    public Inventory getInventory() {
        return petGUI;
    }
}
