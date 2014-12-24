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

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public class SimpleWavPlayer {

	private InputStream inputStream;

	public SimpleWavPlayer (File file) throws IOException {
		inputStream = new FileInputStream(file);
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

				SourceDataLine sourceDataLine = null;
				try {
					AudioFormat audioFormat = audioInputStream.getFormat();
					DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
					sourceDataLine = (SourceDataLine) AudioSystem.getLine(info);
					sourceDataLine.open(audioFormat);
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
