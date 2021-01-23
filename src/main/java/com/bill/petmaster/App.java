package com.bill.petmaster;



import java.util.Objects;

import javax.xml.crypto.Data;

import com.bill.petmaster.command.ItemCommand;
import com.bill.petmaster.listeners.ItemUseEvent;
import com.bill.petmaster.listeners.PetEvent;
import com.bill.petmaster.listeners.PetInterfaceEvent;
import com.bill.petmaster.manager.ConfigManager;
import com.bill.petmaster.manager.DataManager;
import com.bill.petmaster.manager.ItemManeger;
import com.bill.petmaster.manager.MessageManager;
import com.bill.petmaster.manager.QuestManager;

import org.bukkit.plugin.java.JavaPlugin;

public final class App extends JavaPlugin {

    private ItemManeger itemManeger;
    private DataManager dataManager;
    private MessageManager messageManager;
    private QuestManager questManager;
    private ConfigManager configManager;
    @Override public void onEnable() {
        saveDefaultConfig();

        configManager   = new ConfigManager( this );
        messageManager  = new MessageManager( this );
        questManager    = new QuestManager( this, messageManager );
        itemManeger     = new ItemManeger();
        dataManager     = new DataManager( this );

        getServer().getPluginManager().registerEvents(new PetInterfaceEvent(this, dataManager, itemManeger, questManager), this);
        getServer().getPluginManager().registerEvents(new PetEvent(this, dataManager, itemManeger, questManager), this);
        getServer().getPluginManager().registerEvents(new ItemUseEvent(this, dataManager, itemManeger), this);


        Objects.requireNonNull(getCommand("petmaster")).setExecutor( new ItemCommand( itemManeger ) );

    }

    @Override
    public void onDisable() {
        dataManager.savePetData();
    }

}
