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

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

import uk.me.proeto.iss.gui.ImagePanel;
import uk.me.proeto.iss.gui.AudioPanel.AudioPanel;
import uk.me.proeto.iss.gui.ImageList.ImageList;
import uk.me.proeto.iss.images.ImageSet;
import uk.me.proeto.iss.player.PreviewPlayer;
import uk.me.proeto.iss.sound.AudioFile;


public class ImageSoundSyncApplication extends JFrame {

	private ImageSoundData data = new ImageSoundData();
	private AudioPanel audioPanel = new AudioPanel(data);
	private PreviewPlayer previewPlayer = null;
	
	public ImageSoundSyncApplication () {
		
		super("Image Sound Sync");
		setJMenuBar(new ImageSoundSyncMenu(this));
		
		JSplitPane topBottomPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		JSplitPane leftRightPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		setContentPane(topBottomPane);
		
		topBottomPane.setTopComponent(audioPanel);
		topBottomPane.setBottomComponent(leftRightPane);
		
		leftRightPane.setLeftComponent(new ImagePanel(data));
		leftRightPane.setRightComponent(new ImageList(data));
		
		setSize(1024,768);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
				
		setVisible(true);
		leftRightPane.setDividerLocation(0.65);
		topBottomPane.setDividerLocation(0.25);

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
	
	public void loadImages () throws IOException {
		JFileChooser chooser = new JFileChooser();
		chooser.setMultiSelectionEnabled(true);
		chooser.setFileFilter(new FileFilter() {
		
			public String getDescription() {
				return "JPEG files";
			}
		
			public boolean accept(File f) {
				if (f.isDirectory() || f.getName().toLowerCase().endsWith(".jpg") || f.getName().toLowerCase().endsWith(".jpeg")) {
					return true;
				}
				else {
					return false;
				}
			}
		
		});
		
		int result = chooser.showOpenDialog(this);
		if (result == JFileChooser.CANCEL_OPTION) return;

		File [] files = chooser.getSelectedFiles();
		loadImages(files);
		
	}
	
	public void loadImages (File [] files) throws IOException {
		data.setImageSet(new ImageSet(files));
	}
	
	
	public void play () {
		// TODO: Check we have everything we need to actually play.
		previewPlayer = new PreviewPlayer(data);
	}
	
	public void stop () {
		if (previewPlayer != null) {
			previewPlayer.stop();
		}
	}
	
	public void pause () {
		if (previewPlayer != null) {
			previewPlayer.pause();
		}

	}

	public static void main(String[] args) {
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {}

		new ImageSoundSyncApplication();
	}

}
