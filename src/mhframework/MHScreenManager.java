package mhframework;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferStrategy;
import mhframework.media.MHMediaTracker;
import mhframework.media.MHResourceManager;


/********************************************************************
 * The MHScreenManager class maintains references to the current
 * screen, the application's data model, and encapsulates a screen
 * stack (MHScreenStack) for providing the ability to backtrack
 * through multiple game screens.
 *
 * <p><b>AN IDEA:</b>  Maybe this class could contain a generic
 * loading screen that displays automatically if the new screen
 * being loaded is not ready to be displayed.  So, if it's told
 * to change screens, it can query the new screen to see if it's
 * ready.  If not, it can display a generic loading screen until
 * the new screen is ready.  Consider this for the next version.</p>
 * 
 * @author Michael Henson
*/
public final class MHScreenManager
{
    ////////////////////////////////
    ////////  Data members  ////////
    ////////////////////////////////


    /** Stack of loaded screens.  */
    private final MHScreenStack screenStack;

    private MHScreen defaultScreen = null;

    private boolean showFPS = false;
    

    ///////////////////////////////
    ////////    Methods    ////////
    ///////////////////////////////


    /****************************************************************
     * Constructor.  Constructs a stack with the given screen.
     *
     * @param screen An initial screen object used to create the
     *               stack. This is often a loading screen, title
     *               screen, copyright notice, etc.
     */
    public MHScreenManager(final MHScreen screen)
    {
        if (MHGameApplication.getVideoSettings().showSplashScreen)
            defaultScreen = new MHFrameworkSplashScreen(screen);
        else
            defaultScreen = screen;

        // Create screen stack with an initial screen
        screenStack = new MHScreenStack(defaultScreen);
    }


	/****************************************************************
	 * Retrieves the current screen.  The current screen is the one
	 * at the top of the screen stack.
	 *
	 * @return A reference to the top screen on the stack.
	 */
    public MHScreen getScreen()
    {
        return screenStack.top.screen;
    }


	/****************************************************************
	 * Unloads the old screen and then loads a new screen.
	 */
    private void changeScreen()
    {
		// Unload the old screen.
        getScreen().unload();

		// If there's no next screen, remove the current screen and
		// go back to the previous one.
        if (getScreen().getNextScreen() == null)
        {
            do
            {
                removeScreen();
            } while (getScreen().isDisposable());
        }
        else  // else go to the next screen
        {
            getScreen().getNextScreen().setPreviousScreen(getScreen());
            addScreen(getScreen().getNextScreen());
        }

        // Run the garbage collector to remove disposable screens.
        this.gc();

        // Load the next screen.
        getScreen().load();
    }

    /****************************************************************
     * Runs the screen manager's garbage collector to deallocate
     * screens that are no longer needed (i.e. marked as disposable).
     */
    public void gc()
    {
        MHScreen current = getScreen();
        MHScreen next = current.getPreviousScreen();

        while (next != null)
        {
            if (next.isDisposable())
                current.setPreviousScreen(next.getPreviousScreen());

            current = current.getPreviousScreen();

            if (current != null)
                next = current.getPreviousScreen();
            else
                next = current;
        }
    }

	/****************************************************************
	 * Adds a new screen to the screen stack.
	 */
    public void addScreen(final MHScreen newScreen)
    {
        screenStack.push(newScreen);
    }


	/****************************************************************
	 * Removes a screen from the screen stack.
	 */
    public void removeScreen()
    {
        if (screenStack.top.next != null)
            screenStack.pop();
    }


	/****************************************************************
	 * Tells the current screen to update its data.  If the current
	 * screen has finished executing, change to a different screen.
	 */
    public void advance()
    {
        getScreen().advance();

        if (getScreen().isFinished())
            changeScreen();
    }


	/****************************************************************
	 * Tells the current screen to draw itself onto the input
	 * Graphics2D object.
	 */
    public void render(final Graphics2D g)
    {
        final BufferStrategy bufferStrategy = MHDisplayModeChooser.getBufferStrategy();

        if (bufferStrategy.contentsLost())
            return;

        getScreen().render(g);

        if (showFPS)
            renderStats(g);
        
        // TODO:  Scale image to fit screen dimensions

        bufferStrategy.show();
        //g.dispose();
    }

    
    private void renderStats(Graphics2D g)
    {
        int spacing = 25;
        int y = 30;
        renderText(g, "FPS: " + MHGame.getFramesPerSecond(), 20, y);
        y+=spacing;
        renderText(g, "UPS: " + MHGame.getUpdatesPerSecond(), 20, y);
    }

    
    private void renderText(Graphics2D g, String text, int x, int y)
    {
        x += MHDisplayModeChooser.DISPLAY_X;
        y += MHDisplayModeChooser.DISPLAY_Y;
        
        g.setFont(new Font("SansSerif", Font.BOLD, 20));
        
        g.setColor(Color.BLACK);
        g.drawString(text, x-1, y-1);
        g.drawString(text, x+1, y-1);
        g.drawString(text, x+1, y+1);
        g.drawString(text, x-1, y+1);
        g.setColor(Color.WHITE);
        g.drawString(text, x, y);
    }
    

	/****************************************************************
	 */
    public void keyTyped(final KeyEvent e)
    {
        getScreen().keyTyped(e);
    }


	/****************************************************************
	 */
    public void keyPressed(final KeyEvent e)
    {
        getScreen().keyPressed(e);
    }


	/****************************************************************
	 */
    public void keyReleased(final KeyEvent e)
    {
        if (e.getKeyCode() == KeyEvent.VK_F11)
            showFPS = !showFPS;
        
        getScreen().keyReleased(e);
    }


	/****************************************************************
	 */
    public void mousePressed(final MouseEvent e)
    {
        getScreen().mousePressed(e);
    }


	/****************************************************************
	 */
    public void mouseReleased(final MouseEvent e)
    {
        getScreen().mouseReleased(e);
    }


	/****************************************************************
	 */
    public void mouseMoved(final MouseEvent e)
    {
        getScreen().mouseMoved(e);
    }


	/****************************************************************
	 */
    public void mouseClicked(final MouseEvent e)
    {
        getScreen().mouseClicked(e);
    }


    public void mouseDragged(MouseEvent e)
    {
        getScreen().mouseDragged(e);
    }
}

//===================================================================
class MHFrameworkSplashScreen extends MHScreen
{
    private static final long DELAY = 3000L;
    private static final int FADE_RATE = 10;
    private final Image img;
    private long startTime;
    private int logoX, logoY;
    private int alpha = 255;
    private int state = 0;  // 0=fading in; 1=displaying; 2=fading out

    public MHFrameworkSplashScreen(final MHScreen defaultScreen)
    {

        //img = MHResourceManager.loadImage("../images/PoweredByMHFramework.jpg");
        img = MHResourceManager.loadImage("/mhframework/images/PoweredByMHFramework.jpg");

        this.setFinished(false);
        this.setDisposable(true);
        this.setNextScreen(defaultScreen);

        startTime = System.currentTimeMillis();
    }


    @Override
    public void advance()
    {
        //super.advance();

        final long time = System.currentTimeMillis() - startTime;

        logoX = MHDisplayModeChooser.getCenterX() - img.getWidth(null)/2;
        logoY = MHDisplayModeChooser.getCenterY() - img.getHeight(null)/2; 

        switch (state)
        {
            case 0:  alpha -= FADE_RATE;
                     if (alpha <= 0)
                     {
                         alpha = 0;
                         state = 1;
                     }
                     break;
            case 1:  if (time >= DELAY && MHMediaTracker.getInstance().doneLoading())
                        state = 2;
                     break;
            case 2:  alpha += FADE_RATE;
                     if (alpha > 255)
                     {
                         alpha = 255;
                         setFinished(true);
                     }
        }
    }

    @Override
    public void render(final Graphics2D g)
    {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, MHDisplayModeChooser.getScreenSize().width, MHDisplayModeChooser.getScreenSize().height);
        g.drawImage(img, logoX, logoY, null);
        int a = (alpha > 250 ? 255 : alpha);
        g.setColor(new Color(0, 0, 0, a));
        g.fillRect(0, 0, MHDisplayModeChooser.getScreenSize().width, MHDisplayModeChooser.getScreenSize().height);
    }


    @Override
    public void load()
    {
        state = 0;
        startTime = System.currentTimeMillis();
    }


    @Override
    public void unload()
    {
    }


    @Override
    public void actionPerformed(final ActionEvent arg0)
    {
        setFinished(true);
    }

}


/********************************************************************
 * Maintains a stack of game screens.
 *
 * <p>Class defined in MHScreenManager.java.
 */
class MHScreenStack
{
    public MHScreenStackNode top;


	/****************************************************************
	 * Constructor.
	 */
    public MHScreenStack(final MHScreen firstScreen)
    {
        push(firstScreen);
    }


	/****************************************************************
	 * Pushes a new node onto the screen stack.
	 */
    public void push(final MHScreen newScreen)
    {
        final MHScreenStackNode newNode = new MHScreenStackNode(newScreen);
        newNode.next = top;
        top = newNode;
    }


	/****************************************************************
	 * Pops a node from the screen stack.
	 */
    public void pop()
    {
        MHScreenStackNode temp = top;
        top = top.next;
        temp.next = null;
        temp = null;
    }


	/****************************************************************
	 * States whether the screen stack is empty.
	 */
    public boolean isEmpty()
    {
        if (top == null)
            return true;
        return false;
    }
}


/********************************************************************
 * A single node in an MHScreenStack object.
 *
 * <p>Class defined in MHScreenManager.java.
 */
class MHScreenStackNode
{
    MHScreen screen;
    MHScreenStackNode next;


	/****************************************************************
	 * Constructor.  Creates a new stack node containing the input
	 * screen object.
	 */
    public MHScreenStackNode (final MHScreen newScreen)
    {
        screen = newScreen;
        next = null;
    }
}

