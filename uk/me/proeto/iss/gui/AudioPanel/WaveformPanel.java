package uk.me.proeto.iss.gui.AudioPanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

public class WaveformPanel extends JPanel {

	private double [] samples = new double [0];
	private double max = 1;
	private Color colour;
	
	public WaveformPanel (Color colour) {
		this.colour = colour;
	}
	
	public void setSamples (double [] samples) {
		this.samples = samples;
		max = 0;
		for (int i=0;i<samples.length;i++) {
			if (samples[i]>max) max = samples[i];
		}
		repaint();
	}
	
	private int getX (int bin) {
		return (int)((getWidth()/(double)samples.length)*bin);
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
		return new Dimension(1000,120);
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
	
}
