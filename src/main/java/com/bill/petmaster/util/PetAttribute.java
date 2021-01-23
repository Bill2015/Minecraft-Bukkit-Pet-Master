package com.bill.petmaster.util;

public class PetAttribute{
    private short attributePoint[] = new short[7];   //the number of attribute point used
    private float result[] = new float[7];           //caculate the attribute point             
    private short unUsedPoint = 0;                   //unused attribute point
    private final float multiple[];                   //the increament of attribute
    private final float defaultValue[];
    public PetAttribute( short point[], float defaultValue[], float multiple[], short unusedPoint ){
        this.attributePoint = point;
        this.defaultValue   = defaultValue;
        this.multiple       = multiple;
        this.result = new float[]{
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
            result[ petAttribute.get() ] = (attributePoint[ petAttribute.get() ] * multiple[ petAttribute.get() ]);
        }
        else if( petAttribute == AttributePoint.RESIST ){
            result[ petAttribute.get() ] = ((float) attributePoint[ petAttribute.get() ] * multiple[ petAttribute.get() ] / 100);
        }
        else
            result[ petAttribute.get() ] = 1.0f + (((float)( attributePoint[ petAttribute.get() ] * multiple[ petAttribute.get()] )) / 100.0f);
    }
    //取得增量百分比
    public int getIncrement( AttributePoint point ){
        if( point == AttributePoint.ARMOR || point == AttributePoint.REGEN ){
            return  (int)(result[ point.get() ]);
        }
        return  (int)(result[ point.get() ] * 100);
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
    
    /** get array of attribute point 
     *  @return {@link Short}[] array of attribute point  */
    public short[] getPoints(){ return attributePoint; }

    /** get value of attribute 
     *  @return {@link float} value of attribute   */
    public float getValue( AttributePoint petAttribute ){ return result[ petAttribute.get() ] * defaultValue[ petAttribute.get() ]; }
    
    /** get default value of attribute 
     *  @return {@link float} value of attribute   */
    public float getBaseValue( AttributePoint petAttribute  ){ return defaultValue[ petAttribute.get() ]; }
}