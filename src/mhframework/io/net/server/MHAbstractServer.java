package mhframework.io.net.server;

import java.awt.Color;
import java.net.InetAddress;
import mhframework.io.net.MHMessageType;
import mhframework.io.net.MHNetworkMessage;
import mhframework.io.net.MHSerializableClientInfo;
import mhframework.io.net.MHSerializableClientList;

public abstract class MHAbstractServer
{
    /** Number used for assigning unique identifiers to clients */
    protected static int nextClientID = 0;
    /** List of connected clients. */
    protected final MHClientList clients;
    protected Color[] defaultColors = {Color.WHITE, Color.BLUE, Color.RED, Color.GREEN, Color.YELLOW,
                        Color.CYAN, Color.MAGENTA, Color.ORANGE, Color.PINK};
    /** Reference to game server for delegating game-specific messages */
    protected MHGameServer gameServer;

    public MHAbstractServer()
    {
        super();
        clients = new MHClientList();
    }

    public abstract void send(final int recipientClientID, final MHNetworkMessage message);
    public abstract void sendToAll(final MHNetworkMessage message);

    
    public void setColorOptions(final Color[] list)
    {
        defaultColors = list;
        broadcastAvailableColors();
    }

    public void broadcastAvailableColors()
    {
        // Bundle the list into a network message.
        final MHNetworkMessage msg = new MHNetworkMessage();
        msg.setMessageType(MHMessageType.AVAILABLE_COLORS);
        msg.setPayload(defaultColors);
    
        // Send the results to all connected clients.
        sendToAll(msg);
    }

    public Color[] getAvailableColors()
    {
        return defaultColors;
    }


    public void setGameServer(final MHGameServer gameServer)
    {
        this.gameServer = gameServer;
    }

    public MHClientList getClientList()
    {
        return clients;
    }

    public void forwardMessage(final MHNetworkMessage message)
    {
        gameServer.receiveMessage(message, this);
    }

    public void setClientName(final MHNetworkMessage message)
    {
            final MHClientInfo client = clients.get(message.getSender());
            final String newName = (String) message.getPayload();
            client.name = newName;
            
            // If a game server is attached, notify it that a new client has connected.
            if (gameServer != null)
                gameServer.receiveMessage(message, this);
    }

    public boolean setClientColor(final MHNetworkMessage message)
    {
        final MHClientInfo client = clients.get(message.getSender());
        
        if (client == null)
            return false;
        
        final Color c = (Color) message.getPayload();
    
        // If requested color is available, set it.
        boolean success = false;
        for (final Color defaultColor : defaultColors)
        {
            if (defaultColor.equals(c))
            {
                client.color = c;
                success = true;
            }
        }
        
        if (!success)
            return false;

    
        // Remove color from list of available choices.
        final Color[] newList = new Color[defaultColors.length-1];
        int newIndex = 0;
        for (int i = 0; i < defaultColors.length; i++)
        {
            if (!defaultColors[i].equals(c))
                newList[newIndex++] = defaultColors[i];
        }
        defaultColors = newList;
        
        return true;
    }


    public static String getIPAddress()
    {
        String ip = "";

        try
        {
            ip = InetAddress.getLocalHost().getHostAddress();
        }
        catch (final Exception e)
        {
            e.printStackTrace();
        }

        return ip;
    }

    /****************************************************************
     * Send a message to a client telling it what its server-assigned
     * unique identifier is.
     *
     * @param info The information identifying the client's
     *             relevant properties.
     */
    protected void assignClientID(final MHClientInfo info)
    {
        final MHNetworkMessage msg = new MHNetworkMessage();
        msg.setMessageType(MHMessageType.ASSIGN_CLIENT_ID);
        msg.setPayload(new Integer(info.id));
        msg.setSender(info.id);
        send(info.id, msg);
    
        // If a game server is attached, notify it that a new client has connected.
        if (gameServer != null)
            gameServer.receiveMessage(msg, this);
    }
    

    public synchronized void broadcastClientList()
    {
        // Build a serialized summary of connected clients.
        final MHSerializableClientList clientList = new MHSerializableClientList();
        synchronized (clients)
        {
        for (final MHClientInfo c: clients)
            clientList.add(new MHSerializableClientInfo(c.id, c.name, c.color));
    
        // Bundle the list into a network message.
        final MHNetworkMessage msg = new MHNetworkMessage();
        msg.setMessageType(MHMessageType.BROADCAST_CLIENT_LIST);
        msg.setPayload(clientList);
    
        // Send the results to all connected clients.
        sendToAll(msg);
        }
    }

    public abstract void process(MHNetworkMessage message);
}