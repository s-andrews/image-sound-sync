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
	private ImageSoundData data;
	
	public AudioPanel (ImageSoundData data) {
		this.data = data;
		data.addListener(this);
		setLayout(new GridBagLayout());
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx=1;
		gbc.gridy=1;
		gbc.weightx=0.5;
		gbc.weighty=0.9;
		gbc.fill = GridBagConstraints.BOTH;
		
		rawWaveform = new WaveformPanel(new Color(0,0,200));
		add(rawWaveform,gbc);
		
		gbc.gridy++;
		
		normalisedWaveform = new WaveformPanel(new Color(0,200,0));
		add(normalisedWaveform,gbc);
		
		
		
	}
	
	public void newAudioFile(AudioFile audioFile) {

		rawWaveform.setSamples(audioFile.rawSampleData());
		normalisedWaveform.setSamples(audioFile.smoothedSampleData());
	}

	public void newImageSet(ImageSet imageSet) {
		// Redraw the waveforms to highlight and transitions which might
		// have been calculated.
		rawWaveform.setTransitions(data.synchronisation().videoTransitions());
		normalisedWaveform.setTransitions(data.synchronisation().videoTransitions());
	}

	
		
		
		
	
	
	
	

	
}
