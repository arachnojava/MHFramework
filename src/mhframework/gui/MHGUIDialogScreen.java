package mhframework.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.StringTokenizer;
import javax.swing.JOptionPane;
import mhframework.MHDisplayModeChooser;
import mhframework.MHScreen;


/********************************************************************
 * 
 * 
 * 
 * @author Michael Henson
 *
 */
public class MHGUIDialogScreen extends MHScreen
{
    private static final int NO_OPTION_CHOSEN = -999;
    public  static final int OK_OPTION        = JOptionPane.OK_OPTION;
    public  static final int CANCEL_OPTION    = JOptionPane.CANCEL_OPTION;

    private int returnCode = NO_OPTION_CHOSEN;

    private String _strTitle;
    private String _strMessage;

    protected MHGUIButton _btnOK, _btnCancel;

    private Image _backgroundImage;
    private Color titleColor = Color.LIGHT_GRAY, textColor = Color.WHITE, bgColor = Color.BLACK;

    protected Font _titleFont, _messageFont;

    private final int spacing = 20;  // button spacing
    private final int margin = 40;

    public MHGUIDialogScreen()
    {

        _btnCancel = new MHGUIButton();
        _btnCancel.setText("Cancel");
        _btnCancel.setSize(100, 25);
        _btnCancel.addActionListener(this);

        _btnOK = new MHGUIButton();
        _btnOK.setText("OK");
        _btnOK.setSize(_btnCancel.width, _btnCancel.height);
        _btnOK.addActionListener(this);

        add(_btnOK);
        add(_btnCancel);

        _titleFont = new Font("Arial Black", Font.BOLD, 48);
        _messageFont = new Font("Tahoma", Font.PLAIN, 24);

        setTitle("MHGUIDialogScreen");
        setMessage("");

    }


    @Override
    public void actionPerformed(final ActionEvent e)
    {
        if (e.getSource() == _btnOK)
            returnCode = OK_OPTION;
        else if (e.getSource() == _btnCancel)
            returnCode = CANCEL_OPTION;
    }



    @Override
    public void load()
    {
        _btnCancel.setPosition(MHDisplayModeChooser.getScreenSize().width - _btnCancel.width - spacing,
                        MHDisplayModeChooser.getScreenSize().height - _btnCancel.height - spacing);

        _btnOK.setPosition(_btnCancel.getX() - _btnOK.width - spacing, _btnCancel.getY());

        this.setFinished(false);
        this.setDisposable(true);

    }


    @Override
    public void unload()
    {
        // TODO Auto-generated method stub

    }


    @Override
    public void advance()
    {
        super.advance();
        if (getReturnCode() == OK_OPTION || getReturnCode() == CANCEL_OPTION)
            setFinished(true);
    }


    @Override
    public void render(final Graphics2D g2d)
    {
        drawBackground(g2d);

        drawTitle(g2d, margin, 60);

        drawMessage(g2d, margin, 140);

        super.render(g2d);
    }

    public void drawBackground(final Graphics2D g2d)
    {
        final int x0 = 0;
        final int y0 = 0;

        if (this._backgroundImage != null)
    {
        g2d.drawImage(_backgroundImage, x0, y0, MHDisplayModeChooser.getScreenSize().width, MHDisplayModeChooser.getScreenSize().height, null);
    }
    else
    {
        g2d.setColor(bgColor);
        g2d.fillRect(x0, y0, MHDisplayModeChooser.getScreenSize().width, MHDisplayModeChooser.getScreenSize().height);
    }
    }
    public void drawTitle(final Graphics2D g2d, final int x, final int y)
    {
        g2d.setColor(titleColor);
        g2d.setFont(_titleFont);
        g2d.drawString(getTitle(), x, y);
    }

    public void drawMessage(final Graphics2D g2d, final int x, int y)
    {
        g2d.setFont(_messageFont);
        g2d.setColor(textColor);

        // Parse the message out so each line fits on screen
        final ArrayList<String> lines = parseMessage(g2d);

        final int lineSpacing = 5;
        for (final String line: lines)
        {
            g2d.drawString(line, x, y);
            y += _messageFont.getSize() + lineSpacing;
        }

    }


    private ArrayList<String> parseMessage(final Graphics2D g)
    {
        final ArrayList<String> output = new ArrayList<String>();

        final FontMetrics fm = g.getFontMetrics(_messageFont);
        final StringTokenizer tokens = new StringTokenizer(_strMessage);
        final int maxLineWidth = MHDisplayModeChooser.getScreenSize().width - (margin*2);
        int currentLine = 0;

        final StringBuffer line = new StringBuffer();
        while (tokens.hasMoreTokens())
        {
            final String token = tokens.nextToken();
            if (fm.stringWidth(line.toString()+" "+token) < maxLineWidth)
                line.append(token+" ");
            else
            {
                System.out.println("Line "+currentLine+": "+line.toString());
                output.add(currentLine, line.toString());
                currentLine++;
                line.setLength(0);
                line.append(token+" ");
            }
        }

        output.add(line.toString());
        return output;
    }


    /**
     * @return the _strTitle
     */
    public String getTitle()
    {
        return _strTitle;
    }


    /**
     * @param title the _strTitle to set
     */
    public void setTitle(final String title)
    {
        _strTitle = title;
    }


    /**
     * @return the _strMessage
     */
    public String getMessage()
    {
        return _strMessage;
    }


    /**
     * @param message the _strMessage to set
     */
    public void setMessage(final String message)
    {
        _strMessage = message;
    }


    /**
     * @return the _backgroundImage
     */
    public Image getBackgroundImage()
    {
        return _backgroundImage;
    }


    /**
     * @param image the _backgroundImage to set
     */
    public void setBackgroundImage(final Image image)
    {
        _backgroundImage = image;
    }


    /**
     * @param font the _titleFont to set
     */
    public void setTitleFont(final Font font)
    {
        _titleFont = font;
    }


    /**
     * @param font the _messageFont to set
     */
    public void setMessageFont(final Font font)
    {
        _messageFont = font;
    }


    /**
     * @param _btnok the _btnOK to set
     */
    public void setOKButton(final MHGUIButton ok)
    {
        remove(_btnOK);

        if (ok == null) return;

        _btnOK = ok;

        _btnOK.addActionListener(this);

        calculateButtonPositions();

        add(_btnOK);
    }


    /**
     * @param cancel the _btnCancel to set
     */
    public void setCancelButton(final MHGUIButton cancel)
    {
        remove(_btnCancel);

        if (cancel == null) return;

        _btnCancel = cancel;

        _btnCancel.addActionListener(this);

        calculateButtonPositions();

        add(_btnCancel);
    }


    public void setButtonCaptions(final String okCaption, final String cancelCaption)
    {
        _btnOK.caption.setText(okCaption);
        _btnCancel.caption.setText(cancelCaption);
    }


    private void calculateButtonPositions()
    {
        final Rectangle2D b = _btnCancel.getBounds();

        _btnCancel.setPosition((int)(MHDisplayModeChooser.getScreenSize().width - b.getWidth() - spacing),
                               (int)(MHDisplayModeChooser.getScreenSize().height - b.getHeight() - spacing));

        _btnOK.getBounds();

        _btnOK.setPosition((int)(_btnCancel.getX() - b.getWidth() - spacing), _btnCancel.getY());
    }


    /**
     * @return the returnCode
     */
    public int getReturnCode()
    {
        return returnCode;
    }


    public Color getTitleColor()
    {
        return titleColor;
    }


    public void setTitleColor(final Color titleColor)
    {
        this.titleColor = titleColor;
    }


    public Color getTextColor()
    {
        return textColor;
    }


    public void setTextColor(final Color textColor)
    {
        this.textColor = textColor;
    }


    public Color getBackgroundColor()
    {
        return bgColor;
    }


    public void setBackgroundColor(final Color bgColor)
    {
        this.bgColor = bgColor;
    }
}
