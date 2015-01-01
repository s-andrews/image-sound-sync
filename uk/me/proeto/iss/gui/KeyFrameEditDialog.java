package uk.me.proeto.iss.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JSlider;

import uk.me.proeto.iss.ImageSoundData;
import uk.me.proeto.iss.sync.KeyFrame;

public class KeyFrameEditDialog extends JDialog implements ActionListener{

	private KeyFrame keyFrame = null;
	
	private JSlider audioSlider;
	private JSlider videoSlider;
	
	public KeyFrameEditDialog (ImageSoundData data) {
		
		setTitle("Add Key Frame");
		
		getContentPane().setLayout(new BorderLayout());
		JPanel sliderPanel = new JPanel();
		audioSlider = new JSlider(1,data.audioFile().rawSampleData().length,1);
		videoSlider = new JSlider(1,data.imageSet().files().length,1);
		
		sliderPanel.add(audioSlider);
		sliderPanel.add(videoSlider);
		
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
		
		setSize(400,200);
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
	
	
}
