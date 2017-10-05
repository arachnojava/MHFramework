package mhframework;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import mhframework.media.MHImageGroup;


/********************************************************************
 * Base class for creating interactive or autonomous game actors.
 *
 * <p>
 * </p>
 *
 * @author Michael Henson
 */
public class MHActor implements MHRenderable
{
    private double hSpeed;    // horizontal speed
    private double vSpeed;    // vertical speed
    private double speed;     // linear speed
    private int maxHealth;    // max health
    private int health;       // current health

    /** The actor's current animation sequence. */
    private int animationSequence;

    /** The number of this actor's current animation frame. */
    private int frame;

    /** The actor's location. */
    private final MHPoint location;

    /* Sprite information */
    private MHImageGroup images;
    private int frameTimer;
    private Rectangle2D bounds;

    /* Transform */
    private double scale = 1.0f;
    private double rotation = 0.0f;
    private double rotationSpeed = 0.0f;


    /****************************************************************
     * Constructor.
     *
     */
    public MHActor()
    {
    	super();

    	location = new MHPoint(0, 0);
    	frame=0;
    	animationSequence=0;
    }


    /****************************************************************
     * Sets the horizontal movement speed of this actor.
     *
     * @param speed The number of pixels to move left or right on
     *              each iteration of the game loop.  A positive
     * 				input causes the actor to move to the right, and
     *              a negative input causes movement to the left.
     */
    public void setHorizontalSpeed(final double speed)
    {
        hSpeed = speed;
    }


    /****************************************************************
     * Returns the horizontal speed value for this actor.
     *
     * @return The horizontal speed value for this actor.
     */
    public double getHorizontalSpeed()
    {
        return hSpeed;
    }


    /****************************************************************
     * Returns the vertical speed value for this actor.
     *
     * @return The vertical speed value for this actor.
     */
    public double getVerticalSpeed()
    {
        return vSpeed;
    }


    /****************************************************************
     * Sets the vertical movement speed of this actor.
     *
     * @param speed The number of pixels to move up or down on each
     *              iteration of the game loop.  A positive input causes the
     *              actor to go down, and a negative input causes it to go up.
     */
    public void setVerticalSpeed(final double speed)
    {
        vSpeed = speed;
    }


    /****************************************************************
     * Sets the maximum health value for this actor.
     *
     * @param max The maximum health value for this actor
     */
    public void setMaxHealth(final int max)
    {
        maxHealth = max;
    }


    /****************************************************************
     * Returns this actor's maximum health value
     *
     * @return This actor's maximum health value
     */
    public int getMaxHealth()
    {
        return maxHealth;
    }


    /****************************************************************
     * Returns this actor's current health value
     *
     * @return This actor's current health value
     */
    public int getHealth()
    {
        return health;
    }


     /**
     * @param health the health to set
     */
    public void setHealth(final int health)
    {
        this.health = health;
    }


    /****************************************************************
     * Draws the actor onto the sent graphics object based on its
     * current action, animation frame number, and (x, y) coordinates
     *
     * @param g The Graphics object on which to draw the actor
     */
    public void render(final Graphics2D g)
    {
        if (getImage() == null)
            return;

        final int w = (int) (getImage().getWidth(null) * scale);
        final int h = (int) (getImage().getHeight(null) * scale);

        final AffineTransform originalTransform = g.getTransform();
        g.rotate(rotation * (Math.PI / 180.0), getX()+w/2, getY()+h/2);  //.rotate(rotation * Math.PI / 180.0, w / 2.0, h / 2.0);
        g.drawImage(getImage(), (int)getX(), (int)getY(), w, h, null); //(int)x, (int)y, w, h, null);
        g.setTransform(originalTransform);
    }


    /****************************************************************
     * Draws the actor onto the sent graphics object at the (x, y)
     * coordinates specified by rx and ry.
     *
     * @param g The Graphics object on which to draw the actor
     */
    public void render(final Graphics2D g, final int rx, final int ry)
    {
    	if (getImage() == null)
    	    return;

        final int w = (int) (getImage().getWidth(null) * scale);
        final int h = (int) (getImage().getHeight(null) * scale);

        final AffineTransform originalTransform = g.getTransform();
        g.rotate(rotation * (Math.PI / 180.0), rx + w/2, h/2);  //.rotate(rotation * Math.PI / 180.0, w / 2.0, h / 2.0);
        g.drawImage(getImage(), rx, ry, w, h, null); //(int)x, (int)y, w, h, null);
        g.setTransform(originalTransform);
    }


    /****************************************************************
     * Returns current animation frame.
     */
    public Image getImage()
    {
    	Image img = null;

        try { img = images.getImage(animationSequence, frame); }
        catch (final Exception e) { img = null; }

        return img;
    }


    /****************************************************************
     * Updates the actor's animation frame number, location, and
     * rotation.  Should usually be
     * overridden by a subclass method which calls this
     * implementation, then proceeds to do any class-specific updates
     * of its own.
     */
    public void advance()
    {
        setLocation(location.getX() + hSpeed, location.getY() + vSpeed);

        setRotation(rotation + rotationSpeed);

        frameTimer++;

        try
        {
            // if frame time expired, update frame number and reset timer
            if (frameTimer > images.getDuration(animationSequence, frame))
            {
                frameTimer = 0;

                // If we haven't yet reached the end of a frame sequence,
                // increment the frame number.  Else, reset the frame
                // number to zero.
                if (frame < images.getFrameCount(animationSequence)-1)
                    frame++;
                else
                    frame = 0;
            }
        }
        catch (final Exception e) {}
    }


    /****************************************************************
     * Assigns an existing MHImageGroup to this actor.  This set of
     * images will provide the actor's appearance.
     *
     * @param ig A reference to an existing MHImageGroup object.
     */
    public final void setImageGroup(final MHImageGroup ig)
    {
        images = ig;
    }


    public MHImageGroup getImageGroup()
    {
        if (images == null)
            images = new MHImageGroup();

        return images;
    }


    /****************************************************************
     * Calculates and returns the bounding rectangle for this actor's
     * current sprite image without regard to scaling.
     *
     * @return A Rectangle2D object representing this actor's
     *          bounding rectangle
     */
    public Rectangle2D getBounds()
    {
        bounds = new Rectangle2D.Double();

        try
        {
            final Image img = getImage();  // optimization to reduce method calls

            bounds.setRect(getX(), getY(),
                            img.getWidth(null),
                            img.getHeight(null));
        }
        catch (final NullPointerException e)
        {
            //System.err.println("ERROR:  Null pointer exception in MHActor.getBounds()");
            bounds.setRect(getX(), getY(), 1, 1);
        }

        return bounds;
    }


    public int getWidth()
    {
        return (int)getScaledBounds().getWidth();
    }



    public int getHeight()
    {
        return (int)getScaledBounds().getHeight();
    }

    /****************************************************************
     * Calculates and returns the bounding rectangle for this actor's
     * current sprite image at its current scale.
     *
     * @return A Rectangle2D object representing this actor's
     *          scaled bounding rectangle
     */
    public Rectangle2D getScaledBounds()
    {
        final Rectangle2D scaledBounds = (Rectangle2D) getBounds().clone();

        final Image img = getImage();  // optimization to reduce method calls
        if (img != null)
        {
        double origW = img.getWidth(null);
        double origH = img.getHeight(null);
        final double scale = getScale(); // optimization to reduce method calls

        origW = (origW < 1 ? 1 : origW);
        origH = (origH < 1 ? 1 : origH);

        scaledBounds.setRect(getX(), getY(), origW * scale, origH * scale);
        }
        else
            scaledBounds.setRect(getX(), getY(), 1, 1);


        return scaledBounds;
    }


    public double getCenterX()
    {
        return getScaledBounds().getCenterX();
    }


    public double getCenterY()
    {
        return getScaledBounds().getCenterY();
    }


    /****************************************************************
     * Returns the frameTimer.
     *
     * @return int
     */
    public int getFrameTimer()
    {
    	return frameTimer;
    }


    public int getFrameCount()
    {
        return getImageGroup().getFrameCount(getAnimationSequenceNumber());
    }

    
    public MHPoint getLocation()
    {
        return location;
    }
    

    /****************************************************************
     * Returns the x coordinate of this actor's left edge
     *
     * @return The x coordinate of this actor's left edge
     */
    public double getX()
    {
        return location.getX();
    }


    /****************************************************************
     * Returns the y coordinate of this actor's top side
     *
     * @return The y coordinate of this actor's top side
     */
    public double getY()
    {
        return location.getY();
    }


    /****************************************************************
     * Sets the value of the frame timer.
     *
     * @param frameTimer The frameTimer to set
     */
    public void setFrameTimer(final int frameTimer)
    {
    	this.frameTimer = frameTimer;
    }


    /****************************************************************
     * Sets the x.
     *
     * @param px The x to set
     */
    public void setX(final double px)
    {
    	location.setX(px);
    }


    /****************************************************************
     * Sets the y.
     *
     * @param py The y to set
     */
    public void setY(final double py)
    {
        location.setY(py);
    }


    /****************************************************************
     * Positions the actor at the coordinates specified by the input
     * (x, y) parameters.
     *
     * @param px The x coordinate at which to position the actor
     * @param py The y coordinate at which to position the actor
     */
    public void setLocation(final double px, final double py)
    {
        location.setX(px);
        location.setY(py);
    }


    /****************************************************************
     * Returns the actor's current action, which is also the number
     * of the actor's current animation sequence.
     *
     * @return The actor's current action.
     */
    public int getAnimationSequenceNumber()
    {
    	return animationSequence;
    }


    /****************************************************************
     * Sets the actor's action.  The action indicates which animation
     * sequence to perform.
     *
     * @param action The action to set
     */
    public void setAnimationSequence(final int action)
    {
        this.frame = 0;
    	this.animationSequence = action;
    }


    /**
     * @return The number of the actor's current animation frame.
     */
    public int getFrameNumber()
    {
    	return frame;
    }


    /**
     * @param frameNumber The frame number to set.
     */
    public void setFrameNumber(final int frameNumber)
    {
    	if (frameNumber >= getImageGroup().getFrameCount(animationSequence))
    		frame = 0;
    	else if (frameNumber < 0)
    		frame = images.getFrameCount(animationSequence) - 1;
    	else
    		frame = frameNumber;
    }


    public boolean isAnimationFinished()
    {
        return getFrameNumber() >= getFrameCount()-1;
    }


    /**
     * @return the scale
     */
    public double getScale()
    {
        return scale;
    }


    /**
     * @param scale the scale to set
     */
    public void setScale(final double scale)
    {
        this.scale = scale;
    }


    public double getRotation()
    {
        return rotation;
    }


    public void setRotation(final double rotation)
    {
        double r = rotation % 360;

        if (r < 0) r = 360 + r;

        this.rotation = r;
    }


    public double getRotationSpeed()
    {
        return rotationSpeed;
    }


    public void setRotationSpeed(final double rotationSpeed)
    {
        this.rotationSpeed = rotationSpeed;
    }


    /**
     * @return the speed
     */
    public double getSpeed()
    {
        return speed;
    }


    /**
     * @param speed the speed to set
     */
    public void setSpeed(final double speed)
    {
        this.speed = speed;
    }
}
