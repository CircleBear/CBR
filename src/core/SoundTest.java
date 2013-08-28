package core;

// Import statements
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

import java.applet.AudioClip;
import java.applet.Applet;

public class SoundTest {
	// Creating the AudioClip as global
	private static AudioClip myClip;

	public static void main(String[] args) {
		try {
			// Creating the URL this local machine
			URL myClipURL = new URL("file", "localhost", "src/Clips/explosion.wav");
			myClip = Applet.newAudioClip(myClipURL);
		}
		catch (MalformedURLException e) {
			System.err.print(e.getLocalizedMessage());
		}
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String command = "";
		
		try {
			command = br.readLine();
		}
		catch (Exception e) {
			System.exit(0);
		}
		
		/*
		 * Playing if user types 'play'
		 * Looping if user types 'loop'
		 * Stopping if user types 'stop'
		 * 
		 * Escapes program when user types 'escape'
		 */
		while (!(command.equals("escape"))) {
			if (command.equals("play"))
				playSound();
			else if (command.equals("loop"))
				loopSound();
			else if (command.equals("stop"))
				stopSound();
			
			try {
				command = br.readLine();
			}
			catch (Exception e) {
				System.exit(0);
			}
		} // End while
	} // End main()
	
	private static void playSound() {
		myClip.play();
	} // End playSound()
	
	private static void loopSound() {
		myClip.loop();
	} // End loopSound()
	
	private static void stopSound() {
		myClip.stop();
	} // End stopSound()
} // End SoundTest class
