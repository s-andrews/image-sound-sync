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

package uk.me.proeto.iss.gui;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

import uk.me.proeto.iss.ImageSoundData;
import uk.me.proeto.iss.ImageSoundListener;

public class ImagePanel extends JPanel implements ImageSoundListener {
	private Image currentImage = null;
	private int currentImageIndex = -1;

	public ImagePanel (ImageSoundData data) {
		data.addListener(this);
	}
	
	
	public void paint (Graphics g) {
		if (currentImage != null) {
			int width,height,x,y;
			float originalRatio = (float)currentImage.getWidth(null) / currentImage.getHeight(null);
			//			System.out.println("Original ratio is "+originalRatio);

			float thisRatio = (float)getWidth()/getHeight();

			//			System.out.println("This ratio is "+thisRatio);

			if (thisRatio > originalRatio) {
				// We want to use the full height
				y=0;
				height=getHeight();
				width = (int)(height * originalRatio);
				x = (getWidth()-width) / 2;
			}
			else {
				// We want to use the full width
				x = 0;
				width = getWidth();
				height = (int)(width/originalRatio);
				y = (getHeight()-height)/2;
			}

			g.drawImage(currentImage, x, y, width, height, this);

		}
	}

	public void newAudioFile(ImageSoundData data) {}


	public void newImageSet(ImageSoundData data) {
		videoFrameSelected(data, 0);		
	}


	public void transitionsUpdated(ImageSoundData data) {}


	public void audioFrameSelected(ImageSoundData data, int frame) {
		int videoFrame = data.synchronisation().getImageIndexForSoundFrame(frame);
		videoFrameSelected(data, videoFrame);
	}


	public void videoFrameSelected(ImageSoundData data, int frame) {
		if (frame == currentImageIndex) return;
		
		currentImage = data.imageSet().getImage(frame);
		currentImageIndex = frame;
		repaint();
	}


	public void smoothingUpdated(ImageSoundData data) {}
}
