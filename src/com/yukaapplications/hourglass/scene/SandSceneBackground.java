package com.yukaapplications.hourglass.scene;

import com.yukaapplications.hourglass.activity.R;

import android.content.Context;

public class SandSceneBackground extends SandScene {
	public SandSceneBackground(Context context, int backgroundImageId,
			int sandImageId, int wallImageId, int textureMainW,
			int textureMainH, float sandAlpha) {
		super(context, backgroundImageId, R.drawable.dummy,
				R.drawable.dummy, sandImageId, wallImageId, textureMainW,
				textureMainH, sandAlpha);
	}

	@Override
	public void drawForeground() {
		// do nothing
	}
}
