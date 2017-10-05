package mhframework;

import java.util.Random;

/********************************************************************
 * Static convenience methods for working with random numbers.
 * 
 * @author Michael Henson
 *
 */
public class MHRandom
{
    private static MHRandom instance;
    private Random rand;
    
    private MHRandom()
    {
        rand = new Random();
    }

    
    private static MHRandom getInstance()
    {
        if (instance == null)
            instance = new MHRandom();
        
        return instance;
    }
    
    
    public static int random(int min, int max)
    {
        int range = Math.abs(max - min);
        
        return min + getInstance().rand.nextInt(range+1);
    }
    
    
    public static int rollD4()
    {
        return random(1, 4);
    }
    
    
    public static int rollD6()
    {
        return random(1, 6);
    }
    

    public static int rollD8()
    {
        return random(1, 8);
    }

    
    /**
     * TODO:  Does a D10 go from 0-9 or from 1-10?
     * @return
     */
    public static int rollD10()
    {
        return random(1, 10);
    }

    
    public static int rollD12()
    {
        return random(1, 12);
    }
    
    public static int rollD20()
    {
        return random(1, 20);
    }
    
    
    public static boolean flipCoin()
    {
        return rollD4() % 2 == 0;
    }
    
    public static void main(String args[])
    {
        long total = 0;
        long min = 9999;
        long max = -9999;
        int count = 500;
        int rand = 0;
        for (int i = 0; i < count; i++)
        {
            rand = random(-100, 100);
            total += rand;
            min = Math.min(min, rand);
            max = Math.max(max, rand);
        }
        
        long average = total / count;
        System.out.println("Min value = " + min);
        System.out.println("Max value = " + max);
        System.out.println("Average   = " + average);
    }
}
