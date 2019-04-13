package hu.emanuel.jeremi.antitower.message;

import java.util.Objects;

public class Message {

    private final int id;
    private final String sender;
    private final String message;
    private final int timeInSecundum;

    public Message(String sender, String message, int id) {
        this.sender = sender;
        this.message = message;
        this.timeInSecundum = 5;
        this.id = id;
    }

    public Message(String sender, String message, int timeInSecundum, int id) {
        this.sender = sender;
        this.message = message;
        this.timeInSecundum = timeInSecundum;
        this.id = id;
    }

    @Override
    public String toString() {
        return sender + " : " + message;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Message) {
            return this.sender.equals(((Message) other).getSender()) && this.message.equals(((Message) other).getMessage());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 19 * hash + Objects.hashCode(this.sender);
        hash = 19 * hash + Objects.hashCode(this.message);
        return hash;
    }

    public String getSender() {
        return this.sender;
    }

    public String getMessage() {
        return this.message;
    }

    public int getTime() {
        return timeInSecundum;
    }

    public int getId() {
        return id;
    }

}
