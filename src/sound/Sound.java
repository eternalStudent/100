package sound;

import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Sound {
	
	Sound sound = new Sound();
	
	private Sound(){}
	
	public static void play(URL url){
		try{
		    Clip clip = AudioSystem.getClip();
		    AudioInputStream ais = AudioSystem.getAudioInputStream( url );
		    clip.open(ais);
		    clip.start();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void play(String sound){
		play(sound.getClass().getResource("/sound/"+sound+".wav"));
	}
	
}
