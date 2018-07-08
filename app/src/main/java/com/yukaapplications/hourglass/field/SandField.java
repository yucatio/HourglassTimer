package com.yukaapplications.hourglass.field;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import com.yukaapplications.hourglass.model.Sand;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

public class SandField {
	private static final byte NO_MOVE = 0;
	private static final byte TOP    = 1;
	private static final byte RIGHT  = 2;
	private static final byte BOTTOM = 4;
	private static final byte LEFT   = 8;

	private static final int MOVE_LIST_WIDTH = 8;
	private static final byte[] MOVE_LIST = {
		// 0 上右下左
		BOTTOM, BOTTOM, BOTTOM, BOTTOM, BOTTOM, BOTTOM, RIGHT, LEFT,
		// 1 右下左
		BOTTOM, BOTTOM, BOTTOM, BOTTOM, BOTTOM, BOTTOM, RIGHT, LEFT,
		// 2 上下左
		BOTTOM, BOTTOM, BOTTOM, BOTTOM, BOTTOM, LEFT, LEFT, LEFT,
		// 3 下左
		BOTTOM, BOTTOM, BOTTOM, BOTTOM, BOTTOM, LEFT, LEFT, LEFT,
		// 4 上右左
		NO_MOVE, NO_MOVE, NO_MOVE, NO_MOVE, NO_MOVE, NO_MOVE, RIGHT, LEFT,
		// 5 右左
		NO_MOVE, NO_MOVE, NO_MOVE, NO_MOVE, NO_MOVE, NO_MOVE, RIGHT, LEFT,
		// 6 上左
		NO_MOVE, NO_MOVE, NO_MOVE, LEFT, LEFT, LEFT, LEFT, LEFT,
		// 7 左
		NO_MOVE, NO_MOVE, LEFT, LEFT, LEFT, LEFT, LEFT, LEFT,
		// 8 上右下
		BOTTOM, BOTTOM, BOTTOM, BOTTOM, BOTTOM, RIGHT, RIGHT, RIGHT,
		// 9 右下
		BOTTOM, BOTTOM, BOTTOM, BOTTOM, BOTTOM, RIGHT, RIGHT, RIGHT,
		// 10 上下
		BOTTOM, BOTTOM, BOTTOM, BOTTOM, BOTTOM, BOTTOM, BOTTOM, BOTTOM,
		// 11 下
		BOTTOM, BOTTOM, BOTTOM, BOTTOM, BOTTOM, BOTTOM, BOTTOM, BOTTOM,
		// 12 上右
		NO_MOVE, NO_MOVE, NO_MOVE, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT,
		// 13 右
		NO_MOVE, NO_MOVE, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT,
		// 14 上
		NO_MOVE, NO_MOVE, NO_MOVE, NO_MOVE, NO_MOVE, NO_MOVE, NO_MOVE, TOP,
		// 15
		NO_MOVE, NO_MOVE, NO_MOVE, NO_MOVE, NO_MOVE, NO_MOVE, NO_MOVE, NO_MOVE,
	};

	private int width;
	private int height;

	// 砂の配列
	private Sand[] sands;
	// 砂リスト
	private ArrayList<Sand> sandList;

	// 砂移動方向
	private byte[] sandMoveTo;
	// 移動方向のリスト
	private ArrayList<Integer> sandsMoveToList;

	// カベ
	private WallField wall;

	private Random random = new Random();

	public SandField(int width, int height, int resourceId,  Context context, WallField wall) {
		this.width = width;
		this.height = height;
		this.wall = wall;

		sands = new Sand[width * height];
		sandList = new ArrayList<Sand>(width * height / 10);

		sandMoveTo = new byte[width * height];
		sandsMoveToList = new ArrayList<Integer>(width * height / 10);

		// 砂のもとのピクセル
		Bitmap sandsBitmapRaw;

		// 画像読み込み
		InputStream is = context.getResources()
				.openRawResource(resourceId);
		try {
			sandsBitmapRaw = BitmapFactory.decodeStream(is);
			} finally {
			try {
				is.close();
			} catch(IOException e) {
				// Ignore.
			}
		}

		// リサイズ
		Bitmap sandsBitmap;

		// 4角形の描画
		android.graphics.Matrix matrix = new android.graphics.Matrix();

		float scaleX = width * 1.0f / sandsBitmapRaw.getWidth() * 1.0f;
		float scaleY = height * 1.0f / sandsBitmapRaw.getHeight() * 1.0f;
		matrix.postScale( scaleX, scaleY );
		sandsBitmap = Bitmap.createBitmap(sandsBitmapRaw, 0, 0, sandsBitmapRaw.getWidth(),sandsBitmapRaw.getHeight(), matrix,true);

		// sandsFileid, sandsArrayの作成
		for(int y = 0; y < sandsBitmap.getHeight(); y++) {
			for (int x = 0; x < sandsBitmap.getWidth(); x++) {
				if (Color.alpha(sandsBitmap.getPixel(x, y)) == 0xff && !wall.get(x, y)) {
					Sand sand = new Sand(sandsBitmap.getPixel(x, y), x, y);
					set(x, y, sand);
					sandList.add(sand);
				}
			}
		}

		sandsBitmapRaw.recycle();
		sandsBitmap.recycle();

	}

	/**
	 * 砂を移動します
	 */
	public void move() {
		Arrays.fill(sandMoveTo, NO_MOVE);
		sandsMoveToList.clear();

		// 動く方向の決定
		for (Sand sand : sandList) {
			// 状態からindex作成
			int x = sand.getX();
			int y = sand.getY();
			int index = ((get(x, y-1) != null || wall.get(x, y-1)) ? 1 : 0)  * TOP
					+ ((get(x+1, y) != null || wall.get(x+1, y)) ? 1 : 0)  * RIGHT
					+ ((get(x, y+1) != null || wall.get(x, y+1)) ? 1 : 0)  * BOTTOM
					+ ((get(x-1, y) != null || wall.get(x-1, y)) ? 1 : 0)  * LEFT;
			setSandMoveTo(x, y, MOVE_LIST[index * MOVE_LIST_WIDTH + random.nextInt(MOVE_LIST_WIDTH)]);

			switch(getSandMoveTo(x, y)) {
			case TOP :
				sandsMoveToList.add(getIndex(x, y-1));
				break;
			case RIGHT :
				sandsMoveToList.add(getIndex(x+1, y));
				break;
			case BOTTOM :
				sandsMoveToList.add(getIndex(x, y+1));
				break;
			case LEFT :
				sandsMoveToList.add(getIndex(x-1, y));
			}
		}

		// 動かす
		for (int index : sandsMoveToList) {
			int x = getX(index);
			int y = getY(index);

			if (get(x, y) != null) {
				continue;
			}

			// チェック用
			int count = 0;
			if (getSandMoveTo(x, y-1) == BOTTOM) {count++;}
			if (getSandMoveTo(x+1, y) == LEFT) {count++;}
			if (getSandMoveTo(x, y+1) == TOP) {count++;}
			if (getSandMoveTo(x-1, y) == RIGHT) {count++;}

			if (count == 0) {
				continue;
			}

			int i = random.nextInt(count);

			if (getSandMoveTo(x, y-1) == BOTTOM) {
				if (i == 0) {
					set(x, y, get(x, y-1));
					set(x, y-1, null);

					get(x, y).setPos(x, y);
				}
				i--;
			}
			if (getSandMoveTo(x+1, y) == LEFT) {
				if (i == 0) {
					set(x, y, get(x+1, y));
					set(x+1, y, null);

					get(x, y).setPos(x, y);

				}
				i--;
			}
			if (getSandMoveTo(x, y+1) == TOP) {
				if (i == 0) {
					set(x, y, get(x, y+1));
					set(x, y+1, null);

					get(x, y).setPos(x, y);

				}
				i--;
			}
			if (getSandMoveTo(x-1, y) == RIGHT) {
				if (i == 0) {
					set(x, y, get(x-1, y));
					set(x-1, y, null);

					get(x, y).setPos(x, y);
				}
				i--;
			}

		}
	}

	private int getX(int index) {
		return index % width;
	}

	private int getY(int index) {
		return index / width;
	}

	public Sand get(int x, int y) {
		return sands[x + y * width];
	}

	private void set(int x, int y, Sand sand) {
		sands[x + y * width] = sand;
	}

	private byte getSandMoveTo(int x, int y) {
		return sandMoveTo[x + y * width];
	}

	private void setSandMoveTo(int x, int y, byte b) {
		sandMoveTo[x + y * width] = b;
	}

	private int getIndex(int x, int y) {
		return x + y * width;
	}

	public ArrayList<Sand> getSandList() {
		return sandList;
	}
}
