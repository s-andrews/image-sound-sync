package uk.me.proeto.iss.gui.ImageList;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import uk.me.proeto.iss.ImageSoundData;
import uk.me.proeto.iss.ImageSoundListener;

public class ImageList extends JPanel implements ImageSoundListener {

	private ImageSetTableModel model;
	private JTable table;
	private int selectedIndex = -1;
	
	public ImageList (ImageSoundData data) {
		data.addListener(this);
		model = new ImageSetTableModel();
		table = new JTable(model);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		setLayout(new BorderLayout());
		add(new JScrollPane(table),BorderLayout.CENTER);
		
	}
	
	
	public void newAudioFile(ImageSoundData data) {}

	public void newImageSet(ImageSoundData data) {
		model.setImages(data.imageSet());
	}

	public void transitionsUpdated(ImageSoundData data) {}

	public void audioFrameSelected(ImageSoundData data, int frame) {
		int videoFrame = data.synchronisation().getImageIndexForSoundFrame(frame);
		videoFrameSelected(data, videoFrame);
	}

	public void videoFrameSelected(ImageSoundData data, int frame) {
		if (frame != selectedIndex) {
			table.setRowSelectionInterval(frame, frame);
			selectedIndex = frame;
		}
	}

	
	
}
