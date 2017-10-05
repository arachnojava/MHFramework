package mhframework.media;

import java.awt.Graphics2D;
import java.awt.Image;
import mhframework.MHRenderable;

/********************************************************************
 * 
 * 
 * @author Michael Henson
 *
 */
public abstract class MHParticleEmitter implements MHRenderable
{
    public static enum State
    {
        EMITTING,
        FADING,
        DORMANT;
    }
    private State state = State.EMITTING;


    public abstract void advance();
    public abstract void render(Graphics2D g);
    public abstract void render(Graphics2D g, int screenX, int screenY);
    public abstract Image getImage();  // TODO:  Is this method necessary here?  In specific subclasses only?
    public abstract void emit();
    
    
    public State getState()
    {
        return state;
    }

    
    public void setState(State s)
    {
        state = s;
    }
    
    
    public boolean isEmitting()
    {
        return state == State.EMITTING || state == State.FADING;
    }
    
    
    public boolean isDormant()
    {
        return state == State.DORMANT;
    }
}
