package com.yukaapplications.hourglass.model;

import java.io.Serializable;

public class Sand implements Serializable {
	private static final long serialVersionUID = 1L;

	private int color;

	private int x;
	private int y;

	public Sand(int color, int x, int y) {
		super();
		this.color = color;
		this.x = x;
		this.y = y;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void setPos(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public String toString() {
		return "Sand [color=" + color + ", x=" + x + ", y=" + y + "]";
	}

}
