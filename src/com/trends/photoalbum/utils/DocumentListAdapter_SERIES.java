package com.trends.photoalbum.utils;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.wiz.sdk.api.WizDocumentAbstractCache;
import cn.wiz.sdk.api.WizObject.WizAbstract;
import cn.wiz.sdk.api.WizObject.WizDocument;
import cn.wiz.sdk.db.WizDatabase;

import com.trends.photoalbum.msg.Constants;
import com.trends.photoalbum.R;

@SuppressWarnings("unused")
public class DocumentListAdapter_SERIES extends BaseAdapter {

	private Context mContext;
	private ArrayList<WizDocument> mDocList;
	private ArrayList<HashMap<String, String>> mFavorList;
	private ArrayList<String> mReadList;
	private LayoutInflater mInflater;
	private String mTagGuid;
	private WizDatabase mDb;
	private int COUNT = 50;
	private int start = 0;
	private int end = 0;

	public DocumentListAdapter_SERIES(Context context,
			ArrayList<WizDocument> docList, String tagGuid) {

		mContext = context;
		initilize();
		mDocList = docList;
		mTagGuid = tagGuid;
		mDb = WizDatabase.getDb(mContext, Constants.DEFAULT_USERID,
				Constants.KBGUID);

		if (mDocList.size() > COUNT) {
			end = COUNT;
		} else {
			end = mDocList.size();
		}
	}

	private void initilize() {
		mInflater = LayoutInflater.from(mContext);
		mDocList = new ArrayList<WizDocument>();
		mFavorList = new ArrayList<HashMap<String, String>>();
		mReadList = new ArrayList<String>();
	}

	// private void getFavorCountByCut(){
	// if (end == mDocList.size()){
	// return;
	// }
	// start = end;
	// end += COUNT;
	// if (end > mDocList.size()) {
	// end = mDocList.size();
	// }
	// }
	public void updateReadList(int position) {
		mReadList.set(position, "1");
		String guid = mDocList.get(position).guid;
		String sql = "UPDATE WIZ_DOCUMENT SET ISREAD = 1 WHERE DOCUMENT_GUID = '"
				+ guid + "'";
		mDb.execSql(sql);
	}

	/**
	 * 刷新adapter
	 */
	public void refresh() {
		mDocList = mDb.getDocumentsByTag(mTagGuid);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mDocList.size();
	}

	@Override
	public Object getItem(int position) {
		return mDocList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	private ViewHolder mViewHolder;

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.series_style, null);
			mViewHolder = new ViewHolder();
			mViewHolder.mImgView = (ImageView) convertView
					.findViewById(R.id.article_bm);
			mViewHolder.mAbstractView = (TextView) convertView
					.findViewById(R.id.article_abstract);
			mViewHolder.mTitleView = (TextView) convertView
					.findViewById(R.id.article_title_tv);

			convertView.setTag(mViewHolder);
		} else {
			mViewHolder = (ViewHolder) convertView.getTag();
		}

		WizDocument currentDoc = mDocList.get(position);
		if (currentDoc == null) {
			return null;
		}
		setData(currentDoc);

		return convertView;
	}

	private void setData(WizDocument doc) {

		String[] title = new String[2];
		getStrSeqs(doc.title, "@");
		title[0] = getStrSeqs(doc.title, "@")[0];
		if (getStrSeqs(doc.title, "@").length > 1) {
			title[1] = getStrSeqs(doc.title, "@")[1];
		} else {
			title[1] = "";
		}

		if (title.length >= 1) {
			if (getReadList().contains(doc.guid)) {
			} else {
			}
			mViewHolder.mTitleView.setText("" + title[0]);
		}
		if (title.length >= 2) {
			mViewHolder.mAbstractView.setText(title[1]);
		}
		// int end = doc.dateModified.lastIndexOf(" ");
		Bitmap bmp = getBitmap(doc.guid);
		mViewHolder.mImgView.setImageBitmap(bmp);
	}

	private Bitmap getBitmap(String docGuid) {
		WizAbstract abs = WizDocumentAbstractCache.getAbstractDirect(mContext,
				Constants.DEFAULT_USERID, docGuid);
		if (abs != null) {
			if (abs.abstractImage != null) {
				return abs.abstractImage;
			}
		}
		return null;
	}

	// private String getFavorCount(String docGuid) {
	// HashMap<String, String> map = null;
	// String topicid = null;
	// for (int j = 0; j < mFavorList.size(); j++) {
	// map = mFavorList.get(j);
	// topicid = map.get("topicid");
	// if (docGuid.equals(topicid)) {
	// int length = topicid.length();
	// int realLike = Integer.parseInt(map.get("likeCount"));
	// int virturlLike = Integer.parseInt(
	// topicid.substring(length - 2, length - 1), 16) * 16
	// + Integer.parseInt(topicid.substring(length - 1, length), 16);
	// return realLike + virturlLike + "";
	// }
	// }
	// return "0";
	// }

	public String[] getStrSeqs(String strSeq, String charSeq) {
		String strSeqs[] = strSeq.split(charSeq);
		return strSeqs;
	}

	private ArrayList<String> getReadList() {
		WizDatabase db = WizDatabase.getDb(mContext, Constants.DEFAULT_USERID,
				Constants.KBGUID);
		String sql = "SELECT DOCUMENT_GUID FROM WIZ_DOCUMENT WHERE ISREAD=1";
		ArrayList<String> readList = db.sqlToStringArray(sql, 0);
		return readList;
	}

	class ViewHolder {
		ImageView mImgView;
		TextView mTitleView;
		TextView mAbstractView;
		ImageView mFavourView;
	}
}
