package mhframework;

import java.awt.image.BufferedImage;

/********************************************************************
 * Interface for classes that must filter images with various
 * effects.
 * 
 * @author Michael Henson
 */
public interface MHImageFilter
{
    /****************************************************************
     * Apply a filter to a given image.
     *
     * @param image  The original image to be filtered.
     *
     * @return  The filtered image.
     */
    public abstract BufferedImage processImage(BufferedImage image);
}

