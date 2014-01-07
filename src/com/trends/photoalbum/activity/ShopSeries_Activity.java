package com.trends.photoalbum.activity;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.wiz.sdk.api.WizObject.WizDocument;
import cn.wiz.sdk.db.WizDatabase;
import cn.wiz.sdk.util.WizMisc;

import com.trends.photoalbum.R;
import com.trends.photoalbum.R.color;
import com.trends.photoalbum.msg.Constants;
import com.trends.photoalbum.utils.DocumentListAdapter_SERIES;
import com.trends.photoalbum.utils.SystemApplication;

public class ShopSeries_Activity extends Activity {
	private ListView shop_seeries_lv;
	private String mTagGuid;
	private DocumentListAdapter_SERIES mListAdapter;
//	private RefreshableView refreshableView; 
	@SuppressLint("ShowToast")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shop_series);
//		refreshableView = (RefreshableView) findViewById(R.id.refreshable_view);
		shop_seeries_lv = (ListView) findViewById(R.id.shop_seeries_lv);
		View view1 = this.getLayoutInflater().inflate(R.layout.article_style,
				null);
		TextView article_title_tv = (TextView) view1
				.findViewById(R.id.article_title_tv);
		article_title_tv.setTextColor(color.opustitle);
		SystemApplication.getInstance().addActivity(this);
		if (!WizMisc.isNetworkAvailable(this)) {
			Toast.makeText(ShopSeries_Activity.this, "网络异常，请检查您的网络", 1).show();
		}
		initValue();
		shop_seeries_lv.setAdapter(mListAdapter);
		shop_seeries_lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				ArrayList<String> guidList = new ArrayList<String>();
				for (WizDocument doc : getDocumentsList()) {
					guidList.add(doc.guid);
				}
				// Article_Activity.startForResult(ShopAbout_Activity.this,
				// guidList,
				// position, getTitleText(), mTagGuid);
				Article_Activity.startForResult(ShopSeries_Activity.this,
						guidList, position, Constants.ALBUM_OPUS, mTagGuid);
			}
		});
		shop_seeries_lv.setDivider(null);
//		refreshableView.setOnRefreshListener(new PullToRefreshListener() {  
//            @Override  
//            public void onRefresh() {  
//                try {  
//                    Thread.sleep(3000);  
//                } catch (InterruptedException e) {  
//                    e.printStackTrace();  
//                }  
//                refreshableView.finishRefreshing();  
//            }  
//        }, 0);  
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == Article_Activity.ACTIVITYID) {
			mListAdapter.notifyDataSetChanged();
		}
	}

	private void initValue() {
		// mTagGuid = getTagId();
		mTagGuid = Constants.ALBUM_SERIES_WIZTAG;
		mListAdapter = new DocumentListAdapter_SERIES(this, getDocumentsList(),
				mTagGuid);
	}

	private ArrayList<WizDocument> getDocumentsList() {
		// String tagId = getTagId();
		String tagId = Constants.ALBUM_SERIES_WIZTAG;
		WizDatabase dataBase = WizDatabase.getDb(this,
				Constants.DEFAULT_USERID, Constants.KBGUID);
		ArrayList<WizDocument> docLists = dataBase.getDocumentsByTag(tagId,
				true);
		return docLists;
	}

	// private String getTagId() {
	// Intent intent = getIntent();
	// mTagGuid = intent.getStringExtra("tagId");
	//
	// return mTagGuid;
	// }
	//
	// private String getTitleText() {
	// Intent intent = getIntent();
	// String titile = intent.getStringExtra("title");
	// return titile;
	// }
	//
	// public static void start(Activity activity, String tagId, String title) {
	// Intent intent = new Intent(activity, ShopAbout_Activity.class);
	// intent.putExtra("tagId", tagId);
	// intent.putExtra("title", title);
	// activity.startActivity(intent);
	// }
}
