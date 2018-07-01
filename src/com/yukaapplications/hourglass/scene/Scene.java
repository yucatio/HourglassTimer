package com.yukaapplications.hourglass.scene;

public interface Scene {
	public void init();
	public void onSurfaceChanged(int width, int height);

	public void drawBackground();
	public void drawForeground();

	public int getSandImageId();
	public int getWallImageId();
	public float getSandAlpha();
}
