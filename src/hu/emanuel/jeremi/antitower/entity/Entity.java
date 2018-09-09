package hu.emanuel.jeremi.antitower.entity;

public abstract class Entity {

	public int x, y, id;
	
	public Entity(int x, int y, int id) {
		this.x = x;
		this.y = y;
		this.id = id;
	}
	
	public Entity() {
		
	}

}
