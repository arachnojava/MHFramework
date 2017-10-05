package mhframework.io.net.server;

import java.net.Socket;
import java.util.Iterator;
import java.util.Vector;
import mhframework.io.net.MHSerializableClientInfo;
import mhframework.io.net.MHSerializableClientList;


/********************************************************************
 * This is the list that the server maintains so it can track who is
 * connected and communicate with everybody.  Through a series of
 * overloaded methods, it can look up and retrieve client information
 * based on any of the client's attributes.
 * 
 * @author Michael Henson
 *
 */
public class MHClientList implements Iterable<MHClientInfo>
{
    private Vector<MHClientInfo> clients;


    private Vector<MHClientInfo> getClientVector()
    {
        if (clients == null)
            clients = new Vector<MHClientInfo>();

        return clients;
    }


    public void add(final MHClientInfo info)
    {
        getClientVector().add(info);
    }


    public MHClientInfo get(final int clientID)
    {
        for (final MHClientInfo clientInfo : getClientVector())
        {
            if (clientInfo.id == clientID)
                return clientInfo;
        }

        return null;
    }


    public MHClientInfo get(final Socket socket)
    {
        for (final MHClientInfo clientInfo : getClientVector())
        {
            if (clientInfo.socket == socket)
                return clientInfo;
        }

        return null;
    }


    public MHClientInfo get(final String name)
    {
        for (final MHClientInfo clientInfo : getClientVector())
        {
            if (clientInfo.name == name)
                return clientInfo;
        }

        return null;
    }


    public MHSerializableClientList getSerializedVersion()
    {
        final MHSerializableClientList output = new MHSerializableClientList();
        MHSerializableClientInfo ci;

        for (final MHClientInfo info : getClientVector())
        {
            ci = new MHSerializableClientInfo(info.id, info.name, info.color);
            output.add(ci);
        }

        return output;
    }

    @Override
    public Iterator<MHClientInfo> iterator()
    {
        return getClientVector().iterator();
    }


    public void remove(final Socket s)
    {
        getClientVector().remove(get(s));
    }


    public void remove(final int id)
    {
        getClientVector().remove(get(id));
    }

    public void remove(final String name)
    {
        getClientVector().remove(get(name));
    }


    public int size()
    {
        return getClientVector().size();
    }
}
