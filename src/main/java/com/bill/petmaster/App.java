package com.bill.petmaster;



import java.util.Objects;

import javax.xml.crypto.Data;

import com.bill.petmaster.command.ItemCommand;
import com.bill.petmaster.listeners.ItemUseEvent;
import com.bill.petmaster.listeners.PetEvent;
import com.bill.petmaster.manager.DataManager;
import com.bill.petmaster.manager.ItemManeger;

import org.bukkit.plugin.java.JavaPlugin;

public final class App extends JavaPlugin {

    private ItemManeger itemManeger;
    private DataManager DataManager;
    @Override public void onEnable() {
        saveDefaultConfig();

        itemManeger = new ItemManeger();
        DataManager = new DataManager();

        getServer().getPluginManager().registerEvents(new PetEvent(this), this);
        getServer().getPluginManager().registerEvents(new ItemUseEvent(this), this);


        Objects.requireNonNull(getCommand("petmaster")).setExecutor( new ItemCommand( itemManeger ) );

    }

    @Override
    public void onDisable() {
    
    }


    public ItemManeger getItemManeger() {
        return itemManeger;
    }
    public DataManager getDataManager() {
        return DataManager;
    }
}
