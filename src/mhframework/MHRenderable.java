package mhframework;

import java.awt.Graphics2D;

/**
 * <p>Everything in the game that appears on screen and manages its own data
 * must implement this interface.  It enforces the requirement that every visible
 * thing in the game universe must have at least two capabilities:
 * <ol>
 *     <li>The ability to update its own data
 *     <li>The ability to draw itself onto a specified Graphics object
 * </ol>
 * <p>It is important to note that the MHActor class implements this interface.
 * Therefore, every class derived from MHActor automatically inherits the abilities
 * set forth in this interface.
 * 
 * @author Michael Henson
 */
public interface MHRenderable
{
    /**
     * Tells an object to update its data.
     */
    public abstract void advance();

    /**
     * Tells an object to draw itself onto the sent Graphics object.
     *
     * @param g The Graphics2D object on which this object is to draw itself.
     */
    public abstract void render(Graphics2D g);

}