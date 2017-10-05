package mhframework;

import java.awt.Toolkit;

/********************************************************************
 * This class borrows logic and code from the book <i>Killer Game
 * Programming in Java</i> by Andrew Davison.
 * 
 * @author Michael Henson
 */
public class MHRuntimeMetrics
{
    private static final long ONE_SECOND_IN_MILLI = 1000L; 
    private static final long ONE_MILLI_IN_NANO = 1000000L; 
    private static final long ONE_SECOND_IN_NANO  = ONE_SECOND_IN_MILLI * ONE_MILLI_IN_NANO;
    
    /** The frame rate we're trying to achieve. */
    public static final short TARGET_FPS = 28; // 28 = average FPS for Japanese anime
    
    /** Average period in nanoseconds required to achieve the target frame rate. */
    public static final long PERIOD = ONE_SECOND_IN_NANO/TARGET_FPS;

    /** Maximum number of renders to skip when compensating for long loop iterations. */
    static final short MAX_FRAME_SKIPS = 2;
    
    /** Nanoseconds between metrics calculations. */
    private static final long MAX_STATS_INTERVAL = ONE_SECOND_IN_NANO;
    
    /** Number of measurements to track for calculating an average. */
    private static final int SAMPLE_SIZE = 10;

    private int frameCount = 0;        // Number of frames elapsed.
    private int statsInterval;         // Time since last metrics calculation.
    private long gameStartTime;        // Approximate time that game started.
    private long prevStatsTime = 0;    // Time that metrics were last calculated.
    private long totalElapsedTime = 0; // Total amount of time elapsed in game loop.
    private int totalFramesSkipped;    // Total number of renders skipped due to long loop iterations.
    private int framesSkipped = 0;     // Number of renders skipped since last metrics calculation.
    private int statsCount = 0;        // Index into fpsStore and upsStore arrays.
    private double averageFPS;         // Calculated average frames per second.
    private double averageUPS;         // Calculated average updates per second.
    private int[] fpsStore, upsStore;  // Arrays for storing calculation results.
    private long startTime, endTime;   // Starting and ending times for current loop iteration.
    private long excess = 0;           // Amount of extra time attained through short loop iterations.
    
    public MHRuntimeMetrics()
    {
        gameStartTime = System.nanoTime();
        prevStatsTime = gameStartTime;
        
        fpsStore = new int[SAMPLE_SIZE];
        upsStore = new int[SAMPLE_SIZE];
    }
    
       
    public void recordStartTime()
    {
        startTime = System.nanoTime();
    }

    
    public void recordEndTime()
    {
        endTime = System.nanoTime();
        storeStats();  // Update the stored metrics.
        Toolkit.getDefaultToolkit().sync(); // Sync the display (for Linux users).
    }
    
    
    public static long secToNano(double seconds)
    {
        return (long)(seconds * ONE_SECOND_IN_NANO);
    }

    
    public static int nanoToSec(long nano)
    {
        return (int)(nano / ONE_SECOND_IN_NANO);
    }
    
    
    public static  long nanoToMilli(long nano)
    {
        return nano / ONE_MILLI_IN_NANO;
    }
    
    private void storeStats()
    { 
      frameCount++;
      statsInterval += PERIOD;

      if (statsInterval >= MAX_STATS_INTERVAL) 
      {
        long timeNow = System.nanoTime();

        long realElapsedTime = timeNow - prevStatsTime;   // time since last stats collection
        totalElapsedTime += realElapsedTime;

        totalFramesSkipped += framesSkipped;
        
        int actualFPS = 0;     // calculate the latest FPS and UPS
        int actualUPS = 0;
        if (totalElapsedTime >= 1) 
        {
            int seconds = nanoToSec(totalElapsedTime);
            seconds = (seconds > 0 ? seconds : 1);
            actualFPS = (frameCount / seconds);
            actualUPS = ((frameCount + totalFramesSkipped) / seconds);
        }

        // store the latest FPS and UPS
        fpsStore[ statsCount%SAMPLE_SIZE ] = actualFPS;
        upsStore[ statsCount%SAMPLE_SIZE ] = actualUPS;
        statsCount += 1;

        double totalFPS = 0.0;     // total the stored FPSs and UPSs
        double totalUPS = 0.0;
        for (int i=0; i < SAMPLE_SIZE; i++) 
        {
          totalFPS += fpsStore[i];
          totalUPS += upsStore[i];
        }

        if (statsCount < SAMPLE_SIZE) // obtain the average FPS and UPS
        { 
          averageFPS = totalFPS/statsCount;
          averageUPS = totalUPS/statsCount;
        }
        else 
        {
          averageFPS = totalFPS/SAMPLE_SIZE;
          averageUPS = totalUPS/SAMPLE_SIZE;
        }

        framesSkipped = 0;
        prevStatsTime = timeNow;
        statsInterval = 0;   // reset
      }
    }  // end of storeStats()

    
    /****************************************************************
     * Calculate how long the application thread should sleep based 
     * on the time it took to run the game loop.
     */
    public void sleep()
    {
        long sleepTime = PERIOD - (endTime - startTime);

        if (sleepTime > 0)
        {
            try
            {
                Thread.sleep(nanoToMilli(sleepTime));
            } 
            catch (final InterruptedException e)
            {
            }
        } 
        else
        {
            excess -= sleepTime; // store excess time value
            Thread.yield(); // give another thread a chance to run
        }
    }
       
    
    public int getFramesPerSecond()
    {
        return (int) averageFPS;
    }


    public int getUpdatesPerSecond()
    {
        return (int) averageUPS;
    }

    
    public int getTimeSpentInGame()
    {
        return nanoToSec(getGameTimerValue());
    }
    
    
    public long getGameTimerValue()
    {
        return System.nanoTime() - gameStartTime;
    }


    public boolean shouldUpdate()
    {
        boolean updateNeeded = (excess > PERIOD) && (framesSkipped < MAX_FRAME_SKIPS);
        
        if (updateNeeded)
        {
            excess -= MHRuntimeMetrics.PERIOD;
            framesSkipped++;
        }
        
        return updateNeeded;
    }
}
