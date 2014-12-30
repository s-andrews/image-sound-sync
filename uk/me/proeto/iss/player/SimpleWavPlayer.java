package uk.me.proeto.iss.player;

/*
 * This code is adapted from the original WAV player at:
 * 
 * http://albertattard.blogspot.co.uk/2009/12/simple-java-wav-player.html
 */

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

	public SimpleWavPlayer (File file) throws IOException {
		inputStream = new FileInputStream(file);
	}
	
	public void addLineListener (LineListener l) {
		if (l != null && ! listeners.contains(l)) {
			listeners.add(l);
		}
	}
	
	public int getAudioFrame () {
		if (sourceDataLine == null) return 0;
		return sourceDataLine.getFramePosition();
	}
	

	public void play() {
		new Thread() {
			@Override
			public void run() {
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
				byte[] data = new byte[524288];// 128Kb
				try {
					int bytesRead = 0;
					while (bytesRead != -1) {
						bytesRead = audioInputStream.read(data, 0, data.length);
						if (bytesRead >= 0)
							sourceDataLine.write(data, 0, bytesRead);
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

}
