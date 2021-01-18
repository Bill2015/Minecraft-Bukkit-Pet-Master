package com.bill.petmaster.quest;


public abstract class PetObjective{
    private final String name;              //display item or entity  name
    private final String info;              //whole data string
    private final int requireAmount;        //Require mob require amount
    /**
     * construct a Quest item or entity object
     * @param name display item or entity name
     * @param requireAmount Mob require amount in quest
     */
    public PetObjective( String name, int requireAmount ){
        this.name               = name;
        this.requireAmount      = requireAmount;
        this.info               = String.join("", name, " ( ", "0", "/", String.valueOf( requireAmount ), " )" );
    }
    /** get this quest item or entity name  
     *  @return {@link String} item name*/
    public String getName() {
        return name;
    }
    /** get this quest require amount  
     * @return {@link Integer} item require amount*/
    public int getRequireAmount() {
        return requireAmount;
    }
    /** get whole string concat of info 
     *  @return {@link String} item info */
    public String getInfo() {
        return info;
    }
}
