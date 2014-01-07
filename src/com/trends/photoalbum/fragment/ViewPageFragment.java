package com.trends.photoalbum.fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.trends.photoalbum.activity.DisplayImagesActivity;
import com.trends.photoalbum.activity.Main_Activity;
import com.trends.photoalbum.activity.PictureShowActivity;
import com.trends.photoalbum.msg.CommonPath;
import com.trends.photoalbum.utils.MyApplicationInfo;
import com.trends.photoalbum.view.MyScrollView;
import com.trends.photoalbum.view.SlidingMenu;
import com.trends.photoalbum.view.MyScrollView.OnScrollListener;
import com.trends.photoalbum.waterfalltool.ImageLoaderTask;
import com.trends.photoalbum.waterfalltool.TaskParam;
import com.trends.photoalbum.R;

/**
 * 主界面
 * 
 * @author hhn
 * 
 */
public class ViewPageFragment extends Fragment {

	private ViewPager mPager;
	private ArrayList<Fragment> pagerItemList = new ArrayList<Fragment>();
	private MyScrollView waterfall_scroll;
	private LinearLayout waterfall_container;
	private ArrayList<LinearLayout> waterfall_items;
	private Display display;
	private AssetManager assetManager;
	private List<String> image_filenames;
	private int itemWidth;
	private int column_count;// 显示列数
	private int page_count = 14;// 每次加载14张图片
	private int current_page = 0;// 当前页
	private RelativeLayout topBarRL;
	private ImageView slidingMenuSwitch;
	private ImageView playPicSwitch;
	private ImageView columnSwitch;
	private SlidingMenu slidingMenu;
	private View mView;
	private long lastClick;

	@Override
	@SuppressWarnings("deprecation")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		display = getActivity().getWindowManager().getDefaultDisplay();
		// 根据屏幕大小计算每列大小
		column_count = ((MyApplicationInfo) getActivity().getApplication()).getColumn();
		itemWidth = display.getWidth() / column_count;
		// assets文件管理器
		assetManager = getActivity().getAssets();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.view_pager, null);
		mPager = (ViewPager) mView.findViewById(R.id.pager);
		PageFragment1 page1 = new PageFragment1();
		pagerItemList.add(page1);
		initLayout(mView);
		int slidingMenuId = getArguments().getInt("slidingMenuId");
		slidingMenu = (SlidingMenu) getActivity().findViewById(slidingMenuId);
		topBarRL = (RelativeLayout) mView.findViewById(R.id.top_bar_relative);
		slidingMenuSwitch = (ImageView) mView.findViewById(R.id.sliding_switch_iv_id);
		playPicSwitch = (ImageView) mView.findViewById(R.id.play_switch_iv_id);
		columnSwitch = (ImageView) mView.findViewById(R.id.play_switch_column_id);

		if (column_count == CommonPath.SINGLE_COLUMN) {
			columnSwitch.setBackgroundResource(R.drawable.doublecolumn_swtich_selector);
		} else if (column_count == CommonPath.DOUBLE_COLUMN) {
			columnSwitch.setBackgroundResource(R.drawable.singlecolumn_swtich_selector);
		}

		MyOnClickListener myOnClickListener = new MyOnClickListener();
		topBarRL.setOnClickListener(myOnClickListener);
		slidingMenuSwitch.setOnClickListener(myOnClickListener);
		columnSwitch.setOnClickListener(myOnClickListener);
		playPicSwitch.setOnClickListener(myOnClickListener);

		return mView;
	}

	/**
	 * imageview监听类
	 * 
	 * @author zz
	 * 
	 */
	private class MyOnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.sliding_switch_iv_id:
					slidingMenu.showLeftView();
					break;
				case R.id.play_switch_iv_id:
					// 2.5秒内连续点击不通过
					if (System.currentTimeMillis() - lastClick <= 3000) {
						break;
					} else {
						lastClick = System.currentTimeMillis();
						Intent playIntent = new Intent(getActivity(), DisplayImagesActivity.class);
						playIntent.putExtra("position", 0);
						startActivity(playIntent);
					}
					break;
				case R.id.play_switch_column_id:
					// 2.5秒内连续点击不通过
					if (System.currentTimeMillis() - lastClick <= 3000) {
						break;
					} else {
						lastClick = System.currentTimeMillis();
						if (column_count == CommonPath.SINGLE_COLUMN) {
							((MyApplicationInfo) getActivity().getApplication()).setColumn(2);
							System.out.println(((MyApplicationInfo) getActivity().getApplication()).getColumn());
							Intent intent = new Intent(getActivity(), Main_Activity.class);
							startActivity(intent);
							getActivity().finish();
						} else if (column_count == CommonPath.DOUBLE_COLUMN) {
							((MyApplicationInfo) getActivity().getApplication()).setColumn(1);
							System.out.println(((MyApplicationInfo) getActivity().getApplication()).getColumn());
							Intent intent = new Intent(getActivity(), Main_Activity.class);
							startActivity(intent);
							getActivity().finish();
						}
					}
					break;
				case R.id.top_bar_relative:
					break;
			}
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		// playPicSwitch.setClickable(true);
	}

	/**
	 * 初始化布局
	 * 
	 * @param mView
	 */
	private void initLayout(View mView) {
		waterfall_scroll = (MyScrollView) mView.findViewById(R.id.waterfall_scroll);
		waterfall_scroll.getView();
		// 瀑布每一列布局容器
		waterfall_container = (LinearLayout) mView.findViewById(R.id.waterfall_container);

		// 瀑布流每一条
		waterfall_items = new ArrayList<LinearLayout>();

		waterfall_scroll.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onTop() {
				// 到最顶端
				Log.i("MyScrollView", "Scrl top");
				topBarRL.setVisibility(View.VISIBLE);
				topBarRL.setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.top_alpha_translate));

			}

			@Override
			public void onBottom() {
				Log.i("MyScrollView", "onBottom");
				// 滚动到最低端
				topBarRL.setVisibility(View.VISIBLE);
				topBarRL.setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.top_alpha_translate));
				addItemToContainer(++current_page, page_count);
			}

			@Override
			public void onScroll(int direct) {
				Log.i("MyScrollView", "onScroll");
				topBarRL.setVisibility(View.GONE);
			}

			@Override
			public void onScrolling() {
				Log.i("MyScrollView", "onScrolling");
				topBarRL.setVisibility(View.GONE);
			}

			@Override
			public void onScrollStop() {
				Log.i("MyScrollView", "onScrollStop");
				topBarRL.setVisibility(View.VISIBLE);
				topBarRL.setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.top_alpha_translate));
			}

		});

		// 根据列填充相对应的线形布局
		for (int i = 0; i < column_count; i++) {
			LinearLayout itemLayout = new LinearLayout(getActivity());
			LinearLayout.LayoutParams itemParam = new LinearLayout.LayoutParams(itemWidth, LayoutParams.WRAP_CONTENT);
			// itemParam.width = itemWidth;
			// itemParam.height = LayoutParams.WRAP_CONTENT;
			// itemLayout.setPadding(2, 1, 2, 1);
			itemLayout.setOrientation(LinearLayout.VERTICAL);
			itemLayout.setLayoutParams(itemParam);
			waterfall_items.add(itemLayout);
			waterfall_container.addView(itemLayout);
		}

		// 加载所有图片路径

		try {
			image_filenames = Arrays.asList(assetManager.list(CommonPath.IMAGESPATH));
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 第一次加载
		addItemToContainer(current_page, page_count);
	}

	/**
	 * 添加图片到布局中
	 * 
	 * @param pageindex
	 *            当前页
	 * @param pagecount
	 *            总页数
	 */
	private void addItemToContainer(int pageindex, int pagecount) {
		int j = 0;
		int imagecount = image_filenames.size();
		for (int i = pageindex * pagecount; i < pagecount * (pageindex + 1) && i < imagecount; i++) {
			// 原始写法 适应2列以上
			j = j >= column_count ? j = 0 : j;
			// 2列 没列均匀分布
			// if (j == 0) {
			// j = 1;
			// } else {
			// j = 0;
			// }
			addImage(image_filenames.get(i), j++, i);
		}

	}

	/**
	 * 添加图片
	 * 
	 * @param filename
	 *            文件名 从assets中获得
	 * @param columnIndex
	 *            列编号
	 * @param positon
	 *            当前图片位置
	 */
	private void addImage(String filename, int columnIndex, final int positon) {
		LinearLayout layout = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.waterfallitem, null);
		// ImageView item = (ImageView)
		// LayoutInflater.from(this).inflate(R.layout.waterfallitem, null);

		waterfall_items.get(columnIndex).addView(layout);
		final ImageView imageView = (ImageView) layout.getChildAt(0);
		TaskParam param = new TaskParam();
		param.setAssetManager(assetManager);
		param.setFilename(CommonPath.IMAGESPATH + "/" + filename);
		param.setItemWidth(itemWidth);
		ImageLoaderTask task = new ImageLoaderTask(imageView);
		task.execute(param);

		imageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), PictureShowActivity.class);
				intent.putExtra("position", positon);
				startActivity(intent);
			}
		});

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);
	}

	public boolean isFirst() {
		if (mPager.getCurrentItem() == 0)
			return true;
		else
			return false;
	}

	public boolean isEnd() {
		if (mPager.getCurrentItem() == pagerItemList.size() - 1)
			return true;
		else
			return false;
	}

	@SuppressWarnings("unused")
	private MyPageChangeListener myPageChangeListener;

	public void setMyPageChangeListener(MyPageChangeListener l) {
		myPageChangeListener = l;

	}

	public interface MyPageChangeListener {
		public void onPageSelected(int position);
	}

}
