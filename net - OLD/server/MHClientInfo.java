package mhframework.net.server;

import java.awt.Color;
import java.io.ObjectOutputStream;
import java.net.Socket;

/********************************************************************
 * This class provides the server's view of a connected client.
 * 
 * @author Michael Henson
 *
 */
public class MHClientInfo
{
    // Read-only properties
    public final int id;
    public final Socket socket;
    public final ObjectOutputStream outputStream;

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
}
