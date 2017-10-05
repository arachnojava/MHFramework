package mhframework.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import mhframework.MHPoint;
import mhframework.media.MHFont;
import mhframework.media.MHImageFont;


/********************************************************************
 * Custom label component class.
 * 
 * @author Michael Henson
 */
public class MHGUILabel extends MHGUIComponent
{
    // Constants
    /** Default color if no other color is specified. */
    protected final Paint DEFAULT_PAINT = Color.BLACK;

    // Data Members
    /** The text displayed on the label. */
    protected String text;

    /** The paint used to color the label when it's enabled. */
    protected Paint paint;

    /** The paint used to color the label when it's disabled. */
    protected Paint disabledPaint;

    
    private MHFont font;

    public MHGUILabel()
    {
        this("");
    }


    public MHGUILabel(final String caption)
    {
        setText(caption);
        setFocusable(false);
        setPaint(DEFAULT_PAINT);
        setDisabledPaint(DEFAULT_PAINT);
        
        if (getFont() == null)
            setFont(new MHFont("Arial", Font.PLAIN, 12));
    }


    MHFont getFont()
    {
        return font;
    }


    public void setFont(MHFont f)
    {
        font = f;
        updateBounds();
    }


    public void setText(final String caption)
    {
        text = caption;
        updateBounds();
    }


    public void setPaint(final Paint p)
    {
        paint = p;
    }


    public void setDisabledPaint(final Paint p)
    {
        disabledPaint = p;
    }


    public void advance()
    {

    }


    public void render(final Graphics2D g)
    {
        if (!isVisible())
            return;

        if (isEnabled())
            g.setPaint(paint);
        else    
            g.setPaint(disabledPaint);

        font.drawString(g, text, getX(), getY());
    }


    public void centerOn(final Rectangle2D r, final Graphics2D g)
    {
         if (text.length() < 1)
             text = " ";

         MHPoint p = font.centerOn(r, g, text);
         
         setX((int) p.getX());
         setY((int) p.getY());
         
         updateBounds();
    }


    public Rectangle2D updateBounds()
    {
        final Rectangle2D bounds = getBounds();

        if (font == null || text == null) return bounds;
        
        if (bounds.getWidth() < font.stringWidth(text))
            bounds.setRect(getX(), getY(), font.stringWidth(text), bounds.getHeight());
        if (bounds.getHeight() < font.getHeight())
            bounds.setRect(getX(), getY(), bounds.getWidth(), font.getHeight());
        
        return bounds;
    }


    @Override
    public void actionPerformed(final ActionEvent e)
    {

    }


    @Override
    public void mouseClicked(final MouseEvent e)
    {

    }


    @Override
    public void mousePressed(final MouseEvent e)
    {

    }


    @Override
    public void mouseReleased(final MouseEvent e)
    {

    }


    @Override
    public void mouseMoved(final MouseEvent e)
    {

    }


    @Override
    public void keyPressed(final KeyEvent e)
    {

    }


    @Override
    public void keyReleased(final KeyEvent e)
    {

    }


    @Override
    public void keyTyped(final KeyEvent e)
    {

    }


    /**
     * @return the text
     */
    public String getText()
    {
        return text;
    }


    public void setFont(String name, int style, int size)
    {
        font = new MHFont(name, style, size);
    }


    public void setFont(MHImageFont f)
    {
        font = new MHFont(f);
    }
}

