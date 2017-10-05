package mhframework.media;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import mhframework.MHGameApplication;
import mhframework.MHScreen;
import mhframework.MHVideoSettings;

/********************************************************************
 * The MHImageFont class allows loading and drawing of text using
 * images for the characters.
 *
 * Reads all the png images in a directory in the form "charXX.png"
 * where XX is a decimal unicode value.
 *
 * Characters can have different widths and heights.
 * 
 * @author Michael Henson
 */
public class MHImageFont
{
    /** Enumeration of fonts built in to the MHFramework engine. */
    public enum EngineFont
    {
        NES1 ("NES1"),
        OCR_GREEN ("OCRGreen"),
        TAHOMA_BLUE ("TahomaBlue"),
        ANDROID_NATION ("AndroidNation");
        
        private String path;
        
        private EngineFont(String name)
        {
            path = IMAGE_FONT_DIRECTORY + name;
        }
        
        public String getFontPath()
        {
            return path;
        }
    }
    public static final String IMAGE_FONT_DIRECTORY = "/mhframework/images/fonts/";

    public static final int HCENTER = 1;
    public static final int VCENTER = 2;
    public static final int LEFT = 4;
    public static final int RIGHT = 8;
    public static final int TOP = 16;
    public static final int BOTTOM = 32;

    private char firstChar;
    private Image[] characters;
    private final Image invalidCharacter;
    private double scale = 1.0;


    /****************************************************************
     * Creates a new MHImageFont with no characters.
     */
    public MHImageFont()
    {
        this((String)null);
        firstChar = 0;
        characters = new Image[0];
    }
    
    
    public MHImageFont(MHImageFont.EngineFont font)
    {
        this(font.getFontPath());
    }


    /****************************************************************
     * Creates a new MHImageFont and loads character images from the
     * specified path.
     */
    public MHImageFont(final String path)
    {
        if (path != null)
        {
            load(path);
        }

        // make the character used for invalid characters
        invalidCharacter =
            new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        final Graphics g = invalidCharacter.getGraphics();
        g.setColor(Color.RED);
        g.fillRect(0,0,10,10);
        g.dispose();
    }

    
    /**
     *  A new algorithm for the load() method that should work even 
     *  when the fonts are bundled into a JAR.
     */
    public void load(String path)
    {
        final int MIN_CHAR = 32;
        final int MAX_CHAR = 126;
        
        firstChar = MIN_CHAR;
        characters = new Image[MAX_CHAR - MIN_CHAR + 1];
        for (int c = MIN_CHAR; c <= MAX_CHAR; c++)
        {
            characters[c-firstChar] = MHResourceManager.loadImage(path + "/char"+c+".png");
        }
    }

    /****************************************************************
     * Loads the image files for each character from the specified
     * path. For example, if "../fonts/large" is the path, this
     * method searches for all the images names "charXX.png" in that
     * path, where "XX" is a decimal unicode value. Not every
     * character image needs to exist; you can only do numbers or
     * uppercase letters, for example.
     */
    public void load(final String path, boolean useOldAlgorithm) throws NumberFormatException
    {
        if (!useOldAlgorithm) 
        {
            load(path);
            return;
        }
        
        // in this directory:
        // load every png file that starts with 'char'
        File dir = new File(path);
        File[] files = dir.listFiles();

        System.out.println("path = " + path);
        System.out.println("#files = " + files.length);
        System.out.println("Found " + files.length + " files in " + dir.getAbsolutePath());
        
        // find min and max chars
        char minChar = Character.MAX_VALUE;
        char maxChar = Character.MIN_VALUE;
        for (final File file : files)
        {
            final int unicodeValue = getUnicodeValue(file);
            if (unicodeValue != -1) {
                minChar = (char)Math.min(minChar, unicodeValue);
                maxChar = (char)Math.max(maxChar, unicodeValue);
            }
        }

        // load the images
        if (minChar < maxChar) {
            firstChar = minChar;
            characters = new Image[maxChar - minChar + 1];
            for (final File file : files)
            {
                final int unicodeValue = getUnicodeValue(file);
                if (unicodeValue != -1) 
                {
                    final int index = unicodeValue - firstChar;
                    System.out.println("Loading " + file.getAbsolutePath());  //"Loading " + path+"/"+file.getName());
                    characters[index] = MHResourceManager.loadImage(file.getAbsolutePath());
                        //new ImageIcon(file.getAbsolutePath()).getImage();
                }
            }
        }
    }


    private int getUnicodeValue(final File file)
        throws NumberFormatException
    {
        final String name = file.getName().toLowerCase();
        if (name.startsWith("char") && name.endsWith(".png")) {
            final String unicodeString =
                name.substring(4, name.length() - 4);
            return Integer.parseInt(unicodeString);
        }
        return -1;
    }


    /****************************************************************
     * Gets the image for a specific character. If no image for the
     * character exists, a special "invalid" character image is
     * returned.
     */
    public Image getImage(final char ch) 
    {
        final int index = ch - firstChar;
        if (index < 0 || index >= characters.length ||
            characters[index] == null)
        {
            // If letter is lower case, try using a capital version of it.
            if (ch >= 'a' && ch <= 'z')
                if (characters[index-32] != null)
                    return characters[index-32];
            
            return invalidCharacter;
        }
        return characters[index];
    }


    /****************************************************************
     * Gets the string width, in pixels, for the specified string.
     */
    public int stringWidth(final String s)
    {
        if (s == null)
            return 0;
        
        int width = 0;
        for (int i=0; i<s.length(); i++)
        {
            width += charWidth(s.charAt(i));
        }
        return width;
    }


    /****************************************************************
     * Gets the char width, in pixels, for the specified char.
     */
    public int charWidth(final char ch)
    {
        return scale(getImage(ch).getWidth(null));
    }


    /****************************************************************
     * Gets the char height, in pixels, for the specified char.
     */
    public int charHeight(final char ch)
    {
        return scale(getImage(ch).getHeight(null));
    }

    
    
    private int scale(int value)
    {
        return (int)(value * scale);
    }

    /****************************************************************
     * Draws the specified string at the (x, y) location.
     */
    public void drawString(final Graphics g, final String s, final double x, final double y) {
        drawString(g, s, x, y, LEFT | BOTTOM);
    }


    /****************************************************************
     * Draws the specified string at the (x, y) location.
     */
    public void drawString(final Graphics g, final String s, double x, final double y,
        int anchor)
    {
        if (s == null) return;
        
        if ((anchor & HCENTER) != 0) {
            x-=stringWidth(s) / 2;
        }
        else if ((anchor & RIGHT) != 0) {
            x-=stringWidth(s);
        }
        // clear horizontal flags for char drawing
        anchor &= ~HCENTER;
        anchor &= ~RIGHT;

        // draw the characters
        for (int i=0; i<s.length(); i++) {
            drawChar(g, s.charAt(i), x, y, anchor);
            x+=charWidth(s.charAt(i));
        }
    }

    
    /****************************************************************
     *  Creates an image containing the input string.
     *  
     * @param s
     * @return
     */
    public Image createImage(String s)
    {
        BufferedImage img = new BufferedImage(stringWidth(s), charHeight('A'), BufferedImage.TYPE_INT_ARGB_PRE);
        drawString(img.getGraphics(), s, 0, charHeight('A'));
        
        return img;
    }

    /****************************************************************
     * Draws the specified character at the (x, y) location.
     */
    public void drawChar(final Graphics g, final char ch, final int x, final int y) {
        drawChar(g, ch, x, y, LEFT | BOTTOM);
    }


    /****************************************************************
     * Draws the specified character at the (x, y) location.
     */
    public void drawChar(final Graphics g, final char ch, double x, double y,
        final int anchor)
    {
        int width = charWidth(ch);
        int height = charHeight(ch);
        
        if ((anchor & HCENTER) != 0) {
            x -= width / 2;
        }
        else if ((anchor & RIGHT) != 0) {
            x -= width;
        }

        if ((anchor & VCENTER) != 0) {
            y -= height / 2;
        }
        else if ((anchor & BOTTOM) != 0) {
            y -= height;
        }
        g.drawImage(getImage(ch), (int)x, (int)y, width, height, null);
    }
    
    
    
    // FOR TESTING ONLY:
    public static void main(String[] args)
    {
        new MHGameApplication(new MHImageFont.FontTestScreen(), new MHVideoSettings());
    }
    
    // FOR TESTING ONLY:
    private static class FontTestScreen extends MHScreen
    {
        private MHImageFont font;
        
        public FontTestScreen()
        {
        }

        
        public void render(Graphics2D g)
        {
            g.setColor(Color.BLACK);
            g.fillRect(-10, -10, 810, 610);
            font.drawString(g, "HELLO, WORLD!", 20, 80);
            font.drawString(g, "CHECK IT OUT! MHFRAMEWORK CAN NOW USE", 20, 140);
            font.drawString(g, "THE NES SYSTEM FONT! LOOK FAMILIAR?", 20, 170);
            font.drawString(g, "AH, SWEET NOSTALGIA...", 20, 230);
        }
        
        
        @Override
        public void actionPerformed(ActionEvent arg0)
        {
        }

        @Override
        public void load()
        {
            if (font == null)
                font = new MHImageFont("/mhframework/images/fonts/NES1");
        }

        @Override
        public void unload()
        {
        }
        
    }

    public void setScale(double scale)
    {
        this.scale = scale;
    }


    public double getScale()
    {
        return scale;
    }
}