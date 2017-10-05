package mhframework.gui;

import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import mhframework.MHDisplayModeChooser;
import mhframework.MHGameApplication;
import mhframework.MHVideoSettings;
import mhframework.media.MHFont;
import mhframework.io.*;


/********************************************************************
 * 
 * @author Michael Henson
 *
 */
public class MHGUIFileDialogScreen extends MHGUIDialogScreen
{
    private final File directory;
    private FileFilter fileFilter;
    private File selectedFile;
    private File[] fileList;
    private ArrayList<MHGUIButton> buttons;
    private boolean buttonsCreated = false;

    /**
     * Creates a dialog screen listing all files in given directory.
     *
     * @param directory
     */
    public MHGUIFileDialogScreen(final String directory)
    {
        this.directory = new File(directory);
    }

    /**
     * Creates a dialog screen listing files in the given directory
     * having the extension specified by fileType.
     *
     * @param directory
     * @param fileType
     */
    public MHGUIFileDialogScreen(final String directory, final String fileType)
    {
        this(directory);
        fileFilter = new MHFileFilter(fileType);
    }


    @Override
    public void load()
    {
        super.load();
        fileList = directory.listFiles(fileFilter);

        if (fileList.length == 0)
            setMessage("No files found.");
        else
            setMessage("Select a file, then click OK to accept.");
    }


    
    private int findLongestString(MHFont font, File[] fileList)
    {
        int buttonWidth = 0,
        fileNameWidth = 0;
        
        for (final File f: fileList)
        {
            fileNameWidth = font.stringWidth(f.getName());
            if (fileNameWidth > buttonWidth)
                buttonWidth = fileNameWidth;
        }

        return buttonWidth;
    }
    

    private void layoutButtons(final Graphics2D g)
    {
        int buttonWidth = 0,
        buttonHeight = 0;

        
        final int BUTTON_TOP = 150;
        int bx = 50;
        int by = BUTTON_TOP;
        //for (final File f:  fileList)
        for (int i = 0; i < fileList.length; i++)
        {
            File f = fileList[i];
            MHGUIButton button;
            
            if (!buttonsCreated)
            {
                if (buttons == null)
                    buttons = new ArrayList<MHGUIButton>();
                
                button = new MHGUIButton();
                buttons.add(button);
                add(button);
            }
            else
                button = buttons.get(i);

            // Find longest string for sizing buttons
            final MHFont font = button.getFont();
            buttonWidth = findLongestString(font, fileList);
            
            // Enlarge buttons slightly so there's a margin around the
            // file names.
            buttonWidth = (int)(buttonWidth * 1.25);
            buttonHeight = (int)(font.getHeight() * 1.25);

            button.setText(f.getName());
            button.setSize(buttonWidth, buttonHeight);
            button.setPosition(bx, by);
            button.addActionListener(this);
            by += buttonHeight+1;
            if (by > MHDisplayModeChooser.getHeight() * 0.8)
            {
                bx += buttonWidth+1;
                by = BUTTON_TOP;
            }
        }

        buttonsCreated = true;
    }


    @Override
    public void render(final Graphics2D g2d)
    {
        layoutButtons(g2d);

        super.render(g2d);
    }


    public File getSelectedFile()
    {
        return selectedFile;
    }


    @Override
    public void actionPerformed(final ActionEvent e)
    {
        if (e.getSource() == _btnCancel || e.getSource() == _btnOK)
            super.actionPerformed(e);
        else
        {
            final String fileName = ((MHGUIButton)e.getSource()).getCaptionText();
            this.selectedFile = new File(fileName);

            this.setMessage("Selected File:  " + selectedFile.getName());
        }
    }




    public static void main(final String args[])
    {
        final MHVideoSettings settings = new MHVideoSettings();
        settings.displayWidth = 1024;
        settings.displayHeight = 768;

        settings.bitDepth = 32;
        settings.fullScreen = true; //MHAppLauncher.showDialog(null);
        settings.windowCaption = "MHGUIFileDialogScreen Test Program";

        final MHGUIFileDialogScreen screen = new MHGUIFileDialogScreen("C:/Dev/mhframework0202/mhframework0202", ".java");

        new MHGameApplication(screen, settings);

        System.exit(0);
     }
}