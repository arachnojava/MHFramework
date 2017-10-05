package mhframework.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.concurrent.ConcurrentLinkedQueue;
import mhframework.MHPoint;
import mhframework.io.net.MHNetworkMessage;
import mhframework.io.net.client.MHObservableClient;
import mhframework.io.net.event.MHChatMessageListener;


/********************************************************************
 * 
 * 
 * 
 * @author Michael Henson
 */
public class MHGUIChatClient extends MHGUIComponent implements 
                                                MHChatMessageListener
{
    MHObservableClient client;
    ChatDisplayArea displayArea;
    MHGUITextDisplayArea inputArea;
    String displayText;
    StringBuffer inputText = new StringBuffer();
    Font font = new Font("SansSerif", Font.PLAIN, 12);

    MHPoint location;

    public MHGUIChatClient(final MHObservableClient clientModule, 
            final int x, final int y, final int width, final int height)
    {
        client = clientModule;
        client.addChatListener(this);
        
        location = new MHPoint(x, y);
        this.width = width;
        this.height = height;

        inputArea = new MHGUITextDisplayArea();
        inputArea.setPosition(x, y+height-font.getSize()*2);
        inputArea.setBorderColor(new Color(128,0,0));
        inputArea.width = width;
        inputArea.height = font.getSize()*2;

        displayArea = new ChatDisplayArea();
        displayArea.setPosition(x, y);
        displayArea.setBorderWidth(0);
        displayArea.width = width;
        displayArea.height = height-inputArea.height;
    }


    @Override
    public void keyPressed(final KeyEvent e)
    {
        final char keyChar = e.getKeyChar();
        final int  keyCode = e.getKeyCode();

        if (Character.isLetterOrDigit(keyChar)
            || keyChar == '!' || keyChar == '\'' || keyChar == '(' || keyChar == ')'
            || Character.getType(keyCode) == Character.CONNECTOR_PUNCTUATION
            || Character.getType(keyCode) == Character.OTHER_PUNCTUATION)
            inputText.append(keyChar);
        else if (keyChar == ' ' || keyChar == '\t')
            inputText.append(' ');
        else if (keyCode == KeyEvent.VK_BACK_SPACE)
        {
            if (inputText.length() > 0)
                inputText.deleteCharAt(inputText.length()-1);
        }
        else if (keyCode == KeyEvent.VK_ENTER)
        {
            // Send inputText to server!
            client.getClient().sendChat(inputText.toString());
            inputText.setLength(0);
        }
        else if (keyCode == KeyEvent.VK_ESCAPE)
        {
            inputText.setLength(0);
        }

        inputArea.setText(inputText.toString());
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
//        if (client.isMessageWaiting())
//        {
//            MHNetworkMessage msg = client.peek();
//            if (msg.getMessageType().equals(MHMessageType.CHAT))
//            {
//                msg = client.getMessage();
//                displayArea.addMessage(msg.getPayload().toString());
//            }
//        }
    }


    @Override
    public void render(final Graphics2D g)
    {
        displayArea.render(g);
        inputArea.resize(g);
        inputArea.render(g);
    }


    @Override
    public void chatMessageReceived(MHNetworkMessage message)
    {
        displayArea.addMessage(message.getPayload().toString());
    }
}

class ChatDisplayArea extends MHGUITextDisplayArea
{
    private final ConcurrentLinkedQueue<MHGUITextDisplayArea> list = new ConcurrentLinkedQueue<MHGUITextDisplayArea>();

    public void addMessage(final String msg)
    {
        final MHGUITextDisplayArea chatEntry = new MHGUITextDisplayArea();
        chatEntry.setText(msg);
        chatEntry.setWidth(getWidth());
        chatEntry.setBorderWidth(1);
        list.add(chatEntry);
    }

    @Override
    public void render(final Graphics2D g)
    {
        int y = getY() + font.getSize() + 2;


        for (final MHGUITextDisplayArea entry : list)
        {
            entry.resize(g);
            entry.setPosition(getX(), y);
            entry.render(g);
            y += entry.getHeight();
        }

        // This goes here because it depends on the resize() call above.
        while (calculateHeight() > getHeight())
            list.remove();
    }


    private int calculateHeight()
    {
        int totalHeight = 0;
        for (final MHGUITextDisplayArea entry : list)
            totalHeight += entry.getHeight();

        return totalHeight;
    }
}