package com.yukaapplications.hourglass.scene;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

import com.yukaapplications.hourglass.activity.R;
import com.yukaapplications.hourglass.view.Shader;

public class SandScene implements Scene {
	private static final String TAG = "SandScene";

	private static final int FLOAT_SIZE_BYTES = 4;

	private Context context;

	protected int backgroundImageId;
	protected int foregroundImageId;
	protected int foregroundAlphaImageId;
	protected int sandImageId;
	protected int wallImageId;

	protected float sandAlpha;

	// テクスチャ大きさ
	private int textureW;
	private int textureH;

	// テクスチャ描画領域
	private int textureMainW;
	private int textureMainH;

	private float[] mMVPMatrix = new float[16];

	// bg (background shader)
	private Shader bgShader;
	private int bgTextureID;
	private int bguMVPMatrixHandle;
	private int bgaPositionHandle;
	private int bgaTextureCoordHandle;

	// fg (foreground shader)
	private Shader fgShader;
	private int fgTextureID;
	private int fgAlphaTextureID;
	private int fguMVPMatrixHandle;
	private int fgaPositionHandle;
	private int fgaTextureCoordHandle;
	private int fgaTextureHandle;
	private int fgaTextureAlphaHandle;

	// バッファ
	private FloatBuffer textureVertexBuffer; // テクスチャ用頂点バッファ
	private FloatBuffer uvBuffer;    //UVバッファ

	private final float[] texutreVertexs = {
			-1.0f, 1.0f,0.0f,//頂点0
			-1.0f, -1.0f,0.0f,//頂点1
			1.0f, 1.0f,0.0f,//頂点2
			1.0f, -1.0f,0.0f,//頂点3
	};

	private final float[] uvs = {
			0.0f,0.0f,//左上
			0.0f,1.0f,//左下
			1.0f,0.0f,//右上
			1.0f,1.0f,//右下
	};

	public SandScene(Context context, int backgroundImageId,
			int foregroundImageId, int foregroundAlphaImageId, int sandImageId,
			int wallImageId, int textureMainW, int textureMainH, float sandAlpha) {
		super();
		this.context = context;
		this.backgroundImageId = backgroundImageId;
		this.foregroundImageId = foregroundImageId;
		this.foregroundAlphaImageId = foregroundAlphaImageId;
		this.sandImageId = sandImageId;
		this.wallImageId = wallImageId;
		this.textureMainW = textureMainW;
		this.textureMainH = textureMainH;
		this.sandAlpha = sandAlpha;

		textureVertexBuffer =  ByteBuffer.allocateDirect(texutreVertexs.length * FLOAT_SIZE_BYTES)
				.order(ByteOrder.nativeOrder()).asFloatBuffer();

		uvBuffer =  ByteBuffer.allocateDirect(uvs.length  * FLOAT_SIZE_BYTES)
				.order(ByteOrder.nativeOrder()).asFloatBuffer();

	}

	public void init() {
		// background
		bgShader = new Shader(R.raw.simple_texture_vs, R.raw.simple_texture_fs, context);
		if (bgShader.getProgram() == 0) {
			Log.e(TAG, "No Program Created for bgShader");
			return;
		}
		bgaPositionHandle = GLES20.glGetAttribLocation(bgShader.getProgram(), "aPosition");
		checkGlError("glGetAttribLocation aPosition");
		if (bgaPositionHandle == -1) {
			throw new RuntimeException("Could not get attrib location for aPosition");
		}

		bgaTextureCoordHandle = GLES20.glGetAttribLocation(bgShader.getProgram(), "aTextureCoord");
		checkGlError("glGetAttribLocation aTextureCoord");
		if (bgaTextureCoordHandle == -1) {
			throw new RuntimeException("Could not get attrib location for aTextureCoord");
		}

		bguMVPMatrixHandle = GLES20.glGetUniformLocation(bgShader.getProgram(), "uMVPMatrix");
		checkGlError("glGetUniformLocation uMVPMatrix");
		if (bguMVPMatrixHandle == -1) {
			throw new RuntimeException("Could not get attrib location for uMVPMatrix");
		}

		GLES20.glEnableVertexAttribArray(bgaPositionHandle);
		checkGlError("glEnableVertexAttribArray maPositionHandle");
		GLES20.glEnableVertexAttribArray(bgaTextureCoordHandle);
		checkGlError("glEnableVertexAttribArray textureaTextureHandle");

		// foreground
		fgShader = new Shader(R.raw.simple_texture_vs, R.raw.texture_alpha_fs, context);
		if (fgShader.getProgram() == 0) {
			Log.e(TAG, "No Program Created for fgShader");
			return;
		}

		fgaPositionHandle = GLES20.glGetAttribLocation(fgShader.getProgram(), "aPosition");
		checkGlError("glGetAttribLocation aPosition");
		if (fgaPositionHandle == -1) {
			throw new RuntimeException("Could not get attrib location for aPosition");
		}

		fgaTextureCoordHandle = GLES20.glGetAttribLocation(fgShader.getProgram(), "aTextureCoord");
		checkGlError("glGetAttribLocation aTextureCoord");
		if (fgaTextureCoordHandle == -1) {
			throw new RuntimeException("Could not get attrib location for aTextureCoord");
		}

		fguMVPMatrixHandle = GLES20.glGetUniformLocation(fgShader.getProgram(), "uMVPMatrix");
		checkGlError("glGetUniformLocation uMVPMatrix");
		if (fguMVPMatrixHandle == -1) {
			throw new RuntimeException("Could not get attrib location for uMVPMatrix");
		}

		fgaTextureHandle = GLES20.glGetUniformLocation(fgShader.getProgram(), "sTexture");

		fgaTextureAlphaHandle = GLES20.glGetUniformLocation(fgShader.getProgram(), "sTextureAlpha");

		GLES20.glEnableVertexAttribArray(fgaPositionHandle);
		checkGlError("glEnableVertexAttribArray maPositionHandle");
		GLES20.glEnableVertexAttribArray(fgaTextureCoordHandle);
		checkGlError("glEnableVertexAttribArray textureaTextureHandle");


		int[] textures = new int[3];
		GLES20.glGenTextures(textures.length, textures, 0);

		bgTextureID = textures[0];
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, bgTextureID);

		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
				GLES20.GL_NEAREST);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MAG_FILTER,
				GLES20.GL_LINEAR);

		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
				GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
				GLES20.GL_CLAMP_TO_EDGE);

		InputStream is = context.getResources()
				.openRawResource(backgroundImageId);
		Bitmap bitmap;
		try {
			bitmap = BitmapFactory.decodeStream(is);
		} finally {
			try {
				is.close();
			} catch(IOException e) {
				// Ignore.
			}
		}
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

		textureW = bitmap.getWidth();
		textureH = bitmap.getHeight();

		bitmap.recycle();

		fgTextureID = textures[1];
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fgTextureID);

		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
				GLES20.GL_NEAREST);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MAG_FILTER,
				GLES20.GL_LINEAR);

		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
				GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
				GLES20.GL_CLAMP_TO_EDGE);


		is = context.getResources()
				.openRawResource(foregroundImageId);
		try {
			bitmap = BitmapFactory.decodeStream(is);
		} finally {
			try {
				is.close();
			} catch(IOException e) {
				// Ignore.
			}
		}
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

		bitmap.recycle();

		fgAlphaTextureID = textures[2];
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fgAlphaTextureID);

		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
				GLES20.GL_NEAREST);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MAG_FILTER,
				GLES20.GL_LINEAR);

		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
				GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
				GLES20.GL_CLAMP_TO_EDGE);

		is = context.getResources()
				.openRawResource(foregroundAlphaImageId);
		try {
			bitmap = BitmapFactory.decodeStream(is);
		} finally {
			try {
				is.close();
			} catch(IOException e) {
				// Ignore.
			}
		}
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

		bitmap.recycle();

	}

	public void onSurfaceChanged(int width, int height) {
		float textureMainRatio = textureMainW * 1.0f / (textureMainH*1.0f);

		float screenRatio = width * 1.0f / (height * 1.0f);

		int textureDisplayW = 0;
		int textureDisplayH = 0;

		if (screenRatio >= textureMainRatio) {
			// 横のほうが長め
			// 縦はmainいっぱいにとり、横をあわせる
			textureDisplayW = (int) (textureMainH * screenRatio);
			textureDisplayH = textureMainH;

		} else {
			// 縦のほうが長め
			// 横をmainいっぱいにとり、縦をあわせる
			textureDisplayW = textureMainW;
			textureDisplayH = (int) (textureMainW/screenRatio);

		}

		// opanglにおけるテクスチャ座標
		float texCoordW = textureDisplayW * 1.0f / (textureW * 1.0f);
		float texCoordH = textureDisplayH * 1.0f / (textureH * 1.0f);

		float texCoordStartX = (1 - texCoordW) / 2.0f;
		float texCoordStartY = (1 - texCoordH) / 2.0f;

		uvs[0] = texCoordStartX; // 左上x
		uvs[2] = texCoordStartX; // 左下x
		uvs[4] = texCoordStartX + texCoordW; // 右上x
		uvs[6] = texCoordStartX + texCoordW; // 右下x

		uvs[1] = texCoordStartY; // 左上y
		uvs[3] = texCoordStartY + texCoordH; // 左下y
		uvs[5] = texCoordStartY; // 右上y
		uvs[7] = texCoordStartY + texCoordH; // 右下y
	}


	public void drawBackground() {
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

		GLES20.glUseProgram(bgShader.getProgram());
		checkGlError("glUseProgram");

		// テクスチャの有効化
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, bgTextureID);
		checkGlError("glBindTexture");

		textureVertexBuffer.put(texutreVertexs).position(0);

		GLES20.glVertexAttribPointer(bgaPositionHandle, 3, GLES20.GL_FLOAT, false,
				0, textureVertexBuffer);
		checkGlError("glVertexAttribPointer textureaPositionHandle");

		uvBuffer.put(uvs).position(0);
		GLES20.glVertexAttribPointer(bgaTextureCoordHandle, 2, GLES20.GL_FLOAT, false,
				0, uvBuffer);

		Matrix.setIdentityM(mMVPMatrix,0);

		GLES20.glUniformMatrix4fv(bguMVPMatrixHandle, 1, false, mMVPMatrix, 0);
		GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
		checkGlError("glDrawArrays");

	}

	public void drawForeground() {
		GLES20.glUseProgram(fgShader.getProgram());
		checkGlError("glUseProgram");

		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		GLES20.glEnable(GLES20.GL_BLEND);

		// テクスチャの有効化
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fgTextureID);
		GLES20.glUniform1i(fgaTextureHandle, 0);
		checkGlError("glVertexAttribPointer fgaTextureHandle");
		GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fgAlphaTextureID);
		GLES20.glUniform1i(fgaTextureAlphaHandle, 1);
		checkGlError("glVertexAttribPointer fgaTextureAlphaHandle");

		textureVertexBuffer.put(texutreVertexs).position(0);

		GLES20.glVertexAttribPointer(fgaPositionHandle, 3, GLES20.GL_FLOAT, false,
				0, textureVertexBuffer);
		checkGlError("glVertexAttribPointer fgaPositionHandle");

		uvBuffer.put(uvs).position(0);
		GLES20.glVertexAttribPointer(fgaTextureCoordHandle, 2, GLES20.GL_FLOAT, false,
				0, uvBuffer);
		Matrix.setIdentityM(mMVPMatrix,0);

		GLES20.glUniformMatrix4fv(fguMVPMatrixHandle, 1, false, mMVPMatrix, 0);
		GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
		checkGlError("glDrawArrays");

		GLES20.glDisable(GLES20.GL_BLEND);

	}

	public int getSandImageId() {
		return sandImageId;
	}

	public int getWallImageId() {
		return wallImageId;
	}

	public float getSandAlpha() {
		return sandAlpha;
	}

	private void checkGlError(String op) {
		int error;
		while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
			Log.e(TAG, op + ": glError " + error);
			throw new RuntimeException(op + ": glError " + error);
		}
	}
}
