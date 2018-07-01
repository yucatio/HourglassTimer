package com.yukaapplications.hourglass.view;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Arrays;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import com.yukaapplications.hourglass.activity.R;
import com.yukaapplications.hourglass.common.Settings;
import com.yukaapplications.hourglass.field.SandField;
import com.yukaapplications.hourglass.field.WallField;
import com.yukaapplications.hourglass.model.Sand;
import com.yukaapplications.hourglass.scene.Scene;

public class HourglassRenderer implements GLSurfaceView.Renderer {
	private static final String TAG = "SandClockTestRenderer";

	private static final int FLOAT_SIZE_BYTES = 4;

	private Context context;

	private SandField sandField;

	private volatile Scene scene;

	// 砂移動開始
	private volatile boolean started = false;

	//画面サイズ
	private int screenW;
	private int screenH;

	// 砂描画領域
	private int sandScreenW;
	private int sandScreenH;

	// 砂の計算領域
	private int sandFieldW = Settings.SAND_FIELD_WIDTH;
	private int sandFieldH = Settings.SAND_FIELD_HEIGHT;

	// 描画される点のピクセル数
	private float sandSize = 1.0f;
	// 砂透明度
	private volatile float sandAlpha = 1.0f;

	private float[] mMVPMatrix = new float[16];
	private float[] mProjMatrix = new float[16];

	private Shader mShader;
	private int muMVPMatrixHandle;
	private int muPointSizeHandle;
	private int muAlphaHandle;
	private int maPositionHandle;
	private int maColorHandle;

	//バッファ
	private FloatBuffer sandVertexBuffer;// 砂用頂点バッファ
	private FloatBuffer sandColorBuffer;//色バッファ

	// 砂用バッファのサイズ
	private final int bufferSize = 1024;

	private final float[] vertexs = new float[bufferSize * 3];

	private final float[] color = new float[bufferSize * 4];

	// fps 制御
	private int targetFps = Settings.TARGET_FPS;
	private volatile long sandMoveStartTime;
	// 砂移動回数
	private int moveCount = 0;

	// fps測定
//	private int counter = 0;
//	private long previousTime;
//	private long moveTotal = 0;
//	private long renderTotal = 0;
//	private int previousMoveCount=0;

	public HourglassRenderer(Context context) {
		this.context = context;

		// Buffer setting
		sandVertexBuffer =  ByteBuffer.allocateDirect(vertexs.length * FLOAT_SIZE_BYTES)
				.order(ByteOrder.nativeOrder()).asFloatBuffer();
		sandColorBuffer =  ByteBuffer.allocateDirect(color.length * FLOAT_SIZE_BYTES)
				.order(ByteOrder.nativeOrder()).asFloatBuffer();

	}

	public void onDrawFrame(GL10 gl) {

		long startTime = System.currentTimeMillis();

		// fpsに達していなかったら
		if (started && moveCount * 1.0f / (startTime - sandMoveStartTime) * 1000.0f < targetFps) {

			// 砂の移動
			sandField.move();

			moveCount++;
		}

//		long lapTime = System.currentTimeMillis();

		render();

//		long endTime = System.currentTimeMillis();

//		moveTotal += lapTime - startTime;
//		renderTotal += endTime - lapTime;
//
//		counter++;
//
//		if (counter % 128 == 0) {
//			// FPS計測
//
//			float fps = 128.0f / (endTime - previousTime) * 1000.0f;
//			float renderFps = (moveCount - previousMoveCount) * 1.0f / (endTime - previousTime) * 1000.0f;
//			Log.i(TAG, "time=" + ((startTime - sandMoveStartTime)/1000) + ", renderFps=" + renderFps + ", fps=" + fps + ", sandCount=" + sandField.getSandList().size() + ", moveAvg=" + moveTotal/128.0f + ", renderAvg=" + renderTotal/128.0f);
//
//			previousTime = endTime;
//
//			moveTotal = 0;
//			renderTotal = 0;
//			previousMoveCount = moveCount;
//		}
	}


	private void render() {
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

		GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

		// 背景の描画
		scene.drawBackground();

		// 砂の描画
		GLES20.glUseProgram(mShader.getProgram());
		checkGlError("glUseProgram");

		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		GLES20.glEnable(GLES20.GL_BLEND);

		GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false,
				0, sandVertexBuffer);
		checkGlError("glVertexAttribPointer maPosition");

		GLES20.glVertexAttribPointer(maColorHandle, 4,  GLES20.GL_FLOAT, false,
				0, sandColorBuffer);
		checkGlError("glVertexAttribPointer maColorHandle");

		GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, mMVPMatrix, 0);

		GLES20.glUniform1f(muPointSizeHandle, sandSize);

		GLES20.glUniform1f(muAlphaHandle, sandAlpha);

		int vertexOffset = 0;
		int ColorOffset  = 0;

		Arrays.fill(vertexs, -1);
		Arrays.fill(color, 0x00000000);

		for (Sand sand : sandField.getSandList()) {
			vertexs[vertexOffset] = sand.getX();
			vertexs[vertexOffset+1] = sandFieldH - sand.getY();
			vertexs[vertexOffset+2] = 0.0f;

			vertexOffset += 3;

			color[ColorOffset] = Color.red(sand.getColor()) * 1.0f / 255.0f; // R
			color[ColorOffset+1] = Color.green(sand.getColor()) * 1.0f / 255.0f; // G
			color[ColorOffset+2] = Color.blue(sand.getColor()) * 1.0f / 255.0f; // B
			color[ColorOffset+3] = 1.0f;// A

			ColorOffset += 4;

			if (vertexOffset >= vertexs.length) {
				// バッファがいっぱいになったら書き出し
				sandVertexBuffer.put(vertexs).position(0);

				sandColorBuffer.put(color).position(0);

				// 点の描画
				GLES20.glDrawArrays(GLES20.GL_POINTS, 0, bufferSize);
				checkGlError("glDrawArrays");

				vertexOffset = 0;
				ColorOffset = 0;
				Arrays.fill(vertexs, -1);
				Arrays.fill(color, 0x00000000);
			}
		}

		sandVertexBuffer.put(vertexs).position(0);

		sandColorBuffer.put(color).position(0);

		// 点の描画
		GLES20.glDrawArrays(GLES20.GL_POINTS, 0, bufferSize);
		checkGlError("glDrawArrays");
		GLES20.glDisable(GLES20.GL_BLEND);

		// 前景の描画
		scene.drawForeground();
	}

	public void onSurfaceChanged(GL10 gl, int width, int height) {
		screenW = Math.min(Settings.MAX_SCREEN_WIDTH, width);
		screenH =  Math.min(Settings.MAX_SCREEN_HEIGHT, height);
		GLES20.glViewport((width - screenW)/2, (height - screenH)/2, screenW, screenH);

		Matrix.orthoM(mProjMatrix, 0, 0, screenW, 0, screenH, 1, -1);

		Log.i(TAG, "screenW=" + screenW + ", screenH=" + screenH);

		scene.onSurfaceChanged(width, height);

		setupMatrix(screenW, screenH);

	}

	private void setupMatrix(int width, int height) {

		float textureMainRatio = sandFieldW * 1.0f / (sandFieldH*1.0f);

		float screenRatio = width * 1.0f / (height * 1.0f);

		if (screenRatio >= textureMainRatio) {
			// 横のほうが長め
			// 縦はmainいっぱいにとり、横をあわせる
			sandScreenW = (int) (height * textureMainRatio);
			sandScreenH = height;

		} else {
			// 縦のほうが長め
			// 横をmainいっぱいにとり、縦をあわせる
			sandScreenW = width;
			sandScreenH = (int) (width / textureMainRatio);
		}

		float[] textureClipMatrix = new float[16];

		// テクスチャ座標 -> スクリーン座標
		Matrix.setIdentityM(textureClipMatrix, 0);
		Matrix.translateM(textureClipMatrix, 0, (width - sandScreenW) / 2.0f, (height - sandScreenH) / 2.0f, 0.0f);
		Matrix.scaleM(textureClipMatrix, 0, sandScreenW*1.0f/(sandFieldW * 1.0f), sandScreenH*1.0f/(sandFieldH * 1.0f) , 1.0f);

		// ポイントサイズ
		sandSize = sandScreenW*1.0f/(sandFieldW * 1.0f);
		sandSize = Math.max(sandSize, 1.0f);

		// ポイントスプライトを真ん中に持ってくる
		float offsetX = screenW*1.0f / sandFieldW*1.0f / 2;
		float offsetY = screenH*1.0f / sandFieldH*1.0f / 2;
		Matrix.translateM(textureClipMatrix, 0, offsetX, -offsetY, 0.0f);

		Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, textureClipMatrix, 0);
	}


	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// 砂シェーダ
		mShader = new Shader(R.raw.point_size_color_vs, R.raw.simple_color_fs, context);
		if (mShader.getProgram() == 0) {
			Log.e(TAG, "No Program Created.");
			return;
		}
		maPositionHandle = GLES20.glGetAttribLocation(mShader.getProgram(), "aPosition");
		checkGlError("glGetAttribLocation aPosition");
		if (maPositionHandle == -1) {
			throw new RuntimeException("Could not get attrib location for aPosition");
		}

		maColorHandle = GLES20.glGetAttribLocation(mShader.getProgram(), "aColor");
		checkGlError("glGetAttribLocation aColor");
		if (maPositionHandle == -1) {
			throw new RuntimeException("Could not get attrib location for aColor");
		}

		muMVPMatrixHandle = GLES20.glGetUniformLocation(mShader.getProgram(), "uMVPMatrix");
		checkGlError("glGetUniformLocation uMVPMatrix");
		if (muMVPMatrixHandle == -1) {
			throw new RuntimeException("Could not get attrib location for uMVPMatrix");
		}

		muPointSizeHandle = GLES20.glGetUniformLocation(mShader.getProgram(), "uPointSize");
		checkGlError("glGetUniformLocation uPointSize");
		if (muPointSizeHandle == -1) {
			throw new RuntimeException("Could not get attrib location for uPointSize");
		}

		muAlphaHandle = GLES20.glGetUniformLocation(mShader.getProgram(), "uAlpha");
		checkGlError("glGetUniformLocation uAlpha");
		if (muAlphaHandle == -1) {
			throw new RuntimeException("Could not get attrib location for uAlpha");
		}

		// 頂点バッファの有効化
		GLES20.glEnableVertexAttribArray(maPositionHandle);
		checkGlError("glEnableVertexAttribArray maPositionHandle");
		// 色バッファの有効化
		GLES20.glEnableVertexAttribArray(maColorHandle);
		checkGlError("glEnableVertexAttribArray maColorHandle");

		scene.init();

		// 壁の初期化
		WallField wall = new WallField(sandFieldW, sandFieldH, scene.getWallImageId(), context);
		// 砂の初期化
		sandField = new SandField(sandFieldW, sandFieldH, scene.getSandImageId(), context, wall);

	}

	public void setScene(Scene scene) {
		this.scene = scene;
		this.sandAlpha = scene.getSandAlpha();
	}

	public void start() {
//		previousTime = System.currentTimeMillis();
		sandMoveStartTime = System.currentTimeMillis();
		moveCount = 0;
		started = true;
	}

	public void stop() {
		started = false;
	}


	private void checkGlError(String op) {
		int error;
		while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
			Log.e(TAG, op + ": glError " + error);
			throw new RuntimeException(op + ": glError " + error);
		}
	}
}
