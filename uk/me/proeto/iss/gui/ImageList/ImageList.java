package uk.me.proeto.iss.gui.ImageList;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import uk.me.proeto.iss.ImageSoundData;
import uk.me.proeto.iss.ImageSoundListener;

public class ImageList extends JPanel implements ImageSoundListener, ListSelectionListener {

	private ImageSetTableModel model;
	private JTable table;
	private int selectedIndex = -1;
	private ImageSoundData data;
	
	public ImageList (ImageSoundData data) {
		data.addListener(this);
		this.data = data;
		model = new ImageSetTableModel();
		table = new JTable(model);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getSelectionModel().addListSelectionListener(this);
		
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


	public void valueChanged(ListSelectionEvent lse) {
		data.setSelectedVideoFrame(table.getSelectedRow());
	}

	public void smoothingUpdated(ImageSoundData data) {}

	
	
}
