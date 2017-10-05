package mhframework.io.net.server;

import java.awt.Color;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import mhframework.MHRandom;
import mhframework.io.net.MHMessageType;
import mhframework.io.net.MHNetworkMessage;
import mhframework.io.net.client.MHLocalClient;

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
public class MHServerModule extends MHAbstractServer
{
    private static MHServerModule INSTANCE = null;
    
    public static final int DEFAULT_PORT = 5000;
    public static final int RANDOM_PORT = -1;
    private static final int NO_CONNECTION_LIMIT = Integer.MAX_VALUE;

    /** The ServerSocket we'll use for accepting new connections */
    static ServerSocket serverSocket = null;

    private static int port = DEFAULT_PORT;

    private int connectionLimit = NO_CONNECTION_LIMIT;

    static boolean listening = false;

    private static Thread listenerThread = null;

    /****************************************************************
     * Creates a server module that listens for connections on the
     * specified port.
     *
     * @throws IOException
     */
    private MHServerModule(final int port) throws IOException
    {
        if (port == RANDOM_PORT)
            MHServerModule.port = randomPort();
        else
            MHServerModule.port = port;
    }
    
    
    public static MHServerModule getInstance()
    {
        if (INSTANCE == null)
        {
            try
            {
                INSTANCE = new MHServerModule(DEFAULT_PORT);
                listen(MHServerModule.port);
            }
            catch (IOException ioe)
            {
                System.err.println("MHServerModule.getInstance(): " + ioe.getMessage());
                System.err.println(ioe.getMessage());
            }
        }
        
        return INSTANCE;
    }
    
    
    public static MHServerModule getInstance(int port)
    {
        if (INSTANCE == null)
        {
            try
            {
                INSTANCE = new MHServerModule(port);
                listen(MHServerModule.port);
            }
            catch (IOException ioe)
            {
                System.err.println("MHServerModule.getInstance(int): " + ioe.getMessage());
                System.err.println();
            }
        }
        
        return INSTANCE;
    }
    
    
    private static int randomPort()
    {
        return MHRandom.random(DEFAULT_PORT, 9999);
    }

    
    public void connectLocalClient(MHLocalClient client)
    {
        String name = "Client " + MHAbstractServer.nextClientID;
        Color color = getAvailableColors()[MHAbstractServer.nextClientID % getAvailableColors().length];
        int id = MHAbstractServer.nextClientID;
        MHClientInfo info = new MHLocalClientInfo(name, color, id, client);
        addClient(info);
    }


    public synchronized void process(MHNetworkMessage message)
    {
        String messageType = message.getMessageType();
        final MHClientInfo sender = getClientList().get(message.getSender());

        System.out.println("MHServerModule.process(" + messageType + ")");


        if (messageType.equals(MHMessageType.CHAT))
        {
            message.setPayload(sender.name+":  "+message.getPayload().toString());
            sendToAll(message);
        }
        else if (messageType.equals(MHMessageType.DISCONNECT))
        {
            removeConnection(sender.socket);
            broadcastClientList();
        }
        else if (messageType.equals(MHMessageType.REGISTER_NAME))
        {
            setClientName(message);
            broadcastClientList();
        }
        else if (messageType.equals(MHMessageType.REGISTER_COLOR))
        {
            setClientColor(message);
            broadcastClientList();
            broadcastAvailableColors();
        }
        else if (messageType.equals(MHMessageType.SET_CONNECTION_LIMIT))
        {
            setConnectionLimit(((Integer)message.getPayload()).intValue());
            sendToAll(message);
        }

        forwardMessage(message);
    }


    public void send(final int recipientClientID, final MHNetworkMessage message)
    {
        synchronized (clients)
        {
            final MHClientInfo client = clients.get(recipientClientID);

            if (client == null)
                System.err.println("ERROR:  Client " + recipientClientID + " not found.");
            else
                client.send(message);
        }
    }

    @Override
    public void sendToAll(final MHNetworkMessage message)
    {
        // We synchronize on this because another thread might be
        // calling removeConnection() and this would screw us up
        // as we tried to walk through the list
        synchronized (clients)
        {
            // For each client ...
            for (final MHClientInfo ci : clients)
                ci.send(message);
        }
    }


    /* (non-Javadoc)
     * @see mhframework.io.net.server.MHServerModuleInterface#reset()
     */
    public void reset()
    {
        shutdown();
        getInstance(getPort());
    }


    private static void listen(final int port)
    {
        if (listenerThread == null)
        { 
            // Create the ServerSocket
            try
            {
                if (serverSocket == null)
                {
                    // FIXME: Why the #*%&@! are we listening in single player mode?!?
                    boolean bound = false;
                    do
                    {
                        try
                        {
                            serverSocket = new ServerSocket(getPort());
                            bound = true;
                        }
                        catch (BindException be)
                        {
                            System.err.println("ERROR: Bind Exception");
                            MHServerModule.port = randomPort();
                            bound = false;
                        }
                    } while (!bound);
                }
                
                listenerThread = new ListenerThread(getInstance());

                // Wait for server to start before continuing.
                while (!isListening()) 
                    System.out.println("Waiting for connection thread...");
            } 
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }


    /* (non-Javadoc)
     * @see mhframework.io.net.server.MHServerModuleInterface#isListening()
     */
    public static boolean isListening()
    {
        return listening;
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
     * @see mhframework.io.net.server.MHServerModuleInterface#getPort()
     */
    public static int getPort()
    {
        return port;
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


    /* (non-Javadoc)
     * @see mhframework.io.net.server.MHServerModuleInterface#shutdown()
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
     * @see mhframework.io.net.server.MHServerModuleInterface#getMaxConnections()
     */
    public int getMaxConnections()
    {
        return connectionLimit;
    }


    /* (non-Javadoc)
     * @see mhframework.io.net.server.MHServerModuleInterface#setConnectionLimit(int)
     */
    public void setConnectionLimit(final int connectionLimit)
    {
        this.connectionLimit = connectionLimit;
    }


    @Override
    public boolean setClientColor(MHNetworkMessage message)
    {
        boolean success = super.setClientColor(message);

        if (!success)
        {
            // Send error message to client.
            final MHNetworkMessage msg = new MHNetworkMessage(MHMessageType.REGISTER_COLOR_ERROR, "Color already in use.  Choose another.");
            send(message.getSender(), msg);
        }

        return success;
    }


    public void addClient(MHClientInfo info)
    {
        // Add the new client to the list of clients.
        getClientList().add(info);

        // Tell the client what his/her server-assigned ID
        // number is.
        assignClientID(info);

        // Increment the ID number for the next client.
        MHAbstractServer.nextClientID++;

        // Update all the clients so they know who's connected
        // now.
        broadcastClientList();
        broadcastAvailableColors();

        // If this is a network-connected client, create a new thread
        // to handle communication with this connection.
        if (info.socket != null)
            new ServerThread(this, info.socket);

    }
}


/********************************************************************
 * Thread that handles a client connect. It simply takes messages
 * from the connection and sends them to the server's <tt>process()</tt>
 * method.
 */
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
        try
        {
            ObjectInputStream din = new ObjectInputStream(
                    socket.getInputStream());
            // Over and over, forever ...
            while (true)
            {
                MHNetworkMessage message = (MHNetworkMessage) din.readObject();
                if (message != null)
                    server.process(message);
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


/********************************************************************
 * Thread that listens for network connections and adds them to the
 * server's list of connected clients.
 */
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
            MHServerModule.listening = true;

            System.out.println("Starting server at address "
                    + MHServerModule.getIPAddress());
            System.out.println("Listening on " + MHServerModule.serverSocket);
            
            while (true)
            {
                // Grab the next incoming connection
                final Socket s = MHServerModule.serverSocket.accept();

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
                synchronized (clientList)
                {
                    String name = "Client " + MHAbstractServer.nextClientID;
                    Color color = server.getAvailableColors()[MHAbstractServer.nextClientID % server.getAvailableColors().length];
                    int id = MHAbstractServer.nextClientID;
                    MHClientInfo info = new MHClientInfo(name, color, id, s, outStream);
                    server.addClient(info);
                }
            }
        }
        catch (final IOException ioe)
        {
            ioe.printStackTrace();
        }
        finally
        {
            //server.listening = false;
        }
    }
}

