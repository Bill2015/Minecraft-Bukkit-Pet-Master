package com.bill.petmaster.quest;

import org.bukkit.entity.EntityType;

public class EntityObjective extends PetObjective{


    private final EntityType entityType; // Entity type

    public EntityObjective( EntityType entityType, String name, int requireAmount) {
        super(name, requireAmount);
        this.entityType = entityType;
    }
    /** <p>get quest type of entityType</p>
     * @return {@link EntityType}  entity type*/
    public EntityType getEntityType() {
        return entityType;
    }

    /** get clone {@link EntityObjective} */
    public EntityObjective cloneObjective(){
        return new EntityObjective(entityType, name, requireAmount);
    }
}
