package mhframework.media;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.Random;

/********************************************************************
 * 
 * @author Michael Henson
 *
 */
public class MHStarField
{
    private static final Color TRANSPARENT_BACKGROUND = new Color(0, 0, 0, 0);

    private final int width;
    private final int height;
    private final Star stars[];
    private static Color[] colors = {Color.WHITE, Color.LIGHT_GRAY, Color.GRAY, Color.WHITE, new Color (255, 255, 161), Color.LIGHT_GRAY, Color.WHITE, Color.WHITE, Color.CYAN};
    private final Image image;
    private boolean drawBackground = true;

    public MHStarField(final int width, final int height, final int numStars)
    {
        this.width = width;
        this.height = height;

        final Random r = new Random();

        stars = new Star[numStars];
        for (int i = 0; i < stars.length; i++)
            stars[i] = new Star(r.nextInt(width), r.nextInt(height));

        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB_PRE);
    }


    public MHStarField(final int width, final int height, final int numStars, final boolean showBackground)
    {
        this(width, height, numStars);
        this.drawBackground = showBackground;
    }



    public Image getImage()
    {
        final Graphics g = image.getGraphics();
        if (drawBackground)
            g.setColor(Color.BLACK);
        else
            g.setColor(TRANSPARENT_BACKGROUND);

        g.fillRect(0, 0, width, height);

        for (final Star s: stars)
            s.render(g);

        return image;
    }

    private class Star
    {
        private int size = 2;
        private final int x, y;
        private Color color = Color.WHITE;

        public Star(final int px, final int py)
        {
            x = px;
            y = py;

            final Random r = new Random();

            size = 2 + r.nextInt(2);

            color = colors[r.nextInt(colors.length)];
        }


        public void render(final Graphics g)
        {
            g.setColor(color);
            int currentSize = size;
            if (Math.random() < 0.0025)
            {
                final double r = Math.random();
                if (r < 0.5)
                    currentSize--;
                else
                    currentSize++;
            }

            final int x0 = x - currentSize/2;
            final int y0 = y - currentSize/2;

            g.fillOval(x0, y0, currentSize, currentSize);
        }
    }
}
