package mhframework.net.server;

import java.awt.Color;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import mhframework.net.MHMessageType;
import mhframework.net.MHNetworkMessage;
import mhframework.net.MHSerializableClientInfo;
import mhframework.net.MHSerializableClientList;

/********************************************************************
 * The MHServerModule class provides a network interface for a
 * multiplayer game server.
 *
 * <p>The class will automatically handle all of the message types
 * defined in MHMessageType, but if your application has needs
 * beyond those, then you must call setGameServer() and pass it
 * an object that implements the MHGameServer interface.  It is
 * the game server that will receive any messages that this
 * server module does not handle itself.</p>
 *
 * <p>The code upon which this class is based was written by
 * Greg Travis for IBM DeveloperWorks.</p>
 * 
 * @author Michael Henson
 */
public class MHServerModule
{
    public static final int DEFAULT_PORT = 5000;
    private static final int NO_CONNECTION_LIMIT = Integer.MAX_VALUE;

    /** The ServerSocket we'll use for accepting new connections */
    ServerSocket serverSocket;

    /** List of connected clients. */
    private final MHClientList clients;

    /** Number used for assigning unique identifiers to clients */
    static int nextClientID = 0;

    private int port = DEFAULT_PORT;



    private int connectionLimit = NO_CONNECTION_LIMIT;

    /** Reference to game server for delegating game-specific messages */
    private MHGameServer gameServer;

    boolean listening = false;

    Thread listenerThread = null;

    private Color[] defaultColors = {Color.WHITE, Color.BLUE, Color.RED, Color.GREEN, Color.YELLOW,
                    Color.CYAN, Color.MAGENTA, Color.ORANGE, Color.PINK};

    /****************************************************************
     * Creates a server module that listens for connections on the
     * default port (MHServerModule.DEFAULT_PORT).
     *
     * @throws IOException
     */
    public MHServerModule() throws IOException
    {
        this(DEFAULT_PORT);
    }


    /****************************************************************
     * Creates a server module that listens for connections on the
     * specified port.
     *
     * @throws IOException
     */
    public MHServerModule(final int port) throws IOException
    {
        clients = new MHClientList();
        this.port = port;
        listen(port);
    }


    /* (non-Javadoc)
     * @see mhframework.net.server.MHServerModuleInterface#reset()
     */
    public void reset()
    {
        shutdown();
        listen(port);
    }


    private void listen(final int port)
    {
        if (listenerThread == null)
            listenerThread = new ListenerThread(this);

        // Wait for server to start before continuing.
        while (!isListening()) System.out.println("Waiting for connection thread...");
    }


    /* (non-Javadoc)
     * @see mhframework.net.server.MHServerModuleInterface#isListening()
     */
    public boolean isListening()
    {
        return listening;
    }


    /****************************************************************
     * Send a message to a client telling it what its server-assigned
     * unique identifier is.
     *
     * @param info The information identifying the client's
     *             relevant properties.
     */
    void assignClientID(final MHClientInfo info)
    {
        final MHNetworkMessage msg = new MHNetworkMessage();
        msg.setMessageType(MHMessageType.ASSIGN_CLIENT_ID);
        msg.setPayload(new Integer(info.id));
        try
        {
            info.outputStream.writeObject(msg);
        }
        catch (final IOException e)
        {
            e.printStackTrace();
        }

        // If a game server is attached, notify it that a new client has connected.
        if (gameServer != null)
            gameServer.receiveMessage(info, msg, this);
    }


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


    /* (non-Javadoc)
     * @see mhframework.net.server.MHServerModuleInterface#broadcastClientList()
     */
    public void broadcastClientList()
    {
        // Build a serialized summary of connected clients.
        final MHSerializableClientList clientList = new MHSerializableClientList();
        for (final MHClientInfo c: clients)
            clientList.add(new MHSerializableClientInfo(c.id, c.name, c.color));

        // Bundle the list into a network message.
        final MHNetworkMessage msg = new MHNetworkMessage();
        msg.setMessageType(MHMessageType.BROADCAST_CLIENT_LIST);
        msg.setPayload(clientList);

        // Send the results to all connected clients.
        sendToAll(msg);
    }


    /* (non-Javadoc)
     * @see mhframework0202.net.server.MHServerModuleInterface#send(int, mhframework0202.net.common.MHNetworkMessage)
     */
    /* (non-Javadoc)
     * @see mhframework.net.server.MHServerModuleInterface#send(int, mhframework.net.common.MHNetworkMessage)
     */
    public void send(final int recipientClientID,
                    final MHNetworkMessage message)
    {
        synchronized (clients)
        {
            final MHClientInfo client = clients.get(recipientClientID);

            if (client == null)
            {
                System.err.println("ERROR:  Client " + recipientClientID + " not found.");
                return;
            }

            try
            {
                client.outputStream.writeObject(message);
            }
            catch (final IOException ioe)
            {
                System.out.println(ioe);
            }
        }
    }


    /* (non-Javadoc)
     * @see mhframework.net.server.MHServerModuleInterface#sendToAll(mhframework.net.common.MHNetworkMessage)
     */
    public void sendToAll(final MHNetworkMessage message)
    {
        // We synchronize on this because another thread might be
        // calling removeConnection() and this would screw us up
        // as we tried to walk through the list
        synchronized (clients)
        {
            // For each client ...
            for (final MHClientInfo ci : clients)
            {
                try
                {
                	if (ci.outputStream != null)
                		ci.outputStream.writeObject(message);
                }
                catch (final IOException ioe)
                {
                    System.out.println(ioe);
                }
            }
        }
    }


    /****************************************************************
     * Remove a client record from the list of connected clients and
     * close its streams.
     *
     * @param s The socket providing the connection for the client
     *          being removed.
     */
    void removeConnection(final Socket s)
    {
        // Synchronize so we don't mess up sendToAll() while it walks
        // down the list of all output streams
        synchronized (clients)
        {
            System.out.println("Removing connection to " + s);

            clients.remove(s);

            // Make sure it's closed
            try
            {
                s.close();
            }
            catch (final IOException ie)
            {
                System.out.println("Error closing " + s);
                ie.printStackTrace();
            }
        }

        // Update clients' local lists of connected clients.
        broadcastClientList();
    }


    /* (non-Javadoc)
     * @see mhframework.net.server.MHServerModuleInterface#getPort()
     */
    public int getPort()
    {
        return this.port;
    }

    /* (non-Javadoc)
     * @see mhframework.net.server.MHServerModuleInterface#getIPAddress()
     */
    public String getIPAddress()
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


    /* (non-Javadoc)
     * @see mhframework.net.server.MHServerModuleInterface#setGameServer(mhframework.net.server.MHGameServer)
     */
    public void setGameServer(final MHGameServer gameServer)
    {
        this.gameServer = gameServer;
    }


    /* (non-Javadoc)
     * @see mhframework.net.server.MHServerModuleInterface#getClientList()
     */
    public MHClientList getClientList()
    {
        return clients;
    }

    // Main routine
    // java MHServerModule <port>
    static public void main(final String args[]) throws Exception
    {
        // Get the port # from the command line
        final int port = Integer.parseInt(args[0]);
        // Create a Server object, which will automatically begin
        // accepting connections.
        new MHServerModule(port);
    }


    public void forwardMessage(final MHClientInfo sender, final MHNetworkMessage message)
    {
        gameServer.receiveMessage(sender, message, this);
    }


    /* (non-Javadoc)
     * @see mhframework.net.server.MHServerModuleInterface#setClientName(java.net.Socket, mhframework.net.common.MHNetworkMessage)
     */
    public void setClientName(final Socket socket, final MHNetworkMessage message)
    {
        final MHClientInfo client = clients.get(socket);
        final String newName = (String) message.getPayload();
        client.name = newName;
        
        // If a game server is attached, notify it that a new client has connected.
        if (gameServer != null)
            gameServer.receiveMessage(client, message, this);
    }


    /* (non-Javadoc)
     * @see mhframework.net.server.MHServerModuleInterface#shutdown()
     */
    public void shutdown()
    {
        synchronized(clients)
        {
            for (final MHClientInfo c: clients)
                removeConnection(clients.get(c.id).socket);
        }
        
        try
        {
            serverSocket.close();
        } 
        catch (IOException e)
        {
            e.printStackTrace();
        }
        serverSocket = null;
    }


    /* (non-Javadoc)
     * @see mhframework.net.server.MHServerModuleInterface#setClientColor(java.net.Socket, mhframework.net.common.MHNetworkMessage)
     */
    public void setClientColor(final Socket socket, final MHNetworkMessage message)
    {
        final MHClientInfo client = clients.get(socket);
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
        {
            // Send error message to client.
            final MHNetworkMessage msg = new MHNetworkMessage(MHMessageType.REGISTER_COLOR_ERROR, "Color already in use.  Choose another.");
            send(client.id, msg);
            return;
        }

        // Remove color from list of available choices.
        final Color[] newList = new Color[defaultColors.length-1];
        int newIndex = 0;
        for (int i = 0; i < defaultColors.length; i++)
        {
            if (!defaultColors[i].equals(c))
                newList[newIndex++] = defaultColors[i];
        }
        defaultColors = newList;
    }


    /* (non-Javadoc)
     * @see mhframework.net.server.MHServerModuleInterface#getMaxConnections()
     */
    public int getMaxConnections()
    {
        return connectionLimit;
    }


    /* (non-Javadoc)
     * @see mhframework.net.server.MHServerModuleInterface#setConnectionLimit(int)
     */
    public void setConnectionLimit(final int connectionLimit)
    {
        this.connectionLimit = connectionLimit;
    }


    public Color[] getAvailableColors()
    {
        return defaultColors;
    }


}

/*=================================================================*/
class ServerThread extends Thread
{
    // The Server that spawned us
    private final MHServerModule server;

    // The Socket connected to our client
    private final Socket socket;


    // Constructor.
    public ServerThread(final MHServerModule server, final Socket socket)
    {
        // Save the parameters
        this.server = server;
        this.socket = socket;

        // Start up the thread
        start();
    }


    // This runs in a separate thread when start() is called in the
    // constructor.
    @Override
    public void run()
    {
        String messageType;

        try
        {
            // Create an Input Stream for communication; the client
            // is using an Output Stream to talk to us
            final ObjectInputStream din = new ObjectInputStream(
                            socket.getInputStream());
            // Over and over, forever ...
            while (true)
            {
                final MHNetworkMessage message = (MHNetworkMessage) din
                                .readObject();
                messageType = message.getMessageType();

                if (messageType.equals(MHMessageType.CHAT))
                {
                    final String sender = server.getClientList().get(socket).name;
                    message.setPayload(sender+":  "+message.getPayload().toString());
                    server.sendToAll(message);
                }
                else if (messageType.equals(MHMessageType.DISCONNECT))
                {
                    server.removeConnection(socket);
                    server.broadcastClientList();
                }
                else if (messageType.equals(MHMessageType.REGISTER_NAME))
                {
                    server.setClientName(socket, message);
                    server.broadcastClientList();
                }
                else if (messageType.equals(MHMessageType.REGISTER_COLOR))
                {
                    server.setClientColor(socket, message);
                    server.broadcastClientList();
                    server.broadcastAvailableColors();
                }
                else if (messageType.equals(MHMessageType.SET_CONNECTION_LIMIT))
                {
                    server.setConnectionLimit(((Integer)message.getPayload()).intValue());
                    server.sendToAll(message);
                }
                else
                {
                    final MHClientInfo sender = server.getClientList().get(socket);
                    server.forwardMessage(sender, message);
                }
            }
        }
        catch (final EOFException eofe)
        {
            // This doesn't need an error message
        }
        catch (final IOException ioe)
        {
            ioe.printStackTrace();
        }
        catch (final ClassNotFoundException cnfe)
        {
            cnfe.printStackTrace();
        }
        finally
        {
            // The connection is closed for one reason or another,
            // so have the server get rid of it
            server.removeConnection(socket);
        }
    }
}


/*=================================================================*/
class ListenerThread extends Thread
{
    // The Server that spawned us
    private final MHServerModule server;

    // Constructor.
    public ListenerThread(final MHServerModule server)
    {
        // Save the parameters
        this.server = server;

        // Start up the thread
        start();
    }


    // This runs in a separate thread when start() is called in the
    // constructor.
    @Override
    public void run()
    {
        try
        {
            // Create the ServerSocket
            server.serverSocket = new ServerSocket(server.getPort());

            System.out.println("Starting server at address "
                            + server.getIPAddress());
            System.out.println("Listening on " + server.serverSocket);

            while (true)
            {
                server.listening = true;

                // Grab the next incoming connection
                final Socket s = server.serverSocket.accept();

                // Tell the world we've got it
                System.out.println("Connection from " + s);

                final MHClientList clientList = server.getClientList();
                if (clientList != null && clientList.size() >= server.getMaxConnections())
                {
                    continue;
                }

                // Create a stream for writing data to the other side
                final ObjectOutputStream outStream = new ObjectOutputStream(
                                s.getOutputStream());

                // Add info for new client to the list.
                final MHClientInfo info = new MHClientInfo(
                                "Client " + MHServerModule.nextClientID,
                                server.getAvailableColors()[MHServerModule.nextClientID
                                                % server.getAvailableColors().length],
                                MHServerModule.nextClientID, s, outStream);
                server.getClientList().add(info);
                
                // Tell the client what his/her server-assigned ID
                // number is.
                server.assignClientID(info);

                // Increment the ID number for the next client.
                MHServerModule.nextClientID++;

                // Update all the clients so they know who's connected
                // now.
                server.broadcastClientList();
                server.broadcastAvailableColors();

                // Create a new thread for this connection, and then
                // forget about it
                new ServerThread(server, s);
            }
        }
        catch (final IOException ioe)
        {
            ioe.printStackTrace();
        }
        finally
        {
            server.listening = false;
        }
    }
}

