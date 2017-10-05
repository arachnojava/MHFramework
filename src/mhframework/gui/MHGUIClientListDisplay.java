package mhframework.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import mhframework.io.net.MHSerializableClientInfo;
import mhframework.io.net.MHSerializableClientList;

/********************************************************************
 * 
 * 
 * @author Michael Henson
 */
public class MHGUIClientListDisplay extends MHGUIComponent
{
    private MHSerializableClientList data;

    private final Color bgColor;

    private final Color textColor;
    private final Font font;

    private boolean useColors = false;

    public MHGUIClientListDisplay()
    {
        this(null);
    }

    public MHGUIClientListDisplay(final MHSerializableClientList clientList)
    {
        data = clientList;

        bgColor = new Color(64, 64, 64, 64);
        textColor = Color.WHITE;

        font = new Font("Monospaced", Font.PLAIN, 12);
    }


    public void setClientList(final MHSerializableClientList clientList)
    {
        data = clientList;
    }

    
    public MHSerializableClientList getClientList()
    {
        return data;
    }

    public void useClientColors(final boolean colorsOn)
    {
        this.useColors = colorsOn;
    }

    @Override
    public void keyPressed(final KeyEvent e)
    {
        // TODO Auto-generated method stub

    }


    @Override
    public void keyReleased(final KeyEvent e)
    {
        // TODO Auto-generated method stub

    }


    @Override
    public void keyTyped(final KeyEvent e)
    {
        // TODO Auto-generated method stub

    }


    @Override
    public void mouseClicked(final MouseEvent e)
    {
        // TODO Auto-generated method stub

    }


    @Override
    public void mouseMoved(final MouseEvent e)
    {
        // TODO Auto-generated method stub

    }


    @Override
    public void mousePressed(final MouseEvent e)
    {
        // TODO Auto-generated method stub

    }


    @Override
    public void mouseReleased(final MouseEvent e)
    {
        // TODO Auto-generated method stub

    }



    @Override
    public void advance()
    {
    }


    /**
     * Template Method pattern.
     */
    @Override
    public void render(final Graphics2D g)
    {
        drawBackground(g);

        g.setFont(font);
        g.setColor(textColor);

        drawClientList(g);
    }

    
    public void drawClientList(Graphics2D g)
    {
        final int spacing = 2;
        int y = getY() + font.getSize() + spacing;
        if (data == null || data.size() <= 0)
        {
            g.drawString("No clients", getX()+2, y);
        }
        else
        {
            for (final MHSerializableClientInfo client : data)
            {
                if (useColors)
                    g.setColor(client.color);

                //g.drawString(client.id+" "+client.name, getX()+2, y);
                g.drawString(client.name, getX()+2, y);
                y += font.getSize() + spacing;
            }
        }
    }
    
    
    public void drawBackground(Graphics2D g)
    {
        g.setColor(bgColor);
        g.draw3DRect(getX(), getY(), (int)getBounds().getWidth(), (int)getBounds().getHeight(), false);
    }
}
