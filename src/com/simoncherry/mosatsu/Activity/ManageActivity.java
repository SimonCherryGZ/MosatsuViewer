package com.simoncherry.mosatsu.Activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.simoncherry.mosatsu.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class ManageActivity extends Activity implements OnScrollListener{

	private GridView gridView = null;
	private LinearLayout layout_show;
	private ImageView img_side_A;
	private ImageView img_side_B;
	
	private List<String>mListA = null;
	private List<String>mListB = null;
	public static Map<String,Bitmap>gridViewBitmapCaches = new HashMap<String,Bitmap>();
	private MyGridViewAdapter adapter = null;
	
	private int imgCount = 0;
	private int currentPos = 0;
	private int whichSide = 0;
	
	final static int SideA = 0;
	final static int SideB = 1;
	
	final static int GETIMAGEPATH = 1024;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_manage);
		
		gridView = (GridView)findViewById(R.id.gridview);
		layout_show = (LinearLayout)findViewById(R.id.layout_show);
		img_side_A = (ImageView)findViewById(R.id.img_side_A);
		img_side_B = (ImageView)findViewById(R.id.img_side_B);

		img_side_A.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				whichSide = SideA;
				showAlertDialog();
			}
		});
		
		img_side_B.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				whichSide = SideB;
				showAlertDialog();
			}
		});

		img_side_A.setVisibility(View.INVISIBLE);
		img_side_B.setVisibility(View.INVISIBLE);
		layout_show.setVisibility(View.INVISIBLE);
		
		initData();
		setAdapter();
	}
	
	private void showAlertDialog(){
		AlertDialog alertDialog = new AlertDialog.Builder(this)
			.setTitle(R.string.manage_add_title)
			.setPositiveButton(R.string.manage_add_positive, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
					intent.setType("image/*");
					startActivityForResult(intent, GETIMAGEPATH);
				}
			})
			.setNegativeButton(R.string.manage_add_negative, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			}).create();

		DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
		Window window = alertDialog.getWindow();  
		WindowManager.LayoutParams lp = window.getAttributes();
		
		if(whichSide == SideA){
			lp.y = -displayMetrics.heightPixels/4;
		}else{
			lp.y = displayMetrics.heightPixels/4;
		}

		alertDialog.show();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(requestCode == GETIMAGEPATH){
			Uri uri = data.getData();
			
			String filePreference = index2preference(currentPos+1, whichSide);
			SharedPreferences preference = getSharedPreferences(
					"mosatsu_test", Context.MODE_PRIVATE);
			Editor edit = preference.edit();
			edit.putString(filePreference, uri.getPath());
			edit.commit();
			
			if(whichSide == SideA){
				mListA.set(currentPos, uri.getPath() + "/");
				img_side_A.setImageBitmap(BitmapFactory.decodeFile(mListA.get(currentPos)));
			}else{
				mListB.set(currentPos, uri.getPath() + "/");
				img_side_B.setImageBitmap(BitmapFactory.decodeFile(mListB.get(currentPos)));
			}
			//*
			if(currentPos == imgCount){
				imgCount++;
				edit.putInt("count", imgCount);
				edit.commit();
				
				String filePath = Environment.getExternalStorageDirectory().getAbsolutePath();
				filePath += "/MOSATSU/Pic";
				mListA.add(filePath + "/" + "add_icon.png" + "/");
				mListB.add(filePath + "/" + "add_icon.png" + "/");
			}
			//*/
			MyGridViewAdapter ad = (MyGridViewAdapter)gridView.getAdapter();
			ad.notifyDataSetChanged();
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
			if(layout_show.getVisibility() == View.VISIBLE){
				
				layout_show.setVisibility(View.INVISIBLE);
				gridView.setVisibility(View.VISIBLE);
				return true;
			}	
		}
		
		startActivity(new Intent(ManageActivity.this, MenuActivity.class));
		ManageActivity.this.finish();
		
		return super.onKeyDown(keyCode, event);
	}
	
	private void assets2sd(String src, String dst){
		
		try{
			int bytesum = 0;   
	        int byteread = 0;
	        byte[] buffer = new byte[8192];
	        
			InputStream is = this.getBaseContext().getAssets().open(src);
			FileOutputStream fs = new FileOutputStream(dst + "/" + src);
			
			while ( (byteread = is.read(buffer)) != -1) {   
                bytesum += byteread;
                System.out.println(bytesum);   
                fs.write(buffer, 0, byteread);   
            }   
			fs.flush();
			fs.close();
			is.close();   
			
		}catch(IOException e){
			// TODO
			e.printStackTrace();
		}
	}
	
	private void initData(){
		mListA = new ArrayList<String>();
		mListB = new ArrayList<String>();
		String filePath = Environment.getExternalStorageDirectory().getAbsolutePath();
		filePath += "/MOSATSU/Pic";
		File picFileDir = new File(filePath);
		if(!picFileDir.exists()){
			picFileDir.mkdirs();
		}
		
		SharedPreferences preference = getSharedPreferences(
				"mosatsu_test", Context.MODE_PRIVATE);
		Editor edit = preference.edit();
		Boolean isFirstUse = preference.getBoolean("isfirst", true);
		//Boolean isFirstUse = true;
		Log.v("isFirst", String.valueOf(isFirstUse));
		
		if(isFirstUse){
			
			assets2sd("sample1_a.jpg", filePath);
			assets2sd("sample1_b.jpg", filePath);
			assets2sd("sample2_a.png", filePath);
			assets2sd("sample2_b.png", filePath);
			assets2sd("sample3_a.png", filePath);
			assets2sd("sample3_b.png", filePath);
			assets2sd("add_icon.png", filePath);
			
			edit.putString("p_1_a", filePath + "/sample1_a.jpg");
			edit.putString("p_1_b", filePath + "/sample1_b.jpg");
			edit.putString("p_2_a", filePath + "/sample2_a.png");
			edit.putString("p_2_b", filePath + "/sample2_b.png");
			edit.putString("p_3_a", filePath + "/sample3_a.png");
			edit.putString("p_3_b", filePath + "/sample3_b.png");
			edit.putInt("count", 3);
			edit.putBoolean("isfirst", false);
			edit.commit();
		}

		imgCount = preference.getInt("count", 0);
		if(imgCount != 0){
			for(int i=1; i<=imgCount; i++){
				String filenameA = index2filename(i, SideA);
				mListA.add(filenameA + "/");
				String filenameB = index2filename(i, SideB);
				mListB.add(filenameB + "/");
			}
		}
		mListA.add(filePath + "/" + "add_icon.png" + "/");
		mListB.add(filePath + "/" + "add_icon.png" + "/");
	}
	
	private String index2preference(int index, int which){
		String preference = "";
		
		if(which == SideA){
			preference = "p_" + String.valueOf(index) + "_a";	
		}else{
			preference = "p_" + String.valueOf(index) + "_b";
		}
		
		return preference;
	}
	
	private String index2filename(int index, int which){
		SharedPreferences preference = getSharedPreferences(
				"mosatsu_test", Context.MODE_PRIVATE);
		
		String filePath = index2preference(index, which);
		filePath = preference.getString(filePath, "");
		return filePath;
	}
	
	private void delItem(int pos){
		SharedPreferences preference = getSharedPreferences(
				"mosatsu_test", Context.MODE_PRIVATE);
		Editor edit = preference.edit();
		
		int count = preference.getInt("count", 0);
		int index = pos+1;
		
		if(count > 1 && index <= count-1){
			for(int i=index; i<=count-1; i++){
				String keyA = index2preference(i, SideA);
				String keyB = index2preference(i, SideB);
				String filenameA = index2filename(i+1, SideA);
				String filenameB = index2filename(i+1, SideB);
				edit.putString(keyA, filenameA);
				edit.putString(keyB, filenameB);
				edit.commit();
			}
		}
		edit.putInt("count", count-1);
		edit.commit();
		
		mListA.remove(pos);
		mListB.remove(pos);
		adapter.notifyDataSetChanged();
	}
	
	private void setAdapter(){
		adapter = new MyGridViewAdapter(this,mListA);
		gridView.setAdapter(adapter);

		gridView.setOnScrollListener((OnScrollListener)this);
		gridView.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				currentPos = position;
				if(position <= imgCount){
					img_side_A.setImageBitmap(BitmapFactory.decodeFile(mListA.get(position)));
					img_side_B.setImageBitmap(BitmapFactory.decodeFile(mListB.get(position)));
					gridView.setVisibility(View.INVISIBLE);
					layout_show.setVisibility(View.VISIBLE);
					img_side_A.setVisibility(View.VISIBLE);
					img_side_B.setVisibility(View.VISIBLE);
				}else{
					// TODO
				}
			};
		});	
		
		gridView.setOnItemLongClickListener(new OnItemLongClickListener(){
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
				int item_count = gridView.getCount();
				if(position < item_count-1){
					AlertDialog.Builder builder = new AlertDialog.Builder(ManageActivity.this);
	                //builder.setTitle("No." + String.valueOf(position));
	                builder.setTitle(R.string.manage_del_title);
	                builder.setMessage(R.string.manage_del_message);
	                builder.setPositiveButton(R.string.manage_del_positive, new DialogInterface.OnClickListener(){
	                    @Override
	                    public void onClick(DialogInterface dialog, int which){
	                    	// TODO
	                    	delItem(position);
	                    	dialog.dismiss();
	                    }
	                });
	
	                builder.setNegativeButton(R.string.manage_del_negative, new DialogInterface.OnClickListener(){
	                    @Override
	                    public void onClick(DialogInterface dialog, int which){
	                    	dialog.dismiss();
	                    }
	                });
	               
	                builder.show();
				}
				return false;
			}
		});
	}
	
	private void recycleBitmapCaches(int fromPosition, int toPosition){
		Bitmap delBitmap = null;
		for(int del=fromPosition; del<toPosition; del++){
			delBitmap = gridViewBitmapCaches.get(mListA.get(del));
			if(delBitmap != null){
				gridViewBitmapCaches.remove(mListA.get(del));
				delBitmap.recycle();
				delBitmap = null;
			}
		}
	}
	
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount){
		recycleBitmapCaches(0, firstVisibleItem);
		recycleBitmapCaches(firstVisibleItem+visibleItemCount, totalItemCount);
	}
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState){
		// TODO Auto-generated method stub
	}
	
	

	public class MyGridViewAdapter extends BaseAdapter{

		private LayoutInflater mLayoutInflater = null;
		private List<String>mListA = null;
		//private int width = 105;
		//private int height = 187;
		private int width = 150;
		private int height = 240;
		
		public class MyGridViewHolder{
			public ImageView imageview_thumbnail;
		}
		
		public MyGridViewAdapter(Context context, List<String>list){
			this.mListA = list;
			mLayoutInflater = LayoutInflater.from(context);		
		}
		
		@Override
		public int getCount() {
			return mListA.size();
		}

		@Override
		public Object getItem(int position) {
			return mListA.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			MyGridViewHolder viewHolder = null;
			if(convertView == null){
				viewHolder = new MyGridViewHolder();
				convertView = mLayoutInflater.inflate(R.layout.activity_manage_item, null);
				viewHolder.imageview_thumbnail = (ImageView)convertView.findViewById(R.id.imageview_thumbnail);
				convertView.setTag(viewHolder);
			}
			else{
				viewHolder = (MyGridViewHolder)convertView.getTag();
			}
			
			String url = mListA.get(position);

			if(cancelPotentialLoad(url, viewHolder.imageview_thumbnail)){
				AsyncLoadImageTask task = new AsyncLoadImageTask(viewHolder.imageview_thumbnail);
				LoadedDrawable loadedDrawable = new LoadedDrawable(task);
				
				viewHolder.imageview_thumbnail.setImageDrawable(loadedDrawable);
				task.execute(position);
			}

			return convertView;
		}
		
		private Bitmap getBitmapFromUrl(String url){
			Bitmap bitmap = null;
			bitmap = ManageActivity.gridViewBitmapCaches.get(url);
			if(bitmap != null){
				return bitmap;
			}

			url = url.substring(0, url.lastIndexOf("/"));
			try{
				FileInputStream is = new FileInputStream(url);
				bitmap = BitmapFactory.decodeFileDescriptor(is.getFD());
				//added by simon
				is.close(); 
			}catch(FileNotFoundException e){
				e.printStackTrace();
			}catch(IOException e){
				e.printStackTrace();
			}
			
			bitmap = BitmapUtilities.getBitmapThumbnail(bitmap, width, height);
			return bitmap;
		}
		
		private class AsyncLoadImageTask extends AsyncTask<Integer,Void,Bitmap>{
			private String url = null;
			private final WeakReference<ImageView>imageViewReference;
			
			public AsyncLoadImageTask(ImageView imageview){
				super();
				imageViewReference = new WeakReference<ImageView>(imageview);
			}
			
			protected Bitmap doInBackground(Integer...params){
				Bitmap bitmap = null;
				this.url = mListA.get(params[0]);
				bitmap = getBitmapFromUrl(url);
				
				ManageActivity.gridViewBitmapCaches.put(mListA.get(params[0]), bitmap);
				return bitmap;
			}
			
			protected void onPostExecute(Bitmap resultBitmap){
				if(isCancelled()){
					resultBitmap = null;
				}
				
				if(imageViewReference != null){
					ImageView imageview = imageViewReference.get();
					AsyncLoadImageTask loadImageTask = getAsyncLoadImageTask(imageview);
					if(this == loadImageTask)
					{
						imageview.setImageBitmap(resultBitmap);
						//imageview.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
						imageview.setScaleType(ImageView.ScaleType.CENTER_CROP);
					}
				}
				super.onPostExecute(resultBitmap);
			}
		}
		
		private boolean cancelPotentialLoad(String url, ImageView imageview){
			AsyncLoadImageTask loadImageTask = getAsyncLoadImageTask(imageview);
			if(loadImageTask != null){
				String bitmapUrl = loadImageTask.url;
				if((bitmapUrl == null)||(!bitmapUrl.equals(url))){
					loadImageTask.cancel(true);
				}else{
					return false;
				}
			}
			return true;
		}
		
		private AsyncLoadImageTask getAsyncLoadImageTask(ImageView imageview){
			if(imageview != null){
				Drawable drawable = imageview.getDrawable();
				if(drawable instanceof LoadedDrawable){
					LoadedDrawable loadedDrawable = (LoadedDrawable)drawable;
					return loadedDrawable.getLoadImageTask();
				}
			}
			return null;
		}
		
		public class LoadedDrawable extends ColorDrawable{
			private final WeakReference<AsyncLoadImageTask>loadImageTaskReference;
			public LoadedDrawable(AsyncLoadImageTask loadImageTask){
				super(Color.TRANSPARENT);
				loadImageTaskReference = new WeakReference<AsyncLoadImageTask>(loadImageTask);
			}
			
			public AsyncLoadImageTask getLoadImageTask(){
				return loadImageTaskReference.get();
			}
		}
	}

	public static class BitmapUtilities{
		
		public BitmapUtilities(){
		}
		
		public static Bitmap getBitmapThumbnail(String path, int width, int height){
			Bitmap bitmap = null;
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(path, opts);
			opts.inSampleSize = Math.max((int)(opts.outHeight/(float)height), (int)(opts.outWidth/(float)width));
			opts.inJustDecodeBounds = false;
			bitmap = BitmapFactory.decodeFile(path, opts);
			return bitmap;
		}
		
		public static Bitmap getBitmapThumbnail(Bitmap bmp, int width, int height){
			Bitmap bitmap = null;
			if(bmp != null){
				int bmpWidth = bmp.getWidth();
				int bmpHeight = bmp.getHeight();
				if(width!=0 && height!=0){
					Matrix matrix = new Matrix();
//					float scaleWidth = ((float)width/bmpWidth);
//					float scaleHeight = ((float)height/bmpHeight);
//					matrix.postScale(scaleWidth, scaleHeight);
					float scaleWidth = ((float)width/bmpWidth);
					float scaleHeight = ((float)height/bmpHeight);
					float inSampleSize = scaleWidth > scaleHeight ? scaleWidth : scaleHeight;
					matrix.postScale(inSampleSize, inSampleSize);
					
					bitmap = Bitmap.createBitmap(bmp, 0, 0, bmpWidth, bmpHeight, matrix, true);
				}else{
					bitmap = bmp;
				}
			}
			return bitmap;
		}
	}

}
