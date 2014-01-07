package com.trends.photoalbum.fragment;

import java.io.UnsupportedEncodingException;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.trends.photoalbum.activity.Gread_Activity;
import com.trends.photoalbum.activity.New_Activity;
import com.trends.photoalbum.activity.PowerByActivity;
import com.trends.photoalbum.activity.Recommend_Activity;
import com.trends.photoalbum.activity.TabHost;
import com.trends.photoalbum.msg.Constants;
import com.trends.photoalbum.utils.HttpC;
import com.trends.photoalbum.R;

public class LeftFragment extends Fragment implements OnClickListener {

	private int id;
	private ImageButton new_activity, shop_about, recommend, share, gread, call;
	private String mAddinfo;
	private TextView logoWeb;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.left, null);
		// TextView panker_tv = (TextView) view.findViewById(R.id.panker_tv);
		new_activity = (ImageButton) view.findViewById(R.id.new_activity);
		shop_about = (ImageButton) view.findViewById(R.id.shop_about);
		recommend = (ImageButton) view.findViewById(R.id.recommend);
		share = (ImageButton) view.findViewById(R.id.share);
		gread = (ImageButton) view.findViewById(R.id.gread);
		call = (ImageButton) view.findViewById(R.id.call);
		logoWeb = (TextView) view.findViewById(R.id.logo_web_tv_id);
		new_activity.setOnClickListener(this);
		shop_about.setOnClickListener(this);
		// panker_tv.setOnClickListener(this);
		logoWeb.setOnClickListener(this);
		recommend.setOnClickListener(this);
		share.setOnClickListener(this);
		gread.setOnClickListener(this);
		call.setOnClickListener(this);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	private void call(String str) {
		try {
			Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + str));
			startActivity(intent);
		} catch (Exception e) {
		}

	}

	@Override
	public void onClick(View v) {
		id = v.getId();
		Intent inten = new Intent();
		switch (id) {
			case R.id.logo_web_tv_id:
				Intent webIntent = new Intent();
				webIntent.setAction("android.intent.action.VIEW");
				Uri content_url = Uri.parse(getString(R.string.helanduo_web_url));
				webIntent.setData(content_url);
				startActivity(webIntent);
				break;
			// case R.id.panker_tv:
			// inten.setClass(LeftFragment.this.getActivity(),
			// PowerByActivity.class);
			// startActivity(inten);
			// // String pankers =(String) getText(R.string.call_panker);
			// // call(pankers);
			// break;
			case R.id.new_activity:
				New_Activity.start(LeftFragment.this.getActivity(), Constants.NEW_ACTIVITY_WIZTAG, Constants.NEW_ACTIVITY);
				break;
			case R.id.shop_about:
				inten.setClass(LeftFragment.this.getActivity(), TabHost.class);
				startActivity(inten);
				break;
			case R.id.recommend:
				inten.setClass(LeftFragment.this.getActivity(), Recommend_Activity.class);
				startActivity(inten);
				break;
			case R.id.share:
				new Thread(new Add_info(LeftFragment.this.getActivity(), mAddinfo)).start();
				Intent intent = new Intent(Intent.ACTION_SEND);
				intent.setType("text/plain");
				String text = getString(R.string.invate);
				String share_app = getString(R.string.share_app);
				intent.putExtra(Intent.EXTRA_TEXT, share_app);
				startActivity(Intent.createChooser(intent, text));
				break;
			case R.id.gread:
				inten.setClass(LeftFragment.this.getActivity(), Gread_Activity.class);
				startActivity(inten);
				break;
			case R.id.call:
				String str = (String) getText(R.string.call_tel);
				call(str);
				break;

			default:
				break;
		}
	}

	public class Add_info implements Runnable {
		public Add_info(Context context, String string) {
		}

		@Override
		public void run() {
			String url = Constants.SERVERS_PORT + "action=Statistics" + "&itemGuid=" + Constants.KBGUID + "" + "&type=-1&action=3&source=2";
			mAddinfo = HttpC.HttpA(url);
			try {
				mAddinfo = new String(mAddinfo.getBytes(), "gb2312");
				handler_Addinfo.sendEmptyMessage(1);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
	}

	@SuppressLint("HandlerLeak")
	private Handler handler_Addinfo = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
		};
	};
}
