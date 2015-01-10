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

package uk.me.proeto.iss.gui.KeyFrameEditor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

import uk.me.proeto.iss.ImageSoundData;
import uk.me.proeto.iss.sync.KeyFrame;

public class KeyFramePanel extends JPanel implements MouseListener {

	private double [] samples;
	private int numberOfVideoFrames;
	private int [] transitions;
	private KeyFrame [] keyFrames;
	private double max = 1;
	private Color colour;
	private int selectedAudioFrame = 0;
	private int selectedVideoFrame = 0;
	private ImageSoundData data;
	
	
	private int validAudioStart = 0;
	private int validAudioEnd = 0;
	private int validVideoStart = 0;
	private int validVideoEnd = 0;
	
	
	private static final Color LIGHT_RED = new Color(240,190,190);
	private static final Color DARK_RED = new Color(150,0,0);
	private static final Color LIGHT_GREEN = new Color(190,240,190);
	
	public KeyFramePanel (ImageSoundData data) {
		this.data = data;
		samples = data.audioFile().rawSampleData();
		numberOfVideoFrames = data.imageSet().files().length;
		transitions = data.synchronisation().videoTransitions();
		keyFrames = data.synchronisation().keyFrames();
		addMouseListener(this);
	}
	
	public void setSelectedAudioFrame (int frame) {
		this.selectedAudioFrame = frame;
		setValidRegionFromAudio();
		repaint();
	}
	
	public void setSelectedVideoFrame (int frame) {
		selectedVideoFrame = frame;
		setValidRegionFromVideo();
		repaint();
	}
	
	private int getAudioX (int bin) {
		return (int)((getWidth()/(double)samples.length)*bin);
	}

	private int getVideoX (int bin) {
		return (int)((getWidth()/(double)numberOfVideoFrames)*bin);
	}

	
	private int getXAudioFrame (int position) {
		return (int)((samples.length/(double)getWidth())*position);
		
	}
	
	private int getYAudioTop (double value) {
		// The audio occupies the top 2/5 of the view
		double fullHeight = (getHeight()/5)*2;
	
		// We're symmetrical around the centre, so we 
		// scale to half this height
		double height = fullHeight/2;
		double proportion = value/max;
		return (int)(height-(height*proportion));
	}

	private int getYAudioBottom (double value) {
		// The audio occupies the top 2/5 of the view
		double fullHeight = (getHeight()/5)*2;
	
		// We're symmetrical around the centre, so we 
		// scale to half this height
		double height = fullHeight/2;
		double proportion = value/max;
		return (int)(height+(height*proportion));	
	}
	
	private void setValidRegionFromAudio () {

		// Check a simple case first
		if (keyFrames.length == 0) {
			validAudioStart = 1;
			validAudioEnd = samples.length-1;
			validVideoStart = 1;
			validVideoEnd = numberOfVideoFrames-1;
			return;
		}
		
		
		for (int i=0;i<keyFrames.length;i++) {
			
			// We can't duplicate a key frame position
			if (keyFrames[i].audioFrame() == selectedAudioFrame) {
				validAudioStart = -1;
				validAudioEnd = -1;
				validVideoStart = -1;
				validVideoEnd = -1;
				return;
			}
			
			if (keyFrames[i].audioFrame() > selectedAudioFrame) {
				// We need to compare this with the frame before
				validVideoEnd = keyFrames[i].videoFrame()-1;
				validAudioEnd = keyFrames[i].audioFrame()-1;
				if (i>0) {
					validVideoStart = keyFrames[i-1].videoFrame()+1;
					validAudioStart = keyFrames[i-1].audioFrame()+1;
				}
				else {
					validVideoStart = 1;
					validAudioStart = 1;
				}
				return;
			}
		}
		
		// If we get here then none of the existing frames are after
		// the one being added so we need to compare to the end.
		
		validVideoEnd = numberOfVideoFrames-1;
		validAudioEnd = samples.length-1;
		validVideoStart = keyFrames[keyFrames.length-1].videoFrame()+1;
		validAudioStart = keyFrames[keyFrames.length-1].audioFrame()+1;		
	}
	
	private void setValidRegionFromVideo () {

		// Check a simple case first
		if (keyFrames.length == 0) {
			validAudioStart = 1;
			validAudioEnd = samples.length-1;
			validVideoStart = 1;
			validVideoEnd = numberOfVideoFrames-1;
			return;
		}
		
		
		for (int i=0;i<keyFrames.length;i++) {
			
			// We can't duplicate a key frame position
			if (keyFrames[i].videoFrame() == selectedVideoFrame) {
				validAudioStart = -1;
				validAudioEnd = -1;
				validVideoStart = -1;
				validVideoEnd = -1;
				return;
			}
			
			if (keyFrames[i].videoFrame() > selectedVideoFrame) {
				// We need to compare this with the frame before
				validVideoEnd = keyFrames[i].videoFrame()-1;
				validAudioEnd = keyFrames[i].audioFrame()-1;
				if (i>0) {
					validVideoStart = keyFrames[i-1].videoFrame()+1;
					validAudioStart = keyFrames[i-1].audioFrame()+1;
				}
				else {
					validVideoStart = 1;
					validAudioStart = 1;
				}
				return;
			}
		}
		
		// If we get here then none of the existing frames are after
		// the one being added so we need to compare to the end.
		
		validVideoEnd = numberOfVideoFrames-1;
		validAudioEnd = samples.length-1;
		validVideoStart = keyFrames[keyFrames.length-1].videoFrame()+1;
		validAudioStart = keyFrames[keyFrames.length-1].audioFrame()+1;		
		
	}
	
	public Dimension getPreferredSize () {
		return new Dimension(1000,150);
	}
	
	public void paint (Graphics g) {
		
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		if (samples.length == 0) {
			int x = (getWidth()/2)-((g.getFontMetrics().stringWidth("No audio loaded"))/2);
			g.setColor(Color.DARK_GRAY);
			g.drawString("No audio loaded", x, getHeight()/2);
			return;
		}

		// Highlight the valid region
		g.setColor(LIGHT_GREEN);
		if (validAudioStart >=0) {
			int audioStartX = getAudioX(validAudioStart);
			int audioEndX = getAudioX(validAudioEnd);
			int videoStartX = getVideoX(validVideoStart);
			int videoEndX = getVideoX(validVideoEnd);
			
			int audioBottomY = (getHeight()*2)/5;
			int videoTopY = (getHeight()*3)/5;
			
			g.fillPolygon(new int[] {audioStartX,audioEndX, audioEndX,videoEndX,videoEndX,videoStartX,videoStartX,audioStartX}, new int [] {0,0,audioBottomY,videoTopY,getHeight(),getHeight(),videoTopY,audioBottomY}, 8);
		}

		// See if we can highlight any transitions
		if (transitions != null) {
			g.setColor(LIGHT_RED);
			for (int i=0;i<transitions.length;i++) {
				int audioX = getAudioX(transitions[i]);
				int videoX = getVideoX(i);
				g.drawLine(audioX, 0, audioX, (getHeight()*2)/5);
				g.drawLine(audioX, (getHeight()*2)/5,videoX , (getHeight()*3)/5);
				g.drawLine(videoX, (getHeight()*3)/5,videoX , getHeight());
			}
		}
		
		// See if we can highlight any key frames
		if (keyFrames != null) {
			g.setColor(DARK_RED);
			for (int i=0;i<keyFrames.length;i++) {
				for (int offset = -1;offset<=1;offset++) {
					int audioX = getAudioX(keyFrames[i].audioFrame())+offset;
					int videoX = getVideoX(keyFrames[i].videoFrame())+offset;
					g.drawLine(audioX, 0, audioX, (getHeight()*2)/5);
					g.drawLine(audioX, (getHeight()*2)/5,videoX , (getHeight()*3)/5);
					g.drawLine(videoX, (getHeight()*3)/5,videoX , getHeight());
				}
			}
		}

		
		g.setColor(colour);
		
		int lastX = getAudioX(0);
		for (int i=0;i<samples.length;i++) {
			
			int x = getAudioX(i+1);
//			if (x == lastX) continue;

			int yBottom = getYAudioBottom(samples[i]);
			int yTop = getYAudioTop(samples[i]);
			int yHeight = yBottom-yTop;
			
			// Need to give something to see if we're looking at silence
			if (yHeight == 0) yHeight++;
			
//			System.err.println("i="+i+" lastX="+lastX+" x="+x+" ytop="+yTop+" ybottom="+yBottom+" max="+max+" value="+samples[i]);

			g.fillRect(lastX, yTop, x-lastX, yHeight);
			lastX = x;
			
		}	

		// Draw the current selection
		g.setColor(Color.BLACK);
		for (int offset = -1;offset<=1;offset++) {
			int audioX = getAudioX(selectedAudioFrame)+offset;
			int videoX = getVideoX(selectedVideoFrame)+offset;
			g.drawLine(audioX, 0, audioX, (getHeight()*2)/5);
			g.drawLine(audioX, (getHeight()*2)/5,videoX , (getHeight()*3)/5);
			g.drawLine(videoX, (getHeight()*3)/5,videoX , getHeight());
		}

		
	}

	public void mouseClicked(MouseEvent me) {
		int audioBin = getXAudioFrame(me.getX());
		data.setSelectedAudioFrame(audioBin);
	}

	public void mouseEntered(MouseEvent e) {}

	public void mouseExited(MouseEvent e) {}

	public void mousePressed(MouseEvent e) {}

	public void mouseReleased(MouseEvent e) {}
	
}
