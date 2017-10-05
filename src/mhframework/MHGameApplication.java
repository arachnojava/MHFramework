package mhframework;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import mhframework.event.MHKeyListener;
import mhframework.event.MHMouseListener;
import mhframework.event.MHMouseMotionListener;


/********************************************************************
 * Main application class for a game created with my
 * MHFramework package.
 * <p>Its responsibilities are as follows:
 * <ul>
 *   <li>Use the MHDisplayModeChooser to set up the screen
 *   <li>Register event handlers
 *   <li>Instantiate and run the game class
 *   <li>Pass events to the game class as they are received
 * </ul>
 * <p>Here is the best way to use this class.
 * <ol>
 *   <li>Create your own screen class derived from MHScreen.
 *       (See documentation for MHScreen.)
 *   <li>Copy the following source code and paste it into a new file:
 *
<pre>
// BEGIN COPIED CODE ------------------------------------------------
    public static void main(final String args[])
    {
        // TODO:  Create MHScreen subclass for testing.
        final MHScreen screen = new TestScreen();

        final MHVideoSettings settings = new MHVideoSettings();
        settings.displayWidth = 640;
        settings.displayHeight = 480;
        settings.bitDepth = 32;
        settings.fullScreen = false;
        settings.windowCaption = "MHGameApplication Test Program";

        new MHGameApplication(screen, settings);

        System.exit(0);
    }
// END COPIED CODE --------------------------------------------------
</pre>
        <ul>
            <li><i><b>Note:</b>  If you want to give your users the
            option of selecting their own video mode, you may set the
            following properties using <tt>MHAppLauncher</tt>.
            <pre>
                settings.fullScreen = MHAppLauncher.showDialog(null, true);
                settings.displayWidth = MHAppLauncher.getResolution().width;
                settings.displayHeight = MHAppLauncher.getResolution().height;
            </pre>
            </li>
        </ul>
 *
 *    <li> Change <tt>TestScreen()</tt> to the name of the screen
 *         class you created in Step 1 above.</li>
 *  </ol>
 * 
 * @author Michael Henson
 */
public final class MHGameApplication extends JFrame implements MouseListener, MouseMotionListener
{
    private static final long serialVersionUID = 1L;

    private static MHVideoSettings videoSettings;

    private final MHGame gameObject;
    private final MHScreenManager screenManager;

    /****************************************************************
     * Constructor.  Sets up the game window, instantiates the game
     * driver, registers event listeners, and initiates the game
     * process.
     */
    public MHGameApplication(final MHScreen startingScreen, final MHVideoSettings displaySettings)
    {
        videoSettings = displaySettings;
        screenManager = new MHScreenManager(startingScreen);
        gameObject = new MHGame(screenManager);
        
        // Configure the game window with the given settings.
        MHDisplayModeChooser.configureGameWindow(this, displaySettings);

        // Set up the event handlers.
        if (MHDisplayModeChooser.isFullScreen())
        {
            MHDisplayModeChooser.getCanvas().addKeyListener(new MHKeyListener(this));
            MHDisplayModeChooser.getCanvas().addMouseListener(new MHMouseListener(this));
            MHDisplayModeChooser.getCanvas().addMouseMotionListener(new MHMouseMotionListener(this));
            MHDisplayModeChooser.getCanvas().requestFocus();
        }
        else
        {
            MHDisplayModeChooser.getFrame().addKeyListener(new MHKeyListener(this));
            MHDisplayModeChooser.getFrame().addMouseListener(new MHMouseListener(this));
            MHDisplayModeChooser.getFrame().addMouseMotionListener(new MHMouseMotionListener(this));
            MHDisplayModeChooser.getFrame().requestFocus();
        }

        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(final WindowEvent e)
            {
                System.exit(0);
            }
        });


        gameObject.run();
    }


    /****************************************************************
     * Sends Key Typed events to the game object.
     *
     * @param e The key event which triggered a call to this method.
     */
    public void keyTyped(final KeyEvent e)
    {
        gameObject.keyTyped(e);
    }


    /****************************************************************
     * Sends Key Pressed events to the game object.
     *
     * @param e The key event which triggered a call to this method.
     */
    public void keyPressed(final KeyEvent e)
    {
        gameObject.keyPressed(e);
    }


    /****************************************************************
     * Sends Key Released events to the game object.
     *
     * @param e The key event which triggered a call to this method.
     */
    public void keyReleased(final KeyEvent e)
    {
        gameObject.keyReleased(e);
    }


    /****************************************************************
     * Sends Mouse Pressed events to the game object.
     *
     * @param e The mouse event which triggered a call to this method.
     */
    public void mousePressed(final MouseEvent e)
    {
        gameObject.mousePressed(e);
    }


    /****************************************************************
     * Sends Mouse Released events to the game object.
     *
     * @param e The mouse event which triggered a call to this method.
     */
    public void mouseReleased(final MouseEvent e)
    {
        gameObject.mouseReleased(e);
    }


    /****************************************************************
     * Sends Mouse Moved events to the game object.
     *
     * @param e The mouse event which triggered a call to this method.
     */
    public void mouseMoved(final MouseEvent e)
    {
        if (gameObject != null)
            gameObject.mouseMoved(e);
    }


    /****************************************************************
     * Sends Mouse Clicked events to the game object.
     *
     * @param e The mouse event which triggered a call to this method.
     */
    public void mouseClicked(final MouseEvent e)
    {
        gameObject.mouseClicked(e);
    }

    
    public void mouseDragged(MouseEvent e)
    {
        gameObject.mouseDragged(e);
    }
    

    /**
     * @return the gameObject
     */
    public final MHGame getGameObject()
    {
        return gameObject;
    }


    public static MHVideoSettings getVideoSettings()
    {
        return videoSettings;
    }
    
    
    /****************************************************************
     * FOR UNIT TESTING ONLY.
     * Instantiates a game object which kicks off the entire
     * process for running this application.  It then
     * terminates the application when control returns from
     * the game object.
     */
    public static void main(final String args[])
    {
        final MHScreen screen = new TestScreen();

        final MHVideoSettings settings = new MHVideoSettings();
        settings.displayWidth = 640;
        settings.displayHeight = 480;
        settings.bitDepth = 32;
        settings.fullScreen = false;
        settings.showSplashScreen = true;
        settings.windowCaption = "MHGameApplication Test Program";

        new MHGameApplication(screen, settings);

        System.exit(0);
    }


    @Override
    public void mouseEntered(MouseEvent arg0)
    {
        // TODO Auto-generated method stub
        
    }


    @Override
    public void mouseExited(MouseEvent arg0)
    {
        // TODO Auto-generated method stub
        
    }

}


/********************************************************************
 * MHScreen subclass for testing and demonstrating
 * MHGameApplication.
 */
class TestScreen extends MHScreen
{

    @Override
    public void load()
    {
        System.out.println("TestScreen.load()");
        this.setFinished(false);
    }


    @Override
    public void unload()
    {
        System.out.println("TestScreen.unload()");
    }


    @Override
    public void actionPerformed(final ActionEvent e)
    {
        System.out.println("TestScreen.actionPerformed()");
    }


    @Override
    public void render(final Graphics2D g)
    {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, MHDisplayModeChooser.getScreenSize().width, MHDisplayModeChooser.getScreenSize().height);
        this.centerText(g, "Test Screen", 300, Color.YELLOW, false, 0);
    }


    /* (non-Javadoc)
     * @see mhframework2.MHScreen#keyReleased(java.awt.event.KeyEvent)
     */
    @Override
    public void keyReleased(final KeyEvent e)
    {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
        {
            this.setFinished(true);
            MHGame.setProgramOver(true);
        }
    }


    @Override
    public void advance()
    {
    }
}





