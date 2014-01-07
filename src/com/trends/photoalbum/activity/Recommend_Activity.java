package com.trends.photoalbum.activity;

import java.io.UnsupportedEncodingException;

import cn.wiz.sdk.util.WizMisc;

import com.trends.photoalbum.msg.Constants;
import com.trends.photoalbum.utils.HttpC;
import com.trends.photoalbum.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;

@SuppressLint("HandlerLeak")
public class Recommend_Activity extends Activity implements OnClickListener {
	private Button recommend_back;
	private Button recommend_submit;
	private TextView recommend_ok;
	private Spinner recommend_sp_type;
	@SuppressWarnings("rawtypes")
	private ArrayAdapter adapter2;
	private EditText recommend_et_name, recommend_et_telphone;
	private TextView recommend_tv_type;
	private TextView recommend_users;
	private String userinfo;
	private String recommend_num;
	private int sp_num;
	@SuppressLint("ShowToast")
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.recommend);
		recommend_users = (TextView) findViewById(R.id.recommend_users);
		recommend_et_telphone = (EditText) findViewById(R.id.recommend_et_telphone);
		recommend_et_name = (EditText) findViewById(R.id.recommend_et_name);
		recommend_tv_type = (TextView) findViewById(R.id.recommend_tv_type);
		recommend_sp_type = (Spinner) findViewById(R.id.recommend_sp_type);
		recommend_back = (Button) findViewById(R.id.recommend_back);
		recommend_submit = (Button) findViewById(R.id.recommend_submit);
		if (!WizMisc.isNetworkAvailable(this)) {
			Toast.makeText(Recommend_Activity.this, "网络异常，请检查您的网络", 1).show();
			recommend_users.setText("0");
		}else{
			new Thread(new recommend_num(Recommend_Activity.this, recommend_num)).start();
		}
		recommend_back.setOnClickListener(this);
		recommend_submit.setOnClickListener(this);
		adapter2 = ArrayAdapter.createFromResource(this, R.array.plantes,
				android.R.layout.simple_spinner_item);
		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		recommend_sp_type.setAdapter(adapter2);
		recommend_sp_type
				.setOnItemSelectedListener(new SpinnerXMLSelectedListener());
		recommend_sp_type.setVisibility(View.VISIBLE);
	}

	class SpinnerXMLSelectedListener implements OnItemSelectedListener {
		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			TextView tv = (TextView) arg1;
			tv.setHorizontallyScrolling(true);
			sp_num = arg2;
			recommend_tv_type.setText(adapter2.getItem(arg2) + "");
			tv.setText("");
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
		}

	}

	@SuppressLint("ShowToast")
	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.recommend_back:
			finish();
			break;
		case R.id.recommend_submit:
			if ("".equals(recommend_et_name.getText().toString())) {
				Toast.makeText(Recommend_Activity.this, "请完善推荐信息！", 1).show();
			} else if ("".equals(recommend_et_telphone.getText().toString())) {
				Toast.makeText(Recommend_Activity.this, "请完善推荐信息！", 1).show();
			} else if(sp_num == 0){
				Toast.makeText(Recommend_Activity.this, "请完善推荐信息！", 1).show();
			}else{
				new Thread(new recommend_userinfo(Recommend_Activity.this, userinfo)).start();
				Toast.makeText(Recommend_Activity.this, "推荐成功~", 1).show();
				finish();
			}
			break;
		default:
			break;
		}

	}
	public class recommend_userinfo implements Runnable {
		public recommend_userinfo(Context context, String string) {
		}

		@Override
		public void run() {
			String url = Constants.SERVERS_PORT + "action=RecommendFriend&rname="+recommend_et_name.getText().toString()+"" +
					"&tel="+recommend_et_telphone.getText().toString()+"&rtype=2" +
					"&source=1&albumGuid="+Constants.KBGUID+"";
			userinfo = HttpC.HttpA(url);
			try {
				userinfo = new String(userinfo.getBytes(), "gb2312");
				handler_userinfo.sendEmptyMessage(1);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
	}

	private Handler handler_userinfo = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
		};
	};
	public class recommend_num implements Runnable {
		public recommend_num(Context context, String string) {
		}

		@Override
		public void run() {
			String url = Constants.SERVERS_PORT + "action=GetReNumByAlbumGuid&albumGuid="+Constants.KBGUID+"";
			recommend_num = HttpC.HttpA(url);
			try {
				recommend_num = new String(recommend_num.getBytes(), "gb2312");
				handler_recommend.sendEmptyMessage(1);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
	}

	private Handler handler_recommend = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			recommend_users.setText(recommend_num);
		};
	};
}
