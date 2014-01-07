package com.trends.photoalbum.fragment;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher.OnPhotoTapListener;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.trends.photoalbum.R;
import com.trends.photoalbum.msg.CommonPath;
import com.trends.photoalbum.waterfalltool.BitmapCache;

/**
 * PhotoView展示单张图片
 * 
 * @author hhn
 * 
 */
public class ShowPhotoViewItemFragment extends Fragment {

	private AssetManager assetManager;
	private PhotoView mPhotoView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.picture_item, null);
		mPhotoView = (PhotoView) view.findViewById(R.id.picture_item_iv_id);
		// 点击事件
		mPhotoView.setOnPhotoTapListener(new OnPhotoTapListener() {
			@Override
			public void onPhotoTap(View view, float x, float y) {
				getActivity().finish();
			}
		});
		Bundle bundle = getArguments();
		assetManager = getActivity().getAssets();
		// 得到图片位图
		Bitmap bitmap = BitmapCache.getInstance().getBitmap(CommonPath.IMAGESPATH + "/" + bundle.getString("filename"), assetManager);
		mPhotoView.setImageBitmap(bitmap);
		return view;
	}

	// 两种方法赋值ImageView
	// InputStream assetFile = null;
	// try {
	// // 打开指定资源对应的输入流
	// assetFile = assetManager.open(image_path + File.separator +
	// bundle.getString("filename"));
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	//
	// BitmapDrawable bitmapDrawable = (BitmapDrawable)
	// imageView.getDrawable();
	// // 如果图片还未回收，先强制回收该图片
	// if (bitmapDrawable != null &&
	// !bitmapDrawable.getBitmap().isRecycled()) {
	// bitmapDrawable.getBitmap().recycle();
	// }
	// // 改变ImageView显示的图片
	// imageView.setImageBitmap(BitmapFactory.decodeStream(assetFile));

}
