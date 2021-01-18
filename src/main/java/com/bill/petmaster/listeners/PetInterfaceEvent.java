package com.bill.petmaster.listeners;

import com.bill.petmaster.App;
import com.bill.petmaster.entity.MasterCat;
import com.bill.petmaster.holder.PetAttributeMenuHolder;
import com.bill.petmaster.holder.PetInventoryHolder;
import com.bill.petmaster.holder.PetMainMenuHolder;
import com.bill.petmaster.holder.PetQuestHolder;
import com.bill.petmaster.manager.DataManager;
import com.bill.petmaster.manager.ItemManeger;
import com.bill.petmaster.manager.QuestManager;

import org.bukkit.entity.Cat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.InventoryHolder;

public class PetInterfaceEvent implements Listener {
    private App plugin;
    private DataManager dataManager;
    private ItemManeger itemManeger;
    private QuestManager questManager;
    public PetInterfaceEvent(App plugin, DataManager dataManager, ItemManeger itemManeger, QuestManager questManager){
        this.plugin = plugin;
        this.dataManager    = dataManager;
        this.itemManeger    = itemManeger;
        this.questManager   = questManager;
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
                if( event.getRawSlot() == PetMainMenuHolder.ATTRI_SLOT ){
                    mainHolder.updateStatus();
                    player.openInventory( mainHolder.getOwner().getAttributeHolder().getInventory() );  
                }
                if( event.getRawSlot() == PetMainMenuHolder.CHEST_SLOT ){
                    mainHolder.updateStatus();
                    player.openInventory( mainHolder.getOwner().getInventoryHolder().getInventory()  );
                }
                if( event.getRawSlot() == PetMainMenuHolder.QUEST_SLOT ){
                    mainHolder.updateStatus();
                    player.openInventory( mainHolder.getOwner().getQuestHolder().getInventory()  );
                }
                event.setCancelled( true );
                return;
            }
            // player use attribute point
            else if( holder instanceof PetAttributeMenuHolder ){
                PetAttributeMenuHolder attributeHolder = (PetAttributeMenuHolder)holder;
                attributeHolder.getOwner().getPetLevel().useAttributePoint( event.getRawSlot() );
                event.setCancelled( true );
                return;
            }
            // player view qeust 
            else if( holder instanceof PetQuestHolder ){
                PetQuestHolder questHolder = (PetQuestHolder)holder;
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
                        MasterCat cat = new MasterCat( ((Cat)entity), event.getPlayer(), questManager.getQuests()  );
                        dataManager.getPetsMap().put( entity.getUniqueId(), cat );
                        player.openInventory( cat.getMenuHolder().getInventory() );
                    }
                }
            }
        }
    }
}
