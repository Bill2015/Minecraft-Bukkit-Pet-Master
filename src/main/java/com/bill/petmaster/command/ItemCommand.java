package com.bill.petmaster.command;

import java.lang.reflect.Array;
import java.util.Arrays;

import com.bill.petmaster.App;
import com.bill.petmaster.manager.ItemManeger;

import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemCommand implements CommandExecutor{

    
    private final ItemManeger itemManeger; 

    public ItemCommand(ItemManeger itemManeger){
        this.itemManeger = itemManeger;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player){
            Player player = (Player) sender;
            
            if( args.length == 1 ){
                if( args[0].equalsIgnoreCase( ItemManeger.CLAIM_STICK ) ){
                    player.getInventory().addItem( itemManeger.getPetStick() );
                    player.sendMessage("[PetMaster] - 獲得認養棒");
                }
                else if( args[0].equalsIgnoreCase( ItemManeger.CHECKER_COMPASS ) ){
                    player.getInventory().addItem( itemManeger.getPetChecker() );
                    player.sendMessage("[PetMaster] - 獲得認養棒");
                }
            }

        }
        return false;
    }
    

}
