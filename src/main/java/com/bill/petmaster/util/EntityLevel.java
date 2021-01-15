package com.bill.petmaster.util;

import com.bill.petmaster.entity.CustomEntity;
import com.bill.petmaster.holder.PetSkillMenuHolder;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.LivingEntity;

public class EntityLevel {
    protected EntitySkill entitySkill;
    protected CustomEntity owner;
    private final static float MUTIPLE[] = {5.0f, 2.0f, 10.0f, 5.0f, 5.0f, 10.0f, 1.0f};
    public EntityLevel(CustomEntity owner){
        this.owner = owner;
        this.entitySkill  = new EntitySkill( MUTIPLE );
    }

    //點屬性點 (傳遞位置進來判斷)
    public void useSkillPoint( int slot ){
        if( slot != PetSkillMenuHolder.RESET_SLOT && entitySkill.getUnUsedPoint() > 0 ){
            switch (slot) {
                case PetSkillMenuHolder.DAMAGE_SLOT:
                    entitySkill.addPoint( EnitySkillPoint.DAMAGE );break;     //傷害(點)
                case PetSkillMenuHolder.ARMOR_SLOT:
                    entitySkill.addPoint( EnitySkillPoint.ARMOR ); break;     //裝甲值(點)
                case PetSkillMenuHolder.HEALTH_SLOT:
                    entitySkill.addPoint( EnitySkillPoint.HEALTH );break;     //最大血量(點)
                case PetSkillMenuHolder.SPEED_SLOT:
                    entitySkill.addPoint( EnitySkillPoint.SPEED ); break;     //移動速度(點)
                case PetSkillMenuHolder.RESIST_SLOT:
                    entitySkill.addPoint( EnitySkillPoint.RESIST );break;     //擊退抗性(點)
                case PetSkillMenuHolder.FOOD_SLOT:
                    entitySkill.addPoint( EnitySkillPoint.FOOD );  break;     //食物量(點)
                case PetSkillMenuHolder.REGEN_SLOT:
                    entitySkill.addPoint( EnitySkillPoint.REGEN ); break;     //回血速度(點)
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
    public EntitySkill getEntitySkill() {
        return entitySkill;
    }
}
