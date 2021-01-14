package com.bill.petmaster.entity;

import java.util.Arrays;

import com.bill.petmaster.holder.PetMainMenuHolder;
import com.bill.petmaster.holder.PetSkillMenuHolder;
import com.bill.petmaster.util.EnitySkillPoint;
import com.bill.petmaster.util.EntityLevel;
import com.bill.petmaster.util.EntitySkill;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public abstract class CustomEntity {
    protected LivingEntity entity;
    protected Player owner;
    protected PetMainMenuHolder  menuHolder;
    protected PetSkillMenuHolder skillHolder;
    protected String name;

    protected EntityLevel entityLevel;
    protected float[] baseStatus;
    public CustomEntity(LivingEntity entity, Player owner){
        this.entity = entity;
        this.owner  = owner;
        this.name   = "MyPet";
        this.menuHolder     = new PetMainMenuHolder( this );
        this.skillHolder    = new PetSkillMenuHolder( this );
        this.entityLevel    = new EntityLevel( this );
        this.baseStatus     = new float[]{
            5.0f,   // Damage
            0.0f,   // Armor
            15.f,   // Health
            0.5f,   // Movement speed
            0.0f,   // Resistance
            20.0f,  // Food Capcity
            0       // Life Regen
        };
        entityLevel.getEntitySkill().addSkillPoint( (short)20 );

        updateStatus();
    }

    /** get this pet */
    public LivingEntity getEntity() {
        return entity;
    }
    /** get the owner of pet */
    public Player getOwner() {
        return owner;
    }
    /** get this pet main inventory gui */
    public PetMainMenuHolder getMenuHolder() {
        return menuHolder;
    }
    /** get this pet skill inventory gui */
    public PetSkillMenuHolder getSkillHolder() {
        return skillHolder;
    }
    /** get this entityLevel of pet  */
    public EntityLevel getEntityLevel() {
        return entityLevel;
    }
    public String getName() {
        return name;
    }

    public void updateStatus(){
        menuHolder.updateStatus();
        skillHolder.updateStatus();
        updateAttribute();
    }


    //設定所有的數值
    private void updateAttribute(){
        EntitySkill skill = entityLevel.getEntitySkill();
        //傷害  (% 數)
        entity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue( 
            skill.getValue( EnitySkillPoint.DAMAGE ) * baseStatus[ EnitySkillPoint.DAMAGE.get() ] 
        );
        //裝甲值 初始為0.0
        entity.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue( 
            skill.getValue( EnitySkillPoint.ARMOR ) + baseStatus[ EnitySkillPoint.ARMOR.get() ]
        );
        //最大血量 (% 數)
        entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue( 
            skill.getValue( EnitySkillPoint.HEALTH ) * baseStatus[ EnitySkillPoint.HEALTH.get() ] 
        );
        //移動速度 (% 數)
        entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue( 
            skill.getValue( EnitySkillPoint.SPEED ) * baseStatus[ EnitySkillPoint.SPEED.get() ] 
        );
        //擊退抗性 初始值0.0 最大值1.0 % 數
        entity.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue( 
            skill.getValue( EnitySkillPoint.RESIST ) * baseStatus[ EnitySkillPoint.RESIST.get() ]
        );
        //食物量
        //food.setMaxFoodLevel( skill.getResultAndCal( SKILL.FOOD ) );
        //回血速度  初始為0.0
        skill.getValue( EnitySkillPoint.REGEN );
    }
}
