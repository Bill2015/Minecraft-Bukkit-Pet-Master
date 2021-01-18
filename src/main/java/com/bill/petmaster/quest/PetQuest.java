package com.bill.petmaster.quest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;

public class PetQuest{
    public final static String ITEM = "ITEM";
    public final static String ENTITY = "ENTITY";

    private final String questType;                     //Quest type, kill entity or obtain a item
    private final String questName;                     //Quest name
    private final Material representMaterial;           //Quest represente item
    private final Material finishedMaterial;            //Quest finish represente item
    private final int gainPoint;                        //Ather finish quest, give how many point
    private final QeustMap<? extends PetObjective> questObjective;
    /**
     * construt the PetQuest
     * @param questName Quest name
     * @param representMaterial the represent material of this quest
     * @param finishMaterial the finished material of this quest
     * @param gainPoint gain point, after complete this quest */
    public PetQuest( String questType, String questName, Material representMaterial, Material finishMaterial, int gainPoint, QeustMap<? extends PetObjective> questObjective){
        this.questType          = questType;
        this.questName          = questName;
        this.representMaterial  = representMaterial;
        this.finishedMaterial   = finishMaterial;
        this.gainPoint          = gainPoint;
        this.questObjective     = questObjective;
    }
    /** get clone objective for every pet 
     *  @return {@link Map} a cloned objective list */
    public Map<String, PetObjective> cloneObjective(){
        Map<String, PetObjective> map = new HashMap<>();
        for (PetObjective obj : questObjective.values() ) {
            if( obj instanceof ItemObjective ){
                ItemObjective itemObj = (ItemObjective)obj;
                map.put( itemObj.getMaterial().toString(), obj.cloneObjective() );
            }
            else{
                EntityObjective entityObj = (EntityObjective)obj;
                map.put( entityObj.getEntityType().toString(), obj.cloneObjective() );
            }
        }
        return map;
    }
    /** get this quest name 
     *  @return {@link String} quest name*/
    public String getQuestName() {
        return questName;
    }
    /** get represent material of this quest
     *  @return {@link Material} material*/
    public Material getRepresent() {
        return representMaterial;
    }
    /** get finish material of this quest
     *  @return {@link Material} material*/
    public Material getFinished() {
        return finishedMaterial;
    }
    /** get gain point, afther complete this quest
     *  @return {@link Integer} quest point*/
    public int getPoint() {
        return gainPoint;
    }
    /** get this quest type 
     *  @return {@link String} quest type*/
    public String getQuestType() {
        return questType;
    }
    /** get this quest objective map 
     *  @return {@link QeustMap} objective map */
    public QeustMap<? extends PetObjective> getQuestObjective() {
        return questObjective;
    }

}
