package mhframework.tilemap;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import mhframework.media.MHImageGroup;
import mhframework.media.MHResourceManager;

/********************************************************************
 * This class manages the complete collection of all the tilesets in a game.
 * The tiles are organized in three separate groups:  floor tiles, static wall
 * tiles, and interactive tiles.
 *
 * <p><b>Note:</b>  This class stores tile images only.  These are NOT
 * the actor objects represented by the images.
 *
 * <p><b>How to create tiles for a tileset:</b>
 * <ol>
 *     <li>Create the tile image.  Follow the directions in the guidelines document.
 *     <li>Save it with a filename that follows this pattern:
 *     <ul>
 *         <li>First character:  Layer/type indicator.  Valid values are:
 *         <ul>
 *             <li>F -- Floor
 *             <li>L -- Floor detail layer
 *             <li>I -- Items
 *             <li>O -- Obstacles, furniture, etc.
 *             <li>W -- Walls and closed doors
 *             <li>D -- Wall details and open doors
 *             <li>C -- Ceilings and non-interactive overlays
 *         </ul>
 *         <li>Next two characters:  The number of the tile set; 00 - 99
 *         <li>Next three characters:  The number of the tile; 000 - 998
 *             <br><b>Important:</b> The integer 999 is reserved to indicate a
 *                                   null tile.
 *         <li>Next two characters:  The animation frame number; 00 - 99
 *         <li>A gif, png, or jpg extension.
 *     </ul>
 * </ol>
 *
 * <p>To initialize an MHTileSetManager object, simply call the
 *  <tt>loadTileSet()</tt> method with a tile set number, or use the
 * constructor that takes the tile set number parameter.
 * 
 * @author Michael Henson
 */
public class MHTileSetManager
{
    /**
     * Placeholder indicating that tile should be null.
     */
    public static final int NULL_TILE_ID = 999;

    /**
     * Maximum number of supported tile sets.
     * Valid values for tile set IDs are 00 - 99.
     */
    public static final int MAX_TILE_SETS = 100;


    public static final String TILE_SET_DIRECTORY = "images/";

    private static MHTileSet tileSet;

    /****************************************************************
     * Constructor.
     */
    public MHTileSetManager()
    {
    }


    /****************************************************************
     * Constructor.
     */
    public MHTileSetManager(final int tileSetNumber)
    {
        loadTileSet(tileSetNumber);
    }


    /****************************************************************
     * Loads the tile set specified by the given tile set number.
     *
     * @param tileSetNumber  The number of the tile set to be loaded.
     */
    public void loadTileSet(final int tileSetNumber)
    {
        if (tileSetNumber >= MAX_TILE_SETS)
        {
            System.err.println("ERROR:  Tile set number is out of range:  " + tileSetNumber);
            return;
        }

        tileSet = new MHTileSet(tileSetNumber);

        loadTiles(tileSetNumber, "F", MHMapCell.FLOOR_LAYER);
        loadTiles(tileSetNumber, "L", MHMapCell.FLOOR_DETAIL_LAYER);
        loadTiles(tileSetNumber, "I", MHMapCell.ITEM_LAYER);
        loadTiles(tileSetNumber, "O", MHMapCell.OBSTACLE_LAYER);
        loadTiles(tileSetNumber, "W", MHMapCell.WALL_LAYER);
        loadTiles(tileSetNumber, "D", MHMapCell.WALL_DETAIL_LAYER);
        loadTiles(tileSetNumber, "C", MHMapCell.CEILING_LAYER);
    }


    private void loadTiles(final int currentSet, String tilePrefix, final int layerIndex)
    {
        if (twoCharFormat(currentSet).equals("  "))
            return;

        tilePrefix += twoCharFormat(currentSet);

        // loop for individual tiles
        for (int i = 0; i < MHTileSet.MAX_TILES-1; i++)
        {
            final String filebase = tilePrefix + threeCharFormat(i);

            // loop for animation frames
            for (int frame = 0; frame < MHTileSet.MAX_FRAMES; frame++)
            {
                String filename = findNextFile(filebase + twoCharFormat(frame));

                final File tileFile = new File(TILE_SET_DIRECTORY+filename);

                // If file exists...
                if (tileFile.exists())
                {
                    // Assign it to proper layer in tileset
                    tileSet.addTile(layerIndex, i, filename);
                }
                else
                {
                    if (frame == 0)
                        return;
                    break;
                }
            }
        }
    }

    
    private String findNextFile(String filebase)
    {
        // See if the file exists as a GIF.
        String filename = filebase + ".gif";
        File tileFile = new File(TILE_SET_DIRECTORY+filename);
        if (tileFile.exists())
            return filename;
        
        // See if the file exists as a PNG.
        filename = filebase + ".png";
        tileFile = new File(TILE_SET_DIRECTORY+filename);
        if (tileFile.exists())
            return filename;

        // No GIF or PNG, so let's guess JPG and let the calling code figure it out.
        filename = filebase + ".jpg";
        return filename;
    }


    /****************************************************************
     * Returns the identifier of this tileset.
     */
    public int getCurrentTileSetID()
    {
        return tileSet.getTileSetID();
    }


    /****************************************************************
     * Returns the requested tile image.
     */
    public Image getTileImage(final int layer, final int tileNum, final int animFrame)
    {
        final MHImageGroup tileGroup = tileSet.getLayer(layer);

        if (tileGroup == null) return null;

        return tileGroup.getImage(tileNum, animFrame);
    }


    /****************************************************************
     * Returns the image group for a layer of tiles.
     */
    public MHImageGroup getTileImageGroup(final int layer)
    {
        return tileSet.getLayer(layer);
    }


    private String twoCharFormat(final int number)
    {
        final StringBuffer twoChars = new StringBuffer();

        if (number >= 0 && number <= 9)
        {
            twoChars.append("0" + number);
        }
        else twoChars.append((number % 100) + "");

        return twoChars.toString();
    }


    private String threeCharFormat(final int number)
    {
        final StringBuffer threeChars = new StringBuffer();

        if (number >= 0 && number <= 9)
        {
            threeChars.append("00" + number);
        }
        else if (number >= 10 && number < 100)
        {
             threeChars.append("0" + number);
        }
        else
        {
            threeChars.append("" + (number % 1000));
        }

        return threeChars.toString();
    }


    /****************************************************************
     * Stores tile images for one complete tile set.
     */
    static class MHTileSet
    {
        /**
         * Maximum number of tiles in a single tileset.
         * Valid values for tile IDs are 000 - 999.
         */
        public static final int MAX_TILES = 1000;

        /**
         * Maximum number of animation frames for a single tile.
         * Valid values are 00 - 99.
         */
        public static final int MAX_FRAMES = 100;

        /**
         * Number of this tile set.
         */
        private final int tileSetID;

        /**
         * Array of layers.  Constants in this class serve as the indices.
         */
        private final MHImageGroup[] layers;



        /****************************************************************
         * Constructor.
         *
         * @param setID  The character representing this tile set.
         */
        public MHTileSet(final int setID)
        {
            tileSetID = setID;
            layers = new MHImageGroup[MHMapCell.NUM_LAYERS];
        }


        /****************************************************************
         * Adds a new tile to this tile set.
         *
         * @param layerIndex    Which layer the tile belongs to.  (See
         *                       constants.)
         * @param tileNum       The number of the tile being added.
         * @param imageFileName The name of the file containing the new
         *                       tile's image.
         */
        public void addTile(final int layerIndex, final int tileNum, final String imageFileName)
        {
            if (layers[layerIndex] == null)
                layers[layerIndex] = new MHImageGroup();

            if (!layers[layerIndex].sequenceExists(tileNum))
                layers[layerIndex].addSequence(tileNum);

            Image tileImage = MHResourceManager.loadImage(TILE_SET_DIRECTORY+imageFileName);

            // Transform tiles here.
            if (layerIndex == MHMapCell.FLOOR_LAYER)
            {
                // If tile is not correct dimensions, assume it needs to be transformed.
                if (tileImage.getWidth(null) != MHIsoMouseMap.WIDTH)
                {
                    tileImage = transformFloorTile(tileImage);
                }
            }

            layers[layerIndex].addFrame(tileNum, tileImage, 0);

        }


        /****************************************************************
         *
         * @param image
         * @return
         */
        private Image transformFloorTile(final Image image)
        {
            final int w = MHIsoMouseMap.WIDTH;
            final int h = MHIsoMouseMap.HEIGHT;
            final int originalSize = image.getWidth(null);
            final int bufSize = (int) Math.sqrt(2 * Math.pow(originalSize, 2));  // Pythagorean theorem.
            final Image buffer = new BufferedImage(bufSize, bufSize, BufferedImage.TYPE_INT_ARGB_PRE);
            final Graphics2D g = (Graphics2D) buffer.getGraphics();

            // Rotate image.
            g.rotate(45 * (Math.PI / 180.0), bufSize/2, bufSize/2);
            final int offset = bufSize/2 - originalSize/2 ;
            g.drawImage(image, offset, offset, originalSize, originalSize, null);

            // Scale image.
            final Image result = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB_PRE);
            final Graphics2D rg = (Graphics2D) result.getGraphics();
            rg.drawImage(buffer, 0, 0, w, h, null);

            return result;
        }


        /****************************************************************
         * Returns the tile set ID.
         *
         * @return This tile set's ID number.
         */
        public int getTileSetID()
        {
            return tileSetID;
        }


        /****************************************************************
         * Returns the layer at the requested index.
         *
         * @param layer  The index of the requested layer.
         *
         * @return The requested layer.
         */
        public MHImageGroup getLayer(final int layer)
        {
            if (layer == MHMapCell.FLOOR_LAYER ||
                layer == MHMapCell.FLOOR_DETAIL_LAYER ||
                layer == MHMapCell.ITEM_LAYER ||
                layer == MHMapCell.OBSTACLE_LAYER ||
                layer == MHMapCell.WALL_LAYER ||
                layer == MHMapCell.WALL_DETAIL_LAYER ||
                layer == MHMapCell.CEILING_LAYER)
            {
                return layers[layer];
            }

            return null;
        }
    } // class MHTileSet



} // class MHTileSetManager
