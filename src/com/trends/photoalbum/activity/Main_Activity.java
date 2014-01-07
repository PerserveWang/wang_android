package com.trends.photoalbum.activity;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import cn.wiz.sdk.api.WizEventsCenter;
import cn.wiz.sdk.api.WizEventsCenter.WizSyncEventsListener;
import cn.wiz.sdk.api.WizLogger;
import cn.wiz.sdk.api.WizObject.WizKb;
import cn.wiz.sdk.api.WizStatusCenter;
import cn.wiz.sdk.db.WizDatabase;
import cn.wiz.sdk.db.WizDatabase.TableColumnType;
import cn.wiz.sdk.settings.WizSystemSettings;
import cn.wiz.sdk.util.WizMisc;

import com.trends.photoalbum.fragment.LeftFragment;
import com.trends.photoalbum.fragment.ViewPageFragment;
import com.trends.photoalbum.fragment.ViewPageFragment.MyPageChangeListener;
import com.trends.photoalbum.msg.Constants;
import com.trends.photoalbum.utils.HttpC;
import com.trends.photoalbum.utils.LocalMisc;
import com.trends.photoalbum.view.SlidingMenu;
import com.trends.photoalbum.R;

public class Main_Activity extends FragmentActivity implements
		WizSyncEventsListener {
	SlidingMenu mSlidingMenu;
	LeftFragment leftFragment;
	ViewPageFragment viewPageFragment;
	private String Abluminfo;
	private String Ablumuser;
	public static boolean isExit = false;
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.main);
		cn.wiz.sdk.settings.WizSystemSettings.setServerAddress(
				Main_Activity.this, Constants.SERVERS_URL);
		init();
		new Thread(new Ablum_info(Main_Activity.this, Abluminfo)).start();
		new Thread(new Ablum_userinfo(Main_Activity.this, Ablumuser)).start();
		initListener();
		if (isFirstRun()) {
			WizSystemSettings.setGroupDownLoadDataType(this,
					WizSystemSettings.DownloadDataType.DOWNLOAD_NULL);
			increaseColumes();
			setIsFirstRun(false);
		}
		if (WizMisc.isNetworkAvailable(this)) {
			WizEventsCenter.addSyncListener(this);
			WizLogger.logActionOneDay(this, Constants.SHAREDPREFERENCES_VALUE);
			WizStatusCenter.startAllThreads(this, Constants.DEFAULT_USERID,
					Constants.DEFAULT_PASSWORD);
		}
	}

	private void init() {
		mSlidingMenu = (SlidingMenu) findViewById(R.id.slidingMenu);
		mSlidingMenu.setLeftView(getLayoutInflater().inflate(
				R.layout.left_frame, null));
		mSlidingMenu.setCenterView(getLayoutInflater().inflate(
				R.layout.center_frame, null));

		FragmentTransaction t = this.getSupportFragmentManager()
				.beginTransaction();
		leftFragment = new LeftFragment();
		t.replace(R.id.left_frame, leftFragment);

		viewPageFragment = new ViewPageFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("slidingMenuId", mSlidingMenu.getId());
		viewPageFragment.setArguments(bundle);
		t.replace(R.id.center_frame, viewPageFragment);
		t.commit();
	}

	private void initListener() {
		viewPageFragment.setMyPageChangeListener(new MyPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				if (viewPageFragment.isFirst()) {
					mSlidingMenu.setCanSliding(true, false);
				} else if (viewPageFragment.isEnd()) {
					mSlidingMenu.setCanSliding(false, true);
				} else {
					mSlidingMenu.setCanSliding(false, false);
				}
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		WizEventsCenter.removeSyncListener(this);
	}

	private void increaseColumes() {
		WizDatabase dataBase = WizDatabase.getDb(this,
				Constants.DEFAULT_USERID, Constants.KBGUID);
		dataBase.addTableColumn(WizDatabase.mTableNameOfDocument,
				Constants.WIZDOCUMENT_ISREAD, TableColumnType.COLUMNTYPEINT);
		dataBase.addTableColumn(WizDatabase.mTableNameOfDocument,
				Constants.WIZDOCUMENT_ISFAVOR, TableColumnType.COLUMNTYPEINT);
	}

	public void showLeft() {
		mSlidingMenu.showLeftView();
	}

	private boolean isFirstRun() {
		SharedPreferences sharePre = LocalMisc.getSharedPreferevces(this);
		Boolean isFirstRun = sharePre.getBoolean("isFirstRun", true);
		return isFirstRun;
	}

	private void setIsFirstRun(boolean isFirstRun) {
		SharedPreferences sharePre = LocalMisc.getSharedPreferevces(this);
		Editor editor = sharePre.edit();
		editor.putBoolean("isFirstRun", isFirstRun);
		editor.commit();
	}

	@Override
	public void onSyncBegin() {

	}

	@Override
	public void onSyncEnd(boolean arg0) {
		final ArrayList<WizKb> kbs = WizDatabase.getDb(this,
				Constants.DEFAULT_USERID, "").getAllGroups();
		for (int i = 0; i < kbs.size(); i++) {
			@SuppressWarnings("unused")
			WizKb kb = kbs.get(i);
		}
	}

	@Override
	public void onSyncException(Exception arg0) {

	}

	@Override
	public void onSyncProgress(int arg0) {

	}

	@Override
	public void onSyncStatus(String arg0) {

	}
	public class Ablum_info implements Runnable {
		public Ablum_info(Context context, String string) {
		}

		@Override
		public void run() {
			String url = Constants.SERVERS_PORT + "action=AddAlbum&albumGuid="+Constants.KBGUID+"&albumName="+Constants.ALBUM+"";
			Abluminfo = HttpC.HttpA(url);
			try {
				Abluminfo = new String(Abluminfo.getBytes(), "gb2312");
				handler_Abluminfo.sendEmptyMessage(1);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private Handler handler_Abluminfo = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
		};
	};
	public class Ablum_userinfo implements Runnable {
		public Ablum_userinfo(Context context, String string) {
		}

		@Override
		public void run() {
			String AppName = (String) getText(R.string.app_name);
			String url = Constants.SERVERS_PORT + "action=AddCustomer&albumGuid="+Constants.KBGUID+"&customerName="+AppName+"&albumType=1";
			Ablumuser = HttpC.HttpA(url);
			try {
				Ablumuser = new String(Ablumuser.getBytes(), "gb2312");
				handler_Ablumuser.sendEmptyMessage(1);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private Handler handler_Ablumuser = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
		};
	};
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		@SuppressWarnings("unused")
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				isExit = false;
			}
		};
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			showDialog2();
		}
		return true;

	}
	private void showDialog2() {
		AlertDialog.Builder builder = new Builder(this);
		builder.setMessage("您想要退出吗？");
		builder.setTitle("提示");
		builder.setPositiveButton("确认", new Dialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
//				dialog.dismiss();
				Main_Activity.this.finish();
			}
		});
		builder.setNegativeButton("取消", new Dialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}
}
