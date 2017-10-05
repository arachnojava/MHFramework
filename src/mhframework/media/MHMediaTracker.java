package mhframework.media;

import java.awt.Image;
import java.awt.MediaTracker;
import mhframework.MHDisplayModeChooser;

/********************************************************************
 * 
 * @author Michael Henson
 *
 */
public class MHMediaTracker extends MediaTracker
{
    private static final long serialVersionUID = 1L;
    private static MHMediaTracker INSTANCE;

    /** Number of images that have been requested to load. */
    private static int imageCount = 0;

    private MHMediaTracker()
    {
        super(MHDisplayModeChooser.getFrame());
    }

    /**
     * @return A handle to the singleton MHMediaTracker instance.
     */
    public static MHMediaTracker getInstance()
    {
        if (INSTANCE == null)
            INSTANCE = new MHMediaTracker();

        return INSTANCE;
    }


    public void reset()
    {
        INSTANCE = null;
        imageCount = 0;
    }

    /****************************************************************
     * This method counts the number of
     * images that have finished loading.  Useful for calculating
     * how much of the application has finished loading at the
     * point in time when the method is called.
     *
     * @return  The number of images that have finished loading.
     */
    public int countLoadedImages()
    {
        int count = 0;

        for (int i = 0; i < imageCount; i++)
        {
            if (getInstance().checkID(i))
                count++;
        }

        return count;
    }
    /****************************************************************
     * Returns true when all requested images have finished loading.
     *
     * @return  True if all images have finished loading, false
     *          otherwise.
     */
    public boolean doneLoading()
    {
        if (getInstance().checkAll(true))
            return true;

        return false;
    }
    /****************************************************************
     * Returns the number of images for which loading has been
     * requested.
     *
     * <p>Each time an image is added to the media tracker, a private
     * variable is incremented.  This method returns the current
     * value of that variable.
     *
     * @return  The number of images that have been added to the
     *          MHMediaTracker.
     */
    public int getImageCount()
    {
        return imageCount;
    }


    public float getPctLoaded()
    {
        final float total = getImageCount();
        final float loaded = countLoadedImages();

        return (loaded / total);
    }


    @Override
    public synchronized void addImage(final Image image, final int id, final int w,
                    final int h)
    {
        super.addImage(image, id, w, h);
        imageCount++;
    }

    @Override
    public synchronized void addImage(final Image image, final int id)
    {
        super.addImage(image, id);
        imageCount++;
    }

}
