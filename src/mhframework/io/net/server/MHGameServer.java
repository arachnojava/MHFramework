package mhframework.io.net.server;

import mhframework.io.net.MHNetworkMessage;

/********************************************************************
 * Interface implemented by the server side of a client/server
 * game.
 * 
 * @author Michael Henson
 */
public interface MHGameServer
{
    /****************************************************************
     * Abstract method intended to process game-specific messages
     * delegated here by the server module.
     * @param message The message to be processed.
     * @param mhAbstractServer  The server module handling communication with
     *                clients.
     */
    public void receiveMessage(MHNetworkMessage message, MHAbstractServer mhAbstractServer);
}
