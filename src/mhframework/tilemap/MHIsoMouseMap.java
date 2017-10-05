package mhframework.tilemap;


/**
 * Mouse map for isometric tile maps.  The <tt>getDirection()</tt>
 * method in this class accepts an (x, y) pair representing a fine
 * mouse map coordinate, and then returns a constant indicating the
 * direction to perform a tile walk in order to identify the proper
 * map coordinate containing the mouse point.
 *
 * <p>The current version of this class is hard-coded to work only
 * for isometric tiles with dimensions 128x64.
 * 
 * @author Michael Henson
 */
public class MHIsoMouseMap
{
    /////////////////////////////////////////////////////////////////
	////    Constants                                            ////
    /////////////////////////////////////////////////////////////////

	/**
	 * Constant indicating center of mouse map.
	 */
	public static final int CENTER = -1;


	/**
	 * The width of the mouse map matrix.
	 */
	public static final int WIDTH  = 128;


	/**
	 * The height of the mouse map matrix.
	 */
	public static final int HEIGHT = WIDTH / 2;


    /////////////////////////////////////////////////////////////////
	////    Data Members                                         ////
    /////////////////////////////////////////////////////////////////

	/** The mouse map matrix. */
	private final MHTileMapDirection[][] regions = new MHTileMapDirection[HEIGHT][WIDTH];



    /////////////////////////////////////////////////////////////////
	////    Methods                                              ////
    /////////////////////////////////////////////////////////////////

	/****************************************************************
	 * Constructor.
	 */
	public MHIsoMouseMap()
	{
		populateRegions();
	}


	/****************************************************************
	 * Populates the mouse map matrix with the constant values
	 * defined in this class.  The resulting matrix is precisely
	 * aligned with the positions of the pixels in a standard base
	 * tile as defined in my design document.
	 */
	private void populateRegions()
	{
		int row, col;          // indices of mouse map array
		int sideRegions = WIDTH/2-2;  // distance from the top left corner of the
		                              // mouse map to the center region

		for (row = 0; row < HEIGHT; row++)
		{
			for (col = 0; col < WIDTH; col++)
			{
				if (row < HEIGHT/2)  // top half of mouse map
				{
					if (col < sideRegions)
						regions[row][col] = MHTileMapDirection.NORTHWEST;
					else if (col < WIDTH - sideRegions)
						regions[row][col] = MHTileMapDirection.CENTER;
					else
						regions[row][col] = MHTileMapDirection.NORTHEAST;
				}
				else // bottom half of mouse map
				{
					if (col < sideRegions)
						regions[row][col] = MHTileMapDirection.SOUTHWEST;
					else if (col < WIDTH - sideRegions)
						regions[row][col] = MHTileMapDirection.CENTER;
					else
						regions[row][col] = MHTileMapDirection.SOUTHEAST;
				}
			}


			if (row < HEIGHT / 2)
			{
				if (sideRegions > 0)
					sideRegions -= 2;
			}
			else
				sideRegions += 2;
		}
	}


	/****************************************************************
	 * Returns the direction to be "walked" in order for the mouse
	 * point to be accurately interpreted as a map position.
	 *
	 * @param mouseX  The x coordinate of a fine mouse map point.
	 * @param mouseY  The y coordinate of a fine mouse map point.
	 *
	 * @return  A constant indicating the point's position relative
	 *           to a map cell's center.
	 */
	public MHTileMapDirection getDirection(final int mouseX, final int mouseY)
	{
	    MHTileMapDirection direction = MHTileMapDirection.CENTER;

		if (mouseX >= 0 && mouseX < WIDTH &&
		    mouseY >= 0 && mouseY < HEIGHT)
		{
			direction = regions[mouseY][mouseX];
		}

		return direction;
	}



	/****************************************************************
	 * For testing only.  Do not use.
	 */
	public static void main(final String args[])
	{
		final MHIsoMouseMap mouseMap = new MHIsoMouseMap();

		for (int r = 0; r < MHIsoMouseMap.HEIGHT; r++)
		{
			for (int c = 0; c < MHIsoMouseMap.WIDTH; c++)
			{
				System.err.print(mouseMap.getDirection(c, r));
			}

			System.err.println();
		}
	}
}
