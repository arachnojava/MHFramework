package mhframework.io;

import java.io.File;
import java.io.FileFilter;

public class MHFileFilter implements FileFilter
{
    private final String extension;

    public MHFileFilter(final String extension)
    {
        this.extension = extension;
    }
    
    public static File[] listFiles(String directory, String extension)
    {
        MHFileFilter filter = new MHFileFilter(extension);
        File f = new File(directory);
        return f.listFiles(filter);
    }

    @Override
    public boolean accept(final File file)
    {
        if (file.getName().toLowerCase().endsWith(extension.toLowerCase()))
            return true;

        return false;
    }
}