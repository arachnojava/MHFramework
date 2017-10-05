package mhframework.media;


import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;


/********************************************************************
   <p>Maintains a complete set of animation sequences
   (MHFrameSequence objects) for a particular type of
   actor (MHActor).

   <p>Objects requiring groups of images can instantiate an MHImageGroup
   and use it for all sprite animation capabilities.  Client
   classes are encouraged to keep their own sequence ID numbers,
   and are required to track their own sequences and frame numbers.

   <p>These classes are organized in a composition hierarchy like this:
   <ul>
     <li>An MHImageGroup contains a list of MHFrameSequence objects
       <ul><li>An MHFrameSequence contains a list of MHAnimationFrame objects</ul>
   </ul>

    <p>How to use this system:
    <ol>
     <li>Declare and instantiate an MHImageGroup object
     <li>Call addSequence with a sequence ID number
     <li>Call addFrame to add frames to a sequence
    </ol>
    
    @author Michael Henson
*/
public final class MHImageGroup
{
    // Data members

    private final ArrayList<MHFrameSequence> sequences;

	/****************************************************************
	 * Constructor.
	 */
    public MHImageGroup()
    {
        sequences = new ArrayList<MHFrameSequence>();
    }

    
    public MHImageGroup(String spriteSheetFileName, int frameWidth, int frameHeight)
    {
        this();
        
        Image spriteSheet = loadImage(spriteSheetFileName);
        // TODO:  Open sprite sheet file, retrieve frames, call overloaded addFrame with image objects.
    }

    /****************************************************************
     * Adds an animation frame to an existing frame sequence.
     *
     * @param sequenceID  The sequence to which the frame is to be
     *                     added.
     * @param imageFile   The name of the image file containing the
     *                     image to be used for the animation frame.
     * @param duration    The duration in loop iterations for which
     *                     this frame is shown before advancing to
     *                     the next frame.
     */
    public void addFrame(final int sequenceID, final String imageFile,
                                                        final int duration)
    {
        final Image newImage = loadImage(imageFile);
        addFrame(sequenceID, newImage, duration);
    }


    public static Image loadImage(final String imageFile)
    {
        Image newImage;

        try
        {
            newImage = ImageIO.read(new File(imageFile));
        }
        catch (final IOException e)
        {
            // URL for opening image file
            final URL url = MHImageGroup.class.getResource(imageFile);
            newImage = Toolkit.getDefaultToolkit().createImage(url);
        }

        MHMediaTracker.getInstance().addImage(newImage, MHMediaTracker.getInstance().getImageCount()+1);

        return newImage;
    }


    /****************************************************************
     * Adds an animation frame to an existing frame sequence
     *
     * @param sequenceID  The sequence to which the frame is to be
     *                     added.
     * @param newImage    The image to be used for the animation
     *                     frame.
     * @param duration    The duration in loop iterations for which
     *                     this frame is shown before advancing to
     *                     the next frame.
     */
    public void addFrame( final int sequenceID, final Image newImage,
                                                        final int duration)
    {
        final MHAnimationFrame newFrame =
                            new MHAnimationFrame(newImage, duration);

		final MHFrameSequence targetSequence = sequences.get(sequenceID);

        targetSequence.add(newFrame);

        MHMediaTracker.getInstance().addImage(newImage, 0);
    }


    /****************************************************************
     * Adds a new animation sequence to an image group.  If the
     * requested ID for the new sequence is already in use, the old
     * sequence is overwritten.
     *
     * @param sequenceID  The number of the sequence to be added.
     */
    public void addSequence(final int sequenceID)
    {
        for (int i = 0; i < sequenceID; i++)
        {
            if (i >= sequences.size() || sequences.get(i) == null)
                sequences.add(i, new MHFrameSequence());
        }

        sequences.add(sequenceID, new MHFrameSequence());
    }


	public boolean sequenceExists(final int sequenceID)
	{
		boolean exists = true;

		try
		{
		    if (sequences == null || sequences.get(sequenceID) == null)
                exists = false;
		}
		catch (final Exception e)
		{
            exists = false;
		}

        return exists;
	}


    /****************************************************************
     * Returns the requested image from the image group.
     */
    public Image getImage(final int sequenceID, final int frameNumber)
    {
		try
		{
            final MHFrameSequence seq = sequences.get(sequenceID);
            if (seq == null) return null;
            final MHAnimationFrame frame = seq.get(frameNumber);

            return frame.image;
		}
		catch (final Exception e)
		{
			return null;
		}
    }


    /****************************************************************
     * Returns the duration of the specified frame.
     */
    public int getDuration(final int sequenceID, final int frameNumber)
    {
        int duration = 0;
        try
        {
            final MHFrameSequence seq = sequences.get(sequenceID);
            if (seq == null)
                duration = 0;
            else
            {
                final MHAnimationFrame frame = seq.get(frameNumber);
                duration = frame.duration;
            }
        }
        catch (final IndexOutOfBoundsException e)
        {
            duration = 0;
        }

        return duration;
    }


    /****************************************************************
     * Sets the duration of the specified frame.
     */
    public void setDuration(final int sequenceID, final int frameNumber, final int newDuration)
    {
        try
        {
            final MHFrameSequence seq = sequences.get(sequenceID);
            if (seq != null)
            {
                final MHAnimationFrame frame = seq.get(frameNumber);
                frame.duration = newDuration;
            }
        }
        catch (final IndexOutOfBoundsException e)
        {
        }
    }

    /****************************************************************
     * Returns the number of frames in the specified sequence.
     *
     * @param sequenceID  The ID number of the sequence whose frame
     *                     count is requested.
     *
     * @return The number of frames in the sequence.
     */
    public int getFrameCount(final int sequenceID)
    {
        if (sequences.size() <= sequenceID || sequences.get(sequenceID) == null)
            return 0;

        final MHFrameSequence seq = sequences.get(sequenceID);

        return seq.getFrameCount();
    }


    /****************************************************************
     * Returns the number of animation sequences in this image group.
     *
     * @return The total number of animation sequences in this
     *          image group.
     */
    public int getNumSequences()
    {
        return sequences.size();
    }


}


/********************************************************************
 * The MHAnimationFrame class represents a single frame of animation
 * in a frame sequence.
 *
 * It is a non-public class defined in MHImageGroup.java.
 */
class MHAnimationFrame
{
    ////////////////////////
    ////  Data members  ////
    ////////////////////////

    /** The image for this frame of animation */
    public Image image;

    /** The number of loop iterations for which this frame is displayed
     *  before advancing to the next frame. */
    public int duration;


    /****************************************************************
     * Default constructor.  Creates an MHAnimationFrame with a null
     * image and a zero duration.
     */
    public MHAnimationFrame()
    {
        this(null, 0);
    }


    /****************************************************************
     * Overloaded constructor.  Creates an MHAnimationFrame with the
     * given image and duration.
     */
    public MHAnimationFrame(final Image newImage, final int newDuration)
    {
        image = newImage;
        duration = newDuration;
    }

}



/********************************************************************
 * A complete set of animation frames for a single action or
 * activity.
 *
 * It is a non-public class defined in MHImageGroup.java.
 */
class MHFrameSequence
{
    // Data members
    private final ArrayList<MHAnimationFrame> list;

    /****************************************************************
     * Constructor.
     */
    public MHFrameSequence()
    {
        list = new ArrayList<MHAnimationFrame>();
    }


    /****************************************************************
     * Adds a new animation frame to this sequence.
     *
     * @param newFrame the MHAnimationFrame object to be added
     */
    public void add(final MHAnimationFrame newFrame)
    {
        list.add(newFrame);
    }


    /****************************************************************
     * Retrieves the animation frame at the requested index.
     *
     * @param index  The number of the animation frame to be
     *                retrieved.
     *
     * @return The animation frame at the specified index.
     */
    public MHAnimationFrame get(int index)
    {
		if (index >= list.size())
		    index %= list.size();

        return list.get(index);
    }


    /****************************************************************
     * Returns the number of animation frames in this frame sequence.
     *
     * @return The number of frames in this sequence.
     */
    public int getFrameCount()
    {
        return list.size();
    }
}

