package mhframework.gui;

import mhframework.media.MHFont;


/**
 * 
 * 
 * @author Michael Henson
 */
public class MHGUINumericCycleControl extends MHGUICycleControl
{
    private int minValue = 0;
    private int maxValue = 999;
    private int value = 0;
    private int increment = 1;

    public int getMinValue()
    {
        return minValue;
    }

    public void setMinValue(final int minValue)
    {
        if (minValue > value)
            setValue(minValue);
        
        this.minValue = minValue;
    }

    public int getMaxValue()
    {
        return maxValue;
    }

    public void setMaxValue(final int maxValue)
    {
        if (maxValue < value)
            setValue(maxValue);
        
        this.maxValue = maxValue;
    }

    @Override
    public Object getSelectedValue()
    {
        return value;
    }

    @Override
    public void setSelectedIndex(final int indexNumber)
    {
        setValue(indexNumber);
    }

    @Override
    protected void decrement()
    {
        value -= increment;
        if (value < minValue)
            setValue(maxValue);
    }

    @Override
    protected void increment()
    {
        value += increment;
        if (value > maxValue)
            setValue(minValue);
    }

    public int getIncrement()
    {
        return increment;
    }

    public void setIncrement(final int increment)
    {
        this.increment = increment;
    }

    private void setValue(int v)
    {
        value = v;
        getLabel().setText(value+"");
    }
    
    @Override
    public void setValues(final Object[] values)
    {
        System.err.println("ERROR:  Cannot set values in numeric cycle control.");
    }

    @Override
    public void advance()
    {
        getLabel().setText(value+"");
    }

    public void setFont(MHFont font)
    {
        getLabel().setFont(font);
    }
}
