package mhframework.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.StringTokenizer;


/********************************************************************
 * Creates an area on the screen for displaying text.
 * 
 * 
 * @author Michael Henson
 *
 */
public class MHGUITextDisplayArea extends MHGUIComponent
{
    //MHPoint location;
    String text;
    private final int margin = 5;
    int borderWidth = 3;

    Font font = new Font("SansSerif", Font.BOLD, 12);
    Color borderColor = Color.BLACK;
    Color textColor = Color.BLACK;
    Color backgroundColor = new Color(Color.LIGHT_GRAY.getRed(), Color.LIGHT_GRAY.getGreen(), Color.LIGHT_GRAY.getBlue(), 180);
    Image backgroundImage;


    public MHGUITextDisplayArea()
    {
        super.setPosition(20, 20);
        text = "";
        width = 320;
        height = 240;
    }

    public int getBorderWidth()
    {
        return borderWidth;
    }

    public void setBorderWidth(final int borderWidth)
    {
        this.borderWidth = borderWidth;
    }

    public String getText()
    {
        return text;
    }

    public void setText(final String text)
    {
        this.text = text;
    }

    public void setFont(final Font font)
    {
        this.font = font;
    }

    public void setBorderColor(final Color borderColor)
    {
        this.borderColor = borderColor;
    }

    public void setTextColor(final Color textColor)
    {
        this.textColor = textColor;
    }

    public void setBackgroundColor(final Color backgroundColor)
    {
        this.backgroundColor = backgroundColor;
    }

    public void setBackgroundImage(final Image backgroundImage)
    {
        this.backgroundImage = backgroundImage;
    }

    @Override
    public void keyPressed(final KeyEvent e)
    {

    }


    @Override
    public void keyReleased(final KeyEvent e)
    {
        // TODO Auto-generated method stub

    }


    @Override
    public void keyTyped(final KeyEvent e)
    {
        // TODO Auto-generated method stub

    }


    @Override
    public void mouseClicked(final MouseEvent e)
    {
        // TODO Auto-generated method stub

    }


    @Override
    public void mouseMoved(final MouseEvent e)
    {
        // TODO Auto-generated method stub

    }


    @Override
    public void mousePressed(final MouseEvent e)
    {
        // TODO Auto-generated method stub

    }


    @Override
    public void mouseReleased(final MouseEvent e)
    {
        // TODO Auto-generated method stub

    }


    @Override
    public void advance()
    {
        // TODO Auto-generated method stub

    }


    public void resize(final Graphics2D g)
    {
        final ArrayList<String> lines = parseText(g);

        this.height = (font.getSize()+2) * (lines.size()+1);
    }

    @Override
    public void render(final Graphics2D g)
    {
        final int x0 = 0;
        final int y0 = 0;
        int y = getY() + margin + font.getSize();

        g.setFont(font);

        // Draw background
        g.setColor(backgroundColor);
        g.fillRect(getX()+x0, getY()+y0, width, height);

        // Draw border
        if (borderWidth > 0)
        {
            g.setColor(borderColor);
            g.setStroke(new BasicStroke(borderWidth));
            g.drawRect(getX()+x0, getY()+y0, width, height);
        }

        // Parse text
        final ArrayList<String> lines = parseText(g);

        // Draw text
        g.setColor(textColor);
        final int lineSpacing = 5;

        for (final String line: lines)
        {
            g.drawString(line, getX()+margin+x0, y+y0);
            y += font.getSize() + lineSpacing;
        }
    }

    private ArrayList<String> parseText(final Graphics2D g)
    {
        final ArrayList<String> output = new ArrayList<String>();

        final FontMetrics fm = g.getFontMetrics(font);
        final StringTokenizer tokens = new StringTokenizer(text);
        final int maxLineWidth = width - (margin*2);
        int currentLine = 0;

        final StringBuffer line = new StringBuffer();
        while (tokens.hasMoreTokens())
        {
            final String token = tokens.nextToken();
            if (fm.stringWidth(line.toString()+" "+token) < maxLineWidth)
                line.append(token+" ");
            else
            {
                output.add(currentLine, line.toString());
                currentLine++;
                line.setLength(0);
                line.append(token+" ");
            }
        }

        output.add(line.toString());
        return output;
    }

}
