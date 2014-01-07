package com.trends.photoalbum.activity;

import com.trends.photoalbum.msg.CommonPath;
import com.trends.photoalbum.utils.MyApplicationInfo;
import com.trends.photoalbum.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class WelcomeActivity extends Activity {

	private TextView imgIcon;
	private ImageView appName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);
		imgIcon = (TextView) findViewById(R.id.imgIcon);
		appName = (ImageView) findViewById(R.id.appName);
		((MyApplicationInfo) getApplication()).setColumn(CommonPath.SINGLE_COLUMN);
		Animation mAnimation;
		mAnimation = AnimationUtils.loadAnimation(this, R.anim.icon_ainm);
		imgIcon.setAnimation(mAnimation);
		Animation mAnimation1;
		mAnimation1 = AnimationUtils.loadAnimation(this, R.anim.app_name_anim);
		appName.setAnimation(mAnimation1);

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				startActivity(new Intent(WelcomeActivity.this,
						Main_Activity.class));
				overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
				finish();
			}
		}, 2100);

	}

}
