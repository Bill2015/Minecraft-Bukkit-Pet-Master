package com.bill.petmaster.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import com.bill.petmaster.App;
import com.bill.petmaster.entity.CustomEntity;
import com.bill.petmaster.entity.MasterCat;
import com.bill.petmaster.holder.PetMainMenuHolder;
import com.bill.petmaster.holder.PetSkillMenuHolder;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.InventoryHolder;

public class PetEvent implements Listener{
    private App plugin;
    private HashMap<UUID, CustomEntity> pets = new HashMap<>();
    private ArrayList<CustomEntity> ownPets = new ArrayList<>();
    public PetEvent(App plugin){
        this.plugin = plugin;
    }    

    @EventHandler 
    public void onPlayerUsePet( InventoryClickEvent event ){
        Player player = (Player)event.getWhoClicked();
        // judge is null
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
                event.setCancelled( true );
                return;
            }
            // player use skill point
            else if( holder instanceof PetSkillMenuHolder ){
                PetSkillMenuHolder skillHolder = (PetSkillMenuHolder)holder;
                skillHolder.getOwner().getEntityLevel().useSkillPoint( event.getRawSlot() );
                event.setCancelled( true );
                return;
            }
        }
    }

    @EventHandler
    public void onPlayerClaimAnimal( PlayerInteractEntityEvent event ){
        Entity entity = event.getRightClicked();
        Player player = event.getPlayer();
        if( entity == null )
            return;
        
        // must a cat
        if( entity.getType() == EntityType.CAT ){
            if( ((Cat)entity).getOwner() != null ){
                //need to same owner
                if( ((Cat)entity).getOwner().getUniqueId().equals( event.getPlayer().getUniqueId() ) ){

                    // Judge is That pet are claimed
                    if( pets.containsKey( entity.getUniqueId() ) ){
                        MasterCat cat = (MasterCat)pets.get( entity.getUniqueId() );
                        cat.updateStatus();
                        player.openInventory( cat.getMenuHolder().getInventory() );
                    }
                    else{
                        MasterCat cat = new MasterCat( ((Cat)entity), event.getPlayer() );
                        pets.put( entity.getUniqueId(), cat );
                        ownPets.add( cat );
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
            for (CustomEntity pet : ownPets) {
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
    public void onPetKillMob( EntityDeathEvent event ){
        if( event.getEntity() instanceof LivingEntity ){
            if( event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent ){
                EntityDamageByEntityEvent ndnEvent = (EntityDamageByEntityEvent)event.getEntity().getLastDamageCause();

                if( ndnEvent.getDamager() instanceof Cat ){
                    if( pets.containsKey( ndnEvent.getDamager().getUniqueId() ) ){
                        CustomEntity entity = pets.get( ndnEvent.getDamager().getUniqueId() );
                        ((Cat)entity.getEntity()).setTarget( null );
                    }
                }
            }
        } 
    }
    
}
