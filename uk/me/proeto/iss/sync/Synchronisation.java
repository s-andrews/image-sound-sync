package uk.me.proeto.iss.sync;

import java.util.Arrays;
import java.util.Comparator;
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
		// TODO: Actually work out the correspondance between images and sound.
		
		// See if we actually have both image and audio data
		if (data.audioFile() == null || data.imageSet() == null) {
			imageIndices = new int[0];
			soundIndices = new int[0];
			return;
		}
		
		// Make up new data structures to hold the results of the synchronisation
		imageIndices = new int [data.audioFile().rawSampleData().length];
		soundIndices = new int [data.imageSet().files().length];
		
		// Now we need to get the normalised sound data
		double [] audioData = data.audioFile().smoothedSampleData();
		
		// TODO: Eventually split this up into a loop over key frames.
		
		int startAudioFrame = 0;
		int endAudioFrame = audioData.length-1;
		
		int startVideoFrame = 0;
		int endVideoFrame = soundIndices.length-1;
		
		// We now need to make up a set of indices for the range we're looking at
		// and will sort these by their smoothed values.
		
		Integer [] indicesToSort = new Integer [(endAudioFrame-startAudioFrame)];
		
		for (int i=startAudioFrame+1;i<=endAudioFrame;i++) {
			indicesToSort[i-(startAudioFrame+1)] = i;
		}
		
		Arrays.sort(indicesToSort,new doubleValueComparator(audioData));
		
		// Now we take the top hits from the sorted data as the positions we're going
		// to do our transitions.  The first transition will always be at the first
		// frame of this region, and then it's done by the largest effect.
		//
		// TODO: Add some rules to stop a large loud duration from dominating.
		
		int [] transitionFrames = new int[(endVideoFrame-startVideoFrame)+1];
		
		transitionFrames[0] = startAudioFrame;
		for (int i=1;i<transitionFrames.length;i++) {
			transitionFrames[i] = indicesToSort[i-1];
		}
		
		Arrays.sort(transitionFrames);
		
		// Finally we can assign the frames to their sound slots.
		for (int i=0;i<transitionFrames.length;i++) {
			soundIndices[startVideoFrame+i] = transitionFrames[i];
			System.out.println("Transition to frame "+(startVideoFrame+i)+" happens at "+transitionFrames[i]);
		}
		
		// TODO: Assign the video frame data
		
		
		
	}
	
	public int [] videoTransitions () {
		return soundIndices;
	}
	
	
	public void newAudioFile(AudioFile audioFile) {
		keyFrames.clear();
		generateSynchronisation();
	}

	public void newImageSet(ImageSet imageSet) {
		keyFrames.clear();
		generateSynchronisation();
	}
	
	private class doubleValueComparator implements Comparator<Integer> {

		private double [] data;
		
		public doubleValueComparator (double [] data) {
			this.data = data;
		}
		
		public int compare(Integer i1, Integer i2) {
			return Double.compare(data[i2], data[i1]);
		}
		
	}
	
	
}
