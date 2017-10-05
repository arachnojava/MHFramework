package mhframework;

import java.util.Hashtable;

/********************************************************************
 * 
 * @author Michael Henson
 *
 */
public class MHThreadManager
{
    private static MHThreadManager INSTANCE;
    private static Hashtable<Long, Thread> threads;

    private MHThreadManager()
    {
        threads = new Hashtable<Long, Thread>();
    }


    public static MHThreadManager getInstance()
    {
        if (INSTANCE == null)
            INSTANCE = new MHThreadManager();

        return INSTANCE;
    }


    public long createThread(final Runnable target)
    {
        return createThread(target, null);
    }


    public long createThread(final Runnable target, final String threadName)
    {
        // First, let's make sure that this thread hasn't already been created.
        for (Thread t : threads.values())
        {
            if (t.getName().equals(threadName))
                return t.getId();
        }
        
        final Thread newThread = new Thread(target);

        if (threadName != null && threadName.length() > 0)
            newThread.setName(threadName);

        threads.put(newThread.getId(), newThread);

        return newThread.getId();
    }


    public void start(final long threadID)
    {
        try
        {
            if (!isAlive(threadID))
                threads.get(threadID).start();
        }
        catch (Exception e) {e.printStackTrace();}
    }


    public boolean isAlive(final long threadID)
    {
        return threads.get(threadID).isAlive();
    }

    
    public boolean isFinished(final long threadID)
    {
        if (threads.get(threadID) == null)
            return true;
        
        return threads.get(threadID).getState() == Thread.State.TERMINATED;
    }


    public void printStatus()
    {
        for (final Thread t : threads.values())
            System.out.println(t.getName() + " (" + t.getId() + "):  " + t.getState().toString());
    }

}
