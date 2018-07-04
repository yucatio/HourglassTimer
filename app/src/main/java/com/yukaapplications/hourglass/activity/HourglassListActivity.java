package com.yukaapplications.hourglass.activity;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.TabActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import com.yukaapplications.hourglass.adapter.HourglassListRowAdapter;
import com.yukaapplications.hourglass.maker.HourglassMaker;

public class HourglassListActivity extends TabActivity {
	private static final String TAG = "StageSelectActivity";

	private static final List<HourglassListActivity.HourglassListRow> oneMinList = new ArrayList<HourglassListActivity.HourglassListRow>();
	private static final List<HourglassListActivity.HourglassListRow> threeMinList = new ArrayList<HourglassListActivity.HourglassListRow>();
	private static final List<HourglassListActivity.HourglassListRow> fiveMinList = new ArrayList<HourglassListActivity.HourglassListRow>();

	static {
		oneMinList.add(new HourglassListRow(HourglassMaker.ROOM_1MIN, R.drawable.room_thumbnail, R.string.roomTitle));
		oneMinList.add(new HourglassListRow(HourglassMaker.FLOWER_I_1MIN, R.drawable.block_thumbnail, R.string.flowerITitle));
		oneMinList.add(new HourglassListRow(HourglassMaker.HEARTS_1MIN, R.drawable.hearts_thumbnail, R.string.heartsTitle));
		oneMinList.add(new HourglassListRow(HourglassMaker.HANDS_1MIN, R.drawable.hands_thumbnail, R.string.handToHandTitle));
		oneMinList.add(new  HourglassListRow(HourglassMaker.BAMBOO_1MIN, R.drawable.bamboo_thumbnail, R.string.bambooTitle));

		threeMinList.add(new HourglassListRow(HourglassMaker.ROOM_3MIN, R.drawable.room_thumbnail, R.string.roomTitle));
		threeMinList.add(new HourglassListRow(HourglassMaker.FLOWER_I_3MIN, R.drawable.block_thumbnail, R.string.flowerITitle));
		threeMinList.add(new HourglassListRow(HourglassMaker.HEARTS_3MIN, R.drawable.hearts_thumbnail, R.string.heartsTitle));
		threeMinList.add(new HourglassListRow(HourglassMaker.HANDS_3MIN, R.drawable.hands_thumbnail, R.string.handToHandTitle));
		threeMinList.add(new  HourglassListRow(HourglassMaker.BAMBOO_3MIN, R.drawable.bamboo_thumbnail, R.string.bambooTitle));

		fiveMinList.add(new HourglassListRow(HourglassMaker.ROOM_5MIN, R.drawable.room_thumbnail, R.string.roomTitle));
		fiveMinList.add(new HourglassListRow(HourglassMaker.FLOWER_I_5MIN, R.drawable.block_thumbnail, R.string.flowerITitle));
		fiveMinList.add(new HourglassListRow(HourglassMaker.HEARTS_5MIN, R.drawable.hearts_thumbnail, R.string.heartsTitle));
		fiveMinList.add(new HourglassListRow(HourglassMaker.HANDS_5MIN, R.drawable.hands_thumbnail, R.string.handToHandTitle));
		fiveMinList.add(new HourglassListRow(HourglassMaker.BAMBOO_5MIN, R.drawable.bamboo_thumbnail, R.string.bambooTitle));

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.hourglass_list);

		// TabHostクラス初期設定
		TabHost tabHost = getTabHost();

		// 1-min 設定
		TabSpec oneMinTab = tabHost.newTabSpec("oneMinTab");
		oneMinTab.setIndicator(MessageFormat.format(getString(R.string.min), new Object[]{"1"}));
		oneMinTab.setContent(R.id.oneMinList);
		tabHost.addTab(oneMinTab);



		GridView list1 = (GridView) this.findViewById(R.id.oneMinList);
		list1.setAdapter(new HourglassListRowAdapter(this, R.layout.hourglass_list_row, oneMinList));

		list1.setOnItemClickListener(new ClickEvent());

		// 3-min 設定
		TabSpec threeMinTab = tabHost.newTabSpec("threeMinTab");
		threeMinTab.setIndicator(MessageFormat.format(getString(R.string.min), new Object[]{"3"}));
		threeMinTab.setContent(R.id.threeMinList);
		tabHost.addTab(threeMinTab);

		GridView list3 = (GridView) this.findViewById(R.id.threeMinList);
		list3.setAdapter(new HourglassListRowAdapter(this, R.layout.hourglass_list_row, threeMinList));

		list3.setOnItemClickListener(new ClickEvent());

		// 5-min 設定
		TabSpec fiveMinTab = tabHost.newTabSpec("fiveMinTab");
		fiveMinTab.setIndicator(MessageFormat.format(getString(R.string.min), new Object[]{"5"}));
		fiveMinTab.setContent(R.id.fiveMinList);
		tabHost.addTab(fiveMinTab);

		GridView list5 = (GridView) this.findViewById(R.id.fiveMinList);
		list5.setAdapter(new HourglassListRowAdapter(this, R.layout.hourglass_list_row, fiveMinList));

		list5.setOnItemClickListener(new ClickEvent());

		SharedPreferences pref = getSharedPreferences("hourglasss", MODE_PRIVATE);
		String tab = pref.getString("tab", "oneMinTab");
		tabHost.setCurrentTabByTag(tab);

		tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
			public void onTabChanged(String tabId) {
				// タブの場所を保存
				SharedPreferences pref = getSharedPreferences("hourglasss", MODE_PRIVATE);
				Editor e = pref.edit();
				e.putString("tab", tabId);

				e.commit();
			}
		});
	}


	// イベントクラスの定義
	class ClickEvent implements OnItemClickListener {

		// onItemClickメソッドには、AdapterView(adapter)、選択した項目View(TextView)、選択された位置のint値、IDを示すlong値が渡される
		public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
			int hourglassId = 0;
			switch (adapter.getId()) {
			case  R.id.oneMinList :
				hourglassId = oneMinList.get(position).getId();
				break;
			case  R.id.threeMinList :
				hourglassId = threeMinList.get(position).getId();
				break;
			case  R.id.fiveMinList :
				hourglassId = fiveMinList.get(position).getId();
				break;
			}

			Intent sandIntent = new Intent(HourglassListActivity.this, HourglassActivity.class);
			sandIntent.putExtra("id", hourglassId);
			startActivity(sandIntent);
		}

	}

	public static class HourglassListRow {
		private int id;
		private int imageId;
		private int titleId;
		public HourglassListRow(int id,int imageId, int titleId) {
			super();
			this.id = id;
			this.imageId = imageId;
			this.titleId = titleId;
		}
		public int getId() {
			return id;
		}
		public int getImageId() {
			return imageId;
		}
		public int getTitleId() {
			return titleId;
		}

	}
}
