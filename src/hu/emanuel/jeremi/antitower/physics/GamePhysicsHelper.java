package hu.emanuel.jeremi.antitower.physics;

import static hu.emanuel.jeremi.antitower.common.Tile64.*;

import hu.emanuel.jeremi.antitower.entity.EntityManager;

public final class GamePhysicsHelper {

    private GamePhysicsHelper() {
        // Utility class...cannot make instances.
    }

    /**
     * log2(n)
     *
     * @param n
     * @return logarithm (base 2) of n -- round down
     */
    public static final int log2(final int n) {
        return (int) Math.floor(Math.log(n) / Math.log(2));
    }

    /**
     * It converts a (double) degree into (double) radian. It uses ANGLE180 as
     * 180°.
     *
     * @param degrees
     * @param custom180degree
     * @return radian value
     */
    public static final double toCustomRad(final double degrees, final int custom180degree) {
        return (degrees * Math.PI / (float) custom180degree) + .0001;
    }

    /**
     * It converts a (float) degree into (float) radian. It uses ANGLE180 as
     * 180°.
     *
     * @param degrees
     * @param custom180degree
     * @return radian value
     */
    public static final float toCustomRad(final float degrees, final int custom180degree) {
        return ((float) (degrees * Math.PI) / (float) custom180degree) + .0001f;
    }

    /**
     * It converts a (float) degree into (float) radian.
     *
     * @param degrees
     * @return radian value
     */
    public static final float toRad(final float degrees) {
        return (float) ((degrees * Math.PI / (float) 180) + .0001f);
    }

    /**
     * Shoot and trace a single ray from the P(startX,startY) coordinate along
     * the line determined by the angle until it hits an Enemy object.
     *
     * @param maxDistance
     * @param startX
     * @param startY
     * @param cos
     * @param sin
     * @param angle
     */
    /*
	public static final void traceBeamTillHitsEnemy
	(
			final int maxDistance, float startX, float startY, final float cos[], final float sin[], final int angle
	) {
		for(int i = 0; i < maxDistance; i++) {
			startX += cos[angle] * 5;
			startY += sin[angle] * 5;
			if( EntityHandler.isThereAnEnemyThenHitIt( (int)Math.floor(startX/SIZE), (int)Math.floor(startY/SIZE) ) ) {
				return;
			}
		}
	}
     */
    /**
     * Shoot and trace a single ray from the P(startX,startY) coordinate along
     * the line determined by the angle until it hits an Enemy object.
     *
     * @param maxDistance
     * @param startX
     * @param startY
     * @param cos
     * @param sin
     * @param angle
     * @param en
     */
    public static final void traceBeamTillHitsEnemy(
            final int maxDistance, float startX, float startY,
            final float cos[], final float sin[], final int angle,
            final EntityManager en
    ) {
        for (int i = 0; i < maxDistance; i++) {
            startX += cos[angle] * 5;
            startY += sin[angle] * 5;
            if (en.isThereAnEnemyThenHitIt((int) Math.floor(startX / SIZE), (int) Math.floor(startY / SIZE))) {
                return;
            }
        }
    }

    /**
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param FOV
     * @return angle between two points
     */
    public static final float rotatePlayerAngleToTarget(float targetX, float targetY, float originX, float originY, int originAngle, int planeWidth, int FOV) {
        float theta = (float) (Math.atan2(targetY - originY, targetX - originX) * 180 / Math.PI);
        if (theta < 0) {
            theta += 360;
        }
        return theta * ((float) planeWidth / (float) 60);
    }

    /**
     *
     * @param mapX1
     * @param mapY1
     * @param x2
     * @param y2
     * @return distance between the two points
     */
    public static float getDistance(final int mapX1, final int mapY1, final int x2, final int y2) {
        return (float) (Math.sqrt(
                (((mapX1 << SIZE_LOG) + (SIZE >> 1)) - x2)
                * (((mapX1 << SIZE_LOG) + (SIZE >> 1)) - x2)
                + (((mapY1 << SIZE_LOG) + (SIZE >> 1)) - y2)
                * (((mapY1 << SIZE_LOG) + (SIZE >> 1)) - y2)
        ));
    }

}


/*
public static boolean attackPlayer() {
		
		float c, b, a, sinalpha, cosbeta;
		float sloap = .0f;
		int tempX = 0, tempY = 0;
		int angle = 0;
		int sight = 200;
		for(Enemy en : enemiesInMap) {
			if( !en.destroyed ) {
				tempX = en.x * SIZE + (SIZE>>1);
				tempY = en.y * SIZE + (SIZE>>1);
				
				c = GamePhysicsHelper.getDistance(en.x, en.y, player.x, player.y);
				b = tempX - player.x;
				a = tempY - player.y;
				
				angle = (int)Math.floor(GamePhysicsHelper.rotatePlayerAngleToTarget( tempX, tempY, player.x, player.y,player.angle));
				
				//player.angle = angle;
				
				//System.out.println("sin:"+sinalpha+"|cos:"+cosbeta);
				
				tempX = player.x;
				tempY = player.y;
				
				
				//System.out.println("tower x:"+tempX+"|tower y:"+tempY);
				
				for(int i = 0; i < sight; i++) {
					
					tempX += Math.cos(GamePhysicsHelper.toCustomRad(angle, 640*3)) * 10;
					tempY += Math.sin(GamePhysicsHelper.toCustomRad(angle, 640*3)) * 10;
					//player.x = (int)tempX;
					//player.y = (int)tempY;
					
					//System.out.println("==========");
					//System.out.println("angle:"+angle+"\npx:"+player.x/64+"|py:"+player.y/64+"\nbeamx:"+tempX/64+"|beamy:"+tempY/64);
					//System.out.println("player angle:"+player.angle+"|modified angle:"+angle);
					//System.out.println("angle:"+angle+"\ntx:"+en.x+"|ty:"+en.y+"\nbeamx:"+tempX/64+"|beamy:"+tempY/64);
					//System.out.println("==========");
					
					if(mapToProcess[(int)(Math.floor(tempY/SIZE)*mapWidth+Math.floor(tempX/SIZE))] != INSIDE && 
					   mapToProcess[(int)(Math.floor(tempY/SIZE)*mapWidth+Math.floor(tempX/SIZE))] != OUTSIDE) {
								return false;
							}
					if( (int)Math.floor(tempX/SIZE) == en.x && 
						(int)Math.floor(tempY/SIZE) == en.y ) {
						System.out.println("player's hit");
						return true;
					}
					
					tempX = (float) Math.floor(tempX/SIZE);
					tempY = (float) Math.floor(tempY/SIZE);
					
					
					if(mapToProcess[(int)(tempY*mapWidth+tempX)] != INSIDE && 
					   mapToProcess[(int)(tempY*mapWidth+tempX)] != OUTSIDE) {
						return false;
					}
					
					
					if( (int)Math.floor(tempX/SIZE) == (int)Math.floor(player.x/SIZE) && 
						(int)Math.floor(tempY/SIZE) == (int)Math.floor(player.y/SIZE) ) {
						System.out.println("player's hit");
						return true;
					}
					
				}
			}
		}
		
		return false;
	}
 */

 /*
 	public final void traceBeam() {
		int sight = 100;
		//for(Enemy e i)
		int beamX = player.x, beamY = player.y;
		//System.out.println( (player.x/64) +"|"+ (player.y/64) );
		for(int i = 0; i < sight; i++) {
			beamX += cosTable[player.angle] * 10;
			beamY += sinTable[player.angle] * 10;
			if( EntityHandler.isThereAnEnemyThenHitIt(beamX/64, beamY/64) ) {
				return;
			}
		}
	}
 */
/**
 * JPanel for the game. All the graphics will be drawn onto it.
 *
 * @author Kádár Jeremi Emánuel
 *
 */
/*
class MyPanel extends JPanel {
	
	MyPanel() {
		super();
		frame = new BufferedImage(planeWidth,planeHeight,BufferedImage.TYPE_INT_ARGB);
		g = frame.getGraphics();
		this.setIgnoreRepaint(true);
	}
	
	public void render() {
		BufferStrategy bs = getBufferStrategy();
		if(bs == null) {
			createBufferStrategy(3);
			return;
		}
		Graphics g2 = bs.getDrawGraphics();
		g2.drawImage(frame, 0, 0, this);
		bs.show();
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);		
		g.drawImage(frame, 0, 0, this);
		//g.drawImage(hud.hud.texture,(planeWidth>>1)-(hud.hud.texture.getWidth()>>2), planeHeight-hud.hud.texture.getHeight(),null);
	}
}
 */
