package com.trends.photoalbum.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

public class MyScrollView extends ScrollView implements OnGestureListener {
	private View view;
	private Handler handler;
	private GestureDetector mGestureDetector;
	public int touchEventId = 0x7f090076;
	private int lastY = 0;

	public MyScrollView(Context context) {
		super(context);
	}

	public MyScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected int computeVerticalScrollOffset() {
		return super.computeVerticalScrollOffset();
	}

	@Override
	protected int computeVerticalScrollRange() {
		return super.computeVerticalScrollRange();
	}

	public void getView() {
		view = getChildAt(0);
		if (view != null)
			init();
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("HandlerLeak")
	private void init() {
		mGestureDetector = new GestureDetector(this);
		this.setOnTouchListener(onTouchListener);
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
					case 1:
						if (view.getMeasuredHeight() <= getScrollY() + getHeight() + 150) {

							if (onScrollListener != null) {
								onScrollListener.onBottom();
							}
						} else {
							if (onScrollListener != null) {
								onScrollListener.onScroll(1);
								// onScrollListener.onScrolling();
							}
						}
						break;
					case 2:
						if (getScrollY() == 0) {
							if (onScrollListener != null) {
								onScrollListener.onTop();
							}
						} else if (onScrollListener != null) {
							onScrollListener.onScroll(2);
						}
						break;
					case 3:
						if (lastY == getScrollY()) {
							System.out.println(getScrollY());
							onScrollListener.onScrollStop();
						} else {
							handler.sendMessage(handler.obtainMessage(3));
							lastY = getScrollY();
						}
						break;
					case 4:
						onScrollListener.onScrolling();
						break;
					default:
						break;
				}
			}

		};
	}

	/**
	 * 定义在Activity里面实现的接口，用于回调接收来自scrollView的滚动事件
	 * 
	 * @author hhn
	 * 
	 */
	public interface OnScrollListener {
		void onBottom();

		void onTop();

		void onScroll(int direct);

		void onScrolling();

		void onScrollStop();
	}

	private OnScrollListener onScrollListener;

	public void setOnScrollListener(OnScrollListener onScrollListener) {
		this.onScrollListener = onScrollListener;
	}

	/**
	 * 触摸监听器
	 */
	OnTouchListener onTouchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_UP) {
				handler.sendMessage(handler.obtainMessage(3));
			}
			if (event.getAction() == MotionEvent.ACTION_MOVE) {
				handler.sendMessage(handler.obtainMessage(4));
			}
			return false;
		}
	};

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		/**
		 * 此处是关键，onTouchEvent是ScrollView的手指动作处理事件
		 * 在这里面捕获事件后传递给GestureDector的onTouchEvent来处理 从而实现onFling, onScroll等事件的处理
		 */
		mGestureDetector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}

	// ////////////////////// 以下为 onGestureListener 的方法 //////////////////////

	/**
	 * 滑动中
	 */
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		Log.i("TEST", "onFling:velocityX = " + velocityX + " velocityX = " + velocityY);
		if (velocityY < 0) { // 手指向上，屏幕向上滑动
			handler.sendMessage(handler.obtainMessage(1));
		} else if (velocityY > 0) { // 手指向下，屏幕向下滑动
			handler.sendMessage(handler.obtainMessage(2));
		}
		return false;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

}
