package mhframework.tilemap;

import java.io.Serializable;


/********************************************************************
 * Data storage class used to simplify the passing of map coordinates
 * around as parameters.  This class contains no methods, and
 * therefore does no validation of the members.  This decision was
 * made partly to keep the class size as small as possible, and
 * partly to maintain the greatest possible flexibility.
 * 
 * @author Michael Henson
 */
public class MHMapCellAddress implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** The row of the map indicated by this address. */
	public int row;

	/** The column of the map indicated by this address. */
	public int column;


    public MHMapCellAddress(final int row, final int column)
    {
        this.row = row;
        this.column = column;
    }


    public MHMapCellAddress()
    {
        row = 0;
        column = 0;
    }


    @Override
    public String toString()
    {
        return "[" + row + ", " + column + "]";
    }


    @Override
    public boolean equals(Object obj)
    {
        MHMapCellAddress other = (MHMapCellAddress) obj;
        return (this.row == other.row && this.column == other.column);
    }


    public double euclideanDistance(MHMapCellAddress other)
    {
        double rDiff = row - other.row;
        double cDiff = column - other.column;
        
        return Math.sqrt(rDiff*rDiff + cDiff*cDiff);
    }

    
    public int manhattanDistance(MHMapCellAddress other)
    {
        int rDiff = Math.abs(row - other.row);
        int cDiff = Math.abs(column - other.column);
        
        return rDiff + cDiff;
    }
}
