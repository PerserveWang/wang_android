package com.trends.photoalbum.utils;

import android.app.Application;

public class MyApplicationInfo extends Application {
	private int column;

	public int getColumn() {
		return column;
	}

	public void setColumn(int column) {
		this.column = column;
	}
}