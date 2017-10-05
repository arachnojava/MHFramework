package mhframework.ai.path;

import mhframework.tilemap.MHMapCellAddress;


/********************************************************************
 * This A* path-finding system was adapted from the one by
 * Andrew Davison.  (ad@fivedots.coe.psu.ac.th)
 */

public class MHClosedNodeList extends MHNodePath
{
    public MHClosedNodeList(final MHNode node)
    {
        super();
        add(node);
    }


    public MHClosedNodeList()
    {
        super();
    }


    public MHNode findNode(final MHMapCellAddress p)
    // a linear search looking for the tile at point p;
    {
        MHNode entry;
        for (int i = 0; i < size(); i++)
        {
            entry = (MHNode) get(i);
            if (entry.equals(p))
                return entry;
        }
        
        return null;
    } // end of findNode()


    public boolean delete(final MHMapCellAddress p)
    /*
     * Try to delete the tile at point p from the list. If p is not
     * present then do nothing.
     */
    {
        MHNode entry;
        for (int i = 0; i < size(); i++)
        {
            entry = (MHNode) get(i);
            if (entry.equals(p))
            {
                remove(i);
                return true;
            }
        }
        
        return false;
    } // end of delete()
}
