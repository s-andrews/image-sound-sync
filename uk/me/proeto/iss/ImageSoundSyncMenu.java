package uk.me.proeto.iss;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class ImageSoundSyncMenu extends JMenuBar implements ActionListener {

	private ImageSoundSyncApplication application;
	
	public ImageSoundSyncMenu (ImageSoundSyncApplication application) {
		this.application = application;
		
		JMenu fileMenu = new JMenu("File");
		JMenuItem openAudio = new JMenuItem("Open audio...");
		openAudio.setActionCommand("open_audio");
		openAudio.setMnemonic(KeyEvent.VK_A);
		openAudio.addActionListener(this);
		fileMenu.add(openAudio);
		
		
		
		
		add(fileMenu);
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		String command = e.getActionCommand();
		
		if (command.equals("open_audio")) {
			application.readAudio();
		}
		
		
		else {
			throw new IllegalArgumentException("No known menu command '"+command+"'");
		}
	}
	
}
