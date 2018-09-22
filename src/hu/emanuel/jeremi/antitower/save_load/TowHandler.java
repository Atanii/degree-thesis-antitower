package hu.emanuel.jeremi.antitower.save_load;

import hu.emanuel.jeremi.antitower.entity.Enemy;
import hu.emanuel.jeremi.antitower.entity.Enemy.EnemyType;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;

import hu.emanuel.jeremi.antitower.entity.EntityManager;
import hu.emanuel.jeremi.antitower.entity.Sprite;
import hu.emanuel.jeremi.antitower.entity.ToggleDoor;
import hu.emanuel.jeremi.antitower.entity.item.AssumableItem;
import hu.emanuel.jeremi.antitower.entity.item.ItemType;

/**
 *
 * This class loads the levels from the .tow files.
 *
 * @author Jeremi
 *
 */
public final class TowHandler {

    public EntityManager manager;

    private class BlankMapException extends Exception {
    } // just for the exception itself

    public TowHandler(EntityManager gui) {
        this.manager = gui;
    }

    public TowHandler() {
    }

    private void LoadData(Scanner sc, String PATH) throws BlankMapException {
        System.out.println("<<< LOADING: " + PATH + " >>>");

        int current;
        StringTokenizer tokenizer;
        sc.useDelimiter(";");

        sc.nextLine();

        // MAP VERSION
        tokenizer = new StringTokenizer(sc.next(), ",");
        System.out.println("<<< Map version: v" + Integer.parseInt(tokenizer.nextToken()) + " >>>");

        sc.nextLine();
        sc.nextLine();

        // PLAYER DATA
        tokenizer = new StringTokenizer(sc.next(), ",");
        manager.playerX = Integer.parseInt(tokenizer.nextToken());
        manager.playerY = Integer.parseInt(tokenizer.nextToken());
        System.out.println("<<< Player X: " + manager.playerX + " | " + "Y: " + manager.playerY + " >>>");

        sc.nextLine();
        sc.nextLine();

        // USED TEXTURE PACK
        tokenizer = new StringTokenizer(sc.next(), ",");
        manager.map.pack = Integer.parseInt(tokenizer.nextToken());
        System.out.println("<<< Texture pack: " + manager.map.pack + " >>>");

        sc.nextLine();
        sc.nextLine();

        // MAP SIZE (in gridcount)
        tokenizer = new StringTokenizer(sc.next(), ",");
        manager.map.width = Integer.parseInt(tokenizer.nextToken());
        manager.map.height = Integer.parseInt(tokenizer.nextToken());
        System.out.println("<<< Width: " + manager.map.width + " | " + "Height: " + manager.map.height + " >>>");

        if (manager.map.width * manager.map.height == 0) {
            throw new BlankMapException();
        }

        sc.nextLine();
        sc.nextLine();

        // CEILING
        tokenizer = new StringTokenizer(sc.next(), ",");
        manager.map.ceiling = Integer.parseInt(tokenizer.nextToken());
        System.out.println("<<< Ceiling: " + manager.map.ceiling + " >>>");

        sc.nextLine();
        sc.nextLine();

        // MAP CELLS
        // x y inside fw height virtual storey //
        // int x, int y, int fw, int height, int inside, int virtual, int storey
        // int oneDindex = (row * length_of_row) + column; // Indexes
        int size = manager.map.width * manager.map.height;

        manager.map.texMap = new int[size];
        manager.map.heightMap = new int[size];
        manager.map.insideMap = new int[size];
        int virtualMap[] = new int[size];

        for (int i = 0; i < size; i++) {
            tokenizer = new StringTokenizer(sc.next(), ",");

            int x, y;

            x = Integer.parseInt(tokenizer.nextToken());
            y = Integer.parseInt(tokenizer.nextToken());

            manager.map.texMap[(y * manager.map.width) + x] = Integer.parseInt(tokenizer.nextToken());
            manager.map.heightMap[(y * manager.map.width) + x] = Integer.parseInt(tokenizer.nextToken());
            manager.map.insideMap[(y * manager.map.width) + x] = Integer.parseInt(tokenizer.nextToken());
            virtualMap[(y * manager.map.width) + x] = Integer.parseInt(tokenizer.nextToken());
            Integer.parseInt(tokenizer.nextToken());
        }

        sc.nextLine();
        sc.nextLine();

        // DOORS
        // x y closed opened id value //
        tokenizer = new StringTokenizer(sc.next(), ",");
        current = Integer.parseInt(tokenizer.nextToken());
        System.out.println("<<< Doors: " + current + " >>>");

        manager.doors = new ToggleDoor[current];
        manager.map.doors = new ToggleDoor[current];

        if (current > 0) {
            sc.nextLine();
            tokenizer = new StringTokenizer(sc.next(), ",");

            manager.map.closedDoor = Integer.parseInt(tokenizer.nextToken());
            manager.map.openedDoor = Integer.parseInt(tokenizer.nextToken());

            for (int i = 0; i < current; i++) {
                tokenizer = new StringTokenizer(sc.next(), ",");

                int x = Integer.parseInt(tokenizer.nextToken());
                int y = Integer.parseInt(tokenizer.nextToken());
                int id = Integer.parseInt(tokenizer.nextToken());
                int key = Integer.parseInt(tokenizer.nextToken());

                manager.doors[i] = new ToggleDoor(
                        manager.map.closedDoor,
                        manager.map.openedDoor,
                        x, y,
                        key,
                        true
                );
            }
        }

        sc.nextLine();
        sc.nextLine();

        // SPRITES
        // number; texture-id, x, y, id //
        tokenizer = new StringTokenizer(sc.next(), ",");
        current = Integer.parseInt(tokenizer.nextToken());

        manager.sprites = new Sprite[current];

        if (current > 0) {
            sc.nextLine();
            for (int i = 0; i < current; i++) {
                tokenizer = new StringTokenizer(sc.next(), ",");

                int x = Integer.parseInt(tokenizer.nextToken());
                int y = Integer.parseInt(tokenizer.nextToken());
                int tex = Integer.parseInt(tokenizer.nextToken());
                int id = Integer.parseInt(tokenizer.nextToken());

                manager.sprites[i] = new Sprite(
                        tex, x, y, id
                );
            }
        }

        sc.nextLine();
        sc.nextLine();

        // ENEMIES
        // x y tile id type //
        tokenizer = new StringTokenizer(sc.next(), ",");
        current = Integer.parseInt(tokenizer.nextToken());

        manager.enemies = new Enemy[current];

        if (current > 0) {
            sc.nextLine();
            for (int i = 0; i < current; i++) {
                tokenizer = new StringTokenizer(sc.next(), ",");

                int x = Integer.parseInt(tokenizer.nextToken());
                int y = Integer.parseInt(tokenizer.nextToken());
                int id = Integer.parseInt(tokenizer.nextToken());
                int type = Integer.parseInt(tokenizer.nextToken());

                manager.enemies[i] = new Enemy(
                        id, x, y, true, EnemyType.values()[type], manager.texLib
                );
            }
        }

        sc.nextLine();
        sc.nextLine();

        // ITEMS
        // x y tile id value type //
        tokenizer = new StringTokenizer(sc.next(), ",");
        current = Integer.parseInt(tokenizer.nextToken());

        manager.assumables = new AssumableItem[current];

        if (current > 0) {
            sc.nextLine();
            for (int i = 0; i < current; i++) {
                tokenizer = new StringTokenizer(sc.next(), ",");

                ItemType type = ItemType.values()[Integer.parseInt(tokenizer.nextToken()) - 3];
                int id = Integer.parseInt(tokenizer.nextToken());
                int value = Integer.parseInt(tokenizer.nextToken());
                int x = Integer.parseInt(tokenizer.nextToken());
                int y = Integer.parseInt(tokenizer.nextToken());

                manager.assumables[i] = new AssumableItem(
                        type, id, value, x, y
                );
            }
        }

        sc.nextLine();
        sc.nextLine();

        // MESSAGES
        // x y time id sendercode stringCode //
        tokenizer = new StringTokenizer(sc.next(), ",");
        current = Integer.parseInt(tokenizer.nextToken());
        if (current > 0) {
            sc.nextLine();
            for (int i = 0; i < current; i++) {
                tokenizer = new StringTokenizer(sc.next(), ",");
                /*
                            manager.msgh.add( new MessagePoint(
                                    Integer.parseInt(tokenizer.nextToken()),
                                    Integer.parseInt(tokenizer.nextToken()),
                                    Integer.parseInt(tokenizer.nextToken()),
                                    Integer.parseInt(tokenizer.nextToken()),
                                    Integer.parseInt(tokenizer.nextToken()),
                                    Integer.parseInt(tokenizer.nextToken())
                            ));
                 */
            }
        }

        System.out.println("<<< LOADED: " + PATH + " >>>");
    }

    public void LoadLevel(final String PATH, boolean fromResource) {
        // <editor-fold defaultstate="collapsed" desc="Map loaded from resource.">
        if (fromResource) {
            try (
                    Scanner sc = new Scanner(getClass().getResource("/res/" + PATH).openStream());) {
                this.LoadData(sc, PATH);
            } catch (FileNotFoundException e) {
                JOptionPane.showMessageDialog(null, "File not found.");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (BlankMapException e) {
                JOptionPane.showMessageDialog(null, "Invalid map file! There are no map cells to load.");
            }
            // </editor-fold>
        } else {
            // <editor-fold defaultstate="collapsed" desc="Map loaded from out of scope.">
            try (
                    Scanner sc = new Scanner(Paths.get(PATH));) {
                this.LoadData(sc, PATH);
            } catch (FileNotFoundException e) {
                JOptionPane.showMessageDialog(null, "File not found.");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (BlankMapException e) {
                JOptionPane.showMessageDialog(null, "Invalid map file! There are no map cells to load.");
            }
        }
        // </editor-fold>
    }

}
