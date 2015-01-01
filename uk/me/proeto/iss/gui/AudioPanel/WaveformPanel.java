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
	private double max = 1;
	private Color colour;
	private int selectedAudioFrame = 0;
	private ImageSoundData data;
	
	private static final Color DARK_RED = new Color(180,0,0);
	
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
	
	public void setTransitions (int [] transitions) {
		this.transitions = transitions;
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

		// Shade behind the play head
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(getX(selectedAudioFrame)-10, 0, 20, getHeight());
		
		// Draw the play head
		g.setColor(Color.WHITE);
		g.fillRect(getX(selectedAudioFrame)-2, 0, 5, getHeight());

		// See if we can highlight any transitions
		if (transitions != null) {
			g.setColor(DARK_RED);
			for (int i=0;i<transitions.length;i++) {
				g.drawLine(getX(transitions[i]), 0, getX(transitions[i]), getHeight());
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
