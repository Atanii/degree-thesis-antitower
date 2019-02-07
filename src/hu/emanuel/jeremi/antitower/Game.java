package hu.emanuel.jeremi.antitower;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
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
import hu.emanuel.jeremi.antitower.save_load.TowHandler;
import hu.emanuel.jeremi.antitower.world.MapData;

public class Game extends JFrame implements Runnable, KeyListener {

    /**
     *
     */
    private static final long serialVersionUID = -5004007264137569546L;

    // <editor-fold defaultstate="collapsed" desc="variables, constants">
    public final boolean DEBUG = true;
    
    public enum GameState {
        MENU, GAME
    } GameState mode;

    ResourceHandler resourceHandler;
    EntityManager manager;
    TextureLibrary texLib;
    MapData map;
    TowHandler saveLoadHandler;

    // Thread for the game.
    Thread gameThread;
    volatile boolean running = false;

    // The gameplay takes place on it.
    Graphic renderer;
    Player player;

    public static int planeWidth = 640, planeHeight = 480;
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
        super("Fallen Towers v0.8.5");

        mode = GameState.MENU;
        
        this.resourceHandler = new ResourceHandler("hu");

        // Thread for the game:
        gameThread = new Thread(this, "game_thread");

        player = new Player();
        
        texLib = new TextureLibrary(
                "textures/sprites.png", 
                "textures/items.png", 
                "textures/mainframe.png", 
                "textures/winter.png", 
                "textures/office.png"
        );

        manager = new EntityManager(player, planeWidth, planeHeight, resourceHandler, texLib);
        this.saveLoadHandler = new TowHandler(manager);

        // Adding listeners:
        addKeyListener(this);

        // Adding JPanel (gameField):
        setLayout(new FlowLayout());
        renderer = new Graphic(planeWidth, planeHeight, player, manager);
        renderer.setLayout(null);
        setMinimumSize(new Dimension(planeWidth + 20, planeHeight + 20));
        renderer.setPreferredSize(new Dimension(planeWidth, planeHeight));
        add(renderer);

        // not needed because the program uses ESC to exit
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // setting hidden (transparent) cursor
        getContentPane().setCursor(TRANSPARENT_CURSOR);
        // black background
        setBackground(Color.BLACK);

        // not fullscreen
        setLocationRelativeTo(null);
        setResizable(false);

        pack();

        manager.setRenderer(renderer);
        manager.setTowHandler(saveLoadHandler);
        manager.setWeatherAndDayTime();
        manager.LoadLevel();
        renderer.updateMap();

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
        requestFocus();

        long last = 0l;
        long now_fps;
        Queue<Long> times = new LinkedBlockingQueue<>();
        long delta;

        // GAMELOOP
        while (running) {
            now_fps = System.currentTimeMillis();

            while (times.size() > 0 && times.peek() <= now_fps - 1000l) {
                times.poll();
            }
            times.add(now_fps);
            
            delta = (now_fps - last) >> 4;
            switch(mode) {
                case MENU:
                    renderer.repaint();
                    break;
                case GAME:
                    manager.attackPlayer();
                    player.update(manager, delta);
                    manager.checkGoalPoint();
                    renderer.updateWeather(delta);
                    renderer.castGraphic(delta);
                    /*{
                        try {
                            Thread.sleep(delta * 10);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }*/
                    break;
                default:
                    break;
            } // switch
            last = now_fps;
        } // while
    } // run

    // KEYBOARD
    @Override
    public void keyTyped(KeyEvent e) {
        // TODO Auto-generated method stub		
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        
        if(mode == GameState.MENU) {
            if (code == KeyEvent.VK_ESCAPE) {
                System.exit(0);
            }
            else if (code == KeyEvent.VK_SPACE) {
                mode = GameState.GAME;
                renderer.setState(mode);
            }
            return;
        }

        player.keyPressed(e);

        if (code == KeyEvent.VK_F1) {
            renderer.IS_WEATHER_ON = !renderer.IS_WEATHER_ON;
        }
        if (code == KeyEvent.VK_F2) {
            renderer.IS_SHADERS_ON = !renderer.IS_SHADERS_ON;
        }
        if (code == KeyEvent.VK_F3) {
            renderer.IS_SPRITES_ON = !renderer.IS_SPRITES_ON;
        }
        if (code == KeyEvent.VK_F4) {
            renderer.IS_STATUS_ON = !renderer.IS_STATUS_ON;
        }
        if (code == KeyEvent.VK_F5) {
            renderer.IS_SKYBOX_ON = !renderer.IS_SKYBOX_ON;
        }
        if (code == KeyEvent.VK_F6) {
            renderer.IS_RENDERING_WEAPON_ON = !renderer.IS_RENDERING_WEAPON_ON;
        }
        if (code == KeyEvent.VK_F7) {
            renderer.IS_ANGLE_MARKER_ON = !renderer.IS_ANGLE_MARKER_ON;
        }
        
        if (code == KeyEvent.VK_H) {
            renderer.IS_HELP_ON = true;
        }
        if (code == KeyEvent.VK_ESCAPE) {
            mode = GameState.MENU;
            renderer.setState(mode);
        }
        if (code == KeyEvent.VK_F12) {
            renderer.takeScreenshot();
        }

        if (DEBUG) {
            if (code == KeyEvent.VK_F9) {
                manager.reloadActualLevel();
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
