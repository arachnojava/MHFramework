package mhframework.net.server;

import mhframework.net.MHNetworkMessage;

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
     *
     * @param message The message to be processed.
     * @param server  The server module handling communication with
     *                clients.
     */
    public void receiveMessage(MHClientInfo sender, MHNetworkMessage message, MHServerModule server);
}
