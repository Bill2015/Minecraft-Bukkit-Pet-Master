package com.bill.petmaster.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.bill.petmaster.manager.QuestManager;
import com.bill.petmaster.quest.EntityObjective;
import com.bill.petmaster.quest.ItemObjective;
import com.bill.petmaster.quest.PetObjective;
import com.bill.petmaster.quest.PetQuest;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class PetLevel {
    protected int level;
    protected PetQuest nowQuest;
    protected Map<String, PetObjective> objectives;

    public final static int QUEST_ITEM_COMSUME_DELAY = 100;

     /** construstor of PetLevel
     * @param level the initial level */
    public PetLevel( int level ){
        this.level          = level;
        this.nowQuest       = QuestManager.getPetQuests().get( level );

        objectives = nowQuest.cloneObjective();
    }
    /** construstor of PetLevel
     * @param level the initial level
     * @param progress the progress of qeust */
    public PetLevel( int level, List<Integer> progress){
        this.level          = level;
        this.nowQuest       = QuestManager.getPetQuests().get( level );

        //clone objective
        objectives = nowQuest.cloneObjective();
        //set qeust progress
        Iterator<Integer> pItor = progress.iterator();
        for ( PetObjective petObj : objectives.values()) {
            if( pItor == null || pItor.hasNext() == false || petObj == null )
                break;
            petObj.setProgress( pItor.next() );
        }
    }

    public Map<String, PetObjective> getObjectives() {
        return objectives;
    }
    public List<Integer> getProgress(){
        List<Integer> list = new ArrayList<>();
        for (PetObjective obj : objectives.values() ) {
            list.add( obj.getProgress() );
        }
        return list;
    }
    public PetQuest getNowQuest() {
        return nowQuest;
    }
    public int getLevel() {
        return level;
    }
    /** add pet level 
     * @param level level */
    public void addLevel( int level ){
        this.level += level;
        nowQuest = QuestManager.getPetQuests().get( level );
        // copy new objective
        objectives = nowQuest.cloneObjective();
    }
    /** set pet now level
     * @param level level */
    public void setLevel( int level ) {
        this.level = level;
        nowQuest = QuestManager.getPetQuests().get( level );
        // copy new objective
        objectives = nowQuest.cloneObjective();
    }

    /**
     * Consume quest item, only if the pet inventory contain it.
     * @param inventory pet's inventory
     * @return {@link Material} which item are consume, if nothing consumed return null*/
    public Material consumeQuestItem( Inventory inventory ){
        //judge the quest type
        if( nowQuest.getQuestType().equals( PetQuest.ITEM ) ){

            for(PetObjective petObj : objectives.values() ){
                Material material = ((ItemObjective)petObj).getMaterial();
                
                int slot = -1;
                //check this progress are finished
                if( petObj.isFinished() == false ){
                    //get quest item slot
                    if( (slot = inventory.first( material )) != -1 ){
                        //quest item decreased
                        inventory.setItem(slot, new ItemStack( material , inventory.getItem(slot).getAmount() - 1)  );
                        //quest progress add
                        petObj.addProgress( 1 );                    
                        return material;
                    }
                }
            }
           
        }
        return null;
    }
    /**
     * Consume quest item, only if the pet inventory contain it.
     * @param entityType the entity who killed by pet
     * @return {@link Material} which item are consume, if nothing consumed return null*/
    public boolean killingQuestMob( EntityType entityType ){
        //judge the quest type
        if( nowQuest.getQuestType().equals( PetQuest.ENTITY ) ){

            PetObjective petObj = objectives.get( entityType.toString() );
            if( petObj != null ){
                EntityType type = ((EntityObjective)petObj).getEntityType();

                //check the entity type
                if( entityType == type ){
                    //check this progress are finished
                    if( petObj.isFinished() == false ){
                        petObj.addProgress( 1 );  
                        return true;
                    }
                }
            }
        }
        return false;
    }
    /** Check this pet are finish all objective
     * @return {@link Boolean} true if complete, else false */
    public boolean checkLevelUp( ){
        for (PetObjective obj : objectives.values() ) {
            if( obj.isFinished() == false ){
                return false;
            }
        }
        return true;
    }
}
