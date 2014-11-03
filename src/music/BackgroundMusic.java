package music;

import java.net.URL;

import javax.media.*;

public class BackgroundMusic implements Runnable, ControllerListener {
	
	Player player;
	private URL next;
	
	public BackgroundMusic(){
		new Thread(this).start();
	}

	@Override
	public void run() {
	}
	
	public void setNewTrack(URL url){
		this.next = url;
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
	
	public void setNewTrack(String name){
		setNewTrack(getClass().getResource(name+".wav"));
	}

	@Override
	public void controllerUpdate(ControllerEvent e) {
		if (e instanceof EndOfMediaEvent) {
			setNewTrack(next);
        }
	}

}
