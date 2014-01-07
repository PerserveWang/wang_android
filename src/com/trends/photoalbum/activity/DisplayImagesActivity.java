package com.trends.photoalbum.activity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.trends.photoalbum.R;
import com.trends.photoalbum.msg.CommonPath;


/**
 * 播放图片类
 * 
 * @author hhn
 * 
 */
public class DisplayImagesActivity extends Activity {
	private AssetManager assets = null;
	private String[] mImages = null;
	private String[] mMusic = null;
	private int currentImg = 0;
	private ImageView image;
	// private Button btnStart;
	// private Button btnStop;
	// 定义一个负责更新图片的Handler
	private Handler handler = null;
	private Thread thread = null;
	private boolean touchFlag = false;
	private MediaPlayer mediaPlayer = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.play_picture);
		currentImg = getIntent().getIntExtra("position", 0);
		mImages = getImages();
		mMusic = getMucis();

		// 初始化视图
		onInitView();
		// 获取assets下图片

		// displayAssets();
		playMusic();
	}

	private void playMusic() {

		System.out.println("---播放assest的资源文件----");
		final AssetManager assetManager = this.getAssets();
		try {
			AssetFileDescriptor fileDescriptor = assetManager.openFd(CommonPath.MUSIC + File.separator + mMusic[0]);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(), fileDescriptor.getStartOffset(), fileDescriptor.getLength());
			mediaPlayer.prepare();
			mediaPlayer.start();
			mediaPlayer.setLooping(true);
			// mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
			// @Override
			// public void onCompletion(MediaPlayer mp) {
			// try {
			//
			// } catch (IllegalStateException e) {
			// e.printStackTrace();
			// } catch (IOException e) {
			// e.printStackTrace();
			// }
			//
			// }
			// });
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mediaPlayer.stop();
	}

	@SuppressLint("HandlerLeak")
	private void onInitView() {
		image = (ImageView) findViewById(R.id.image);
		image.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				touchFlag = true;
				dispalyAnimotion(touchFlag);
				mediaPlayer.stop();
				finish();
			}
		});

		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// 表明消息是该程序发出的
				if (msg.what == 0x110) {
					// 展示下一张图片
					dispalyNextImage();
				}
			};
		};
		dispalyAnimotion(touchFlag);
	}

	private String[] getImages() {
		String[] tempImages = null;
		try {
			assets = getAssets();
			// 获取/assets/目录下所有文件
			if (null != assets) {
				tempImages = assets.list(CommonPath.IMAGESPATH);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return tempImages;
	}

	private String[] getMucis() {
		String[] tempMusic = null;
		try {
			assets = getAssets();
			// 获取/assets/目录下所有文件
			if (null != assets) {
				tempMusic = assets.list(CommonPath.MUSIC);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return tempMusic;
	}

	// 展示下一张图片
	private void dispalyNextImage() {
		// 如果发生数组越界
		if (currentImg >= mImages.length) {
			currentImg = 0;
		}
		// 如果有不是图片的东西 干掉 currentImg+1 找到下一个图片文件
		while (!mImages[currentImg].endsWith(".png") && !mImages[currentImg].endsWith(".jpg") && !mImages[currentImg].endsWith(".gif")) {
			currentImg++;
			// 如果已发生数组越界
			if (currentImg >= mImages.length) {
				currentImg = 0;
			}
		}

		InputStream assetFile = null;
		try {
			// 打开指定资源对应的输入流
			assetFile = assets.open(CommonPath.IMAGESPATH + File.separator + mImages[currentImg++]);
		} catch (IOException e) {
			e.printStackTrace();
		}

		BitmapDrawable bitmapDrawable = (BitmapDrawable) image.getDrawable();

		// 改变ImageView显示的图片
		image.setImageBitmap(BitmapFactory.decodeStream(assetFile));
		// 如果图片还未回收，先强制回收该图片
		if (bitmapDrawable != null && !bitmapDrawable.getBitmap().isRecycled()) {
			bitmapDrawable.getBitmap().recycle();
		}
		// new Handler().postDelayed(new Runnable() {
		// public void run() {
		//
		// }
		// }, 8000);
		// 设置动画
		image.startAnimation(getAnimation());
		// image.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
		// R.anim.alpha));

	}

	/**
	 * 播放图片动画线程
	 * 
	 * @param flag
	 *            是否播放标志位
	 */
	public void dispalyAnimotion(boolean flag) {
		if (!flag) {
			if (thread == null) {
				thread = new Thread() {
					@Override
					public void run() {
						Thread curThread = Thread.currentThread();
						while (thread != null && thread == curThread) {
							try {
								Message msg = Message.obtain();
								msg.what = 0x110;
								handler.sendMessage(msg);
								Thread.sleep(7000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				};
				thread.start();
			}
		} else if (flag) {
			if (thread == null) {
				thread = new Thread();
			}
			Thread temp = thread;
			thread = null;
			temp.interrupt();

		}

	}

	/**
	 * 获取随机动画属性
	 * 
	 * @return Animation
	 */
	public Animation getAnimation() {
		Animation animation = null;
		int random = new Random().nextInt(6);
		// 随机获得动画效果
		switch (random) {
			case 0:
				// 渐变旋转
				animation = AnimationUtils.loadAnimation(this, R.anim.alpha_rotate);
				break;
			case 1:
				// 放大
				animation = AnimationUtils.loadAnimation(this, R.anim.scale);
				break;
			case 2:
				// 渐变缩放位移
				animation = AnimationUtils.loadAnimation(this, R.anim.alpha_scale_translate);
				break;
			case 3:
				// 渐变缩放位移旋转
				animation = AnimationUtils.loadAnimation(this, R.anim.alpha_scale_translate_rotate);
				break;
			case 4:
				// 自定义 位移伸缩
				animation = AnimationUtils.loadAnimation(this, R.anim.myown_design);
				break;
			case 5:
				// 从下淡入位移
				animation = AnimationUtils.loadAnimation(this, R.anim.alpha_translate);
				break;
		}
		return animation;
	}

}
