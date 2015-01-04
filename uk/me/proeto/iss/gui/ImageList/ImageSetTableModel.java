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

public class ImageSetTableModel extends AbstractTableModel implements ImageSoundListener {

	private ImageSoundData data = null;
	
	public ImageSetTableModel (ImageSoundData data) {
		this.data = data;
		data.addListener(this);
	}
		
	public int getColumnCount() {
		return 3;
	}

	public int getRowCount() {
		if (data.imageSet() == null) return 0;
		return (data.imageSet().files().length);
	}

	public String getColumnName (int col) {
		if (col == 0) return "Index";
		else if (col == 1) return "File";
		else if (col == 2) return "Transition Time";
		return null;
	}
	
	public Object getValueAt(int row, int col) {
		if (col == 0) {
			return new Integer(row);
		}
		else if (col == 1) {
			return data.imageSet().files()[row].getName();
		}
		else if (col == 2) {
			if (data.audioFile() == null) return 0;
			return data.synchronisation().getSoundFrameForImageIndex(row);
		}

		return null;
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
