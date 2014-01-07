package com.trends.photoalbum.activity;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import cn.wiz.sdk.api.WizObject.WizDocument;
import cn.wiz.sdk.db.WizDatabase;
import cn.wiz.sdk.util.WizMisc;

import com.trends.photoalbum.activity.RefreshableView.PullToRefreshListener;
import com.trends.photoalbum.msg.Constants;
import com.trends.photoalbum.utils.DocumentListAdapter;
import com.trends.photoalbum.utils.HttpC;
import com.trends.photoalbum.utils.SystemApplication;
import com.trends.photoalbum.R;

public class New_Activity extends Activity {
	private ListView new_activity_lv;
	private String mTagGuid;
	private Button new_activity_back, new_activity_tel;
	private DocumentListAdapter mListAdapter;
	private RefreshableView refreshableView; 
	private String mBoradinfo;
	@SuppressLint("ShowToast")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_activity);
		refreshableView = (RefreshableView) findViewById(R.id.refreshable_view);
		new_activity_tel = (Button) findViewById(R.id.new_activity_tel);
		new Thread(new Board_info(New_Activity.this, mBoradinfo)).start();
		new_activity_tel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String str = (String) getText(R.string.call_tel);
				Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
						+ str));
				startActivity(intent);

			}
		});
		new_activity_back = (Button) findViewById(R.id.new_activity_back);
		new_activity_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		new_activity_lv = (ListView) findViewById(R.id.new_activity_lv);
		SystemApplication.getInstance().addActivity(this);
		if (!WizMisc.isNetworkAvailable(this)) {
			Toast.makeText(New_Activity.this, "网络异常，请检查您的网络", 1).show();
		}
		refreshableView.setOnRefreshListener(new PullToRefreshListener() {  
            @Override  
            public void onRefresh() {  
                try {  
                    Thread.sleep(3000);  
                } catch (InterruptedException e) {  
                    e.printStackTrace();  
                }  
                refreshableView.finishRefreshing();  
            }  
        }, 0);  
		initValue();
		new_activity_lv.setAdapter(mListAdapter);
		new_activity_lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				ArrayList<String> guidList = new ArrayList<String>();
				for (WizDocument doc : getDocumentsList()) {
					guidList.add(doc.guid);
				}
				Article_Activity.startForResult(New_Activity.this, guidList,
						position, getTitleText(), mTagGuid);
			}
		});
		new_activity_lv.setDivider(null);
	}
//	action=AddBoard&albumGuid=908A2DBC-2E95-4594-BF20-BCF4AC0F3A00
//	&boardGuid=3D4BDB33-680F-400F-BD98-BB99E5AACCA8
//			&boardName=最新活动&boardType=1 
	public class Board_info implements Runnable {
		public Board_info(Context context, String string) {
		}

		@Override
		public void run() {
			String url = Constants.SERVERS_PORT + "action=AddBoard" +
					"&albumGuid="+Constants.KBGUID+""+
					"&boardGuid="+Constants.NEW_ACTIVITY_WIZTAG+"" +
					"&boardName="+Constants.NEW_ACTIVITY+"&boardType=1";
			mBoradinfo = HttpC.HttpA(url);
			try {
				mBoradinfo = new String(mBoradinfo.getBytes(), "gb2312");
				handler_Boradinfo.sendEmptyMessage(1);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
	}

	private Handler handler_Boradinfo = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
		};
	};
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == Article_Activity.ACTIVITYID) {
			mListAdapter.notifyDataSetChanged();
		}
	}

	private void initValue() {
		mTagGuid = getTagId();
		mListAdapter = new DocumentListAdapter(this, getDocumentsList(),
				mTagGuid);
	}

	private ArrayList<WizDocument> getDocumentsList() {
		String tagId = getTagId();
		WizDatabase dataBase = WizDatabase.getDb(this,
				Constants.DEFAULT_USERID, Constants.KBGUID);
		ArrayList<WizDocument> docLists = dataBase.getDocumentsByTag(tagId,
				true);
		return docLists;
	}

	private String getTagId() {
		Intent intent = getIntent();
		mTagGuid = intent.getStringExtra("tagId");

		return mTagGuid;
	}

	private String getTitleText() {
		Intent intent = getIntent();
		String titile = intent.getStringExtra("title");
		return titile;
	}

	public static void start(Activity activity, String tagId, String title) {
		Intent intent = new Intent(activity, New_Activity.class);
		intent.putExtra("tagId", tagId);
		intent.putExtra("title", title);
		activity.startActivity(intent);
	}
}
