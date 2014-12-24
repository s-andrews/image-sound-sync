package uk.me.proeto.iss.gui.AudioPanel;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;

import uk.me.proeto.iss.ImageSoundData;
import uk.me.proeto.iss.ImageSoundListener;
import uk.me.proeto.iss.images.ImageSet;
import uk.me.proeto.iss.sound.AudioFile;

public class AudioPanel extends JPanel implements ImageSoundListener {

	private WaveformPanel rawWaveform;
	private WaveformPanel normalisedWaveform;
	
	public AudioPanel (ImageSoundData data) {
		data.addListener(this);
		setLayout(new GridBagLayout());
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx=1;
		gbc.gridy=1;
		gbc.weightx=0.5;
		gbc.weighty=0.9;
		
		rawWaveform = new WaveformPanel(new Color(0,0,200));
		add(rawWaveform,gbc);
		
		gbc.gridy++;
		
		normalisedWaveform = new WaveformPanel(new Color(0,200,0));
		add(normalisedWaveform,gbc);
		
		
		
	}
	
	@Override
	public void newAudioFile(AudioFile audioFile) {

		rawWaveform.setSamples(audioFile.rawSampleData());
		normalisedWaveform.setSamples(audioFile.rawSampleData()); // TODO: Make normalised
	}

	@Override
	public void newImageSet(ImageSet imageSet) {
		// TODO Auto-generated method stub
		
	}

	
		
		
		
	
	
	
	

	
}
