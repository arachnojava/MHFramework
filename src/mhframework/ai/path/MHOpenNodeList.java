package mhframework.ai.path;

public class MHOpenNodeList extends MHClosedNodeList
{
    public MHOpenNodeList()
    {
        super();
    }


    public MHOpenNodeList(MHNode node)
    {
        super(node);
    }


    public void add(MHNode node)
    /*
     * Override add() so the list is built in decreasing order by score.
     */
    {
        double newScore = node.getScore();
        MHNode entry;
        for (int i = 0; i < size(); i++)
        {
            entry = (MHNode) get(i);
            if (newScore <= entry.getScore())
            {
                add(i, node);
                return;
            }
        }
        super.add(node); // add node at end of list
    } // end of add()


    public MHNode removeFirst()
    {
        return (MHNode) remove(0);
    }

} // end of TilesPriQueue
