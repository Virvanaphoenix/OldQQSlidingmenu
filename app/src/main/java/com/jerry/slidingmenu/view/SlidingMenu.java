package com.jerry.slidingmenu.view;

import com.jerry.qq_50_slidingmenu.R;
import com.nineoldandroids.view.ViewHelper;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

public class SlidingMenu extends HorizontalScrollView {
	private LinearLayout mWapper;
	private ViewGroup mMenu;
	private ViewGroup mConetent;
	private int mScreenWidth;

	private int mMenuWidth;// 左侧菜单宽度

	private boolean once;

	private boolean isOpen;
	// dp
	private int mMenuRightPadding = 50;

	/**
	 * 未使用自定义属性时，调用
	 * 
	 * @param context
	 * @param attrs
	 */
	public SlidingMenu(Context context, AttributeSet attrs) {
		this(context, attrs, 0);

	}

	/**
	 * 设置子View的宽和高 设置自己的宽和高
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (!once) {// 避免重复设置宽和高
			mWapper = (LinearLayout) getChildAt(0);
			mMenu = (ViewGroup) mWapper.getChildAt(0);
			mConetent = (ViewGroup) mWapper.getChildAt(1);
			mMenuWidth = mMenu.getLayoutParams().width = mScreenWidth
					- mMenuRightPadding;
			mConetent.getLayoutParams().width = mScreenWidth;
			once = true;
		}

		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	/**
	 * 当使用了自定义属性，会调用此方法获得自定义值
	 * 
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public SlidingMenu(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		// 获取我们定义的属性
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
				R.styleable.SlidingMenu, defStyle, 0);
		int n = a.getIndexCount();// 拿到attr里属性的数量
		for (int i = 0; i < n; i++) {
			int attr = a.getIndex(i);
			switch (attr) {
			case R.styleable.SlidingMenu_rightPadding:
				mMenuRightPadding = a.getDimensionPixelSize(attr,
						(int) TypedValue.applyDimension(
								TypedValue.COMPLEX_UNIT_DIP, 50, context
										.getResources().getDisplayMetrics()));
				break;

			default:
				break;
			}
		}
		a.recycle();// 需释放

		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		mScreenWidth = outMetrics.widthPixels;

		/*
		 * // dp转换成px mMenuRightPadding = (int) TypedValue.applyDimension(
		 * TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources()
		 * .getDisplayMetrics());
		 */
	}

	public SlidingMenu(Context context) {
		this(context, null);

	}

	/**
	 * 通过设置偏移量，将menu隐藏
	 */
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if (changed) {
			this.scrollTo(mMenuWidth, 0);// 控制menu隐藏
		}

	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		int action = ev.getAction();
		switch (action) {
		case MotionEvent.ACTION_UP:
			int scrollX = getScrollX();// 内容多出来的部分(隐藏在左边的宽度)
			if (scrollX >= mMenuWidth / 2) {
				this.smoothScrollTo(mMenuWidth, 0);// 将其隐藏
				isOpen = false;

			} else {
				this.smoothScrollTo(0, 0);// 将其显示
				isOpen = true;
			}
			return true;
		}
		return super.onTouchEvent(ev);
	}

	/**
	 * 打开菜单
	 */
	public void openMenu() {
		if (isOpen)
			return;
		this.smoothScrollTo(0, 0);
		isOpen = true;
	}

	/**
	 * 关闭菜单
	 */
	public void closeMenu() {
		if (!isOpen)
			return;
		this.smoothScrollTo(mMenuWidth, 0);
		isOpen = false;

	}

	/**
	 * 切换菜单
	 */
	public void toggle() {
		if (isOpen) {
			closeMenu();
		} else {
			openMenu();
		}
	}

	/**
	 * 滚动发生时
	 */
	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);

		float scale = l * 1.0f / mMenuWidth;// 1~0,梯度值

		/**
		 * 区别1：内容区域1.0~0.7缩放的效果 scale:1.0 ~ 0.0 0.7 + 0.3 * scale
		 * 
		 * 区别2：菜单的偏移量需要修改
		 * 
		 * 区别3：菜单的显示时有缩放，以及透明度变化 缩放 0.7 ~ 1.0 1.0 - scale * 0.3 透明度 0.6 ~ 1.0
		 * 0.6 + 0.4 * (1-scale)
		 */

		float rightScale = 0.7f + 0.3f * scale;
		float leftScale = 1.0f - scale * 0.3f;
		float leftAlpha = 0.6f + 0.4f * (1 - scale);

		// 调用属性动画，设置TranslationX
		ViewHelper.setTranslationX(mMenu, mMenuWidth * scale * 0.7f);
		ViewHelper.setScaleX(mMenu, leftScale);
		ViewHelper.setScaleY(mMenu, leftScale);
		ViewHelper.setAlpha(mMenu, leftAlpha);
		
		// 设置content的缩放中心点
		ViewHelper.setPivotX(mConetent, 0);
		ViewHelper.setPivotY(mConetent, mConetent.getHeight() / 2);

		ViewHelper.setScaleX(mConetent, rightScale);
		ViewHelper.setScaleY(mConetent, rightScale);
	}
}
