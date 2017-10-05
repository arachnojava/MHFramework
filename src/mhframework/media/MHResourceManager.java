package mhframework.media;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.net.URL;
import javax.imageio.ImageIO;
import mhframework.tilemap.MHTileSetManager;

/********************************************************************
 * Provides access to resource management objects using a variation
 * of the Flyweight Pattern.
 * 
 * @author Michael Henson
 *
 */
public class MHResourceManager
{
    private static MHTileSetManager tileSetManager;
    private static MHMidiPlayer midiPlayer;
    private static MHSoundManager soundManager;
    private static MHMediaTracker mediaTracker;

    
    public static MHTileSetManager getTileSetManager()
    {
        if (tileSetManager == null)
            tileSetManager = new MHTileSetManager();

        return tileSetManager;
    }


    public static MHMidiPlayer getMidiPlayer()
    {
        if (midiPlayer == null)
            midiPlayer = new MHMidiPlayer();

        return midiPlayer;
    }


    public static MHSoundManager getSoundManager()
    {
        if (soundManager == null)
            soundManager = new MHSoundManager();

        return soundManager;
    }


    public static MHMediaTracker getMediaTracker()
    {
        if (mediaTracker == null)
            mediaTracker = MHMediaTracker.getInstance();

        return mediaTracker;
    }


    
    public static Image loadImage(final String imageFile)
    {
        Image newImage = null;

        try
        {
            // The following line only works for images not in a JAR file.
            newImage = ImageIO.read(new File(imageFile));
        }
        catch (final Exception e)
        {
            // The following line only works for images in MHFramework.
            URL url = MHResourceManager.class.getResource(imageFile);
            newImage = Toolkit.getDefaultToolkit().createImage(url);
        }

        MHMediaTracker.getInstance().addImage(newImage, MHMediaTracker.getInstance().getImageCount()+1);

        return newImage;
    }
    
    
//    public static Image loadImage(final String imageFile)
//    {
//        Image newImage = null;
//
//        // The following line only works for images in MHFramework.
//        final URL url = MHImageGroup.class.getResource(imageFile);
//
//        try
//        {
//            //newImage = ImageIO.read(url);
//
//            // To retrieve images from game's JAR file:
//            // jar:file:<location of JAR file>!imageFile
//
//            // The following line only works for images not in a JAR file.
//            newImage = ImageIO.read(new File(imageFile));
//        }
//        catch (final Exception e)
//        {
//            newImage = Toolkit.getDefaultToolkit().createImage(url);
//        }
//
//        MHMediaTracker.getInstance().addImage(newImage, MHMediaTracker.getInstance().getImageCount()+1);
//
//        return newImage;
//    }

    /****************************************************************
     * Loads images from a JAR file.
     *
     * @param imageFile The name of the image file to load.
     * @param jarFile   The name of the JAR file containing the
     *                  image file.
     * @return Image object containing image data from file.
     */
    public static Image loadImage(final String imageFile, final String jarFile)
    {
        if (jarFile == null || !((new File(jarFile)).exists()))
            return loadImage(imageFile);

        Image newImage = null;

        try
        {
            // To retrieve images from game's JAR file:
            // jar:file:<location of JAR file>!imageFile
            final URL url = new URL("jar:file:" + jarFile + "!" + imageFile);

            newImage = ImageIO.read(url);
        }
        catch (final Exception e)
        {
            newImage = loadImage(imageFile);
        }

        MHMediaTracker.getInstance().addImage(newImage, MHMediaTracker.getInstance().getImageCount()+1);

        return newImage;
    }
}
