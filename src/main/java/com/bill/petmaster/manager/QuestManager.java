package com.bill.petmaster.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bill.petmaster.App;
import com.bill.petmaster.quest.EntityObjective;
import com.bill.petmaster.quest.EntityQuestMap;
import com.bill.petmaster.quest.ItemObjective;
import com.bill.petmaster.quest.ItemQuestMap;
import com.bill.petmaster.quest.PetQuest;

import org.bukkit.Material;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;

public class QuestManager {
    private Map<Integer, PetQuest> quests;
    private FileConfiguration dataConfig;
    private final App plugin;
    private final MessageManager messageManager;
    public QuestManager( App plugin, MessageManager messageManager ){
        this.plugin         = plugin;
        this.messageManager = messageManager;
        
        loadQuestFile();
        this.quests         = loadQuestData();
        plugin.getLogger().info("Data load finished.");
        printResult();
    }

    /** get the quest of pet List
     *  @return {@link Map} qeust List*/
    public Map<Integer, PetQuest> getQuests() {
        return quests;
    }

    private Map<Integer, PetQuest> loadQuestData(){
        Map<Integer, PetQuest> questMap = new HashMap<>();
        //get root of file
        if( dataConfig.get("quest") instanceof MemorySection ){
            MemorySection root = (MemorySection)dataConfig.get("quest");
            int lvlcount = 1;

            //get each lvl data
            for (Object lvltemp = root.get( String.valueOf( lvlcount ) ) ; 
                    lvltemp != null; 
                        lvlcount += 1, lvltemp = root.get( String.valueOf( lvlcount ) )) {

                MemorySection lvlSection = (MemorySection)lvltemp;

                String questName            = lvlSection.getString("questName");
                Material representMaterial  = Material.matchMaterial( lvlSection.getString("representMaterial").toUpperCase() );
                Material finishMaterial     = Material.matchMaterial( lvlSection.getString("finishedMaterial").toUpperCase() );

                // if can't find represent material
                if( representMaterial == null ){
                    questName = "Material Not found, set dirt as default";
                    representMaterial = Material.DIRT;
                    messageManager.sendQuestDataLoadMissingMaterial( lvlcount );
                }
                // if can't find finished material
                if( finishMaterial == null ){
                    questName = "Material Not found, set dirt as default";
                    finishMaterial = Material.DIRT;
                    messageManager.sendQuestDataLoadMissingMaterial( lvlcount );
                }
                short points        = (short)lvlSection.getInt("points");
                String questType    = lvlSection.getString("type").toUpperCase();

                //judge which type of quest
                if( questType.equals( PetQuest.ITEM ) ){
                    ItemQuestMap itemMap = getItemQuest(lvlSection, lvlcount);
                    questMap.put( lvlcount, new PetQuest(questType, questName, representMaterial, finishMaterial, points, itemMap ) );
                }
                else if( questType.equals( PetQuest.ENTITY  ) ){
                    EntityQuestMap entityMap = getEntityQuest(lvlSection, lvlcount);
                    questMap.put( lvlcount,new PetQuest(questType, questName, representMaterial, finishMaterial, points, entityMap ) );
                }
                else{
                    messageManager.sendQuestDataLoadQuestTypeNotFound( lvlcount );
                }

            }
        }
        return questMap;
    }


    private EntityQuestMap getEntityQuest( MemorySection lvlSection, int lvlcount ){
        EntityQuestMap entityMap = new EntityQuestMap();

        int objectiveCount = 0;
        for (Object objtemp = lvlSection.get( String.join(".", "objectives", String.valueOf( objectiveCount ) )  ) ; 
                    objtemp != null; 
                        objectiveCount += 1, objtemp = lvlSection.get( String.join(".", "objectives", String.valueOf( objectiveCount ) )  )) {

            MemorySection objSection = (MemorySection)objtemp;
            String objName              = objSection.getString("name");
            short requireAmount         = (short)objSection.getInt("require");
            EntityType objEntity        = EntityType.fromName( objSection.getString("key").toUpperCase() );

            // check objective item is exist
            if( objEntity == null ){
                objName = "Entity Not found, set chicken as default";
                objEntity = EntityType.CHICKEN;
                messageManager.sendQuestDataLoadMissingEntity( lvlcount );
            }
            entityMap.put( objEntity.toString() , new EntityObjective(objEntity, objName, requireAmount) );
        }
        return entityMap;
    }

    private ItemQuestMap getItemQuest( MemorySection lvlSection, int lvlcount ){
        ItemQuestMap itmeMap = new ItemQuestMap();

        int objectiveCount = 0;
        for (Object objtemp = lvlSection.get( String.join(".", "objectives", String.valueOf( objectiveCount ) ) ) ; 
                objtemp != null; objectiveCount += 1, 
                    objtemp = lvlSection.get( String.join(".", "objectives", String.valueOf( objectiveCount ) ) ) ) {

            MemorySection objSection = (MemorySection)objtemp;
            String objName          = objSection.getString("name");
            short requireAmount     = (short)objSection.getInt("require");
            Material objMaterial    = Material.matchMaterial( objSection.getString("key").toUpperCase() );

            // check objective item is exist
            if( objMaterial == null ){
                objName = "Material Not found, set dirt as default";
                objMaterial = Material.DIRT;
                messageManager.sendQuestDataLoadMissingMaterial( lvlcount );
            }
            itmeMap.put( objMaterial.toString() , new ItemObjective(objMaterial, objName, requireAmount) );
        }
        return itmeMap;
    }

    //==========================================================================================
    private void loadQuestFile(){
        File dataFile = new File( plugin.getDataFolder(), "quest.yml" ); 
        if( !dataFile.exists() ){
            try {
                dataFile.getParentFile().mkdirs();
                plugin.saveResource( "quest.yml", false );

                dataConfig = YamlConfiguration.loadConfiguration( dataFile );
                plugin.saveDefaultConfig();

            } catch ( IllegalArgumentException e) {
                dataLoadFailed();
            }
        }
        else{
            dataConfig = YamlConfiguration.loadConfiguration( dataFile );

        }
    }

    private void dataLoadFailed(){
        messageManager.sendQuestDataLoadFail();
        plugin.onDisable(); //close plugin
    }
    //==========================================================================================
    /** just for test */
    private void printResult(){
        plugin.getLogger().info( "Print Pet Quest Data");
        for (PetQuest petQuest : quests.values() ) {
            plugin.getLogger().info( petQuest.getQuestName() );

            if( petQuest.getQuestObjective() instanceof ItemQuestMap ){
                ItemQuestMap itemQuestMap = (ItemQuestMap)petQuest.getQuestObjective();
                for (  ItemObjective obj : itemQuestMap.values() ) {
                    plugin.getLogger().info( "   - " + obj.getName() + " " + obj.getRequireAmount());
                }
            }
            else if( petQuest.getQuestObjective() instanceof EntityQuestMap ){
                EntityQuestMap entityQuestMap = (EntityQuestMap)petQuest.getQuestObjective();
                for (  EntityObjective obj : entityQuestMap.values() ) {
                    plugin.getLogger().info( "   - " + obj.getName() + " " + obj.getRequireAmount());
                }
            }
        }
    }
}
