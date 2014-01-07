package com.trends.photoalbum.utils;

import com.trends.photoalbum.msg.Constants;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

public class LocalMisc {
	private static SharedPreferences mPref;

	public static SharedPreferences getSharedPreferevces(Context context) {
		mPref = context.getSharedPreferences(Constants.SHAREDPREFERENCES_VALUE,
				0);
		return mPref;
	}

	@SuppressLint("ShowToast")
	public static void showToast(Context context, int strId) {
		Toast.makeText(context, strId, Toast.LENGTH_SHORT).show();
	}
}
