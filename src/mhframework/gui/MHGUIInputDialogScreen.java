package mhframework.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;


/********************************************************************
 * 
 * 
 * 
 * 
 * @author Michael Henson
 *
 */
public class MHGUIInputDialogScreen extends MHGUIDialogScreen
{
    protected final MHGUILabel _txtInput = new MHGUILabel();
    protected final StringBuffer input;

    public MHGUIInputDialogScreen()
    {
        setTitle("MHGUIInputDialogScreen");
        setMessage("Customize this screen by calling the methods of this class.");
        setOKButton(null);
        setCancelButton(null);

        final int fontSize = 32;
        input = new StringBuffer();

        _txtInput.setText(input.toString());
        _txtInput.setPaint(Color.GREEN);
        _txtInput.setFont("SansSerif", Font.BOLD, fontSize);
        _txtInput.setPosition(50, 280);
        _txtInput.setSize(720, fontSize+10);

        add(_txtInput);
    }


    @Override
    public void render(final Graphics2D g2d)
    {
            final int x0 = 0;
            final int y0 = 0;

            super.render(g2d);

            // Draw outline around text.  Might do away with this
            // eventually.
            g2d.setColor(Color.GRAY);
            g2d.draw3DRect(x0+_txtInput.getX()-10, y0+(int)(_txtInput.getY()-_txtInput.getBounds().getHeight())+10, (int)_txtInput.getBounds().getWidth()+10, (int)_txtInput.getBounds().getHeight(), false);

    }


    public String getInputText()
    {
        return _txtInput.getText();
    }


    private boolean isAccepted(final int keyCode, final char keyChar)
    {
        final int type = Character.getType(keyCode);
        return Character.isLetterOrDigit(keyChar)
            || type == Character.CONNECTOR_PUNCTUATION
            || type == Character.OTHER_PUNCTUATION
            || keyChar == '.'
            || keyChar == '\''
            || keyChar == '-';
    }


    @Override
    public void keyPressed(final KeyEvent e)
    {
        final char keyChar = e.getKeyChar();
        final int  keyCode = e.getKeyCode();

        if (isAccepted(keyCode, keyChar))
            input.append(keyChar);
        else if (keyChar == ' ' || keyChar == '\t')
            input.append(' ');
        else if (keyCode == KeyEvent.VK_BACK_SPACE)
        {
            if (input.length() > 0)
                input.deleteCharAt(input.length()-1);
        }
        else if (keyCode == KeyEvent.VK_ENTER)
        {
            this.setNextScreen(null);
            this.setDisposable(true);
            this.setFinished(true);
        }
        else if (keyCode == KeyEvent.VK_ESCAPE)
        {
            this.setNextScreen(null);
            this.setDisposable(true);
            this.setFinished(true);
            input.setLength(0);
        }
        else
            super.keyPressed(e);

        _txtInput.setText(input.toString());
    }
}
