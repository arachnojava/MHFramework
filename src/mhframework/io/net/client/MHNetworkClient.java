package mhframework.io.net.client;

import java.awt.Color;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.net.ConnectException;
import java.net.Socket;
import mhframework.io.MHLogFile;
import mhframework.io.MHTextFile;
import mhframework.io.net.MHMessageType;
import mhframework.io.net.MHNetworkMessage;
import mhframework.io.net.MHSerializableClientList;
import mhframework.io.net.server.MHServerModule;

/********************************************************************
 * This class can be instantiated by any client application that
 * needs to communicate with a multiplayer server.  It simply needs
 * the IP address, and optionally the port number, of the server.
 * 
 * @author Michael Henson
 */
public class MHNetworkClient extends MHAbstractClient implements Runnable
{

    public MHTextFile logFile = new MHLogFile("MHNetworkClientLog.txt");
    
    private Socket socket;
    private ObjectOutputStream outStream;
    private ObjectInputStream inStream;
    
    private String ip;
    private int port;


    private int maxConnections;
    /****************************************************************
     * Connects to the server specified by <tt>hostIP</tt> using
     * the default port (5000), and then begins a background thread
     * to receive messages.
     *
     * @param hostIP A string containing the IP address of the server
     */
    private MHNetworkClient(final String hostIP)
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
    private MHNetworkClient(final String hostIP, final int port)
    {
        this.ip = hostIP;
        this.port = port;
    }
    
  
    @Override
    public void connect()
    {

        try
        {
            socket = new Socket(ip, port);

            inStream = new ObjectInputStream(socket.getInputStream());
            outStream = new ObjectOutputStream(socket.getOutputStream());

            setStatus(STATUS_CONNECTED);
            statusMessage = "Connected to IP address "+ip;

            new Thread(this).start();
        }
        catch (ConnectException ce)
        {
            System.out.println(ce);
            setStatus(STATUS_DISCONNECTED);
            statusMessage = ce.getMessage();
            setErrorState(true);
        }
        catch (final IOException ie)
        {
            System.out.println(ie);
            setStatus(STATUS_DISCONNECTED);
            statusMessage = ie.getMessage();
            setErrorState(true);
        }
    }

    
    public void run()
    {
        while (true)
        {
        try
        {
                final MHNetworkMessage msg = (MHNetworkMessage) inStream.readObject();
                
                logFile.append(msg.getMessageType() + " recieved by Client " + getClientID());

                process(msg);
            }
        catch (final StreamCorruptedException sce)
        {
            System.out.println("StreamCorruptedException");
            System.out.println(this.getClass().getName() + ".run():  "+sce);
            statusMessage = sce.getMessage();
            setStatus(STATUS_DISCONNECTED);
        }
        catch (final EOFException eofe)
        {
            // Do nothing?  This is normal when there's not a message waiting.
        }
        catch (OptionalDataException ode)
        {
            System.out.println("OptionalDataException");
            System.out.println("\tEOF: " + ode.eof);
            System.out.println("\tLength: " + ode.length);
            statusMessage = ode.getMessage();
            setStatus(STATUS_DISCONNECTED);
        }
        catch (final IOException ioe)
        {
            System.out.println("IOException");
            System.out.println(this.getClass().getName() + ".run():  "+ioe);
            statusMessage = ioe.getMessage();
            setStatus(STATUS_DISCONNECTED);
        }
        catch (final ClassNotFoundException cnfe)
        {
            System.out.println("ClassNotFoundException");
            System.out.println(this.getClass().getName() + ".run():  "+cnfe);
            statusMessage = cnfe.getMessage();
            cnfe.printStackTrace();
        }
        catch (final ClassCastException cce)
        {
            System.out.println("ClassCastException");
            System.out.println(this.getClass().getName() + ".run():  "+cce);
            statusMessage = cce.getMessage();
            cce.printStackTrace();
        }
        }
    }


    public synchronized void sendMessage(final MHNetworkMessage message)
    {
        if (message == null) return;

        try
        {
            logFile.append("MHNetworkClient.sendMessage(" + message.getMessageType() + ") from Client " + getClientID());
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

    
    public void disconnect()
    {
        try
        {
            final MHNetworkMessage disco = new MHNetworkMessage(MHMessageType.DISCONNECT, null);

            sendMessage(disco);
            inStream = null;
            outStream = null;
            socket.close();
            socket = null;
            super.disconnect();
        }
        catch (final Exception e) {}
    }


    public int getMaxConnections()
    {
        return maxConnections;
    }


    public static MHAbstractClient create(String ip)
    {
        return new MHNetworkClient(ip);
    }
}
