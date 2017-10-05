package mhframework;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/*********************************************************************
 * Abstract class for switching full-screen display modes and
 * maintaining references to critical singleton objects.
 *
 * <p>
 * This class iterates through an internal list of recommended display
 * modes and selects the best one supported by the system on which
 * this program is running.
 *
 * <p>
 * It contains the objects necessary for full-screen rendering along
 * with accessor methods for retrieving references to them. Included
 * classes of particular interest are:
 * <ul>
 * <li>A GraphicsEnvironment for obtaining a usable GraphicsDevice
 * <li>A GraphicsDevice for obtaining a usable GraphicsConfiguration,
 * setting the display mode, setting the application Frame to a
 * full-screen window.
 * <li>A GraphicsConfiguration for associating with an application
 * Frame upon instantiation.
 * <li>A BufferStrategy for obtaining a Graphics object for drawing.
 * </ul>
 * 
 * @author Michael Henson
 */
public abstract class MHDisplayModeChooser
{
    // //////////////////////
    // // Data members ////
    // //////////////////////
    private static GraphicsEnvironment env;

    private static GraphicsDevice device;

    private static GraphicsConfiguration gc;

    private static Canvas canvas;

    private static JFrame frame;

    private static DisplayMode displayMode;

    /** Top-left corner of screen display, to account for insets in windowed mode. */
    public static int DISPLAY_X=0, DISPLAY_Y=0;
    public static int NATIVE_DISPLAY_WIDTH=800, NATIVE_DISPLAY_HEIGHT=600;

    // Requirements for double-buffering
    /** The virtual screen in memory used for double buffering */
    private static java.awt.image.BufferedImage screen;

    private static DisplayMode[] BEST_DISPLAY_MODES = new DisplayMode[] {
                    // new DisplayMode(1280, 800, 32, 0),
                    // Widescreen mode
                    new DisplayMode(1280, 800, 32, 0),
                    new DisplayMode(1280, 720, 32, 0),
                    new DisplayMode(1024, 768, 32, 0),
                    new DisplayMode(800, 600, 32, 0),
                    new DisplayMode(800, 600, 24, 0),
                    new DisplayMode(800, 600, 16, 0),
                    new DisplayMode(640, 480, 32, 0),
                    new DisplayMode(640, 480, 24, 0),
                    new DisplayMode(640, 480, 16, 0) };

    private static boolean fullScreen;

    /*****************************************************************
     *
     * @param device
     * @return
     */
    private static DisplayMode getBestDisplayMode(final GraphicsDevice device)
    {
        for (final DisplayMode listedDisplayMode : BEST_DISPLAY_MODES)
        {
            final DisplayMode[] modes = device.getDisplayModes();
            for (final DisplayMode deviceDisplayMode : modes)
            {
                if (deviceDisplayMode.getWidth() == listedDisplayMode.getWidth()
                                && deviceDisplayMode.getHeight() == listedDisplayMode.getHeight()
                                && deviceDisplayMode.getBitDepth() == listedDisplayMode.getBitDepth())
                {
                    return listedDisplayMode;
                }
            }
        }
        return null;
    }


    /*****************************************************************
     *
     * @param device
     * @param height
     * @param width
     * @param bitDepth
     * @return
     */
    private static DisplayMode getRequestedDisplayMode(final GraphicsDevice device, final int height, final int width, final int bitDepth)
    {
        final DisplayMode[] modes = device.getDisplayModes();

        final DisplayMode requestedMode = new DisplayMode(height, width, bitDepth, DisplayMode.REFRESH_RATE_UNKNOWN);

        for (final DisplayMode deviceDisplayMode : modes)
        {
            if (deviceDisplayMode.getWidth() == requestedMode.getWidth()
                            && deviceDisplayMode.getHeight() == requestedMode.getHeight()
                            && deviceDisplayMode.getBitDepth() == requestedMode.getBitDepth())
            {
                return requestedMode;
            }
        }

        System.out.println("MHDisplayModeChooser:  Requested display mode not available.  Choosing best mode available.");
        return getBestDisplayMode(device);
    }


    /*****************************************************************
     * Returns the active BufferStrategy object being used with the
     * current display mode.
     *
     * @return The BufferStrategy currently in use.
     */
    public static BufferStrategy getBufferStrategy()
    {
        return frame.getBufferStrategy();
    }


    /*****************************************************************
     * Returns a reference to the Canvas object which represents the
     * graphics screen.
     *
     * @return The Canvas object being using in the current display
     *         mode.
     */
    public static Canvas getCanvas()
    {
        return canvas;
    }


    /*****************************************************************
     * Returns the bounds of the current screen
     *
     * @return A Rectangle object of the same dimensions as the
     *         current screen
     */
    /*public static Rectangle getScreenBounds()
    {
        return frame.getBounds();
    }*/


    /*****************************************************************
     * Configures a JFrame object for full-screen or windowed
     * rendering as specified in the MHVideoSettings parameter.
     *
     * @param appFrame
     *            The JFrame object to be configured.  If null, a new
     *            JFrame is created.
     * @param settings
     *            A MHVideoSettings object containing guideline
     *            parameters to specify details of the video mode to
     *            configure.  If null, default settings are used to
     *            attempt full-screen mode.
     */
    public static void configureGameWindow(JFrame appFrame, MHVideoSettings settings)
    {
        boolean success = false;

        if (appFrame == null)
            appFrame = new JFrame();

        // Instantiate rendering objects
        frame = appFrame;
        env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        device = env.getDefaultScreenDevice();
        gc = device.getDefaultConfiguration();

        if (!settings.fullScreen) // If user requested windowed mode...
            configureWindowedMode(settings);

        // First, find out if full screen is supported and the user requested it.
        if (device.isFullScreenSupported() && settings.fullScreen)
        {
            System.out.println("MHDisplayModeChooser:  Configuring game screen for full-screen mode.");

            // Instantiate a new Frame with the GraphicsContext object
            canvas = new Canvas(gc);
            //canvas.setSize(settings.displayWidth, settings.displayHeight);
            frame.getContentPane().add(canvas);

            // Disable the visible GUI parts of the window
            frame.setUndecorated(true);

            // Ignore paint requests from the operating system
            frame.setIgnoreRepaint(true);

            // Associate the application frame with the screen device
            device.setFullScreenWindow(frame);

            // If we are able to change the screen display . . .
            if (device.isDisplayChangeSupported())
            {
                // . . . then get the best display mode for the
                // GraphicsDevice . . .
//                if (settings == null)
//                {
//                    displayMode = getBestDisplayMode(device);
//                    settings = new MHVideoSettings();
//                }
//                else
                    displayMode = getRequestedDisplayMode(device, settings.displayWidth, settings.displayHeight, settings.bitDepth);

                if (displayMode != null)
                {
                    // . . . and set the display mode.
                    device.setDisplayMode(displayMode);

                    System.out.println("MHDisplayModeChooser:  Setting display mode ("
                                    + displayMode.getWidth() + "x"
                                    + displayMode.getHeight() + "x"
                                    + displayMode.getBitDepth() + ")");

                    success = true;
                } // if (displayMode != null)
                else
                {
                    success = false;
                    System.err.println("ERROR:  No valid display modes specified.  Trying windowed mode.");
                }
            }
            else
            {
                success = false;
                System.err.println("ERROR:  Display change not supported.  Trying windowed mode.");
            } // if (device.isDisplayChangeSupported())

            if (success)
                fullScreen = true;
            else
                configureWindowedMode(settings);

        } // if fullScreenSupported...

        // Create a BufferStrategy with two buffers
        frame.createBufferStrategy(2);
        waitForReadyStrategy();
    }


    private static void configureWindowedMode(final MHVideoSettings settings)
    {
        System.out.println("MHDisplayModeChooser:  Configuring game screen for windowed mode.");

        displayMode = new DisplayMode(settings.displayWidth, settings.displayHeight, settings.bitDepth, 0);

        final Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation((d.width - settings.displayWidth) / 2, (d.height - settings.displayHeight) / 2);
        frame.setTitle(settings.windowCaption);
        frame.setVisible(true);

        final Insets insets = frame.getInsets();
        DISPLAY_X = insets.left;
        DISPLAY_Y = insets.top;
        resizeToInternalSize(settings.displayWidth, settings.displayHeight);

        fullScreen = false;
    }

    private static void resizeToInternalSize(final int internalWidth, final int internalHeight)
    {
        final Insets insets = frame.getInsets();
        final int newWidth = internalWidth + insets.left + insets.right;
        final int newHeight = internalHeight + insets.top + insets.bottom;

        final Runnable resize = new Runnable()
        {
            public void run()
            {
                frame.setSize(newWidth, newHeight);
            }
        };

        if(!SwingUtilities.isEventDispatchThread())
        {
            try
            {
                SwingUtilities.invokeAndWait(resize);
            }
            catch(final Exception e) {}
        }
        else
            resize.run();

        frame.setResizable(false);
        frame.validate();
    }


    public static Dimension getScreenSize()
    {
        if (displayMode == null)
            displayMode = getBestDisplayMode(device);

        return new Dimension(displayMode.getWidth(), displayMode.getHeight());
    }


    /**
     * @return the frame
     */
    public static JFrame getFrame()
    {
        if (frame == null)
            frame = new JFrame();

        return frame;
    }


    /**
     * @return the buffer
     */
    public static Graphics2D getGraphics2D()
    {
        // return buffer;
        //return (Graphics2D) getBufferedImage().getGraphics();
        final Graphics2D g = (Graphics2D) getBufferStrategy().getDrawGraphics();

        // double scaleX = nativeWidth / screenWidth;
        //g.scale(scaleX, scaleY);

        return g;  //(Graphics2D) getBufferStrategy().getDrawGraphics();
    }


    public static boolean isFullScreen()
    {
        return fullScreen;
    }


    public static BufferedImage getBufferedImage()
    {
        if (screen == null)
            screen = new BufferedImage(displayMode.getWidth(), displayMode.getHeight(),
                            BufferedImage.TYPE_INT_ARGB);

        return screen;
    }



    private static void waitForReadyStrategy()
    {
        int iterations = 0;

        while(true)
        {
            try
            {
                Thread.sleep(20);
            }
            catch(final InterruptedException e) {}

            try
            {
                MHDisplayModeChooser.getBufferStrategy().getDrawGraphics();
                break;
            }
            catch(final IllegalStateException e)
            {
                System.out.println("BufferStrategy not ready yet");
            }

            iterations++;
            if(iterations == 100)
            {
                //  (Unlikely event) No use after 2 seconds (100*20ms = 2secs) give up trying
                System.out.println("Exiting Program, unable to use BufferStrategy");
                System.exit(0);
            }
        }
    }


    //public void setCursor(final String cursorImageFileName)
    //{
    //    final Image cursorImage = Toolkit.getDefaultToolkit().getImage(cursorImageFileName);
    //    final Cursor cursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImage, new Point( 0, 0), "" );
    //    frame.setCursor( cursor );
    //}

    
    public static void restoreScreen()
    { 
        Window w = device.getFullScreenWindow();
        
        if (w != null)
            w.dispose();
        
        device.setFullScreenWindow(null);
    }
    

    /**
     * TODO:  Figure out if this method can be fixed.
     *
     */
    public static void changeScreen(final boolean fullScreen)
    {
        final MHVideoSettings settings = new MHVideoSettings();
        settings.displayWidth = displayMode.getWidth();
        settings.displayHeight = displayMode.getHeight();
        settings.bitDepth = displayMode.getBitDepth();
        settings.fullScreen = fullScreen;

        configureGameWindow(frame, settings);
    }

    public static Rectangle getBounds()
    {
        return new Rectangle(0, 0, getWidth(), getHeight());
    }


    public static int getHeight()
    {
        return getScreenSize().height;
    }


    public static int getWidth()
    {
        return getScreenSize().width;
    }
    

    public static int getMaxX()
    {
        return getWidth();
    }

    
    public static int getMaxY()
    {
        return DISPLAY_Y + getHeight();
    }
    
    
    public static int getCenterX()
    {
        return getWidth()/2;
    }
    

    public static int getCenterY()
    {
        return DISPLAY_Y + getHeight()/2;
    }

}