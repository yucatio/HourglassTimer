package com.yukaapplications.hourglass.field;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

public class WallField {
	// 壁の配列
	private boolean[] wall;

	private int width;
	private int height;

	public WallField(int width, int height, int resourceId, Context context) {
		this.width = width;
		this.height = height;

		wall = new boolean[width * height];

		// 壁のもとのピクセル
		Bitmap wallBitmapRaw;

		// 画像読み込み
		InputStream is = context.getResources()
				.openRawResource(resourceId);
		try {
			wallBitmapRaw = BitmapFactory.decodeStream(is);
			} finally {
			try {
				is.close();
			} catch(IOException e) {
				// Ignore.
			}
		}

		// リサイズ
		Bitmap wallBitmap;

		// 4角形の描画
		android.graphics.Matrix matrix = new android.graphics.Matrix();

		float scaleX = width * 1.0f / wallBitmapRaw.getWidth() * 1.0f;
		float scaleY = height * 1.0f / wallBitmapRaw.getHeight() * 1.0f;
		matrix.postScale( scaleX, scaleY );
		wallBitmap = Bitmap.createBitmap(wallBitmapRaw, 0, 0, wallBitmapRaw.getWidth(),wallBitmapRaw.getHeight(), matrix,true);

		// wallにコピー
		for(int y = 0; y < wallBitmap.getHeight(); y++) {
			for (int x = 0; x < wallBitmap.getWidth(); x++) {
				if (wallBitmap.getPixel(x, y) == Color.WHITE) {
					wall[y * width + x] = true;
				}
			}
		}

		// 上を壁にする
		for (int x = 0; x < wallBitmap.getWidth(); x++) {
			set(x, 0, true);
		}
		// 下を壁にする
		for (int x = 0; x < wallBitmap.getWidth(); x++) {
			set(x, height -1, true);
		}
		// 左を壁にする
		for (int y=0; y < wallBitmap.getHeight(); y++) {
			set(0, y, true);
		}
		// 右を壁にする
		for (int y=0; y < wallBitmap.getHeight(); y++) {
			set(width - 1, y, true);
		}

		wallBitmapRaw.recycle();
		wallBitmap.recycle();
	}

	public boolean get(int x , int y) {
		return wall[x + y * width];
	}

	private void set(int x, int y, boolean b) {
		wall[x + y * width] = b;
	}
}
