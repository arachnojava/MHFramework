package mhframework.io.net;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Vector;

/********************************************************************
 * This class represents the list of connected clients that is
 * passed from the server to the clients during a
 * <code>MHMessageType.BROADCAST_CLIENT_LIST</code> operation.
 * 
 * @author Michael Henson
 */
public class MHSerializableClientList implements Iterable<MHSerializableClientInfo>, Serializable
{
    private static final long serialVersionUID = -8228268305901052973L;

    /* Vectors are both serializable and thread-safe. */
    private final Vector<MHSerializableClientInfo> clients;

    public MHSerializableClientList()
    {
        clients = new Vector<MHSerializableClientInfo>();
    }

    /****************************************************************
     * Add a client information record to this collection.
     *
     * @param info The client information record to add.
     */
    public void add(final MHSerializableClientInfo info)
    {
        clients.add(info);
    }

    /****************************************************************
     * Retrieve client information by ID number lookup.
     *
     * @param clientID The ID number of the client to retrieve.
     *
     * @return The requested client record.
     */
    public MHSerializableClientInfo get(final int clientID)
    {
        for (final MHSerializableClientInfo clientInfo : clients)
        {
            if (clientInfo.id == clientID)
                return clientInfo;
        }

        return null;
    }


    /****************************************************************
     * Retrieve client information by name lookup.
     *
     * @param clientID The name of the client to retrieve.
     *
     * @return The requested client record.
     */
    public MHSerializableClientInfo get(final String name)
    {
        for (final MHSerializableClientInfo clientInfo : clients)
        {
            if (clientInfo.name == name)
                return clientInfo;
        }

        return null;
    }


    /****************************************************************
     * Allows the use of enhanced <tt>for</tt> loops for iterating
     * through this collection.
     */
    @Override
    public Iterator<MHSerializableClientInfo> iterator()
    {
        return clients.iterator();
    }


    /****************************************************************
     * Removes a client's information from this collection.
     *
     * @param id The ID number of the client to remove.  This number
     * can be obtained on the server by calling the <tt>get()</tt>
     * methods in MHClientList.  It could be argued that the client
     * should never use this functionality.
     */
    public void remove(final int id)
    {
        clients.remove(get(id));
    }

    public int size()
    {
        return clients.size();
    }
}
