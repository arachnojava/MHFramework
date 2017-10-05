package mhframework.media;


import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Vector;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;

/********************************************************************
 * Manages all sound data for a game application made with the
 * MHFramework package.  I recommend that maintaining the sound
 * manager for a game application should be the responsibility of
 * the application's data model.
 *
 * <p>This class borrows heavily from the SoundManager class
 * presented in the the book <i>Java 1.4 Game Programming</i> by
 * Andrew Mulholland and Glenn Murphy (Wordware Publishing).
 * 
 * @author Michael Henson
 */
public class MHSoundManager implements LineListener
{
    /**
     * Constant for telling the MHSoundManager object to
     * automatically assign an available channel number for playing
     * a sound.
     */
    public static final int AUTO_ASSIGN_CHANNEL = -1;


    ////////////////////////
    ////  Data Members  ////
    ////////////////////////


    /** Array of audio clips. */
    private final Clip channelArray[];

    /** Array of channel IDs. */
    private final int channelSoundIdRef[];

    /** Vector of sound data. */
    private final Vector<byte[]> soundByteData;

    private boolean soundOn = true;

    ///////////////////
    ////  Methods  ////
    ///////////////////


    /****************************************************************
     * Constructor.
     *
     * Instantiates internal data structures and initializes the
     * channel IDs.
     */
	public MHSoundManager()
	{
		// Initialize the vector to hold the sound data...
		soundByteData = new Vector<byte[]>();
		channelSoundIdRef = new int[32];
		channelArray = new Clip[32];

		for(int i=0; i<32; i++)
		{
			channelSoundIdRef[i] = -1;
		}
	}


    /****************************************************************
     * Adds a sound file to the sound manager.
     *
     * @param filepath  The path and file name of the sound file to
     *                   be added.
     *
     * @return If successful, the position in the sound data vector
     * 			where the new sound byte data was stored;
     *          otherwise -1.
     */
	public int addSound(final String filepath)
	{
		// Can't use this in an applet?
		final File soundFile = new File(filepath);

		if(!soundFile.isFile())
		{
			System.err.println("ERROR:  Sound File '" + filepath +
			                   "' does not exist.");
			return -1;
		}

		final byte[] tempArray = new byte[(int)soundFile.length()];

		try
		{
			final DataInputStream inputStream = new DataInputStream(
			                         new FileInputStream(soundFile));
			inputStream.read(tempArray);
			inputStream.close();
		}
		catch(final IOException e)
		{
			System.out.println(e);
			System.out.println("ERROR:  There was a problem " +
    			                   "reading sound file " + filepath);
			return -1;
		}

		// Add it to the Vector...
		soundByteData.add(tempArray);

		// return its position in the vector...
		return soundByteData.size()-1;
	}


    public synchronized void play(final int soundId)
    {
        play(soundId, false, MHSoundManager.AUTO_ASSIGN_CHANNEL);
    }
    
    
    public synchronized void play(final int[] sounds)
    {
        new Thread()
        {
            public void run()
            {
                for (int soundID = 0; soundID < sounds.length; soundID++)
                {
                    play(sounds[soundID]);
                    while (isSoundPlaying(sounds[soundID]));
                }
            }
        }.start();
    }
    
    
    
    
    /****************************************************************
     * Plays a sound.
     *
     * Loads sound byte data into the channel specified by the input
     * channel ID.  If the <tt>channelId</tt> parameter is the
     * constant <tt>MHSoundManager.AUTO_ASSIGN_CHANNEL</tt>, then an
     * available channel is automatically assigned.  The sound data
     * is then looped if <tt>loop</tt> is true, or just played once
     * if <tt>loop</tt> is false.
     *
     * <p><b>Note:</b>  This method was made "synchronized" due to
     * problems with calling it from event handlers.  The same
     * channel was being allocated to multiple sound instances,
     * which caused the program to hang.
     *
     * @param soundId   ID of which sound to play.
     * @param loop      True if sound should be looped.
     * @param channelId ID of the channel on which to play the
     *                   sound.
     */
	public synchronized void play(final int soundId,
	                                 final boolean loop,
	                                 final int channelId)
	{
	    if (!isSoundOn()) return;
	    
	    // Check the channelId is valid...
		if(channelId < -1 || channelId >= 32)
		{
			System.out.println("ERROR:  Channel ID out of range");
			return;
		}

		// Check the soundId is valid...
		if(soundId < 0 || soundId >= soundByteData.size())
		{
			System.out.println("ERROR:  Sound ID out of range");
			return;
		}



        int validChannelId = -1;  // variable for locating valid
                                  // channel ID

		// This if/else structure locates a valid channel ID.
		if(channelId == AUTO_ASSIGN_CHANNEL)
		{
			// we need to find a suitable channel...

			// first find a free channel...
			for(int i=0; i<32; i++)
			{
				if(channelArray[i] == null ||
				   !channelArray[i].isOpen())
				{
					// this one will do...
					validChannelId = i;
					break;
				} //if
			} // for

			if(validChannelId == -1)
			{
				System.out.println("ERROR:  Could not find a " +
       	    		                   "suitable sound channel.");
				return;
			}
        }
		else
        {
		    // we need to ensure the selected channel is stopped...
		    stopChannel(channelId);

		    // set the valid channel id...
		    validChannelId = channelId;
        }

		//System.out.println("Allocating channel " + validChannelId +
        //                                    " for sound " + soundId);

		try
		{
			final AudioInputStream audioInputStream =
			    AudioSystem.getAudioInputStream(
			        new ByteArrayInputStream(
                        soundByteData.get(soundId)));

			// retreive the audio format...
			final AudioFormat audioFormat = audioInputStream.getFormat();

			// set the line up
			final DataLine.Info dataLineInfo =
			    new DataLine.Info(
			            Clip.class,
			            audioInputStream.getFormat(),
			            ((int)audioInputStream.getFrameLength() *
                            audioFormat.getFrameSize()));

			// assign a clip (channel) for the sample
			channelArray[validChannelId] =
        			      (Clip) AudioSystem.getLine(dataLineInfo);

			channelArray[validChannelId].addLineListener(this);

			channelArray[validChannelId].open(audioInputStream);
			channelSoundIdRef[validChannelId] = soundId;

			// play the clip (or loop it)
			if(loop)
			{
				channelArray[validChannelId].loop(
      				                       Clip.LOOP_CONTINUOUSLY);
			}
			else
			{
				channelArray[validChannelId].loop(0); // was 1
			}

		}
		catch(final Exception e)
		{

			System.out.println(e);
			if(channelArray[validChannelId] != null)
			{
				if(channelArray[validChannelId].isOpen())
				{
					channelArray[validChannelId].close();
				}

				channelArray[validChannelId] = null;
				channelSoundIdRef[validChannelId] = -1;
			}
		}
	}


    /****************************************************************
     * Stops the sound specified by <tt>soundId</tt> if it is
     * currently playing.
     *
     * @param soundId The ID of the sound to be stopped.
     */
	public void stop(final int soundId)
	{
		// find the first occurrence of the sound and stop it...
		for(int i=0; i<32; i++)
		{
			if(channelSoundIdRef[i] == soundId)
			{
				// reset the channel...
				// System.out.println("Stopping Channel "+i);
				channelArray[i].stop();
				break;
			}
		}
	}


    /****************************************************************
     * Stops the sound playing in the channel specified by channelId.
     *
     * @param channelId The channel containing the sound to be
     *                   stopped.
     */
	public void stopChannel(final int channelId)
	{
		if(isChannelPlaying(channelId))
		{
			channelArray[channelId].stop();
			// note the 'update' method closes the channel properly
		}
	}


    /****************************************************************
     * States whether the requested channel is currently playing a
     * sound.
     *
     * <p>Checks two conditions to determine this:
     * <ol>
     *   <li>Does the channel contain sound byte data?
     *   <li>Is the channel open?
     * </ol>
     *
     * <p>If both of the above conditions are true, the channel is
     * playing a sound.
     *
     * @param channelId The ID of the channel to be checked for
     *                   sound data.
     *
     * @return True if channel is playing a sound; false otherwise.
     */
	public boolean isChannelPlaying(final int channelId)
	{
		return (channelArray[channelId] != null &&
	            channelArray[channelId].isOpen());
	}


    /****************************************************************
     * States whether the sound specified by soundId is currently
     * playing.
     *
     * @param soundId The ID of the sound in question.
     *
     * @return True if sound is playing; false otherwise.
     */
	public boolean isSoundPlaying(final int soundId)
	{
		// check to see if any occurence of the sound is playing...
		for(int i=0; i<32; i++)
		{
			if(channelSoundIdRef[i] == soundId)
			{
				return true;
			}
		}
		return false;
	}


    /****************************************************************
     * Event handler for line events.  If the type of event is
     * <tt>LineEvent.Type.STOP</tt>, this method finds the channel
     * associated with the line and resets it, and then closes the
     * line.
     *
     * @param e The line event being handled.
     */
	public void update(final LineEvent e)
	{
		// handles samples stopping...
		if(e.getType() == LineEvent.Type.STOP)
		{
			// find the channel this line relates to...
			for(int i=0; i<32; i++)
			{
				if(channelArray[i] == e.getLine())
				{
					// reset the channel...
					channelArray[i] = null;
					channelSoundIdRef[i] = -1;
				}
			}

			// close the line...
			e.getLine().close();
		}
	}


    /****************************************************************
     * Stops all active channels.
     */
	public void stopAllChannels()
	{
		// stop active channels...
		for(int i=0; i<32; i++)
			stopChannel(i);
	}


    public void remove(int soundID)
    {
        // TODO Auto-generated method stub
        
    }


    public void setSoundOn(boolean soundOn)
    {
        this.soundOn = soundOn;
    }


    public boolean isSoundOn()
    {
        return soundOn;
    }

}