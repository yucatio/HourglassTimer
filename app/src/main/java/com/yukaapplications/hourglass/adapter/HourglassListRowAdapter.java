package com.yukaapplications.hourglass.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yukaapplications.hourglass.activity.HourglassListActivity;
import com.yukaapplications.hourglass.activity.R;

public class HourglassListRowAdapter extends ArrayAdapter<HourglassListActivity.HourglassListRow> {
	private int resourceId;
	private List<HourglassListActivity.HourglassListRow> items;
	private LayoutInflater inflater;
	private OnClickListener listener;

	public HourglassListRowAdapter(Context context, int resourceId, List<HourglassListActivity.HourglassListRow> items) {
		this(context, resourceId, items, null);
	}

	public HourglassListRowAdapter(Context context, int resourceId, List<HourglassListActivity.HourglassListRow> items, OnClickListener listener) {
		super(context, resourceId, items);
		this.resourceId = resourceId;
		this.items = items;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.listener = listener;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			// 受け取ったビューがnullなら新しくビューを生成
			view = inflater.inflate(resourceId, null);
		}

		HourglassListActivity.HourglassListRow row = items.get(position);

		// imageをセット
		ImageView thumbnail = (ImageView) view.findViewById(R.id.sandThumbnail);
		thumbnail.setImageResource(row.getImageId());

		// 名前をセット
		TextView title = (TextView)view.findViewById(R.id.sandTitle);
		title.setText(row.getTitleId());

		return view;
	}
}
