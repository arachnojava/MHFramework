package mhframework;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.Serializable;

/********************************************************************
 * This class drives the entire game process.
 * 
 * @author Michael Henson
 */
public final class MHGame implements Serializable
{
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    /** The screen manager. */
    static MHScreenManager    screenManager;
    
    static MHRuntimeMetrics   timer = new MHRuntimeMetrics();;

    private static boolean    programOver;

    /****************************************************************
     * Constructor.
     */
    public MHGame(final MHScreenManager s)
    {
        screenManager = s;

        Runtime.getRuntime().addShutdownHook(new Thread()
            {
                public void run()
                {
                    programOver = true;
                    MHDisplayModeChooser.restoreScreen();
                }
            });
    }


    /****************************************************************
     * Executes the game loop.
     * 
     * The entire game loop consists of the following steps:
     * <ul>
     * <li>While the program is not finished:
     * <ol>
     * <li>Record the start time of the current iteration
     * <li>Tell the screen manager to advance (update) itself
     * <li>Tell the screen manager to render itself
     * <li>Record the ending time of the current iteration
     * <li>Calculate how long we should "sleep" before continuing, and then
     * sleep for that amount of time.
     * <li>Resynchronize the frame rate by updating without rendering, if
     * necessary.
     * </ol>
     * </ul>
     */
    public final void run()
    {
        BufferedImage backBuffer = new BufferedImage(MHDisplayModeChooser.getWidth(), MHDisplayModeChooser.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = (Graphics2D)backBuffer.getGraphics();
        
        // Loop until the program is over
        while (!programOver)
        {
            // Record the starting time of the loop
            timer.recordStartTime();

            // Update the screen data
            screenManager.advance();

            // Draw the updated screen
            screenManager.render(g);
            MHDisplayModeChooser.getGraphics2D().drawImage(backBuffer, MHDisplayModeChooser.DISPLAY_X, MHDisplayModeChooser.DISPLAY_Y, MHDisplayModeChooser.getWidth(), MHDisplayModeChooser.getHeight(), null);
            
            // Record the ending time of the loop
            timer.recordEndTime();

            // Calculate how long it took to run this loop and
            // use that value to see how long we should wait
            // (or "sleep") before starting the next iteration
            timer.sleep();

            // Separate UPS from FPS to maintain a  better frame rate.
            while (timer.shouldUpdate())
                screenManager.advance();
        } // while (!programOver) . . .

        MHDisplayModeChooser.restoreScreen();

    } // run()


    /****************************************************************
     * Sends Key Typed events to the screen manager.
     * 
     * @param e
     *            The event which triggered a call to this method.
     */
    public void keyTyped(final KeyEvent e)
    {
        screenManager.keyTyped(e);
    }


    /****************************************************************
     * Sends Key Pressed events to the screen manager.
     * 
     * @param e
     *            The event which triggered a call to this method.
     */
    public void keyPressed(final KeyEvent e)
    {
        screenManager.keyPressed(e);
    }


    /****************************************************************
     * Sends Key Released events to the screen manager.
     * 
     * @param e
     *            The event which triggered a call to this method.
     */
    public void keyReleased(final KeyEvent e)
    {
        screenManager.keyReleased(e);
    }


    /****************************************************************
     * Sends Mouse Pressed events to the screen manager.
     * 
     * @param e
     *            The event which triggered a call to this method.
     */
    public void mousePressed(final MouseEvent e)
    {
        screenManager.mousePressed(e);
    }


    /****************************************************************
     * Sends Mouse Moved events to the screen manager.
     * 
     * @param e
     *            The event which triggered a call to this method.
     */
    public void mouseMoved(final MouseEvent e)
    {
        screenManager.mouseMoved(e);
    }


    /****************************************************************
     * Sends Mouse Released events to the screen manager.
     * 
     * @param e
     *            The event which triggered a call to this method.
     */
    public void mouseReleased(final MouseEvent e)
    {
        screenManager.mouseReleased(e);
    }


    /****************************************************************
     * Sends Mouse Clicked events to the screen manager.
     * 
     * @param e
     *            The event which triggered a call to this method.
     */
    public void mouseClicked(final MouseEvent e)
    {
        screenManager.mouseClicked(e);
    }


    public static MHScreenManager getScreenManager()
    {
        return screenManager;
    }


    public static boolean isProgramOver()
    {
        return programOver;
    }


    public static void setProgramOver(final boolean isOver)
    {
        programOver = isOver;
    }


    public static int getFramesPerSecond()
    {
        return timer.getFramesPerSecond();
    }


    public static int getUpdatesPerSecond()
    {
        return timer.getUpdatesPerSecond();
    }

    
    public static int getTimeSpentInGame()
    {
        return timer.getTimeSpentInGame();
    }
    
    
    /** Returns the value of the game timer in nanoseconds. */
    public static long getGameTimerValue()
    {
        return timer.getGameTimerValue();
    }


    public void mouseDragged(MouseEvent e)
    {
        screenManager.mouseDragged(e);
    }
}
