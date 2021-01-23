package com.bill.petmaster.util;

import org.bukkit.ChatColor;
import org.bukkit.Material;


public enum AttributePoint {
    DAMAGE(0, Material.DIAMOND_SWORD,   ChatColor.DARK_RED + "⚔ 傷害量", "% 傷害"),
    ARMOR (1, Material.IRON_CHESTPLATE, ChatColor.WHITE + "☗  護甲值", "點 護甲"),
    HEALTH(2, Material.REDSTONE,        ChatColor.RED + "❤  最大血量", "% 生命最大值"),
    SPEED (3, Material.MINECART,        ChatColor.AQUA + "✠  移動速度", "% 移動速度"),
    RESIST(4, Material.SHIELD,          ChatColor.BLUE + "✙  擊退抗性" ,"% 擊退抗性"),
    FOOD  (5, Material.COOKED_CHICKEN,  ChatColor.GOLD + "☕  食物容量", "% 食物容量"),
    REGEN (6, Material.HEART_OF_THE_SEA,ChatColor.DARK_PURPLE + "♻  回血速度", " 滴 / 每分鐘 ");

    //成員
    private final Material material;
    private final String name;
    private final String unit;
    private final int slot;
    private AttributePoint(int slot, Material material, String name, String unit){
        this.material = material;
        this.name = name;
        this.slot = slot;
        this.unit = unit;

    }
    //取得位置
    public int get(){ return slot; }
    //取的代表物品
    public Material getMaterial(){ return material; }
    //取得名稱
    public String getName(){ return name; }
    //取得單位
    public String getUnit(){ return unit; }
    //取的字串
    public String getWhole(int values){ return name + "  " + values + unit; }
    //取得下個元素
    public AttributePoint next() {return values()[ordinal() + 1];}
}
