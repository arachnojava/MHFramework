package mhframework.io;

import java.io.Serializable;

/********************************************************************
 * Interface for classes that must be transmitted across a network
 * or written to a file.  The way this class is intended to be used
 * is as follows:  When an object implementing this interface is
 * ready to be sent through a stream, the
 * <code>getSerializableVersion()</code> method should be called to
 * obtain a guaranteed serializable version of the object.  When
 * an instance is received from a stream, it should be sent to a
 * method which should reconstitute the parameter's members into 
 * their regular forms.
 * 
 * @author Michael Henson
 */
public interface MHSerializable extends Serializable
{
    /****************************************************************
     * Creates an object that contains only the serializable parts
     * of this class so that a representative object can be sent
     * through a stream.  The object returned by this method is
     * eligible to be transmitted across a network or saved to a
     * file.
     *
     * @return A serializable equivalent of the class that implements
     *         this interface.
     */
    public Serializable getSerializableVersion();


    /****************************************************************
     * Accepts a serializable object and copies its members into
     * the equivalent members in the implementing class.
     *
     * @param data A serializable object to be reconstituted into
     *             the class that implements this interface.
     * @return A reference to the reconstituted object
     */
    //public MHSerializable deserialize(Serializable data);
}
