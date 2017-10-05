package mhframework.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import mhframework.MHDisplayModeChooser;
import mhframework.MHPoint;
import mhframework.MHScreen;
import mhframework.media.MHFont;

public class MHGUIDialogBox extends MHScreen
{
    private static final Color BACKGROUND_COLOR = new Color(0, 0, 0, 180);
    private int boxWidth, boxHeight;
    private int preferredWidth, lineSpacing; 
    private Image backgroundImage;
    private boolean tileBackground = false;
    private String text, title;
    private MHFont textFont, titleFont;
    private Rectangle2D titleBounds;

    /*
     * Border images are assumed to be in this order:
     * 0 1 2
     * 3 4 5
     * 6 7 8
     */
    private Image[] border = null;
    
    
    public MHGUIDialogBox(MHScreen parentScreen, String text, MHFont font)
    {
        setPreviousScreen(parentScreen);
        this.text = text;
        textFont = font;
        load();
        createBackgroundImage();
        calculateBoxDimensions();
    }
    
    
    public MHGUIDialogBox(MHScreen parentScreen, String text, String title, MHFont font, MHFont titleFont)
    {
        setPreviousScreen(parentScreen);
        this.text = text;
        this.title = title;
        textFont = font;
        this.titleFont = titleFont;
        load();
        createBackgroundImage();
        calculateBoxDimensions();
    }
    
    
    public void advance()
    {
        getPreviousScreen().advance();
    }
    
    public void setText(String text)
    {
        this.text = text;
        calculateBoxDimensions();
    }

    
    public void setTitle(String title)
    {
        this.title = title;
        calculateBoxDimensions();
    }

    
    public void setFont(MHFont font)
    {
        textFont = font;
        calculateBoxDimensions();
    }
    
    
    public void setTitleFont(MHFont font)
    {
        titleFont = font;
        calculateBoxDimensions();
    }
    
    
    public void render(Graphics2D g)
    {
        calculateBoxDimensions();
        
        if (tileBackground)
            tileImage(g, backgroundImage, 0, 0);
        else
            getPreviousScreen().render(g);
        
        drawBorder(g);
        
        drawTitle(g);
        
        drawText(g);
    }

    
    
    private void drawTitle(Graphics2D g)
    {
//        int x = (int)getTitleBounds().getX();
//        int y = (int)getTitleBounds().getY();
//        int w = (int)getTitleBounds().getWidth();
//        int h = (int)getTitleBounds().getHeight();
//        g.setColor(Color.WHITE);
//        g.drawRect(x, y, w, h);

        if (title != null && title.trim().length() > 0)
        {
            MHPoint p;
            if (titleFont != null)
            {
                p = titleFont.centerOn(getTitleBounds(), g, title);  //(g, title, x0, y0);
                titleFont.drawString(g, title, (int)p.getX(), (int)p.getY());
            }
            else
            {
                p = textFont.centerOn(getTitleBounds(), g, title);
                textFont.drawString(g, title, (int)p.getX(), (int)p.getY());
            }
            
            
        }
    }


    private void calculateBoxDimensions()
    {
        lineSpacing = (int)(textFont.getHeight() * 1.1);

        while (calculateBoxHeight() > MHDisplayModeChooser.getHeight() & calculateBoxWidth() < MHDisplayModeChooser.getWidth())
            preferredWidth = (int)(preferredWidth * 1.1);
    }
    
    
    
    private int calculateBoxHeight()
    {
        boxHeight = 5;
        if (border != null)
            boxHeight += border[1].getHeight(null) + border[7].getHeight(null);
        
        if (title != null && title.trim().length() > 0)
        {
            if (titleFont != null)
                boxHeight += titleFont.getHeight() + 5;
            else
                boxHeight += textFont.getHeight() + 5;
            
            double x = getTitleBounds().getX();
            double y = getTitleBounds().getY();
            double w = getTitleBounds().getWidth();
            double h = boxHeight;
            getTitleBounds().setRect(x, y, w, h);
        }
            
        
        int numLines = textFont.splitLines(text, preferredWidth).length;
        boxHeight += numLines * lineSpacing + 5;

        return boxHeight;
    }

    
    private Rectangle2D getTitleBounds()
    {
        if (titleBounds == null)
        {
            titleBounds = new Rectangle2D.Double();
        }
        
        double x = MHDisplayModeChooser.getCenterX() - boxWidth/2;
        double y = MHDisplayModeChooser.getCenterY() - boxHeight/2;
        double w = titleBounds.getWidth();
        double h = titleBounds.getHeight();
        titleBounds.setRect(x, y, w, h);
        
        return titleBounds;
    }
    
    private int calculateBoxWidth()
    {
        boxWidth = 5;
        if (border != null)
            boxWidth += border[3].getWidth(null) + border[5].getWidth(null);
        
        String[] lines = textFont.splitLines(text, preferredWidth);
        int longest = preferredWidth;
        for (int i = 0; i < lines.length; i++)
            longest = Math.max(longest, textFont.stringWidth(lines[i]));
        
        boxWidth += longest;
        
        double x = getTitleBounds().getX();
        double y = getTitleBounds().getY();
        double w = boxWidth;
        double h = getTitleBounds().getHeight();
        getTitleBounds().setRect(x, y, w, h);

        
        return boxWidth;
    }
    
    
    public void setBackgroundImage(Image img, boolean tiled)
    {
        backgroundImage = img;
        tileBackground = tiled;
    }

    
    public void setBorderImages(Image[] images)
    {
        border = images;
        calculateBoxDimensions();
    }
    

    @Override
    public void load()
    {
        setFinished(false);
        setDisposable(true);
        preferredWidth = MHDisplayModeChooser.getWidth() / 2;
    }
    

    @Override
    public void unload()
    {
    }
    
    
    private void drawText(Graphics2D g)
    {
        int x0 = (int)getTitleBounds().getX();
        int y0 = (int)getTitleBounds().getY();
        
        if (titleBounds != null)
            y0 += titleBounds.getHeight();
        
        if (border == null)
        {
            x0 += 5;
            y0 += textFont.getHeight();
        }
        else
        {
            x0 += border[0].getWidth(null) + 5;
            y0 += border[0].getHeight(null) + textFont.getHeight();
        }
        
        String[] lines = textFont.splitLines(text, preferredWidth);
        for (int line = 0; line < lines.length; line++)
        {
            int y = y0 + (line*lineSpacing);
            textFont.drawString(g, lines[line], x0, y);
        }
        
    }
    
    
    private void drawBorder(Graphics2D g)
    {
        int x0 = (int)getTitleBounds().getX();
        int y0 = (int)getTitleBounds().getY();
        
        if (border == null)
        {
            g.setColor(Color.BLACK);
            g.fillRect(x0, y0, boxWidth, boxHeight);
            g.setColor(Color.LIGHT_GRAY);
            g.draw3DRect(x0, y0, boxWidth, boxHeight, true);
            return;
        }

        // draw center fill
        for (int x = x0; x < boxWidth-border[4].getWidth(null); x+=border[4].getWidth(null))
            for (int y = y0; y < boxHeight-border[4].getHeight(null); y+=border[4].getHeight(null))
                g.drawImage(border[4], x, y, null);
        
        // draw top and bottom edges
        for (int x = x0 + border[0].getWidth(null); x < boxWidth - border[2].getWidth(null); x+=border[1].getWidth(null))
        {
            g.drawImage(border[1], x, y0, null);
            g.drawImage(border[7], x, y0 + boxHeight-border[7].getHeight(null), null);
        }

        // draw left and right edges
        for (int y = y0 + border[0].getHeight(null); y < boxHeight - border[6].getHeight(null); y+=border[3].getHeight(null))
        {
            g.drawImage(border[3], x0, y, null);
            g.drawImage(border[5], x0 + boxWidth-border[5].getWidth(null),  y, null);
        }

        // draw corners
        g.drawImage(border[0], x0, y0, null);
        g.drawImage(border[2], x0 + boxWidth-border[2].getWidth(null), y0, null);
        g.drawImage(border[6], x0, y0 + boxHeight-border[6].getHeight(null), null);
        g.drawImage(border[8], x0 + boxWidth-border[8].getWidth(null), y0 + boxHeight-border[8].getHeight(null), null);
    }
    
    
    private void createBackgroundImage()
    {
        backgroundImage = new BufferedImage(MHDisplayModeChooser.getWidth(), MHDisplayModeChooser.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D bg = (Graphics2D) backgroundImage.getGraphics();
        getPreviousScreen().render(bg);
        bg.setColor(BACKGROUND_COLOR);
        bg.fillRect(0, 0, MHDisplayModeChooser.getWidth()*2, MHDisplayModeChooser.getHeight()*2);
    }


    @Override
    public void keyReleased(KeyEvent e)
    {
        setFinished(true);
    }


    @Override
    public void mouseReleased(MouseEvent e)
    {
        setFinished(true);
    }


    @Override
    public void actionPerformed(ActionEvent e)
    {
        // TODO Auto-generated method stub
        
    }
    
}
