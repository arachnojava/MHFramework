package mhframework;

import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

/*********************************************************************
 * Creates an image filter for blurring an image.
 * 
 * @author Michael Henson
 */
public class MHBlurFilter implements MHImageFilter
{
    private static final float[] DEFAULT_MATRIX =
    {
         1.0f / 9.0f, 1.0f / 9.0f, 1.0f / 9.0f,
         1.0f / 9.0f, 1.0f / 9.0f, 1.0f / 9.0f,
         1.0f / 9.0f, 1.0f / 9.0f, 1.0f / 9.0f
    };

    private static final int MATRIX_SIZE = 9;

    private float[] blurMatrix;


   /**
    * Applies a blurring filter to an image.
    */
   public BufferedImage processImage(final BufferedImage image)
   {
        blurMatrix = getBlurValues();

        final int kernelSize = (int) Math.sqrt(blurMatrix.length);

        final Kernel blurKernel = new Kernel(kernelSize, kernelSize, blurMatrix);

        // create ConvolveOp for blurring BufferedImage
        final BufferedImageOp blurFilter = new ConvolveOp(
            blurKernel, ConvolveOp.EDGE_NO_OP, null );

        // apply blurFilter to BufferedImage
        return blurFilter.filter( image, null );

   }


    /**
     * Sets the array of values to determine the amount of blur.
     */
    public void setBlurValues(final float[] values)
    {
        blurMatrix = values;
    }


    /**
     * Sets the value to determine the amount of blur.
     */
    public void setBlurValue(final float value)
    {
        for (int i = 0; i < MATRIX_SIZE; i++)
            blurMatrix[i] = value;
    }


    /**
     * Sets the value to determine the amount of blur.
     */
    public void setBlurValue(final float numerator, final float denominator)
    {
        setBlurValue(numerator/denominator);
    }

    /**
     * Returns the array of values currently being used to
     * blur the image.
     */
    public float[] getBlurValues()
    {
        if (blurMatrix == null)
            blurMatrix = DEFAULT_MATRIX;

        return blurMatrix;
    }

}

