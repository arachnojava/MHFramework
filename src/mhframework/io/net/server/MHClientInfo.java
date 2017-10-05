package mhframework.io.net.server;

import java.awt.Color;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import mhframework.io.net.MHNetworkMessage;

/********************************************************************
 * This class provides the server's view of a network-connected 
 * client.
 * 
 * @author Michael Henson
 *
 */
public class MHClientInfo implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    
    // Read-only properties
    public final int id;
    public final Socket socket;
    private final ObjectOutputStream outputStream;

    // Customizable properties
    public String name;
    public Color color;

    public MHClientInfo(final String clientName, final Color playerColor,
                    final int idNum, final Socket s, final ObjectOutputStream o)
    {
        name = clientName;
        color = playerColor;
        id = idNum;
        socket = s;
        outputStream = o;
    }
    
    public boolean send(MHNetworkMessage message)
    {
        try
        {
            outputStream.writeObject(message);
            return true;
        } 
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
    }
}
