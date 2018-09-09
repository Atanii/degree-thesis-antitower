package hu.emanuel.jeremi.antitower.message;

import java.awt.Color;
import java.awt.Graphics;

public class MessageDisplayer {
	
	private MessageHandler msgh;
	
	public MessageDisplayer(MessageHandler msgh) {
		this.msgh = msgh;
	}
	
	public void showMessage(Graphics screen) {
		if(screen == null)
			return;
		if(msgh.getMessage() == null)
			return;
		screen.setColor(Color.yellow);
		screen.drawString(msgh.getMessage().toString(), 10, 20);
	}
	
}
