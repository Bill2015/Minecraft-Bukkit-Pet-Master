package com.bill.petmaster.util;

public class PetAttribute{
    private short attributePoint[] = new short[7];
    private float percent[] = new float[7];
    private float mutiple[]= new float[7];
    private short unUsedPoint = 0;                                //技能點數
    public PetAttribute( float mutiple[] ){
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
    public void addAttributePoint(short in){ unUsedPoint += in; }
    //計算技能點
    private void calculate(AttributePoint petAttribute){
        if( petAttribute == AttributePoint.ARMOR || petAttribute == AttributePoint.REGEN ){
            percent[ petAttribute.get() ] = (attributePoint[ petAttribute.get() ] * mutiple[ petAttribute.get() ]);
        }
        else if( petAttribute == AttributePoint.RESIST ){
            percent[ petAttribute.get() ] = ((float) attributePoint[ petAttribute.get() ] * mutiple[ petAttribute.get() ] / 100);
        }
        else
            percent[ petAttribute.get() ] = 1.0f + (((float)( attributePoint[ petAttribute.get() ] * mutiple[ petAttribute.get()] )) / 100.0f);
    }
    //取得增量百分比
    public int getIncrement( AttributePoint point ){
        if( point == AttributePoint.ARMOR || point == AttributePoint.REGEN ){
            return  (int)(percent[ point.get() ]);
        }
        return  (int)(percent[ point.get() ] * 100);
    }
    //取得點數
    public short getUnUsedPoint(){ return unUsedPoint; }
    //點數上加
    public void addPoint( AttributePoint petAttribute ){ 
        attributePoint[ petAttribute.get() ] += 1; 
        unUsedPoint -= 1;
        calculate( petAttribute );
    }
    //取得點數
    public short getPoint( AttributePoint petAttribute ){ return attributePoint[ petAttribute.get() ]; }
    //取得數值 + 計算
    public float getValue( AttributePoint petAttribute ){ return percent[ petAttribute.get() ]; }
}