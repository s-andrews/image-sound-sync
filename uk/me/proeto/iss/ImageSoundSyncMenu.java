package uk.me.proeto.iss;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

public class ImageSoundSyncMenu extends JMenuBar implements ActionListener {

	private ImageSoundSyncApplication application;
	
	public ImageSoundSyncMenu (ImageSoundSyncApplication application) {
		this.application = application;
		
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		
		JMenuItem openAudio = new JMenuItem("Open audio...");
		openAudio.setActionCommand("open_audio");
		openAudio.setMnemonic(KeyEvent.VK_A);
		openAudio.addActionListener(this);
		fileMenu.add(openAudio);

		JMenuItem openImages = new JMenuItem("Load images...");
		openImages.setActionCommand("open_images");
		openImages.setMnemonic(KeyEvent.VK_I);
		openImages.addActionListener(this);
		fileMenu.add(openImages);
		
		add(fileMenu);
		
		JMenu playerMenu = new JMenu("View");
		playerMenu.setMnemonic(KeyEvent.VK_V);
		JMenuItem playerPlay = new JMenuItem("Play");
		playerPlay.setActionCommand("play");
		playerPlay.addActionListener(this);
		playerPlay.setMnemonic(KeyEvent.VK_P);
		playerMenu.add(playerPlay);

		JMenuItem playerStop = new JMenuItem("Stop");
		playerStop.setActionCommand("stop");
		playerStop.addActionListener(this);
		playerStop.setMnemonic(KeyEvent.VK_S);
		playerMenu.add(playerStop);

		
		add(playerMenu);
		
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		String command = e.getActionCommand();
				
		if (command.equals("open_audio")) {
			try {
				application.readAudio();
			}
			catch (IOException ioe) {
				JOptionPane.showMessageDialog(application, ioe.getMessage(), "Can't load audio", JOptionPane.ERROR_MESSAGE);
			}
		}

		else if (command.equals("open_images")) {
			try {
				application.loadImages();
			}
			catch (IOException ioe) {
				JOptionPane.showMessageDialog(application, ioe.getMessage(), "Can't load images", JOptionPane.ERROR_MESSAGE);
			}
		}

		
		
		else if (command.equals("play")) {
			application.play();
		}
		
		else if (command.equals("stop")) {
			application.stop();
		}
		
		else {
			throw new IllegalArgumentException("No known menu command '"+command+"'");
		}
	}
	
}
