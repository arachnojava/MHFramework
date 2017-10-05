package mhframework.ai.path;

import java.util.ArrayList;
import mhframework.tilemap.MHMapCellAddress;

public class MHNodePath
{
    private ArrayList<MHMapCellAddress> path;
    
    public MHNodePath()
    {
        path = new ArrayList<MHMapCellAddress>();
    }
    
    public MHMapCellAddress get(int index)
    {
        if (index < path.size())
            return path.get(index);
        
        return null;
    }
    
    public void add(MHMapCellAddress node)
    {
        path.add(node);
    }
    
    public int size()
    {
        return path.size();
    }

    public void add(int index, MHNode node)
    {
        path.add(index, node);
    }

    public MHNode remove(int index)
    {
        return (MHNode) path.remove(index);
    }
}
