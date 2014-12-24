package uk.me.proeto.iss;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

import uk.me.proeto.iss.sound.AudioFile;


public class ImageSoundSyncApplication extends JFrame {

	private ImageSoundData data = new ImageSoundData();
	
	public ImageSoundSyncApplication () {
		
		super("Image Sound Sync");
		setJMenuBar(new ImageSoundSyncMenu(this));
		
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
	}
	
	
	public static void main(String[] args) {
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {}

		new ImageSoundSyncApplication();
	}

}
