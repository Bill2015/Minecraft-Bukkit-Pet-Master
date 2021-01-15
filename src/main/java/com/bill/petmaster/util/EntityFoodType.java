package com.bill.petmaster.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;

//食物類型
public enum EntityFoodType{
    CARNIVORISM( ChatColor.LIGHT_PURPLE + "肉食主義",
                 Arrays.asList( Material.BEEF, Material.CHICKEN, Material.PORKCHOP, Material.MUTTON, Material.RABBIT),
                 Arrays.asList(  0.2f,    0.2f,    0.2f,     0.2f,    0.2f) 
                ),
    
    FISHMEAT   ( ChatColor.LIGHT_PURPLE + "魚肉主義",
                 Arrays.asList( Material.COD, Material.COOKED_COD, Material.SALMON, Material.COOKED_SALMON, Material.PUFFERFISH),
                 Arrays.asList(  0.3f,    0.3f,    0.3f,     0.3f,    0.3f) 
                );
    final String name;
    final HashMap<Material, Float> foods;
    private EntityFoodType(String name, List<Material> materials, List<Float> values){
       this.name                = name;
       this.foods               = new HashMap<>();
       Iterator<Material> mItor = materials.iterator();
       Iterator<Float>    vItor = values.iterator(); 
       for( ; mItor.hasNext() && vItor.hasNext(); ){
           foods.put( mItor.next(), vItor.next() );
       }
    }
    //是否包括同原料
    public boolean isContain( Material in ){
        return foods.containsKey( in );
    }
    //取得名稱
    public String getName(){ return name; }
    //取得原料清單
    public Set<Material> getMaterials(){ return foods.keySet(); }
    //取得所有數值
    public Collection<Float> getValues(){ return foods.values(); }
    //取得原料所提供的數值
    public float getValue( Material material ){
        return foods.containsKey( material ) ? foods.get( material ) : 0.0f;
    }
}
