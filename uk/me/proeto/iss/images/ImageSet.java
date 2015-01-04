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
