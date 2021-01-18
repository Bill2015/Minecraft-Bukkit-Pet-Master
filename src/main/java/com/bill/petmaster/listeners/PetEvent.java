package com.bill.petmaster.listeners;

import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import com.bill.petmaster.App;
import com.bill.petmaster.entity.CustomEntity;
import com.bill.petmaster.entity.MasterCat;
import com.bill.petmaster.holder.PetInventoryHolder;
import com.bill.petmaster.holder.PetMainMenuHolder;
import com.bill.petmaster.holder.PetQuestHolder;
import com.bill.petmaster.holder.PetAttributeMenuHolder;
import com.bill.petmaster.manager.DataManager;
import com.bill.petmaster.manager.ItemManeger;
import com.bill.petmaster.manager.QuestManager;
import com.bill.petmaster.quest.PetQuest;
import com.bill.petmaster.util.PetLevel;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

public class PetEvent implements Listener{
    private App plugin;
    private DataManager dataManager;
    private ItemManeger itemManeger;
    private QuestManager questManager;
    public PetEvent(App plugin, DataManager dataManager, ItemManeger itemManeger, QuestManager questManager){
        this.plugin = plugin;
        this.dataManager    = dataManager;
        this.itemManeger    = itemManeger;
        this.questManager   = questManager;
        updaterTick();
    }    

    @EventHandler
    public void onPlayerAttack( EntityDamageByEntityEvent event ){
        if( !(event.getDamager() instanceof Player) )
            return;
        if( !(event.getEntity() instanceof LivingEntity) )
            return;
        
        Player player = (Player)event.getDamager();
        //dont attack other own pets
        if( !(event.getEntity() instanceof Tameable) || ((Tameable)event.getEntity()).getOwner() == null || !((Tameable)event.getEntity()).getOwner().getUniqueId().equals( player.getUniqueId() ) ){
            //set every pet to this target
            for (CustomEntity pet : dataManager.getPets( player ) ) {
                // if is a masterCat
                if( pet instanceof MasterCat ){
                    //check is target out of range
                    double followRange = pet.getEntity().getAttribute( Attribute.GENERIC_FOLLOW_RANGE ).getDefaultValue();
                    if( pet.getEntity().getLocation().distance( event.getEntity().getLocation() ) <= followRange ){
                        ((Cat)pet.getEntity()).setTarget( (LivingEntity)event.getEntity() );
                    }
                }
            }
        } 
    }

    @EventHandler
    public void onPetAttack( EntityDamageByEntityEvent event ){
        if( !(event.getDamager() instanceof LivingEntity) )
            return;
        if( !(event.getEntity() instanceof Mob) )
            return;
        
        if( dataManager.getPetsMap().containsKey( event.getDamager().getUniqueId() ) ){
            CustomEntity pet = (CustomEntity)dataManager.getPetsMap().get( event.getDamager().getUniqueId() );
            Mob victim = (Mob)event.getEntity();
            victim.setTarget( pet.getEntity() );
        }
    }

    @EventHandler
    public void onPetKillMob( EntityDamageByEntityEvent event){
        if( !(event.getDamager() instanceof LivingEntity) )
            return;
        //the target mob is not a pet
        if( !(event.getEntity() instanceof Mob) || dataManager.getPetsMap().containsKey( event.getEntity().getUniqueId() ) )
            return;

        //if damager is a pet
        if( dataManager.getPetsMap().containsKey( event.getDamager().getUniqueId() )  ){
            CustomEntity pet = (CustomEntity)dataManager.getPetsMap().get( event.getDamager().getUniqueId() );
            Mob victim = (Mob)event.getEntity();
            // check the victim is dead
            if( victim.getHealth() - event.getDamage() <= 0  ){
                //check is the pet have qeust progress
                pet.killingQuestMob( victim.getType() );
            }
        }
    }


    @EventHandler
    public void PetDeath( EntityDamageEvent event ){
        //check entity are living entity
        if( event.getEntity() instanceof LivingEntity ){
            LivingEntity livEntity = (LivingEntity)event.getEntity();
            //if entity dead
            if( livEntity.getHealth() - event.getDamage() <= 0  ){
                //check it's pet dead
                UUID uuid = livEntity.getUniqueId();
                if( dataManager.getPetsMap().containsKey( uuid ) ){
                    //get custom entity and excute dead
                    CustomEntity customEntity = dataManager.getPetsMap().get( uuid );
                    customEntity.dead();
                    event.setCancelled( true );
                    return;
                }
            }
        }
    }

    // update evey tick
    public void updaterTick(){
        BukkitScheduler scheduler = plugin.getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(plugin, () -> {
              try{
                for ( CustomEntity entity : dataManager.getPetsMap().values() ) {
                    entity.updateTick( plugin );
                }
            }
            catch(ConcurrentModificationException e){}
         }, 0L, 1L);
    }
}
