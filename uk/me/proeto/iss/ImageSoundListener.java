package uk.me.proeto.iss;

import uk.me.proeto.iss.images.ImageSet;
import uk.me.proeto.iss.sound.AudioFile;


public interface ImageSoundListener {

	public void newAudioFile (AudioFile audioFile);
	public void newImageSet (ImageSet imageSet);
	
}
