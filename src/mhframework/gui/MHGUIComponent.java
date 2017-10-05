package mhframework.gui;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import mhframework.MHRenderable;

/**
 * Base class for creating custom visual components.
 * 
 * @author Michael Henson
 */
public abstract class MHGUIComponent
        implements MHRenderable, ActionListener, MouseListener
{
    // Data Members
    /** Upper-left corner position. TODO:  Replace with MHPoint. */
    private int x, y;

    /** Size of the component. */
    protected int width, height;

    /** Flag indicating whether component is enabled. */
    private boolean enabled;

    /** Flag indicating whether component is visible. */
    private boolean visible;

    /** Flag indicating whether component can get focus. */
    protected boolean focusable;

    /** Flag indicating whether component currently has focus. */
    private boolean focused;
    
    
    private Rectangle2D bounds;

    /** The state of the component. This has different
     * meanings for different types of components. */
    protected int state;
    
    
    private String tooltip;
    
    // Constructor  =======================================
    public MHGUIComponent()
    {
        setEnabled(true);
        setVisible(true);
        setFocusable(true);
        setFocus(false);
        x = 0;
        y = 0;
        width = 120;
        height = 80;
        state = 0;
    }
    //=====================================================


//    public void paint(final Graphics2D g)
//    {
//        if (visible) render(g);
//    }

    
    public void setEnabled(final boolean e)
    {
        enabled = e;
    }


    public boolean isEnabled()
    {
        return enabled;
    }


    public void setVisible(final boolean v)
    {
        visible = v;
    }


    public boolean isVisible()
    {
        return visible;
    }


    public void setFocusable(final boolean f)
    {
        focusable = f;
    }


    public void setFocus(final boolean f)
    {
        focused = f;
    }


    public boolean hasFocus()
    {
        return focused;
    }


    public void setX(final int px)
    {
        x = px;
    }


    public int getX()
    {
        return x;
    }


    public void setY(final int py)
    {
        y = py;
    }


    public int getY()
    {
        return y;
    }


    public void setPosition(final int px, final int py)
    {
        setX(px);
        setY(py);
    }


    public void setSize(final int w, final int h)
    {
        width = w;
        height = h;
    }


    public Rectangle2D getBounds()
    {
        if (bounds == null)
            bounds = new Rectangle2D.Double();
        bounds.setRect(getX(), getY(), width, height);
        return bounds;
    }


    public int getWidth()
    {
        return width;
    }


    public void setWidth(final int width)
    {
        this.width = width;
    }


    public int getHeight()
    {
        return height;
    }


    public void setHeight(final int height)
    {
        this.height = height;
    }

    
    public void setToolTip(String text)
    {
        tooltip = text;
    }

    
    public String getToolTip()
    {
        return tooltip;
    }

    
    public void actionPerformed(final ActionEvent e)
    {}

    public abstract void mouseClicked(MouseEvent e);
    public abstract void mousePressed(MouseEvent e);
    public abstract void mouseReleased(MouseEvent e);
    public abstract void mouseMoved(MouseEvent e);
    public abstract void keyPressed(KeyEvent e);
    public abstract void keyReleased(KeyEvent e);
    public abstract void keyTyped(KeyEvent e);

    public final void mouseEntered(final MouseEvent e)
    {}

    public final void mouseExited(final MouseEvent e)
    {}

    public final void mouseDragged(final MouseEvent e)
    {}
    
    

}
