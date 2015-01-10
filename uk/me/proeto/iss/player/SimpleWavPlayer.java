package uk.me.proeto.iss.player;

/*
 * This code is adapted from the original WAV player at:
 * 
 * http://albertattard.blogspot.co.uk/2009/12/simple-java-wav-player.html
 */

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Vector;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public class SimpleWavPlayer {

	private InputStream inputStream;
	private Vector<LineListener>listeners = new Vector<LineListener>();
	private SourceDataLine sourceDataLine = null;
	private boolean stop = false;
	private boolean pause = false;
	
	// These variables are used to tell where we should start when we want to play
	// from part way through the file.
	
	// This is the number of bytes into the file where we need to start playing.
	private int startSample;
	
	// This is the number of frames we skipped over so we can correctly get the
	// play frome from the sourceDataLine when asked.
	private int sampleWhenPlayStarted = -1;
	
	public SimpleWavPlayer (File file) throws IOException {
		// Some systems don't like playing directly from a file input stream
		// without an intervening buffer.
		inputStream = new BufferedInputStream(new FileInputStream(file));
	}
	
	public void addLineListener (LineListener l) {
		if (l != null && ! listeners.contains(l)) {
			listeners.add(l);
		}
	}
	
	public int getAudioFrame () {
		if (sourceDataLine == null) return 0;
		return sourceDataLine.getFramePosition()+sampleWhenPlayStarted;
	}
	

	public void play(int startSample) {
		this.startSample = startSample;
		new Thread() {
			@Override
			public void run() {
				
				int samplesRead = 0;
				
				AudioInputStream audioInputStream = null;
				try {
					audioInputStream = AudioSystem.getAudioInputStream(inputStream);
				} catch (UnsupportedAudioFileException e) {
					e.printStackTrace();
					return;
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
				
				try {
					AudioFormat audioFormat = audioInputStream.getFormat();
					DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
					sourceDataLine = (SourceDataLine) AudioSystem.getLine(info);
					sourceDataLine.open(audioFormat);
					Enumeration<LineListener> en = listeners.elements();
					while (en.hasMoreElements()) {
						sourceDataLine.addLineListener(en.nextElement());
					}
				} 
				catch (LineUnavailableException e) {
					e.printStackTrace();
					return;
				}

				sourceDataLine.start();
//				byte[] data = new byte[524288];// 128Kb
				byte[] data = new byte[131072];// 128Kb
				try {
					int bytesRead = 0;
					while (bytesRead != -1) {
						if (stop) {
							sourceDataLine.stop();
							break;
						}
						
						if (pause) {
							try {
								Thread.sleep(20);
							} catch (InterruptedException e) {}
							continue;
						}
						bytesRead = audioInputStream.read(data, 0, data.length);
						if (bytesRead >= 0) {
							// This allows us to start playing part through the sample.  It's
							// pretty inefficient in that we end up reading the part of the file
							// we're skipping rather than just jumping to the right offset, but
							// hopefully it's pretty safe.
							samplesRead += bytesRead;

							if (samplesRead >= SimpleWavPlayer.this.startSample) {
								if (sampleWhenPlayStarted < 0) {
									sampleWhenPlayStarted = (samplesRead-bytesRead)/4;
								}
								sourceDataLine.write(data, 0, bytesRead);
							}
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
					return;
				} finally {
					sourceDataLine.drain();
					sourceDataLine.close();
				}
			}
		}.start();
	}
	
	public void stop () {
		stop = true;
	}
	
	public void pause () {
		pause = !pause;
	}

}
