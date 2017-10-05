package mhframework.io.net.server;

import java.awt.Color;
import mhframework.io.net.MHNetworkMessage;
import mhframework.io.net.client.MHLocalClient;

public class MHLocalClientInfo extends MHClientInfo
{
    public final MHLocalClient client;

    public MHLocalClientInfo(String clientName, Color playerColor, int idNum,
            MHLocalClient clientModule)
    {
        super(clientName, playerColor, idNum, null, null);
        client = clientModule;
    }

    
    public boolean send(MHNetworkMessage message)
    {
        client.queueMessage(message);
        return true;
    }
    
    
    private static final long serialVersionUID = -674957417157593198L;
}
