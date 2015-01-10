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

package uk.me.proeto.iss;

import java.util.Enumeration;
import java.util.Vector;

import uk.me.proeto.iss.images.ImageSet;
import uk.me.proeto.iss.sound.AudioFile;
import uk.me.proeto.iss.sync.KeyFrame;
import uk.me.proeto.iss.sync.Synchronisation;

public class ImageSoundData {

	private ImageSet imageSet = null;
	private AudioFile audioFile = null;	
	private Vector<ImageSoundListener> listeners = new Vector<ImageSoundListener>();
	private Synchronisation synchronisation = new Synchronisation(this);
	private int currentAudioFrame = -1;
	private int currentVideoFrame = -1;


	public void setAudioFile (AudioFile audioFile) {
		this.audioFile = audioFile;

		Enumeration<ImageSoundListener> en = listeners.elements();
		while (en.hasMoreElements()) {
			en.nextElement().newAudioFile(this);
		}
	}
	
	public void addKeyFrame (KeyFrame keyFrame) {
		synchronisation.addKeyFrame(keyFrame);
		Enumeration<ImageSoundListener> en = listeners.elements();
		while (en.hasMoreElements()) {
			en.nextElement().transitionsUpdated(this);
		}
	}

	public void removeKeyFrame (KeyFrame keyFrame) {
		synchronisation.removeKeyFrame(keyFrame);
		Enumeration<ImageSoundListener> en = listeners.elements();
		while (en.hasMoreElements()) {
			en.nextElement().transitionsUpdated(this);
		}
	}

	
	public void setSelectedVideoFrame (int index) {
		currentVideoFrame = index;
		currentAudioFrame = synchronisation.getSoundFrameForImageIndex(index);
		Enumeration<ImageSoundListener> en = listeners.elements();
		while (en.hasMoreElements()) {
			en.nextElement().videoFrameSelected(this, index);
		}
	}

	public void setSelectedAudioFrame (int index) {
		currentAudioFrame = index;
		Enumeration<ImageSoundListener> en = listeners.elements();
		while (en.hasMoreElements()) {
			en.nextElement().audioFrameSelected(this, index);
		}
	}
	
	public int currentAudioFrame () {
		return currentAudioFrame;
	}
	
	public int currentVideoFrame () {
		return currentVideoFrame;
	}
	

	public void setSmoothing (int smoothing) {
		if (audioFile != null) {
			audioFile.setSmoothing(smoothing);
			Enumeration<ImageSoundListener> en = listeners.elements();
			while (en.hasMoreElements()) {
				en.nextElement().smoothingUpdated(this);
			}
			if (imageSet != null) {
				en = listeners.elements();
				while (en.hasMoreElements()) {
					en.nextElement().transitionsUpdated(this);
				}
			}
		}
	}

	public void setMinGap (int minGap) {
		synchronisation.setMinGap(minGap);
		
		if (imageSet != null) {
			Enumeration<ImageSoundListener> en = listeners.elements();
			while (en.hasMoreElements()) {
				en.nextElement().transitionsUpdated(this);
			}
		}
	}

	public void setImageSet (ImageSet imageSet) {
		this.imageSet = imageSet;

		Enumeration<ImageSoundListener> en = listeners.elements();
		while (en.hasMoreElements()) {
			en.nextElement().newImageSet(this);
		}
	}


	public void addListener (ImageSoundListener l) {
		if (l != null && ! listeners.contains(l)) {
			listeners.add(l);
		}
	}
	public void removeListener (ImageSoundListener l) {
		if (l != null && listeners.contains(l)) {
			listeners.remove(l);
		}
	}

	public AudioFile audioFile () {
		return audioFile;
	}

	public ImageSet imageSet () {
		return imageSet;
	}

	public Synchronisation synchronisation () {
		return synchronisation;
	}

}
