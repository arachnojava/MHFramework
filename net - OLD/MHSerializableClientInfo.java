package mhframework.net;

import java.awt.Color;
import java.io.Serializable;

/********************************************************************
 * A simple class for allowing clients to keep information on which
 * other clients are connected to the same server.
 * 
 * @author Michael Henson
 */
public class MHSerializableClientInfo implements Serializable
{
    private static final long serialVersionUID = 4731563505649191404L;

    public final int id;
    public final String name;
    public final Color color;

    public MHSerializableClientInfo(final int clientID, final String clientName, final Color clientColor)
    {
        id = clientID;
        name = clientName;
        color = clientColor;
    }
}
