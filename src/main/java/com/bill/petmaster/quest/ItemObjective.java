package com.bill.petmaster.quest;

import org.bukkit.Material;

public class ItemObjective extends PetObjective{

    private final Material material; // material
    /**
     * construct a Quest item or entity object
     * @param name display item or entity name
     * @param requireAmount Mob require amount in quest
     */
    public ItemObjective( Material material,  String name, int requireAmount) {
        super(name, requireAmount);
        this.material = material;
    }

    /** <p>get quest type of material</p>
     * @return {@link Material}  material*/
    public Material getMaterial() {
        return material;
    }
}
