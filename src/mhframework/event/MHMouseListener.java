package mhframework.event;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import mhframework.MHGameApplication;


/********************************************************************
 * Mouse event handler for games made with the MHFramework package.
 * 
 * @author Michael Henson
 */
public class MHMouseListener implements MouseListener
{
    private final MHGameApplication adaptee;
    private static Point clickPoint, releasePoint;

    public MHMouseListener(final MHGameApplication app)
    {
        adaptee = app;
    }


    public void mousePressed(final MouseEvent e)
    {
        this.clickPoint = e.getPoint();
        adaptee.mousePressed(e);
    }


    public void mouseReleased(final MouseEvent e)
    {
        MHMouseListener.releasePoint = e.getPoint();
        adaptee.mouseReleased(e);
    }


    public void mouseClicked(final MouseEvent e)
    {
        adaptee.mouseClicked(e);
    }


    @Override
    public void mouseEntered(final MouseEvent arg0)
    {
        // TODO Auto-generated method stub

    }


    @Override
    public void mouseExited(final MouseEvent arg0)
    {
        // TODO Auto-generated method stub

    }


    public static Point getClickPoint()
    {
        Point p = clickPoint;
        clickPoint = null;
        return p;
    }


    public static Point getReleasePoint()
    {
        Point p = releasePoint;
        releasePoint = null;
        return p;
    }
}
