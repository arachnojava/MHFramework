package mhframework.io.net.event;

import java.util.EventListener;
import mhframework.io.net.MHNetworkMessage;

public interface MHChatMessageListener extends EventListener
{
    public void chatMessageReceived(MHNetworkMessage message);
}
