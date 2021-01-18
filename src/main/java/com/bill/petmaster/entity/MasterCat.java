package com.bill.petmaster.entity;

import java.util.List;

import javax.swing.text.html.parser.Entity;

import com.bill.petmaster.quest.PetQuest;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Cat;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class MasterCat extends CustomEntity{
    public MasterCat(Cat cat, Player owner, List<PetQuest> petQuest){
        super(cat, owner, petQuest);
    }

    @Override
    public Cat getEntity(){
        return (Cat)entity;
    }

    @Override
    public void checkTarget(){
        Cat cat = ((Cat)entity);
        if( cat.getTarget() != null ){
            LivingEntity target = cat.getTarget();

            if( target.isDead() == true || target.isValid() == false ){
                cat.setTarget( null );
            }
            else{
                double followRange = cat.getAttribute( Attribute.GENERIC_FOLLOW_RANGE ).getDefaultValue();
                if( cat.getLocation().distance( target.getLocation() ) > followRange ){
                    cat.setTarget( null );
                }
            }
        }
    }
}
