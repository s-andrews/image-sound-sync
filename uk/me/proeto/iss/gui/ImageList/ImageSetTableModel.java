package uk.me.proeto.iss.gui.ImageList;

import javax.swing.table.AbstractTableModel;

import uk.me.proeto.iss.images.ImageSet;

public class ImageSetTableModel extends AbstractTableModel {

	private ImageSet imageSet = null;
	
	public void setImages (ImageSet imageSet) {
		this.imageSet = imageSet;
		fireTableDataChanged();
	}
	
	public int getColumnCount() {
		return 2;
	}

	public int getRowCount() {
		if (imageSet == null) return 0;
		return (imageSet.images().length);
	}

	public String getColumnName (int col) {
		if (col == 0) return "Index";
		if (col == 1) return "File";
		return null;
	}
	
	public Object getValueAt(int row, int col) {
		if (col == 0) {
			return new Integer(row);
		}
		else if (col == 1) {
			return imageSet.files()[row].getName();
		}
		return null;
	}

}
