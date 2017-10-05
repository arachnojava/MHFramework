package mhframework;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/********************************************************************
 * Requests initial configuration settings from the user.  Future
 * versions will also save settings to a configuration file.
 *
 * <p>Here is some sample code showing how to use this class
 * in collaboration with <tt>MHVideoSettings</tt>
 * to give users the option to select a video resolution.
<pre>
    MHVideoSettings settings = new MHVideoSettings();
    settings.fullScreen = MHAppLauncher.showDialog(null, true); // true = show resolution options
    settings.displayWidth = MHAppLauncher.getResolution().width;
    settings.displayHeight = MHAppLauncher.getResolution().height;
 </pre>
 * @author Michael Henson
 *
 */
public class MHAppLauncher
{
    /** Constant indicating that full screen mode was chosen. */
    public static final boolean FULL_SCREEN = true;

    /** Constant indicating that windowed mode was chosen. */
    public static final boolean WINDOWED    = false;

    private final static String strFullScreenPrompt = "Would you like to run this program in full-screen mode?";

    private static Dimension resolution;

    public static Dimension getResolution()
    {
        return resolution;
    }

    /****************************************************************
     * Displays a pre-launch dialog to ask for relevant startup
     * parameters.
     *
     * @param optionalParent (Optional) The window frame of the
     *                       application.
     *
     * @return FULL_SCREEN (true) if full-screen mode was chosen,
     *         WINDOWED (false) otherwise.  (See constants defined
     *         in this class.)
     */
    public static boolean showDialog(final JFrame optionalParent)
    {

        final int rc = JOptionPane.showConfirmDialog(optionalParent, strFullScreenPrompt, "MHFramework Application Launcher", JOptionPane.YES_NO_OPTION);

        if (rc == JOptionPane.YES_OPTION)
            return FULL_SCREEN;

        return WINDOWED;
    }

    public static boolean showDialog(final JFrame optionalParent, final boolean showResolutions)
    {
        if (!showResolutions)
            return showDialog(optionalParent);

        final JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        final JLabel lblFullScreen = new JLabel(strFullScreenPrompt);
        final JLabel lblResolution = new JLabel("Choose a video mode:");

        final JComboBox<MHResolutionListEntry> cboResolutions = new JComboBox<MHResolutionListEntry>();
        //cboResolutions.addItem(new MHResolutionListEntry("HDTV", 1920, 1280));
        cboResolutions.addItem(new MHResolutionListEntry("Widescreen (16:10)", 1280, 800));
        cboResolutions.addItem(new MHResolutionListEntry("Widescreen (16:9)", 1280, 720));
        cboResolutions.addItem(new MHResolutionListEntry("Standard High-Res", 1024, 768));
        cboResolutions.addItem(new MHResolutionListEntry("Standard Low-Res",  800, 600));
        cboResolutions.setEditable(false);
        cboResolutions.setSelectedIndex(2);
        cboResolutions.addActionListener(new MHResolutionListListener());

        setResolution(((MHResolutionListEntry)cboResolutions.getSelectedItem()).getResolution());

        panel.add(lblResolution, BorderLayout.NORTH);
        panel.add(cboResolutions, BorderLayout.CENTER);
        panel.add(lblFullScreen, BorderLayout.SOUTH);

        final int rc = JOptionPane.showConfirmDialog(optionalParent, panel, "MHFramework Application Launcher", JOptionPane.YES_NO_OPTION);

        if (rc == JOptionPane.YES_OPTION)
            return FULL_SCREEN;

        return WINDOWED;
    }


    public static void main(final String[] args)
    {
        showDialog(null, true);

        System.out.println(resolution.toString());

        System.exit(0);
    }

    public static void setResolution(final Dimension res)
    {
        resolution = res;
    }
}

class MHResolutionListListener implements ActionListener
{
    @SuppressWarnings("unchecked")
    public void actionPerformed(final ActionEvent e)
    {
        final JComboBox<MHResolutionListEntry> list = (JComboBox<MHResolutionListEntry>) e.getSource();
        final MHResolutionListEntry selected = (MHResolutionListEntry) list.getSelectedItem();

        MHAppLauncher.setResolution(selected.getResolution());
    }
}

class MHResolutionListEntry
{
    private final Dimension resolution;
    private final String description;

    public MHResolutionListEntry(final String desc, final int width, final int height)
    {
        description = desc;
        resolution = new Dimension(width, height);
    }

    public Dimension getResolution()
    {
        return resolution;
    }

    public String getDescription()
    {
        return description;
    }

    @Override
    public String toString()
    {
        return resolution.width + " x " + resolution.height + " (" + description + ")";
    }
}