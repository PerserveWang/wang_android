package com.trends.photoalbum.msg;

import java.io.File;

import android.os.Environment;

/**
 * 公共路径
 * 
 * @author hhn
 * 
 */
public class CommonPath {
	public final static String IMAGESPATH = "pic";
	public final static String MUSIC = "mymusic";
	public final static String SDCARD_PATH = Environment.getExternalStorageDirectory().getPath() + File.separator + "pic";
	public final static int DOUBLE_COLUMN = 2;
	public final static int SINGLE_COLUMN = 1;
}
