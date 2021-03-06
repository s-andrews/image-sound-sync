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

package uk.me.proeto.iss.gui.AudioPanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import uk.me.proeto.iss.ImageSoundData;
import uk.me.proeto.iss.ImageSoundListener;

public class WaveformPanel extends JPanel implements MouseListener, MouseMotionListener, ImageSoundListener {

	private double [] rawSamples = new double [0];
	private double [] smoothedSamples = new double [0];
	private int [] transitions = null;
	private int [] keyFrames = null;
	private double rawMax = 1;
	private double smoothedMax = 1;
	private int selectedAudioFrame = 0;
	private ImageSoundData data;
	
	private static final Color LIGHT_RED = new Color(240,190,190);
	private static final Color DARK_RED = new Color(150,0,0);
	private static final Color DARK_BLUE = new Color(0,0,170);
	private static final Color DARK_GREEN = new Color(0,170,0);
	private static final Color YELLOW = Color.YELLOW;
	
	private int currentStartFrame = 0;
	private int currentEndFrame = 0;
	
	private boolean makingSelection = false;
	private int selectionStartFrame = 0;
	private int selectionEndFrame = 0;
	
	
	public WaveformPanel (ImageSoundData data) {
		this.data = data;
		data.addListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
	}
	
	private void setRawSamples (double [] samples) {
		this.rawSamples = samples;
		rawMax = 0;
		for (int i=0;i<samples.length;i++) {
			if (samples[i]>rawMax) rawMax = samples[i];
		}
	}

	private void setSmoothedSamples (double [] samples) {
		this.smoothedSamples = samples;
		smoothedMax = 0;
		for (int i=0;i<samples.length;i++) {
			if (samples[i]>smoothedMax) smoothedMax = samples[i];
		}
	}

	
	private void setTransitions (int [] transitions, int [] keyFrames) {
		this.transitions = transitions;
		this.keyFrames = keyFrames;
		repaint();
	}
	
	private void setSelectedFrame (int frame) {
		this.selectedAudioFrame = frame;
		
		// Work out if we need to reposition the viewpoint.
		int viewWidth = currentEndFrame - currentStartFrame;
		
		currentStartFrame = selectedAudioFrame - (viewWidth/2);
		if (currentStartFrame < 0) currentStartFrame = 0;
		
		currentEndFrame = currentStartFrame + viewWidth;
		
		if (currentEndFrame > (rawSamples.length-1)) {
			currentEndFrame = rawSamples.length-1;
			currentStartFrame = currentEndFrame - viewWidth;
		}
		
		repaint();
	}
	
	private int getX (int bin) {
		return (int)((getWidth()/(double)(currentEndFrame-currentStartFrame)*(bin-currentStartFrame)));
	}
	
	private int getXFrame (int position) {
		return currentStartFrame+(int)((((currentEndFrame-currentStartFrame))/(double)getWidth())*position);
		
	}
	
	private int getRawYTop (double value) {
		double height = getHeight()/4;
		double proportion = value/rawMax;
		return (int)(height-(height*proportion));
	}

	private int getRawYBottom (double value) {
		double height = getHeight()/4;
		double proportion = value/rawMax;
		return (int)(height+(height*proportion));
	}

	private int getSmoothedYTop (double value) {
		double height = getHeight()/4;
		double proportion = value/smoothedMax;
		return (int)((height*3)-(height*proportion));
	}

	private int getSmoothedYBottom (double value) {
		double height = getHeight()/4;
		double proportion = value/smoothedMax;
		return (int)((height*3)+(height*proportion));
	}

	
	public Dimension getPreferredSize () {
		return new Dimension(1000,150);
	}
	
	public void paint (Graphics g) {
		
		super.paint(g);
		
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		if (rawSamples.length == 0) {
			int x = (getWidth()/2)-((g.getFontMetrics().stringWidth("No audio loaded"))/2);
			g.setColor(Color.DARK_GRAY);
			g.drawString("No audio loaded", x, getHeight()/2);
			return;
		}

		// Shade any selection we're making
		if (makingSelection) {
			g.setColor(YELLOW);
			
			int maxSel = Math.max(selectionStartFrame,selectionEndFrame);
			int minSel = Math.min(selectionStartFrame,selectionEndFrame);
			
			g.fillRect(getX(minSel), 0, getX(maxSel)-getX(minSel), getHeight());
		}
		
		
		// Shade behind the play head
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(getX(selectedAudioFrame)-10, 0, 20, getHeight());
		
		// Draw the play head
		g.setColor(Color.WHITE);
		g.fillRect(getX(selectedAudioFrame)-2, 0, 5, getHeight());

		// See if we can highlight any transitions
		if (transitions != null) {
			g.setColor(LIGHT_RED);
			for (int i=0;i<transitions.length;i++) {
				g.drawLine(getX(transitions[i]), 0, getX(transitions[i]), getHeight());
			}
		}
		
		// See if we can highlight any key frames
		if (keyFrames != null) {
			g.setColor(DARK_RED);
			for (int i=0;i<keyFrames.length;i++) {
				g.fillRect(getX(keyFrames[i])-1, 0, 3, getHeight());
			}
		}
		

		// Now draw the raw waveform
		g.setColor(DARK_BLUE);
		
		int lastX = getX(0);
		for (int i=0;i<rawSamples.length;i++) {
			
			int x = getX(i+1);
//			if (x == lastX) continue;

			int yBottom = getRawYBottom(rawSamples[i]);
			int yTop = getRawYTop(rawSamples[i]);
			int yHeight = yBottom-yTop;
			
			// Need to give something to see if we're looking at silence
			if (yHeight == 0) yHeight++;
			
//			System.err.println("i="+i+" lastX="+lastX+" x="+x+" ytop="+yTop+" ybottom="+yBottom+" max="+max+" value="+samples[i]);

			g.fillRect(lastX, yTop, x-lastX, yHeight);
			lastX = x;
			
		}

		// Now draw the smoothed waveform
		g.setColor(DARK_GREEN);
		
		lastX = getX(0);
		for (int i=0;i<smoothedSamples.length;i++) {
			
			int x = getX(i+1);
//			if (x == lastX) continue;

			int yBottom = getSmoothedYBottom(smoothedSamples[i]);
			int yTop = getSmoothedYTop(smoothedSamples[i]);
			int yHeight = yBottom-yTop;
			
			// Need to give something to see if we're looking at silence
			if (yHeight == 0) yHeight++;
			
//			System.err.println("i="+i+" lastX="+lastX+" x="+x+" ytop="+yTop+" ybottom="+yBottom+" max="+max+" value="+samples[i]);

			g.fillRect(lastX, yTop, x-lastX, yHeight);
			lastX = x;
			
		}

	}

	public void mouseClicked(MouseEvent me) {
		if (SwingUtilities.isRightMouseButton(me)) {
			// We're not setting the play head, we're zooming out.
			int viewWidth = currentEndFrame-currentStartFrame;
			viewWidth *= 2;
			if (viewWidth > rawSamples.length-1) {
				viewWidth = rawSamples.length-1;
			}
			
			currentStartFrame = selectedAudioFrame-(viewWidth/2);
			if (currentStartFrame < 0) currentStartFrame = 0;
			
			currentEndFrame = currentStartFrame+viewWidth;
			if (currentEndFrame >= rawSamples.length) {
				currentEndFrame = rawSamples.length-1;
				currentStartFrame = currentEndFrame-viewWidth;
			}
			repaint();
			return;
		}
		
		int audioBin = getXFrame(me.getX());
		data.setSelectedAudioFrame(audioBin);
	}

	public void mouseEntered(MouseEvent e) {}

	public void mouseExited(MouseEvent e) {}

	public void mousePressed(MouseEvent me) {
		makingSelection = true;
		selectionStartFrame = getXFrame(me.getX());
		selectionEndFrame = getXFrame(me.getX());
	}
	
	public void mouseDragged (MouseEvent me) {
		if (makingSelection) {
			selectionEndFrame = getXFrame(me.getX())+1;
			if (selectionEndFrame >= rawSamples.length) selectionEndFrame = rawSamples.length-1;
		}
		repaint();
		
	}

	public void mouseMoved(MouseEvent e) {}

	public void mouseReleased(MouseEvent e) {
		if (makingSelection && Math.abs(selectionEndFrame-selectionStartFrame) > 5) {
			currentStartFrame = Math.min(selectionStartFrame,selectionEndFrame);
			currentEndFrame = Math.max(selectionStartFrame, selectionEndFrame);
			
		}
		makingSelection = false;

		if (selectedAudioFrame < currentStartFrame || selectedAudioFrame > currentEndFrame) {
			// We can't have the selected audio frame being outside the current selection
			data.setSelectedAudioFrame(currentStartFrame + ((currentEndFrame-currentStartFrame)/2));
		}

		
		repaint();
	}

	public void newAudioFile(ImageSoundData data) {
		setRawSamples(data.audioFile().rawSampleData());
		setSmoothedSamples(data.audioFile().smoothedSampleData());
		currentStartFrame = 0;
		currentEndFrame = rawSamples.length-1;
		repaint();		
	}

	public void newImageSet(ImageSoundData data) {
		if (rawSamples != null) {
			setTransitions(data.synchronisation().videoTransitions(),data.synchronisation().keyFrameAudioFrames());
			repaint();
		}
	}

	public void transitionsUpdated(ImageSoundData data) {
		setTransitions(data.synchronisation().videoTransitions(),data.synchronisation().keyFrameAudioFrames());
		repaint();
	}

	public void smoothingUpdated(ImageSoundData data) {
		setSmoothedSamples(data.audioFile().smoothedSampleData());
		repaint();
	}

	public void audioFrameSelected(ImageSoundData data, int frame) {
		setSelectedFrame(frame);
		
	}

	public void videoFrameSelected(ImageSoundData data, int frame) {
		setSelectedFrame(data.synchronisation().getSoundFrameForImageIndex(frame));
	}
	
}
