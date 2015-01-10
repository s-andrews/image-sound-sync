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

package uk.me.proeto.iss.sync;

import java.util.Arrays;
import java.util.Collections;
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

	public void addKeyFrame (KeyFrame keyFrame) {
		if (! isValidKeyFrame(keyFrame)) {
			throw new IllegalStateException("Key frame added isn't compatible with the existing key frames");
		}
		keyFrames.add(keyFrame);
		Collections.sort(keyFrames);
		generateSynchronisation();
	}

	public void removeKeyFrame (KeyFrame keyFrame) {
		keyFrames.remove(keyFrame);
		generateSynchronisation();
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

		
		for (int kf=0;kf<=keyFrames.size();kf++) {

			int startAudioFrame;
			int endAudioFrame;

			int startVideoFrame;
			int endVideoFrame;

			if (kf==0) {
				// We start from the very beginning
				startAudioFrame = 0;
				startVideoFrame = 0;
			}
			else {
				// We start from the previous key frame
				startAudioFrame = keyFrames.elementAt(kf-1).audioFrame();
				startVideoFrame = keyFrames.elementAt(kf-1).videoFrame();
			}
			
			if (kf == keyFrames.size()) {
				// We end at the end
				endAudioFrame = audioData.length-1;
				endVideoFrame = soundIndices.length-1;
			}
			else {
				// We end just before the next key frame
				endAudioFrame = keyFrames.elementAt(kf).audioFrame()-1;
				endVideoFrame = keyFrames.elementAt(kf).videoFrame()-1;
			}
			
//			System.out.println("From key index "+kf+" audio was "+startAudioFrame+"-"+endAudioFrame+" video was "+startVideoFrame+"-"+endVideoFrame+" total was "+imageIndices.length+","+soundIndices.length);
			
			// We need to work out the minimum number of frames we have to put
			// between adjacent transitions.

			// First we need to work out the optimal number of audio frames between
			// images if everything was evenly spread.

			// TODO: Account for the case where there's only one frame assignment possible.
			int optimalAudioGap = (endAudioFrame-startAudioFrame)/(endVideoFrame-startVideoFrame);

			// We now take a percentage of this to be the minimum gap

			int minGapThisSegment = (optimalAudioGap*minGap)/100;

//			System.out.println("Min gap per segment is "+minGapThisSegment+" from optimal gap "+optimalAudioGap);

			// We now need to make up a set of indices for the range we're looking at
			// and will sort these by their smoothed values.

			Integer [] indicesToSort = new Integer [(endAudioFrame-startAudioFrame)];
			
			for (int i=startAudioFrame+1;i<=endAudioFrame;i++) {
				indicesToSort[i-(startAudioFrame+1)] = i;
//				System.out.println("Index "+(i-(startAudioFrame+1))+" is "+i);
			}

			Arrays.sort(indicesToSort,new doubleValueComparator(audioData));

			// Now we take the top hits from the sorted data as the positions we're going
			// to do our transitions.  The first transition will always be at the first
			// frame of this region, and then it's done by the largest effect.

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
					if (usedSlots[indicesToSort[j]-startAudioFrame]) {
						// This slots isn't available as it's too close to one
						// which has already been used.
						System.out.println("Skipping "+indicesToSort[j]+" as it's too close to a used slot");
						currentSortedIndex++;
						continue;
					}
					// We can use this
					transitionFrames[i] = indicesToSort[j];

					for (int k=0-minGapThisSegment;k<=minGapThisSegment;k++) {
						if ((indicesToSort[j]-startAudioFrame)+k >=0 && (indicesToSort[j]-startAudioFrame)+k < usedSlots.length) {
							usedSlots[(indicesToSort[j]-startAudioFrame)+k] = true;
						}
					}
					currentSortedIndex++;
					break;
				}

			}

			Arrays.sort(transitionFrames);

			// Finally we can assign the frames to their sound slots.
			for (int i=0;i<transitionFrames.length;i++) {
				soundIndices[startVideoFrame+i] = transitionFrames[i];
//				System.out.println("Transition to frame "+(startVideoFrame+i)+" happens at "+transitionFrames[i]);
			}

			for (int i=1;i<soundIndices.length;i++) {
				for (int j=soundIndices[i-1];j<soundIndices[i];j++) {
					imageIndices[j] = i-1;
				}
			}


		}
		for (int i=soundIndices[soundIndices.length-1];i<imageIndices.length;i++) {
			imageIndices[i] = soundIndices.length-1;
		}



	}

	public int [] videoTransitions () {
		return soundIndices;
	}
	
	public int [] keyFrameAudioFrames () {
		int [] keys = new int[keyFrames.size()];
		
		for (int i=0;i<keys.length;i++) {
			keys[i] = keyFrames.elementAt(i).audioFrame();
		}
		
		return keys;
	}
	
	public boolean isValidKeyFrame (KeyFrame kf) {
		
		// For this to be valid we need to find the existing frame which
		// has the audio frame just after this one, and then see if the
		// video frame falls between that and the one before.
		
		// Check a simple case first
		if (keyFrames.size() == 0) return true;
		
		// You can't assign to the initial frames
		if (kf.audioFrame() == 0 || kf.videoFrame() == 0) return false;
		
		for (int i=0;i<keyFrames.size();i++) {
			
			// We can't duplicate a key frame position
			if (keyFrames.elementAt(i).audioFrame() == kf.audioFrame()) return false;
			
			if (keyFrames.elementAt(i).audioFrame() > kf.audioFrame()) {
				// We need to compare this with the frame before
				int endVideoFrame = keyFrames.elementAt(i).videoFrame();
				int startVideoFrame = 0;
				if (i>0) {
					startVideoFrame = keyFrames.elementAt(i-1).videoFrame();
				}
				
				if (kf.videoFrame() > startVideoFrame && kf.videoFrame() < endVideoFrame) {
					return true;
				}
				
				return false;
				
			}
		}
		
		// If we get here then none of the existing frames are after
		// the one being added so we need to compare to the end.
		
		int endVideoFrame = imageIndices.length-1;
		int startVideoFrame = keyFrames.elementAt(keyFrames.size()-1).videoFrame();
		
		if (kf.videoFrame() > startVideoFrame && kf.videoFrame() < endVideoFrame) {
			return true;
		}
		
		return false;
		
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
