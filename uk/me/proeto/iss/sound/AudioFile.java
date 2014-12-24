package uk.me.proeto.iss.sound;

import java.io.File;
import java.io.IOException;

import uk.co.labbookpages.WavFile.WavFile;

public class AudioFile {

	private File file = null;
	private double [] rawSamples = null;
		
	public static final int SAMPLES_PER_SECOND = 10;

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
		
		int bufferSize = (int)(sampleRate/SAMPLES_PER_SECOND);
		
		System.out.println("Buffer size = "+bufferSize);
		
		System.out.println("Sample length is "+((wavFile.getNumFrames()/bufferSize)/SAMPLES_PER_SECOND)+" seconds");
		
		double [] buffer = new double[bufferSize*wavFile.getNumChannels()];
				
		rawSamples = new double[(int)(wavFile.getNumFrames()/wavFile.getSampleRate())*SAMPLES_PER_SECOND];
		
		System.out.println("Samples to read is "+rawSamples.length);
		
		for (int i=0;i<rawSamples.length;i++) {
			wavFile.readFrames(buffer, bufferSize);
			
			double max = 0;
			for (int j=0;j<buffer.length;j++) {
				if (buffer[j] > max) max = buffer[j];
			}
			
			rawSamples[i] = max;
			
			System.out.println("Max value for sample "+i+" is "+rawSamples[i]);
			
			
		}
		
	}
	
	public File file () {
		return file;
	}
	
	public double [] rawSampleData () {
		return rawSamples;
	}
	
	
}
