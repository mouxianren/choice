package com.yiqi.choose.view;


import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yiqi.choose.R;


public class XListViewHeader extends LinearLayout {
	private LinearLayout mContainer;
	private ImageView mArrowImageView;
	private ProgressBar mProgressBar;
	private TextView mHintTextView;
	private int mState = STATE_NORMAL;

	private Animation mRotateUpAnim;
	private Animation mRotateDownAnim;
	
	private final int ROTATE_ANIM_DURATION = 180;
	
	public final static int STATE_NORMAL = 0;
	public final static int STATE_READY = 1;
	public final static int STATE_REFRESHING = 2;

	public XListViewHeader(Context context) {
		super(context);
		initView(context);//初始化界面
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public XListViewHeader(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);//初始化界面
	}

	private void initView(Context context) {
		// 初始情况，设置下拉刷新view高度为0
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, 0);
		//获取布局文件到本类
		mContainer = (LinearLayout) LayoutInflater.from(context).inflate(
				R.layout.xlistview_header, null);
		addView(mContainer, lp);//将Header添加到本类
		setGravity(Gravity.BOTTOM);//设置Header的位置在底部


		//初始化Header子控件
		mArrowImageView = (ImageView)findViewById(R.id.xlistview_header_arrow);
		mHintTextView = (TextView)findViewById(R.id.xlistview_header_hint_textview);
		mProgressBar = (ProgressBar)findViewById(R.id.xlistview_header_progressbar);

		//将箭头旋转180度（向上）
		mRotateUpAnim = new RotateAnimation(0.0f, -180.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		mRotateUpAnim.setDuration(ROTATE_ANIM_DURATION);//设置旋转速度
		mRotateUpAnim.setFillAfter(true);//终止填充
		mRotateDownAnim = new RotateAnimation(-180.0f, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);////将箭头旋转180度（向下）
		mRotateDownAnim.setDuration(ROTATE_ANIM_DURATION);//设置旋转速度
		mRotateDownAnim.setFillAfter(true);//终止填充
	}

	//设置状态
	public void setState(int state) {
		if (state == mState) return ;//当传递过来的状态值为当前状态时，不做设置

		if (state == STATE_REFRESHING) {	// 显示进度
			mArrowImageView.clearAnimation();//清楚imageView上的Animation
			mArrowImageView.setVisibility(View.INVISIBLE);//箭头设置不可见
			mProgressBar.setVisibility(View.VISIBLE);//进度条设置可见
		} else {	// 显示箭头图片
			mArrowImageView.setVisibility(View.VISIBLE);
			mProgressBar.setVisibility(View.INVISIBLE);//隐藏进度条
		}

		switch(state){
		case STATE_NORMAL://当返回的状态是NORMAL时
			if (mState == STATE_READY) {//若当前状态为READY
				mArrowImageView.startAnimation(mRotateDownAnim);//设置箭头向下
			}
			if (mState == STATE_REFRESHING) {//若当前状态为正在刷新
				mArrowImageView.clearAnimation();//清除Animation
			}
			mHintTextView.setText(R.string.xlistview_header_hint_normal);//下拉刷新
			break;
		case STATE_READY://当返回的状态是READY时
			if (mState != STATE_READY) {//若当前的状态不为READY
				mArrowImageView.clearAnimation();//清除Animation
				mArrowImageView.startAnimation(mRotateUpAnim);//设置箭头向上
				mHintTextView.setText(R.string.xlistview_header_hint_ready);//松开刷新数据
			}
			break;
		case STATE_REFRESHING://当返回的状态是REFRESHING时
			mHintTextView.setText(R.string.xlistview_header_hint_loading);//正在加载...
			break;
			default:
		}

		mState = state;//设置当前状态为返回的状态值
	}

	//设置可见的Header高度
	public void setVisiableHeight(int height) {
		if (height < 0)
			height = 0;//若返回的高度小于0，将它设置为0
		//获取父布局的信息
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mContainer
				.getLayoutParams();
		lp.height = height;//设置父布局的高度为传回来的高度
		mContainer.setLayoutParams(lp);//按照此信息重新设置Header
	}

	//获取Header的高度
	public int getVisiableHeight() {
		return mContainer.getHeight();//返回当前布局的高度
	}

}
