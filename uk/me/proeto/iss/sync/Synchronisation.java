package uk.me.proeto.iss.sync;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Vector;

import uk.me.proeto.iss.ImageSoundData;
import uk.me.proeto.iss.ImageSoundListener;

public class Synchronisation implements ImageSoundListener {

	private ImageSoundData data;
	
	// This data structure is the length of the audio indices and says which video
	// index is being shown for each audio frame.
	private int [] imageIndices = new int[0];
	
	// This is the equivalent data structure for the sound indices it's the length of the
	// video indices and tells you which audio frame happens at the transition to each 
	// new image.
	private int [] soundIndices = new int[0];
	
	// Key frames specify points in the data where the synchronisation is manually
	// set (ie at sound frame 83 we must be on image 20).  Automated assignment
	// happens between key frames.
	private Vector<KeyFrame> keyFrames = new Vector<KeyFrame>();
	
	// So we don't get a rush of images at the same point we can set a minimum gap
	// between frames.  This isn't an absolute value, but rather a percentage of
	// the minimum theoretical time which would exist between frames if they were
	// all spaced evenly
	
	private int minGap = 0;

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
	
	public KeyFrame [] keyFrames () {
		return keyFrames.toArray(new KeyFrame [0]);
	}
	
	public int getSoundFrameForImageIndex (int imageIndex) {
		if (imageIndex >=0 && imageIndex < soundIndices.length) {
			return soundIndices[imageIndex];
		}
		return -1;
	}

	public void setMinGap (int minGap) {
		this.minGap = minGap;
		generateSynchronisation();
	}
	
	private void generateSynchronisation () {
		
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
		
		// We need to work out the minimum number of frames we have to put
		// between adjacent transitions.
		
		// First we need to work out the optimal number of audio frames between
		// images if everything was evenly spread.
		
		int optimalAudioGap = (endAudioFrame-startAudioFrame)/(endVideoFrame-startVideoFrame);
		
		// We now take a percentage of this to be the minimum gap
		
		int minGapThisSegment = (optimalAudioGap*minGap)/100;
		
		System.out.println("Min gap per segment is "+minGapThisSegment+" from optimal gap "+optimalAudioGap);
		
		// We now need to make up a set of indices for the range we're looking at
		// and will sort these by their smoothed values.
		
		Integer [] indicesToSort = new Integer [(endAudioFrame-startAudioFrame)];
		
		for (int i=startAudioFrame+1;i<=endAudioFrame;i++) {
			indicesToSort[i-(startAudioFrame+1)] = i;
//			System.out.println("Index "+(i-(startAudioFrame+1))+" is "+i);
		}
		
		Arrays.sort(indicesToSort,new doubleValueComparator(audioData));
				
		// Now we take the top hits from the sorted data as the positions we're going
		// to do our transitions.  The first transition will always be at the first
		// frame of this region, and then it's done by the largest effect.
		//
		// TODO: Add some rules to stop a large loud duration from dominating.
		
		// To stop several sudden transitions we check which slots we've already
		// assigned and then don't assign to those which are too close.
		boolean [] usedSlots = new boolean[indicesToSort.length+1];
		for (int i=0;i<usedSlots.length;i++) {
			usedSlots[i] = false;
		}
		
		int [] transitionFrames = new int[(endVideoFrame-startVideoFrame)+1];
		
		// The first transition always happens at the first audio frame.
		transitionFrames[0] = startAudioFrame;
		for (int i=0;i<=minGapThisSegment;i++) {
			usedSlots[0+i] = true;
		}

		int currentSortedIndex = 0;
		for (int i=1;i<transitionFrames.length;i++) {

			// We go through to find the next available audio frame
			for (int j=currentSortedIndex;j<indicesToSort.length;j++) {
				if (usedSlots[indicesToSort[j]]) {
					// This slots isn't available as it's too close to one
					// which has already been used.
					System.out.println("Skipping "+indicesToSort[j]+" as it's too close to a used slot");
					currentSortedIndex++;
					continue;
				}
				// We can use this
				transitionFrames[i] = indicesToSort[j];

				for (int k=0-minGapThisSegment;k<=minGapThisSegment;k++) {
						usedSlots[indicesToSort[j]+k] = true;
				}
				currentSortedIndex++;
				break;
			}
			
		}
		
		Arrays.sort(transitionFrames);
		
		// Finally we can assign the frames to their sound slots.
		for (int i=0;i<transitionFrames.length;i++) {
			soundIndices[startVideoFrame+i] = transitionFrames[i];
			System.out.println("Transition to frame "+(startVideoFrame+i)+" happens at "+transitionFrames[i]);
		}
		
		for (int i=1;i<soundIndices.length;i++) {
			for (int j=soundIndices[i-1];j<soundIndices[i];j++) {
				imageIndices[j] = i-1;
			}
		}
		for (int i=soundIndices[soundIndices.length-1];i<imageIndices.length;i++) {
			imageIndices[i] = soundIndices.length-1;
		}
		
		
		
	}
	
	public int [] videoTransitions () {
		return soundIndices;
	}
	
	
	public void newAudioFile(ImageSoundData data) {
		keyFrames.clear();
		generateSynchronisation();
	}

	public void newImageSet(ImageSoundData data) {
		keyFrames.clear();
		generateSynchronisation();
	}
	
	public void transitionsUpdated(ImageSoundData data) {}

	public void audioFrameSelected(ImageSoundData data, int frame) {}

	public void videoFrameSelected(ImageSoundData data, int frame) {}

	public void smoothingUpdated(ImageSoundData data) {
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
