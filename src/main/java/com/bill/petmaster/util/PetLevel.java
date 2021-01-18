package com.bill.petmaster.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.bill.petmaster.entity.CustomEntity;
import com.bill.petmaster.holder.PetSkillMenuHolder;
import com.bill.petmaster.quest.ItemObjective;
import com.bill.petmaster.quest.ItemQuestMap;
import com.bill.petmaster.quest.PetObjective;
import com.bill.petmaster.quest.PetQuest;
import com.bill.petmaster.quest.QeustMap;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Mob;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class PetLevel {
    protected int level;
    protected PetSkill petSkill;
    protected PetQuest nowQuest;
    protected PetObjective objectives[];
    
    protected final CustomEntity owner;
    protected final List<PetQuest> petQuests;

    public final static int QUEST_ITEM_COMSUME_DELAY = 100;
    private final static float MUTIPLE[] = {5.0f, 2.0f, 10.0f, 5.0f, 5.0f, 10.0f, 1.0f};
    public PetLevel(CustomEntity owner, List<PetQuest> petQuests){
        this.owner          = owner;
        this.petQuests      = petQuests;
        this.level          = 1;
        this.petSkill       = new PetSkill( MUTIPLE );
        this.nowQuest       = petQuests.get( level - 1 );

        QeustMap<? extends PetObjective> objMap = nowQuest.getQuestObjective();
        objectives = new PetObjective[ objMap.size() ];
        objectives = Arrays.copyOf( objMap.values().toArray( objectives ), objMap.size() );
    }


    public PetSkill getPetSkill() {
        return petSkill;
    }
    public PetObjective[] getObjectives() {
        return objectives;
    }
    public PetQuest getNowQuest() {
        return nowQuest;
    }
    public int getLevel() {
        return level;
    }
    //點屬性點 (傳遞位置進來判斷)
    public void useSkillPoint( int slot ){
        if( slot != PetSkillMenuHolder.RESET_SLOT && petSkill.getUnUsedPoint() > 0 ){
            switch (slot) {
                case PetSkillMenuHolder.DAMAGE_SLOT:
                    petSkill.addPoint( PetSkillPoint.DAMAGE );break;     //傷害(點)
                case PetSkillMenuHolder.ARMOR_SLOT:
                    petSkill.addPoint( PetSkillPoint.ARMOR ); break;     //裝甲值(點)
                case PetSkillMenuHolder.HEALTH_SLOT:
                    petSkill.addPoint( PetSkillPoint.HEALTH );break;     //最大血量(點)
                case PetSkillMenuHolder.SPEED_SLOT:
                    petSkill.addPoint( PetSkillPoint.SPEED ); break;     //移動速度(點)
                case PetSkillMenuHolder.RESIST_SLOT:
                    petSkill.addPoint( PetSkillPoint.RESIST );break;     //擊退抗性(點)
                case PetSkillMenuHolder.FOOD_SLOT:
                    petSkill.addPoint( PetSkillPoint.FOOD );  break;     //食物量(點)
                case PetSkillMenuHolder.REGEN_SLOT:
                    petSkill.addPoint( PetSkillPoint.REGEN ); break;     //回血速度(點)
                default: break;
            }
            owner.getEntity().getWorld().playSound( owner.getEntity().getLocation() , Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.AMBIENT, 1.0f, 1.0f);
            owner.updateStatus();
        }
        else if( slot == PetSkillMenuHolder.RESET_SLOT ){
            //TODO
            //if( level.reset( playerInv ) ) entity.getWorld().playSound(entity.getLocation() , Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.AMBIENT, 1.0f, 1.0f);
        }
    }
    /**
     * Consume quest item, only if the pet inventory contain it.
     * @param inventory pet's inventory
     * @return {@link Material} which item are consume, if nothing consumed return null*/
    public Material consumeQuestItem( Inventory inventory ){
        //judge that quest type
        if( nowQuest.getQuestType().equals( PetQuest.ITEM ) ){

            int objCount = 0;
            for(PetObjective petObj : objectives ){
                Material material = ((ItemObjective)petObj).getMaterial();
                
                int slot = -1;
                //check this progress are finished
                if( objectives[ objCount ].isFinished() == false ){
                    //get quest item slot
                    if( (slot = inventory.first( material )) != -1 ){
                        //quest item decreased
                        inventory.setItem(slot, new ItemStack( material , inventory.getItem(slot).getAmount() - 1)  );
                        //quest progress add
                        objectives[ objCount ].addProgress( 1 );                    
                        return material;
                    }
                }

                objCount += 1;
            }
           
        }
        return null;
    }
    /** Check this pet are finish all objective
     * @return {@link Boolean} true if complete, else false */
    public boolean checkLevelUp( ){
        for (PetObjective obj : objectives) {
            if( obj.isFinished() == false ){
                return false;
            }
        }
        return true;
    }
    /** The level up funtion */
    public void levelUp(){
        //create effect
        Mob entity = owner.getEntity();
        entity.getWorld().playSound( entity.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        entity.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, entity.getLocation().add(0,0.5,0), 20, 0.4, 0.4, 0.4, 0.25, null, true);
        
        // setting
        level += 1;
        nowQuest = petQuests.get( level - 1 );
        // copy new objective
        QeustMap<? extends PetObjective> objMap = nowQuest.getQuestObjective();
        objectives = new PetObjective[ objMap.size() ];
        objectives = Arrays.copyOf( objMap.values().toArray( objectives ), objMap.size() );
    }

}
