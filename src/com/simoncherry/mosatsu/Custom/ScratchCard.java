package com.simoncherry.mosatsu.Custom;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DiscretePathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.simoncherry.mosatsu.R;

public class ScratchCard extends View
{
	private Paint mOutterPaint = new Paint();
	private Paint mStrokePaint = new Paint();
	private Paint mNormalPaint = new Paint();
	private Path mPath = new Path();
	private Canvas mCanvas;
	private Bitmap mBitmap;

	private Bitmap imgSideA;
	private Bitmap imgSideB;
	
	private boolean isComplete;

	private int mLastX;
	private int mLastY;
	 
	private int mViewWidth=1;  
	private int mViewHeight=1;  
	
	private int PosX = 0;
	private int PosY = 0;	
	
	private int widthSize;
	private int heightSize;
	
	private boolean measureLock = false;
	private boolean isPaintEffect = true;
	private int paintSize = 30;

	public ScratchCard(Context context)
	{
		this(context, null);
	}

	public ScratchCard(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
	}

	public ScratchCard(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);

		TypedArray myTypedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ScratchCard, defStyle, 0);
		imgSideA = BitmapFactory.decodeResource(getResources(), 
				myTypedArray.getResourceId(R.styleable.ScratchCard_imageSideA, 0));
		imgSideB = BitmapFactory.decodeResource(getResources(), 
				myTypedArray.getResourceId(R.styleable.ScratchCard_imageSideB, 0));
		myTypedArray.recycle();
		
		Resources resources = this.getResources();
		DisplayMetrics dm = resources.getDisplayMetrics();
		widthSize = dm.widthPixels;
		heightSize = dm.heightPixels;
		
		setUpOutterPaint();
		setUpStrokePaint();
		setUpNormalPaint();
		
		mPath = new Path();
	}
	
	public void setScratchCard(int which, Bitmap bitmap){
		if(mCanvas != null){
			mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
		}

		if(which == 0){
			imgSideA = bitmap;	
		}else if(which == 1){
			imgSideB = bitmap;
		}
		
		ResetCard();
	}
	
	public void ResetCard(){
		mPath = new Path();
		if(mCanvas != null && imgSideA != null){
			
			mCanvas.drawBitmap(imgSideA, null, 
					new RectF(PosX, PosY, 
							mViewWidth + PosX, 
							mViewHeight + PosY), null);

		}

		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas)
	{	     
		canvas.drawBitmap(imgSideB, null, 
				new RectF(PosX, PosY, 
						mViewWidth + PosX, 
						mViewHeight + PosY), null);
		drawPath();
		canvas.drawBitmap(mBitmap, PosX, PosY, null);

		/*
		if (!isComplete)
		{
			drawPath();
			canvas.drawBitmap(mBitmap, 0, 0, null);
		}
		*/
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		if(measureLock == false){

		    int desireW = MeasureSpec.getSize(widthMeasureSpec);
		    int desireH = MeasureSpec.getSize(heightMeasureSpec);
		    
		    if(this.getResources().getConfiguration().orientation ==Configuration.ORIENTATION_PORTRAIT) {
		    	
			    if(desireW >= desireH){
		
					mViewWidth = Math.min(widthSize, desireW);
					float scaleXY = (float)(desireH) / desireW;
					mViewHeight = (int)(mViewWidth * scaleXY);
					
				}else{
		
					if(desireW <= widthSize){
						mViewHeight = Math.min(heightSize, desireH);
						float scaleXY = (float)(desireW) / desireH;
						mViewWidth = (int)(mViewHeight * scaleXY);
					}else{
						mViewWidth = Math.min(widthSize, desireW);
						float scaleXY = (float)(desireH) / desireW;
						mViewHeight = (int)(mViewWidth * scaleXY);
						
						if(mViewHeight > heightSize){
							mViewHeight = Math.min(heightSize, mViewHeight);
							mViewWidth = (int)(mViewHeight / scaleXY);
						}
					}
				}
			    
		    }else if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
		    	
		    	if(desireH >= desireW){
		    		/*
		    		mViewWidth = Math.min(widthSize, desireW);
					float scaleXY = (float)(desireH) / desireW;
					mViewHeight = (int)(mViewWidth * scaleXY);
					*/
		    		mViewHeight = Math.min(heightSize, desireH);
					float scaleXY = (float)(desireW) / desireH;
					mViewWidth = (int)(mViewHeight * scaleXY);
					
				}else{
		
					if(desireH <= heightSize){
						/*
						mViewHeight = Math.min(heightSize, desireH);
						float scaleXY = (float)(desireW) / desireH;
						mViewWidth = (int)(mViewHeight * scaleXY);
						*/
						mViewWidth = Math.min(widthSize, desireW);
						float scaleXY = (float)(desireH) / desireW;
						mViewHeight = (int)(mViewWidth * scaleXY);
						
					}else{
						/*
						mViewWidth = Math.min(widthSize, desireW);
						float scaleXY = (float)(desireH) / desireW;
						mViewHeight = (int)(mViewWidth * scaleXY);
						
						if(mViewHeight > heightSize){
							mViewHeight = Math.min(heightSize, mViewHeight);
							mViewWidth = (int)(mViewHeight / scaleXY);
						}
						*/
						mViewHeight = Math.min(heightSize, desireH);
						float scaleXY = (float)(desireW) / desireH;
						mViewWidth = (int)(mViewHeight * scaleXY);
						
						if(mViewWidth > widthSize){
							mViewWidth = Math.min(widthSize, mViewWidth);
							mViewHeight = (int)(mViewWidth / scaleXY);
						}
					}
				}
		    	
		    }
		}
	    
	    setMeasuredDimension(mViewWidth, mViewHeight);
		
	    if(mViewWidth > 0 && mViewHeight > 0){
			mBitmap = Bitmap.createBitmap(mViewWidth, mViewHeight, Config.ARGB_8888);
			mCanvas = new Canvas(mBitmap);
	
			mCanvas.drawBitmap(imgSideA, null, 
					new RectF(PosX, PosY, 
							mViewWidth + PosX, 
							mViewHeight + PosY), null);
	    }

	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
    }
	
	public int readCardWidth()
	{
		return mViewWidth;
	}
	
	public int readCardHeight()
	{
		return mViewHeight;
	}
	
	public void setMeasureLock(boolean islock){
		measureLock = islock;
	}
	
	private void setUpOutterPaint()
	{
		mOutterPaint.setPathEffect(new DiscretePathEffect(5.0F, 5.0F)); //离散路径效果
		mOutterPaint.setColor(Color.parseColor("#c0c0c0"));
		mOutterPaint.setStyle(Paint.Style.STROKE);
		mOutterPaint.setStrokeJoin(Paint.Join.MITER);
		mOutterPaint.setStrokeCap(Paint.Cap.BUTT);
		//mOutterPaint.setStrokeWidth(30);
		mOutterPaint.setStrokeWidth(paintSize);
		mOutterPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
	}
	
	private void setUpStrokePaint()
	{
		mStrokePaint.setPathEffect(new DiscretePathEffect(1.0F, 0.7F));
		mStrokePaint.setColor(Color.parseColor("#ffffff"));
		mStrokePaint.setStyle(Paint.Style.STROKE);
		mStrokePaint.setStrokeJoin(Paint.Join.MITER);
		mStrokePaint.setStrokeCap(Paint.Cap.BUTT);
		//mStrokePaint.setStrokeWidth(40);
		mStrokePaint.setStrokeWidth(paintSize + 10);
	}
	
	private void setUpNormalPaint()
	{
		mNormalPaint.setColor(Color.parseColor("#ffffff"));
		mNormalPaint.setStyle(Paint.Style.STROKE);
		mNormalPaint.setStrokeJoin(Paint.Join.ROUND);
		mNormalPaint.setStrokeCap(Paint.Cap.ROUND);
		//mNormalPaint.setStrokeWidth(30);
		mNormalPaint.setStrokeWidth(paintSize);
		mNormalPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
	}
	
	public void setPaintSize(int size)
	{
		paintSize = (size+1) * 10;
		mOutterPaint.setStrokeWidth(paintSize);
		mStrokePaint.setStrokeWidth(paintSize + 10);
		mNormalPaint.setStrokeWidth(paintSize);
	}

	private void drawPath()
	{
		if(isPaintEffect == true){
			mCanvas.drawPath(mPath, mStrokePaint);
			mCanvas.drawPath(mPath, mOutterPaint);
		}else{
			mCanvas.drawPath(mPath, mNormalPaint);
		}
	}
	
	public void setPaintEffect(boolean PaintEffect){
		isPaintEffect = PaintEffect;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		int action = event.getAction();
		int x = (int) event.getX();
		int y = (int) event.getY();
		
		switch (action)
		{
			case MotionEvent.ACTION_DOWN:
				mLastX = x;
				mLastY = y;
				mPath.moveTo(mLastX, mLastY);
				break;
			case MotionEvent.ACTION_MOVE:
	
				int dx = Math.abs(x - mLastX);
				int dy = Math.abs(y - mLastY);
	
				if (dx > 3 || dy > 3)
					mPath.lineTo(x, y);
	
				mLastX = x;
				mLastY = y;
				break;
			case MotionEvent.ACTION_UP:
				//new Thread(mRunnable).start();
				break;
		}

		invalidate();
		return true;
	}

	private Runnable mRunnable = new Runnable()
	{
		private int[] mPixels;

		@Override
		public void run()
		{

			int w = getWidth();
			int h = getHeight();

			float wipeArea = 0;
			float totalArea = w * h;

			Bitmap bitmap = mBitmap;

			mPixels = new int[w * h];

			bitmap.getPixels(mPixels, 0, w, 0, 0, w, h);

			for (int i = 0; i < w; i++)
			{
				for (int j = 0; j < h; j++)
				{
					int index = i + j * w;
					if (mPixels[index] == 0)
					{
						wipeArea++;
					}
				}
			}

			if (wipeArea > 0 && totalArea > 0)
			{
				int percent = (int) (wipeArea * 100 / totalArea);
				Log.e("TAG", percent + "");

				if (percent > 70)
				{
					Log.e("TAG", "percent > 70");
					isComplete = true;
					postInvalidate();
				}
			}
		}

	};
	
}
