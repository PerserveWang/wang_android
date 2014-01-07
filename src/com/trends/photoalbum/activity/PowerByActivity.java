package com.trends.photoalbum.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.trends.photoalbum.R;
public class PowerByActivity extends Activity implements OnClickListener {
	private TextView pankerWeb;
	private Button powerby_back;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.powerby_layout);
		powerby_back = (Button) findViewById(R.id.powerby_back);
		pankerWeb = (TextView) findViewById(R.id.panker_web);
		pankerWeb.setText(Html.fromHtml("<u>" + getString(R.string.panker_web)
				+ "</u>"));
		powerby_back.setOnClickListener(this);
		pankerWeb.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.panker_web:
			Intent intent = new Intent();
			intent.setAction("android.intent.action.VIEW");
			Uri content_url = Uri.parse("http://www.panker.cn");
			intent.setData(content_url);
			startActivity(intent);
			break;
		case R.id.powerby_back:
			finish();
			break;
		default:
			break;
		}
	}
}
