package uk.me.proeto.iss.images;

import java.io.File;

import javax.swing.ImageIcon;

public class ImageSet {

	private File [] imageFiles;
	private ImageIcon [] icons;
	
	
	public ImageSet (File [] imageFiles) {
		this.imageFiles = imageFiles;
		
		icons = new ImageIcon [imageFiles.length];
		
		for (int i=0;i<imageFiles.length;i++) {
			icons[i] = new ImageIcon(imageFiles[i].getAbsolutePath());
		}
		
	}
	
	public File [] files () {
		return imageFiles;
	}
	
	public ImageIcon [] icons () {
		return icons;
	}
	
}
