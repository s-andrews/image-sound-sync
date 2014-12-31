package uk.me.proeto.iss;

public interface ImageSoundListener {

	public void newAudioFile (ImageSoundData data);
	public void newImageSet (ImageSoundData data);
	public void transitionsUpdated (ImageSoundData data);
	public void smoothingUpdated (ImageSoundData data);
	public void audioFrameSelected(ImageSoundData data, int frame);
	public void videoFrameSelected(ImageSoundData data, int frame);
	
}
