package mhframework.gui;

import java.awt.Color;
import java.awt.Graphics2D;


/********************************************************************
 * 
 * @author Michael Henson
 *
 */
public class MHGUIColorCycleControl extends MHGUICycleControl
{
    public MHGUIColorCycleControl()
    {
        final Color[] c = new Color[]
        {
            Color.BLUE, Color.RED, Color.GREEN, Color.YELLOW
        };

        setLabelVisible(false);
        setValues(c);
    }

    @Override
    public void render(final Graphics2D g)
    {
        final Color c = getSelectedValue();
        g.setColor(c);
        g.fillRect(getX(), getY(), getWidth(), getHeight());
        super.render(g);
    }


    @Override
    public Color getSelectedValue()
    {
        return (Color)(super.getSelectedValue());
    }

}
