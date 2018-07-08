package com.yukaapplications.hourglass.maker;

import android.content.Context;

import com.yukaapplications.hourglass.activity.R;
import com.yukaapplications.hourglass.model.HourglassModel;
import com.yukaapplications.hourglass.scene.SandScene;
import com.yukaapplications.hourglass.scene.SandSceneBackground;

public class HourglassMaker {
	// 1min
	public static final int FLOWER_I_1MIN = 0x01000000;
	public static final int HANDS_1MIN = 0x01000001;
	public static final int BAMBOO_1MIN = 0x01000002;
	public static final int ROOM_1MIN = 0x01000003;
	public static final int HEARTS_1MIN = 0x01000004;

	// 3min
	public static final int FLOWER_I_3MIN = 0x03000000;
	public static final int HANDS_3MIN = 0x03000001;
	public static final int BAMBOO_3MIN = 0x03000002;
	public static final int ROOM_3MIN = 0x03000003;
	public static final int HEARTS_3MIN = 0x03000004;

	// 5min
	public static final int FLOWER_I_5MIN = 0x05000000;
	public static final int HANDS_5MIN = 0x05000001;
	public static final int BAMBOO_5MIN = 0x05000002;
	public static final int ROOM_5MIN   = 0x05000003;
	public static final int HEARTS_5MIN = 0x05000004;

	private Context context;

	public HourglassMaker(Context context) {
		this.context = context;
	}

	public HourglassModel get(int id) {
		switch (id) {

		case FLOWER_I_1MIN:
			return new HourglassModel(1, new SandScene(context,
					R.drawable.block_background, R.drawable.block_background,
					R.drawable.block_alpha, R.drawable.block_sand_1min,
					R.drawable.block_wall_1min, 576, 960, 1.0f));
		case HANDS_1MIN:
			return new HourglassModel(1, new SandScene(context,
					R.drawable.hand_background, R.drawable.hand_background,
					R.drawable.hand_alpha, R.drawable.hand_sand_1min,
					R.drawable.hand_wall_1min, 576, 960, 0.8f));
		case BAMBOO_1MIN:
			return new HourglassModel(1, new SandSceneBackground(context,
					R.drawable.bamboo_background, R.drawable.bamboo_sand_1min,
					R.drawable.bamboo_wall_1min, 576, 960, 0.7f));
		case ROOM_1MIN:
			return new HourglassModel(1, new SandScene(context,
					R.drawable.room_background, R.drawable.room_foreground_1min,
					R.drawable.room_alpha_1min, R.drawable.room_sand_1min,
					R.drawable.room_wall_1min, 576, 960, 0.9f));
		case HEARTS_1MIN:
			return new  HourglassModel(1, new SandScene(context,
					R.drawable.hearts_background, R.drawable.hearts_foreground,
					R.drawable.hearts_alpha, R.drawable.hearts_sand_1min,
					R.drawable.hearts_wall_1min, 576, 960, 1.0f));

		case FLOWER_I_3MIN:
			return new HourglassModel(3, new SandScene(context,
					R.drawable.block_background, R.drawable.block_background,
					R.drawable.block_alpha, R.drawable.block_sand_3min,
					R.drawable.block_wall_3min, 576, 960, 1.0f));
		case HANDS_3MIN:
			return new HourglassModel(3, new SandScene(context,
					R.drawable.hand_background, R.drawable.hand_background,
					R.drawable.hand_alpha, R.drawable.hand_sand_3min,
					R.drawable.hand_wall_3min, 576, 960, 0.8f));
		case BAMBOO_3MIN:
			return new HourglassModel(3, new SandSceneBackground(context,
					R.drawable.bamboo_background, R.drawable.bamboo_sand_3min,
					R.drawable.bamboo_wall_3min, 576, 960, 0.7f));
		case ROOM_3MIN:
			return new HourglassModel(3, new SandScene(context,
					R.drawable.room_background, R.drawable.room_foreground_3min,
					R.drawable.room_alpha_3min, R.drawable.room_sand_3min,
					R.drawable.room_wall_3min, 576, 960, 0.9f));
		case HEARTS_3MIN:
			return new  HourglassModel(3, new SandScene(context,
					R.drawable.hearts_background, R.drawable.hearts_foreground,
					R.drawable.hearts_alpha, R.drawable.hearts_sand_3min,
					R.drawable.hearts_wall_3min, 576, 960, 1.0f));

		case FLOWER_I_5MIN:
			return new HourglassModel(5, new SandScene(context,
					R.drawable.block_background, R.drawable.block_background,
					R.drawable.block_alpha, R.drawable.block_sand_5min,
					R.drawable.block_wall_5min, 576, 960, 1.0f));
		case HANDS_5MIN:
			return new HourglassModel(5, new SandScene(context,
					R.drawable.hand_background, R.drawable.hand_background,
					R.drawable.hand_alpha, R.drawable.hand_sand_5min,
					R.drawable.hand_wall_5min, 576, 960, 0.8f));
		case BAMBOO_5MIN:
			return new HourglassModel(5, new SandSceneBackground(context,
					R.drawable.bamboo_background, R.drawable.bamboo_sand_5min,
					R.drawable.bamboo_wall_5min, 576, 960, 0.7f));
		case ROOM_5MIN:
			return new HourglassModel(5, new SandScene(context,
					R.drawable.room_background, R.drawable.room_foreground_5min,
					R.drawable.room_alpha_5min, R.drawable.room_sand_5min,
					R.drawable.room_wall_5min, 576, 960, 0.9f));
		case HEARTS_5MIN:
			return new  HourglassModel(5, new SandScene(context,
					R.drawable.hearts_background, R.drawable.hearts_foreground_5min,
					R.drawable.hearts_alpha_5min, R.drawable.hearts_sand_5min,
					R.drawable.hearts_wall_5min, 576, 960, 1.0f));
		default:
			throw new IllegalArgumentException("id not found.");
		}
	}
}
