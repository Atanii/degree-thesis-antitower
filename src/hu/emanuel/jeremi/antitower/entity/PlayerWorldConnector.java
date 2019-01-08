package hu.emanuel.jeremi.antitower.entity;

import hu.emanuel.jeremi.antitower.graphic.Graphic;

public interface PlayerWorldConnector {

	public void synchSkyboxWithRotation(boolean left, int rotateSpeed);
	
	public float[] getSinTable();
	public float[] getCosTable();
	
	public boolean isOutside(int worldX, int worldY);
	public boolean isCollision(int playerx, int playery, float dx, float dy);
	
	public Graphic g(); 
	
	public void shootTowards();
	
	public void setOutSide(boolean isOutside);
	
	public void makenteractWithClosestInteractive();
	
	public void synchGraphWithData(int x, int y, int angle, int FOV, int playerPaneDist);

	public void updateDistBetweenndSprites();

	public void checkAssumableCollision();

	public EntityManager getManager();

	public void handleShoot(int x, int y, int angle);

	public void updateRendererPlayerReference(int x, int y, int angle, int fOV, int playerPaneDist);
	
}
