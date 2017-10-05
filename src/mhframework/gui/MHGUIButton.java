package mhframework.gui;


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.io.File;
import mhframework.media.MHFont;
import mhframework.media.MHMediaTracker;
import mhframework.media.MHResourceManager;
import mhframework.media.MHSoundManager;


/********************************************************************
 * This class provides graphical image-based buttons within my game
 * framework.
 *
 * <p><b>How to add buttons to a game screen:</b>
 * <ol>
 *    <li>Create a game screen by following the instructions in the
 *        MHScreen documentation.
 *    <li>Declare an MHGUIButton object (or possibly several of them)
 *        as a data member in your screen class.
 *    <li>In the screen's constructor, instantiate the MHGUIButton
 *        object(s) and set the properties you're interested in,
 *        especially its size, position, and caption text.  Set the
 *        screen object as the action listener for the button like
 *        so:  <tt>myButton.addActionListener(this);</tt>
 *    <li>In the <tt>actionPerformed()</tt> method of your screen
 *        class, include a condition for each of your buttons like
 *        this:  <tt>if (e.getSource() == myButton)</tt> ...  Within
 *        the code blocks following each condition, write the code
 *        that will execute when each button is clicked.
 * </ol>
 * 
 * @author Michael Henson
 */
public class MHGUIButton extends MHGUIComponent
{
    // Button types
    /** Indicates button with text only. */
    public static final int TYPE_TEXT_BUTTON  = 0;

    /** Indicates button with icon and text. */
    public static final int TYPE_ICON_BUTTON  = 1;

    /** Indicates that the entire button is a single image. */
    public static final int TYPE_IMAGE_BUTTON = 2;


	// Supported image file formats
	/** Indicates that button image files are in GIF format. */
	public static final String IMAGE_TYPE_GIF = ".gif";

	/** Indicates that button image files are in PNG format. */
	public static final String IMAGE_TYPE_PNG = ".png";

	/** Indicates that button image files are in JPEG (JPG)
	 * format. */
	public static final String IMAGE_TYPE_JPG = ".jpg";


    // Button states
    /** Indicates button is in normal state */
    public static final int BUTTON_NORMAL     = 0;
    /** Indicates button is pressed down */
    public static final int BUTTON_DOWN       = 1;
    /** Indicates that mouse is over the button */
    public static final int BUTTON_OVER       = 2;


	// Default colors
    public static final Color NORMAL_COLOR = Color.LIGHT_GRAY;
    public static final Color DOWN_COLOR = Color.GRAY;
    public static final Color OVER_COLOR = Color.GRAY;



    ////////////////////
    //  Data members  //
    ////////////////////


    /** The text caption on the button */
    protected MHGUILabel caption;

	/** Color of normal state */
	protected Color normalColor;

	/** Color of down state */
	protected Color downColor;

	/** Color of mouseover state */
	protected Color overColor;

    /** The icon on the button */
    protected Image icon;

    /** Sound manager for playing "button over" and "button down" sounds.  */
    private MHSoundManager audio;
    private int overSoundID = -1, clickSoundID = -1;
    
    /**
     * Array of button images for BUTTON_NORMAL, BUTTON_DOWN, and
     * BUTTON_OVER states.
     */
    protected Image images[] = new Image[3];

    /** Indicates type of button (See TYPE_ constants) */
    protected int type;

    /** ActionEvent listener */
    protected ActionListener actionListener;

    protected KeyListener keyListener;
    protected MouseListener mouseListener;
    protected MouseMotionListener mouseMotionListener;

    /** MediaTracker for loading button images */
    private final MHMediaTracker tracker = MHMediaTracker.getInstance();

    private boolean displayBounds;


    /****************************************************************
     * Default constructor which creates a text-only button.
     */
    public MHGUIButton()
    {
        this.setText(" ");
        this.setFocusable(true);
        this.setEnabled(true);
        this.state = BUTTON_NORMAL;
        this.setSize(128, 64);
        this.type = TYPE_TEXT_BUTTON;
        this.setForeColor(Color.BLACK);

        normalColor = NORMAL_COLOR;
        downColor = DOWN_COLOR;
        overColor = OVER_COLOR;
    }


    /****************************************************************
     * Default constructor which creates a text-only button.
     */
    public MHGUIButton(final Image normal, final Image down, final Image over)
    {
    	this();

    	images[BUTTON_NORMAL] = normal;
    	images[BUTTON_DOWN] = down;
    	images[BUTTON_OVER] = over;

        if (images[0] != null)
            this.setSize(images[0].getWidth(null),
                         images[0].getHeight(null));

    }


    /****************************************************************
     * Overloaded constructor which creates an image button from
     * GIF files.
     *
     * <p>The file names of the images are generated by appending the
     * digits 0, 1, and 2 along with a .gif extension to the input
     * parameter, and then loading an image file by that name.
     * The 0 is for the image displayed in BUTTON_NORMAL state, the
     * 1 is for the BUTTON_DOWN state, and 2 is for the BUTTON_OVER
     * state.
     *
     * @param filenamebase  The base of the filename for loading
     *                       images.
     */
    public MHGUIButton(final String filenamebase)
    {
    	this(filenamebase, MHGUIButton.IMAGE_TYPE_GIF);
    }


    /****************************************************************
     * Overloaded constructor which creates an image button from
     * files of a specified type.  See the constants defined in this
     * class for a list of valid file types.
     *
     * FIXME This constructor DOES NOT WORK!  It claims that the
     * image files do not exist even when they do.
     *
     * @param filenamebase  The base of the filename for loading
     *                       images.
     * @param imagetype     A string specifying the image file format
     *                       of the button image buttons.
     */
    public MHGUIButton(final String filenamebase, final String imagetype)
    {
        java.net.URL url;

        if (filenamebase == null ||
            isValidFileType(imagetype) == false)
        {
            return;
        }

        // load button images
        for (int i = 0; i < 3; i++)
        {
        	final String filename = filenamebase + i + imagetype;
        	final File imageFile = new File(filename);

        	try
            {
            	if (imageFile.exists())
                {
            		System.out.println("Getting resource " + filename);
            		url = getClass().getResource(filename);

            		if (url == null)
            		{
            			System.out.println("Resource "+filename+" does not exist.");
            			return;
            		}

            		System.out.println("Creating image " + url.toString());
            		images[i] = MHResourceManager.loadImage(filename);

            		setType(TYPE_IMAGE_BUTTON);
            		setText("");

            		tracker.addImage(images[i], 0);
                }
            	else System.err.println("ERROR:  File " + filename + " does not exist.");
            }
            catch (final Exception e)
            {
            	System.err.println("====  EXCEPTION:  ====");
            	System.err.println(e.getMessage());

            	System.err.println("====  STACK TRACE:  ====");
            	e.printStackTrace();

            	// Just in case they don't want a mouse-over effect
            	// on the button, then just use the normal button
            	// image.
                if (i == 2)
                    images[2] = images[0];
            }
        }

        try
        {
            tracker.waitForAll();
        }
        catch (final InterruptedException e) {}

        if (images[0] != null)
            this.setSize(images[0].getWidth(null),
                         images[0].getHeight(null));
    }

    
    public void setNormalImage(Image img)
    {
        images[BUTTON_NORMAL] = img;
    }

    
    public void setDownImage(Image img)
    {
        images[BUTTON_DOWN] = img;
    }


    public void setOverImage(Image img)
    {
        images[BUTTON_OVER] = img;
    }


    private MHGUILabel getLabel()
    {
        if (caption == null)
            caption = new MHGUILabel();

        return caption;
    }

    public String getCaptionText()
    {
        return getLabel().text;
    }

    public MHFont getFont()
    {
        return getLabel().getFont();
    }

	/****************************************************************
	 */
	private boolean isValidFileType(final String filetype)
	{
		if (filetype == IMAGE_TYPE_GIF  ||
    		filetype == IMAGE_TYPE_PNG  ||
	    	filetype == IMAGE_TYPE_JPG)
	    	return true;

		return false;
	}


    public void render(final Graphics2D g)
    {
        if (!isVisible())
            return;

        // Draw button image if available
        if (images != null && images[state] != null)
            g.drawImage(images[state], getX(), getY(), null);
        else
        {
            if (state == BUTTON_NORMAL)
                g.setColor(normalColor);
            else if (state == BUTTON_OVER)
                g.setColor(overColor);
            else if (state == BUTTON_DOWN)
                g.setColor(downColor);

            g.fill3DRect(getX(), getY(), this.width, this.height,
                         (state!=BUTTON_DOWN));
        }

        // Draw icon if we're "that kind" of button
        if (type == TYPE_ICON_BUTTON && icon != null)
        {
            // find center (x, y) coordinates, then draw the icon there
            final int centerX = (this.width/2) - (icon.getWidth(null)/2);
            final int centerY = (this.height/2) - (icon.getHeight(null)/2);
            g.drawImage(icon, centerX, centerY, null);
        }

        // Draw text if there is any
        if (caption != null)
        {
            renderCaption(g);
        }

        if (displayBounds)
            g.drawRect(getX(), getY(), width, height);

    }


    protected void renderCaption(final Graphics2D g)
    {
        caption.centerOn(getBounds(), g);
        caption.render(g);
    }


    public void setFont(final MHFont font)
    {
        caption.setFont(font);
    }


    public void setText(final String text)
    {
        if (caption == null)
            caption = new MHGUILabel(text);
        else
            caption.setText(text);
    }


    public void setForeColor(final Color c)
    {
        caption.setPaint(c);
    }


    /****************************************************************
     * Sets the button to its "pressed" state if it is enabled and
     * the click event took place over the button.
     */
    @Override
    public void mousePressed(final MouseEvent e)
    {
         if(getBounds().contains(e.getPoint()) && isEnabled())
         {
             state = BUTTON_DOWN;
             playButtonDownSound();
         }
    }


    /****************************************************************
     * Simulates the "clicking" of a button by informing the action
     * listener that a click event took place.  If the mouse was
     * released not while over the button, the button is returned to
     * its normal state and no action is performed.
     */
    @Override
    public void mouseReleased(final MouseEvent e)
    {
         // only process enabled components
         if(isEnabled())
         {
              // trigger the event only if the mouse is still over the button
              if(getBounds().contains(e.getPoint()))
              {
                   if(state == BUTTON_DOWN)
                   {
                        final ActionEvent thisEvent = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "");

                        // let the listener know something went down
                        if (this.actionListener != null)
                            actionListener.actionPerformed(thisEvent);
                   }

                   // restore the OVER state
                   state = BUTTON_OVER;
              }

              // return the button to normal if it wasn't released over the button
              else
              {
                   state = BUTTON_NORMAL;
              }
         }
    }


    public void addActionListener(final java.awt.event.ActionListener listener)
    {
        this.actionListener = listener;
    }


    public void addKeyListener(final java.awt.event.KeyListener listener)
    {
        this.keyListener = listener;
    }


    public void addMouseListener(final java.awt.event.MouseListener listener)
    {
        this.mouseListener = listener;
    }


    public void addMouseMotionListener(final java.awt.event.MouseMotionListener listener)
    {
        this.mouseMotionListener = listener;
    }


    public void advance()
    {

    }


    @Override
    public void actionPerformed(final ActionEvent e)
    {
        if (this.isEnabled() && this.actionListener != null)
            this.actionListener.actionPerformed(e);
    }


    @Override
    public void mouseClicked(final MouseEvent e)
    {
        if (this.mouseListener != null)
            this.mouseListener.mouseClicked(e);
    }


    @Override
    public void mouseMoved(final MouseEvent e)
    {
         if(isEnabled())
         {
              if(getBounds().contains(e.getPoint()))
              {
                  if (state != BUTTON_OVER)
                  {
                      if (this.mouseListener != null)
                      {
                          e.setSource(this);
                          this.mouseListener.mouseEntered(e);
                      }
                      
                      playButtonOverSound();
                  }

                  state = BUTTON_OVER;
                  setFocus(true);
              }
              else
              {
                  if (state != BUTTON_NORMAL && mouseListener != null)
                  {
                      e.setSource(this);
                      mouseListener.mouseExited(e);
                  }
                  
                  state = BUTTON_NORMAL;
                  setFocus(false);
              }
         }
    }

    
    protected void playButtonOverSound()
    {
        if (audio != null && overSoundID != -1)
        {
            //audio.stop(overSoundID);
            audio.play(overSoundID);
        }
    }


    protected void playButtonDownSound()
    {
        if (audio != null && clickSoundID != -1)
            audio.play(clickSoundID);
    }


    @Override
    public void keyPressed(final KeyEvent e)
    {
        if (keyListener != null)
        	keyListener.keyPressed(e);
    }


    @Override
    public void keyReleased(final KeyEvent e)
    {

    }


    @Override
    public void keyTyped(final KeyEvent e)
    {

    }
	/**
	 * Returns the downColor.
	 * @return Color
	 */
	public Color getDownColor()
	{
		return downColor;
	}

	/**
	 * Returns the normalColor.
	 * @return Color
	 */
	public Color getNormalColor()
	{
		return normalColor;
	}

	/**
	 * Returns the overColor.
	 * @return Color
	 */
	public Color getOverColor()
	{
		return overColor;
	}

	/**
	 * Sets the downColor.
	 * @param downColor The downColor to set
	 */
	public void setDownColor(final Color downColor)
	{
		this.downColor = downColor;
	}

	/**
	 * Sets the normalColor.
	 * @param normalColor The normalColor to set
	 */
	public void setNormalColor(final Color normalColor)
	{
		this.normalColor = normalColor;
	}

	/**
	 * Sets the overColor.
	 * @param overColor The overColor to set
	 */
	public void setOverColor(final Color overColor)
	{
		this.overColor = overColor;
	}

	/**
	 * Returns the icon.
	 * @return Image
	 */
	public Image getIcon()
	{
		return icon;
	}
	
	
	public Image getImage()
	{
	    return images[this.state];
	}

	/**
	 * Sets the icon.
	 * @param icon The icon to set
	 */
	public void setIcon(final Image icon)
	{
		this.icon = icon;

        for (int i = 0; i <= 2; i++)
        {
        	images[i] = icon;
        }
	}

	/**
	 * Returns the type.
	 * @return int
	 */
	public int getType()
	{
		return type;
	}

	/**
	 * Sets the type.
	 * @param type The type to set
	 */
	public void setType(final int type)
	{
		this.type = type;

		if (this.type == MHGUIButton.TYPE_IMAGE_BUTTON)
		    setText("");
	}

	private int getMaxWidth()
	{
	    try {
	    int w = images[0].getWidth(null);

	    for (final Image btnImg: images)
	        w = Math.max(w, btnImg.getWidth(null));

	    return w;
	    }catch(final Exception e){}

	    return this.width;
	}

	private int getMaxHeight()
    {
        try {
        int h = images[0].getHeight(null);

        for (final Image btnImg: images)
            h = Math.max(h, btnImg.getHeight(null));

        return h;
        }catch(final Exception e){}

        return this.height;
    }

    protected Rectangle2D updateBounds()
    {
        // set width and height
        this.setSize(getMaxWidth(), getMaxHeight());

        // tell superclass to recalculate
        return super.getBounds();
    }


    public void showBounds(final boolean show)
    {
        displayBounds = show;
    }
    
    
    public void setButtonOverSound(MHSoundManager soundManager, int soundID)
    {
        audio = soundManager;
        overSoundID = soundID;
    }
    
    
    public void setButtonDownSound(MHSoundManager soundManager, int soundID)
    {
        audio = soundManager;
        clickSoundID = soundID;
    }
    
    
    public void setButtonSounds(MHSoundManager soundManager, int downID, int overID)
    {
        setButtonDownSound(soundManager, downID);
        setButtonOverSound(soundManager, overID);
    }
}
