package hu.emanuel.jeremi.antitower;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.JFrame;

import hu.emanuel.jeremi.antitower.entity.EntityManager;
import hu.emanuel.jeremi.antitower.entity.Player;
import hu.emanuel.jeremi.antitower.graphic.Graphic;
import hu.emanuel.jeremi.antitower.graphic.TextureLibrary;
import hu.emanuel.jeremi.antitower.i18n.ResourceHandler;
import hu.emanuel.jeremi.antitower.message.helpmessage.Help;
import hu.emanuel.jeremi.antitower.save_load.TowHandler;
import hu.emanuel.jeremi.antitower.world.MapData;

public class Game extends JFrame implements Runnable, KeyListener {

    /**
     *
     */
    private static final long serialVersionUID = -5004007264137569546L;

    // <editor-fold defaultstate="collapsed" desc="variables, constants">
    public final boolean DEBUG = true;

    ResourceHandler resourceHandler;
    EntityManager manager;
    TextureLibrary texLib;
    MapData map;
    Help help;
    TowHandler saveLoadHandler;

    // Thread for the game.
    Thread gameThread;
    volatile boolean running = false;

    // The gameplay takes place on it.
    Graphic renderer;
    Player player;

    public static int planeWidth = 640, planeHeight = 480;
    public static boolean IS_FULLSCREEN = false;
    // </editor-fold>

    // transparent cursor
    private static final Cursor TRANSPARENT_CURSOR
            = Toolkit.getDefaultToolkit().createCustomCursor(
                    new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB),
                    new Point(0, 0),
                    "blank cursor");

    // Program constructor:
    /**
     * Setup: jframe and gameField It calls the function for filling the
     * sinTable, cosTable, ...etc. Hide cursor by make it equal with the
     * transparentCursor. It starts the gameThread.
     */
    @SuppressWarnings({"OverridableMethodCallInConstructor", "LeakingThisInConstructor"})
    public Game() {
        super("Fallen Towers v0.7");

        this.saveLoadHandler = new TowHandler();
        this.resourceHandler = new ResourceHandler("hu");

        // Thread for the game:
        gameThread = new Thread(this, "game_thread");

        player = new Player();

        help = new Help(resourceHandler);
        texLib = new TextureLibrary("sprites.png", "items.png", "mainframe.png", "winter.png", "office.png");

        manager = new EntityManager(player, planeWidth, planeHeight, resourceHandler, help, texLib, saveLoadHandler, renderer);

        // Adding listeners:
        addKeyListener(this);

        if (IS_FULLSCREEN) {
            // getting resolution, screensize
            planeWidth = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode().getWidth();
            planeHeight = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode().getHeight();

            // Adding JPanel (gameField):
            //setLayout(new FlowLayout());		
            renderer = new Graphic(planeWidth, planeHeight, player, manager);
            renderer.setLayout(null);
            renderer.setPreferredSize(new Dimension(planeWidth, planeHeight));
            add(renderer);

            // hidden (transparent) cursor
            getContentPane().setCursor(TRANSPARENT_CURSOR);
            // black background
            setBackground(Color.BLACK);
            renderer.setBackground(Color.BLACK);

            // fulscreen
            setLocationRelativeTo(null);
            setExtendedState(JFrame.MAXIMIZED_BOTH);
            setUndecorated(true);
            setResizable(false);
        } else {
            // Adding JPanel (gameField):
            setLayout(new FlowLayout());
            renderer = new Graphic(planeWidth, planeHeight, player, manager);
            renderer.setLayout(null);
            setMinimumSize(new Dimension(planeWidth + 20, planeHeight + 20));
            renderer.setPreferredSize(new Dimension(planeWidth, planeHeight));
            add(renderer);

            // not needed because the program uses ESC to exit
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // hidden (transparent) cursor
            getContentPane().setCursor(TRANSPARENT_CURSOR);
            // black background
            setBackground(Color.BLACK);

            // not fullscreen
            setLocationRelativeTo(null);
            setResizable(false);

            pack();
        }

        manager.renderer = renderer;

        setVisible(true);

        // Start gameThread:
        start();
    }

    // THREAD HANDLING
    public synchronized void start() {
        running = true;
        gameThread.start();
    }

    public synchronized void stop() {
        System.out.println("Stopping game_thread...");
        running = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void run() {
        // GAMELOOP

        long now;
        long lastTime;
        final long ns = 1000000000 / 60;
        requestFocus();

        long last = 0l;
        long now_fps;
        Queue<Long> times = new LinkedBlockingQueue<>();
        long delta;

        // GAMELOOP
        while (running) {
            now = System.nanoTime();
            lastTime = now;

            now_fps = System.currentTimeMillis();

            while (times.size() > 0 && times.peek() <= now_fps - 1000l) {
                times.poll();
            }
            times.add(now_fps);
            //System.out.println(times.size());

            delta = (now_fps - last) >> 4;
            manager.attackPlayer();
            player.update(manager, 1);
            renderer.castGraphic();
            if (player.SHOOTING) {
                renderer.renderBeam();
            }
            //renderer.paintImmediately(0, 0, planeWidth, planeHeight);
            renderer.repaint();
            /*
            try {
                Thread.sleep((long) ((lastTime - System.nanoTime() + ns) / 1000000));
            } catch (InterruptedException e) {
            }
            */
            last = now_fps;
        }

    }

    public void toggleFullScreen() {
        IS_FULLSCREEN = !IS_FULLSCREEN;

        if (IS_FULLSCREEN) {
            planeWidth = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode().getWidth();
            planeHeight = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode().getHeight();
        } else {
            planeWidth = 1024;
            planeHeight = 768;
        }

        setSize(planeWidth, planeHeight);
        renderer.setResolution(planeWidth, planeHeight);
        renderer.setSize(planeWidth, planeHeight);
        setLocationRelativeTo(null);
    }

    // KEYBOARD
    @Override
    public void keyTyped(KeyEvent e) {
        // TODO Auto-generated method stub		
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        player.keyPressed(e);

        if (code == KeyEvent.VK_F1) {
            renderer.IS_RAIN_ON = !renderer.IS_RAIN_ON;
        }
        if (code == KeyEvent.VK_F2) {
            renderer.IS_SHADERS_ON = !renderer.IS_SHADERS_ON;
        }
        if (code == KeyEvent.VK_F3) {
            renderer.IS_SPRITES_ON = !renderer.IS_SPRITES_ON;
        }
        if (code == KeyEvent.VK_F4) {
            renderer.IS_ANGLE_MARKER_ON = !renderer.IS_ANGLE_MARKER_ON;
        }
        
        
        if (code == KeyEvent.VK_H) {
            renderer.IS_HELP_ON = true;
        }
        if (code == KeyEvent.VK_ESCAPE) {
            System.exit(0);
        }
        if (code == KeyEvent.VK_F11) {
            toggleFullScreen();
        }
        if (code == KeyEvent.VK_F12) {
            renderer.takeScreenshot();
        }

        if (DEBUG) {
            if (code == KeyEvent.VK_F9) {
                manager.reloadActualLevel();
            }
            if (code == KeyEvent.VK_F10) {
                manager.chooseLevel();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_H) {
            renderer.IS_HELP_ON = false;
        }
        player.keyReleased(e);
    }
}
