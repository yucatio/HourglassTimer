package com.yukaapplications.hourglass.view;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class HourglassView extends GLSurfaceView {
	private HourglassRenderer renderer;

	public HourglassView(Context context) {
		this(context, null);
	}

	public HourglassView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setEGLContextClientVersion(2);
		renderer = new HourglassRenderer(context);
		setRenderer(renderer);
	}

	public HourglassRenderer getRenderer() {
		return renderer;
	}

}
