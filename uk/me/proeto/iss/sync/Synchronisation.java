package uk.me.proeto.iss.sync;

import java.util.Vector;

import uk.me.proeto.iss.ImageSoundData;
import uk.me.proeto.iss.ImageSoundListener;
import uk.me.proeto.iss.images.ImageSet;
import uk.me.proeto.iss.sound.AudioFile;

public class Synchronisation implements ImageSoundListener {

	private ImageSoundData data;
	
	// This is going to hold the image indices for each sub-sampled sound frame.  It's
	// the basic data structure used to hold the syncrhonisation.
	private int [] imageIndices = new int[0];
	
	// This is the equivalent data structure for the sound indices so we can look up
	// which sound frame should be playing at any given image transition.
	private int [] soundIndices = new int[0];
	
	// Key frames specify points in the data where the synchronisation is manually
	// set (ie at sound frame 83 we must be on image 20).  Automated assignment
	// happens between key frames.
	private Vector<KeyFrame> keyFrames = new Vector<KeyFrame>();
	
	
	public Synchronisation (ImageSoundData data) {
		this.data = data;
		data.addListener(this);
	}
	
	public int getImageIndexForSoundFrame (int soundFrame) {
		if (soundFrame >=0 && soundFrame < imageIndices.length) {
			return imageIndices[soundFrame];
		}
		return -1;
	}
	
	public int getSoundFrameForImageIndex (int imageIndex) {
		if (imageIndex >=0 && imageIndex < soundIndices.length) {
			return soundIndices[imageIndex];
		}
		return -1;
	}

	private void generateSynchronisation () {
		// TODO: Actually work out the correspondance between images and sound.`
		
	}
	
	
	public void newAudioFile(AudioFile audioFile) {
		keyFrames.clear();
		generateSynchronisation();
	}

	public void newImageSet(ImageSet imageSet) {
		keyFrames.clear();
		generateSynchronisation();
	}
	
	
}
