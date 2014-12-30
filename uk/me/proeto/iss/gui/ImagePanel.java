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
		repaint();
	}
}
