package uk.me.proeto.iss.player;

import uk.me.proeto.iss.ImageSoundData;

public class PreviewPlayer implements Runnable {

	private ImageSoundData data;
	
	public PreviewPlayer (ImageSoundData data) {
		
		this.data = data;
		
		Thread t = new Thread(this);
		t.start();
	}

	@Override
	public void run() {
		try {			
			SimpleWavPlayer audioPlayer = new SimpleWavPlayer(data.audioFile().file());
			audioPlayer.play();
			
		}
		catch (Exception e) {
			System.err.println(e.getLocalizedMessage());
		}
		
		
		
	}
	
}
