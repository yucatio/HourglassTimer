package com.yukaapplications.hourglass.view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

public class Shader {
	private static final String TAG = "Shader";

	private int mProgram;

	public Shader(int vsId, int fsId, Context context) {
		StringBuffer vsBuffer = new StringBuffer();
		StringBuffer fsBuffer = new StringBuffer();

		// read the files
		InputStream inputStream = null;
		BufferedReader in = null;
		try {
			// vertex shader
			inputStream = context.getResources().openRawResource(vsId);
			in = new BufferedReader(new InputStreamReader(inputStream));

			String read = in.readLine();
			while (read != null) {
				vsBuffer.append(read + "\n");
				read = in.readLine();
			}
			vsBuffer.deleteCharAt(vsBuffer.length() - 1);

			// fragment shader
			inputStream = context.getResources().openRawResource(fsId);
			in = new BufferedReader(new InputStreamReader(inputStream));

			read = in.readLine();
			while (read != null) {
				fsBuffer.append(read + "\n");
				read = in.readLine();
			}
			fsBuffer.deleteCharAt(fsBuffer.length() - 1);
		} catch (IOException e) {
			Log.e("ERROR-readingShader", "Could not read shader: " + e.getLocalizedMessage(), e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					Log.e(TAG, "faild to close BufferedReader.", e);
				}
			}

			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					Log.e(TAG, "faild to close InputStream.", e);
				}
			}
		}

		mProgram = createProgram(vsBuffer.toString(), fsBuffer.toString());
	}

	private int createProgram(String vertexSource, String fragmentSource) {
		int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
		if (vertexShader == 0) {
			return 0;
		}

		int pixelShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
		if (pixelShader == 0) {
			return 0;
		}

		int program = GLES20.glCreateProgram();
		if (program != 0) {
			GLES20.glAttachShader(program, vertexShader);
			checkGlError("glAttachShader");
			GLES20.glAttachShader(program, pixelShader);
			checkGlError("glAttachShader");
			GLES20.glLinkProgram(program);
			int[] linkStatus = new int[1];
			GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
			if (linkStatus[0] != GLES20.GL_TRUE) {
				Log.e(TAG, "Could not link program: ");
				Log.e(TAG, GLES20.glGetProgramInfoLog(program));
				GLES20.glDeleteProgram(program);
				program = 0;
			}
		}
		return program;
	}

	private int loadShader(int shaderType, String source) {
		int shader = GLES20.glCreateShader(shaderType);
		if (shader != 0) {
			GLES20.glShaderSource(shader, source);
			GLES20.glCompileShader(shader);
			int[] compiled = new int[1];
			GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
			if (compiled[0] == 0) {
				Log.e(TAG, "Could not compile shader " + shaderType + ":");
				Log.e(TAG, GLES20.glGetShaderInfoLog(shader));
				GLES20.glDeleteShader(shader);
				shader = 0;
			}
		}
		return shader;
	}

	private void checkGlError(String op) {
		int error;
		while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
			Log.e(TAG, op + ": glError " + error);
			throw new RuntimeException(op + ": glError " + error);
		}
	}


	public int getProgram() {
		return mProgram;
	}
}
