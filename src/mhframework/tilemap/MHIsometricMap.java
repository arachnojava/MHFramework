package mhframework.tilemap;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Hashtable;
import mhframework.MHActor;
import mhframework.MHDisplayModeChooser;
import mhframework.MHPoint;

/********************************************************************
 * Abstract base class for deriving isometric tile maps.
 * 
 * @author Michael Henson
 */
public abstract class MHIsometricMap extends MHTileMap
{

    protected MHIsoMouseMap mouseMap = new MHIsoMouseMap();

    /** Hash for storing the finely-placed objects -- those whose
     * positions are not constrained by the map grid. */
    //private Hashtable<Integer, ArrayList<MHActor>> fineObjects;
    
    protected boolean cursorOn = false;

    protected boolean cursorFlasher = false;


    /****************************************************************
     * Constructor.
     *
     * @param data
     *            A reference to the application's data model.
     * @param filename
     *            The name of the map file to be loaded into this tile
     *            map.
     * @param vendor
     *            A reference to an existing MHObjectVendor for
     *            instantiating interactive actor objects.
     */
    public MHIsometricMap(final String filename,
            final MHObjectFactory vendor)
    {
        super(filename, vendor);

        setCursorOn(true);
    }

    
    
//    public void placeFineObject(MHActor object)
//    {
//        // Determine z-order.
//        Point basePt = calculateBasePoint(object);
//        MHMapCellAddress a = mapMouse(basePt);
//        int z = a.row + a.column;
//        
//        // Put object into hashtable.
//        if (fineObjects == null)
//            fineObjects = new Hashtable<Integer, ArrayList<MHActor>>();
//        
//        if (!fineObjects.containsKey(z))
//            fineObjects.put(z, new ArrayList<MHActor>());
//        
//        fineObjects.get(z).add(object);
//    }
    
    
    public Point calculateBasePoint(MHActor actor)
    {
        Point p = new Point();
        p.x = (int) (actor.getX() + (actor.getWidth() / 2));
        p.y = (int) (actor.getY() + actor.getHeight() - (MHIsoMouseMap.HEIGHT/2));
        return p;
    }

    /****************************************************************
     * Calculates the next map position to which an actor would walk
     * if it were in position (<i>row</i>, <i>column</i>) and
     * traveled in the direction specified by <i>direction</i>.
     *
     * @param row
     *            The actor's current row position.
     * @param column
     *            The actor's current column position.
     * @param direction
     *            The direction in which to walk.
     *
     * @return A point indicating the actor's new column and row
     *         position after walking from its original position.
     */
    public abstract MHMapCellAddress tileWalk(int row, int column,
            MHTileMapDirection direction);


    /****************************************************************
     * Calculates the next map position to which an actor would walk
     * if it were in the position specified by <i>origin</i> and
     * travelled in the direction specified by <i>direction</i>.
     *
     * @param origin
     *            The actor's current map cell position.
     * @param direction
     *            The direction in which to walk.
     *
     * @return A point indicating the actor's new column and row
     *         position after walking from its original position.
     */
    public MHMapCellAddress tileWalk(
            final MHMapCellAddress origin, final MHTileMapDirection direction)
    {
        if (origin == null)
            return null;
        
        return tileWalk(origin.row, origin.column, direction);
    }


    /****************************************************************
     * Calculates the pixel position at which the input actor will be
     * rendered.
     *
     * @param row
     *            The row of the map containing the actor (tile) to be
     *            plotted.
     * @param column
     *            The column of the map containing the actor (tile) to
     *            be plotted.
     *
     * @return The pixel position at which the actor will be rendered.
     */
    @Override
    public abstract Point plotTile(int row, int column);


    /****************************************************************
     * Returns the vertical height of the standard base tile image in
     * this tile map.
     *
     * @return The height of the standard base tile in this map.
     */
    @Override
    public int getTileHeight()
    {
        return getTileWidth() / 2;
    }


    /****************************************************************
     * Converts a screen (mouse) coordinate into a map coordinate.
     * Uses the five-step mouse mapping algorithm presented in the
     * book <i>Isometric Game Programming with DirectX 7.0</i> by
     * Ernest Pazera.
     *
     * @param mousePoint
     *            A point representing a mouse coordinate.
     *
     * @return The address of the map cell containing the input mouse
     *         coordinate.
     */
    @Override
    public MHMapCellAddress mapMouse(final Point mousePoint)
    {

        // ///////////////////////////////////////////////////////////
        // Step #1: Convert Screen Coordinates to World Coordinates
        // ///////////////////////////////////////////////////////////

        final Point worldPoint = screenToWorld(mousePoint);

        // ///////////////////////////////////////////////////////////
        // Step #2: Subtract World Coordinates for the Upper Left of
        // the Map Position (0, 0)
        // ///////////////////////////////////////////////////////////

        // calculate reference point -- Point relative to mouse map
        final Point refPoint = plotTile(0, 0);

        refPoint.x += refPoint.x;
        refPoint.y += refPoint.y;

        // subtract reference point
        worldPoint.x -= refPoint.x;
        worldPoint.y -= refPoint.y;

        // ///////////////////////////////////////////////////////////
        // Step #3: Determine Mouse Map Coordinates
        // ///////////////////////////////////////////////////////////

        // coarse coordinates -- Estimate which cell we're near
        final MHMapCellAddress mouseMapCoarse = new MHMapCellAddress();
        mouseMapCoarse.column = worldPoint.x / MHIsoMouseMap.WIDTH;
        mouseMapCoarse.row = worldPoint.y / MHIsoMouseMap.HEIGHT;

        // fine coordinates -- Where are we relative to the coarse
        // cell?
        final Point mouseMapFine = new Point();
        mouseMapFine.x = worldPoint.x % MHIsoMouseMap.WIDTH;
        mouseMapFine.y = worldPoint.y % MHIsoMouseMap.HEIGHT;

        // adjust for negative fine coordinates
        if (mouseMapFine.x < 0)
        {
            mouseMapFine.x += MHIsoMouseMap.WIDTH;
            mouseMapCoarse.column--;
        }

        if (mouseMapFine.y < 0)
        {
            mouseMapFine.y += MHIsoMouseMap.HEIGHT;
            mouseMapCoarse.row--;
        }

        MHMapCellAddress mapAddress = new MHMapCellAddress();
        mapAddress.row = 0;
        mapAddress.column = 0;

        // ///////////////////////////////////////////////////////////
        // Step #4: Perform a Coarse Tile Walk
        // ///////////////////////////////////////////////////////////

        // North
        while (mouseMapCoarse.row < 0)
        {
            mapAddress = tileWalk(mapAddress, MHTileMapDirection.NORTH);
            mouseMapCoarse.row++;
        }

        // South
        while (mouseMapCoarse.row > 0)
        {
            mapAddress = tileWalk(mapAddress, MHTileMapDirection.SOUTH);
            mouseMapCoarse.row--;
        }

        // West
        while (mouseMapCoarse.column < 0)
        {
            mapAddress = tileWalk(mapAddress, MHTileMapDirection.WEST);
            mouseMapCoarse.column++;
        }

        // East
        while (mouseMapCoarse.column > 0)
        {
            mapAddress = tileWalk(mapAddress, MHTileMapDirection.EAST);
            mouseMapCoarse.column--;
        }

        // ///////////////////////////////////////////////////////////
        // Step #5: Use the Mouse Map Lookup Table
        // ///////////////////////////////////////////////////////////

        // Figure out which direction the fine coordinates indicate
        final MHTileMapDirection mouseMapDirection = mouseMap.getDirection(
                mouseMapFine.x, mouseMapFine.y);

        // Walk in the direction specified above
        mapAddress = tileWalk(mapAddress, mouseMapDirection);

        // return map coordinate
        return mapAddress;
    }

    
    public void moveFineObject(MHActor object, int destRow, int destColumn)
    {
        // Remove object from map.
        MHMapCellAddress old = mapMouse(calculateBasePoint(object));
        this.getMapData().getMapCell(old.row, old.column).setLayer(MHMapCell.WALL_LAYER, null);

        // Insert object at new location.
        this.getMapData().getMapCell(destRow, destColumn).setLayer(MHMapCell.WALL_LAYER, object);
        
        // Remove object from fine list.
//        if (fineObjects != null && fineObjects.containsKey(old.row+old.column))
//        {
//            if (fineObjects.get(old.row + old.column).remove(object))
//                System.out.println("Object removed: " + object.toString());
//            else
//                System.out.println("Object not found: " + object.toString());
//        }
        
        // Re-insert object into fine list.
//        this.placeFineObject(object);
    }

    
    public void moveFineObject(MHActor object, MHMapCellAddress destination)
    {
        moveFineObject(object, destination.row, destination.column);
    }


    /****************************************************************
     * Draws the map onto the sent Graphics object.
     *
     * @param g
     *            The graphics object on which we are rendering the
     *            map.
     * @param optimized
     *            A flag indicating whether to use the optimized
     *            rendering algorithm.
     */
    public void render(final Graphics2D g, final boolean optimized)
    {
        if (optimized)
        {
            render(g);
            return;
        }

        MHMapCell mapCell;
        MHActor tile;
        Point ptTile;

        for (int layer = 0; layer < MHMapCell.NUM_LAYERS; layer++)
        {
            for (int row = 0; row < getMapData().getHeight(); row++)
            {
                for (int column = 0; column < getMapData().getWidth(); column++)
                {
                    // Calculate the position of the image anchor
                    ptTile = plotTile(row, column);

                    // Translate to the base tile's center point
                    ptTile.x += (getTileWidth() / 2);
                    ptTile.y += getTileHeight();

                    // Get the map cell to be rendered
                    mapCell = getMapData().getMapCell(row, column);

                    // Get the tile to be rendered
                    tile = mapCell.getLayer(layer);

                    if (tile == null || tile.getImage() == null)
                    {
                        continue;
                    }

                    // Calculate its position relative to the base
                    // tile.
                    ptTile.x -= tile.getImage().getWidth(null) / 2;
                    ptTile.y -= tile.getImage().getHeight(null);

                    // Adjust for screen anchor to enable scrolling
                    ptTile.x -= screenAnchor.x;
                    ptTile.y -= screenAnchor.y;

                    mapCell.render(g, layer, ptTile.x, ptTile.y);

                    // if the current (row, column) is the one
                    // selected by the cursor,
                    // save its plot coordinates
                    if (cursorAddress.row == row
                            && cursorAddress.column == column)
                    {
                        cursorAnchor.x = ptTile.x;
                        cursorAnchor.y = ptTile.y
                        + tile.getImage().getHeight(
                                null)
                                - getTileHeight();
                        ;
                    }
                }
            }
        }

        g.setColor(Color.BLACK);
        g.drawString("Screen:  (" + cursorPoint.x + ", "
                + cursorPoint.y + ")", 10, 30);
        final Point wp = screenToWorld(cursorPoint);
        g.drawString("World:  (" + wp.x + ", " + wp.y + ")", 10, 50);
        g.drawString("Map:  (" + cursorAddress.row + ", "
                + cursorAddress.column + ")", 10, 70);

        if (isCursorOn())
        {
            if (cursorFlasher)
            {
                // Draw the cursor
                g.setColor(Color.GREEN);
                final int w = getTileWidth();
                final int h = getTileHeight() - 1;
                g.drawLine(cursorAnchor.x + w / 2, cursorAnchor.y,
                        cursorAnchor.x + w - 1,
                        cursorAnchor.y + h / 2);
                g.drawLine(cursorAnchor.x + w - 1, cursorAnchor.y + h
                        / 2, cursorAnchor.x + w / 2,
                        cursorAnchor.y + h - 1);
                g.drawLine(cursorAnchor.x + w / 2 - 1, cursorAnchor.y
                        + h - 1, cursorAnchor.x + 2,
                        cursorAnchor.y + h / 2 + 1);
                g.drawLine(cursorAnchor.x, cursorAnchor.y + h / 2,
                        cursorAnchor.x + w / 2 - 1,
                        cursorAnchor.y);
            }

            cursorFlasher = !cursorFlasher;
        }

    }


    /****************************************************************
     * Renders the map onto the sent Graphics object using an
     * optimized algorithm.
     *
     * NOTE:  This method renders one CELL at a time.
     *
     * @param g
     *            The graphics object on which we are rendering the
     *            map.
     */
    @Override
    public void render(final Graphics2D g)
    {
        MHMapCellAddress upperRight = new MHMapCellAddress(), 
        upperLeft = new MHMapCellAddress(), 
        lowerRight = new MHMapCellAddress(), 
        lowerLeft = new MHMapCellAddress();
        final MHMapCellAddress mouseMapCoarse = new MHMapCellAddress();
        MHMapCellAddress mapAddress;
        Point screenPoint, worldPoint, refPoint;

        // ///////////////////////////////////////////////////////////
        // //
        // // Prepatory Stage
        // //
        // ///////////////////////////////////////////////////////////

        // /////////////////////////////
        // UPPER LEFT CORNER:
        // /////////////////////////////

        // screen point
        screenPoint = new Point();
        screenPoint.x = (int) screenSpace.getX();
        screenPoint.y = (int) screenSpace.getY();

        // change into world coordinate
        worldPoint = screenToWorld(screenPoint);

        // adjust by mouse map reference point
        refPoint = plotTile(0, 0);

        refPoint.x += refPoint.x;
        refPoint.y += refPoint.y;

        // subtract reference point
        worldPoint.x -= refPoint.x;
        worldPoint.y -= refPoint.y;

        // Calculate coarse coordinates
        mouseMapCoarse.column = worldPoint.x / MHIsoMouseMap.WIDTH;
        mouseMapCoarse.row = worldPoint.y / MHIsoMouseMap.HEIGHT;

        if (worldPoint.x % MHIsoMouseMap.WIDTH < 0)
            mouseMapCoarse.column--;

        if (worldPoint.y % MHIsoMouseMap.HEIGHT < 0)
            mouseMapCoarse.row--;

        // Initialize map address
        mapAddress = tileWalk(0, 0, MHTileMapDirection.EAST);
        mapAddress.row *= mouseMapCoarse.column;
        mapAddress.column *= mouseMapCoarse.column;

        upperLeft = new MHMapCellAddress();
        upperLeft.row = mapAddress.row;
        upperLeft.column = mapAddress.column;

        // Reset map point
        mapAddress = tileWalk(0, 0, MHTileMapDirection.SOUTH);
        mapAddress.row *= mouseMapCoarse.row;
        mapAddress.column *= mouseMapCoarse.row;

        upperLeft.row += mapAddress.row;
        upperLeft.column += mapAddress.column;

        // /////////////////////////////
        // UPPER RIGHT CORNER
        // /////////////////////////////

        // screen point
        screenPoint.x = (int) screenSpace.getWidth() - 1;
        screenPoint.y = (int) screenSpace.getY();

        // change into world coordinate
        worldPoint = screenToWorld(screenPoint);

        // adjust by mousemap reference point
        worldPoint.x -= refPoint.x;
        worldPoint.y -= refPoint.y;

        // calculate coarse coordinates
        mouseMapCoarse.row = worldPoint.y / MHIsoMouseMap.HEIGHT;
        mouseMapCoarse.column = worldPoint.x / MHIsoMouseMap.WIDTH;

        // adjust for negative remainders
        if (worldPoint.x % MHIsoMouseMap.WIDTH < 0)
        {
            mouseMapCoarse.column--;
        }

        if (worldPoint.y % MHIsoMouseMap.HEIGHT < 0)
        {
            mouseMapCoarse.row--;
        }

        // do eastward tilewalk
        mapAddress = tileWalk(0, 0, MHTileMapDirection.EAST);
        mapAddress.row *= mouseMapCoarse.column;
        mapAddress.column *= mouseMapCoarse.column;

        // assign ptmap to corner point
        upperRight.row = mapAddress.row;
        upperRight.column = mapAddress.column;

        // do southward tilewalk
        mapAddress = tileWalk(0, 0, MHTileMapDirection.SOUTH);
        mapAddress.row *= mouseMapCoarse.row;
        mapAddress.column *= mouseMapCoarse.row;

        // add mapAddress to corner point
        upperRight.row += mapAddress.row;
        upperRight.column += mapAddress.column;

        // /////////////////////////////
        // LOWER LEFT CORNER
        // /////////////////////////////

        // screen point
        screenPoint.x = (int) screenSpace.getX();
        screenPoint.y = (int) screenSpace.getHeight() + 128;// - 1;

        // change into world coordinate
        worldPoint = screenToWorld(screenPoint);

        // adjust by mousemap reference point
        worldPoint.x -= refPoint.x;
        worldPoint.y -= refPoint.y;

        // calculate coarse coordinates
        mouseMapCoarse.row = worldPoint.y / MHIsoMouseMap.HEIGHT;
        mouseMapCoarse.column = worldPoint.x / MHIsoMouseMap.WIDTH;

        // adjust for negative remainders
        if (worldPoint.x % MHIsoMouseMap.WIDTH < 0)
            mouseMapCoarse.column--;

        if (worldPoint.y % MHIsoMouseMap.HEIGHT < 0)
            mouseMapCoarse.row--;

        // do eastward tilewalk
        mapAddress = tileWalk(0, 0, MHTileMapDirection.EAST);
        mapAddress.row *= mouseMapCoarse.column;
        mapAddress.column *= mouseMapCoarse.column;

        // assign ptmap to corner point
        lowerLeft.row = mapAddress.row;
        lowerLeft.column = mapAddress.column;

        // do southward tilewalk
        mapAddress = tileWalk(0, 0, MHTileMapDirection.SOUTH);
        mapAddress.row *= mouseMapCoarse.row;
        mapAddress.column *= mouseMapCoarse.row;

        // add ptmap to corner point
        lowerLeft.row += mapAddress.row;
        lowerLeft.column += mapAddress.column;

        // /////////////////////////////
        // LOWER RIGHT CORNER
        // /////////////////////////////

        // screen point
        screenPoint.x = (int) screenSpace.getWidth() - 1;
        screenPoint.y = (int) screenSpace.getHeight() + 128;// - 1;

        // change into world coordinate
        worldPoint = screenToWorld(screenPoint);

        // adjust by mousemap reference point
        worldPoint.x -= refPoint.x;
        worldPoint.y -= refPoint.y;

        // calculate coarse coordinates
        mouseMapCoarse.row = worldPoint.y / MHIsoMouseMap.HEIGHT;
        mouseMapCoarse.column = worldPoint.x / MHIsoMouseMap.WIDTH;

        // adjust for negative remainders
        if (worldPoint.x % MHIsoMouseMap.WIDTH < 0)
        {
            mouseMapCoarse.column--;

        }
        if (worldPoint.y % MHIsoMouseMap.HEIGHT < 0)
        {
            mouseMapCoarse.row--;

            // do eastward tilewalk
        }
        mapAddress = tileWalk(0, 0, MHTileMapDirection.EAST);
        mapAddress.row *= mouseMapCoarse.column;
        mapAddress.column *= mouseMapCoarse.column;

        // assign mapAddress to corner point
        lowerRight.row = mapAddress.row;
        lowerRight.column = mapAddress.column;

        // do southward tilewalk
        mapAddress = tileWalk(0, 0, MHTileMapDirection.SOUTH);
        mapAddress.row *= mouseMapCoarse.row;
        mapAddress.column *= mouseMapCoarse.row;

        // add mapAddress to corner point
        lowerRight.row += mapAddress.row;
        lowerRight.column += mapAddress.column;

        // tilewalk from corners
        upperLeft = tileWalk(upperLeft, MHTileMapDirection.NORTHWEST);
        upperRight = tileWalk(upperRight, MHTileMapDirection.NORTHEAST);
        lowerLeft = tileWalk(lowerLeft, MHTileMapDirection.SOUTHWEST);
        lowerRight = tileWalk(lowerRight, MHTileMapDirection.SOUTHEAST);

        // ///////////////////////////////////////////////////////////
        // //
        // // Rendering Loop
        // //
        // ///////////////////////////////////////////////////////////

        MHMapCellAddress currentAddress = new MHMapCellAddress();
        MHMapCellAddress startAddress = new MHMapCellAddress();
        MHMapCellAddress endAddress = new MHMapCellAddress();
        int rowCount = 0;

        MHMapCell mapCell;
        MHActor tile;

        // Variables used in rendering loop. Placed here to reduce
        // the number of method calls performed during rendering.
        final MHMap mapData = getMapData();
        final int mapWidth = mapData.getWidth();
        final int mapHeight = mapData.getHeight();
        final int tileWidth = getTileWidth();
        final int tileHeight = getTileHeight();

        // set up rows
        startAddress.row = upperLeft.row;
        startAddress.column = upperLeft.column;

        endAddress.row = upperRight.row;
        endAddress.column = upperRight.column;

        for (;;)
        {
            // "infinite" loop for rows
            // set current point to rowstart
            currentAddress.row = startAddress.row;
            currentAddress.column = startAddress.column;

            // render a row of tiles
            for (;;)
            {
                // check for valid point. if valid, render
                if (currentAddress.column >= 0
                        && currentAddress.row >= 0
                        && currentAddress.column < mapWidth
                        && currentAddress.row < mapHeight)
                {
                    
                    // start rendering loops
                    for (int layer = 0; layer < MHMapCell.NUM_LAYERS; layer++)
                    {
                        // valid, so render
                        screenPoint = plotTile(currentAddress.row,
                                currentAddress.column);

                        // screenPoint =
                        // worldToScreen(screenPoint);//world->screen

                        // Translate to the base tile's center point
                        screenPoint.x += (tileWidth / 2);
                        screenPoint.y += tileHeight;

                        // Get the map cell to be rendered
                        mapCell = mapData.getMapCell(
                                currentAddress.row,
                                currentAddress.column);

                        // Get the tile to be rendered
                        tile = mapCell.getLayer(layer);
                        if (tile != null)
                        {
                            Image tileImage = tile.getImage();

                            if (tileImage != null)
                            {
                                // Calculate its position relative to
                                // the base tile.
                                screenPoint.x -= tileImage
                                .getWidth(null) / 2;
                                screenPoint.y -= tileImage
                                .getHeight(null);

                                // Adjust for screen anchor to enable
                                // scrolling
                                screenPoint.x -= screenAnchor.x;
                                screenPoint.y -= screenAnchor.y;

                                // Draw the tile
                                mapCell.render(g, layer,
                                        screenPoint.x,
                                        screenPoint.y);
                            } // If tileImage != null

                                // if the current (row, column) is the
                                // one selected by the
                                // cursor, save its plot coordinates
                                if (cursorAddress.row == currentAddress.row
                                        && cursorAddress.column == currentAddress.column)
                                {
                                    cursorAnchor.x = screenPoint.x;
                                    cursorAnchor.y = screenPoint.y
                                    + tileImage
                                    .getHeight(null)
                                    - tileHeight;
                                }
                            } // if tile not null
                        

                        // Render fine objects.
//                        if (layer == MHMapCell.ITEM_LAYER)
//                            renderFineObjects(g, currentAddress.row + currentAddress.column);

                        } // for layer...

                } // if valid point

                // check if at end of row. if we are, break out of
                // inner loop
                if (currentAddress.column == endAddress.column
                        && currentAddress.row == endAddress.row)
                {
                    break;
                }

                // walk east to next tile
                currentAddress = tileWalk(currentAddress,
                        MHTileMapDirection.EAST);
            } // render a row

            // check to see if we are at the last row. if we are,
            // break out of
            // the loop
            if (startAddress.column == lowerLeft.column
                    && startAddress.row == lowerLeft.row)
            {
                break;
            }

            // move the row start and end points, based on the row
            // number
            if (rowCount % 2 != 0)
            {
                // odd
                // start moves SW, end moves SE
                startAddress = tileWalk(startAddress,
                        MHTileMapDirection.SOUTHWEST);
                endAddress = tileWalk(endAddress, MHTileMapDirection.SOUTHEAST);
            }
            else
            {
                // even
                // start moves SE, end moves SW
                startAddress = tileWalk(startAddress,
                        MHTileMapDirection.SOUTHEAST);
                endAddress = tileWalk(endAddress, MHTileMapDirection.SOUTHWEST);
            }

            // increase the row number
            rowCount++;
        }

        // Misc. stuff
        /*
         * g.setColor(Color.BLACK); g.drawString("Screen:  (" +
         * cursorPoint.x + ", " + cursorPoint.y + ")", 10, 30); Point
         * wp = screenToWorld(cursorPoint); g.drawString("World:  (" +
         * wp.x + ", " + wp.y + ")", 10, 50); g.drawString("Map:  (" +
         * cursorAddress.row + ", " + cursorAddress.column + ")", 10,
         * 70);
         */

        if (isCursorOn())
        {
            if (cursorFlasher)
                drawCursor(g);

            cursorFlasher = !cursorFlasher;
        }

    }


    /****************************************************************
     * Renders the selected layers of the map onto the sent Graphics
     * object using an optimized algorithm.
     * 
     * NOTE:  This method renders one LAYER at a time.
     *
     * @param g
     *            The graphics object on which we are rendering the
     *            map.
     * @param flags
     *            An array of flags indicating which layers are to be
     *            rendered.
     */
    public void render(final Graphics2D g, final boolean[] flags)
    {
        MHMapCellAddress upperRight = new MHMapCellAddress(), upperLeft = new MHMapCellAddress(), lowerRight = new MHMapCellAddress(), lowerLeft = new MHMapCellAddress();
        final MHMapCellAddress mouseMapCoarse = new MHMapCellAddress();
        MHMapCellAddress mapAddress;
        Point screenPoint, worldPoint, refPoint;

        // ///////////////////////////////////////////////////////////
        // //
        // // Prepatory Stage
        // //
        // ///////////////////////////////////////////////////////////

        // /////////////////////////////
        // UPPER LEFT CORNER:
        // /////////////////////////////

        // screen point
        screenPoint = new Point();
        screenPoint.x = (int) screenSpace.getX();
        screenPoint.y = (int) screenSpace.getY();

        // change into world coordinate
        worldPoint = screenToWorld(screenPoint);

        // adjust by mouse map reference point
        refPoint = plotTile(0, 0);

        refPoint.x += refPoint.x;
        refPoint.y += refPoint.y;

        // subtract reference point
        worldPoint.x -= refPoint.x;
        worldPoint.y -= refPoint.y;

        // Calculate coarse coordinates
        mouseMapCoarse.column = worldPoint.x / MHIsoMouseMap.WIDTH;
        mouseMapCoarse.row = worldPoint.y / MHIsoMouseMap.HEIGHT;

        if (worldPoint.x % MHIsoMouseMap.WIDTH < 0)
            mouseMapCoarse.column--;

        if (worldPoint.y % MHIsoMouseMap.HEIGHT < 0)
            mouseMapCoarse.row--;

        // Initialize map address
        mapAddress = tileWalk(0, 0, MHTileMapDirection.EAST);
        mapAddress.row *= mouseMapCoarse.column;
        mapAddress.column *= mouseMapCoarse.column;

        upperLeft = new MHMapCellAddress();
        upperLeft.row = mapAddress.row;
        upperLeft.column = mapAddress.column;

        // Reset map point
        mapAddress = tileWalk(0, 0, MHTileMapDirection.SOUTH);
        mapAddress.row *= mouseMapCoarse.row;
        mapAddress.column *= mouseMapCoarse.row;

        upperLeft.row += mapAddress.row;
        upperLeft.column += mapAddress.column;

        // /////////////////////////////
        // UPPER RIGHT CORNER
        // /////////////////////////////

        // screen point
        screenPoint.x = (int) screenSpace.getWidth() - 1;
        screenPoint.y = (int) screenSpace.getY();

        // change into world coordinate
        worldPoint = screenToWorld(screenPoint);

        // adjust by mousemap reference point
        worldPoint.x -= refPoint.x;
        worldPoint.y -= refPoint.y;

        // calculate coarse coordinates
        mouseMapCoarse.row = worldPoint.y / MHIsoMouseMap.HEIGHT;
        mouseMapCoarse.column = worldPoint.x / MHIsoMouseMap.WIDTH;

        // adjust for negative remainders
        if (worldPoint.x % MHIsoMouseMap.WIDTH < 0)
        {
            mouseMapCoarse.column--;
        }

        if (worldPoint.y % MHIsoMouseMap.HEIGHT < 0)
        {
            mouseMapCoarse.row--;
        }

        // do eastward tilewalk
        mapAddress = tileWalk(0, 0, MHTileMapDirection.EAST);
        mapAddress.row *= mouseMapCoarse.column;
        mapAddress.column *= mouseMapCoarse.column;

        // assign ptmap to corner point
        upperRight.row = mapAddress.row;
        upperRight.column = mapAddress.column;

        // do southward tilewalk
        mapAddress = tileWalk(0, 0, MHTileMapDirection.SOUTH);
        mapAddress.row *= mouseMapCoarse.row;
        mapAddress.column *= mouseMapCoarse.row;

        // add mapAddress to corner point
        upperRight.row += mapAddress.row;
        upperRight.column += mapAddress.column;

        // /////////////////////////////
        // LOWER LEFT CORNER
        // /////////////////////////////

        // screen point
        screenPoint.x = (int) screenSpace.getX();
        screenPoint.y = (int) screenSpace.getHeight() - 1;

        // change into world coordinate
        worldPoint = screenToWorld(screenPoint);

        // adjust by mousemap reference point
        worldPoint.x -= refPoint.x;
        worldPoint.y -= refPoint.y;

        // calculate coarse coordinates
        mouseMapCoarse.row = worldPoint.y / MHIsoMouseMap.HEIGHT;
        mouseMapCoarse.column = worldPoint.x / MHIsoMouseMap.WIDTH;

        // adjust for negative remainders
        if (worldPoint.x % MHIsoMouseMap.WIDTH < 0)
            mouseMapCoarse.column--;

        if (worldPoint.y % MHIsoMouseMap.HEIGHT < 0)
            mouseMapCoarse.row--;

        // do eastward tilewalk
        mapAddress = tileWalk(0, 0, MHTileMapDirection.EAST);
        mapAddress.row *= mouseMapCoarse.column;
        mapAddress.column *= mouseMapCoarse.column;

        // assign ptmap to corner point
        lowerLeft.row = mapAddress.row;
        lowerLeft.column = mapAddress.column;

        // do southward tilewalk
        mapAddress = tileWalk(0, 0, MHTileMapDirection.SOUTH);
        mapAddress.row *= mouseMapCoarse.row;
        mapAddress.column *= mouseMapCoarse.row;

        // add ptmap to corner point
        lowerLeft.row += mapAddress.row;
        lowerLeft.column += mapAddress.column;

        // /////////////////////////////
        // LOWER RIGHT CORNER
        // /////////////////////////////

        // screen point
        screenPoint.x = (int) screenSpace.getWidth() - 1;
        screenPoint.y = (int) screenSpace.getHeight() - 1;

        // change into world coordinate
        worldPoint = screenToWorld(screenPoint);

        // adjust by mousemap reference point
        worldPoint.x -= refPoint.x;
        worldPoint.y -= refPoint.y;

        // calculate coarse coordinates
        mouseMapCoarse.row = worldPoint.y / MHIsoMouseMap.HEIGHT;
        mouseMapCoarse.column = worldPoint.x / MHIsoMouseMap.WIDTH;

        // adjust for negative remainders
        if (worldPoint.x % MHIsoMouseMap.WIDTH < 0)
        {
            mouseMapCoarse.column--;

        }
        if (worldPoint.y % MHIsoMouseMap.HEIGHT < 0)
        {
            mouseMapCoarse.row--;

            // do eastward tilewalk
        }
        mapAddress = tileWalk(0, 0, MHTileMapDirection.EAST);
        mapAddress.row *= mouseMapCoarse.column;
        mapAddress.column *= mouseMapCoarse.column;

        // assign mapAddress to corner point
        lowerRight.row = mapAddress.row;
        lowerRight.column = mapAddress.column;

        // do southward tilewalk
        mapAddress = tileWalk(0, 0, MHTileMapDirection.SOUTH);
        mapAddress.row *= mouseMapCoarse.row;
        mapAddress.column *= mouseMapCoarse.row;

        // add mapAddress to corner point
        lowerRight.row += mapAddress.row;
        lowerRight.column += mapAddress.column;

        // tilewalk from corners
        upperLeft = tileWalk(upperLeft, MHTileMapDirection.NORTHWEST);
        upperRight = tileWalk(upperRight, MHTileMapDirection.NORTHEAST);
        lowerLeft = tileWalk(lowerLeft, MHTileMapDirection.SOUTHWEST);
        lowerRight = tileWalk(lowerRight, MHTileMapDirection.SOUTHEAST);

        // ///////////////////////////////////////////////////////////
        // //
        // // Rendering Loop
        // //
        // ///////////////////////////////////////////////////////////

        MHMapCellAddress currentAddress = new MHMapCellAddress();
        MHMapCellAddress startAddress = new MHMapCellAddress();
        MHMapCellAddress endAddress = new MHMapCellAddress();
        int rowCount = 0;

        MHMapCell mapCell;
        MHActor tile;

        // Variables used in rendering loop. Placed here to reduce
        // the number of method calls performed during rendering.
        final MHMap mapData = getMapData();
        final int mapWidth = mapData.getWidth();
        final int mapHeight = mapData.getHeight();
        final int tileWidth = getTileWidth();
        final int tileHeight = getTileHeight();
        Image tileImage;

        // start rendering loops
        for (int layer = 0; layer < MHMapCell.NUM_LAYERS; layer++)
        {
            if (!flags[layer])
                continue;

            // set up rows
            startAddress.row = upperLeft.row;
            startAddress.column = upperLeft.column;

            endAddress.row = upperRight.row;
            endAddress.column = upperRight.column;

            for (;;)
            {
                // "infinite" loop for rows
                // set current point to rowstart
                currentAddress.row = startAddress.row;
                currentAddress.column = startAddress.column;

                // render a row of tiles
                for (;;)
                {
                    // check for valid point. if valid, render
                    if (currentAddress.column >= 0
                            && currentAddress.row >= 0
                            && currentAddress.column < mapWidth
                            && currentAddress.row < mapHeight)
                    {
                        // valid, so render
                        screenPoint = plotTile(currentAddress.row,
                                currentAddress.column);

                        // screenPoint =
                        // worldToScreen(screenPoint);//world->screen

                        // Translate to the base tile's center point
                        screenPoint.x += (tileWidth / 2);
                        screenPoint.y += tileHeight;

                        // Get the map cell to be rendered
                        mapCell = mapData.getMapCell(
                                currentAddress.row,
                                currentAddress.column);

                        // Get the tile to be rendered
                        tile = mapCell.getLayer(layer);
                        if (tile != null)
                        {
                            tileImage = tile.getImage();

                            if (tileImage != null)
                            {
                                // Calculate its position relative to
                                // the base
                                // tile.
                                screenPoint.x -= tileImage
                                .getWidth(null) / 2;
                                screenPoint.y -= tileImage
                                .getHeight(null);

                                // Adjust for screen anchor to enable
                                // scrolling
                                screenPoint.x -= screenAnchor.x;
                                screenPoint.y -= screenAnchor.y;

                                // Draw the tile
                                mapCell.render(g, layer,
                                        screenPoint.x,
                                        screenPoint.y);

                                // if the current (row, column) is the
                                // one selected by the
                                // cursor, save its plot coordinates
                                if (cursorAddress.row == currentAddress.row
                                        && cursorAddress.column == currentAddress.column)
                                {
                                    cursorAnchor.x = screenPoint.x;
                                    cursorAnchor.y = screenPoint.y
                                    + tileImage
                                    .getHeight(null)
                                    - tileHeight;
                                }
                            } // if tile image not null
                        } // if tile not null
                    } // if valid point

                    // Render fine objects.
//                    if (layer == MHMapCell.ITEM_LAYER)
//                        renderFineObjects(g, currentAddress.row + currentAddress.column);  
                    
                    // check if at end of row. if we are, break out of
                    // inner loop
                    if (currentAddress.column == endAddress.column
                            && currentAddress.row == endAddress.row)
                    {
                        break;
                    }

                    // walk east to next tile
                    currentAddress = tileWalk(currentAddress,
                            MHTileMapDirection.EAST);
                } // render a row

                // check to see if we are at the last row. if we are,
                // break out of
                // the loop
                if (startAddress.column == lowerLeft.column
                        && startAddress.row == lowerLeft.row)
                {
                    break;
                }

                // move the row start and end points, based on the row
                // number
                if (rowCount % 2 != 0)
                {
                    // odd
                    // start moves SW, end moves SE
                    startAddress = tileWalk(startAddress,
                            MHTileMapDirection.SOUTHWEST);
                    endAddress = tileWalk(endAddress, MHTileMapDirection.SOUTHEAST);
                }
                else
                {
                    // even
                    // start moves SE, end moves SW
                    startAddress = tileWalk(startAddress,
                            MHTileMapDirection.SOUTHEAST);
                    endAddress = tileWalk(endAddress, MHTileMapDirection.SOUTHWEST);
                }

                // increase the row number
                rowCount++;
            }
        } // for layer...

        // Misc. stuff
        /*
         * g.setColor(Color.BLACK); g.drawString("Screen:  (" +
         * cursorPoint.x + ", " + cursorPoint.y + ")", 10, 30); Point
         * wp = screenToWorld(cursorPoint); g.drawString("World:  (" +
         * wp.x + ", " + wp.y + ")", 10, 50); g.drawString("Map:  (" +
         * cursorAddress.row + ", " + cursorAddress.column + ")", 10,
         * 70);
         */

        if (isCursorOn())
        {
            if (cursorFlasher)
                drawCursor(g);

            cursorFlasher = !cursorFlasher;
        }

    }


    /****************************************************************
    *
    */
//    private void renderFineObjects(Graphics2D g, int z)
//    {
//        if (fineObjects != null && fineObjects.containsKey(z))
//        {
//            ArrayList<MHActor> list = fineObjects.get(z);
//            for (MHActor actor : list)
//            {
//                int x = (int) (actor.getX() - screenAnchor.x);
//                int y = (int) (actor.getY() - screenAnchor.y);
//                actor.render(g, x, y);
//            }
//        }
//    }
    
    /****************************************************************
     *
     */
    protected void drawCursor(final Graphics g)
    {
        g.setColor(Color.GREEN);
        final int w = getTileWidth();
        final int h = getTileHeight() - 1;
        final int x = cursorAnchor.x;
        final int y = cursorAnchor.y;

        g.drawLine(x+w/2,   y,     x+w-1,   y+h/2);
        g.drawLine(x+w-1,   y+h/2, x+w/2,   y+h-1);
        g.drawLine(x+w/2-1, y+h-1, x+2,     y+h/2+1);
        g.drawLine(x,       y+h/2, x+w/2-1, y);
    }


    /****************************************************************
     *
     */

    @Override
    public void advance()
    {
        super.advance();

        cursorAddress = mapMouse(cursorPoint);

        clipCursorAddress();

        cursorAnchor = plotTile(cursorAddress.row,
                cursorAddress.column);
    }


    /**
     * Returns the cursorOn.
     *
     * @return boolean
     */
    public boolean isCursorOn()
    {
        return cursorOn;
    }


    /****************************************************************
     * Sets the cursorOn.
     *
     * @param cursorOn
     *            The cursorOn to set
     */
    public void setCursorOn(final boolean cursorOn)
    {
        this.cursorOn = cursorOn;
    }


    /****************************************************************
     * Centers the view space on the map cell specified by the row
     * and column parameters.
     * 
     * @param row
     * @param column
     */
    public void centerOn(int row, int column)
    {
        Point p = plotTile(row, column);
        int width = MHDisplayModeChooser.getWidth();
        int height = MHDisplayModeChooser.getHeight();// + MHIsoMouseMap.HEIGHT * 4;
        int x = p.x - width/2 + MHIsoMouseMap.WIDTH/2;
        int y = p.y - height/2;
        setScreenAnchor(x, y);
    }



    /****************************************************************
     * Looks in the finely-placed objects to see if there is one at
     * the given coordinate.
     * 
     * @param r
     * @param c
     * 
     * @return
     */
//    public boolean isObjectAt(int r, int c)
//    {
//        boolean b = false;
//        
//        if (fineObjects != null)
//        {
//            for (ArrayList<MHActor> actors : fineObjects.values())
//            {
//                for (MHActor actor : actors)
//                {
//                    MHMapCellAddress location = mapMouse(calculateBasePoint(actor));
//                    if (location.row == r && location.column == c)
//                        b = true;
//                }
//            }
//        }
//        
//        return b;
//    }
}
