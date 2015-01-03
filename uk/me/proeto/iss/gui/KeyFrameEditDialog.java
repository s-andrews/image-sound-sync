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
		
		// TODO: Start from the current audio head
		audioSlider = new JSlider(1,data.audioFile().rawSampleData().length,1);
		audioSlider.addChangeListener(this);
		sliderPanel.add(audioSlider,gbc);

		
		gbc.gridy++;
		gbc.weighty=0.33;
		
		waveform = new WaveformPanel(Color.RED, data);
		waveform.setSamples(data.audioFile().rawSampleData());
		sliderPanel.add(waveform,gbc);
		
		gbc.gridy++;
		gbc.weighty=0.01;

		sliderPanel.add(new JLabel("Video Frame"),gbc);

		gbc.gridy++;

		// TODO: Start from the current selected video frame
		videoSlider = new JSlider(1,data.imageSet().files().length,1);
		videoSlider.addChangeListener(this);
		
		sliderPanel.add(videoSlider,gbc);
		
		gbc.gridy++;
		gbc.weighty=0.99;

		
		imagePanel = new ImagePanel(data);
		// We don't want this to respond to global events
		data.removeListener(imagePanel);
		
		sliderPanel.add(imagePanel,gbc);
		
		imagePanel.videoFrameSelected(data, 0);
		
		getContentPane().add(sliderPanel,BorderLayout.CENTER);
		
		JPanel buttonPanel = new JPanel();
		
		JButton addButton = new JButton("Add Key Frame");
		addButton.setActionCommand("add");
		addButton.addActionListener(this);
		buttonPanel.add(addButton);
		
		JButton cancelButton = new JButton("Cancel");
		cancelButton.setActionCommand("cancel");
		cancelButton.addActionListener(this);
		buttonPanel.add(cancelButton);
		
		getContentPane().add(buttonPanel,BorderLayout.SOUTH);
		
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

		if (ce.getSource().equals(audioSlider)) {
			waveform.setSelectedFrame(audioSlider.getValue());
		}
		else if (ce.getSource().equals(videoSlider)) {
			imagePanel.videoFrameSelected(data, videoSlider.getValue());
		}
	}
	
	
}
