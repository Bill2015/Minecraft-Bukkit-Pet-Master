package com.bill.petmaster.manager;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import com.bill.petmaster.App;

import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;

public class ConfigManager {
    private final App plugin;

    public static HashMap<EntityType, PetSetting> GROWTH_MULTIPLE;
    public ConfigManager( App plugin ){
        this.plugin = plugin;

        MemorySection setting = (MemorySection)plugin.getConfig().get("settings");

        GROWTH_MULTIPLE = new HashMap<>();
        //----------------------------------------------------------------------------
        loadData( setting, EntityType.CAT );    //load cat data 
        loadData( setting, EntityType.WOLF );   //load wolf data 
    }

    /** load pet's data setting
     * @param setting the path
     * @param type the type of pet */
    private static void loadData( MemorySection setting, EntityType type ){
        //get base of pet status
        List<Float> baseList = setting.getFloatList( String.join(".", type.toString(), "attribute", "baseValues" ) );
        float[] baseValues = new float[ baseList.size() ];
        int i1 = 0;
        for (Float f : baseList) {
            baseValues[ i1++ ] = (f != null) ? f : 0;
        }

        //get growth of pet multiple
        List<Float> growthList = setting.getFloatList( String.join(".", type.toString(), "attribute", "growthMultiple" ) );
        float[] growthValues = new float[ growthList.size() ];
        int i2 = 0;
        for (Float f : growthList) {
            growthValues[ i2++ ] = (f != null) ? f : 0;
        }

        //put setting in map
        PetSetting petSetting = new PetSetting( baseValues, growthValues );
        GROWTH_MULTIPLE.put( type, petSetting );
    }

    public static float[] getPetsGrowthValues( EntityType type ){
        return GROWTH_MULTIPLE.get( type ).getGrowthMultiple();
    }
    public static float[] getPetsBaseValues( EntityType type ){
        return GROWTH_MULTIPLE.get( type ).getBaseValues();
    }


    static class PetSetting{
        private float[] baseValues;
        private float[] growthValues;
        public PetSetting( float[] baseValues, float[] growthValues ){
            this.baseValues         = baseValues;
            this.growthValues       = growthValues;
        }
        public float[] getBaseValues() {
            return baseValues;
        }
        public float[] getGrowthMultiple() {
            return growthValues;
        }
    }
}
