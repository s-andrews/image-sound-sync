package uk.me.proeto.iss;

import java.util.Enumeration;
import java.util.Vector;

import uk.me.proeto.iss.images.ImageSet;
import uk.me.proeto.iss.sound.AudioFile;
import uk.me.proeto.iss.sync.Synchronisation;



public class ImageSoundData {

	private ImageSet imageSet = null;
	private AudioFile audioFile = null;
	private Synchronisation synchronisation = new Synchronisation();
	
	private Vector<ImageSoundListener> listeners = new Vector<ImageSoundListener>();
	
	
	
	public void setAudioFile (AudioFile audioFile) {
		this.audioFile = audioFile;
		//TODO: Tell the synchronisation that the audio file has updated
		
		Enumeration<ImageSoundListener> en = listeners.elements();
		while (en.hasMoreElements()) {
			en.nextElement().newAudioFile(audioFile);
		}
	}
	
	public void setImageSet (ImageSet imageSet) {
		this.imageSet = imageSet;
		//TODO: Tell the synchronisation that the image set has updated
		
		Enumeration<ImageSoundListener> en = listeners.elements();
		while (en.hasMoreElements()) {
			en.nextElement().newImageSet(imageSet);
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
	
}
