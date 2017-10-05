package mhframework.io.net.event;

import java.util.EventListener;
import mhframework.io.net.MHNetworkMessage;

public interface MHSystemMessageListener extends EventListener
{
    public void systemMessageReceived(MHNetworkMessage message);
}
