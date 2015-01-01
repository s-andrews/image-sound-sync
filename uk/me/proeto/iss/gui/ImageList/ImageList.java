package uk.me.proeto.iss.gui.ImageList;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

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

public class ImageList extends JPanel implements ImageSoundListener, ListSelectionListener {

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
		buttonPanel.add(addButton);
		
		removeButton = new JButton("Remove");
		removeButton.setActionCommand("remove_key_frame");
		removeButton.setEnabled(false);
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


	public void valueChanged(ListSelectionEvent lse) {
		if (lse.getSource().equals(imageTable)) {
			data.setSelectedVideoFrame(imageTable.getSelectedRow());
		}
		else if (lse.getSource().equals(keyFrameTable)) {
			data.setSelectedVideoFrame(data.synchronisation().keyFrames()[keyFrameTable.getSelectedRow()].videoFrame());
		}
		else {
			throw new IllegalStateException("Unknown source for table change "+lse.getSource());
		}
	}

	public void smoothingUpdated(ImageSoundData data) {}

	
	
}
