package uk.me.proeto.iss.images;

import java.awt.Image;
import java.io.File;

import javax.swing.ImageIcon;

public class ImageSet {

	private File [] imageFiles;
	private Image [] images;
	
	
	public ImageSet (File [] imageFiles) {
		this.imageFiles = imageFiles;
		
		images = new Image [imageFiles.length];
		
		for (int i=0;i<imageFiles.length;i++) {
			images[i] = (new ImageIcon(imageFiles[i].getAbsolutePath())).getImage();
		}
		
	}
	
	public File [] files () {
		return imageFiles;
	}
	
	public Image [] images () {
		return images;
	}
	
	public Image getImage (int index) {
		return images[index];
	}
	
}
