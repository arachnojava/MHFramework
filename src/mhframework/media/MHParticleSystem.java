package mhframework.media;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;
import mhframework.MHDisplayModeChooser;
import mhframework.MHGame;
import mhframework.MHGameApplication;
import mhframework.MHPoint;
import mhframework.MHRenderable;
import mhframework.MHScreen;
import mhframework.MHVideoSettings;
import mhframework.gui.MHGUIButton;

/********************************************************************
 * 
 * @author Michael Henson
 *
 */
public class MHParticleSystem implements MHRenderable
{
    private ArrayList<MHParticleEmitter> emitters;
    private int nextID = -1;
    
    private MHParticleSystem()
    {
        emitters = new ArrayList<MHParticleEmitter>();
    }

    public int createFireEmitter(int emitterX, int emitterY, int emitterWidth, int emitterHeight)
    {
        return addEmitter(new MHFireEmitter(emitterX, emitterY, emitterWidth, emitterHeight));
    }
    
    
    public int addEmitter(MHParticleEmitter newEmitter)
    {
        emitters.add(++nextID, newEmitter);
        return nextID;
    }
    
    
    @Override
    public void advance()
    {
        for (MHParticleEmitter e : emitters)
            if (!e.isDormant())
                e.advance();
    }

    @Override
    public void render(Graphics2D g)
    {
        for (MHParticleEmitter e : emitters)
            if (!e.isDormant())
                e.render(g);
    }
    
    
    
    public static void main(String args[])
    {
        MHVideoSettings s = new MHVideoSettings();
        s.showSplashScreen = false;
        s.fullScreen = false;
        s.displayWidth=640;
        s.displayHeight=480;
        new MHGameApplication(new TestScreen(), s);
    }

}


class MHFireEmitter extends MHParticleEmitter
{
    private Color[] palette = new Color[128];  //256];
    //private Color[] keyColors = new Color[] {Color.WHITE, Color.YELLOW, Color.ORANGE, Color.RED, new Color(0, 0, 0, 0)};
    //private Color[] keyColors = new Color[] {new Color(0, 0, 0, 0), Color.RED, Color.ORANGE, Color.YELLOW, Color.WHITE};
    private BufferedImage output;
    private int[][] fireMatrix;
    private MHPoint location;
    private Rectangle2D emitterBounds;
    private int countdown;
    
    public MHFireEmitter(int x, int y, int width, int height)
    {
        location = new MHPoint(x, y);
        emitterBounds = new Rectangle2D.Double(x, y, width, height);
        loadPalette();
        int rows = palette.length;
        int cols = width;
        initMatrix(rows, cols);
        output = new BufferedImage(cols, rows, BufferedImage.TYPE_INT_ARGB_PRE);
    }
    
    
    public void startEmitting()
    {
        setState(State.EMITTING);
    }
    

    public void stopEmitting()
    {
        if (getState() == State.EMITTING)
        {
            setState(State.FADING);
            countdown = palette.length;
        }
    }
    

    public void advance()
    {
        emit();
        for (int r = 0; r < (int)(fireMatrix.length-emitterBounds.getHeight()); r++)
            for (int c = 1; c < fireMatrix[r].length-1; c++)
            {
                int total;
                if (r < fireMatrix.length - 2)
                {
                    total = (fireMatrix[r+1][c-1] + fireMatrix[r+1][c] + fireMatrix[r+1][c+1] + fireMatrix[r+2][c]) - 1;
                    fireMatrix[r][c] = (total / 4);
                }
                else
                {
                    total = (fireMatrix[r+1][c-1] + fireMatrix[r+1][c] + fireMatrix[r+1][c+1]) -1;
                    fireMatrix[r][c] = (total / 3);
                }
            }
        
        for (int r = 0; r < fireMatrix.length-1; r++)
            for (int c = 1; c < fireMatrix[r].length-1; c++)
                output.setRGB(c, r, palette[fireMatrix[r][c]].getRGB());
    }

    
    public Image getImage()
    {
        return output;
    }
    
    public void render(Graphics2D parameter)
    {
        parameter.drawImage(output, (int)location.getX(), (int)location.getY()-output.getHeight(), null);
        
        // DEBUG
        //parameter.drawImage(output, (int)location.getX(), (int)location.getY()-output.getHeight(), null);
        //for (int i = 0; i < palette.length; i++)
        //{
        //    parameter.setColor(palette[i]);
        //    parameter.fillRect(20 + i*3, 80, 3, 30);
        //}
    }
    
    public void emit()
    {
        if (isDormant()) return;
        
        Random rand = new Random();
        for (int r = (int)(fireMatrix.length - emitterBounds.getHeight()); 
                 r <= fireMatrix.length-1; r++)
            for (int c = 0; c < fireMatrix[r].length; c++)
            {
                switch (getState())
                {
                    case EMITTING:
                        if (rand.nextInt(2) == 0)
                            fireMatrix[r][c] = rand.nextInt(palette.length/10);
                        else
                            fireMatrix[r][c] = palette.length-1;
                        break;
                    case FADING:
                        fireMatrix[r][c] = 0;
                        break;
                }
            }
        
        if (getState() == State.FADING)
        {
            countdown--;
            if (countdown < 0)
                setState(State.DORMANT);
        }
    }
    
    
    private void initMatrix(int rows, int cols)
    {
        fireMatrix = new int[rows][cols];
        for (int r = 0; r < fireMatrix.length; r++)
            for (int c = 0; c < fireMatrix[r].length; c++)
                fireMatrix[r][c] = 0;
    }
    
    private void loadPalette()
    {
        Image paletteImage = MHResourceManager.loadImage("FirePalette.gif");
        int width = paletteImage.getWidth(null);
        int height = paletteImage.getHeight(null);
        BufferedImage pi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        pi.getGraphics().drawImage(paletteImage, 0, 0, null);

        int color = 0;
        for (int index = 0; index < palette.length; index++)
        {
            palette[index] = new Color(pi.getRGB(color, 0));
            color += 2; // palette.length / width;
        }
        
        // Increase alpha on darker colors.
        for (int a = 1; a < palette.length/2; a++)
        {
            int r = palette[a].getRed();
            int g = palette[a].getGreen();
            int b = palette[a].getBlue();
            palette[a] = new Color(r,g,b,a*2);
        }
        palette[0] = new Color(0,0,0,0);
    }


    @Override
    public void render(Graphics2D g, int screenX, int screenY)
    {
        // TODO Auto-generated method stub
        
    }

/*
 Old implementation:
        int numValues = palette.length / (keyColors.length-1);

        for (int col = 0; col < keyColors.length-1; col++)
        {
            Color start = keyColors[col];
            Color end = keyColors[col+1];
            int dr = (end.getRed() - start.getRed()) / numValues;
            int dg = (end.getGreen() - start.getGreen()) / numValues;
            int db = (end.getBlue() - start.getBlue()) / numValues;
            int da = (end.getAlpha() - start.getAlpha()) / numValues;

            palette[col * numValues] = keyColors[col];
            for (int p = (col * numValues); p < ((col+1) * numValues)-1; p++)
                palette[p+1] = new Color(palette[p].getRed() + dr, palette[p].getGreen() + dg, palette[p].getBlue() + db, palette[p].getAlpha() + da); 
        }
        palette[palette.length-1] = keyColors[keyColors.length-1];

 */
}




class TestScreen extends MHScreen
{
    int emitterW = 100;
    int spacing = 150;
    MHFireEmitter[] emitter;
    Image img;
    boolean fire = true;
    MHGUIButton button;
    
    public TestScreen()
    {
        img = MHResourceManager.loadImage("/mhframework/images/PoweredByMHFramework.jpg");

        button = new MHGUIButton();
        button.setText("Extinguish");
        button.setPosition(20, 40);
        button.setSize(100, 20);
        button.addActionListener(this);
        add(button);
        
        emitter = new MHFireEmitter[5];
        
        emitter[0] = new MHFireEmitter(100, 479, 640, 1);
        //for (int i = 0; i < emitter.length; i++)
        //    emitter[i] = new MHFireEmitter(50+spacing*i, 550, emitterW, i+1);
    }
    
    
    public void advance()
    {
        //for (int i = 0; i < emitter.length; i++)
            emitter[0].advance();
    }
    
    
    public void render(Graphics2D g)
    {
        int y = MHDisplayModeChooser.getHeight() - emitter[0].getImage().getHeight(null);
        
        fill(g, Color.BLACK);
        g.drawImage(img, 0, 0, 640, 480, null);
        //for (int i = 0; i < emitter.length; i++)
        //    emitter[i].render(g);

        // Unmodified fire image:
        g.drawImage(emitter[0].getImage(), 0, y, null);
        
        // Stretched vertically:
        //g.drawImage(emitter[0].getImage(), 0, 0, 640, 480, null);
        
//        g.drawImage(emitter[0].getImage(), 0, 0, 120, 480, null);
//        g.drawImage(emitter[0].getImage(), 120, 0, 120, 480, null);
//        g.drawImage(emitter[0].getImage(), 240, 0, 120, 480, null);
//        g.drawImage(emitter[0].getImage(), 360, 0, 120, 480, null);
//        g.drawImage(emitter[0].getImage(), 480, 0, 120, 480, null);
//        g.drawImage(emitter[0].getImage(), 600, 0, 120, 480, null);
        
        super.render(g);
    }
    
    
    @Override
    public void actionPerformed(ActionEvent arg0)
    {
        fire = !fire;
        if (fire)
        {
            emitter[0].startEmitting();
            button.setText("Extinguish");
        }
        else
        {
            emitter[0].stopEmitting();
            button.setText("Ignite");
        }
    }

    
    @Override
    public void load()
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void unload()
    {
        // TODO Auto-generated method stub
        
    }


    @Override
    public void keyReleased(KeyEvent e)
    {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
            MHGame.setProgramOver(true);
            
        super.keyReleased(e);
    }
}