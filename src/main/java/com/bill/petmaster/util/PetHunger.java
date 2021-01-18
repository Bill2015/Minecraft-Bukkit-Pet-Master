package com.bill.petmaster.util;

import com.bill.petmaster.entity.CustomEntity;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class PetHunger {
    private CustomEntity owner;
    private float maxFoodValue;
    private float foodValue;
    private final PetFoodType foodType;
    private final float defaultValue;
    public final static int DECREASE_FOOD_TIME = 360;
    public final static int DECREASE_LIFE_TIME = 120;
    public final static int FOOD_COSUME_TIME = 120;
    public PetHunger( CustomEntity owner, PetFoodType foodType, float defaultValue ){
        this.owner          = owner;
        this.foodValue      = defaultValue;
        this.maxFoodValue   = defaultValue;
        this.defaultValue   = defaultValue;
        this.foodType       = foodType;
    }
    //吃食物 true 成功消耗食物  false 沒有找到食物或時候未到
    public Material consumeFood( Inventory inventory ){
        //把所有這個食物類型 包含的食物取出
        for(Material mater : foodType.getMaterials() ){
            //背包有食物
            if( inventory.contains( mater ) ){
                //取得食物的 slot
                int slot = inventory.first( mater );
                //食物量減少
                inventory.setItem(slot, new ItemStack( mater , inventory.getItem(slot).getAmount() - 1)  );
                //飽食度增加
                foodValue += foodType.getValue( mater );
                return mater;
            }
        }
        return null;
    }
    /** Judge this enity is on hungring */
    public boolean isHunger(){ 
        return foodValue <= 0 ? true : false;
    }
    //判斷是否該減少飢餓度
    public boolean isFull(){ 
        return foodValue >= maxFoodValue ? true : false;
    }
    //增加或減少飽食度
    public void addFoodValue(float in){ 
        if(foodValue + in <= maxFoodValue){
            foodValue += in;
        }
    }
    public float getFoodValue() {
        return foodValue;
    }
    public float getMaxFoodValue() {
        return maxFoodValue;
    }
    public void setMaxFoodLevel(float maxFoodLevel) {
        this.maxFoodValue = maxFoodLevel;
    }
    public CustomEntity getOwner() {
        return owner;
    }
    public float getDefaultValue() {
        return defaultValue;
    }
}
