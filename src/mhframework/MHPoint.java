package mhframework;

import java.awt.Point;

/********************************************************************
 * Represents a point in 2D space.  This class also contains
 * functions for translating this point along a vector and rotating
 * it around another point.
 *
 * @author Michael Henson
 * @since 2.01
 */
public class MHPoint extends Point
{
    /**
     * 
     */
    private static final long serialVersionUID = -3442339859867101477L;
    private double x, y;

    /****************************************************************
     * Default constructor.  Creates new point at the origin (0, 0).
     */
    public MHPoint()
    {
        this(0, 0);
    }

    /****************************************************************
     * Constructor.  Creates new point at the location specified by
     * the input <i>x</i> and <i>y</i> values.
     */
    public MHPoint(final double xValue, final double yValue)
    {
        x = xValue;
        y = yValue;
    }


    /****************************************************************
     * Constructor.  Creates new point by copying the input point.
     */
    public MHPoint(final MHPoint point)
    {
        x = point.getX();
        y = point.getY();
    }


    /****************************************************************
     * Returns the x component of this point.
     *
     * @return The x component of this point.
     */
    public double getX()
    {
        return x;
    }

    /****************************************************************
     * Sets the x component of this point.
     */
    public void setX(final double x)
    {
        this.x = x;
    }

    /****************************************************************
     * Returns the y component of this point.
     *
     * @return The y component of this point.
     */
    public double getY()
    {
        return y;
    }

    /****************************************************************
     * Sets the y component of this point.
     */
    public void setY(final double y)
    {
        this.y = y;
    }

    /****************************************************************
     * Sets the location of this point.
     */
    public void setLocation(final double x, final double y)
    {
        this.x = x;
        this.y = y;
    }

    /****************************************************************
     * Rotates this point around (originX, originY) by the
     * angle specified.
     *
     * @param originX The x component of the point around which to
     *                rotate this point.
     * @param originY The y component of the point around which to
     *                rotate this point.
     * @param angle   The angle by which to rotate this point.
     */
    public void rotate(final double originX, final double originY, final double angle)
    {
        //final double radians = Math.toRadians(angle);
        final double cosine = MHRotationLookup.cosine(angle); //Math.cos(radians);
        final double sine =   MHRotationLookup.sine(angle); //Math.sin(radians);
        setLocation((cosine*x - sine*y) + originX, (sine*x + cosine*y) + originY);
    }


    /****************************************************************
     * Translates this point in the direction specified by <tt>angle</tt> to
     * the distance specified by <tt>distance</tt>.
     *
     * @param angle    The angle at which this point is to be translated.
     * @param distance The distance that this point is to be translated.
     */
    public void translate(final double angle, final double distance)
    {
        final double degree = 90.0f - angle;
        final double pi180 = Math.PI / 180;
        setLocation(Math.cos((degree) * pi180) * distance,
                        -Math.sin((degree) * pi180) * distance);
    }

    
    /**
     * Returns the angle that an object would have to face in order
     * to point toward the target (x, y) coordinate.
     * 
     * @param targetX
     * @param targetY
     * @return
     */
    public double pointToward(final double targetX, final double targetY)
    {
        return (Math.atan2(targetY - getY(), targetX - getX())/Math.PI) * 180 + 90;
    }

    
    public double distanceTo(MHPoint other)
    {
        double xDiff = getX() - other.getX();
        double yDiff = getY() - other.getY();
        
        return Math.sqrt(xDiff*xDiff + yDiff*yDiff);
    }
    

    /****************************************************************
     * Creates an exact copy of this point.
     *
     * @return A new point at the same location as this point.
     */
    @Override
    public MHPoint clone()
    {
        return new MHPoint(getX(), getY());
    }
    
    
    public String toString()
    {
        return "(" + (int)x + ", " + (int)y + ")";
    }
}
