package mhframework.media;

import java.io.File;
import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;

/********************************************************************
 * Plays MIDI files.
 *
 * <p>This class originated as the MidiPlayer class provided
 * in the book <i>Developing Games in Java</i> by David Brackeen.</p>
 * That code did contain one defect, however, which has been repaired
 * for this version.
 *
 * @author Michael Henson
 */
public class MHMidiPlayer implements MetaEventListener
{
    // Midi meta event
    public static final int END_OF_TRACK_MESSAGE = 47;

    private Sequencer sequencer;
    private boolean loop = false;
    private boolean paused = false;

    /****************************************************************
     * Constructor.
     */
    public MHMidiPlayer()
    {
        try
        {
            sequencer = MidiSystem.getSequencer();
            sequencer.open();
            sequencer.addMetaEventListener(this);
        }
        catch (final MidiUnavailableException e)
        {
            System.err.println("ERROR:  MIDI sequencer unavailable.");
            sequencer = null;
        }
    }


    /****************************************************************
     * Loads a sequence from the file system.  Returns null if an
     * error occurs.
     */
    public Sequence getSequence(final String filename)
    {
        try
        {
            return MidiSystem.getSequence(new File(filename));
        }
        catch (final InvalidMidiDataException imde)
        {
            imde.printStackTrace();
            return null;
        }
        catch (final IOException ioe)
        {
            ioe.printStackTrace();
            return null;
        }
    }


    /****************************************************************
     * Plays a sequence, optionally looping.  This method returns
     * immediately.  The sequence is not played if it is invalid.
     */
    public void play(final Sequence sequence, final boolean loop)
    {
        if (sequencer != null && sequence != null)
        {
            try
            {
                sequencer.setSequence(sequence);
                sequencer.start();
                this.loop = loop;
            }
            catch (final InvalidMidiDataException imde)
            {
                imde.printStackTrace();
            }
        }
        else
            System.err.println("ERROR:  Cannot play MIDI sequence.");
    }


    /****************************************************************
     * This method is called by the sound system when a meta event
     * occurs.  In this case, when the end-of-track meta event is
     * received, the sequence is restarted if looping is on.
     */
    public void meta(final MetaMessage event)
    {
        if (event.getType() == END_OF_TRACK_MESSAGE)
        {
            if (sequencer != null && sequencer.isOpen() && loop)
            {
                sequencer.setMicrosecondPosition(0);
                sequencer.start();
            }
        }
    }


    /****************************************************************
     * Stops the sequencer and resets its position to 0.
     */
    public void stop()
    {
        if (sequencer != null && sequencer.isOpen())
        {
            sequencer.stop();
            sequencer.setMicrosecondPosition(0);
        }
    }


    /****************************************************************
     * Closes the sequencer.
     */
    public void close()
    {
        if (sequencer != null && sequencer.isOpen())
            sequencer.close();
    }


    /****************************************************************
     * Gets the sequencer.
     */
    public Sequencer getSequencer()
    {
        return sequencer;
    }


    /****************************************************************
     * Sets the paused state.  Music may not immediately pause.
     */
    public void setPaused(final boolean paused)
    {
        if (this.paused != paused && sequencer != null)
        {
            this.paused = paused;
            if (paused)
                sequencer.stop();
            else
                sequencer.start();
        }
    }


    /****************************************************************
     * Returns the paused state.
     */
    public boolean isPaused()
    {
        return paused;
    }


    public static void main(final String args[])
    {
        final MHMidiPlayer p = new MHMidiPlayer();
        final String f = "C:/Dev/CornShark/sounds/UIMusic.mid";
        final Sequence s = p.getSequence(f);

        System.out.println("Playing " + f);
        p.play(s, true);
    }
}
