package hu.emanuel.jeremi.antitower.message;

public class MessagePoint extends Message {

	public int x, y;
	
	public MessagePoint(String sender, String message, int id, int x, int y) {
		super(sender, message, id);
		this.x = x;
		this.y = y;
	}
	
	public MessagePoint(String sender, String message, int time, int id, int x, int y) {
		super(sender, message, time, id);
		this.x = x;
		this.y = y;
	}

}
