package com.yukaapplications.hourglass.activity;

import java.text.MessageFormat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ToggleButton;

import com.yukaapplications.hourglass.common.Settings;
import com.yukaapplications.hourglass.maker.HourglassMaker;
import com.yukaapplications.hourglass.model.HourglassModel;
import com.yukaapplications.hourglass.view.HourglassView;

public class HourglassActivity extends Activity {
	private static final String TAG = "HourglassActivity";

	private HourglassView view;
	private ToggleButton alarmButton;
	private ToggleButton vibrationButton;
	private ToggleButton dialogInformButton;

	private SoundPool soundPool;
	private int ringingSoundId = -1;
	private int ringingStreamId = -1;

	private DialogInterface startDialog;
	private DialogInterface informDialog;

	private boolean alarmOn = false;
	private boolean vibrationOn = false;
	private boolean dialogInformOn = false;

	private Handler mHandler;
	private Handler longTimeHandlar;

	private Vibrator vibrator;

	private int timerMin;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hourglass);

		// 音の制御
		setVolumeControlStream(AudioManager.STREAM_MUSIC);

		// バイブレータ用意
		vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

		// ハンドラ
		mHandler = new Handler(new HourglassHandlerCallback());
		longTimeHandlar = new Handler(new LongTimeHandlerCallback());

		// シーンのセット
		Intent stageSelectIntent = getIntent();
		int id = stageSelectIntent.getIntExtra("id", 0);

		HourglassMaker hourglassMaker = new HourglassMaker(this);
		HourglassModel hourglass = hourglassMaker.get(id);

		view = (HourglassView) findViewById(R.id.hourglassView);
		view.getRenderer().setScene(hourglass.getScene());

		// タイマー設定
		timerMin = hourglass.getTime();

		// トグルボタン
		LayoutInflater inflater = LayoutInflater.from(HourglassActivity.this);
		View layout = inflater.inflate(R.layout.start_dialog, (ViewGroup) findViewById(R.id.startDalogRoot));

		alarmButton        = (ToggleButton)layout.findViewById(R.id.alarmButton);
		vibrationButton    = (ToggleButton)layout.findViewById(R.id.vibrationButton);
		dialogInformButton = (ToggleButton)layout.findViewById(R.id.dialogInformButton);

		alarmButton.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton button, boolean checked) {
				alarmOn = checked;
			}
		});
		vibrationButton.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton button, boolean checked) {
				vibrationOn = checked;
			}
		});
		dialogInformButton.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton button,
					boolean checked) {
				dialogInformOn = checked;
			}
		});

		// アラーム・バイブの設定を設定から読み込む
		SharedPreferences pref = getSharedPreferences("hourglasss", MODE_PRIVATE);
		boolean alarmSetting = pref.getBoolean("alarm_on", true);
		boolean vibrationSetting = pref.getBoolean("vibration_on", false);
		boolean dialogInformSetting = pref.getBoolean("dialog_inform_on", true);

		alarmButton.setChecked(alarmSetting);
		vibrationButton.setChecked(vibrationSetting);
		dialogInformButton.setChecked(dialogInformSetting);


		// スタートボタン
		startDialog = new AlertDialog.Builder(HourglassActivity.this)
				.setView(layout)
				.setPositiveButton(R.string.start, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// アラーム・バイブ設定の保存
						SharedPreferences pref = getSharedPreferences("hourglasss", MODE_PRIVATE);
						Editor e = pref.edit();
						e.putBoolean("alarm_on", alarmOn);
						e.putBoolean("vibration_on", vibrationOn);
						e.putBoolean("dialog_inform_on", dialogInformOn);
						e.commit();

						// 設定の表示
						if (alarmOn) {
							ImageView alarmIcon = (ImageView) findViewById(R.id.alarmIcon);
							alarmIcon.setImageResource(R.drawable.alarm_32);
						}
						if (vibrationOn) {
							ImageView vibrationIcon = (ImageView) findViewById(R.id.vibrationIcon);
							vibrationIcon.setImageResource(R.drawable.vibration_32);
							vibrationIcon.setVisibility(View.VISIBLE);
						}
						if (dialogInformOn) {
							ImageView infoIcon = (ImageView) findViewById(R.id.infoIcon);
							infoIcon.setImageResource(R.drawable.info_32);
						}

						// タイマー設定
						mHandler.sendEmptyMessageDelayed(0, timerMin * 60000);
						longTimeHandlar.sendEmptyMessageDelayed(0, Settings.MAX_HOURGLASS_DISPLAY_TIME * 1000);

						view.getRenderer().start();

					}
				}).setTitle(MessageFormat.format(getString(R.string.minuteTimer), new Object[]{Integer.valueOf(timerMin)}))
				.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				}).setOnCancelListener(new  DialogInterface.OnCancelListener() {
					public void onCancel(DialogInterface dialog) {
						finish();
					}
				})
				.show();

	}

	@Override
	protected void onResume() {
		super.onResume();

		view.onResume();

		// 画面ロックを防ぐ
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC,0);
		ringingSoundId = soundPool.load( this.getApplicationContext(), R.raw.se_maoudamashii_system46, 1);

	}

	@Override
	protected void onPause() {
		super.onPause();

		// 画面設定のクリア
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		startDialog.dismiss();
		if (informDialog != null) {
			informDialog.dismiss();
		}
		mHandler.removeMessages(0);
		longTimeHandlar.removeMessages(0);

		soundPool.stop(ringingStreamId);
		soundPool.unload(ringingSoundId);
		soundPool.release();

		vibrator.cancel();

		view.getRenderer().stop();
		view.onPause();

		if (! isFinishing()) {
			finish();
		}
	}

	private class HourglassHandlerCallback implements Callback {
		// タイマー作動
		public boolean handleMessage(Message msg) {
			if (alarmOn) {
				ringingStreamId = soundPool.play(ringingSoundId, 1.0f, 1.0f, 0, 2, 1.0f);
			}
			if (vibrationOn) {
				vibrator.vibrate(Settings.VIBRATION_PATTERN, -1);
			}
			if (dialogInformOn) {
				informDialog = new AlertDialog.Builder(HourglassActivity.this)
				.setTitle(MessageFormat.format(getString(R.string.minutesPassed), new Object[]{Integer.valueOf(timerMin)}))
				.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// alarm stop
						soundPool.stop(ringingStreamId);

						// vibration stop
						vibrator.cancel();

						dialog.dismiss();
					}
				}).show();
			}
			return false;
		}
	}

	private class LongTimeHandlerCallback implements Callback {
		public boolean handleMessage(Message message) {
			finish();
			return false;
		}
	}


}