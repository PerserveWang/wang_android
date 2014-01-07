package com.trends.photoalbum.activity;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomButtonsController;
import cn.wiz.sdk.api.WizASXmlRpcServer;
import cn.wiz.sdk.api.WizAsyncAction;
import cn.wiz.sdk.api.WizAsyncAction.WizAction;
import cn.wiz.sdk.api.WizAsyncAction.WizAsyncActionThread;
import cn.wiz.sdk.api.WizAsyncAction.WizAsyncThread;
import cn.wiz.sdk.api.WizDocumentAbstractCache;
import cn.wiz.sdk.api.WizKSXmlRpcServer;
import cn.wiz.sdk.api.WizObject.WizCert;
import cn.wiz.sdk.api.WizObject.WizDataStatus;
import cn.wiz.sdk.api.WizObject.WizDocument;
import cn.wiz.sdk.api.WizObject.WizKb;
import cn.wiz.sdk.db.WizDatabase;
import cn.wiz.sdk.settings.WizAccountSettings;
import cn.wiz.sdk.settings.WizSystemSettings;
import cn.wiz.sdk.util.FileUtil;
import cn.wiz.sdk.util.ImageUtil;
import cn.wiz.sdk.util.WizMisc;
import cn.wiz.sdk.util.WizMisc.WizInvalidPasswordException;
import cn.wiz.sdk.util.ZipUtil;

import com.trends.photoalbum.R;
import com.trends.photoalbum.msg.Constants;
import com.trends.photoalbum.utils.HttpC;
import com.trends.photoalbum.utils.LocalMisc;

@SuppressWarnings("unused")
public class Article_Activity extends Activity implements OnClickListener {
	private ImageButton article_share;
	private TextView article_title;
	private ImageButton article_back;
	private WebView article_webview;
	private String mDocumentUrl;
	private View mContentView;
	private WizDatabase mDb;
	private WizDocument mDocument;
	private int mPosition = 0;
	private Boolean mDestroyed = false;
	private String mCertPassword = "";
	private String posision;
	private String tagid;
	private String title;
	private ArrayList<String> guidlist;
	private ProgressBar progressBar;
	private String mReadinfo,mShareinfo,mAddinfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = LayoutInflater.from(this);
		mContentView = inflater.inflate(R.layout.article, null);
		setContentView(mContentView);
		progressBar = (ProgressBar) findViewById(R.id.progressBar1);
		article_share = (ImageButton) findViewById(R.id.article_share);
		article_title = (TextView) findViewById(R.id.article_title);
		article_webview = (WebView) findViewById(R.id.article_webview);
		article_back = (ImageButton) findViewById(R.id.article_back);
		article_share.setOnClickListener(this);
		article_title.setOnClickListener(this);
		article_webview.setOnClickListener(this);
		article_back.setOnClickListener(this);
		Intent intent = getIntent();
		posision = intent.getStringExtra("posision");
		tagid = intent.getStringExtra("tagid");
		title = intent.getStringExtra("title");
		guidlist = intent.getStringArrayListExtra("guidlist");
		mDb = WizDatabase.getDb(this, Constants.DEFAULT_USERID,
				Constants.KBGUID);
		mPosition = getFirstPosition();
		mDocument = getCurrentDoc();
		webset();
		getTagId();
		getTitleText();
		startViewDocument();
		initTitle();
		new Thread(new Add_info(Article_Activity.this, mAddinfo)).start();
		new Thread(new Read_info(Article_Activity.this, mReadinfo)).start();
	}

	@SuppressLint("JavascriptInterface")
	public void webset() {
		WebSettings webSettings = article_webview.getSettings();
		webSettings.setAllowFileAccess(true);
		webSettings.setDefaultFontSize(30);
		if (WizSystemSettings.isAutoAdaptsScreen(this))
			webSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		article_webview.addJavascriptInterface(this, "WIZSHELL");
		article_webview.addJavascriptInterface(this, "imgOnClick");

		setZoomControlGone(Article_Activity.this, article_webview);
		webSettings.setBuiltInZoomControls(false);
		webSettings.setSupportZoom(false);
	}

	private int getFirstPosition() {
		Intent intent = getIntent();
		int position = intent.getIntExtra("position", 0);
		return position;
	}

	private WizDocument getCurrentDoc() {
		String guid = getGuidList().get(mPosition);
		WizDocument curDoc = mDb.getDocumentByGuid(guid);
		return curDoc;
	}

	private String getTitleText() {
		Intent intent = getIntent();
		return intent.getStringExtra("title");

	}

	private String getTagId() {
		Intent intent = getIntent();
		return intent.getStringExtra("tagId");
	}

	public ArrayList<String> getGuidList() {
		Intent intent = getIntent();

		ArrayList<String> guidList = intent.getStringArrayListExtra("guidList");
		System.out.println(guidList);
		return guidList;
	}

	public static void setZoomControlGone(View view) {
		Class<?> classType;
		Field field;
		try {
			classType = WebView.class;
			field = classType.getDeclaredField("mZoomButtonsController");
			field.setAccessible(true);
			ZoomButtonsController mZoomButtonsController = new ZoomButtonsController(
					view);

			mZoomButtonsController.getZoomControls().setVisibility(View.GONE);
			try {
				field.set(view, mZoomButtonsController);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
	}

	public static void setZoomControlGone(Context ctx, WebView web) {
		int sdkVersion = WizSystemSettings.getVersionSDK();
		int sdk3_0 = WizSystemSettings.androidSDKVersionOf3_0;
		if (sdkVersion > sdk3_0) {
			web.getSettings().setDisplayZoomControls(false);
		} else {
			setZoomControlGone(web);
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.article_back:
			finish();
			break;
		case R.id.article_share:
			new Thread(new Share_info(Article_Activity.this, mShareinfo)).start();
			if (mPopWindow != null && mPopWindow.isShowing()) {
				mPopWindow.dismiss();
				mPopWindow = null;
			} else {
				showPopupWindow();
			}
			break;

		default:
			break;
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
		}
		return true;
	}

	private View mPopWindowView;
	private PopupWindow mPopWindow;

	@SuppressWarnings("deprecation")
	@SuppressLint("InlinedApi")
	private void showPopupWindow() {
		LayoutInflater inflater = LayoutInflater.from(this);
		mPopWindowView = inflater.inflate(R.layout.share, null);
		mPopWindow = new PopupWindow(mPopWindowView,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT, false);

		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		int xP = (int) (metric.density * 40) + 2;
		mPopWindow.showAtLocation(mContentView, Gravity.TOP, 173, 175);
		mPopWindow.setBackgroundDrawable(new BitmapDrawable());
		mPopWindow.setOutsideTouchable(true);
		mPopWindow.setFocusable(true);
		addListenerForPop();
	}

	// 分享按钮点击事件 以及窗口的弹出
	private void addListenerForPop() {
		mPopWindowView.findViewById(R.id.weibo).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						mPopWindow.dismiss();
						onShare("com.sina.weibo");

					}
				});

		mPopWindowView.findViewById(R.id.friends).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						mPopWindow.dismiss();
						onShare("com.tencent.mm");
					}
				});

		mPopWindowView.findViewById(R.id.email).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						mPopWindow.dismiss();
						onShare("com.android.email");
					}
				});
		mPopWindowView.findViewById(R.id.qq).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						mPopWindow.dismiss();
						onShare("com.tencent.mobileqq");

					}
				});
		mPopWindowView.findViewById(R.id.more).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(Intent.ACTION_SEND);
						intent.setType("text/plain");
						String text = getString(R.string.invate);
						intent.putExtra(Intent.EXTRA_TEXT, text);
						startActivity(Intent.createChooser(intent, text));

					}
				});

	}

	private void initTitle() {
		if (mDocument.title.contains("@")) {
			int last = mDocument.title.lastIndexOf("@");
			article_title.setText(mDocument.title.subSequence(0, last));
		} else {
			article_title.setText(mDocument.title);
		}
		int end = mDocument.dateModified.lastIndexOf(" ");
		// article_data.setText(mDocument.dateModified.substring(0, end));
	}

	// 分享文章这块
	private void onShare(String packageName) {
		PackageManager mPpackageManager = getPackageManager();
		Intent intent = new Intent(Intent.ACTION_SEND, null);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.setType("image/*");
		List<ResolveInfo> mShareAppsResolveInfo = mPpackageManager
				.queryIntentActivities(intent,
						PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);
		boolean flag = false;
		for (ResolveInfo resolveInfo : mShareAppsResolveInfo) {
			ActivityInfo activityInfo = resolveInfo.activityInfo;
			if (activityInfo.packageName.contains(packageName)) {
				onBeforeShare(resolveInfo);
				flag = true;
			}
		}
		if (!flag) {
			// 用户没有安装
			if (packageName.equals("cn.wiz.note")) {
				Intent itent = new Intent(Intent.ACTION_VIEW,
						Uri.parse("http://note.wiz.cn/login"));
				itent.setClassName("com.android.browser",
						"com.android.browser.BrowserActivity");
				startActivity(itent);
			} else if (packageName.equals("com.tencent.mm")) {
				LocalMisc.showToast(this, R.string.noshare_text);
			} else if (packageName.equals("com.sina.weibo")) {
				LocalMisc.showToast(this, R.string.noshare_text);
			}
		}
	}

	private void onBeforeShare(ResolveInfo resolveInfo) {
		String bmpFile = FileUtil.getCacheRootPath(this);
		bmpFile = FileUtil.pathAddBackslash(bmpFile);
		bmpFile = bmpFile + System.currentTimeMillis() + ".jpg";

		Picture webShot = article_webview.capturePicture();
		Bitmap noteBmp = Bitmap.createBitmap(webShot.getWidth(),
				webShot.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(noteBmp);
		webShot.draw(canvas);
		ImageUtil.saveBitmap(noteBmp, bmpFile);
		try {
			noteBmp.recycle();
		} catch (Exception e) {
		}
		Intent intent = new Intent(Intent.ACTION_SEND);
		File file = new File(bmpFile);
		intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
		intent.setType("image/jpeg");
		intent.putExtra(Intent.EXTRA_TITLE, getString(R.string.shop_about));
		// intent.putExtra(Intent.EXTRA_SUBJECT, mDocument.title);
		ActivityInfo activityInfo = resolveInfo.activityInfo;
		intent.setClassName(activityInfo.packageName, activityInfo.name);
		startActivity(intent);
	}

	Handler mHandler = new Handler();
	Runnable mRunnableUI = new Runnable() {
		@Override
		public void run() {
		}

	};

	private void startViewDocument() {

		WizAsyncAction.startAsyncAction(null, new WizAction() {

			@Override
			public Object work(WizAsyncActionThread thread, Object actionData)
					throws Exception {
				for (int i = 0; i < 2; i++) {
					// 判断数据是否已下载
					if (WizDataStatus.DOWNLOADDATA == mDocument
							.getDocumentStatus(Article_Activity.this,
									Constants.DEFAULT_USERID)) {
						if (!WizMisc.isNetworkAvailable(Article_Activity.this)) {
							// 网络不可用
							mHandler.post(mRunnableUI);
						} else {
							// mHandler.post(mRunProgressBar);
							downloadData();
						}
					}

					if (mDestroyed)
						return Boolean.valueOf(false);

					if (WizDataStatus.DECRYPTIONDATA == mDocument
							.getDocumentStatus(Article_Activity.this,
									Constants.DEFAULT_USERID)) {
						if (!decryptData(thread)) {
							return Boolean.valueOf(false);
						}
					} else {
						if (WizDataStatus.UNZIPDATA == mDocument
								.getDocumentStatus(Article_Activity.this,
										Constants.DEFAULT_USERID)) {
							if (!unzipData()) {
								String fileName = mDocument.getZipFileName(
										Article_Activity.this,
										Constants.DEFAULT_USERID);
								FileUtil.deleteFile(fileName);
								continue;
							}
						}
					}

					if (WizDataStatus.VIEWDATA != mDocument.getDocumentStatus(
							Article_Activity.this, Constants.DEFAULT_USERID)) {
						throw new Exception(
								getString(R.string.prompt_network_not_available));
					}

					String documentFile = mDocument
							.getNoteFileName(Article_Activity.this);
					FileUtil.reSaveFileToUTF8(documentFile);
					return Boolean.valueOf(true);
				}
				return Boolean.valueOf(true);
			}

			@Override
			public void onStatus(Object actionData, String status, int arg1,
					int arg2, Object obj) {
				if (mDestroyed)
					return;
			}

			@Override
			public void onException(Object actionData, Exception e) {
				if (mDestroyed)
					return;
			}

			@Override
			public void onEnd(Object actionData, Object ret) {
				if (mDestroyed)
					return;
				Boolean b = (Boolean) ret;
				if (b.booleanValue()) {
					initTitle();
					viewDocument();
					// if (getReadStateFromLocal() != 1) {
					// //将该文章在数据库标记为已读
					// uodateReadState();
					// updateReadStateToServer();
					// }
				}
			}

			@Override
			public void onBegin(Object actionData) {
				if (mDestroyed)
					return;
			}

			// 下载data
			private void downloadData() throws Exception {

				WizKb kb = mDb.getKbByGuid(Constants.KBGUID);
				String Token = WizASXmlRpcServer.getToken(
						Article_Activity.this, Constants.DEFAULT_USERID,
						Constants.DEFAULT_PASSWORD);
				WizKSXmlRpcServer ks = new WizKSXmlRpcServer(
						Article_Activity.this, kb.kbDatabaseUrl,
						Constants.DEFAULT_USERID, Token, Constants.KBGUID);
				mDb.onBeforeDownloadDocument(mDocument);
				File destFile = mDocument.getZipFile(Article_Activity.this,
						Constants.DEFAULT_USERID);
				ks.downloadDocument(mDocument.guid, destFile);
				mDb.onDocumentDownloaded(mDocument);
				WizDocumentAbstractCache.forceUpdateAbstract(
						Constants.DEFAULT_USERID, mDocument.guid);
			}

			private boolean decryptData(WizAsyncThread thread) throws Exception {
				WizCert cert = mDb.getCert();
				if (TextUtils.isEmpty(cert.e)
						|| TextUtils.isEmpty(cert.encryptedD)
						|| TextUtils.isEmpty(cert.n)) {
					WizASXmlRpcServer as = new WizASXmlRpcServer(
							Article_Activity.this, Constants.DEFAULT_USERID);
					cert = as.getCert(Constants.DEFAULT_PASSWORD);
					mDb.saveCert(cert);
				}

				while (true) {
					if (TextUtils.isEmpty(mCertPassword)) {
						String hint = cert.hint;
						if (hint == null) {
							hint = "";
						}
						// synchronized (thread)
						//
						if (TextUtils.isEmpty(mCertPassword)) {
							return false;
						}
						try {
							if (WizMisc.decryptAndUnzipDocument(
									Article_Activity.this,
									Constants.DEFAULT_USERID, mDocument,
									mCertPassword, cert))
								return true;
							//
							throw new Exception(
									Article_Activity.this
											.getString(R.string.error_message_file_not_fount));
						} catch (WizInvalidPasswordException e) {
							WizAccountSettings.setCertPassword("");
							mCertPassword = "";
						}
					}
				}
			}

			private boolean unzipData() {
				try {
					return ZipUtil.unZipData(mDocument.getZipFileName(
							Article_Activity.this, Constants.DEFAULT_USERID),
							mDocument.getNotePatth(Article_Activity.this), "");
				} catch (Exception e) {
					return false;
				}
			}
		});

	}

	@SuppressLint({ "SetJavaScriptEnabled", "ShowToast", "JavascriptInterface" })
	private void viewDocument() {
		// mProgressBar.setVisibility(View.GONE);
		progressBar.setVisibility(View.GONE);
		String documentFile;
		try {
			documentFile = mDocument.getNoteFileName(this);
			mDocumentUrl = "file://" + documentFile;
			WebSettings settings = article_webview.getSettings();
			settings.setJavaScriptEnabled(true);
			article_webview.addJavascriptInterface(
					new DemoJavaScriptInterface(), "demo");

			article_webview.loadUrl(mDocumentUrl);
		} catch (Exception e) {
			// mProgressBar.setVisibility(View.GONE);
			Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT);
		}
	}

	final class DemoJavaScriptInterface {
		DemoJavaScriptInterface() {
		}

		public void getLength() {

		}
	}

	@Override
	protected void onDestroy() {
		mDestroyed = true;
		super.onDestroy();
	}
	public class Add_info implements Runnable {
		public Add_info(Context context, String string) {
		}

		@Override
		public void run() {
			String url2 = Constants.SERVERS_PORT+"action=AddArticle" +
					"&itemGuid="+mDocument.guid+"" +
							"&itemName="+title+"" +
									"&itemType=-1" +
									"&boardGuid="+mDocument.tagGUIDs+"";
			mAddinfo = HttpC.HttpA(url2);
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
	public class Read_info implements Runnable {
		public Read_info(Context context, String string) {
		}

		@Override
		public void run() {
			String url = Constants.SERVERS_PORT + "action=Statistics" +
					"&itemGuid="+mDocument.guid+"" +
					"&type=-1&action=1&source=2";
			mReadinfo = HttpC.HttpA(url);
			try {
				mReadinfo = new String(mReadinfo.getBytes(), "gb2312");
				handler_Readinfo.sendEmptyMessage(1);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
	}

	@SuppressLint("HandlerLeak")
	private Handler handler_Readinfo = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
		};
	};
	public class Share_info implements Runnable {
		public Share_info(Context context, String string) {
		}

		@Override
		public void run() {
			String url = Constants.SERVERS_PORT + "action=Statistics" +
					"&itemGuid="+mDocument.guid+"" +
					"&type=-1&action=4&source=2";
			mShareinfo = HttpC.HttpA(url);
			try {
				mShareinfo = new String(mShareinfo.getBytes(), "gb2312");
				handler_Shareinfo.sendEmptyMessage(1);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
	}

	@SuppressLint("HandlerLeak")
	private Handler handler_Shareinfo = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
		};
	};

	public static int ACTIVITYID = 1;

	public static void startForResult(Activity activity,
			ArrayList<String> guidList, int position, String titile,
			String tagId) {
		Intent intent = new Intent(activity, Article_Activity.class);
		intent.putStringArrayListExtra("guidList", guidList);
		intent.putExtra("position", position);
		intent.putExtra("title", titile);
		intent.putExtra("tagId", tagId);
		activity.startActivityForResult(intent, ACTIVITYID);
	}
}
