package com.bill.petmaster.manager;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.bill.petmaster.entity.CustomEntity;

import org.bukkit.entity.Player;

public class DataManager {
    
    private ConcurrentHashMap<UUID, CustomEntity> petsMap = new ConcurrentHashMap<>();
    public DataManager(){

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


    public boolean savePetData(){
       /* for ( CustomEntity entity : petsMap.values() ) {
            
        }*/
        return false;
    }
}
