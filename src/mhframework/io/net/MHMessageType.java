package mhframework.io.net;

/********************************************************************
 * Standard network message types that MHClientModule and 
 * MHServerModule both understand and can handle by themselves 
 * without any specific game code.
 * 
 * @author Michael Henson
 *
 */
public abstract class MHMessageType
{
    public static final String CHAT                  = "CHAT";
    public static final String DISCONNECT            = "SYSTEM DISCONNECT FROM SERVER";
    public static final String ASSIGN_CLIENT_ID      = "SYSTEM ASSIGN ID TO CLIENT";
    public static final String REGISTER_NAME         = "SYSTEM REGISTER PLAYER NAME";
    public static final String REGISTER_COLOR        = "SYSTEM REGISTER PLAYER COLOR";
    public static final String BROADCAST_CLIENT_LIST = "SYSTEM BROADCAST CLIENT LIST";
    public static final String SET_CONNECTION_LIMIT  = "SYSTEM SET CONNECTION LIMIT";
    public static final String AVAILABLE_COLORS      = "SYSTEM BRAOADCAST AVAILABLE COLORS";

    // Error codes
    public static final String REGISTER_NAME_ERROR   = "SYSTEM REGISTER PLAYER NAME ERROR";
    public static final String REGISTER_COLOR_ERROR  = "SYSTEM REGISTER PLAYER COLOR ERROR";
}
