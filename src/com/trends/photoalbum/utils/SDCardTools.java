package com.trends.photoalbum.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.os.Environment;

import com.trends.photoalbum.msg.CommonPath;

public class SDCardTools {

	/**
	 * 判断SDCard是否挂载
	 * 
	 * @return true:SDCard已挂载 false:没有SdCard
	 */
	public static boolean judgeSDCardIsExist() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}

	/**
	 * 判断SDCard卡文件夹是否存在
	 * 
	 * @return true:文件夹存在可写入 false:SDCard不存在
	 */
	public static boolean judgeFolderIsExist() {
		if (judgeSDCardIsExist()) {
			File file = new File(CommonPath.SDCARD_PATH);
			if (!file.exists()) {
				file.mkdir();
				System.out.println("文件夹创建");
				return true;
			} else {
				System.out.println("文件夹存在");
				return true;
			}
		}
		System.out.println("sd不存在");
		return false;
	}

	/**
	 * 保存到SDCrad上
	 * 
	 * @param inputStream
	 *            输入流
	 * @param fileName
	 *            文件名
	 * @return
	 */
	public static String savaToSDCard(InputStream inputStream, String fileName) {
		FileOutputStream fos = null;
		try {
			File file = new File(CommonPath.SDCARD_PATH + File.separator + fileName);
			if (!judgeFileIsExist(file)) {
				fos = new FileOutputStream(file);
				byte[] buffer = new byte[7168];
				int count = 0;
				while ((count = inputStream.read(buffer)) > 0) {
					fos.write(buffer, 0, count);
				}
				return file.getAbsolutePath();
			} else {
				System.out.println("图片文件存在");
				return file.getAbsolutePath();
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

	/**
	 * 判断写入图片是否存在
	 * 
	 * @param file
	 *            文件
	 * @return true:文件存在 false:文件不存在
	 */
	public static boolean judgeFileIsExist(File file) {
		boolean flag = false;
		flag = file.exists();
		return flag;
	}
}
