package mhframework.ai.state;

/********************************************************************
 * Interface to be implemented in state-based AI agents.  This system
 * is based on the one presented in "Programming Game AI by Example"
 * by Mat Buckland. (Wordware Publishing, 2005)
 * 
 * @author Michael Henson
 */
public interface MHState
{
    public void enter(Object subject);
    public void execute(Object subject);
    public void exit(Object subject);
}
