package mhframework;

public class MHRotationLookup
{
    public static final int SINE = 0;
    public static final int COSINE = 1;

    private static double[][] table;

    private MHRotationLookup()
    {
        // Private constructor just to keep people from
        // instantiating this class unnecessarily.
    }

    private static int validate(double angle)
    {
        angle %= 360;

        if (angle < 0) angle += 360;

        return (int)angle;
    }

    public static double sine(final double angle)
    {
        return getTable()[validate(angle)][SINE];
    }

    public static double cosine(final double angle)
    {
        return getTable()[validate(angle)][COSINE];
    }

    private static double[][] getTable()
    {
        if (table == null)
        {
            table = new double[360][2];
            for (int angle = 0; angle < 360; angle++)
            {
                final double radians = Math.toRadians(angle);
                table[angle][SINE]   = Math.sin(radians);
                table[angle][COSINE] = Math.cos(radians);

                //System.out.printf("%10.10f\t%10.10f\n",table[angle][SINE],table[angle][COSINE]);
            }
        }

        return table;
    }
}
