package mhframework;


import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import mhframework.gui.MHGUIComponent;
import mhframework.gui.MHGUIComponentList;
import mhframework.media.MHFont;


/********************************************************************
 * This is an abstract base class for deriving game screens.  It
 * provides the following required pieces:
 * <ul>
 *     <li>Reference to next screen to be loaded, if any
 *     <li>Flag to indicate when screen is finished
 *     <li>List of custom image-based GUI components
 *         (MHGUIComponentList)
 * </ul>
 *
 * <p><b>How to create a new game screen:</b>
 * <ol>
 *     <li>Create a class that extends MHScreen.
 *         <b>Recommendation:</b>  Derive a fairly generic class from
 *         MHScreen, then use that class as your base for the
 *         specific game screens.  This will provide the flexibility
 *         of processing events to satisfy your application's needs,
 *         while maintaining the reusability of this MHScreen class.
 *     <li>Define the following methods:
 *         <ul>
 *             <li><tt>load()</tt>    (Called automatically by screen
 *                                    manager when changing screens.)
 *
 *             <li><tt>unload()</tt>  (Called automatically by screen
 *                                    manager when changing screens.)
 *
 *             <li><tt>advance()</tt> (Required by MHRenderable
 *                                    interface.)
 *
 *             <li><tt>render(Graphics2D)</tt>  (To make your screen show
 *                                    something.)
 *
 *             <li><tt>actionPerformed()</tt>  (So we can use the
 *                                    screen as an action event
 *                                    handler.)
 *
 *             <li>any other event handlers you need
 *                 (An MHScreen object is the final destination of
 *                 the long chain of event handler calls.)
 *         </ul>
 *
 *     <li>Add the screen to the MHScreenManager object for your
 *         application.
 * </ol>
 *
 * <p>And that's it!  The screen manager (MHScreenManager) will hold
 * the screen while the game class (MHGame) runs it.  Note that
 * neither MHScreenManager nor MHGame need to be extended.  They were
 * designed to work autonomously in their current forms.
 *
 * <p><b>How to change screens in an application:</b>
 * <ol>
 *     <li>Define multiple subclasses of MHScreen.
 *     <li>When it is time to change to the next screen, call
 *         <tt>setNextScreen()</tt> and pass it a
 *         reference to the next screen to be displayed.
 *         Then do this:  <tt>setFinished(true);</tt>
 *     <li>To go back to the previous screen:<br>
 *             <tt>setNextScreen(null);</tt><br>
 *             <tt>setFinished(true);</tt><br>
 * </ol>
 *
 *
 * @author Michael Henson
 */
public abstract class MHScreen
                              implements MHRenderable, ActionListener
{
    /** References the next screen to be displayed.
     * If null, previous screen is displayed. */
    private MHScreen next;

    /** References the previous screen that was displayed.
     */
    private MHScreen previous;

    /** Flag indicating when screen is finished executing */
    private boolean finished = false;

    /** Flag indicating whether screen is disposable when it's
     * finished. */
    private boolean disposable = false;

    /** List of GUI components on the screen. */
    private final MHGUIComponentList components;

    // Error message support.
    private boolean isErrorDisplayed = false;
    private String strErrorMessage;

    // Status bar.
    public static final int statusBarHeight = 18;
    private Font statusBarFont = new Font("SansSerif", Font.PLAIN, 14);


    /****************************************************************
     * Default constructor.
     */
    public MHScreen()
    {
        components = new MHGUIComponentList();
    }


    public MHGUIComponentList getComponentList()
    {
        return this.components;
    }


    /****************************************************************
     * Set up a screen and prepare it to run.  This method is called
     * before every time this screen is displayed.
     */
    public abstract void load();


    /****************************************************************
     * Perform termination housekeeping or reset the screen
     * variables when a screen has finished executing.
     */
    public abstract void unload();


    /****************************************************************
     * Adds an MHGUIComponent object to the screen.
     *
     * @param c The component to be added
     */
    public void add(final MHGUIComponent c)
    {
        components.add(c);
    }


    /****************************************************************
     * Removes an MHGUIComponent object from the screen.
     *
     * @param c The component to be removed
     */
    public void remove(final MHGUIComponent c)
    {
        components.remove(c);
    }


    /****************************************************************
     * Return a reference to the GUI component that currently has the
     * focus.
     */
    public MHGUIComponent getFocusedComponent()
    {
        return components.getFocusedComponent();
    }


    /****************************************************************
     * Sets the focus to the next component capable of receiving
     * focus.  If the search for this component reaches the end of
     * the component list, it wraps around to the beginning and
     * continues until a focusable component is located.
     */
    public void nextFocusableComponent()
    {
        components.nextFocusableComponent();
    }


    /****************************************************************
     * Sets the focus to the previous component capable of receiving
     * focus. If the search for this component reaches the beginning
     * of the component list, it wraps around to the end and
     * continues until a focusable component is located.
     */
    public void prevFocusableComponent()
    {
        components.prevFocusableComponent();
    }


    /****************************************************************
     * Sets a reference to the next screen to be pushed when this one
     * is finished.  If the application should return to the previous
     * screen, the next screen can be set to null to indicate this.
     *
     * @param nextScreen The next screen to be displayed
     */
    public void setNextScreen(final MHScreen nextScreen)
    {
        next = nextScreen;
    }


    /****************************************************************
     * Returns a reference to the next screen to be pushed.
     * If this method returns null, this will cause the current
     * screen to be popped from the stack and the previous screen
     * will be displayed.
     */
    public MHScreen getNextScreen()
    {
        return next;
    }


    /****************************************************************
     * Draws the GUI components onto the sent Graphics object.
     * This method is intended to be overridden by methods that
     * do their own specialized rendering, then call this base-class
     * version to draw the components.
     */
    public void render(final java.awt.Graphics2D g)
    {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                             RenderingHints.VALUE_ANTIALIAS_ON);

        components.render(g);

        if (isErrorDisplayed)
        {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, MHDisplayModeChooser.getScreenSize().width, MHDisplayModeChooser.getScreenSize().height);
            g.setFont(new Font("Monospaced", Font.BOLD+Font.ITALIC, 30));
            centerText(g, this.strErrorMessage, MHDisplayModeChooser.getScreenSize().height/2, Color.RED, true, 4);
        }
    }

    public void advance()
    {
        components.advance();
    }
    
    
    public void fill(Graphics2D g, Color fillColor)
    {
        g.setColor(fillColor);
        g.fillRect(0, 0, MHDisplayModeChooser.getWidth(), MHDisplayModeChooser.getHeight());
    }


    /**
     * Remember, this method adjusts for windows insets!
     * 
     * @param g
     * @param text
     * @param y
     * @param c
     * @param shadowed
     * @param shadowDistance
     */
    public void centerText(final Graphics g, final String text, final int y, final Color c, final boolean shadowed, final int shadowDistance)
    {
        final FontMetrics fm = g.getFontMetrics(g.getFont());
        final int width = fm.stringWidth(text);
        final int scrW = MHDisplayModeChooser.getScreenSize().width;
        final int centerX = ((scrW/2) - (width/2));

        if (shadowed)
        {
            g.setColor(Color.black);
            g.drawString(text, centerX+shadowDistance, y+shadowDistance);
        }

        g.setColor(c);
        g.drawString(text, centerX, y);
    }

    /** Centers text at screen center */
    public void centerText(final Graphics2D g, final String text, final int y, MHFont mhFont)
    {
        centerText(g, text, MHDisplayModeChooser.getCenterX(), y, mhFont);
    }

    
    /** Centers text at x parameter. */
    public void centerText(final Graphics2D g, final String text, int x, final int y, MHFont mhFont)
    {
        int width = mhFont.stringWidth(text);
        int centerX = (x - (width/2));
        mhFont.drawString(g, text, centerX, y);
    }

    
    public void centerComponent(final MHGUIComponent c)
    {
        final int center = (int) (MHDisplayModeChooser.getBounds().getWidth()/2 - c.getWidth()/2);
        c.setPosition(center, c.getY());
    }

    protected void drawStatusBar(String text, Graphics2D g)
    {
        int w = (int) MHDisplayModeChooser.getWidth();
        int y = (int)(MHDisplayModeChooser.getHeight()-statusBarHeight );
        
        g.setColor(Color.BLACK);
        g.fillRect(0, y, w, statusBarHeight);
        
        g.setFont(statusBarFont);
        g.setColor(Color.WHITE);
        g.drawString(text, 5, y+statusBarHeight-5);
    }
    
    public void showErrorMessage(final String text)
    {
        strErrorMessage = text;
        isErrorDisplayed = true;
    }


    public void hideErrorMessage()
    {
        isErrorDisplayed = false;
    }

    /****************************************************************
     * Sets the value of the "finished" flag to the input parameter.
     *
     */
    public void setFinished(final boolean f)
    {
        finished = f;
    }


    /****************************************************************
     * Returns the value of the "finished" flag.
     *
     * @return True if the screen has finished executing, false
     *          otherwise.
     */
    public boolean isFinished()
    {
        return finished;
    }


    /**
     * @return A boolean indicating whether this screen is disposable.
     */
    public boolean isDisposable()
    {
        return disposable;
    }


    /**
     * @param disposable A flag to indicate that screen can be deallocated.
     */
    public void setDisposable(final boolean disposable)
    {
        this.disposable = disposable;
    }


    public static void tileImage(final Graphics2D g, final Image image, int x, int y)
    {
        while (x > 0) x -= image.getWidth(null);
        while (y > 0) y -= image.getHeight(null);

        int cx = x;
        int cy = y;
        final Dimension res = MHDisplayModeChooser.getScreenSize();
        final int imgWidth = image.getWidth(null);
        final int imgHeight = image.getHeight(null);

        while (cy < res.height+imgHeight)
        {
            while (cx < res.width+imgWidth)
            {
                g.drawImage(image, cx, cy, null);
                cx += imgWidth;
            }
            cx = x;
            cy += imgHeight;
        }
    }

    /****************************************************************
     * Delivers Key Pressed events to the GUI components.  This
     * method is intended to be overridden in derived classes which
     * may handle the event on their own, then optionally call this
     * version.
     *
     * @param e The event that triggered a call to this method.
     */
    public void keyPressed(final KeyEvent e)
    {
//        if (e.getKeyCode() == KeyEvent.VK_F12)
//        {
//            System.out.println("F12:  Changing screen mode");
//            MHDisplayModeChooser.changeScreen(!MHDisplayModeChooser.isFullScreen());
//        }
        //hideErrorMessage();
        components.keyPressed(e);
    }


    /****************************************************************
     * Delivers Key Released events to the GUI components.  This
     * method is intended to be overridden in derived classes which
     * may handle the event on their own, then optionally call this
     * version.
     *
     * @param e The event that triggered a call to this method.
     */
    public void keyReleased(final KeyEvent e)
    {
        components.keyReleased(e);
    }


    /****************************************************************
     * Delivers Key Typed events to the GUI components.  This method
     * is intended to be overridden in derived classes which may
     * handle the event on their own, then optionally call this
     * version.
     *
     * @param e The event that triggered a call to this method.
     */
    public void keyTyped(final KeyEvent e)
    {
        components.keyTyped(e);
    }


    private MouseEvent adjustMousePosition(final MouseEvent e)
    {
        return new MouseEvent((Component) e.getSource(),
                                        e.getID(),
                                        e.getWhen(),
                                        e.getModifiers(),
                                        e.getX(),
                                        e.getY(),
                                        e.getClickCount(),
                                        e.isPopupTrigger());
    }

    /****************************************************************
     * Delivers Mouse Pressed events to the GUI components.  This
     * method is intended to be overridden in derived classes which
     * may handle the event on their own, then optionally call this
     * version.
     *
     * @param e The event that triggered a call to this method.
     */
    public void mousePressed(final MouseEvent e)
    {
        hideErrorMessage();
        components.mousePressed(adjustMousePosition(e));
    }


    /****************************************************************
     * Delivers Mouse Released events to the GUI components.  This
     * method is intended to be overridden in derived classes which
     * may handle the event on their own, then optionally call this
     * version.
     *
     * @param e The event that triggered a call to this method.
     */
    public void mouseReleased(final MouseEvent e)
    {
        components.mouseReleased(adjustMousePosition(e));
    }


    /****************************************************************
     * Delivers Mouse Clicked events to the GUI components.  This
     * method is intended to be overridden in derived classes which
     * may handle the event on their own, then optionally call this
     * version.
     *
     * @param e The event that triggered a call to this method.
     */
    public void mouseClicked(final MouseEvent e)
    {
        components.mouseClicked(adjustMousePosition(e));
    }


    /****************************************************************
     * Delivers Mouse Moved events to the GUI components.  This
     * method is intended to be overridden in derived classes which
     * may handle the event on their own, then optionally call this
     * version.
     *
     * @param e The event that triggered a call to this method.
     */
    public void mouseMoved(final MouseEvent e)
    {
        components.mouseMoved(adjustMousePosition(e));
    }


    public final void setCursor(final String cursorImageFileName)
    {
        setCursor(cursorImageFileName, new Point(0, 0));
    }


    public final void setCursor(final String cursorImageFileName, final Point hotspot)
    {
        final Image cursorImage = Toolkit.getDefaultToolkit().getImage(cursorImageFileName);
        setCursor(cursorImage, hotspot);
    }

    public final void setCursor(final Image cursorImage)
    {
        setCursor(cursorImage, new Point(0, 0));
    }


    public final void setCursor(final Image cursorImage, final Point hotspot)
    {
        BufferedImage adjustCursor = new BufferedImage(MHDisplayModeChooser.DISPLAY_X+cursorImage.getWidth(null), 2*MHDisplayModeChooser.DISPLAY_Y+cursorImage.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics g = adjustCursor.getGraphics();
        g.drawImage(cursorImage, MHDisplayModeChooser.DISPLAY_X, 2*MHDisplayModeChooser.DISPLAY_Y, null);
        
        final Cursor cursor = Toolkit.getDefaultToolkit().createCustomCursor(adjustCursor, hotspot, "" );
        MHDisplayModeChooser.getFrame().setCursor(cursor);
    }


    /**
     * @return the previous
     */
    public MHScreen getPreviousScreen()
    {
        return previous;
    }


    /**
     * @param previous the previous to set
     */
    public void setPreviousScreen(final MHScreen previous)
    {
        this.previous = previous;
    }


    public void removeComponents()
    {
        this.getComponentList().clear();
    }


    public void mouseDragged(MouseEvent e)
    {
        components.mouseDragged(e);
    }
}

