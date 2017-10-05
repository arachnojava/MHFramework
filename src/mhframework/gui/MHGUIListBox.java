package mhframework.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;


/********************************************************************
 * Class for making a list box for use within an MHFramework
 * application.
 */
public class MHGUIListBox extends MHGUIComponent
{
	static final Color DEFAULT_BACKGROUND  = Color.WHITE;
	static final Color DEFAULT_FOREGROUND  = Color.BLACK;
	static final Color HIGHLIGHT_COLOR     = Color.BLUE;
	static final Color SELECTED_TEXT_COLOR = Color.WHITE;

	private static final int DEFAULT_FONT_SIZE = 10;
	//private static final Font DEFAULT_FONT
	//              = new Font("Arial", Font.PLAIN, DEFAULT_FONT_SIZE);


    // private MHGUILabel caption;
    private final MHGUIComponentList items;
    private Color background;
    private final int verticalSpacing = 2;


	/****************************************************************
	 * Constructor.
	 */
    public MHGUIListBox()
    {
        items = new MHGUIComponentList();

        setBackgroundColor(DEFAULT_BACKGROUND);

    }


	/****************************************************************
	 * Sets the color for the list box's background.
	 *
	 * @param bgColor  The color to use as the menu's background
	 *                  color.
	 */
    public void setBackgroundColor(final Color bgColor)
    {
        background = bgColor;
    }


	/****************************************************************
	 * Adds a button or other component to the menu.
	 *
	 * @param component  The button or component being added to the
	 *                    menu.
	 */
    public void add (final MHGUIComponent component)
    {
        items.add(component);

		if (component.width > this.width + 4)
		{
			this.width = component.width + 4;

			for (int i = 0; i < items.getSize(); i++)
			    items.get(i).width = component.width;
		}

		this.height = (DEFAULT_FONT_SIZE + verticalSpacing) * items.getSize();

    }


	public void add (final String caption, final Object object)
	{
		final MHGUIListBoxItem newItem = new MHGUIListBoxItem();

		newItem.caption.setText(caption);
		newItem.item = object;

		this.add(newItem);
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


		final int x = getX() + 2;
		int y = getY() + 2;

        for (int i = 0; i < items.getSize(); i++)
        {
			items.get(i).setPosition(x, y);

            items.get(i).render(g);

            y += DEFAULT_FONT_SIZE + verticalSpacing;
        }
    }



    @Override
    public void keyTyped(final KeyEvent e)
    {
        items.keyTyped(e);
    }

    @Override
    public void keyReleased(final KeyEvent e)
    {
        items.keyReleased(e);
    }

    @Override
    public void keyPressed(final KeyEvent e)
    {
        items.keyPressed(e);
    }

    @Override
    public void mouseMoved(final MouseEvent e)
    {
        items.mouseMoved(e);
    }

    @Override
    public void mousePressed(final MouseEvent e)
    {
        items.mousePressed(e);
    }

    @Override
    public void mouseReleased(final MouseEvent e)
    {
        items.mouseReleased(e);
    }

    @Override
    public void mouseClicked(final MouseEvent e)
    {
        items.mouseClicked(e);
    }



}

/********************************************************************
 */
class MHGUIListBoxItem extends MHGUIComponent
{
	    /** The text caption for the item. */
    	protected MHGUILabel caption;

		/** The object represented by this list item. */
		protected Object item;

	    /** ActionEvent listener */
	    protected ActionListener actionListener;

	    protected KeyListener keyListener;
	    protected MouseListener mouseListener;
	    protected MouseMotionListener mouseMotionListener;


    public void render(final Graphics2D g)
    {
		if (this.hasFocus())
		{
			g.setColor(MHGUIListBox.HIGHLIGHT_COLOR);
	        g.fillRect(getX(), getY(), this.width, this.height);
	        g.setColor(MHGUIListBox.SELECTED_TEXT_COLOR);
		}
		else
		    g.setColor(MHGUIListBox.DEFAULT_FOREGROUND);

        // Draw text if there is any
        if (caption != null)
        {
            caption.centerOn(getBounds(), g);
            caption.render(g);
        }


    }


    public void setFont(final Font f)
    {
        caption.setFont(f.getName(), f.getStyle(), f.getSize());
    }


    public void setText(final String text)
    {
        if (caption == null)
            caption = new MHGUILabel(text);
        else
            caption.setText(text);
    }


    public void setForeColor(final Color c)
    {
        caption.setPaint(c);
    }


    /****************************************************************
     */
    @Override
    public void mousePressed(final MouseEvent e)
    {
    	mouseClicked(e);
    }


    /****************************************************************
     */
    @Override
    public void mouseReleased(final MouseEvent e)
    {
    }


    public void addActionListener(final java.awt.event.ActionListener listener)
    {
        this.actionListener = listener;
    }


    public void addKeyListener(final java.awt.event.KeyListener listener)
    {
        this.keyListener = listener;
    }


    public void addMouseListener(final java.awt.event.MouseListener listener)
    {
        this.mouseListener = listener;
    }


    public void addMouseMotionListener(final java.awt.event.MouseMotionListener listener)
    {
        this.mouseMotionListener = listener;
    }


    public void advance()
    {

    }


    @Override
    public void actionPerformed(final ActionEvent e)
    {

    }


    @Override
    public void mouseClicked(final MouseEvent e)
    {
         if(isEnabled())
         {
              if(getBounds().contains(e.getPoint()))
              {
                  setFocus(true);
              }
              else
              {
                  setFocus(false);
              }
         }
    }


    @Override
    public void mouseMoved(final MouseEvent e)
    {
    }


    @Override
    public void keyPressed(final KeyEvent e)
    {
        keyListener.keyPressed(e);
    }


    @Override
    public void keyReleased(final KeyEvent e)
    {

    }


    @Override
    public void keyTyped(final KeyEvent e)
    {

    }
    }
