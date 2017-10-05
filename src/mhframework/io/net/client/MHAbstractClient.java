package mhframework.io.net.client;

import java.awt.Color;
import java.util.Observable;
import mhframework.io.net.MHMessageType;
import mhframework.io.net.MHNetworkMessage;
import mhframework.io.net.MHSerializableClientList;


/********************************************************************
 * Abstract class for deriving network clients.  The only abstract
 * method in this class is <tt>sendMessage()</tt>, which may be
 * overridden to work with TCP, UDP, or local method calls.
 *
 * <p>For example, MHClientModule extends this class and provides a 
 * concrete implementation of a TCP network client.</p>
 *
 * @author Michael Henson
 */
public abstract class MHAbstractClient extends Observable
{
    // ID Constants
    public static final int NO_ID_ASSIGNED = -1;

    // Status Constants
    public static final int STATUS_DISCONNECTED = 0;
    public static final int STATUS_CONNECTED = 1;

    private int status = STATUS_DISCONNECTED;
    protected String statusMessage = "Not connected.";
    private int clientID = NO_ID_ASSIGNED;
    private boolean errorState = false;
    private Color[] availableColors = new Color[] {Color.BLUE, Color.RED, Color.GREEN, Color.YELLOW};
    //private ConcurrentLinkedQueue<MHNetworkMessage> messageQueue;
    private MHSerializableClientList clientList;
    private String playerName;


    // Abstract methods
    public abstract void sendMessage(MHNetworkMessage message);
    
    // Concrete methods
    public void sendChat(final String message)
    {
        final MHNetworkMessage chat = new MHNetworkMessage(MHMessageType.CHAT, message, clientID);

        sendMessage(chat);
    }
    
    
    public int getStatus()
    {
        return status;
    }

    public void setStatus(int status)
    {
        this.status = status;
    }

    public int getClientID()
    {
        return clientID;
    }

    public void setClientID(int clientID)
    {
        this.clientID = clientID;
    }

    
    public boolean process(MHNetworkMessage msg)
    {
        System.out.println("MHAbstractClient.process(" + msg.getMessageType() + ")");

    // If message is one that engine should respond to, do it.
    if (msg.getMessageType().equals(MHMessageType.ASSIGN_CLIENT_ID))
    {
        setClientID(Integer.parseInt(msg.getPayload().toString()));
        return true;
    }
    else if (msg.getMessageType().equals(MHMessageType.BROADCAST_CLIENT_LIST))
    {
        setClientList((MHSerializableClientList)msg.getPayload());
        return true;
    }
//    else if (msg.getMessageType().equals(MHMessageType.SET_CONNECTION_LIMIT))
//    {
//        maxConnections = ((Integer)msg.getPayload()).intValue();
//    }
    else if (msg.getMessageType().equals(MHMessageType.AVAILABLE_COLORS))
    {
        setAvailableColors((Color[])msg.getPayload());
        return true;
    }
    else if (msg.getMessageType().equals(MHMessageType.REGISTER_COLOR_ERROR))
    {
        setErrorState(true);
        statusMessage = (String)msg.getPayload();
        return true;
    }
    else
    {
        // If message is not for the engine, then it must be
        // for the game, so queue it up.
        // DEBUG
        //System.out.println("MHClientModule queueing " + msg.getMessageType());
        queueMessage(msg);
        return false;
    }
    }
    
    public void queueMessage(final MHNetworkMessage message)
    {
        //getMessageQueue().add(message);
        setChanged();
        notifyObservers(message);
    }
    
    
//    public boolean isMessageWaiting()
//    {
//        return !getMessageQueue().isEmpty();
//    }
    
    
    public String getStatusMessage()
    {
        if (statusMessage == null)
            statusMessage = "Status unknown";
    
        return statusMessage;
    }
 
    
    public Color[] getAvailableColors()
    {
        return availableColors;
    }
    
    
    public void setAvailableColors(final Color[] colors)
    {
        availableColors = colors;
    }
    
    public void clearErrorState()
    {
        errorState = false;
        statusMessage = "OK";
    }

    public boolean isErrorState()
    {
        return errorState;
    }


    protected void setErrorState(boolean errorState)
    {
        this.errorState = errorState;
    }


    public void disconnect()
    {
        setStatus(STATUS_DISCONNECTED);
        statusMessage = "Not connected.";
        clientList = null;
    }

    /****************************************************************
     * Utility method for lazy, safe instantiation of the message
     * queue.
     *
     * @return The message queue.
     */
//    private ConcurrentLinkedQueue<MHNetworkMessage> getMessageQueue()
//    {
//        if (messageQueue == null)
//            messageQueue = new ConcurrentLinkedQueue<MHNetworkMessage>();
//
//        return messageQueue;
//    }

//    public MHNetworkMessage peek()
//    {
//        final MHNetworkMessage msg = getMessageQueue().peek();
//
//        return msg;
//    }
    
//    public MHNetworkMessage getMessage()
//    {
//        final MHNetworkMessage msg = getMessageQueue().remove();
//
//        return msg;
//    }
    
    public void registerPlayerColor(final Color color)
    {
        final MHNetworkMessage setColor = new MHNetworkMessage(MHMessageType.REGISTER_COLOR, color, clientID);

        sendMessage(setColor);
    }
    
    
    public void registerPlayerName(final String name)
    {
        final MHNetworkMessage setName = new MHNetworkMessage(MHMessageType.REGISTER_NAME, name, clientID);

        sendMessage(setName);

        setPlayerName(name);
    }
    
    
    public MHSerializableClientList getClientList()
    {
        if (clientList == null)
            clientList = new MHSerializableClientList();

        return clientList;
    }
    
    
    public void setClientList(MHSerializableClientList mhSerializableClientList)
    {
        this.clientList = mhSerializableClientList;
    }

    public String getPlayerName()
    {
        return playerName;
    }

    public void setPlayerName(String playerName)
    {
        this.playerName = playerName;
    }

    public abstract void connect();

    public boolean isMessageWaiting()
    {
        // TODO Auto-generated method stub
        return false;
    }

    public MHNetworkMessage getMessage()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public MHNetworkMessage peek()
    {
        // TODO Auto-generated method stub
        return null;
    }
}
