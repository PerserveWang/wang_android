package com.trends.photoalbum.activity;

import java.io.UnsupportedEncodingException;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TabHost.TabSpec;

import com.trends.photoalbum.msg.Constants;
import com.trends.photoalbum.utils.HttpC;
import com.trends.photoalbum.R;

@SuppressWarnings("deprecation")
public class TabHost extends TabActivity implements OnClickListener {
	private android.widget.TabHost tabHost;
	private Button back;
	private Button tel;
	private String mOpusinfo;
	private String mSeriesinfo;

	// private TextView opus_tx;
	// private TextView series_tx;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tabhost);
		back = (Button) findViewById(R.id.tabhost_back);
		tel = (Button) findViewById(R.id.tabhost_call);
		back.setOnClickListener(this);
		tel.setOnClickListener(this);
		View view1 = this.getLayoutInflater()
				.inflate(R.layout.tab_series, null);
		View view2 = this.getLayoutInflater().inflate(R.layout.tab_opus, null);
		// opus_tx = (TextView) view2.findViewById(R.id.opus_tx);
		// series_tx = (TextView) view1.findViewById(R.id.series_tx);
		 ImageView series_img = (ImageView)view1.findViewById(R.id.series_img);
		// ImageView opus_img = (ImageView) view2.findViewById(R.id.opus_img);
		// Resources res = getResources();
		tabHost = getTabHost();
		TabSpec spec;
		Intent intent;

		// 第一个Tab
		intent = new Intent(this, ShopOpus_Activity.class);
		spec = tabHost.newTabSpec("tab1").setIndicator(view2)
				.setContent(intent);
		new Thread(new Opus_info(this, mOpusinfo)).start();
		tabHost.addTab(spec);// 添加进tabHost

		// 第二个Tab
		intent = new Intent(this, ShopSeries_Activity.class);
		spec = tabHost.newTabSpec("tab2").setIndicator(view1)
				.setContent(intent);
		new Thread(new Series_info(this, mSeriesinfo)).start();
		tabHost.addTab(spec);// 添加进tabHost

	}

	public class Opus_info implements Runnable {
		public Opus_info(Context context, String string) {
		}

		@Override
		public void run() {
			String url = Constants.SERVERS_PORT + "action=AddBoard"
					+ "&albumGuid=" + Constants.KBGUID + "" + "&boardGuid="
					+ Constants.ALBUM_OPUS_WIZTAG + "" + "&boardName="
					+ Constants.ALBUM_OPUS + "&boardType=2";
			mOpusinfo = HttpC.HttpA(url);
			try {
				mOpusinfo = new String(mOpusinfo.getBytes(), "gb2312");
				handler_Opusinfo.sendEmptyMessage(1);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
	}

	private Handler handler_Opusinfo = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
		};
	};

	public class Series_info implements Runnable {
		public Series_info(Context context, String string) {
		}

		@Override
		public void run() {
			String url = Constants.SERVERS_PORT + "action=AddBoard"
					+ "&albumGuid=" + Constants.KBGUID + "" + "&boardGuid="
					+ Constants.ALBUM_SERIES_WIZTAG + "" + "&boardName="
					+ Constants.ALBUM_SERIES + "&boardType=3";
			mSeriesinfo = HttpC.HttpA(url);
			try {
				mSeriesinfo = new String(mSeriesinfo.getBytes(), "gb2312");
				handler_Seriesinfo.sendEmptyMessage(1);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
	}

	private Handler handler_Seriesinfo = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
		};
	};

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.tabhost_back:
			finish();
			break;
		case R.id.tabhost_call:
			String str = (String) getText(R.string.call_tel);
			call(str);
			break;
		default:
			break;
		}

	}

	private void call(String str) {
		try {
			Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
					+ str));
			startActivity(intent);
		} catch (Exception e) {
		}

	}
}
