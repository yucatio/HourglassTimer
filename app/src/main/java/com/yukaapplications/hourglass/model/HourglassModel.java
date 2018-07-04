package com.yukaapplications.hourglass.model;

import com.yukaapplications.hourglass.scene.Scene;

public class HourglassModel {
	// 時間
	private int time = 1;

	// シーン
	private Scene scene;

	public HourglassModel(int time, Scene scene) {
		super();
		this.time = time;
		this.scene = scene;
	}

	public int getTime() {
		return time;
	}

	public Scene getScene() {
		return scene;
	}

}
