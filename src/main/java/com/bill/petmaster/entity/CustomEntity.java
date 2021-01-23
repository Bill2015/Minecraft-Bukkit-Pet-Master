package com.bill.petmaster.entity;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.bill.petmaster.holder.PetInventoryHolder;
import com.bill.petmaster.holder.PetMainMenuHolder;
import com.bill.petmaster.holder.PetQuestHolder;
import com.bill.petmaster.manager.ConfigManager;
import com.bill.petmaster.manager.QuestManager;
import com.bill.petmaster.holder.PetAttributeMenuHolder;
import com.bill.petmaster.quest.PetObjective;
import com.bill.petmaster.quest.PetQuest;
import com.bill.petmaster.util.AttributePoint;
import com.bill.petmaster.util.PetFoodType;
import com.bill.petmaster.util.PetHunger;
import com.bill.petmaster.util.PetLevel;
import com.bill.petmaster.util.PetAttribute;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

public abstract class CustomEntity {
    protected Mob entity;                               //current pet
    protected OfflinePlayer owner;                      //owner of pet (only player)
    protected PetMainMenuHolder  menuHolder;            //main menu of pet
    protected PetAttributeMenuHolder attributeHolder;   //Attribute menu of pet
    protected PetInventoryHolder inventoryHolder;       //inventory pf pet
    protected PetQuestHolder     questHolder;           //quest menu of pet
    protected String name;                              //pet name

    protected PetAttribute petAttribute;                //pet Attribute

    protected PetLevel petLevel;                        //pet level system
    private int questItemDelay;                         //pet comsume quest item delay

    protected int lifeRegenDelay;                       //pet life regen delay
    protected float lifeRegen;                          //life regen of this pet

    protected PetHunger petHunger;                      //pet's hunger
    protected int foodDelay;                            //pet food delay
    protected int foodCosumeDelay;                      //pet cosume delay

    protected boolean isDead;

    public final static double CHASE_TARGE_DISTANCE = 16.0;

    protected CustomEntity( Mob entity, OfflinePlayer owner, String name, boolean isDead, PetAttribute petAttribute, PetLevel petLevel, PetHunger petHunger, ItemStack[] items ){
        this.entity = entity;
        this.owner  = owner;
        this.name   = name;
        this.isDead = isDead;
        this.petAttribute = petAttribute;
        this.petLevel     = petLevel;
        this.petHunger    = petHunger;
        // holder
        this.menuHolder         = new PetMainMenuHolder( this );
        this.attributeHolder    = new PetAttributeMenuHolder( this );
        this.questHolder        = new PetQuestHolder(); 
        this.inventoryHolder    = new PetInventoryHolder( this );
        this.inventoryHolder.getInventory().setContents( items );

        updateStatus();
    }

    /** get this pet */
    public Mob getEntity() {
        return entity;
    }
    /** get the owner of pet */
    public OfflinePlayer getOwner() {
        return owner;
    }
    /** get this pet main inventory gui */
    public PetMainMenuHolder getMenuHolder() {
        return menuHolder;
    }
    /** get this pet attribute inventory gui */
    public PetAttributeMenuHolder getAttributeHolder() {
        return attributeHolder;
    }
    /** get this pet chest inventory gui */
    public PetInventoryHolder getInventoryHolder() {
        return inventoryHolder;
    }
    /** get this qeust of pet */
    public PetQuestHolder getQuestHolder(){
        return questHolder;
    }
    /** get this entityLevel of pet  */
    public PetLevel getPetLevel() {
        return petLevel;
    }
    /** get this attribute of pet  */
    public PetAttribute getPetAttribute() {
        return petAttribute;
    }
    /** get this hunger of pet  */
    public PetHunger getPetHunger() {
        return petHunger;
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
       Location lastLocation = entity.getLocation();
       
       entity.setHealth( 0.01 );
       //create a dummy, let him looks like relly dead.
       Mob dummy = (Mob)entity.getWorld().spawnEntity( lastLocation , entity.getType());
       dummy.setVelocity( entity.getVelocity() );
       dummy.damage( dummy.getAttribute( Attribute.GENERIC_MAX_HEALTH ).getValue() );

        //prevent player or monster keep attacking or blocking way(TODO:, this is a temperating way)
        entity.teleport( new Location( entity.getWorld() ,lastLocation.getX(),  255, lastLocation.getZ() ) );

        //sending message to online owner
        if( owner.isOnline() ){
            Player player = owner.getPlayer();
            player.sendMessage( "[PetMaster] Your pet " + name + " is died."  );
            player.getWorld().playSound( player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1.0f, 1.25f);
        }
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

        //sending message to online owner and teleport pet
        if( owner.isOnline() ){
            Player player = owner.getPlayer();        
            player.getWorld().playSound( player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 0.75f);
            player.sendMessage( "[PetMaster] Revial your pet " + name + " sccuessfully."  );
        }
    }

    /** a interface update */
    public void updateStatus(){
        menuHolder.updateStatus();
        attributeHolder.updateStatus();
        updateAttribute();
    }

    /** a tick update */
    public void updateTick( Plugin plugin ){
        if( isDead == false && entity.isValid() ){
            lifeRegeneration();     //pet life regen
            updateHunger();         //pet getting hunger
            consumeFood( plugin );  //pet consume food
            checkTarget();          //detect the target are valid
            comsumeQuestItem( plugin );     //pet consume quest item
        }
    }


    /** update Attribute of pet */
    private void updateAttribute(){
        //傷害  (% 數)
        entity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue( 
            petAttribute.getValue( AttributePoint.DAMAGE )
        );
        //裝甲值 初始為0.0
        entity.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue( 
            petAttribute.getValue( AttributePoint.ARMOR )
        );
        //最大血量 (% 數)
        entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue( 
            petAttribute.getValue( AttributePoint.HEALTH )
        );
        //移動速度 (% 數)
        entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue( 
            petAttribute.getValue( AttributePoint.SPEED )
        );
        //擊退抗性 初始值0.0 最大值1.0 % 數
        entity.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue( 
            petAttribute.getValue( AttributePoint.RESIST )
        );
        //食物量
        petHunger.setMaxFoodLevel( petAttribute.getValue( AttributePoint.FOOD ) );
        //回血速度  初始為0.0
        lifeRegen = petAttribute.getValue( AttributePoint.REGEN );
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

    private void levelUp(){
        //create effect
        entity.getWorld().playSound( entity.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        entity.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, entity.getLocation().add(0,0.5,0), 20, 0.4, 0.4, 0.4, 0.25, null, true);
        
        //add attribute point into PetAttribute 
        petAttribute.addAttributePoint( (short)petLevel.getNowQuest().getPoint() );

        // setting
        petLevel.addLevel( 1 );
        attributeHolder.updateStatus();
    };

    
    //點屬性點 (傳遞位置進來判斷)
    public void useAttributePoint( int slot ){
        if( slot != PetAttributeMenuHolder.RESET_SLOT && petAttribute.getUnUsedPoint() > 0 ){
            switch (slot) {
                case PetAttributeMenuHolder.DAMAGE_SLOT:
                    petAttribute.addPoint( AttributePoint.DAMAGE );break;     //傷害(點)
                case PetAttributeMenuHolder.ARMOR_SLOT:
                    petAttribute.addPoint( AttributePoint.ARMOR ); break;     //裝甲值(點)
                case PetAttributeMenuHolder.HEALTH_SLOT:
                    petAttribute.addPoint( AttributePoint.HEALTH );break;     //最大血量(點)
                case PetAttributeMenuHolder.SPEED_SLOT:
                    petAttribute.addPoint( AttributePoint.SPEED ); break;     //移動速度(點)
                case PetAttributeMenuHolder.RESIST_SLOT:
                    petAttribute.addPoint( AttributePoint.RESIST );break;     //擊退抗性(點)
                case PetAttributeMenuHolder.FOOD_SLOT:
                    petAttribute.addPoint( AttributePoint.FOOD );  break;     //食物量(點)
                case PetAttributeMenuHolder.REGEN_SLOT:
                    petAttribute.addPoint( AttributePoint.REGEN ); break;     //回血速度(點)
                default: break;
            }
            entity.getWorld().playSound( entity.getLocation() , Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.AMBIENT, 1.0f, 1.0f);
            updateStatus();
        }
        else if( slot == PetAttributeMenuHolder.RESET_SLOT ){
            //TODO
            //if( level.reset( playerInv ) ) entity.getWorld().playSound(entity.getLocation() , Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.AMBIENT, 1.0f, 1.0f);
        }
    }
    //=========================================================================================
    //=======================             event updater             ===========================
    public void killingQuestMob( EntityType entityType ){
        if( petLevel.killingQuestMob( entityType ) == true ){
            // check is complete
            if( petLevel.checkLevelUp() ){
                questHolder.updateItem( petLevel, true );
                levelUp();
            }
            else {
                entity.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, entity.getEyeLocation(), 10, 0.1, 0.1, 0.1, 0.025, null, false);
                entity.getWorld().playSound( entity.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP , 1.0f, 1.0f );
                entity.getWorld().playSound( entity.getEyeLocation(), Sound.ENTITY_ITEM_PICKUP , 1.0f, 1.0f );
                questHolder.updateItem( petLevel, false );
                
            } 
        }
    }

    //=========================================================================================
    //=======================             enity updater             ===========================
    private void comsumeQuestItem( Plugin plugin ){
        if( questItemDelay++ >= PetLevel.QUEST_ITEM_COMSUME_DELAY ){
            questItemDelay = 0;
            // consume quest item
            Material material = null;
            if( (material = petLevel.consumeQuestItem( inventoryHolder.getInventory() ) ) != null ){
                // check is complete
                if( petLevel.checkLevelUp() ){
                    questHolder.updateItem( petLevel, true );
                    levelUp();
                }
                else {
                    entity.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, entity.getEyeLocation(), 10, 0.1, 0.1, 0.1, 0.025, null, false);
                    entity.getWorld().playSound( entity.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP , 1.0f, 1.0f );
                    entity.getWorld().playSound( entity.getEyeLocation(), Sound.ENTITY_ITEM_PICKUP , 1.0f, 1.0f );
                    questHolder.updateItem( petLevel, false );
                    
                    Item item = entity.getWorld().dropItemNaturally( entity.getLocation(),  new ItemStack( material ) );
                    item.setPickupDelay( Integer.MAX_VALUE );
                    item.setVelocity( new Vector(0.0, 0.25, 0.0) );
                    
                    new BukkitRunnable(){
                        @Override public void run(){
                            item.remove();
                        }
                    }.runTaskLater(plugin, 10);
                } 
            }
        }
    }
    /** the life regeneration of pet, every tick check once */
    private void lifeRegeneration(){
        if( petAttribute.getValue( AttributePoint.REGEN ) != 0){
            lifeRegenDelay = 0;
            if( lifeRegenDelay++ >= (1200 / lifeRegen ) ){
                //必須要有飽食度
                if( !petHunger.isHunger() ){
                    addHealth( 1.0 ); 
                    //回血時 增加飽食度消耗
                    if( entity.getHealth() < entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() ){
                        foodDelay += 5;
                    }
                    menuHolder.updateStatus();             //更新狀態
                }
            }
        }
    }

    private void consumeFood( Plugin plugin ){
        if( (foodCosumeDelay += 1) >= PetHunger.FOOD_COSUME_TIME ){
            foodCosumeDelay = 0;
            if( petHunger.isFull() == false ){
                //消耗食物 回復飽食度
                Material isEeating = petHunger.consumeFood( inventoryHolder.getInventory() );
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
    }
    /** the hunger status of pet, every tick check once */
    private void updateHunger(){
        //假如還有飽食度
        if( petHunger.getFoodValue() > 0 ){
            //每半小時扣 1 點 飽食度
            if( (foodDelay += 1) >= PetHunger.DECREASE_FOOD_TIME ){
                foodDelay = 0;
                petHunger.addFoodValue( -1.0f );
                menuHolder.updateStatus();
            }
        }
        //假如沒有飽食度
        else{
            //每 1 分鐘扣 1 點血量
            if( (foodDelay += 1) >= PetHunger.DECREASE_LIFE_TIME ){
                foodDelay = 0;
                entity.damage( 1.0 );
                menuHolder.updateStatus();
            }
        }
    }
    /** check the target are death already */
    private void checkTarget(){
        if( entity.getTarget() != null ){
            LivingEntity target = entity.getTarget();

            if( target.isDead() == true || target.isValid() == false ){
                entity.setTarget( null );
            }
            else{
                if( entity.getLocation().distance( target.getLocation() ) > CHASE_TARGE_DISTANCE ){
                    entity.setTarget( null );
                }
            }
        }
    }

    //=========================================================================================
    /** generte the custom entity bulider 
     *  @param entity target entity
     *  @param owner the entity owner
     *  @return Custom entity builder */
    public static CustomEntityBuilder getBuilder( Mob entity, OfflinePlayer owner ){
        return new CustomEntityBuilder( entity, owner );
    }
    //=========================================================================================
    //=======================             enity builder             ===========================
    public static class CustomEntityBuilder{
        protected Mob entity;                               //current pet
        protected OfflinePlayer owner;                      //owner of pet (only player)
        protected String name;                              //pet name
        protected boolean isDead;                           //pet status
        //---------- attribute --------------
        protected short unusedPoint;                        //unused point
        protected short[] point;                            //attribute points
        //---------- level --------------
        protected int level;                                //pet's level
        protected List<Integer> progress;                   //pet's quest progress
        //---------- food ---------------
        protected PetFoodType foodType;                     //the type of pet food
        protected float foodValue;                          //the default food value
        //-------- inventory ------------
        protected ItemStack[] items;                        //the inventory of pet
        
        public CustomEntityBuilder( Mob entity, OfflinePlayer owner ){
            //setting default value
            this.entity         = entity;
            this.owner          = owner;
            this.name           = "MyPet";
            this.isDead         = false;
            this.point          = new short[7];
            this.unusedPoint    = 0;
            this.level          = 1;
            this.progress       = new ArrayList<>();
            this.foodType       = PetFoodType.FISHMEAT;
            this.foodValue      = 5.0f;
            this.items          = new ItemStack[1];
        }
        /** Intance a Pet Class which is extanded the {@link CustomEntity} class, otherwise return null;
         * @param petClass a class def which extanded the {@link CustomEntity} class
         * @return a {@link CustomEntity} class, if faided return null */
        public CustomEntity build( Class<?> petClass ){
            float[] defaultValue      = ConfigManager.getPetsBaseValues( entity.getType() );
            float[] growthValue       = ConfigManager.getPetsGrowthValues( entity.getType() );
            PetAttribute attribute = new PetAttribute(point, defaultValue, growthValue, unusedPoint);
            
            PetLevel petLevel = ( progress == null ) ? new PetLevel( level ) : new PetLevel( level, progress );

            PetHunger petHunger = new PetHunger( foodType, foodValue );

            //TODO: it could be better , maybe?
            CustomEntity customEntity = null;
            try {
                customEntity = (CustomEntity)petClass.getDeclaredConstructors()[0].newInstance(entity, owner, name, isDead, attribute, petLevel, petHunger, items);
            } catch (IllegalAccessException | IllegalArgumentException | InstantiationException | InvocationTargetException e) {
                e.printStackTrace();
            }
            return customEntity;
        }
        //--------------------------------------------
        //-------------- attribute point -------------
        public CustomEntityBuilder setUnusedPoint( short unusedPoint ){
            this.unusedPoint = unusedPoint;
            return this;
        }
        public CustomEntityBuilder setAttributePoint( List<Short> pointList ){
            point = new short[ pointList.size() ];
            int i = 0;
            for (Short s : pointList) point[ i++ ] = s;
            return this;
        }
        //--------------------------------------------
        //------------------ base --------------------
        public CustomEntityBuilder setName( String name ){
            this.name = name;
            return this;
        }
        public CustomEntityBuilder setDead( boolean dead ){
            this.isDead = dead;
            return this;
        }
        //--------------------------------------------
        //----------------- level --------------------
        public CustomEntityBuilder setLevel( int level ){
            this.level  = level;
            return this;
        }
        public CustomEntityBuilder setObjective( List<Integer> progress ){
            this.progress = progress;
            return this;
        }
        //--------------------------------------------
        //------------------ food --------------------
        public CustomEntityBuilder setFoodType( PetFoodType foodType ){
            this.foodType = foodType;
            return this;
        }
        public CustomEntityBuilder setFoodValue( float foodValue ){
            this.foodValue = foodValue;
            return this;
        }
        //--------------------------------------------
        //-------------- iventort --------------------
        public CustomEntityBuilder setItemContents( List<ItemStack> itemList ){
            int i = 0;
            items = new ItemStack[ itemList.size() ];
            for (ItemStack itemStack : itemList){
                if( itemStack != null )
                    items[ i++ ] = itemStack;
            }
            return this;
        }
    }
}
