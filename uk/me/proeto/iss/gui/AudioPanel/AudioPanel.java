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

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import uk.me.proeto.iss.ImageSoundData;

public class AudioPanel extends JPanel {

	private WaveformPanel waveformPanel;
	private JSlider smoothingSlider;
	private JSlider minGapSlider;
	private ImageSoundData data;
	
	public AudioPanel (ImageSoundData data) {
		this.data = data;
		setLayout(new GridBagLayout());
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx=1;
		gbc.gridy=1;
		gbc.weightx=0.5;
		gbc.weighty=0.9;
		gbc.fill = GridBagConstraints.BOTH;
		
		waveformPanel = new WaveformPanel(data);
		add(waveformPanel,gbc);
				
		gbc.gridx=2;
		gbc.gridy=1;
		gbc.weightx=0.01;
		
		JPanel smoothingSliderPanel = new JPanel();
		smoothingSliderPanel.setLayout(new BorderLayout());
		smoothingSliderPanel.add(new JLabel("Smoothing",JLabel.CENTER),BorderLayout.NORTH);
		
		smoothingSlider = new JSlider(JSlider.VERTICAL,1, 200, 1);
		smoothingSlider.addChangeListener(new ChangeListener() {
			
			public void stateChanged(ChangeEvent ce) {
				System.out.println("Set smoothing to "+smoothingSlider.getValue());
				AudioPanel.this.data.setSmoothing(smoothingSlider.getValue());
			}
		});
		
		smoothingSliderPanel.add(smoothingSlider,BorderLayout.CENTER);
		
		add(smoothingSliderPanel,gbc);

		
		gbc.gridx=3;
		gbc.gridy=1;
		gbc.gridheight=2;
		gbc.weightx=0.01;
		
		JPanel minGapSliderPanel = new JPanel();
		minGapSliderPanel.setLayout(new BorderLayout());
		minGapSliderPanel.add(new JLabel("Min Gap",JLabel.CENTER),BorderLayout.NORTH);
		
		minGapSlider = new JSlider(JSlider.VERTICAL,0, 50, 0);
		minGapSlider.addChangeListener(new ChangeListener() {
			
			public void stateChanged(ChangeEvent ce) {
				System.out.println("Set minGap to "+minGapSlider.getValue());
				AudioPanel.this.data.setMinGap(minGapSlider.getValue());
			}
		});
		
		minGapSliderPanel.add(minGapSlider,BorderLayout.CENTER);
		
		add(minGapSliderPanel,gbc);

		
	}
	

	

	
}
