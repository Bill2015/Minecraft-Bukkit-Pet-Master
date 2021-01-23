package com.bill.petmaster.manager;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.bill.petmaster.App;
import com.bill.petmaster.entity.CustomEntity;
import com.bill.petmaster.holder.PetInventoryHolder;
import com.bill.petmaster.quest.PetObjective;
import com.bill.petmaster.util.PetAttribute;
import com.bill.petmaster.util.PetFoodType;
import com.bill.petmaster.util.PetHunger;
import com.bill.petmaster.util.PetLevel;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class DataManager {
    
    private ConcurrentHashMap<UUID, CustomEntity> petsMap = new ConcurrentHashMap<>();

    private FileConfiguration dataConfig;
    private File dataFile;
    private final App plugin;
    public DataManager( App plugin ){
        this.plugin = plugin;
        this.petsMap = loadPetData();
    }

    public ConcurrentHashMap<UUID, CustomEntity> getPetsMap() {
        return petsMap;
    }

    /** get this player all of pets */
    public ArrayList<CustomEntity> getPets( Player player ){
        ArrayList<CustomEntity> list = new ArrayList<>();
        for( CustomEntity entity : petsMap.values() ){
            if( entity.getOwner().getUniqueId().equals( player.getUniqueId() ) ){
                list.add( entity );
            }
        }
        return list;
    }

    private ConcurrentHashMap<UUID, CustomEntity> loadPetData(){
        ConcurrentHashMap<UUID, CustomEntity> petsDataMap = new ConcurrentHashMap<>();
        //check the data is avalible
        if( loadDataFile() == true ){
            MemorySection root = (MemorySection)dataConfig.get("data");

            //get every data
            int dataCount = 0;
            for( String rootStr : Objects.requireNonNull( root.getKeys( false ) ) ){
                MemorySection objRoot = (MemorySection)root.get( rootStr );
 
                try {
                    //get basic pet data
                    Class<?> petClass   = Class.forName( objRoot.getString( "petClass" ) );
                    UUID petUUID        = UUID.fromString( objRoot.getString("petUUID") );
                    UUID ownerUUID      = UUID.fromString( objRoot.getString("petOwnerUUID") );
                    String petName      = objRoot.getString( "petName" );
                    boolean petStatus   = objRoot.getBoolean( "petIsDead" );
                    double petHealth    = objRoot.getDouble( "petHealth" );
                
                    //---------------------------------------------------------------------------------
                    //load pet level
                    int petLevel        = objRoot.getInt( "petLevel" );
                    List<Integer> petProgress = objRoot.getIntegerList( "petObjectives" );

                    //---------------------------------------------------------------------------------
                    //load pet attribute point
                    List<Short> petPoint        = objRoot.getShortList( "petAttributePoint" );    //save attribute point array
                    int petUnusedPoint          = objRoot.getInt( "petUnusedPoint" );

                    //---------------------------------------------------------------------------------
                    //load pet hunger data
                    PetFoodType petFoodType     = PetFoodType.valueOf( objRoot.getString("foodType") );
                    double petFoodValue         = objRoot.getDouble( "foodValue" );

                    //---------------------------------------------------------------------------------
                    //load pet's inventory data
                    MemorySection objInventory = (MemorySection)objRoot.get( "petInventory" );
                    List<ItemStack> itemList = new ArrayList<>();
                    for (String itemStr : Objects.requireNonNull( objInventory.getKeys( false ) ) ) {
                        ItemStack itemStack = objInventory.getItemStack( itemStr );
                        if (itemStack != null) {
                            itemList.add(itemStack);
                        }
                    }
                        
                    Mob entity = (Mob)Bukkit.getEntity( petUUID );
                    OfflinePlayer player = Bukkit.getOfflinePlayer( ownerUUID );

                    CustomEntity customEntity = CustomEntity.getBuilder( entity, player )
                                                                .setName( petName )
                                                                .setDead( petStatus )
                                                                .setLevel( petLevel )
                                                                .setObjective( petProgress )
                                                                //----------------------------
                                                                .setAttributePoint( petPoint )
                                                                .setUnusedPoint( (short)petUnusedPoint )
                                                                //----------------------------
                                                                .setFoodType( petFoodType )
                                                                .setFoodValue( (float)petFoodValue )
                                                                //----------------------------
                                                                .setItemContents( itemList )
                                                                //----------------------------
                                                                .build( petClass );
                    //put pet's data in map
                    petsDataMap.put( petUUID, customEntity );
                }
                catch (LinkageError | ClassNotFoundException | NullPointerException | IndexOutOfBoundsException e) {
                    plugin.getLogger().info( "Pet's data load failed at No. " + dataCount + " pet's data！" );
                    e.printStackTrace();
                }
                finally{
                    dataCount += 1;
                }
            }
        }
        else{
            plugin.getLogger().info( "No dectected any pet's data." );
        }
        return petsDataMap;
    }
    //=============================================================================================
    /**
     * saving all pet's data */
    public void savePetData(){

        createDataFile();

        ConfigurationSection root = dataConfig.createSection( "data" );
        int count = 0;
        for ( CustomEntity entity : petsMap.values() ) {

            //generate the entity object
            ConfigurationSection objRoot = root.createSection( String.valueOf( count ) );

            objRoot.set( "petClass",        entity.getClass().getName() );                  //save pet belonging class
            objRoot.set( "petUUID",         entity.getEntity().getUniqueId().toString() );          //save pet uuid
            objRoot.set( "petOwnerUUID",    entity.getOwner().getUniqueId().toString() );           //save pet owner
            objRoot.set( "petName",         entity.getName() );                                     //save pet name
            objRoot.set( "petIsDead",       entity.isDead() );                                      //save pet status
            objRoot.set( "petHealth",       entity.getEntity().getHealth() );                       //save pet health

            //---------------------------------------------------------------------------------
            //save pet level data
            PetLevel petLevel = entity.getPetLevel();
            objRoot.set( "petLevel" , petLevel.getLevel() );
            objRoot.set( "petObjectives", petLevel.getProgress() );

            //---------------------------------------------------------------------------------
            //save pet skill point
            PetAttribute petAttribute = entity.getPetAttribute();
            objRoot.set( "petAttributePoint", petAttribute.getPoints() );    //save attribute point array
            objRoot.set( "petUnusedPoint", petAttribute.getUnUsedPoint() );

            //---------------------------------------------------------------------------------
            //save pet hunger data
            PetHunger petHunger = entity.getPetHunger();
            objRoot.set( "foodType" , petHunger.getFoodType().toString() );
            objRoot.set( "foodValue", petHunger.getFoodValue() );

            //---------------------------------------------------------------------------------
            //save pet's inventory data
            Inventory petInventory = entity.getInventoryHolder().getInventory();
            ConfigurationSection objInventory = objRoot.createSection( "petInventory" );
            int invCount = 0;
            for (ItemStack itemStack : petInventory ) {
                if (itemStack != null) {
                    objInventory.set( String.valueOf( invCount ), itemStack);
                    invCount += 1;
                }
            }

            count += 1;

            saveData();
        }
    }

    //=============================================================================================
    private boolean loadDataFile(){
        dataFile = new File( plugin.getDataFolder(), "petData.yml" );
        if ( !dataFile.exists() ) {
            plugin.getLogger().info("No pets data");
            return false;
        }
        dataConfig = new YamlConfiguration();
        try {
            dataConfig.load( dataFile );
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void createDataFile() {
        dataFile = new File( plugin.getDataFolder(), "petData.yml" );
        if ( !dataFile.exists() ) {
            try {
                if (dataFile.createNewFile()) {
                    plugin.getLogger().info("Create pets data file!");
                }
            } catch (IOException e) {
                plugin.getLogger().info("Create pets data failed!");
                e.printStackTrace();
            }
        }

        dataConfig = new YamlConfiguration();
        try {
            dataConfig.load( dataFile );
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    private void saveData() {
        try {  dataConfig.save(dataFile); } catch (IOException e) {
            plugin.getLogger().info("Saving pets data failed!");
            e.printStackTrace();
        }
    }
}
