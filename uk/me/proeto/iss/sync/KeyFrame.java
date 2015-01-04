package uk.me.proeto.iss.sync;

public class KeyFrame implements Comparable<KeyFrame> {

	private int audioFrame;
	private int videoFrame;
	
	public KeyFrame (int audioFrame,int videoFrame) {
		this.audioFrame = audioFrame;
		this.videoFrame = videoFrame;
	}
	
	public int audioFrame () {
		return audioFrame;
	}
	
	public int videoFrame () {
		return videoFrame;
	}

	public int compareTo(KeyFrame o) {
		return audioFrame - o.audioFrame;
	}
}
