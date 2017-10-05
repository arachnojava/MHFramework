package mhframework.io.net.client;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import mhframework.io.net.MHNetworkMessage;
import mhframework.io.net.event.MHChatMessageListener;
import mhframework.io.net.event.MHGameMessageListener;
import mhframework.io.net.event.MHSystemMessageListener;

public class MHObservableClient implements Observer
{
    private MHAbstractClient client;
    private ArrayList<MHChatMessageListener> chatListeners;
    private ArrayList<MHGameMessageListener> gameListeners;
    private ArrayList<MHSystemMessageListener> systemListeners;
    
    /****************************************************************
     * Constructor.
     * 
     * @param client
     */
    public MHObservableClient(MHAbstractClient client)
    {
        this.client = client;
        client.addObserver(this);
    }
    
    
    /****************************************************************
     * 
     * @param listener
     */
    public void addChatListener(MHChatMessageListener listener)
    {
        getChatListeners().add(listener);
    }
    
    
    /****************************************************************
     * 
     * @param listener
     */
    public void addGameListener(MHGameMessageListener listener)
    {
        getGameListeners().add(listener);
    }
    
    
    /****************************************************************
     * 
     * @param listener
     */
    public void addSystemListener(MHSystemMessageListener listener)
    {
        getSystemListeners().add(listener);
    }
    
    
    /****************************************************************
     * 
     * @param listener
     */
    public void removeChatListener(MHChatMessageListener listener)
    {
        getChatListeners().remove(listener);
    }
    
    
    /****************************************************************
     * 
     * @param listener
     */
    public void removeGameListener(MHGameMessageListener listener)
    {
        getGameListeners().remove(listener);
    }
    
    
    /****************************************************************
     * 
     * @param listener
     */
    public void removeSystemListener(MHSystemMessageListener listener)
    {
        getSystemListeners().remove(listener);
    }
    
    
    /****************************************************************
     */
    private ArrayList<MHChatMessageListener> getChatListeners()
    {
        if (chatListeners == null)
            chatListeners = new ArrayList<MHChatMessageListener>();
        
        return chatListeners;
    }

    
    /****************************************************************
     */
    private ArrayList<MHGameMessageListener> getGameListeners()
    {
        if (gameListeners == null)
            gameListeners = new ArrayList<MHGameMessageListener>();
        
        return gameListeners;
    }

    
    /****************************************************************
     */
    private ArrayList<MHSystemMessageListener> getSystemListeners()
    {
        if (systemListeners == null)
            systemListeners = new ArrayList<MHSystemMessageListener>();
        
        return systemListeners;
    }

    
    /****************************************************************
     * Returns the client wrapped in this class.
     * 
     * @return
     */
    public MHAbstractClient getClient()
    {
        return client;
    }


    @Override
    public void update(Observable client, Object message)
    {
        MHNetworkMessage msg = (MHNetworkMessage) message;
        if (msg.getMessageType().startsWith("CHAT"))
            notifyChatObservers(msg);
        else if (msg.getMessageType().startsWith("SYSTEM"))
            notifySystemObservers(msg);
        else 
            notifyGameObservers(msg);
        
//        MHAbstractClient c = (MHAbstractClient) client;
//        c.getMessage(); // Remove the message because it's been handled now.
    }


    private void notifyGameObservers(MHNetworkMessage msg)
    {
        for (MHGameMessageListener listener : getGameListeners())
            listener.gameMessageReceived(msg);
    }


    private void notifySystemObservers(MHNetworkMessage msg)
    {
        for (MHSystemMessageListener listener : getSystemListeners())
            listener.systemMessageReceived(msg);
    }


    private void notifyChatObservers(MHNetworkMessage msg)
    {
        for (MHChatMessageListener listener : getChatListeners())
            listener.chatMessageReceived(msg);
    }
}
