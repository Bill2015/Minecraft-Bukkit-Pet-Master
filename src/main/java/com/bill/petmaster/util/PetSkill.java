package com.bill.petmaster.util;

public class PetSkill{
    private short skillpoint[] = new short[7];
    private float percent[] = new float[7];
    private float mutiple[]= new float[7];
    private short unUsedPoint = 0;                                //技能點數
    public PetSkill( float mutiple[] ){
       this.mutiple = mutiple;
       this.percent = new float[]{
            1.0f,   // Damage
            0.0f,   // Armor
            1.0f,   // Health
            1.0f,   // Movement speed
            0.0f,   // Resistance
            1.0f,   // Food Capcity
            0.0f    // Life Regen
        };
    }
    public void addSkillPoint(short in){ unUsedPoint += in; }
    //計算技能點
    private void calculate(PetSkillPoint skill){
        if( skill == PetSkillPoint.ARMOR || skill == PetSkillPoint.REGEN ){
            percent[ skill.get() ] = (skillpoint[ skill.get() ] * mutiple[ skill.get() ]);
        }
        else if( skill == PetSkillPoint.RESIST ){
            percent[ skill.get() ] = ((float) skillpoint[ skill.get() ] * mutiple[ skill.get() ] / 100);
        }
        else
            percent[ skill.get() ] = 1.0f + (((float)( skillpoint[ skill.get() ] * mutiple[ skill.get()] )) / 100.0f);
    }
    //取得增量百分比
    public int getIncrement( PetSkillPoint skill ){
        if( skill == PetSkillPoint.ARMOR || skill == PetSkillPoint.REGEN ){
            return  (int)(percent[ skill.get() ]);
        }
        return  (int)(percent[ skill.get() ] * 100);
    }
    //取得點數
    public short getUnUsedPoint(){ return unUsedPoint; }
    //點數上加
    public void addPoint( PetSkillPoint skill ){ 
        skillpoint[ skill.get() ] += 1; 
        unUsedPoint -= 1;
        calculate( skill );
    }
    //取得點數
    public short getPoint( PetSkillPoint skill ){ return skillpoint[ skill.get() ]; }
    //取得數值 + 計算
    public float getValue( PetSkillPoint skill ){ return percent[ skill.get() ]; }
}