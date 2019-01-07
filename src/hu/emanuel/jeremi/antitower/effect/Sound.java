package hu.emanuel.jeremi.antitower.effect;

// importing important 

import java.io.IOException;
import java.net.MalformedURLException;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Simple class playing sound.
 * 
 * @author K�d�r Jeremi Em�nuel
 *
 */
public class Sound {
	
	public final static String PATH_TO_RESOURCES = "/res/";
	
	/*
	 * Two ways:
	 *  - Clip
	 * 	- SourceDataLine
	 * 
	 * I have already the whole audio-file, furthermore I want to make it loop continuously, so
	 * it's a wise choice to use clip rather than SourceDataLine (being more capable when it's real-time audio).
	 */
    private Clip clip;
    
    /**
     * Constructor of the sound class.
     * 
     * @param fileName
     */
    public Sound(String fileName) {
        try {
        	// add the sound to play, assuming it's a *.wav
        	clip = AudioSystem.getClip();
        	// getting the sound file
			clip.open(AudioSystem.getAudioInputStream(Sound.class.getResource(PATH_TO_RESOURCES + fileName)));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    } // constr. Sound
    /**
     * Playing the sound.
     */
    public void play() {
       clip.setFramePosition(0);			// it'd be good to start from the beginning
       clip.start();
    }
    /**
     * Making the song looping.
     */
    public void loop() {
        clip.loop(Clip.LOOP_CONTINUOUSLY); 	// loop not only once
    }
    
    /**
     * Stop playing sound.
     */
    public void stop() {
            clip.stop();
    }    
} // Sound