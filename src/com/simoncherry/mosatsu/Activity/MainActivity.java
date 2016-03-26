package com.simoncherry.mosatsu.Activity;

import java.util.ArrayList;
import java.util.List;

import com.simoncherry.mosatsu.R;
import com.simoncherry.mosatsu.Adapter.HorizontalScrollViewAdapter;
import com.simoncherry.mosatsu.Custom.MyHorizontalScrollView;
import com.simoncherry.mosatsu.Custom.MyScrollView;
import com.simoncherry.mosatsu.Custom.ScratchCard;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewStub;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class MainActivity extends Activity implements OnSeekBarChangeListener
{
	private ScratchCard myScratchCard;
	private LinearLayout top_menu;
	private Button reset_btn;
	private Button select_btn;
	private ImageView img_load;
	private Animation img_load_anim;
	private ViewStub main_viewstub;
	private Animation gallery_show_anim;
	private Animation gallery_hide_anim;
	private Animation btn_show_anim;
	private Animation btn_hide_anim;
	private Animation seekbar_show_anim;
	private Animation seekbar_hide_anim;
	private SeekBar paint_seekbar;
	
	private MyHorizontalScrollView mHorizontalScrollView;
	private HorizontalScrollViewAdapter mAdapter;
	private MyScrollView mScrollView;
	
	private List<String>mListA = null;
	private List<String>mListB = null;
	
	private int imgIndex = 0;
	private int imgCount = 1;
	private int widthSize;
	private int heightSize;
	
	private final static int SideA = 0;
	private final static int SideB = 1;
	private final static int MSG_INIT_OK = 1024;
	private final static int MSG_VIEWSTUB_OK = 512;
	private final static int MSG_GALLERY_OK = 256;
	private final static int MSG_ANIM_END = 128;
	
	private boolean isLoadImgOK = false;
	private boolean isInitOK = false;
	private boolean isMenuShow = false;
	private boolean isPaintEffect = true;
	
	private int activePos = 0;
	
	Handler myHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch(msg.what)
			{
				case MSG_INIT_OK :
					inflatedViewStub();
					break;
				case MSG_VIEWSTUB_OK :
					initGallery();
					break;
				case MSG_GALLERY_OK :
					ShowImage(imgIndex);
					img_load.startAnimation(img_load_anim);
					break;
				case MSG_ANIM_END :
					img_load.setVisibility(View.GONE);
					break;
			}
		}
	};
	
	private void inflatedViewStub(){
		View inflatedView = main_viewstub.inflate();
		top_menu = (LinearLayout)inflatedView.findViewById(R.id.top_menu);
		reset_btn = (Button)inflatedView.findViewById(R.id.reset_btn);
		select_btn = (Button)inflatedView.findViewById(R.id.select_btn);
		paint_seekbar = (SeekBar)inflatedView.findViewById(R.id.seekbar);
		myScratchCard = (ScratchCard)inflatedView.findViewById(R.id.mycard);
		
		if(this.getResources().getConfiguration().orientation ==Configuration.ORIENTATION_PORTRAIT) {
			mHorizontalScrollView = (MyHorizontalScrollView)inflatedView.findViewById(R.id.id_horizontalScrollView);
		}else if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			mScrollView = (MyScrollView)inflatedView.findViewById(R.id.id_horizontalScrollView);
		}
		
		main_viewstub.setVisibility(View.VISIBLE);
		
		reset_btn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if(top_menu.getVisibility() == View.VISIBLE){
					myScratchCard.ResetCard();
				}
			}
		});
		
		select_btn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if(top_menu.getVisibility() == View.VISIBLE){
					if(isPaintEffect == true){
						isPaintEffect = false;
						select_btn.setText(R.string.main_btn_select2);
					}else{
						isPaintEffect = true;
						select_btn.setText(R.string.main_btn_select1);
					}
					myScratchCard.setPaintEffect(isPaintEffect);
				}
			}	
		});
		
		if(this.getResources().getConfiguration().orientation ==Configuration.ORIENTATION_PORTRAIT) {
			mHorizontalScrollView.setOnItemClickListener(new com.simoncherry.mosatsu.Custom.MyHorizontalScrollView.OnItemClickListener()
			{
				@Override
				public void onClick(View view, int position)
				{
					if(mHorizontalScrollView.getVisibility() == View.VISIBLE){
						activePos = position;
						ShowImage(position);
						view.setBackgroundColor(Color.parseColor("#AA024DA4"));
					}
				}
			});
		}else if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
			mScrollView.setOnItemClickListener(new com.simoncherry.mosatsu.Custom.MyScrollView.OnItemClickListener(){
				@Override
				public void onClick(View view, int pos) {
					if(mScrollView.getVisibility() == View.VISIBLE){
						activePos = pos;
						ShowImage(pos);
						view.setBackgroundColor(Color.parseColor("#AA024DA4"));
					}
				}
			});
		}

		paint_seekbar.setOnSeekBarChangeListener((OnSeekBarChangeListener) this);
		
		myHandler.sendEmptyMessage(MSG_VIEWSTUB_OK);
	}
	
	@Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		if(paint_seekbar.getVisibility() == View.VISIBLE){
			myScratchCard.setPaintSize(progress);
		}
    }
	
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
	}

	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		if(savedInstanceState != null){
			imgIndex = savedInstanceState.getInt("savePos", 0);
			activePos = imgIndex;
		}

		main_viewstub = (ViewStub)findViewById(R.id.main_viewstub);
		img_load = (ImageView)findViewById(R.id.img_load);
		
		Resources resources = this.getResources();
		DisplayMetrics dm = resources.getDisplayMetrics();
		widthSize = dm.widthPixels;
		heightSize = dm.heightPixels;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		new Thread(new InitThread()).start();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("savePos", activePos);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}
	
	private void InitSystem(){
		InitImgData();
		if(this.getResources().getConfiguration().orientation ==Configuration.ORIENTATION_PORTRAIT) {
			gallery_show_anim = AnimationUtils.loadAnimation(this, R.anim.slide_in_from_bottom);
			gallery_hide_anim = AnimationUtils.loadAnimation(this, R.anim.slide_out_from_top);
			btn_show_anim = AnimationUtils.loadAnimation(this, R.anim.slide_in_from_top);
			btn_hide_anim = AnimationUtils.loadAnimation(this, R.anim.slide_out_from_bottom);
		}else if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			gallery_show_anim = AnimationUtils.loadAnimation(this, R.anim.slide_in_from_right);
			gallery_hide_anim = AnimationUtils.loadAnimation(this, R.anim.slide_out_from_left);
			btn_show_anim = AnimationUtils.loadAnimation(this, R.anim.slide_in_from_left);
			btn_hide_anim = AnimationUtils.loadAnimation(this, R.anim.slide_out_from_right);
		}
		
		seekbar_show_anim = AnimationUtils.loadAnimation(this, R.anim.slide_in_from_top);
		seekbar_hide_anim = AnimationUtils.loadAnimation(this, R.anim.slide_out_from_bottom);
		
		img_load_anim = AnimationUtils.loadAnimation(this, R.anim.pull_out_from_right);
		img_load_anim.setAnimationListener(new AnimationListener(){
			@Override
			public void onAnimationStart(Animation animation) {
			}
			@Override
			public void onAnimationEnd(Animation animation) {
				img_load.setVisibility(View.GONE);
			}
			@Override
			public void onAnimationRepeat(Animation animation) {
			}			
		});
	}
	
	public class InitThread implements Runnable {
		@Override
		public void run() {
			try{
				InitSystem();
				while(!isInitOK){
					if(isLoadImgOK == true && img_load_anim != null){
						isInitOK = true;
						myHandler.sendEmptyMessage(MSG_INIT_OK);
					}
				}
				
			}catch(Exception e){
				Log.e("InitThread", e.toString());
			}
		}
	}
	
	private void initGallery(){
		mAdapter = new HorizontalScrollViewAdapter(this, mListA);
		if(this.getResources().getConfiguration().orientation ==Configuration.ORIENTATION_PORTRAIT) {
			mHorizontalScrollView.initDatas(mAdapter);
		}else if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			mScrollView.initDatas(mAdapter);
		}
		myHandler.sendEmptyMessage(MSG_GALLERY_OK);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
			startActivity(new Intent(MainActivity.this, MenuActivity.class));
			MainActivity.this.finish();
		}else if(keyCode == KeyEvent.KEYCODE_MENU){
			if(isMenuShow == false){
				if(this.getResources().getConfiguration().orientation ==Configuration.ORIENTATION_PORTRAIT) {
					mHorizontalScrollView.setVisibility(View.VISIBLE);
					mHorizontalScrollView.startAnimation(gallery_show_anim);
					mHorizontalScrollView.setTouchLock(false);
					mHorizontalScrollView.setEnabled(true);
				}else if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
					mScrollView.setVisibility(View.VISIBLE);
					mScrollView.startAnimation(gallery_show_anim);
					mScrollView.setTouchLock(false);
					mScrollView.setEnabled(true);
				}
				
				top_menu.setVisibility(View.VISIBLE);
				top_menu.startAnimation(btn_show_anim);
				paint_seekbar.setVisibility(View.VISIBLE);
				paint_seekbar.startAnimation(seekbar_show_anim);
				paint_seekbar.setEnabled(true);
				isMenuShow = true;
			}else{
				if(this.getResources().getConfiguration().orientation ==Configuration.ORIENTATION_PORTRAIT) {
					mHorizontalScrollView.startAnimation(gallery_hide_anim);
					mHorizontalScrollView.setVisibility(View.GONE);
					mHorizontalScrollView.setTouchLock(true);
					mHorizontalScrollView.setEnabled(false);
				}else if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
					mScrollView.startAnimation(gallery_hide_anim);
					mScrollView.setVisibility(View.GONE);
					mScrollView.setTouchLock(true);
					mScrollView.setEnabled(false);
				}
				
				top_menu.startAnimation(btn_hide_anim);
				top_menu.setVisibility(View.GONE);
				paint_seekbar.startAnimation(seekbar_hide_anim);
				paint_seekbar.setVisibility(View.GONE);
				paint_seekbar.setEnabled(false);
				isMenuShow = false;
			}
			return true;
		}
		
		return super.onKeyDown(keyCode, event);
	}
	/*
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		
	}
	*/

	private void InitImgData(){
		mListA = new ArrayList<String>();
		mListB = new ArrayList<String>();

		SharedPreferences preference = getSharedPreferences(
				"mosatsu_test", Context.MODE_PRIVATE);
		
		imgCount = preference.getInt("count", 1);
		if(imgCount != 0){
			for(int i=1; i<=imgCount; i++){
				String filenameA = index2filename(i, SideA);
				mListA.add(filenameA + "/");
				String filenameB = index2filename(i, SideB);
				mListB.add(filenameB + "/");
			}
		}
		isLoadImgOK = true;
	}
	
	private String index2filename(int index, int which){
		SharedPreferences preference = getSharedPreferences(
				"mosatsu_test", Context.MODE_PRIVATE);
		
		String filePath = "";
		if(which == SideA){
			filePath = "p_" + String.valueOf(index) + "_a";	
		}else{
			filePath = "p_" + String.valueOf(index) + "_b";
		}
		filePath = preference.getString(filePath, "");
		return filePath;
	}
	
	private void ShowImage(int imgIndex){
		Bitmap bitmap;
		bitmap = BitmapFactory.decodeFile(mListA.get(imgIndex));
		
		myScratchCard.setMeasureLock(false);
		
		myScratchCard.setScratchCard(SideA, bitmap);

		myScratchCard.measure(MeasureSpec.AT_MOST + bitmap.getWidth(), 
				MeasureSpec.AT_MOST + bitmap.getHeight());
		
		int posX = (widthSize - myScratchCard.readCardWidth())/2;
		int posY = (heightSize - myScratchCard.readCardHeight())/2;
		
		myScratchCard.layout(
				posX, 
				posY, 
				myScratchCard.readCardWidth() + posX, 
				myScratchCard.readCardHeight() + posY);
		
		bitmap = BitmapFactory.decodeFile(mListB.get(imgIndex));
		myScratchCard.setScratchCard(SideB, bitmap);
		
		myScratchCard.setMeasureLock(true);
	}

}
