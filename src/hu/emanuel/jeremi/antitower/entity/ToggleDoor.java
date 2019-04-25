package hu.emanuel.jeremi.antitower.entity;

import hu.emanuel.jeremi.antitower.effect.Sound;

public class ToggleDoor {

    public int x, y;
    private final int key;
    public boolean isClosed;
    public int closed, opened;

    public ToggleDoor(int closed, int opened, int x, int y, int key, boolean isClosed) {
        this.x = x;
        this.y = y;
        this.closed = closed;
        this.opened = opened;
        this.key = key;
        this.isClosed = isClosed;
    }

    public void toggle() {
        isClosed = !isClosed;
        (new Sound("sound/door.wav")).play();
    }

    public int getActualTexture() {
        return isClosed ? closed : opened;
    }

    public boolean interactWithPlayer(int key_card_id) {
        if (this.key == key_card_id) {
            toggle();
            return true;
        } else {
            return false;
        }
    }

    public int getMapX() {
        return x;
    }

    public int getMapY() {
        return y;
    }

}
