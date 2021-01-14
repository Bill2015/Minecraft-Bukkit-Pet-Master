package com.bill.petmaster.entity;

import javax.swing.text.html.parser.Entity;

import org.bukkit.entity.Cat;
import org.bukkit.entity.Player;

public class MasterCat extends CustomEntity{
    public MasterCat(Cat cat, Player owner){
        super(cat, owner);
    }

    @Override
    public Cat getEntity(){
        return (Cat)entity;
    }
}
