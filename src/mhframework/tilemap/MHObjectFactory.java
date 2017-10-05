package mhframework.tilemap;

import mhframework.MHActor;

/********************************************************************
 * This interface is to be implemented by the class in your game that
 * is responsible for taking a tile ID and returning a special
 * non-static object.
 *
 * <p>The code in such a class might look something like this:
<pre>
    if (layer == MHMapCell.FLOOR_LAYER)
    {
        switch(tileID)
        {
            case FLOOR_SWITCH:
                return new MyGameFloorSwitch();
                break;
            case HAZARD_SPIKES:
                return new MyGameSpikeHazard();
                break;
            default:
                return null;
        }
    }
	else if (layer == MHMapCell.WALL_LAYER)
	{
	    switch(tileID)
	    {
	    	case DOOR_1:
	    	case DOOR_2:
	    	case DOOR_3:
	    	    return new MyGameDoor(tileID);
	    	    break;
	    	case COMPUTER_CONSOLE:
	    	    return new MyGameComputerConsole();
	    	    break;
	    	case ALIEN_0:
	    	    return new MyGameAlien0();
	    	    break;
            default:
                return null;
	    }
	}
	else if (layer == MHMapCell.ITEM_LAYER)
	{
	    switch(tileID)
	    {
	    	case WEAPON_1:
	    	    return new MyGameLaserPistol();
	    	    break;
	    	case WEAPON_2:
	    	    return new MyGameLaserRifle();
	    	    break;
	    	case POWERUP:
	    		return new MyGamePowerUp();
	    		break;
	    	case KEY:
	    		return new MyGameKey();
	    		break;
            default:
                return null;
	    }
	}
	else
	{
	    return null;
	}
</pre>

 * @author Michael Henson
 */
public interface MHObjectFactory
{
	/****************************************************************
	 * Accepts layer and tile identifiers, instantiates a special
	 * object indicated by them, and returns a reference to it.  If
	 * there is no special object for a given identifier, this
	 * method should return <tt>null</tt>.
	 *
	 * @param layer    The layer of the map for which this tile is
	 *                 intended.
	 * @param tileID   A tile identifier, usually read from a map
	 *                 data file.
	 * @param location The position in the map where the tile is located.
	 *
	 * @return  An object to be placed into a layer of a map cell.
	 */
	public abstract MHActor getObject(int layer, int tileID, MHMapCellAddress location);
}
