package com.trends.photoalbum.activity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.trends.photoalbum.fragment.ShowPhotoViewItemFragment;
import com.trends.photoalbum.msg.CommonPath;
import com.trends.photoalbum.msg.Constants;
import com.trends.photoalbum.utils.HttpC;
import com.trends.photoalbum.utils.InternalStorage;
import com.trends.photoalbum.utils.SDCardTools;
import com.trends.photoalbum.R;

/**
 * 展示ViewPager
 * 
 * @author hhn
 * 
 */
public class PictureShowActivity extends FragmentActivity {

	private ViewPager viewPager;
	private LinearLayout bottonBar;
	private FragmentManager manager;
	private MyFragmentPagerAdapter adapter;
	private AssetManager assetManager;
	private ImageView play;
	private ImageView share;
	private int currentPosition;
	private List<String> image_filenames;
	private View mContentView;
	private boolean slidingFlag;
	private String mPictureinfo;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		LayoutInflater inflater = LayoutInflater.from(this);
		mContentView = inflater.inflate(R.layout.picture_viewpagaer, null);
		setContentView(mContentView);
		viewPager = (ViewPager) findViewById(R.id.picture_viewPager_id);
		play = (ImageView) findViewById(R.id.pic_item_play_iv_id);
		share = (ImageView) findViewById(R.id.pic_item_share_iv_id);
		bottonBar = (LinearLayout) findViewById(R.id.pic_viewpager_bottom_bar);
		manager = getSupportFragmentManager();
		currentPosition = getIntent().getIntExtra("position", 0);
		initData();
		MyImageViewOnClickListerner onClick = new MyImageViewOnClickListerner();
		play.setOnClickListener(onClick);
		share.setOnClickListener(onClick);
		viewPager.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_MOVE:
						// if (!slidingFlag) {
						bottonBar.setVisibility(View.GONE);
						// mPopWindow.dismiss();
						// }
						break;
					case MotionEvent.ACTION_UP:
						// Toast.LENGTH_SHORT).show();
						// if (!slidingFlag) {
						bottonBar.setVisibility(View.VISIBLE);
						bottonBar.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bottom_alpha_translate));
						// }
						break;
					default:
						break;
				}
				return slidingFlag;
			}
		});

	}

	private class MyImageViewOnClickListerner implements OnClickListener {
		public class Picture_info implements Runnable {
			public Picture_info(Context context, String string) {
			}

			@Override
			public void run() {
				String url = Constants.SERVERS_PORT + "action=Statistics" + "&itemGuid=" + Constants.KBGUID + "" + "&type=-1&action=2&source=2";
				mPictureinfo = HttpC.HttpA(url);
				try {
					mPictureinfo = new String(mPictureinfo.getBytes(), "gb2312");
					handler_mPictureinfo.sendEmptyMessage(1);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}

		@SuppressLint("HandlerLeak")
		private Handler handler_mPictureinfo = new Handler() {
			@Override
			public void handleMessage(android.os.Message msg) {
			};
		};

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.pic_item_play_iv_id:
					Intent intent = new Intent(getApplicationContext(), DisplayImagesActivity.class);
					intent.putExtra("position", viewPager.getCurrentItem());
					startActivity(intent);
					break;
				case R.id.pic_item_share_iv_id:
					new Thread(new Picture_info(PictureShowActivity.this, mPictureinfo)).start();
					String picName = image_filenames.get(viewPager.getCurrentItem());
					String sharePath = "";
					if (SDCardTools.judgeFolderIsExist()) {
						InputStream inputStream = null;
						try {
							inputStream = assetManager.open(CommonPath.IMAGESPATH + File.separator + image_filenames.get(viewPager.getCurrentItem()));
						} catch (IOException e) {
							e.printStackTrace();
						}
						sharePath = SDCardTools.savaToSDCard(inputStream, picName);
						System.out.println(sharePath);
					} else {
						System.out.println("SDCrad不存在");
						InternalStorage inStorage = new InternalStorage(getApplicationContext());
						InputStream inputStream = null;
						try {
							inputStream = assetManager.open(CommonPath.IMAGESPATH + File.separator + image_filenames.get(viewPager.getCurrentItem()));
						} catch (IOException e) {
							e.printStackTrace();
						}
						sharePath = inStorage.saveToInternalStorage(inputStream, picName);
					}
					share(sharePath);
					break;

			}
		}
	}

	/**
	 * 分享图片出去
	 * 
	 * @param file
	 */
	private void share(String fileName) {
		File file = new File(fileName);
		if (file.exists()) {
			Intent shareIntent = new Intent(Intent.ACTION_SEND);
			shareIntent.setType("image/*");
			shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
			shareIntent.putExtra(Intent.EXTRA_TEXT, "来自" + getTitle());
			startActivity(Intent.createChooser(shareIntent, getTitle()));
		}

	}

	/**
	 * 初始化数据
	 */
	public void initData() {
		assetManager = this.getAssets();
		try {
			image_filenames = Arrays.asList(assetManager.list(CommonPath.IMAGESPATH));
			adapter = new MyFragmentPagerAdapter(manager);
			adapter.addData(image_filenames);
			viewPager.setAdapter(adapter);
			viewPager.setCurrentItem(currentPosition);
			adapter.notifyDataSetChanged();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private class MyFragmentPagerAdapter extends FragmentStatePagerAdapter {

		private List<String> imageList = new ArrayList<String>();

		public void addData(List<String> data) {
			this.imageList = data;
		}

		public MyFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			ShowPhotoViewItemFragment fragment = new ShowPhotoViewItemFragment();
			Bundle bundle = new Bundle();
			bundle.putString("filename", imageList.get(position));
			fragment.setArguments(bundle);
			return fragment;
		}

		@Override
		public int getCount() {
			return imageList.size();
		}

	}

}
