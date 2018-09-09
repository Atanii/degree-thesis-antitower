package hu.emanuel.jeremi.antitower.message.helpmessage;

import hu.emanuel.jeremi.antitower.i18n.ResourceHandler;

public class Help {

	String msg;
	
	public Help(ResourceHandler rr) {
		this.msg = 
				rr.get("move_forward") + "\n" +
				rr.get("move_backward") + "\n" +
				rr.get("move_left") + "\n" +
				rr.get("move_right") + "\n" +
				rr.get("turn_left") + "\n" +
				rr.get("turn_right") + "\n" +
				rr.get("shoot") + "\n" +
				rr.get("inventory_slots") + "\n" +
				rr.get("toggle_rain") + "\n" +
				rr.get("use") + "\n";
	}
	
	public String getHelp() {
		return this.msg;
	}
	
}