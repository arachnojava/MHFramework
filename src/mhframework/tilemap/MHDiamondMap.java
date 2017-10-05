package mhframework.tilemap;

import java.awt.Point;
import java.awt.geom.Rectangle2D;



/********************************************************************
 * Handles the presentation  of a diamond (angled) isometric map.
 */
public class MHDiamondMap extends MHIsometricMap
{
    public MHDiamondMap(final String filename, final MHObjectFactory vendor)
    {
        super(filename, vendor);
        screenAnchor = new Point((int)anchorSpace.getX(),
                                 (int)anchorSpace.getY());
    }



    @Override
    public MHMapCellAddress tileWalk(final int row, final int column, final MHTileMapDirection direction)
    {
        final MHMapCellAddress destination = new MHMapCellAddress();

        destination.row = row;
        destination.column = column;

        switch(direction)
        {
            case NORTH:
                destination.row--;
                destination.column--;
                break;

            case NORTHEAST:
                destination.row--;
                break;

            case EAST:
                destination.row--;
                destination.column++;
                break;

            case SOUTHEAST:
                destination.column++;
                break;

            case SOUTH:
                destination.row++;
                destination.column++;
                break;

            case SOUTHWEST:
                destination.row++;
                break;

            case WEST:
                destination.row++;
                destination.column--;
                break;

            case NORTHWEST:
                destination.column--;
                break;

            default:
                break;
        }

        return destination;
    }


    @Override
    public Point plotTile(final int row, final int column)
    {
        int plotX, plotY;

        plotX = (column - row) * (getTileWidth() / 2);
        plotY = (column + row) * (getTileHeight() / 2);

        return new Point(plotX, plotY);
    }


        /****************************************************************
         * Calculate the world space for this tile map.
         */
    @Override
    protected void calculateWorldSpace()
    {
        //set worldspace rect to empty
        worldSpace = new Rectangle2D.Double(0.0, 0.0, 0.0, 0.0);

        //point for plotting
        Point ptPlot;

                // Parameters for world space rectangle
                double wx = 0.0,
                        wy = 0.0,
                        width = 0.0,
                        height = 0.0;

                // top corner
                ptPlot = plotTile(0, 0);
                wy = Math.min(wy, ptPlot.y);

                // bottom corner
                ptPlot = plotTile(getMapData().getHeight()-1,
                                  getMapData().getWidth()-1);
                height = Math.max(height, ptPlot.y + getTileHeight());

                // left corner
                ptPlot = plotTile(getMapData().getHeight()-1, 0);
                wx = Math.min(wx, ptPlot.x);

                // right corner
                ptPlot = plotTile(0, getMapData().getWidth()-1);
                width = Math.max(width, ptPlot.x + getTileWidth());

            worldSpace.setRect(wx, wy, width, height);
    }

}

