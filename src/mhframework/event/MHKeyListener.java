package mhframework.event;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import mhframework.MHGameApplication;


/********************************************************************
 * Key event handler for games made with the MHFramework package.
 * 
 * 
 * @author Michael Henson
 */
public class MHKeyListener implements KeyListener
{
    private final MHGameApplication adaptee;

    public MHKeyListener(final MHGameApplication app)
    {
        adaptee = app;
    }


    public void keyTyped(final KeyEvent e)
    {
        adaptee.keyTyped(e);
    }


    public void keyPressed(final KeyEvent e)
    {
        adaptee.keyPressed(e);
    }


    public void keyReleased(final KeyEvent e)
    {
        adaptee.keyReleased(e);
    }


}
