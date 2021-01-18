package com.bill.petmaster.listeners;

import java.util.ArrayList;
import java.util.List;

import com.bill.petmaster.App;
import com.bill.petmaster.entity.CustomEntity;
import com.bill.petmaster.holder.PetNavigationHolder;
import com.bill.petmaster.manager.DataManager;
import com.bill.petmaster.manager.ItemManeger;
import com.bill.petmaster.util.AttributePoint;
import com.bill.petmaster.util.PetAttribute;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class ItemUseEvent implements Listener{
    private DataManager dataManager;
    private ItemManeger itemManeger;
    public ItemUseEvent( App plugin, DataManager dataManager, ItemManeger itemManeger ){
        this.dataManager = dataManager;
        this.itemManeger = itemManeger;
    }
    @EventHandler
    public void onPlayerUseChecker( PlayerInteractEvent event ){
        Player player = event.getPlayer(); 
        //check the player is vaild
        if( player != null && player.isValid() ){
            //must right click
            if( event.getAction() != Action.RIGHT_CLICK_AIR )
                return;
            //check the player hand not the null
            if( player.getInventory().getItemInMainHand() == null || 
                    !player.getInventory().getItemInMainHand().hasItemMeta() ||
                        !player.getInventory().getItemInMainHand().equals( itemManeger.getPetChecker() ) ){
                return;
            }

            List<CustomEntity> pets = dataManager.getPets( player );
            PetNavigationHolder petNavigationHolder = new PetNavigationHolder( pets );
            player.openInventory( petNavigationHolder.getInventory() );
        }
    }

    @EventHandler
    public void onPlayerClickCatIcon( InventoryClickEvent event ){
        //if the navigation inventory are click
        if( event.getInventory().getHolder() instanceof PetNavigationHolder ){
            PetNavigationHolder navigationHolder = (PetNavigationHolder)event.getInventory().getHolder();
            Player player = (Player)event.getWhoClicked();
            CustomEntity entity = navigationHolder.getClickedPet( event.getRawSlot() );
            //check entity null
            if( entity != null ){
                //if pet is dead
                if( entity.isDead() ){
                    entity.getEntity().setHealth( entity.getEntity().getAttribute( Attribute.GENERIC_MAX_HEALTH ).getValue() / 2 );
                    entity.getEntity().teleport( player, TeleportCause.COMMAND );
                    entity.getEntity().getWorld().playSound( player.getLocation() , Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f );
                    entity.revival();
                }
                //if pet is alive
                else{
                    entity.getEntity().teleport( player, TeleportCause.COMMAND );
                    entity.getEntity().getWorld().playSound( player.getLocation() , Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f );
                }
            }
            event.setCancelled( true );
            return;
        }
    }

}
