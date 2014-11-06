package music;

import java.net.URL;

import javax.media.*;

public class BackgroundMusic implements Runnable, ControllerListener {
	
	private Player player;
	private URL next;
	
	public BackgroundMusic(){
		new Thread(this).start();
	}

	@Override
	public void run() {
	}
	
	public void play(URL url, URL next){
		this.next = next;
		if (player != null){
			player.stop();
			player.close();
		}	
		try {
	         MediaLocator locator = new MediaLocator(url);
	         player = Manager.createPlayer(locator);
	         player.addControllerListener(this);
	         player.realize();
	         player.start();
	      } catch (Exception e) {
	         e.printStackTrace();
	      }
	}
	
	public void play(String name1, String name2){
		play(getClass().getResource(name1+".wav"), getClass().getResource(name2+".wav"));
	}
	
	public void play(String name){
		URL url = getClass().getResource(name+".wav");
		play(url, url);
	}

	@Override
	public void controllerUpdate(ControllerEvent e) {
		if (e instanceof EndOfMediaEvent) {
			play(next, next);
        }
	}
	
	public void stop(){
		player.stop();
		player.close();
	}

}
