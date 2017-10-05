package mhframework.ai.path;

import mhframework.tilemap.MHMapCellAddress;
import mhframework.tilemap.MHTileMap;
import mhframework.tilemap.MHTileMapDirection;

/**
 * Derived from pseudo-code in "The Basics of A* for Path Planning", Bryan
 * Stout In 'Game Programming Gems', Mike DeLoura (ed.) Charles River Media,
 * 2000, part 3.3, pp. 254-263
 */

public class MHPathFinder
{
    /**
     * Searches the given map for a path from the specified start to 
     * the specified goal by stepping only in the specified 
     * directions.
     * 
     * @param startLoc The starting location.
     * @param goalLoc The goal.
     * @param map The map to be searched.
     * @param directions An array of MHTileMapDirection values indicating which directions a unit can step.
     * 
     * @return An MHNodePath from the start to the goal, or null if no path found.
     */
    public static MHNodePath aStarSearch(final MHMapCellAddress startLoc, final MHMapCellAddress goalLoc, MHTileMap map, MHTileMapDirection[] directions)
    {
        // DEBUG
//        System.out.println("########################################");
//        System.out.println("# A* search from " + startLoc + " to " + goalLoc);
//        System.out.println("########################################");
        double newCost;
        MHNode bestNode, newNode;

        final MHNode startNode = new MHNode(startLoc); // set start node
        startNode.costToGoal(goalLoc);

        // create the open queue and closed list
        final MHOpenNodeList open = new MHOpenNodeList(startNode);
        final MHClosedNodeList closed = new MHClosedNodeList();

        // while some node still left to investigate
        while (open.size() != 0)
        { 
            bestNode = open.removeFirst();
            if (goalLoc.equals(bestNode)) // reached the goal
            {
                //DEBUG
                //System.out.println("Goal reached: " + goalLoc);
                return bestNode.buildPath(); // return a path to that goal
            }

            for (int i = 0; i < directions.length; i++)
            { 
                // try every direction
                newNode = bestNode.makeNeighbor(directions[i], map);
                if (newNode != null)
                {
                    //DEBUG
                    //System.out.print("\tnewNode = " + newNode);
                    
                    newCost = newNode.getCostFromStart();
                    MHNode oldVer;
                    // if this tile already has a cheaper open or closed node
                    // then ignore the new node
                    if ((oldVer = open.findNode(newNode)) != null && oldVer.getCostFromStart() <= newCost)
                    {
                        //DEBUG
                        //System.out.println("...ignoring it 'cause there's already a cheaper open path.");
                        continue;
                    }
                    else if ((oldVer = closed.findNode(newNode)) != null && oldVer.getCostFromStart() <= newCost)
                    {
                        //DEBUG
                        //System.out.println("...ignoring it 'cause there's already a cheaper closed path.");
                        continue;
                    }
                    else
                    { 
                        //DEBUG
                        //System.out.println("Adding " + newNode + " to the list.");
                        
                        // store the new/improved node, removing the old one
                        newNode.costToGoal(goalLoc);
                        // delete the old details (if they exist)
                        closed.delete(newNode); // may do nothing
                        open.delete(newNode); // may do nothing
                        open.add(newNode);
                    }
                }
            } // end of for block
            closed.add(bestNode);
        }
        return null; // no path found
    } // end of aStarSearch()
    

    /**
     * Searches the given map for a path from the specified start to 
     * the specified goal by stepping in any direction.
     * 
     * @param startLoc The starting location.
     * @param goalLoc The goal.
     * @param map The map to be searched.
     * 
     * @return An MHNodePath from the start to the goal, or null if no path found.
     */
    public static MHNodePath aStarSearch(final MHMapCellAddress startLoc, final MHMapCellAddress goalLoc, MHTileMap map)
    {
        final MHTileMapDirection[] directions = MHTileMapDirection.values();
        return aStarSearch(startLoc, goalLoc, map, directions);
    } // end of aStarSearch()
}
