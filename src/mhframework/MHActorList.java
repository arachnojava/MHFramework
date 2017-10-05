package mhframework;

import java.util.HashMap;

/********************************************************************
 * This class keeps a collection of MHActor objects.
 * 
 * @author Michael Henson
 *******************************************************************/
public class MHActorList implements MHRenderable
{
    ////////////////////////////
    ////    Data Members    ////
    ////////////////////////////

    /** List of actors */
    //private final ArrayList<MHActor> list;
    private final HashMap<Integer, MHActor> list;


	////////////////////////////
	////      Methods       ////
	////////////////////////////

    /****************************************************************
     * Constructor.
     */
    public MHActorList()
    {
       //list = new ArrayList<MHActor>();
        list = new HashMap<Integer, MHActor>();
    }


    /****************************************************************
     * Adds a new actor to the list.
     *
     * @param actor  The new actor to be added to the list.
     */
    public void add(final MHActor actor)
    {
        //list.add(actor);
        list.put(list.size(), actor);
    }


    /****************************************************************
     * Returns the actor at the given index.
     *
     * @param index  The index of the actor being requested.
     *
     * @return The actor at the given index.
     */
    public MHActor get(final int index)
    {
        final MHActor actor = list.get(index);

        return actor;
    }


    /****************************************************************
     * Clears the list of actor references.
     */
    public void clear()
    {
        list.clear();
    }


    /****************************************************************
     * States whether the actor list is empty.
     *
     * @return  True if list is empty, false otherwise.
     */
    public boolean isEmpty()
    {
        return list.isEmpty();
    }


    /****************************************************************
     * Removes the actor at the given index and returns a
     * reference to it.
     *
     * @param index  The index of the actor being removed.
     *
     * @return  The actor who was removed.
     */
    public MHActor remove(final int index)
    {
        final MHActor actor = list.remove(index);

        return actor;

    }


    /****************************************************************
     * Advances the actor at the given index.
     *
     * @param index  The index of the actor being advanced.
     */
    public void advance(final int index)
    {
    	MHActor actor = get(index);
    	if (actor != null)
    		actor.advance();
    }


    /****************************************************************
     * Advances all actors in the list.
     */
    public void advance()
    {
        for (int i = 0; i < list.size(); i++)
            advance(i);
    }


    /****************************************************************
     * Renders all actors in the list.
     *
     * @param g  The Graphics2D object on which to render the actor.
     */
    public void render(final java.awt.Graphics2D g)
    {
        for (int i = 0; i < list.size(); i++)
        {
        	MHActor actor = get(i);
        	if (actor != null)
        		actor.render(g);
        }
    }


    /****************************************************************
     * Returns the number of actors in the list.
     *
     * @return The number of actors in the list.
     */
    public int getSize()
    {
        return list.size();
    }
}
