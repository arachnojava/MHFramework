package mhframework.ai.state;

/********************************************************************
 * 
 * @author Michael
 *
 */
public class MHFiniteStateMachine
{
    private MHState currentState;
    private Object subject;
   
    /****************************************************************
     * 
     * @param subject The object whose state is handled by this FSM.
     */
    public MHFiniteStateMachine(Object subject)
    {
        this.subject = subject;
        currentState = new NullState();
    }
    
    /****************************************************************
     * 
     * @return
     */
    public MHState getState()
    {
        return currentState;
    }
    
    /****************************************************************
     * 
     * @param nextState
     */
    public void changeState(MHState nextState)
    {
        currentState.exit(subject);
        currentState = nextState;
        currentState.enter(subject);
    }
    
    /****************************************************************
     * 
     */
    public void execute()
    {
        currentState.execute(subject);
    }
    
    /****************************************************************
     */
    private class NullState implements MHState
    {
        @Override
        public void enter(Object subject)
        {
        }

        @Override
        public void execute(Object subject)
        {
        }

        @Override
        public void exit(Object subject)
        {
        }
    }
}
