package mhframework;


import java.awt.Image;
import javax.sound.midi.Sequence;
import mhframework.media.MHImageGroup;
import mhframework.media.MHMediaTracker;
import mhframework.media.MHMidiPlayer;
import mhframework.media.MHSoundManager;
import mhframework.tilemap.MHTileSetManager;

/********************************************************************
 * <p>A facade for game engine utilities in MHFramework.  This class
 * is currently being redesigned to behave more like an actual facade
 * that simplifies the usage of these vital subsystems.</p>
 *
 * <h1>Old Documentation (Deprecated!)</h1>
 * <p>This is the generic base class for managing game data.  Due to
 * the application-specific nature of a game's data model, this class
 * must be extended and specialized in order to be used.
 *
 * <p>Subclasses of MHDataModel should:
 * <ul>
 *     <li>Maintain their own image groups (MHImageGroup objects), but use
 *         the MediaTracker in this class for tracking image loading.
 *     <li>Maintain their own sound data identifiers, but use the
 *         MHSoundManager in this class for storing and playing the sounds.
 *     <li>Behave as singletons by making the constructor private and
 *         storing a private static final reference along with a public
 *         static method for obtaining a handle.
 * </ul>
 *
 * <p>Here is a primitive outline which may be used as a starting point for your
 * customized data model.  Note the private member <tt>INSTANCE</tt>, the
 * private constructor, and the <tt>getInstance()</tt> method.
<pre>

// BEGIN COPIED CODE ------------------------------------------------

import mhframework.*;
import java.awt.*;

public class MHDataModelTest extends MHDataModel
{
    private static MHDataModelTest INSTANCE = new MHDataModelTest();

    private MHDataModelTest()
    {
    }

    public static MHDataModel getInstance()
    {
        return INSTANCE;
    }
}

// END COPIED CODE --------------------------------------------------

</pre>
 *
 * @author Michael Henson
 * @version 1.0
 */
public abstract class MHDataModel
{
    /**
     * Flag for indicating that the application should terminate.
     */
    private final boolean programOver = false;

    /** MHSoundManager for organizing and playing sound effects. */
    private MHSoundManager soundManager;

    /** MHMidiPlayer for creating and player MIDI sequences. */
    private MHMidiPlayer midiPlayer;

    /**
	 * MHTileSetManager for managing a single repository of tile
	 * images.
	 */
	private static MHTileSetManager tileSetManager;


	/****************************************************************
     * Returns a reference to the sound manager.
     *
     *  @return A reference to the sound manager.
     */
    public MHSoundManager getSoundManager()
    {
    	if (soundManager == null)
			soundManager = new MHSoundManager();

        return soundManager;
    }


    public int loadSoundFile(final String filename)
    {
        return getSoundManager().addSound(filename);
    }


	/****************************************************************
	 * Returns a reference to the tile set manager.
	 *
	 * @return A reference to the tile set manager.
	 */
	public static MHTileSetManager getTileSetManager()
	{
		if (tileSetManager == null)
			tileSetManager = new MHTileSetManager();

		return tileSetManager;
	}


	/****************************************************************
     * Determines whether the entire program has finished executing.
     *
     * @return The value of the "program over" flag.
     */
    public boolean isProgramOver()
    {
        return programOver;
    }


    public Image loadImage(final String filename)
    {
        return MHImageGroup.loadImage(filename);
    }


    /**
     * @return the midiPlayer
     */
    public MHMidiPlayer getMidiPlayer()
    {
        if (midiPlayer == null)
            midiPlayer = new MHMidiPlayer();

        return midiPlayer;
    }



    public Sequence loadMidiFile(final String filename)
    {
        return getMidiPlayer().getSequence(filename);
    }


    public static MHMediaTracker getMediaTracker()
    {
        return MHMediaTracker.getInstance();
    }
}

