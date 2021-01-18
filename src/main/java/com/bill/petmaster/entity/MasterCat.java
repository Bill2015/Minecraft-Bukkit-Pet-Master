package com.bill.petmaster.entity;

import java.util.List;
import java.util.Map;

import javax.swing.text.html.parser.Entity;

import com.bill.petmaster.quest.PetQuest;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Cat;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class MasterCat extends CustomEntity{
    public MasterCat(Cat cat, Player owner, Map<Integer, PetQuest> petQuest){
        super(cat, owner, petQuest);
    }

    @Override
    public Cat getEntity(){
        return (Cat)entity;
    }
}
