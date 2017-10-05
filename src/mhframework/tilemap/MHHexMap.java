package mhframework.tilemap;

import java.awt.Point;



/********************************************************************
 * Handles the presentation of a hexagonal tile map.
 */
public class MHHexMap extends MHIsometricMap
{

	public MHHexMap(final String filename,
                                               final MHObjectFactory vendor)
    {
        super(filename, vendor);
    }


    @Override
    public MHMapCellAddress tileWalk(final int row, final int column, final MHTileMapDirection direction)
    {
    	return null;
    }


	@Override
    public MHMapCellAddress mapMouse(final Point p)
	{
		return null;
	}


	@Override
    public Point plotTile(final int mapRow, final int mapCol)
	{
		return null;
	}

}
