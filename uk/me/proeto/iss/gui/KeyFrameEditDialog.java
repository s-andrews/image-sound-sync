package uk.me.proeto.iss.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import uk.me.proeto.iss.ImageSoundData;
import uk.me.proeto.iss.gui.AudioPanel.WaveformPanel;
import uk.me.proeto.iss.sync.KeyFrame;

public class KeyFrameEditDialog extends JDialog implements ActionListener, ChangeListener {

	private KeyFrame keyFrame = null;
	
	private JSlider audioSlider;
	private JSlider videoSlider;
	private WaveformPanel waveform;
	private ImagePanel imagePanel;
	private ImageSoundData data;
	private JButton addButton;
	
	public KeyFrameEditDialog (ImageSoundData data) {
		this.data = data;
		setTitle("Add Key Frame");
		
		getContentPane().setLayout(new BorderLayout());
		JPanel sliderPanel = new JPanel();
		sliderPanel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx=0;
		gbc.gridy=0;
		gbc.weightx=0.5;
		gbc.weighty=0.01;
		gbc.fill=GridBagConstraints.BOTH;
		
		sliderPanel.add(new JLabel("Audio Frame"),gbc);
		
		gbc.gridy++;
		
		audioSlider = new JSlider(1,data.audioFile().rawSampleData().length,Math.max(1,data.currentAudioFrame()));
		audioSlider.addChangeListener(this);
		sliderPanel.add(audioSlider,gbc);

		
		gbc.gridy++;
		gbc.weighty=0.33;
		
		waveform = new WaveformPanel(Color.RED, data);
		waveform.setSamples(data.audioFile().rawSampleData());
		waveform.setSelectedFrame(audioSlider.getValue());
		sliderPanel.add(waveform,gbc);
		
		gbc.gridy++;
		gbc.weighty=0.01;

		sliderPanel.add(new JLabel("Video Frame"),gbc);

		gbc.gridy++;

		videoSlider = new JSlider(1,data.imageSet().files().length,Math.max(data.currentVideoFrame(),1));
		videoSlider.addChangeListener(this);
		
		sliderPanel.add(videoSlider,gbc);
		
		gbc.gridy++;
		gbc.weighty=0.99;

		
		imagePanel = new ImagePanel(data);
		// We don't want this to respond to global events
		data.removeListener(imagePanel);
		
		sliderPanel.add(imagePanel,gbc);
		
		imagePanel.videoFrameSelected(data, videoSlider.getValue());
		
		getContentPane().add(sliderPanel,BorderLayout.CENTER);
		
		JPanel buttonPanel = new JPanel();
		
		addButton = new JButton("Add Key Frame");
		addButton.setActionCommand("add");
		addButton.addActionListener(this);
		addButton.setEnabled(false);
		buttonPanel.add(addButton);
		
		JButton cancelButton = new JButton("Cancel");
		cancelButton.setActionCommand("cancel");
		cancelButton.addActionListener(this);
		buttonPanel.add(cancelButton);
		
		getContentPane().add(buttonPanel,BorderLayout.SOUTH);
		stateChanged(null);
		
		setSize(700,700);
		setLocationRelativeTo(null);
		setModal(true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setVisible(true);
		
	}
	
	public KeyFrame keyFrame () {
		return keyFrame;
	}

	public void actionPerformed(ActionEvent ae) {
		if (ae.getActionCommand().equals("add")) {
			// TODO: Add some logic to see if this is a valid frame to select.
			keyFrame = new KeyFrame(audioSlider.getValue(), videoSlider.getValue());
		}
		
		setVisible(false);
		dispose();
	}

	public void stateChanged(ChangeEvent ce) {
		
		// Check whether we can enable the add key frame button.
		KeyFrame testFrame = new KeyFrame(audioSlider.getValue(), videoSlider.getValue());
		addButton.setEnabled(data.synchronisation().isValidKeyFrame(testFrame));
		
		if (ce == null) return; // This was the initial trigger to set up the correct state
		
		if (ce.getSource().equals(audioSlider)) {
			waveform.setSelectedFrame(audioSlider.getValue());
		}
		else if (ce.getSource().equals(videoSlider)) {
			imagePanel.videoFrameSelected(data, videoSlider.getValue());
		}
	}
	
	
}
