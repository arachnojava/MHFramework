package mhframework.gui;


import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

/********************************************************************
 * Provides a rectangular status indicator similar to a progress bar.
 *
 * @author Michael Henson
 */
public class MHGUIProgressBar extends MHGUIComponent
{
    ////////////////////////////////
    ////////  Data Members  ////////
    ////////////////////////////////

  /** Progress bar color (if not gradient). */
  private Color barColor;

  /** Progress bar color (if gradient painted). */
  private GradientPaint paint;

  /** Progress bar border color. */
  private Color borderColor;

  /** Optional caption for the bar. */
  private MHGUILabel label;

  /** Orientation -- true if horizontal, false if vertical. */
  private boolean horizontal;

  /** Current width or height (depending on orientation) of the
   * progress bar. */
  private double currSize;

  /** Maximum value. */
  private float maxValue;

  /** Current value */
  private double currValue;


    ///////////////////////////
    ////////  Methods  ////////
    ///////////////////////////

  /******************************************************************
   * Constructor.
   */
  public MHGUIProgressBar()
  {
    this.setPosition(0,0);
    this.setSize(100, 20);
    this.setBarColor(Color.BLUE);
    this.setBorderColor(Color.WHITE);
    this.setOrientation(true);
    this.setMaxValue(100);
    this.update(maxValue);
    this.paint = null;
  }


  /******************************************************************
   * Sets the color of the progress bar.  This color will be used
   * only if no gradient paint is defined.
   *
   * @param c  The color to use for the progress bar.
   */
  public void setBarColor(final Color c)
  {
    barColor = c;
  }


  /******************************************************************
   * Sets the color of the border around the component.
   *
   * @param c  The color to use for the border rectangle.
   */
  public void setBorderColor(final Color c)
  {
    borderColor = c;
  }


  /******************************************************************
   * Sets the color of the component's caption.
   *
   * @param c  The color to use for the caption.
   */
  public void setTextColor(final Color c)
  {
      if (label == null)
          label = new MHGUILabel();
      
      label.setPaint(c);
  }


    /******************************************************************
     * Sets the orientation of the component to horizontal or vertical.
     *
     * @param horiz  True if component is to be drawn horizontally,
     *               false if it is to be drawn vertically.
     */
    public void setOrientation(final boolean horiz)
    {
        horizontal = horiz;
    }


  /******************************************************************
   * Sets an optional gradient paint with which to color the progress
   * bar.  If a gradient paint is not specified, the color set by
   * <tt>setBarColor()</tt> will be used.
   *
   * @param grad  An existing GradientPaint object to be used for
   *               coloring the progress bar.
   */
  public void setPaint(final GradientPaint grad)
  {
      paint = grad;
  }


  /******************************************************************
   * Sets the maximum value that this status indicator can represent.
   *
   * @param max  The maximum value of this indicator.
   */
  public void setMaxValue(final int max)
  {
    maxValue = max;
  }


    /****************************************************************
     * Sets the text for the component's caption.
     *
     * @param text  The text to appear over the indicator.
     */
    public void setText(final String text)
    {
        if (label == null)
            label = new MHGUILabel(text);
        else
            label.setText(text);
    }


    /****************************************************************
     * Updates the value represented by the progress bar.
     *
     * @param currentValue  The new value represented by the bar.
     */
  public void update(final double currentValue)
  {

    currValue = currentValue;

    if (horizontal)
    {
        currSize = (width * (currValue / maxValue));
    }
    else
    {
        currSize = (height * (currValue / maxValue));
    }

  }


  /******************************************************************
   * Not currently used.  Can be overridden by subclasses who may
   * want to animate the bar or provide some other autonomous
   * functionality.
   */
  public void advance()
  {
  }


  /******************************************************************
   * Draws the status indicator onto the sent Graphics object.
   *
   * @param graphics  The Graphics object on which to draw the
   *                   indicator.
   */
  @Override
  public void render(final Graphics2D g)
  {
    if (paint == null)
        g.setColor(barColor);
    else
        g.setPaint(paint);

    if (horizontal)
    {
      g.fill(new Rectangle2D.Double(getX(), getY(), currSize, height));
    }
    else
    {
      g.fill(new Rectangle2D.Double(getX(), getY()+(height-currSize), width, currSize));
    }

    // draw border rectangle
    g.setColor(borderColor);
    g.draw3DRect(getX(), getY(), width, height, false);


        // Draw text if there is any
        if (label != null)
        {
            label.centerOn(getBounds(), g);
            label.setPosition(label.getX(), label.getY());
            label.render(g);
        }

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


    public void setFont(final Font font)
    {
        if (label == null)
            label = new MHGUILabel("");

        label.setFont(font.getName(), font.getStyle(), font.getSize());
    }


    /**
     * @return the maxValue
     */
    public float getMaxValue()
    {
        return maxValue;
    }

}
