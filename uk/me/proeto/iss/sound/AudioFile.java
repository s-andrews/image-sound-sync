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

package uk.me.proeto.iss.sound;

import java.io.File;
import java.io.IOException;

import uk.co.labbookpages.WavFile.WavFile;

public class AudioFile {

	public static final int SAMPLES_PER_SECOND = 10;

	private File file = null;
	private double [] rawSamples = null;
	private double [] smoothedSamples = null;
	private int smoothingWindow = -1;
	private int bufferSize;
	
	public AudioFile (File file) throws IOException {
		this.file = file;
		
		WavFile wavFile = WavFile.openWavFile(file);
		
//		wavFile.display();
		
		long sampleRate = wavFile.getSampleRate();
		
		// We're going to try to sample 5 data points per second.  Any more than this is a waste
		// of time as we're not going to switch images that quickly anyway.
		
		if (sampleRate % SAMPLES_PER_SECOND != 0) {
			throw new IOException("Can't make a suitable buffer for "+SAMPLES_PER_SECOND+" frames per second");
		}
		
		bufferSize = (int)(sampleRate/SAMPLES_PER_SECOND);
		
//		System.out.println("Buffer size = "+bufferSize);	
//		System.out.println("Sample length is "+((wavFile.getNumFrames()/bufferSize)/SAMPLES_PER_SECOND)+" seconds");
		
		double [] buffer = new double[bufferSize*wavFile.getNumChannels()];
				
		rawSamples = new double[(int)(wavFile.getNumFrames()/wavFile.getSampleRate())*SAMPLES_PER_SECOND];
		smoothedSamples = new double[rawSamples.length];
		
//		System.out.println("Samples to read is "+rawSamples.length);
		
		for (int i=0;i<rawSamples.length;i++) {
			wavFile.readFrames(buffer, bufferSize);
			
			double max = 0;
			for (int j=0;j<buffer.length;j++) {
				if (buffer[j] > max) max = buffer[j];
			}
			
			rawSamples[i] = max;
						
//			System.out.println("Max value for sample "+i+" is "+rawSamples[i]);
				
		}

		setSmoothing(SAMPLES_PER_SECOND);

		
	}
		
	public int getFrameForRawAudioPosition (int position) {
		return (position/bufferSize);
	}
	
	public int smoothingWindow () {
		return smoothingWindow;
	}
	
	public int bufferSize () {
		return bufferSize;
	}
	
	public void setSmoothing (int smoothingWindow) {
		if (smoothingWindow == this.smoothingWindow) {			
			return;
		}
		this.smoothingWindow = smoothingWindow;
		
		for (int i=0;i<rawSamples.length;i++) {
			int startIndex = i-smoothingWindow;
			if (startIndex < 0) startIndex = 0;
			int endIndex = i+smoothingWindow;
			if (endIndex >= rawSamples.length) endIndex = rawSamples.length-1;
			
			double total = 0;
			int count = 0;
			
			for (int j=startIndex;j<=endIndex;j++) {
//				if (j==i) continue;
				total += rawSamples[j];
				++count;
			}
			
			// If we're not smoothing then we don't want to do the subtraction
			// otherwise everything ends up at zero.
			if (smoothingWindow == 0) {
				smoothedSamples[i] = rawSamples[i];
			}
			else {
				smoothedSamples[i] = rawSamples[i] - (total/count);
			}
		}
		
		// We used to removed negative values, but actually it's easier to see what's
		// going on if we leave these in place.

	}
	
	public File file () {
		return file;
	}
	
	public double [] rawSampleData () {
		return rawSamples;
	}
	
	public double [] smoothedSampleData () {
		return smoothedSamples;
	}

	
}
