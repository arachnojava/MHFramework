package mhframework.net.client;

import java.awt.Color;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.net.ConnectException;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;
import mhframework.MHLogFile;
import mhframework.MHTextFile;
import mhframework.io.net.client.MHAbstractClient;
import mhframework.net.MHMessageType;
import mhframework.net.MHNetworkMessage;
import mhframework.net.MHSerializableClientList;
import mhframework.net.server.MHServerModule;

/********************************************************************
 * This class can be instantiated by any client application that
 * needs to communicate with a multiplayer server.  It simply needs
 * the IP address, and optionally the port number, of the server.
 * 
 * @author Michael Henson
 */
public class MHClientModule extends MHAbstractClient implements Runnable
{
    public static final int STATUS_CONNECTED = 1;
    public static final int STATUS_DISCONNECTED = 0;
    public static final int NO_ID_ASSIGNED = -1;

    public MHTextFile logFile = new MHLogFile("MHClientModuleLog.txt");
    
    private Socket socket;
    private ObjectOutputStream outStream;
    private ObjectInputStream inStream;
    private ConcurrentLinkedQueue<MHNetworkMessage> messageQueue;
    private MHSerializableClientList clientList;
    private int clientID = NO_ID_ASSIGNED;
    private String playerName;
    private int status = STATUS_DISCONNECTED;
    private String statusMessage = "Not connected.";
    private int maxConnections;
    private Color[] availableColors = new Color[] {Color.BLUE, Color.RED, Color.GREEN, Color.YELLOW};
    private boolean errorState = false;

    
    private static MHClientModule instance;
    

    public int getClientID()
    {
        return clientID;
    }


    /****************************************************************
     * Connects to the server specified by <tt>hostIP</tt> using
     * the default port (5000), and then begins a background thread
     * to receive messages.
     *
     * @param hostIP A string containing the IP address of the server
     */
    private MHClientModule(final String hostIP)
    {
        this(hostIP, MHServerModule.DEFAULT_PORT);
    }


    /****************************************************************
     * Connects to the server specified by <tt>hostIP</tt> on the
     * port specified by <tt>port</tt>, and then begins a background
     * thread to receive messages.
     *
     * @param hostIP A string containing the IP address of the server.
     * @param port   An integer indicating the server's listening
     *               port.
     */
    private MHClientModule(final String hostIP, final int port)
    {
        // Connect to the server
        try
        {
            socket = new Socket(hostIP, port);

            inStream = new ObjectInputStream(socket.getInputStream());
            outStream = new ObjectOutputStream(socket.getOutputStream());

            status = STATUS_CONNECTED;
            statusMessage = "Connected to IP address "+hostIP;

            new Thread(this).start();
        }
        catch (ConnectException ce)
        {
            System.out.println(ce);
            status = STATUS_DISCONNECTED;
            statusMessage = ce.getMessage();
            errorState = true;
        }
        catch (final IOException ie)
        {
            System.out.println(ie);
            status = STATUS_DISCONNECTED;
            statusMessage = ie.getMessage();
            errorState = true;
        }
    }

    
    public static MHClientModule create(final String hostIP)
    {
        if (instance == null)
            instance = new MHClientModule(hostIP);
        
        return instance;
    }
    
    
    public static MHClientModule create(final String hostIP, final int port)
    {
        if (instance == null)
            instance = new MHClientModule(hostIP, port);

        return instance;
    }
    
    
    public static MHClientModule get()
    {
        return instance;
    }
    
    
    /****************************************************************
     * Retrieve the next message from the queue.
     *
     * @return The next message from the queue.
     */
    public MHNetworkMessage getMessage()
    {
        final MHNetworkMessage msg = getMessageQueue().remove();

        return msg;
    }


    /****************************************************************
     * Peek at the next message in the queue without removing it.
     *
     * @return The next message from the queue.
     */
    public MHNetworkMessage peek()
    {
        final MHNetworkMessage msg = getMessageQueue().peek();

        return msg;
    }


    /****************************************************************
     *
     * @param colors
     */
    public void setColorList(final Color[] colors)
    {
        availableColors = colors;
    }


    /****************************************************************
     *
     * @return An array of available colors.
     */
    public Color[] getAvailableColors()
    {
        return availableColors;
    }



    /****************************************************************
     * Return true if the queue of incoming messages is not empty;
     * return false it if is.
     *
     * @return True if there is an incoming message waiting, false
     *         otherwise.
     */
    public boolean isMessageWaiting()
    {
        return !getMessageQueue().isEmpty();
    }


    /****************************************************************
     * Continuously receives server messages and adds them to this
     * class' internal message queue.  If the message is recognized
     * as a standard game engine message, this class responds to it.
     * Otherwise it is queued up for the game to retrieve and handle.
     */
    public void run()
    {
        while (true)
        {
        try
        {
                final MHNetworkMessage msg = (MHNetworkMessage) inStream.readObject();
                
                logFile.append(msg.getMessageType() + " recieved by Client " + clientID);

                // If message is one that engine should respond to, do it.
                if (msg.getMessageType().equals(MHMessageType.ASSIGN_CLIENT_ID))
                {
                        clientID = Integer.parseInt(msg.getPayload().toString());
                }
                else if (msg.getMessageType().equals(MHMessageType.BROADCAST_CLIENT_LIST))
                {
                    clientList = (MHSerializableClientList)msg.getPayload();
                }
                else if (msg.getMessageType().equals(MHMessageType.SET_CONNECTION_LIMIT))
                {
                    maxConnections = ((Integer)msg.getPayload()).intValue();
                }
                else if (msg.getMessageType().equals(MHMessageType.AVAILABLE_COLORS))
                {
                    setColorList((Color[])msg.getPayload());
                }
                else if (msg.getMessageType().equals(MHMessageType.REGISTER_COLOR_ERROR))
                {
                    errorState = true;
                    statusMessage = (String)msg.getPayload();
                }
                else
                {
                    // If message is not for the engine, then it must be
                    // for the game, so queue it up.
                    // DEBUG
                    //System.out.println("MHClientModule queueing " + msg.getMessageType());
                    queueMessage(msg);
                }
            }
        catch (final StreamCorruptedException sce)
        {
            System.out.println("MHClientModule.run():  "+sce);
            statusMessage = sce.getMessage();
            status = STATUS_DISCONNECTED;
        }
        catch (final EOFException eofe)
        {
            // Do nothing?  This is normal when there's not a message waiting.
        }
        catch (final IOException ioe)
        {
            System.out.println("MHClientModule.run():  "+ioe);
            statusMessage = ioe.getMessage();
            status = STATUS_DISCONNECTED;
        }
        catch (final ClassNotFoundException cnfe)
        {
            System.out.println("MHClientModule.run():  "+cnfe);
            statusMessage = cnfe.getMessage();
            cnfe.printStackTrace();
        }
        catch (final ClassCastException cce)
        {
            System.out.println("MHClientModule.run():  "+cce);
            statusMessage = cce.getMessage();
            cce.printStackTrace();
        }
        }
    }


    public int getStatus()
    {
        return status;
    }


    public String getStatusMessage()
    {
        if (statusMessage == null)
            statusMessage = "Status unknown";

        return statusMessage;
    }


    /****************************************************************
     * Returns a list of clients who are connected to the same
     * server as this client module.
     *
     * @return A list of all clients connected to the same server.
     */
    public MHSerializableClientList getClientList()
    {
        if (clientList == null)
            clientList = new MHSerializableClientList();

        return clientList;
    }


    /****************************************************************
     * Sends a message to the server.
     */
    public void sendMessage(final MHNetworkMessage message)
    {
        if (message == null) return;

        try
        {
            logFile.append("MHClientModule.sendMessage(" + message.getMessageType() + ") from Client " + clientID);
            outStream.writeObject(message);
        }
        catch (final IOException ie)
        {
            System.out.println(ie);
            statusMessage = "I/O error.  Message not sent.";
            logFile.append(ie.getMessage());
            logFile.append(statusMessage);
        }
    }

    /****************************************************************
     * Utility method for lazy, safe instantiation of the message
     * queue.
     *
     * @return The message queue.
     */
    private ConcurrentLinkedQueue<MHNetworkMessage> getMessageQueue()
    {
        if (messageQueue == null)
            messageQueue = new ConcurrentLinkedQueue<MHNetworkMessage>();

        return messageQueue;
    }

    /****************************************************************
     * Add the latest incoming message to the message queue.
     *
     * @param message
     */
    public void queueMessage(final MHNetworkMessage message)
    {
        logFile.append("MHClientModule.queueMessage(" + message.getMessageType() + ")");
        getMessageQueue().add(message);
    }

    public void disconnect()
    {
        try
        {
            final MHNetworkMessage disco = new MHNetworkMessage(MHMessageType.DISCONNECT, null);

            sendMessage(disco);
            status = STATUS_DISCONNECTED;
            statusMessage = "Not connected.";
            clientList = null;
            inStream = null;
            outStream = null;
            socket.close();
            socket = null;
        }
        catch (final Exception e) {}
    }


    public int getMaxConnections()
    {
        return maxConnections;
    }

    public void sendChat(final String message)
    {
        final MHNetworkMessage chat = new MHNetworkMessage(MHMessageType.CHAT, message);

        sendMessage(chat);
    }


    public void registerPlayerName(final String name)
    {
        final MHNetworkMessage setName = new MHNetworkMessage(MHMessageType.REGISTER_NAME, name);

        sendMessage(setName);

        playerName = name;
    }


    public void registerPlayerColor(final Color color)
    {
        final MHNetworkMessage setColor = new MHNetworkMessage(MHMessageType.REGISTER_COLOR, color);

        sendMessage(setColor);
    }


    public String getPlayerName()
    {
        return playerName;
    }


    /**
     *
     */
    public void clearErrorState()
    {
        errorState = false;
        statusMessage = "OK";
    }


    /**
     * @return the errorState
     */
    public boolean isErrorState()
    {
        return errorState;
    }


    @Override
    public void sendMessage(mhframework.io.net.MHNetworkMessage message)
    {
        // TODO Auto-generated method stub
        
    }


    @Override
    public void connect()
    {
        // TODO Auto-generated method stub
        
    }
}
