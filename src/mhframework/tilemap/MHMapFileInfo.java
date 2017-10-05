package mhframework.tilemap;


/********************************************************************
 * This is the information that gets written to a file by LIME.
 * 
 * @author Michael Henson
 *
 */
public class MHMapFileInfo
{
    public static final String MAP_FILE_EXTENSION = ".lime";
    public static final String LAYER_FILE_EXTENSION = ".layer";

    public int width;
    public int height;
    public int tileSetId;
    public String fileName;
    public String floorFile;
    public String floorDetailFile;
    public String itemFile;
    public String obstacleFile;
    public String wallFile;
    public String detailFile;
    public String ceilingFile;
}
