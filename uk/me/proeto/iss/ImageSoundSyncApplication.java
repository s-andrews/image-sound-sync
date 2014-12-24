package uk.me.proeto.iss;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

import uk.me.proeto.iss.gui.AudioPanel;
import uk.me.proeto.iss.sound.AudioFile;


public class ImageSoundSyncApplication extends JFrame {

	private ImageSoundData data = new ImageSoundData();
	private AudioPanel audioPanel = new AudioPanel();
	
	public ImageSoundSyncApplication () {
		
		super("Image Sound Sync");
		setJMenuBar(new ImageSoundSyncMenu(this));
		
		setLayout(new BorderLayout());
		add(audioPanel,BorderLayout.NORTH);
		
		
		setSize(1024,768);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);
		
	}
	
	public void readAudio () throws IOException {
		JFileChooser chooser = new JFileChooser();
		chooser.setMultiSelectionEnabled(false);
		chooser.setFileFilter(new FileFilter() {
		
			public String getDescription() {
				return "WAV files";
			}
		
			public boolean accept(File f) {
				if (f.isDirectory() || f.getName().toLowerCase().endsWith(".wav")) {
					return true;
				}
				else {
					return false;
				}
			}
		
		});
		
		int result = chooser.showOpenDialog(this);
		if (result == JFileChooser.CANCEL_OPTION) return;

		File file = chooser.getSelectedFile();
		readAudio(file);
		
	}
	
	public void readAudio (File file) throws IOException {
		data.setAudioFile(new AudioFile(file));
		audioPanel.setSamples(data.audioFile().rawSampleData());
	}
	
	
	public static void main(String[] args) {
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {}

		new ImageSoundSyncApplication();
	}

}
