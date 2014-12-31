package uk.me.proeto.iss.sound;

import java.io.File;
import java.io.IOException;

import uk.co.labbookpages.WavFile.WavFile;

public class AudioFile {

	public static final int SAMPLES_PER_SECOND = 10;

	private File file = null;
	private double [] rawSamples = null;
	private double [] smoothedSamples = null;
	private int smoothingWindow;
	private int bufferSize;
	
	public AudioFile (File file) throws IOException {
		this.file = file;
		
		WavFile wavFile = WavFile.openWavFile(file);
		
		wavFile.display();
		
		long sampleRate = wavFile.getSampleRate();
		
		// We're going to try to sample 5 data points per second.  Any more than this is a waste
		// of time as we're not going to switch images that quickly anyway.
		
		if (sampleRate % SAMPLES_PER_SECOND != 0) {
			throw new IOException("Can't make a suitable buffer for "+SAMPLES_PER_SECOND+" frames per second");
		}
		
		bufferSize = (int)(sampleRate/SAMPLES_PER_SECOND);
		
		System.out.println("Buffer size = "+bufferSize);
		
		System.out.println("Sample length is "+((wavFile.getNumFrames()/bufferSize)/SAMPLES_PER_SECOND)+" seconds");
		
		double [] buffer = new double[bufferSize*wavFile.getNumChannels()];
				
		rawSamples = new double[(int)(wavFile.getNumFrames()/wavFile.getSampleRate())*SAMPLES_PER_SECOND];
		smoothedSamples = new double[rawSamples.length];
		
		System.out.println("Samples to read is "+rawSamples.length);
		
		for (int i=0;i<rawSamples.length;i++) {
			wavFile.readFrames(buffer, bufferSize);
			
			double max = 0;
			for (int j=0;j<buffer.length;j++) {
				if (buffer[j] > max) max = buffer[j];
			}
			
			rawSamples[i] = max;
			
			setSmoothing(SAMPLES_PER_SECOND);
			
//			System.out.println("Max value for sample "+i+" is "+rawSamples[i]);
			
			
		}
		
	}
		
	public int getFrameForRawAudioPosition (int position) {
		return (position/bufferSize);
	}
	
	public int smoothingWindow () {
		return smoothingWindow;
	}
	
	public void setSmoothing (int smoothingWindow) {
		this.smoothingWindow = smoothingWindow;
		
		for (int i=0;i<rawSamples.length;i++) {
			int startIndex = i-smoothingWindow;
			if (startIndex < 0) startIndex = 0;
			int endIndex = i+smoothingWindow;
			if (endIndex >= rawSamples.length) endIndex = rawSamples.length-1;
			
			double total = 0;
			int count = 0;
			
			for (int j=startIndex;j<endIndex;j++) {
//				if (j==i) continue;
				total += rawSamples[j];
				++count;
			}
			smoothedSamples[i] = rawSamples[i] - (total/count);
		}
		
		// We will now have some negative values, which we need to remove to keep
		// everything on a positive scale.
//		double minValue = Double.MAX_VALUE;
//		
//		for (int i=0;i<smoothedSamples.length;i++) {
//			if (smoothedSamples[i] < minValue) minValue = smoothedSamples[i];
//		}
//
//		for (int i=0;i<smoothedSamples.length;i++) {
//			smoothedSamples[i] -= minValue;
//		}

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
