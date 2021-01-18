package com.bill.petmaster.quest;


public abstract class PetObjective{
    private final String name;              //display item or entity  name
    private final int requireAmount;        //Require mob require amount
    private int nowAmount = 0;
    /**
     * construct a Quest item or entity object
     * @param name display item or entity name
     * @param requireAmount Mob require amount in quest
     */
    public PetObjective( String name, int requireAmount ){
        this.name               = name;
        this.requireAmount      = requireAmount;
    }
    public PetObjective( PetObjective obj ){
        this.name               = obj.name;
        this.requireAmount      = obj.requireAmount;
        this.nowAmount          = obj.nowAmount;
    }
    /** add targer object amount 
     *  @param amount amount */
    public void addProgress( int amount ){
        nowAmount += amount;
    }
    /** judge progress are exceed requireAmount 
     *  @return {@link Boolean} true exceeded, false not yet*/
    public boolean isFinished(){
        return nowAmount >= requireAmount;
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
        return String.join("", name, " ( ", String.valueOf( nowAmount ), " / ", String.valueOf( requireAmount ), " )" );
    }
}
