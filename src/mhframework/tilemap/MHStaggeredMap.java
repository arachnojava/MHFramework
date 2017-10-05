package mhframework.tilemap;

import java.awt.Point;
import java.awt.geom.Rectangle2D;



/********************************************************************
 * Handles the presentation of a staggered (layered) isometric map.
 */
public class MHStaggeredMap extends MHIsometricMap
{

	private final boolean flatEdges;

    /****************************************************************
	 */
	public MHStaggeredMap(final String filename,
                                               final MHObjectFactory vendor)
    {
        super(filename, vendor);

        flatEdges = true;

        calculateAnchorSpace();

        screenAnchor = new Point((int)anchorSpace.getX(),
                                 (int)anchorSpace.getY());
    }


    /****************************************************************
	 */
	public MHStaggeredMap(final String filename,
                           final MHObjectFactory vendor, final boolean flatEdges)
    {
        super(filename, vendor);

        this.flatEdges = flatEdges;

        screenAnchor = new Point((int)anchorSpace.getX(),
                                 (int)anchorSpace.getY());
    }


    /****************************************************************
     */
    @Override
    public MHMapCellAddress tileWalk(final int row, final int column, final MHTileMapDirection direction)
    {
		final MHMapCellAddress destination = new MHMapCellAddress();

		destination.row = row;
	    destination.column = column;

		switch(direction)
		{
			case NORTH:
		        destination.row -= 2;
		        break;

			case NORTHEAST:
			    destination.column += (destination.row%2);
			    destination.row--;
				break;

			case EAST:
			    destination.column++;
				break;

			case SOUTHEAST:
			    destination.column += (destination.row%2);
				destination.row++;
				break;

			case SOUTH:
				destination.row += 2;
				break;

			case SOUTHWEST:
				destination.column += ((destination.row%2) - 1);
				destination.row++;
				break;

			case WEST:
				destination.column--;
				break;

			case NORTHWEST:
				destination.column += ((destination.row%2) - 1);
				destination.row--;
				break;

			default:
				break;
		}

        return destination;
    }


    /****************************************************************
     * Plots the upper-left anchor point of a base tile image.
     */
    @Override
    public Point plotTile(final int mapRow, final int mapCol)
    {
		int plotX, plotY;

		final int width = getTileWidth();
		final int height = getTileHeight();

		// calculate pixel position for the map position given
		plotX = mapCol * width + (mapRow & 1) * (width / 2);
		plotY = mapRow * (height / 2);

		return new Point(plotX, plotY);
    }


	/****************************************************************
	 */
	@Override
    protected void calculateAnchorSpace()
	{
    	double width = worldSpace.getWidth() -
    	                                 screenSpace.getWidth();
    	double height = worldSpace.getHeight() -
    	                                screenSpace.getHeight();

	    if(width <= 0)
        	width = 1;

	    if(height <= 0)
            height = 1;

		// If edges should be flat, adjust anchor space to eliminate
		// jaggies.
		if (flatEdges)
    		anchorSpace = new Rectangle2D.Double(worldSpace.getX() + 32,
    		               worldSpace.getY() + 16, width - 32, height - 16);
        else
    		anchorSpace = new Rectangle2D.Double(worldSpace.getX(),
    		               worldSpace.getY(), width, height);
	}


}

