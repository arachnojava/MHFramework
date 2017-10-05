package mhframework.gui;


import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import mhframework.MHRenderable;

/********************************************************************
 * Container class for maintaining a list of my custom
 * GUI components.  It is responsible for rendering each
 * component and passing events to them.
 *
 * <p>The MHScreen class contains an MHGUIComponentList
 * object as a data member, giving every game screen
 * the inherent ability to use GUI components.
 * 
 * @author Michael Henson
 */
public class MHGUIComponentList implements MHRenderable
{
    private final ArrayList<MHGUIComponent> list;
    private int currentSelectionIndex = 0;


    public MHGUIComponentList()
    {
        list = new ArrayList<MHGUIComponent>();
    }


    public void add(final MHGUIComponent c)
    {
        list.add(c);
    }


    public void remove(final MHGUIComponent c)
    {
        list.remove(c);
    }


    public synchronized void render(final Graphics2D g)
    {
        for (final MHGUIComponent c : list)
        {
            if (c.isVisible())
                c.render(g);
        }
    }


    public void advance()
    {
        for (final MHGUIComponent c : list)
            c.advance();
    }


    public MHGUIComponent getFocusedComponent()
    {
        int i = currentSelectionIndex;

        while(true)
        {
            if (list.get(i).hasFocus())
                return list.get(i);
            i++;
            if (i >= list.size())
                i = 0;
        }
    }


//===================================================================
    public void nextFocusableComponent()
    {
        int i = currentSelectionIndex+1;
        boolean found = false;

        list.get(currentSelectionIndex).setFocus(false);
        while (!found)
        {
            if (i >= list.size())
                i = 0;
            if (list.get(i).focusable)
            {
                currentSelectionIndex = i;
                list.get(i).setFocus(true);
                found = true;
            }
            i++;
        }

    }


//===================================================================
    public void prevFocusableComponent()
    {
        int i = currentSelectionIndex-1;
        boolean found = false;

        list.get(currentSelectionIndex).setFocus(false);
        while (!found)
        {
            if (i < 0)
                i = list.size()-1;
            if (list.get(i).focusable)
            {
                currentSelectionIndex = i;
                list.get(i).setFocus(true);
                found = true;
            }
            i--;
        }

    }


    public void mouseClicked(final java.awt.event.MouseEvent e)
    {
        for (final MHGUIComponent c : list)
            if (c != null && c.isVisible())
            c.mouseClicked(e);
    }


    public void mousePressed(final java.awt.event.MouseEvent e)
    {
        for (final MHGUIComponent c : list)
            if (c != null && c.isVisible())
            c.mousePressed(e);
    }


    public synchronized void mouseReleased(final java.awt.event.MouseEvent e)
    {
        try {
        synchronized(list)
        {
            for (final MHGUIComponent c : list)
                if (c != null && c.isVisible())
                    c.mouseReleased(e);
        }
        }
        catch (Exception ex){}
    }


    public synchronized void mouseMoved(final java.awt.event.MouseEvent e)
    {
        for (final MHGUIComponent c : list)
            if (c != null && c.isVisible())
                c.mouseMoved(e);
    }


//    public void mouseDragged(java.awt.event.MouseEvent e)
//    {
//        for (MHGUIComponent c : list)
//        {
//            c.mouseDragged(e);
//        }
//    }


    public void keyPressed(final java.awt.event.KeyEvent e)
    {
        MHGUIComponent c;

        for (int i = 0; i < list.size(); i++)
        {
            c = list.get(i);
            if (c != null && c.isVisible())
            c.keyPressed(e);
        }
    }


    public void keyReleased(final java.awt.event.KeyEvent e)
    {
        MHGUIComponent c;

        for (int i = 0; i < list.size(); i++)
        {
            c = list.get(i);
            if (c != null && c.isVisible())
            c.keyReleased(e);
        }
    }


    public void keyTyped(final java.awt.event.KeyEvent e)
    {
        MHGUIComponent c;

        for (int i = 0; i < list.size(); i++)
        {
            c = list.get(i);
            if (c != null && c.isVisible())
                c.keyTyped(e);
        }
    }


    public void hideAll()
    {
        MHGUIComponent c;

        for (int i = 0; i < list.size(); i++)
        {
            c = list.get(i);
            if (c != null && c.isVisible())
                c.setVisible(false);
        }
    }


    public void showAll()
    {
        MHGUIComponent c;

        for (int i = 0; i < list.size(); i++)
        {
            c = list.get(i);
            if (c != null)
                c.setVisible(true);
        }
    }


    public MHGUIComponent get(final int index)
    {
        return list.get(index);
    }


    public int getSize()
    {
        return list.size();
    }

	/**
	 * Method add.
	 * @param component
	 * @param index
	 */
	public void add(final int index, final MHGUIComponent component)
	{
		list.add(index, component);
	}


    public void clear()
    {
        list.clear();
    }


    public void mouseDragged(MouseEvent e)
    {
        MHGUIComponent c;

        for (int i = 0; i < list.size(); i++)
        {
            c = list.get(i);
            if (c != null && c.isVisible())
            c.mouseDragged(e);
        }
    }

}
