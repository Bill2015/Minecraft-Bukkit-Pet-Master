package com.bill.petmaster.manager;

import com.bill.petmaster.App;

public class MessageManager {
    private final App plugin;

    public MessageManager( App plugin ){
        this.plugin = plugin;
    }


    //=============================================================================
    public void sendQuestDataLoadFail(){
        plugin.getLogger().info("Quest data load failed!");
    }
    public void sendQuestDataLoadMissingMaterial( int levelAt ){
        plugin.getLogger().info("Quest data load failed, Material missing at level: " + levelAt + " !");
    }
    public void sendQuestDataLoadMissingEntity( int levelAt ){
        plugin.getLogger().info("Quest data load failed, EntityType missing at level: " + levelAt + " !");
    }
    public void sendQuestDataLoadQuestTypeNotFound( int levelAt ){
        plugin.getLogger().info("Quest data load failed, QusetType not found at level: " + levelAt + " !");
    }
    
}
