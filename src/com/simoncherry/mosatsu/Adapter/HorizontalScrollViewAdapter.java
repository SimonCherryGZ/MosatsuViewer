package com.simoncherry.mosatsu.Adapter;

import java.util.List;

import com.simoncherry.mosatsu.R;
import com.simoncherry.mosatsu.R.id;
import com.simoncherry.mosatsu.R.layout;

import android.content.Context;
import android.graphics.Bitmap;
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
		//viewHolder.mImg.setImageBitmap(BitmapFactory.decodeFile(mDatas.get(position)));
		viewHolder.mImg.setImageBitmap(
				decodeSampledBitmapFromResource(mDatas.get(position), 80, 80));

		return convertView;
	}

	private class ViewHolder
	{
		ImageView mImg;
	}
	
	public int calculateInSampleSize(BitmapFactory.Options options, 
	        int reqWidth, int reqHeight) { 
	    // 源图片的高度和宽度 
	    final int height = options.outHeight; 
	    final int width = options.outWidth; 
	    int inSampleSize = 1; 
	    if (height > reqHeight || width > reqWidth) { 
	        // 计算出实际宽高和目标宽高的比率 
	        final int heightRatio = Math.round((float) height / (float) reqHeight); 
	        final int widthRatio = Math.round((float) width / (float) reqWidth); 
	        // 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高 
	        // 一定都会大于等于目标的宽和高。 
	        inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio; 
	    } 
	    return inSampleSize; 
	}
	
	public Bitmap decodeSampledBitmapFromResource(String path, 
	        int reqWidth, int reqHeight) { 
	    // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小 
	    final BitmapFactory.Options options = new BitmapFactory.Options(); 
	    options.inJustDecodeBounds = true; 
	    BitmapFactory.decodeFile(path, options);
	    // 调用上面定义的方法计算inSampleSize值 
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight); 
	    
	    // 2016.03.08
	    options.inPreferredConfig = Bitmap.Config.RGB_565;
	    
	    // 使用获取到的inSampleSize值再次解析图片 
	    options.inJustDecodeBounds = false; 
	    return BitmapFactory.decodeFile(path, options); 
	}

}
