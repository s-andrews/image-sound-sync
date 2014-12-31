package uk.me.proeto.iss;

import java.util.Enumeration;
import java.util.Vector;

import uk.me.proeto.iss.images.ImageSet;
import uk.me.proeto.iss.sound.AudioFile;
import uk.me.proeto.iss.sync.Synchronisation;

public class ImageSoundData {

	private ImageSet imageSet = null;
	private AudioFile audioFile = null;	
	private Vector<ImageSoundListener> listeners = new Vector<ImageSoundListener>();
	private Synchronisation synchronisation = new Synchronisation(this);

	
	public void setAudioFile (AudioFile audioFile) {
		this.audioFile = audioFile;
		
		Enumeration<ImageSoundListener> en = listeners.elements();
		while (en.hasMoreElements()) {
			en.nextElement().newAudioFile(this);
		}
	}
	
	public void setSelectedVideoFrame (int index) {
		Enumeration<ImageSoundListener> en = listeners.elements();
		while (en.hasMoreElements()) {
			en.nextElement().videoFrameSelected(this, index);
		}
	}

	public void setSelectedAudioFrame (int index) {
		Enumeration<ImageSoundListener> en = listeners.elements();
		while (en.hasMoreElements()) {
			en.nextElement().audioFrameSelected(this, index);
		}
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
					en.nextElement().transitionsUpdated(this);;
				}
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
