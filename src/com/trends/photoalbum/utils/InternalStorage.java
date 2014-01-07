package com.trends.photoalbum.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import android.annotation.SuppressLint;
import android.content.Context;

public class InternalStorage {
	private Context context;
	private String floder;

	public InternalStorage(Context context) {
		this.context = context;
		floder = context.getFilesDir().getAbsolutePath();
	}

	/**
	 * 判断图片文件是否存在
	 * 
	 * @param file
	 * @return
	 */
	public boolean judgeFileIsExist(String fileName) {
		boolean flag = false;
		File file = new File(floder + File.separator + fileName);
		flag = file.exists();
		return flag;
	}

	/**
	 * 
	 * @param inputStream
	 * @param filname
	 * @return
	 */
	@SuppressLint("WorldReadableFiles")
	@SuppressWarnings({ "static-access", "deprecation" })
	public String saveToInternalStorage(InputStream inputStream, String fileName) {
		FileOutputStream fos = null;
		try {
			if (!judgeFileIsExist(fileName)) {
				// fos = new FileOutputStream(file);
				fos = context.openFileOutput(fileName, context.MODE_WORLD_READABLE);
				// File file = new File(context.getFilesDir(), fileName);
				// fos = new FileOutputStream(file);
				byte[] buffer = new byte[7168];
				int count = 0;
				while ((count = inputStream.read(buffer)) > 0) {
					fos.write(buffer, 0, count);
				}
				return floder + File.separator + fileName;
			} else {
				System.out.println("图片文件存在");
				return floder + File.separator + fileName;
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fos != null && inputStream != null) {
					fos.close();
					inputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		return "";
	}

}
