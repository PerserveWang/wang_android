package com.trends.photoalbum.waterfalltool;

//来自：http://blog.csdn.net/listening_music/article/details/7192629
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ScrollView;

public class LazyScrollView extends ScrollView implements android.widget.AbsListView.OnScrollListener {
	@SuppressWarnings("unused")
	private static final String tag = "LazyScrollView";
	private Handler handler;
	private View view;

	public LazyScrollView(Context context) {
		super(context);
	}

	public LazyScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public LazyScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	// 这个获得总的高度
	@Override
	public int computeVerticalScrollRange() {
		return super.computeHorizontalScrollRange();
	}

	@Override
	public int computeVerticalScrollOffset() {
		return super.computeVerticalScrollOffset();
	}

	@SuppressLint("HandlerLeak")
	private void init() {

		this.setOnTouchListener(onTouchListener);
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// process incoming messages here
				super.handleMessage(msg);
				switch (msg.what) {
					case 1:
						if (view.getMeasuredHeight() <= getScrollY() + getHeight()) {
							if (onScrollListener != null) {
								onScrollListener.onBottom();
							}

						} else if (getScrollY() == 0) {
							if (onScrollListener != null) {
								onScrollListener.onTop();
							}
						} else {
							if (onScrollListener != null) {
								onScrollListener.onScroll();
							}
						}
						break;
					case 2:
						onScrollListener.onScrolling();
						break;

				}
			}
		};

	}

	OnTouchListener onTouchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {

				case MotionEvent.ACTION_UP:
					if (view != null && onScrollListener != null) {
						handler.sendMessageDelayed(handler.obtainMessage(1), 1);
					}
					break;
				case MotionEvent.ACTION_MOVE:
					if (view != null && onScrollListener != null) {
						handler.sendMessageDelayed(handler.obtainMessage(2), 1);
					}
					break;
				default:
					break;
			}
			return false;
		}

	};

	/**
	 * 获得参考的View，主要是为了获得它的MeasuredHeight，然后和滚动条的ScrollY+getHeight作比较。
	 */
	public void getView() {
		this.view = getChildAt(0);
		if (view != null) {
			init();
		}
	}

	/**
	 * 定义接口
	 * 
	 * @author admin
	 * 
	 */
	public interface OnScrollListener {
		void onBottom();

		void onTop();

		void onScroll();

		void onScrolling();
	}

	private OnScrollListener onScrollListener;

	public void setOnScrollListener(OnScrollListener onScrollListener) {
		this.onScrollListener = onScrollListener;
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (scrollState == SCROLL_STATE_FLING) {
			handler.sendMessage(handler.obtainMessage(3));
		}
		if (scrollState == SCROLL_STATE_IDLE) {
			handler.sendMessage(handler.obtainMessage(4));
		}
	}

}
