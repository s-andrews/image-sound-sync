package uk.me.proeto.iss.gui.AudioPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import uk.me.proeto.iss.ImageSoundData;
import uk.me.proeto.iss.ImageSoundListener;
import uk.me.proeto.iss.sound.AudioFile;

public class AudioPanel extends JPanel implements ImageSoundListener {

	private WaveformPanel rawWaveform;
	private WaveformPanel normalisedWaveform;
	private JSlider smoothingSlider;
	private JSlider minGapSlider;
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
		
		rawWaveform = new WaveformPanel(new Color(0,0,170),data);
		add(rawWaveform,gbc);
		
		gbc.gridy++;
		
		normalisedWaveform = new WaveformPanel(new Color(0,140,0),data);
		add(normalisedWaveform,gbc);
		
		gbc.gridx=2;
		gbc.gridy=1;
		gbc.gridheight=2;
		gbc.weightx=0.01;
		
		JPanel smoothingSliderPanel = new JPanel();
		smoothingSliderPanel.setLayout(new BorderLayout());
		smoothingSliderPanel.add(new JLabel("Smoothing",JLabel.CENTER),BorderLayout.NORTH);
		
		smoothingSlider = new JSlider(JSlider.VERTICAL,0, 50, AudioFile.SAMPLES_PER_SECOND);
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

	public void smoothingUpdated(ImageSoundData data) {
		normalisedWaveform.setSamples(data.audioFile().smoothedSampleData());
	}
	

	
}
