package mhframework.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;


/********************************************************************
 * Class for making a menu of buttons.
 *
 *
 *@author Michael Henson
 */
public class MHGUIButtonMenu extends MHGUIComponent
{

    private MHGUILabel caption;
    private final MHGUIComponentList buttons;
    private Color background;
    private int verticalSpacing = 5,
                borderSpacing = 5,
                buttonWidth = 100,
                buttonHeight = 25;


	/****************************************************************
	 * Constructor.
	 */
    public MHGUIButtonMenu()
    {
        buttons = new MHGUIComponentList();

        setButtonSize(100, 25);

    }


	/****************************************************************
	 * Sets the color for the menu's background.
	 *
	 * @param bgColor  The color to use as the menu's background
	 *                  color.
	 */
    public void setBackgroundColor(final Color bgColor)
    {
        background = bgColor;
    }


	/****************************************************************
	 * Sets the width and height of every button on the menu.
	 *
	 * @param width  The width of the buttons.
	 * @param height The height of the buttons.
	 */
    public void setButtonSize(final int width, final int height)
    {
        for (int i = 0; i < buttons.getSize(); i++)
        {
            buttons.get(i).setSize(width, height);
        }

        buttonWidth = width;
        buttonHeight = height;
    }


	/****************************************************************
	 * Sets the distance in pixels between each button on the menu.
	 *
	 * @param v  The vertical distance in pixels between the
	 *            buttons.
	 */
    public void setVerticalSpacing(final int v)
    {
        verticalSpacing = v;
    }


	/****************************************************************
	 * Sets the distance in pixels between the edge of the menu and
	 * the buttons on the menu.
	 *
	 * @param b  The distance in pixels between the menu's edge and
	 *            the buttons.
	 */
    public void setBorderSpacing(final int b)
    {
        borderSpacing = b;
    }


	/****************************************************************
	 * Sets the caption text for the menu.
	 *
	 * @param text  The string to use as the menu caption.
	 */
    public void setText(final String text)
    {
        if (caption == null)
        {
            caption = new MHGUILabel(text);
            add(caption);
        }
        else
            caption.setText(text);
    }


	/****************************************************************
	 * Sets the text color for the menu's caption.
	 *
	 * @param c  The color to use for the menu caption.
	 */
    public void setTextColor(final Color c)
    {
        caption.setPaint(c);
    }


	/****************************************************************
	 * Adds a button or other component to the menu.
	 *
	 * @param component  The button or component being added to the
	 *                    menu.
	 */
    public void add(final MHGUIComponent component)
    {
        buttons.add(component);

        setButtonSize(buttonWidth, buttonHeight);

        final int height = buttons.getSize() *
                     ( verticalSpacing + buttonHeight ) +
                     borderSpacing;
        final int width = borderSpacing * 2 + buttonWidth;

        setSize(width, height);

    }


	/****************************************************************
	 * Adds a button or other component to the menu.
	 *
	 * @param component  The button or component being added to the
	 *                    menu.
	 */
    public void add(final int index, final MHGUIComponent component)
    {
        buttons.add(index, component);

        setButtonSize(buttonWidth, buttonHeight);

        final int height = buttons.getSize() *
                     ( verticalSpacing + buttonHeight ) +
                     borderSpacing;
        final int width = borderSpacing * 2 + buttonWidth;

        setSize(width, height);

    }


    public void advance()
    {
    }


    public void render(final Graphics2D g)
    {
        if (background != null)
        {
            g.setColor(background);
            g.fill3DRect(getX(), getY(),
                         (int)(getBounds().getWidth()),
                         (int)(getBounds().getHeight()), false);
        }

        final int x = getX() + borderSpacing;
        int y = getY() + borderSpacing;

        if (caption != null)
        {
            final Rectangle2D b = new Rectangle2D.Double(x, y, buttonWidth, buttonHeight);
            caption.centerOn(b, g);
        }


        for (int i = 0; i < buttons.getSize(); i++)
        {
            if (buttons.get(i) instanceof MHGUIButton)
                buttons.get(i).setPosition(x, y);

            buttons.get(i).render(g);

            y += buttonHeight + verticalSpacing;
        }
    }



    @Override
    public void keyTyped(final KeyEvent e)
    {
        //buttons.keyTyped(e);
    }

    @Override
    public void keyReleased(final KeyEvent e)
    {
        //buttons.keyReleased(e);
    }

    @Override
    public void keyPressed(final KeyEvent e)
    {
        //buttons.keyPressed(e);
    }

    @Override
    public void mouseMoved(final MouseEvent e)
    {
        buttons.mouseMoved(e);
    }

    @Override
    public void mousePressed(final MouseEvent e)
    {
        buttons.mousePressed(e);
    }

    @Override
    public void mouseReleased(final MouseEvent e)
    {
        buttons.mouseReleased(e);
    }

    @Override
    public void mouseClicked(final MouseEvent e)
    {
        buttons.mouseClicked(e);
    }

	/**
	 * Returns the buttons.
	 * @return MHGUIComponentList
	 */
	public MHGUIComponentList getButtons()
	{
		return buttons;
	}

}