package mhframework.gui;

import java.awt.Image;
import java.awt.event.ActionEvent;

public class MHGUICommandButton extends MHGUIButton
{
    private MHCommand command;
    
    public MHGUICommandButton(Image normal, Image down,
            Image over, MHCommand cmd)
    {
        super(normal, down, over);
        addActionListener(this);
        setCommand(cmd);
    }


    public void setCommand(MHCommand c)
    {
        command = c;
    }
    
    
    @Override
    public void actionPerformed(ActionEvent e)
    {
        command.execute();
    }

}
