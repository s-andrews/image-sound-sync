package uk.me.proeto.iss.player;

import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;

import uk.me.proeto.iss.ImageSoundData;

public class PreviewPlayer implements Runnable, LineListener {

	private ImageSoundData data;
	private boolean playerStopped = false;
	private int lastAudioFrame = 0;
	
	public PreviewPlayer (ImageSoundData data) {
		
		this.data = data;
		
		Thread t = new Thread(this);
		t.start();
	}

	@Override
	public void run() {
		try {			
			SimpleWavPlayer audioPlayer = new SimpleWavPlayer(data.audioFile().file());
			audioPlayer.addLineListener(this);
			audioPlayer.play();
			
			while (! playerStopped) {
				int audioFrame = data.audioFile().getFrameForRawAudioPosition(audioPlayer.getAudioFrame());
				if (audioFrame != lastAudioFrame) {
					data.setSelectedAudioFrame(audioFrame); // TODO: Correct for sampling
					lastAudioFrame = audioFrame;
				}
				
				Thread.sleep(20);
			}
			
			
		}
		catch (Exception e) {
			System.err.println(e.getLocalizedMessage());
		}
		
		
		
	}

	public void update(LineEvent le) {
		System.out.println(le);
		
		if (le.getType() == LineEvent.Type.STOP) {
			playerStopped = true;
		}
	}
	
}
