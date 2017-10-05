package mhframework.event;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import mhframework.MHGameApplication;


/********************************************************************
 * Mouse motion listener for the games made with the MHFramework
 * package.
 * 
 * @author Michael Henson
 */
public class MHMouseMotionListener implements MouseMotionListener
{
    private static Point mousePoint;
    private final MHGameApplication adaptee;

    /****************************************************************
     * Constructor.
     *
     * @param app The MHGameApplication object for whom we are
     *            handling mouse motion events.
     */
    public MHMouseMotionListener(final MHGameApplication app)
    {
        adaptee = app;
        mousePoint = new Point(0, 0);
    }


    /****************************************************************
     * Not currently implemented.
     */
    public void mouseDragged(final MouseEvent e)
    {
        mousePoint = e.getPoint();
        adaptee.mouseDragged(e);
    }


    /****************************************************************
     * Sends Mouse Moved events to the game application.
     *
     * @param e The mouse event that triggered a call to this
     *           method.
     */
    public void mouseMoved(final MouseEvent e)
    {
        mousePoint = e.getPoint();
        adaptee.mouseMoved(e);
    }

    
    public static Point getMousePoint()
    {
        return mousePoint;
    }
}
