package mhframework;

/********************************************************************
 * Class used for requesting specific video mode parameters.
 * Think of it as a bean, albeit an improper one.
 * 
 * @author Michael Henson
 *
 */
public class MHVideoSettings
{
    public int displayWidth=800, displayHeight=600, bitDepth=32;
    public boolean showSplashScreen = true;

    public String windowCaption = "MHFramework Application";

    public boolean fullScreen = false;
}
