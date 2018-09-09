package hu.emanuel.jeremi.antitower.message;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class MessageHandler {
	
	private long startTime;
	private long endTime;
	
	private Queue<Message> msgToPlay;
	private Message activeTemp;

	public MessageHandler(int maxAmountOfMessages) {
		msgToPlay = new ArrayBlockingQueue<>(maxAmountOfMessages);
	}
	
	public MessageHandler() {
		msgToPlay = new ArrayBlockingQueue<>(30);
	}
	
	public boolean addMessage(String sender, String msg, int id, int time) {
		if (msgToPlay.contains(new Message(sender,msg,id,time))) {
			return false;
		} else {
			msgToPlay.add(new Message(sender, msg, id, time));
			return true;
		}
	}
	
	public boolean addMessage(Message m) {
		if (msgToPlay.contains(m)) {
			return false;
		} else {
			msgToPlay.add(m);
			return true;
		}
	}
	
	public Message getMessage() {
		prepareMessage();
		return activeTemp;
	}
	
	private void prepareMessage() {
		if(activeTemp != null) {
			if( (startTime = System.currentTimeMillis()) >= endTime ) {
				activeTemp = null;
			}	
		}
		if(activeTemp == null) {
			if(msgToPlay.size() == 0) {
				return;
			} else {
				activeTemp = msgToPlay.poll();
				startTime = System.currentTimeMillis();
				endTime = startTime + activeTemp.getTime() * 1000;
			}
		}
	}

}
