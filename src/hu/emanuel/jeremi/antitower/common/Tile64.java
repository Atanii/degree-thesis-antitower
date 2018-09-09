package hu.emanuel.jeremi.antitower.common;

public final class Tile64 {
	
	private Tile64() {}
	
	public static final int SIZE = 64;
	public static final int SIZE_LOG = 6;
	public static final int PLAYERHEIGHT = SIZE>>1;
	
	
	/*
	 * 0 - 9 -- special
	 * 
	 */
	
	public static final int INSIDE =  -1;
	public static final int OUTSIDE = -2;
	public static final int VIRTUAL = -3;
	
	public static final int I = INSIDE;
	public static final int O = OUTSIDE;
	//public static final int V = VIRTUAL;
	
	public static final int DOOR_CLOSED = 18;
	public static final int DOOR_OPENED = 19;

	public static final int ICONSIZE = SIZE>>1;
	
	public static final int START_TEXTURE_ID = 10;
	public static final int START_ID = 0;
	
}
