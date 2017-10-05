package mhframework.tilemap;

import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Hashtable;
import mhframework.MHActor;
import mhframework.MHDataModel;


/********************************************************************
 * <p>Maintains data for a grid-based game map.  This includes
 *    regular rectangular tile maps, isometric maps, and hexagonal
 *    maps.
 * </p>
 *
 * <p>Note that this class requires an existing object which
 * implements the MHObjectVendor interface.  The object vendor class
 * is responsible for instantiating the dynamic actor objects in a
 * map.
 * </p>
 *
 * <p>Also note that this class only maintains the data.  The actual
 *    presentation of the map (scrolling, plotting, mouse mapping,
 *    etc.) is handled by subclasses of MHTileMap.  If you are using
 *    a subclass of MHTileMap to handle your map presentation, then
 *    you do not need to explicitly instantiate an MHMap object
 *    because the MHTileMap base class takes care of that for you
 *    when you provide a map file name to the constructor.
 * </p>
 *
 * <p>The map data will be stored in five separate files:
 * <ol>
 *     <li><tt>M0000000.lime</tt> -- This file will contain only the
 *         following five lines in the order given:
 *         <ul>
 *             <li>A tile set ID number specifying which tile set
 *                 will be used to render the map.
 *             <li>The name of the file containing the floor layer
 *                 data.
 *             <li>The name of the file containing the floor detail
 *                 data.
 *             <li>The name of the file containing the item layer
 *                 data.
 *             <li>The name of the file containing the obstacle
 *                 data.
 *             <li>The name of the file containing the wall layer
 *                 data.
 *             <li>The name of the file containing the wall detail
 *                 data.
 *             <li>The name of the file containing the ceiling layer
 *                 data.
 *        </ul>
 *    <li><tt>F0000000.layer</tt> -- The file containing floor layer
 *                                 data.
 *    <li><tt>L0000000.layer</tt> -- The file containing floor detail
 *                                 data.
 *    <li><tt>I0000000.layer</tt> -- The file containing item layer
 *                                 data.
 *    <li><tt>O0000000.layer</tt> -- The file containing obstacle layer
 *                                 data.
 *    <li><tt>W0000000.layer</tt> -- The file containing static wall
 *                                 layer data.
 *    <li><tt>D0000000.layer</tt> -- The file containing wall detail
 *                                 layer data.
 *    <li><tt>C0000000.layer</tt> -- The file containing ceiling layer
 *                                 data.
 * </ol>
 *
 * <p><b>Note:</b>  The file names given here are merely suggestions,
 * and the code in this class does nothing to enforce any sort of
 * naming conventions.  It simply opens and reads whatever file names
 * are supplied.
 * 
 * @author Michael Henson
 */
public class MHMap
{
    /** Array for storing the map data. */
    private MHMapCell mapGrid[][];

    /** Info about the map file to be loaded into this map object. */
    private static MHMapFileInfo info;

    /** Object vendor for instantiating specialized objects in the
     * game universe. */
    private final MHObjectFactory objectVendor;


   /****************************************************************
    * Constructor that creates a map with the given number of rows
    * and columns.  If this constructor is used, it must be followed
    * by a call to <tt>loadMapFile()</tt> to populate the map
    * structure.
    *
    * @param dataModel  A reference to the application's data model.
    * @param mapHeight  The height of the game map in number of
    *                    cells.
    * @param mapWidth   The width of the game map in number of cells.
    */
    public MHMap(final int mapHeight, final int mapWidth, final MHObjectFactory vendor)
    {
        objectVendor = vendor;
        mapGrid = new MHMapCell[mapHeight][mapWidth];
    }


   /****************************************************************
    * Constructor that creates a map using the given map file name.
    * The number of rows and columns is computed from the map data
    * which is loaded into the map structure by this method.
    *
    * @param filename The name of the map file to be loaded.
    */
    public MHMap(final String filename, final MHObjectFactory vendor)
    {
        objectVendor = vendor;
        loadMapFile(filename);
    }


    /****************************************************************
     * Loads all the data files listed in the given file name.
     * This method assumes that the image groups (MHImageGroup
     * objects) for normal, static tiles already exist in a tile set
     * manager (MHTileSetManager), and that specialized objects have
     * taken the responsibility for acquiring their own image groups.
     *
     * @param filename The name of the map file to be loaded.
     */
    public void loadMapFile(final String filename)
    {
        info = retrieveMapFileInfo(filename);

        mapGrid = new MHMapCell[info.height][info.width];

        MHDataModel.getTileSetManager().loadTileSet(info.tileSetId);

        for (int layer = 0; layer < MHMapCell.NUM_LAYERS; layer++)
        {
            int row = 0, col = 0;

            // Select the data file for the current layer
            final String layerFile = chooseLayerFile(layer);

            try
            {
                final RandomAccessFile file = new RandomAccessFile(
                                layerFile, "r");

                for (row = 0; row < info.height; row++)
                {
                    // Read line of data from file
                    final String line = file.readLine();

                    // The data files should be tab-delimited.
                    final String[] dataRow = line.trim().split("\t");

                    // dataRow.length and info.width should be equal
                    // only if every cell is accounted for in the
                    // data file
                    // NOTE: This will fail if any layer is larger
                    // than the floor layer!
                    // Possible solution: Examine ALL the data files
                    // when getting the map's metrics.
                    for (col = 0; col < dataRow.length; col++)
                    {
                        int tileID = MHTileSetManager.NULL_TILE_ID;
                        MHMapCellAddress currentCell = new MHMapCellAddress();
                        currentCell.row = row;
                        currentCell.column = col;

                        // Convert the input tile ID into an integer
                        try
                        {
                            tileID = Integer.parseInt(dataRow[col]);
                        }
                        catch (final NumberFormatException nfe)
                        {
                            tileID = MHTileSetManager.NULL_TILE_ID;
                        }

                        // If there is no map cell at our current
                        // position in the map, make one.
                        if (mapGrid[row][col] == null)
                            mapGrid[row][col] = new MHMapCell();

                        // Create actor object for current tile
                        MHActor tile = null;

                        // 999 indicates a null tile
                        if (tileID < MHTileSetManager.MHTileSet.MAX_TILES
                                        && tileID != MHTileSetManager.NULL_TILE_ID)
                        {
                            // Instantiate special objects based on
                            // the layer and tile ID.
                            tile = objectVendor.getObject(layer, tileID, currentCell);

                            // If the object vendor returned null,
                            // we have to make the tile object
                            // ourselves.
                            if (tile == null)
                            {
                                tile = new MHActor();
                                tile.setImageGroup(MHDataModel.getTileSetManager().getTileImageGroup(layer));
                                // For a tile actor, the animation
                                // sequence is the tile ID since
                                // that's what determines
                                // which image is displayed.
                                tile.setAnimationSequence(tileID);
                            }
                        }

                        // assign tile object to map cell field
                        mapGrid[row][col].setLayer(layer, tile);

                    } // for (col...
                } // for (row...
            } // try
            catch (final EOFException eofe)
            {
            }
            catch (final IOException ioe)
            {
            }
        } // for (layer...)
    } // loadMapFile




        /*************************************************************
         * Returns the map cell at the given coordinates
         */
        public MHMapCell getMapCell(int row, int column)
        {
            if (row >= mapGrid.length)
                row = mapGrid.length - 1;
            
            if (column >= mapGrid[row].length)
                column = mapGrid[row].length - 1;
                
            return mapGrid[row][column];
        }


        /*************************************************************
         * Returns the number of rows in the map.
         */
    public int getHeight()
    {
        return info.height;
    }


        /*************************************************************
         * Returns the number of columns in the map.
         */
    public int getWidth()
    {
        return info.width;
    }


        /*************************************************************
         * Choose the correct data file for the layer we're building.
         */
        private String chooseLayerFile(final int layer)
        {
                String layerFile = null;

                switch (layer)
                {
                        case MHMapCell.FLOOR_LAYER:
                                layerFile = info.floorFile;
                                break;
                        case MHMapCell.FLOOR_DETAIL_LAYER:
                            layerFile = info.floorDetailFile;
                            break;
                        case MHMapCell.WALL_LAYER:
                                layerFile = info.wallFile;
                                break;
                        case MHMapCell.ITEM_LAYER:
                                layerFile = info.itemFile;
                                break;
                        case MHMapCell.OBSTACLE_LAYER:
                            layerFile = info.obstacleFile;
                            break;
                        case MHMapCell.WALL_DETAIL_LAYER:
                                layerFile = info.detailFile;
                                break;
                        case MHMapCell.CEILING_LAYER:
                                layerFile = info.ceilingFile;
                                break;
                }

                return layerFile;
        }


                /**
                 * Returns the ceilingFile.
                 * @return String
                 */
                public String getCeilingFile()
                {
                        return info.ceilingFile;
                }

                /**
                 * Returns the fileName.
                 * @return String
                 */
                public String getMapFileName()
                {
                        return info.fileName;
                }

                /**
                 * Returns the floorFile.
                 * @return String
                 */
                public String getFloorFile()
                {
                        return info.floorFile;
                }

                public String getFloorDetailFile()
                {
                        return info.floorDetailFile;
                }

                /**
                 * Returns the itemFile.
                 * @return String
                 */
                public String getItemFile()
                {
                        return info.itemFile;
                }

                
                public String getObstacleFile()
                {
                        return info.obstacleFile;
                }

                
                /**
                 * Returns the detailFile.
                 * @return String
                 */
                public String getWallDetailFile()
                {
                        return info.detailFile;
                }

                /**
                 * Returns the tileSetId.
                 * @return int
                 */
                public int getTileSetId()
                {
                        return info.tileSetId;
                }

                /**
                 * Returns the wallFile.
                 * @return String
                 */
                public String getWallFile()
                {
                        return info.wallFile;
                }


		public static MHMapFileInfo getMapFileInfo()
		{
		    if (info == null)
		        info = new MHMapFileInfo();

			return info;
		}

        /****************************************************************
         * Reads the info from the given map file name.
         */
    private MHMapFileInfo retrieveMapFileInfo(final String filename)
    {
        RandomAccessFile file;

        info = new MHMapFileInfo();

        info.fileName = filename;

        System.out.println("MHMap.retrieveMapFileInfo():  Retrieving map info from " + info.fileName);

        try
        {
            // Open file
            file = new RandomAccessFile(filename, "r");

            // Read tile ID from file
            info.tileSetId=Integer.parseInt(file.readLine());

            // Read floorFile
            info.floorFile = file.readLine();
            
            // Read floorDetailFile
            info.floorDetailFile = file.readLine();

            // Read itemFile
            info.itemFile = file.readLine();

            // Read obstacleFile
            info.obstacleFile = file.readLine();

            // Read wallFile
            info.wallFile = file.readLine();

            // Read detailFile
            info.detailFile = file.readLine();

            // Read ceilingFile
            info.ceilingFile = file.readLine();

            file.close();
        }
        catch (final IOException ioe)
        {
            System.err.println("ERROR:  Problem retrieving map file information.");
        }

        // Summarize data.
        System.out.println("\t         Tile set:  " + info.tileSetId);
        System.out.println("\t       Floor data:  " + info.floorFile);
        System.out.println("\tFloor detail data:  " + info.floorDetailFile);
        System.out.println("\t        Item data:  " + info.itemFile);
        System.out.println("\t    Obstacle data:  " + info.obstacleFile);
        System.out.println("\t        Wall data:  " + info.wallFile);
        System.out.println("\t      Detail data:  " + info.detailFile);
        System.out.println("\t     Ceiling data:  " + info.ceilingFile);

        int mapWidth = 0,
            mapHeight = 0;

        try
        {
            // Open floorFile, count the lines,
            // and find the longest line
            file = new RandomAccessFile(info.floorFile, "r");

            while (true)
            {
                final String line = file.readLine();

                final String[] tiles = line.trim().split("\t");

                mapHeight++;

                if (tiles.length > mapWidth)
                    mapWidth = tiles.length;
            }
        }
                catch (final FileNotFoundException fnfe)
                {
                        System.err.println("ERROR:  File not found:  " + filename);
                }
        catch (final IOException ioe)
        {
            System.err.println("ERROR:  I/O exception thrown.");
        }
        catch (final Exception e)
        {
            // Not a problem -- happens when all data has been read.
        }
                finally
                {
                        file = null;
                }

        // Assign length of longest line to info.width
        info.width = mapWidth;

        // Assign number of lines to info.height
        info.height = mapHeight;

        return info;
    } // getMapFileInfo


    public boolean canWalkOn(int row, int column)
    {
        if (isValidCell(row, column))
        {
            if (mapGrid[row][column].canWalkOn())
                return true;
        }
        
        return false;
    }
    
        public boolean isValidCell(int row, int column)
        {
            return (row >= 0 && column >= 0 && 
                    row < mapGrid.length && 
                    column < mapGrid[row].length);
        }
}


