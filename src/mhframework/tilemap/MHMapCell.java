package mhframework.tilemap;


import java.awt.Graphics2D;

import mhframework.MHActor;
import mhframework.MHRenderable;


/********************************************************************
 * A single cell in the map grid (MHMap).
 * 
 * @author Michael Henson
 */
public class MHMapCell implements MHRenderable
{
    public static final int FLOOR_LAYER        = 0;
    public static final int FLOOR_DETAIL_LAYER = 1;
    public static final int ITEM_LAYER         = 2;
    public static final int OBSTACLE_LAYER     = 3;
    public static final int WALL_LAYER         = 4;
    public static final int WALL_DETAIL_LAYER  = 5;
    public static final int CEILING_LAYER      = 6;

    public static final int NUM_LAYERS   = 7;

    private final MHActor[] layers;

	/****************************************************************
	 * Constructor.
	 */
	public MHMapCell()
	{
	   layers = new MHActor[NUM_LAYERS];
	}


    public void setLayer(final int layer, final MHActor actor)
    {
        layers[layer] = actor;
    }


    public MHActor getLayer(final int layer)
    {
        return layers[layer];
    }


    public void advance()
    {
        for (final MHActor layer : layers)
        {
            if (layer != null)
                layer.advance();
        }
    }


    public void render(final Graphics2D g)
    {
    }


    public void render(final Graphics2D g, final int layer, final int x, int y)
    {
        if (layers[layer] != null)
        {
        	if (layer == CEILING_LAYER)
        	    y -= getBaseHeight() * 2;

            layers[layer].render(g, x, y);
        }
    }
    
    
    public boolean canWalkOn()
    {
        if (layers[FLOOR_LAYER] == null)
            return false;
        
        if (layers[OBSTACLE_LAYER] != null)
            return false;
        
        if (layers[WALL_LAYER] != null)
            return false;
        
        return true;
    }


    public boolean canFlyOver()
    {
        if (layers[WALL_LAYER] != null)
            return false;
        
        return true;
    }


    public int getBaseHeight()
    {
        return MHIsoMouseMap.HEIGHT;
    }


    public int getBaseWidth()
    {
        return MHIsoMouseMap.WIDTH;
    }
}
