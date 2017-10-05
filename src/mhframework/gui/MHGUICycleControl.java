package mhframework.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import mhframework.MHDisplayModeChooser;
import mhframework.MHGame;
import mhframework.MHGameApplication;
import mhframework.MHScreen;
import mhframework.MHVideoSettings;

/**
 * 
 * 
 * @author Michael Henson
 */
public class MHGUICycleControl extends MHGUIComponent implements ActionListener
{
    private MHGUIButton btnPrevious;
    private MHGUIButton btnNext;
    private MHGUILabel lblValue;
    private Object[] values;
    private int index = 0;

    public MHGUICycleControl()
    {
        setSize(100, 25);

        btnPrevious = getPreviousButton();
        btnNext = getNextButton();
    }


    public void setValues(final Object[] values)
    {
        this.values = values;
        if (index >= values.length)
            index = values.length-1;
        getLabel().setText(values[index].toString());
    }


    public void setLabelColor(final Color color)
    {
        getLabel().setPaint(color);
    }


    protected MHGUIButton getPreviousButton()
    {
        if (btnPrevious == null)
        {
            btnPrevious = new MHGUIButton();
            btnPrevious.setText(" < ");
            btnPrevious.addActionListener(this);
        }

        return btnPrevious;
    }


    protected MHGUIButton getNextButton()
    {
        if (btnNext == null)
        {
            btnNext = new MHGUIButton();
            btnNext.setText(" > ");
            btnNext.addActionListener(this);
        }

        return btnNext;
    }


    protected MHGUILabel getLabel()
    {
        if (lblValue == null)
            lblValue = new MHGUILabel();

        return lblValue;
    }


    @Override
    public void keyPressed(final KeyEvent e){}


    @Override
    public void keyReleased(final KeyEvent e){}


    @Override
    public void keyTyped(final KeyEvent e){}


    @Override
    public void mouseClicked(final MouseEvent e)
    {
    }


    @Override
    public void mouseMoved(final MouseEvent e)
    {
        btnNext.mouseMoved(e);
        btnPrevious.mouseMoved(e);
    }


    @Override
    public void mousePressed(final MouseEvent e)
    {
        btnNext.mousePressed(e);
        btnPrevious.mousePressed(e);
    }


    @Override
    public void mouseReleased(final MouseEvent e)
    {
        btnNext.mouseReleased(e);
        btnPrevious.mouseReleased(e);
    }


    @Override
    public void advance()
    {
        getLabel().setText(values[index].toString());
    }


    @Override
    public void setSize(final int w, final int h)
    {
        super.setSize(w, h);

        getPreviousButton().setHeight(h);
        getNextButton().setHeight(h);
        getLabel().setHeight(h);
    }


    @Override
    public void setHeight(final int h)
    {
        setSize(getWidth(), h);
    }


    @Override
    public void render(final Graphics2D g)
    {
        validateWidth(g);

        getLabel().centerOn(getBounds(), g);
        getLabel().render(g);
        btnPrevious.render(g);
        btnNext.render(g);
    }


    private void validateWidth(final Graphics2D g)
    {
        // Make the buttons the same size, then give the label
        // whatever room is left.

            final int prevWidth = (int)(btnPrevious.getFont().stringWidth(btnPrevious.getCaptionText()) * 1.1);
            final int nextWidth = (int)(btnNext.getFont().stringWidth(btnNext.getCaptionText()) * 1.1);
            int buttonWidth;
            if (prevWidth > nextWidth)
                buttonWidth = prevWidth;
            else
                buttonWidth = nextWidth;

            btnPrevious.setWidth(buttonWidth);
            btnNext.setWidth(buttonWidth);

            lblValue.setWidth(getWidth() - buttonWidth*2);

            btnPrevious.setPosition(getX(), getY());
            lblValue.setPosition(btnPrevious.getX()+btnPrevious.getWidth(), getY());
            btnNext.setPosition(getX()+getWidth()-buttonWidth, getY());
    }


    public int getSelectedIndex()
    {
        return index;
    }


    public void setSelectedIndex(final int indexNumber)
    {
        if (index < 0)
            index = 0;
        else if (index >= values.length)
            index = values.length - 1;
        else
            index = indexNumber;
    }


    public Object getSelectedValue()
    {
        if (index < 0)
            index = values.length - 1;
        else if (index >= values.length)
            index = 0;
        
        return values[index];
    }


    @Override
    public void actionPerformed(final ActionEvent e)
    {
        if (e.getSource() == btnPrevious)
            decrement();
        else if (e.getSource() == btnNext)
            increment();
    }


    protected void decrement()
    {
        index--;
        if (index < 0)
            index = values.length - 1;
    }


    protected void increment()
    {
        index++;
        if (index >= values.length)
            index = 0;
    }


    public static void main(final String[] args)
    {
        final MHVideoSettings settings = new MHVideoSettings();

        settings.displayWidth = 800;
        settings.displayHeight = 600;

        settings.bitDepth = 32;
        settings.fullScreen = true;
        settings.windowCaption = "Test";

        new MHGameApplication(new TestScreen(), settings);

        System.exit(0);
    }


    public void setLabelVisible(final boolean show)
    {
        getLabel().setVisible(show);
    }
}

class TestScreen extends MHScreen
{
    MHGUICycleControl cycler;
    MHGUIButton btn;

    public TestScreen()
    {
        cycler = new MHGUICycleControl();
        cycler.setPosition(50, 100);
        cycler.setSize(100, 25);
        cycler.setLabelColor(Color.LIGHT_GRAY);
        cycler.setValues(new String[] {"Hiko", "Tabitha", "Star", "Phantom", "April", "Skittle", "Michael", "Michelle"});
        add(cycler);

        btn = new MHGUIButton();
        btn.setText("Exit");
        btn.setSize(100, 25);
        btn.setPosition(50, 400);
        btn.addActionListener(this);
        add(btn);
    }


    @Override
    public void render(final Graphics2D g)
    {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, MHDisplayModeChooser.getScreenSize().width, MHDisplayModeChooser.getScreenSize().height);

        g.setColor(Color.YELLOW);
        g.drawString("Selected value:  " + cycler.getSelectedValue().toString(), 50, 200);

        super.render(g);
    }


    @Override
    public void load()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void unload()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void actionPerformed(final ActionEvent e)
    {
        MHGame.setProgramOver(true);
    }

}
