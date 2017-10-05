package mhframework.io.net;

import java.io.Serializable;
import mhframework.io.net.server.MHClientInfo;

/********************************************************************
 * Provides a standard structure for network communication for a
 * client/server app powered by MHFramework.
 * 
 * @author Michael Henson
 */
public class MHNetworkMessage implements Serializable
{
    private static final long serialVersionUID = 8471829415162918745L;
    
    private int sender;
    private String messageType;
    private Serializable payload;

    /****************************************************************
     * Constructor.  Creates a default network message of type
     * CHAT with a payload of "No payload specified."
     */
    public MHNetworkMessage()
    {
        this(MHMessageType.CHAT, "No payload specified.");
    }


    /****************************************************************
     * Constructor.  Creates a network message of the type specified
     * by msgType with a payload specified by payloadObject.
     *
     * @param msgType       A string indicating what type of message
     *                      this is.  The default engine messages
     *                      defined in MHMessageType will be handled
     *                      by the MHServerModule class.  Other
     *                      message types will be delegated to the
     *                      game-specific class implementing the
     *                      MHGameServer interface.
     * @param payloadObject The object carrying the execution details
     *                      for the message.  For a chat, this is
     *                      the message string; for a player name
     *                      registration, this is the player's name;
     *                      for a client list broadcast, this is the
     *                      the client list object; and so on.
     */
    public MHNetworkMessage(final String msgType, final Serializable payloadObject)
    {
        this(msgType, payloadObject, 0);
    }

    
    /****************************************************************
     * Constructor.  Creates a network message of the type specified
     * by msgType with a payload specified by payloadObject.
     *
     * @param msgType       A string indicating what type of message
     *                      this is.  The default engine messages
     *                      defined in MHMessageType will be handled
     *                      by the MHServerModule class.  Other
     *                      message types will be delegated to the
     *                      game-specific class implementing the
     *                      MHGameServer interface.
     * @param payloadObject The object carrying the execution details
     *                      for the message.  For a chat, this is
     *                      the message string; for a player name
     *                      registration, this is the player's name;
     *                      for a client list broadcast, this is the
     *                      the client list object; and so on.
     * @param sender        The client who sent the message.
     */
    public MHNetworkMessage(final String msgType, final Serializable payloadObject, final int sender)
    {
        messageType = msgType;
        payload = payloadObject;
        this.sender = sender;
    }
    
    

    public String getMessageType()
    {
        return messageType;
    }


    public void setMessageType(final String messageType)
    {
        this.messageType = messageType;
    }


    public Serializable getPayload()
    {
        return payload;
    }


    public void setPayload(final Serializable payload)
    {
        this.payload = payload;
    }


    public int getSender()
    {
        return sender;
    }


    public void setSender(int sender)
    {
        this.sender = sender;
    }
}
