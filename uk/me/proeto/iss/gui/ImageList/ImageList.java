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

package uk.me.proeto.iss.gui.ImageList;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import uk.me.proeto.iss.ImageSoundData;
import uk.me.proeto.iss.ImageSoundListener;
import uk.me.proeto.iss.gui.KeyFrameEditor.KeyFrameEditDialog;
import uk.me.proeto.iss.sync.KeyFrame;

public class ImageList extends JPanel implements ImageSoundListener, ListSelectionListener, ActionListener {

	private ImageSetTableModel imageModel;
	private JTable imageTable;
	private KeyFrameTableModel keyFrameModel;
	private JTable keyFrameTable;
	private int selectedIndex = -1;
	private ImageSoundData data;
	
	private JButton addButton;
	private JButton removeButton;

		
	public ImageList (ImageSoundData data) {
		data.addListener(this);
		this.data = data;
		imageModel = new ImageSetTableModel(data);
		imageTable = new JTable(imageModel);
		imageTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		imageTable.getSelectionModel().addListSelectionListener(this);
		
		keyFrameModel = new KeyFrameTableModel(data);
		keyFrameTable = new JTable(keyFrameModel);
		keyFrameTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		keyFrameTable.getSelectionModel().addListSelectionListener(this);
		
		setLayout(new GridBagLayout());
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx=1;
		gbc.gridy=1;
		gbc.weightx=0.5;
		gbc.weighty=0.01;
		gbc.fill = GridBagConstraints.BOTH;
		
		add(new JLabel("Images",JLabel.CENTER),gbc);
		
		gbc.gridy++;
		gbc.weighty=0.99;
		
		add(new JScrollPane(imageTable),gbc);

		gbc.gridy++;
		gbc.weighty=0.01;

		add(new JLabel("Key Frames",JLabel.CENTER),gbc);

		gbc.gridy++;
		gbc.weighty=0.66;
		
		add(new JScrollPane(keyFrameTable),gbc);

		gbc.gridy++;
		gbc.weighty=0.01;

		JPanel buttonPanel = new JPanel();
		
		addButton = new JButton("Add");
		addButton.setActionCommand("add_key_frame");
		addButton.addActionListener(this);
		buttonPanel.add(addButton);
		
		removeButton = new JButton("Remove");
		removeButton.setActionCommand("remove_key_frame");
		removeButton.setEnabled(false);
		removeButton.addActionListener(this);
		buttonPanel.add(removeButton);
		
		add(buttonPanel,gbc);
		
		
	}
	
	
	public void newAudioFile(ImageSoundData data) {}

	public void newImageSet(ImageSoundData data) {}

	public void transitionsUpdated(ImageSoundData data) {}

	public void audioFrameSelected(ImageSoundData data, int frame) {
		int videoFrame = data.synchronisation().getImageIndexForSoundFrame(frame);
		videoFrameSelected(data, videoFrame);
	}

	public void videoFrameSelected(ImageSoundData data, int frame) {
		if (frame != selectedIndex) {
			imageTable.setRowSelectionInterval(frame, frame);
			selectedIndex = frame;
		}
	}


	public void smoothingUpdated(ImageSoundData data) {}


	public void valueChanged(ListSelectionEvent lse) {

		if (keyFrameTable.getSelectedRow()>=0) {
			removeButton.setEnabled(true);
		}
		else {
			removeButton.setEnabled(false);			
		}
		
		if (lse.getSource().equals(imageTable.getSelectionModel())) {
			if (imageTable.getSelectedRow() >=0) {
				data.setSelectedVideoFrame(imageTable.getSelectedRow());
			}
		}
		else if (lse.getSource().equals(keyFrameTable.getSelectionModel())) {
			if (keyFrameTable.getSelectedRow()>=0) {
				data.setSelectedVideoFrame(data.synchronisation().keyFrames()[keyFrameTable.getSelectedRow()].videoFrame());
			}
		}
		else {
			throw new IllegalStateException("Unknown source for table change "+lse.getSource());
		}
	}

	public void actionPerformed(ActionEvent ae) {
		if (ae.getActionCommand().equals("add_key_frame")) {
			KeyFrameEditDialog ked = new KeyFrameEditDialog(data);
			if (ked.keyFrame() != null) {
				data.addKeyFrame(ked.keyFrame());
			}
		}
		else if (ae.getActionCommand().equals("remove_key_frame")) {
			KeyFrame kf = keyFrameModel.getKeyFrameAt(keyFrameTable.getSelectedRow());
			data.removeKeyFrame(kf);
		}
		
	}

	
	
}
