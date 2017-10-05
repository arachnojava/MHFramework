package mhframework;

import java.util.Calendar;

/**
 * 
 * 
 * 
 * @author Michael Henson
 *
 */
public class MHLogFile extends MHTextFile
{

    public MHLogFile(final String filename)
    {
        super(filename);
    }



    @Override
    public void write(final String data)
    {
        super.write(getTimestamp()+data);
    }


    private String getTimestamp()
    {
        final Calendar c    = Calendar.getInstance();
        final String year   = c.get(Calendar.YEAR)+"";
        final String month  = twoDigits(c.get(Calendar.MONTH) + 1);
        final String day    = twoDigits(c.get(Calendar.DAY_OF_MONTH));
        final String hour   = twoDigits(c.get(Calendar.HOUR));
        final String minute = twoDigits(c.get(Calendar.MINUTE));
        final String second = twoDigits(c.get(Calendar.SECOND));

        return "[" + year + "/" + month + "/" + day + " " + hour + ":" + minute + ":" + second + "] ";
    }


    private String twoDigits(final int number)
    {
        if (number < 10)
            return "0"+number;

        return number+"";
    }
}


