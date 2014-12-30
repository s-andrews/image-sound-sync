package uk.me.proeto.iss.gui.AudioPanel;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;

import uk.me.proeto.iss.ImageSoundData;
import uk.me.proeto.iss.ImageSoundListener;

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
		gbc.fill = GridBagConstraints.BOTH;
		
		rawWaveform = new WaveformPanel(new Color(0,0,200));
		add(rawWaveform,gbc);
		
		gbc.gridy++;
		
		normalisedWaveform = new WaveformPanel(new Color(0,200,0));
		add(normalisedWaveform,gbc);	
	}
	
	public void newAudioFile(ImageSoundData data) {
		rawWaveform.setSamples(data.audioFile().rawSampleData());
		normalisedWaveform.setSamples(data.audioFile().smoothedSampleData());
	}

	public void newImageSet(ImageSoundData data) {
		transitionsUpdated(data);
	}

	public void transitionsUpdated(ImageSoundData data) {
		// Redraw the waveforms to highlight and transitions which
		// have been calculated.
		rawWaveform.setTransitions(data.synchronisation().videoTransitions());
		normalisedWaveform.setTransitions(data.synchronisation().videoTransitions());		
	}

	public void audioFrameSelected(ImageSoundData data, int frame) {
		rawWaveform.setSelectedFrame(frame);
		normalisedWaveform.setSelectedFrame(frame);
	}

	public void videoFrameSelected(ImageSoundData data, int frame) {
		int audioFrame = data.synchronisation().getSoundFrameForImageIndex(frame);
		audioFrameSelected(data, audioFrame);
	}
	

	
}
