package com.bill.petmaster;



import com.bill.petmaster.listeners.PetEvent;

import org.bukkit.plugin.java.JavaPlugin;

public final class App extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();

        getServer().getPluginManager().registerEvents(new PetEvent(this), this);


    }

    @Override
    public void onDisable() {
    
    }


}
