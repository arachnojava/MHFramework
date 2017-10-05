package mhframework.io.net.client;

import mhframework.io.net.MHNetworkMessage;
import mhframework.io.net.server.MHAbstractServer;
import mhframework.io.net.server.MHServerModule;

public class MHLocalClient extends MHAbstractClient
{
    private MHAbstractServer server;
    
    public MHLocalClient(MHAbstractServer server)
    {
        this.server = server;
        setStatus(MHAbstractClient.STATUS_DISCONNECTED);
        statusMessage = "Not connected";
    }

    
    public MHAbstractServer getGameServer()
    {
        return server;
    }


    @Override
    public void sendMessage(MHNetworkMessage message)
    {
        server.process(message);
    }
    
    
    public void connect()
    {
        ((MHServerModule) server).connectLocalClient(this);
        setStatus(STATUS_CONNECTED);
        statusMessage = "Connected";
    }
}
