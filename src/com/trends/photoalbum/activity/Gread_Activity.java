package com.trends.photoalbum.activity;

import java.io.UnsupportedEncodingException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.Toast;

import com.trends.photoalbum.R;
import com.trends.photoalbum.msg.Constants;
import com.trends.photoalbum.utils.HttpC;

public class Gread_Activity extends Activity implements OnClickListener,
		OnRatingBarChangeListener {
	private RatingBar grade_ratingBar1, grade_ratingBar2, grade_ratingBar3,
			grade_ratingBar4;
	private EditText gread_et;
	private ImageButton grade_back;
	private Button btn_grade_send;
	private int cameraman;
	private int dresser;
	private int effect;
	private int service;
	private String greadmsg;
	private EditText gread_tel;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.grade);
		intview();
	}

	private void intview() {
		gread_tel = (EditText) findViewById(R.id.gread_tel);
		grade_ratingBar1 = (RatingBar) findViewById(R.id.grade_ratingBar1);
		grade_ratingBar2 = (RatingBar) findViewById(R.id.grade_ratingBar2);
		grade_ratingBar3 = (RatingBar) findViewById(R.id.grade_ratingBar3);
		grade_ratingBar4 = (RatingBar) findViewById(R.id.grade_ratingBar4);
		gread_et = (EditText) findViewById(R.id.gread_et);
		grade_back = (ImageButton) findViewById(R.id.grade_back);
		btn_grade_send = (Button) findViewById(R.id.btn_grade_send);
		grade_back.setOnClickListener(this);
		btn_grade_send.setOnClickListener(this);
		grade_ratingBar1.setOnRatingBarChangeListener(this);
		grade_ratingBar2.setOnRatingBarChangeListener(this);
		grade_ratingBar3.setOnRatingBarChangeListener(this);
		grade_ratingBar4.setOnRatingBarChangeListener(this);
	}

	@SuppressLint("ShowToast")
	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.grade_back:
			finish();
			break;
		case R.id.btn_grade_send:
			if(cameraman == 0){
				Toast.makeText(Gread_Activity.this, "您还没有给摄影师评分~", 1).show();
			}else if(dresser == 0){
				Toast.makeText(Gread_Activity.this, "您还没有给化妆师评分~", 1).show();
			}else if(effect == 0){
				Toast.makeText(Gread_Activity.this, "您还没有对拍摄效果评分~", 1).show();
			}else if(service == 0 ){
				Toast.makeText(Gread_Activity.this, "您还没有对服务态度评分~", 1).show();
			}else if("".equals(gread_tel.getText().toString())){
				Toast.makeText(Gread_Activity.this, "您还没有输入电话号码~", 1).show();
			}else{
				new Thread(new Gread_Runnable(Gread_Activity.this, greadmsg)).start();
				Toast.makeText(Gread_Activity.this, "评论成功，谢谢您的参与~", 1).show();
				 finish();
			}
			break;
		default:
			break;
		}

	}

	@Override
	public void onRatingChanged(RatingBar ratingBar, float rating,
			boolean fromUser) {
		int id = ratingBar.getId();
		switch (id) {
		case R.id.grade_ratingBar1:
			cameraman = (int) grade_ratingBar1.getRating();
			break;
		case R.id.grade_ratingBar2:
			dresser = (int) grade_ratingBar2.getRating();
			break;
		case R.id.grade_ratingBar3:
			effect = (int) grade_ratingBar3.getRating();
			break;
		case R.id.grade_ratingBar4:
			service = (int) grade_ratingBar4.getRating();
			break;
		default:
			break;
		}

	}
	public class Gread_Runnable implements Runnable {
		public Gread_Runnable(Context context, String string) {
		}

		@Override
		public void run() {
			String url = Constants.SERVERS_PORT + "action=AddGrade" +
					"&albumGuid="+Constants.KBGUID+"&source=2" +
					"&content="+gread_et.getText().toString()+"&cameraman="+cameraman+"&dresser="+dresser+"" +
							"&result="+effect+"&service="+service+"&tel="+gread_tel.getText().toString()+"";
			greadmsg = HttpC.HttpA(url);
			try {
				greadmsg = new String(greadmsg.getBytes(), "gb2312");
				handler_userinfo.sendEmptyMessage(1);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
	}

	@SuppressLint("HandlerLeak")
	private Handler handler_userinfo = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
		};
	};
//	action=AddGrade&albumGuid=908A2DBC-2E95-4594-BF20-BCF4AC0F3A00&type=1&source=1
//		&content=我评论了&cameraman=3&dresser=5&result=7&service=9 
}
