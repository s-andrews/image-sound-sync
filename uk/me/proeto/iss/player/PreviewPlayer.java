/*
 * Copyright 2014-2015 Simon Andrews
 * 
 * This file is part of Image Sound Sync.
 * Image Sound Sync is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Image Sound Sync is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Image Sound Sync.  If not, see <http://www.gnu.org/licenses/>.
 */

package uk.me.proeto.iss.player;

import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;

import uk.me.proeto.iss.ImageSoundData;

public class PreviewPlayer implements Runnable, LineListener {

	private ImageSoundData data;
	private boolean playerStopped = false;
	private int lastAudioFrame = 0;
	private boolean forceStop = false;
	private boolean pauseFlag = false;
	
	public PreviewPlayer (ImageSoundData data) {
		
		this.data = data;
		
		Thread t = new Thread(this);
		t.start();
	}

	public void stop () {
		forceStop = true;
	}
	
	public void pause () {
		pauseFlag = true;
	}
	
	@Override
	public void run() {
		try {			
			SimpleWavPlayer audioPlayer = new SimpleWavPlayer(data.audioFile().file());
			audioPlayer.addLineListener(this);
			audioPlayer.play();
			
			while (! playerStopped) {
				int audioFrame = data.audioFile().getFrameForRawAudioPosition(audioPlayer.getAudioFrame());
				
				if (forceStop) {
					audioPlayer.stop();
				}
				
				if (pauseFlag) {
					audioPlayer.pause();
					pauseFlag = false;
				}
				
				if (audioFrame != lastAudioFrame && audioFrame >=0) {
					data.setSelectedAudioFrame(audioFrame);
					lastAudioFrame = audioFrame;
				}
				
				Thread.sleep(20);
			}
			
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
	}

	public void update(LineEvent le) {
//		System.out.println(le);
		
		if (le.getType() == LineEvent.Type.STOP) {
			playerStopped = true;
		}
	}
	
}
