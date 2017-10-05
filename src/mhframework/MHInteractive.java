package mhframework;


/********************************************************************
 * This interface is implemented by all objects in the game universe
 * that must interact with each other.  It defines a standard set of
 * methods indicating the different ways that one object can act upon
 * another.
 * 
 * @author Michael Henson
 */
public interface MHInteractive
{

    /****************************************************************
     * To be triggered when an actor begins moving toward an
     * object implementing this interface.  Possible uses
     * of this action might include opening a door before the
     * player gets to it, causing an NPC to run away, and
     * checking for collisions.
     *
     * @param actor The actor who is moving toward this object.
     */
    public void movingTo(MHActor actor);


    /****************************************************************
     * To be triggered when an actor steps on an
     * object implementing this interface.  Possible uses
     * of this action might include causing damage when a
     * player steps on a hazard, triggering a mechanism when
     * a player steps on a switch, activating automatic
     * devices such as elevators, activating dormant
     * non-player characters when a player enters a certain
     * area, etc.
     *
     * @param actor The actor who stepped on this object.
     */
    public void steppedOn(MHActor actor);


    /****************************************************************
     * To be triggered when an actor steps off of an
     * object implementing this interface.  Possible uses
     * of this action might include deactivating a mechanism
     * when a player steps off of a switch, etc.
     *
     * @param actor The actor who stepped off of this object.
     */
    public void steppedOff(MHActor actor);


    /****************************************************************
     * To be called when an object implementing this
     * interface is hit by a weapon.  For example, this method
     * might be called by a map object when a projectile
     * travels into an occupied map cell, or when a player with
     * a melee weapon issues an "attack" command when adjacent
     * to an occupied map cell.  It might also be used to
     * tell an actor that it has been hit so it can update its
     * health data accordingly.
     *
     * <p>If the object that was "hit" is not an obstacle and
     * therefore should not stop a projectile from travelling on,
     * the method should return -1 to indicate this.
     *
     * <p>If the object that was hit is an obstacle but is not
     * destructible, the method should return 0.
     *
     * @param weapon The weapon that hit this object.
     *
     * @return The amount of damage done to this object, or -1 if
     *          the weapon is ignored.
     */
    public int hitByWeapon(MHWeapon weapon);


    /****************************************************************
     * To be called when an "activate" command is issued to an
     * object.
     *
     * @param actor The actor that originated the "activate"
     *              command.
     */
    public void activate(MHActor actor);


    /****************************************************************
     * Returns a description of a game object.  Useful in games where
     * the player needs the ability to look closely at things to get
     * clues, hints, instructions, etc.
     *
     * @return A string containing a description of an object.
     */
    public String examine();


    /****************************************************************
     * Returns the name of a game object.
     *
     * @return A string containing the name of an object.
     */
    public String getName();


    /****************************************************************
     * To be called when a character picks up an item.
     * May be called in response to a "pick up item" command, or
     * automatically when a player steps on an item (possibly from
     * the steppedOn() method declared in this interface).
     *
     * @param actor The character who is picking up the item.
     */
    public void pickUp(MHActor actor);


	/****************************************************************
	 * Returns true if the actor implementing this interface can be
	 * "walked" over.
	 */
	public boolean isTraversable();

}
