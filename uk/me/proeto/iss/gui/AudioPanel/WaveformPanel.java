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

import javax.swing.JPanel;

import uk.me.proeto.iss.ImageSoundData;

public class WaveformPanel extends JPanel implements MouseListener {

	private double [] samples = new double [0];
	private int [] transitions = null;
	private int [] keyFrames = null;
	private double max = 1;
	private Color colour;
	private int selectedAudioFrame = 0;
	private ImageSoundData data;
	
	private static final Color LIGHT_RED = new Color(240,190,190);
	private static final Color DARK_RED = new Color(150,0,0);
	
	public WaveformPanel (Color colour, ImageSoundData data) {
		this.data = data;
		this.colour = colour;
		addMouseListener(this);
	}
	
	public void setSamples (double [] samples) {
		this.samples = samples;
		max = 0;
		for (int i=0;i<samples.length;i++) {
			if (samples[i]>max) max = samples[i];
		}
		repaint();
	}
	
	public void setTransitions (int [] transitions, int [] keyFrames) {
		this.transitions = transitions;
		this.keyFrames = keyFrames;
		repaint();
	}
	
	public void setSelectedFrame (int frame) {
		this.selectedAudioFrame = frame;
		repaint();
	}
	
	private int getX (int bin) {
		return (int)((getWidth()/(double)samples.length)*bin);
	}
	
	private int getXFrame (int position) {
		return (int)((samples.length/(double)getWidth())*position);
		
	}
	
	private int getYTop (double value) {
		double height = getHeight()/2;
		double proportion = value/max;
		return (int)(height-(height*proportion));
	}

	private int getYBottom (double value) {
		double height = getHeight()/2;
		double proportion = value/max;
		return (int)(height+(height*proportion));
	}
	
	public Dimension getPreferredSize () {
		return new Dimension(1000,150);
	}
	
	public void paint (Graphics g) {
		
		super.paint(g);
		
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		if (samples.length == 0) {
			int x = (getWidth()/2)-((g.getFontMetrics().stringWidth("No audio loaded"))/2);
			g.setColor(Color.DARK_GRAY);
			g.drawString("No audio loaded", x, getHeight()/2);
			return;
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
		
		
		g.setColor(colour);
		
		int lastX = getX(0);
		for (int i=0;i<samples.length;i++) {
			
			int x = getX(i+1);
//			if (x == lastX) continue;

			int yBottom = getYBottom(samples[i]);
			int yTop = getYTop(samples[i]);
			int yHeight = yBottom-yTop;
			
			// Need to give something to see if we're looking at silence
			if (yHeight == 0) yHeight++;
			
//			System.err.println("i="+i+" lastX="+lastX+" x="+x+" ytop="+yTop+" ybottom="+yBottom+" max="+max+" value="+samples[i]);

			g.fillRect(lastX, yTop, x-lastX, yHeight);
			lastX = x;
			
		}
		
		
		
		
	}

	public void mouseClicked(MouseEvent me) {
		int audioBin = getXFrame(me.getX());
		data.setSelectedAudioFrame(audioBin);
	}

	public void mouseEntered(MouseEvent e) {}

	public void mouseExited(MouseEvent e) {}

	public void mousePressed(MouseEvent e) {}

	public void mouseReleased(MouseEvent e) {}
	
}
