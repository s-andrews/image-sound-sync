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
