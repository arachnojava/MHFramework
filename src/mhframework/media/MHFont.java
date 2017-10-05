package mhframework.media;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import mhframework.MHPoint;

public class MHFont
{
    private static final long serialVersionUID = -6528144541118918429L;
    private MHFontInterface font;
    
    public MHFont(String name, int style, int size)
    {
        font = new NormalFont(name, style, size);
    }
    
    
    public MHFont(MHImageFont.EngineFont imageFont)
    {
        font = new ImageFont(imageFont);
    }
    
    
    public MHFont(MHImageFont imageFont)
    {
        font = new ImageFont(imageFont);
    }

    
    public MHFont(MHFontInterface f)
    {
        font = f;
    }
    
    
    public MHFont clone()
    {
        return new MHFont(font);
    }

    
    public void setAllCaps(boolean caps)
    {
        font.setAllCaps(caps);
    }
    
    public void drawString(Graphics2D g, String text, double x, double y)
    {
        font.drawString(g, text, x, y);
    }
    

    public int stringWidth(String text)
    {
        return font.stringWidth(text);
    }


    public int getHeight()
    {
        return font.getHeight();
    }
    
    
    public void setScale(double scale)
    {
        font.setScale(scale);
    }


    public MHPoint centerOn(Rectangle2D r, Graphics2D g, String text)
    {
        return font.centerOn(r, g, text);
    }

    
    public String[] splitLines(String text, int lineWidthPx)
    {
        ArrayList<String> lines = new ArrayList<String>();
        String[] words = text.split(" ");
        String line = "";
        
        for (int i = 0; i < words.length; i++)
        {
            line += words[i];
            if (stringWidth(line) >= lineWidthPx)
            {
                lines.add(line.trim());
                line = "";
            }
            else
                line += " ";
        }
        if (line.trim().length() > 0)
            lines.add(line.trim());
        
        String[] result = new String[lines.size()];
        for (int s = 0; s < result.length; s++)
            result[s] = lines.get(s);

        return result;
    }

    
    
    /****************************************************************
     */
    private class NormalFont implements MHFontInterface
    {
        private Font font;
        private FontMetrics fontMetrics;
        private boolean allCaps = false;
        
        public NormalFont(String name, int style, int size)
        {
            font = new Font(name, style, size);
        }

        @Override
        public void drawString(Graphics2D g, String text, double x, double y)
        {
            if (allCaps)
                text = text.toUpperCase();
            
            g.setFont(font);
            fontMetrics = g.getFontMetrics(font);
            g.drawString(text, (int)x, (int)y);
        }

        @Override
        public int stringWidth(String text)
        {
            if (fontMetrics != null)
                return fontMetrics.stringWidth(text);
            
            return 0;
        }

        
        @Override
        public MHPoint centerOn(Rectangle2D r, Graphics2D g, String text)
        {
            fontMetrics = g.getFontMetrics(font);
            
            // get the FontRenderContext for the Graphics2D context
            final FontRenderContext frc = g.getFontRenderContext();

            final TextLayout layout = new TextLayout(text, font, frc);

            // get the bounds of the layout
            final Rectangle2D textBounds = layout.getBounds();

            // set the new position
            MHPoint p = new MHPoint();
            p.setX(r.getX() + (r.getWidth()/2) - (textBounds.getWidth()  / 2));
            p.setY(r.getY() + ((r.getHeight()  + textBounds.getHeight()) / 2));

            return p;
        }

        
        @Override
        public int getHeight()
        {
            return font.getSize();
        }

        
        @Override
        public void setScale(double scale)
        {
            int size = (int)(font.getSize() * scale);
            font = new Font(font.getName(), font.getStyle(), size);
        }
        
        
        public MHFontInterface clone()
        {
            return (MHFontInterface) new MHFont(font.getName(), font.getStyle(), font.getSize());
        }

        @Override
        public void setAllCaps(boolean caps)
        {
            this.allCaps  = caps;
        }
    }
    
    
    /****************************************************************
     */
    private class ImageFont implements MHFontInterface
    {
        private MHImageFont font;
        private boolean allCaps = false;
        //private double targetHeight = 0.0;
        
        public ImageFont(MHImageFont.EngineFont imageFont)
        {
            this(new MHImageFont(imageFont));
        }

        public ImageFont(MHImageFont imageFont)
        {
            font = imageFont;
        }

        @Override
        public void drawString(Graphics2D g, String text, double x, double y)
        {
            if (allCaps) text = text.toUpperCase();
            
            font.drawString(g, text, x, y);
        }

        @Override
        public int stringWidth(String text)
        {
            return font.stringWidth(text);
        }

        @Override
        public MHPoint centerOn(Rectangle2D r, Graphics2D g, String text)
        {
            MHPoint p = new MHPoint();
            
            double x = r.getWidth()/2 - stringWidth(text)/2;
            double y = r.getHeight()/2 - getHeight()/2;
            
            p.setLocation(r.getX() + x, r.getY() + y + getHeight());
            
            return p;
        }

        @Override
        public int getHeight()
        {
            return font.charHeight(' ');
        }

        
        @Override
        public void setScale(double scale)
        {
            font.setScale(scale);
        }
        
        
        public MHFontInterface clone()
        {
            return (MHFontInterface) new MHFont(font);
        }

        @Override
        public void setAllCaps(boolean caps)
        {
            allCaps = caps;
        }
    }
    
    
    /****************************************************************
     */
    private interface MHFontInterface
    {
        public void setAllCaps(boolean caps);
        public void drawString(Graphics2D g, String text, double x, double y);
        public MHPoint centerOn(Rectangle2D r, Graphics2D g, String text);
        public int getHeight();
        public int stringWidth(String text);
        public void setScale(double scale);
        public MHFontInterface clone();
    }
}
