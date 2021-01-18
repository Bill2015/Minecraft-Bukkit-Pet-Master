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
import com.bill.petmaster.holder.PetSkillMenuHolder;
import com.bill.petmaster.manager.DataManager;
import com.bill.petmaster.manager.ItemManeger;

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
    public PetEvent(App plugin, DataManager dataManager, ItemManeger itemManeger){
        this.plugin = plugin;
        this.dataManager = dataManager;
        this.itemManeger = itemManeger;
        updaterTick();
    }    

    @EventHandler
    public void onPlayerUsePet(InventoryOpenEvent event ){
        Player player = (Player)event.getPlayer();
        // check is null
        if( event.getInventory().getHolder() != null ){
            InventoryHolder holder = event.getInventory().getHolder();

            //check  player open pet inventory
            if( holder instanceof PetInventoryHolder ){
                PetInventoryHolder inventoryHolder = (PetInventoryHolder)holder;
                if( inventoryHolder.isOpen() == false ){
                    inventoryHolder.setOpen( true );
                    player.openInventory( inventoryHolder.getInventory() );
                }
            }
        }
    }

    @EventHandler
    public void onPlayerUsePet( InventoryClickEvent event ){
        Player player = (Player)event.getWhoClicked();
        // check is null
        if( event.getInventory().getHolder() != null ){
            InventoryHolder holder = event.getInventory().getHolder();
            // judge which holder
            // player on main menu
            if( holder instanceof PetMainMenuHolder ){
                PetMainMenuHolder mainHolder = (PetMainMenuHolder)holder;
                if( event.getRawSlot() == PetMainMenuHolder.SKILL_SLOT ){
                    mainHolder.updateStatus();
                    player.openInventory( mainHolder.getOwner().getSkillHolder().getInventory() );  
                }
                if( event.getRawSlot() == PetMainMenuHolder.CHEST_SLOT ){
                    mainHolder.updateStatus();
                    player.openInventory( mainHolder.getOwner().getInventoryHolder().getInventory()  );
                }
                event.setCancelled( true );
                return;
            }
            // player use skill point
            else if( holder instanceof PetSkillMenuHolder ){
                PetSkillMenuHolder skillHolder = (PetSkillMenuHolder)holder;
                skillHolder.getOwner().getPetLevel().useSkillPoint( event.getRawSlot() );
                event.setCancelled( true );
                return;
            }
           
        }
    }

    @EventHandler
    public void onPlayerStopUsePet( InventoryCloseEvent event ){
        Player player = (Player)event.getPlayer();
        // judge is null
        if( event.getInventory().getHolder() != null ){
            InventoryHolder holder = event.getInventory().getHolder();
            // check is pet's inventory
            if( holder instanceof PetInventoryHolder ){

                PetInventoryHolder inventoryHolder = (PetInventoryHolder)holder;
                if( inventoryHolder.isOpen() == true ){
                    inventoryHolder.setOpen( false );
                }
            }
        }
    }

    @EventHandler
    public void onPlayerClaimAnimal( PlayerInteractEntityEvent event ){
        Entity entity = event.getRightClicked();
        Player player = event.getPlayer();
        if( entity == null )
            return;
        
        // the player must be hold the PetStick in hand
        if( player.getInventory().getItemInMainHand() == null ||
                player.getInventory().getItemInMainHand().getItemMeta() == null ||
                    !player.getInventory().getItemInMainHand().equals( itemManeger.getPetStick() ) ){
            return;
        }

        // must a cat
        if( entity.getType() == EntityType.CAT ){
            if( ((Cat)entity).getOwner() != null ){
                //need to same owner
                if( ((Cat)entity).getOwner().getUniqueId().equals( event.getPlayer().getUniqueId() ) ){

                    // Judge is That pet are claimed
                    if( dataManager.getPetsMap().containsKey( entity.getUniqueId() ) ){
                        MasterCat cat = (MasterCat)dataManager.getPetsMap().get( entity.getUniqueId() );
                        cat.updateStatus();
                        player.openInventory( cat.getMenuHolder().getInventory() );
                    }
                    else{
                        MasterCat cat = new MasterCat( ((Cat)entity), event.getPlayer() );
                        dataManager.getPetsMap().put( entity.getUniqueId(), cat );
                        player.openInventory( cat.getMenuHolder().getInventory() );
                    }
                }
            }
        }
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
