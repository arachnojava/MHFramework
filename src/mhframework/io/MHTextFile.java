package mhframework.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/********************************************************************
 * 
 * @author Michael Henson
 *
 */
public class MHTextFile
{
    private final File file;
    private RandomAccessFile randomAccessFile;


    public MHTextFile (final String filename)
    {
        file = new File(filename);
        try
        {
            randomAccessFile = new RandomAccessFile(file, "rw");
            randomAccessFile.setLength(0);
        }
        catch (final FileNotFoundException fnfe)
        {
            fnfe.printStackTrace();
        } 
        catch (final IOException ioe)
        {
            ioe.printStackTrace();
        }
    }


    public String getName()
    {
        return file.getName();
    }


    public String getAbsolutePath()
    {
        return file.getAbsolutePath();
    }


    public void close()
    {
        try
        {
            randomAccessFile.close();
        }
        catch (final IOException e)
        {
            e.printStackTrace();
        }
    }


    public void write(String data)
    {
        data = data.replaceAll("\n", System.getProperty("line.separator"));

        try
        {
            randomAccessFile.writeBytes(data + System.getProperty("line.separator"));
        }
        catch (final IOException e)
        {
            e.printStackTrace();
        }
    }


    public void append(final String data)
    {
        try
        {
            randomAccessFile.seek(randomAccessFile.length());
            write(data);
        }
        catch (final IOException e)
        {
            e.printStackTrace();
        }
    }


    public String readLine()
    {
        String line;
        try
        {
            line = randomAccessFile.readLine();
        }
        catch (final IOException e)
        {
            e.printStackTrace();
            line = e.getMessage();
        }

        return line;
    }


    @Override
    protected void finalize() throws Throwable
    {
        close();
    }


    public static void main(final String[] args)
    {
        final MHTextFile file = new MHTextFile("C:\\MHTextFileTEST.txt");
        for (int i = 0; i < 10; i++)
            file.write("Write " + i);

        for (int i = 0; i < 10; i++)
            file.append("Append " + i);
}

}
