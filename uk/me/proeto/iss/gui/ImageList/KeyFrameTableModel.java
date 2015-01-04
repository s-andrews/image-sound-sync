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

import javax.swing.table.AbstractTableModel;

import uk.me.proeto.iss.ImageSoundData;
import uk.me.proeto.iss.ImageSoundListener;
import uk.me.proeto.iss.sync.KeyFrame;

public class KeyFrameTableModel extends AbstractTableModel implements ImageSoundListener {

	private ImageSoundData data = null;
	
	public KeyFrameTableModel (ImageSoundData data) {
		this.data = data;
		data.addListener(this);
	}
		
	public int getColumnCount() {
		return 3;
	}

	public int getRowCount() {
		if (data.imageSet() == null) return 0;
		return (data.synchronisation().keyFrames().length);
	}

	public String getColumnName (int col) {
		if (col == 0) return "Index";
		else if (col == 1) return "Video Frame";
		else if (col == 2) return "Audio Frame";
		return null;
	}
	
	public Object getValueAt(int row, int col) {
		if (col == 0) {
			return new Integer(row);
		}
		else if (col == 1) {
			if (data.audioFile() == null) return 0;
			return data.synchronisation().keyFrames()[row].videoFrame();
		}
		else if (col == 2) {
			if (data.audioFile() == null) return 0;
			return data.synchronisation().keyFrames()[row].audioFrame();
		}

		return null;
	}
	
	public KeyFrame getKeyFrameAt (int row) {
		return data.synchronisation().keyFrames()[row];
	}

	public void newAudioFile(ImageSoundData data) {}

	public void newImageSet(ImageSoundData data) {
		fireTableDataChanged();
	}

	public void transitionsUpdated(ImageSoundData data) {
		fireTableDataChanged();		
	}

	public void smoothingUpdated(ImageSoundData data) {
		
	}

	public void audioFrameSelected(ImageSoundData data, int frame) {}

	public void videoFrameSelected(ImageSoundData data, int frame) {}

}
