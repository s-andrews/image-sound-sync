/*
 * Copyright 2014-2015 Simon Andrews
 * 
 * This file is part of Image Sound Sync.
 * Image Sound Sync is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Image Sound Sync is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Image Sound Sync.  If not, see <http://www.gnu.org/licenses/>.
 */

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

		JMenuItem loadProject = new JMenuItem("Load Project...");
		loadProject.setActionCommand("load");
		loadProject.setMnemonic(KeyEvent.VK_L);
		loadProject.addActionListener(this);
		fileMenu.add(loadProject);
		
		JMenuItem saveProject = new JMenuItem("Save Project...");
		saveProject.setActionCommand("save");
		saveProject.setMnemonic(KeyEvent.VK_S);
		saveProject.addActionListener(this);
		fileMenu.add(saveProject);

		JMenuItem saveProjectAs = new JMenuItem("Save Project As...");
		saveProjectAs.setActionCommand("saveas");
		saveProjectAs.setMnemonic(KeyEvent.VK_A);
		saveProjectAs.addActionListener(this);
		fileMenu.add(saveProjectAs);

		fileMenu.addSeparator();
		
		
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
		
		fileMenu.addSeparator();
		
		JMenuItem exit = new JMenuItem("Exit");
		exit.setActionCommand("exit");
		exit.addActionListener(this);
		exit.setMnemonic(KeyEvent.VK_X);
		fileMenu.add(exit);
		
		add(fileMenu);
		
		JMenu playerMenu = new JMenu("Player");
		playerMenu.setMnemonic(KeyEvent.VK_P);
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

		JMenuItem playerPause = new JMenuItem("Pause");
		playerPause.setActionCommand("pause");
		playerPause.addActionListener(this);
		playerPause.setMnemonic(KeyEvent.VK_U);
		playerMenu.add(playerPause);

		
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

		else if (command.equals("pause")) {
			application.pause();
		}

		else if (command.equals("save")) {
			application.saveProject();
		}
		else if (command.equals("saveas")) {
			application.saveProjectAs();
		}
		else if (command.equals("exit")) {
			System.exit(0);
		}
		
		else {
			throw new IllegalArgumentException("No known menu command '"+command+"'");
		}
	}
	
}
