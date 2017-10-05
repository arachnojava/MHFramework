package mhframework.io.net.event;

import java.util.EventListener;
import mhframework.io.net.MHNetworkMessage;

public interface MHGameMessageListener extends EventListener
{
    public void gameMessageReceived(MHNetworkMessage message);
}
