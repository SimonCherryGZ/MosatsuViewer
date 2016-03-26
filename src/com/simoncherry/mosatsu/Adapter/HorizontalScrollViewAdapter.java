package com.simoncherry.mosatsu.Adapter;

import java.util.List;

import com.simoncherry.mosatsu.R;
import com.simoncherry.mosatsu.R.id;
import com.simoncherry.mosatsu.R.layout;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class HorizontalScrollViewAdapter
{

	private Context mContext;
	private LayoutInflater mInflater;
	private List<String> mDatas;

	public HorizontalScrollViewAdapter(Context context, List<String> mDatas)
	{
		this.mContext = context;
		mInflater = LayoutInflater.from(context);
		this.mDatas = mDatas;
	}

	public int getCount()
	{
		return mDatas.size();
	}

	public Object getItem(int position)
	{
		return mDatas.get(position);
	}

	public long getItemId(int position)
	{
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder viewHolder = null;
		if (convertView == null)
		{
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(
					R.layout.activity_gallery_item, parent, false);
			viewHolder.mImg = (ImageView) convertView
					.findViewById(R.id.id_index_gallery_item_image);

			convertView.setTag(viewHolder);
		} else
		{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		//viewHolder.mImg.setImageResource(mDatas.get(position));
		viewHolder.mImg.setImageBitmap(BitmapFactory.decodeFile(mDatas.get(position)));

		return convertView;
	}

	private class ViewHolder
	{
		ImageView mImg;
	}

}
