package com.bill.petmaster.entity;

import java.util.Arrays;

import com.bill.petmaster.holder.PetInventoryHolder;
import com.bill.petmaster.holder.PetMainMenuHolder;
import com.bill.petmaster.holder.PetSkillMenuHolder;
import com.bill.petmaster.util.EnitySkillPoint;
import com.bill.petmaster.util.EntityFoodType;
import com.bill.petmaster.util.EntityHunger;
import com.bill.petmaster.util.EntityLevel;
import com.bill.petmaster.util.EntitySkill;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public abstract class CustomEntity {
    protected Mob entity;                           //current pet
    protected Player owner;                         //owner of pet (only player)
    protected PetMainMenuHolder  menuHolder;        //main menu of pet
    protected PetSkillMenuHolder skillHolder;       //skill menu of pet
    protected PetInventoryHolder inventoryHolder;   //inventory pf pet
    protected String name;                          //pet name

    protected EntityLevel entityLevel;              //pet level system
    protected float[] baseStatus;                   //pet base status

    protected int lifeRegenDelay;                   //pet life regen delay
    protected float lifeRegen;                      //life regen of this pet

    protected EntityHunger entityHunger;            //pet's hunger
    protected int foodDelay;                        //pet food delay
    protected int foodCosumeDelay;                  //pet cosume delay

    protected boolean isDead;

    public CustomEntity(Mob entity, Player owner){
        this.entity         = entity;
        this.owner          = owner;
        this.name           = "MyPet";
        this.isDead         = false;
        this.menuHolder     = new PetMainMenuHolder( this );
        this.skillHolder    = new PetSkillMenuHolder( this );
        this.inventoryHolder= new PetInventoryHolder( this );
        this.entityLevel    = new EntityLevel( this );
        this.entityHunger   = new EntityHunger( this, EntityFoodType.FISHMEAT, 20.0f );
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
    public Mob getEntity() {
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
    /** get this pet chest inventory gui */
    public PetInventoryHolder getInventoryHolder() {
        return inventoryHolder;
    }
    /** get this entityLevel of pet  */
    public EntityLevel getEntityLevel() {
        return entityLevel;
    }
    /** get this hunger of pet  */
    public EntityHunger getEntityHunger() {
        return entityHunger;
    }
    /** get this pet the name */
    public String getName() {
        return name;
    }
    /** is this pet are death */
    public boolean isDead() {
        return isDead;
    }

    /** set this entity are dead */
    public void dead() {
       isDead = true;
       entity.setAI( false );
       entity.setSilent( true );
       entity.setCollidable( false );
       entity.setInvulnerable( true );
       entity.setTarget( null );
       entity.setFireTicks( 0 );
       entity.addPotionEffect( new PotionEffect( PotionEffectType.INVISIBILITY , Integer.MAX_VALUE, 1) );
       owner.getWorld().playSound( owner.getLocation(), Sound.BLOCK_ANVIL_LAND, 1.0f, 1.25f);
       entity.setHealth( 0.01 );
       //create a dummy, let him looks like relly dead.
       Mob dummy = (Mob)entity.getWorld().spawnEntity(entity.getLocation() , entity.getType());
       dummy.setVelocity( entity.getVelocity() );
       dummy.damage( dummy.getAttribute( Attribute.GENERIC_MAX_HEALTH ).getValue() );
       owner.sendMessage( "[PetMaster] Your pet " + name + " is died."  );
    }

    /** revival the pet */
    public void revival(){
        isDead = false;
        entity.setAI( true );
        entity.setSilent( false );
        entity.setCollidable( true );
        entity.setInvulnerable( false );
        entity.removePotionEffect( PotionEffectType.INVISIBILITY  );
        entity.setHealth( entity.getAttribute( Attribute.GENERIC_MAX_HEALTH ).getValue() * 0.2 );
        owner.getWorld().playSound( owner.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 0.75f);
        owner.sendMessage( "[PetMaster] Revial your pet " + name + " sccuessfully."  );
    }

    /** a interface update */
    public void updateStatus(){
        menuHolder.updateStatus();
        skillHolder.updateStatus();
        updateAttribute();
    }

    /** a tick update */
    public void updateTick( Plugin plugin ){
        if( entity.isDead() == false && entity.isValid() ){
            lifeRegeneration();     //pet life regen
            updateHunger();         //pet getting hunger
            consumeFood( plugin );   //pet comsume food
            checkTarget();          //detect the target are valid
        }
    }


    /** update Attribute of pet */
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
        entityHunger.setMaxFoodLevel( skill.getValue( EnitySkillPoint.FOOD ) * baseStatus[ EnitySkillPoint.FOOD.get() ] );
        //回血速度  初始為0.0
        lifeRegen = skill.getValue( EnitySkillPoint.REGEN );
    }

    
    /** add healt to pet, have bound check 
     *  @param val value of heath (can negative)*/
    private void addHealth(double val){
        //假如不超過血量就繼續加
        if( val > 0){
            entity.setHealth( Math.min(  entity.getHealth() + val , entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() )  );
        }
        //血量減少
        else{
            entity.setHealth( Math.max( (entity.getHealth() + val), 0 )  );
        }
    }

    //=========================================================================================
    //=======================             enity updater             ===========================
    /** the life regeneration of pet, every tick check once */
    private void lifeRegeneration(){
        if( entityLevel.getEntitySkill().getValue( EnitySkillPoint.REGEN ) != 0){
            if( lifeRegenDelay++ >= (1200 / lifeRegen ) ){
                //必須要有飽食度
                if( !entityHunger.isHunger() ){
                    addHealth( 1.0 ); 
                    //回血時 增加飽食度消耗
                    if( entity.getHealth() < entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() ){
                        foodDelay += 5;
                    }
                    updateStatus();             //更新狀態
                }
                lifeRegenDelay = 0;
            }
        }
    }

    private void consumeFood( Plugin plugin ){
        if( (foodCosumeDelay += 1) >= EntityHunger.FOOD_COSUME_TIME ){
            foodCosumeDelay = 0;
            //消耗食物 回復飽食度
            Material isEeating = entityHunger.consumeFood( inventoryHolder.getInventory() );
            if(isEeating != null){  
                for(int i = 0; i < 9; i++){
                    final int j = i;
                    new BukkitRunnable(){
                        @Override
                        public void run() {
                            entity.getWorld().spawnParticle(Particle.ITEM_CRACK, entity.getEyeLocation(), 5, 0.05, 0.05, 0.05, 0.025, new ItemStack( isEeating ), false);
                            entity.getWorld().playSound( entity.getEyeLocation(), Sound.ENTITY_CAT_EAT, SoundCategory.AMBIENT, 1.0f, 1.0f );
                            if(j == 8)entity.getWorld().playSound( entity.getEyeLocation(), Sound.ENTITY_FOX_EAT, SoundCategory.AMBIENT, 1.0f, 1.0f );
                            this.cancel();
                        }
                    }.runTaskLaterAsynchronously(plugin, i * 4 );
                }
            }
        }
    }
    /** the hunger status of pet, every tick check once */
    private void updateHunger(){
        //假如還有飽食度
        if( entityHunger.getFoodValue() > 0 ){
            //每半小時扣 1 點 飽食度
            if( (foodDelay += 1) >= EntityHunger.DECREASE_FOOD_TIME ){
                foodDelay = 0;
                entityHunger.addFoodValue( -1.0f );
                updateStatus();
            }
        }
        //假如沒有飽食度
        else{
            //每 1 分鐘扣 1 點血量
            if( (foodDelay += 1) >= EntityHunger.DECREASE_LIFE_TIME ){
                foodDelay = 0;
                entity.damage( 1.0 );
                updateStatus();
            }
        }
    }
    /** check the target are death already */
    protected abstract void checkTarget();
}
