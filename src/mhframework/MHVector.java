package mhframework;

/********************************************************************
 * 
 * 
 * @author Michael Henson
 *
 */
public class MHVector
{
    private final double PI_180 = Math.PI / 180;
    private MHPoint originPoint;
    private double magnitude;
    private double direction;


    public MHVector()
    {
        this(0, 0, 0, 0);
    }


    public MHVector(final double x0, final double y0, final double length, final double angle)
    {
        originPoint = new MHPoint(x0, y0);
        magnitude = length;
        direction = angle;
    }


    public MHPoint getOriginPoint()
    {
        return originPoint;
    }


    public MHPoint getEndPoint()
    {
        final double degree = 90.0 - direction;
        return new MHPoint(Math.cos((degree) * PI_180) * magnitude,-Math.sin((degree) * PI_180) * magnitude);
    }


    public double getMagnitude()
    {
        return magnitude;
    }


    public void setMagnitude(final double magnitude)
    {
        this.magnitude = magnitude;
    }


    public double getDirectionAngle()
    {
        return direction;
    }


    public void setDirectionAngle(double angle)
    {
        angle %= 360;

        if (angle < 0) angle = 360 + angle;

        magnitude = angle;
    }
}
