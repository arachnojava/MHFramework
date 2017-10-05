package mhframework.net;

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
    public static final String CHAT                  = "SEND CHAT TO ALL";
    public static final String DISCONNECT            = "DISCONNECT FROM SERVER";
    public static final String ASSIGN_CLIENT_ID      = "ASSIGN ID TO CLIENT";
    public static final String REGISTER_NAME         = "REGISTER PLAYER NAME";
    public static final String REGISTER_COLOR        = "REGISTER PLAYER COLOR";
    public static final String BROADCAST_CLIENT_LIST = "BROADCAST CLIENT LIST";
    public static final String SET_CONNECTION_LIMIT  = "SET CONNECTION LIMIT";
    public static final String AVAILABLE_COLORS      = "BRAOADCAST AVAILABLE COLORS";

    // Error codes
    public static final String REGISTER_NAME_ERROR   = "REGISTER PLAYER NAME ERROR";
    public static final String REGISTER_COLOR_ERROR  = "REGISTER PLAYER COLOR ERROR";
}
