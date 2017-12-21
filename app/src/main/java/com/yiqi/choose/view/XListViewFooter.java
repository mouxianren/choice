package com.yiqi.choose.view;


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yiqi.choose.R;


public class XListViewFooter extends LinearLayout {
	public final static int STATE_NORMAL = 0;
	public final static int STATE_READY = 1;
	public final static int STATE_LOADING = 2;

	private Context mContext;

	private View mContentView;
	private View mProgressBar;
	private TextView mHintView;
	
	public XListViewFooter(Context context) {
		super(context);
		initView(context);//初始化界面
	}
	
	public XListViewFooter(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);//初始化界面
	}

	//设置状态，根据调用者传递回来的参数做不同的事情
	public void setState(int state) {
		//Footer设置为不可见
		mHintView.setVisibility(View.INVISIBLE);
		mProgressBar.setVisibility(View.INVISIBLE);
		mHintView.setVisibility(View.INVISIBLE);
		if (state == STATE_READY) {//若参数为READY
			mHintView.setVisibility(View.VISIBLE);//mHintView设置为可见
			mHintView.setText(R.string.xlistview_footer_hint_ready);//松开载入更多
		} else if (state == STATE_LOADING) {//若参数为LOADING
			mProgressBar.setVisibility(View.VISIBLE);//mProgressBar设置为可见
		} else {//若参数为NORMAL
			mHintView.setVisibility(View.VISIBLE);//mHintView设置为可见
			mHintView.setText(R.string.xlistview_footer_hint_normal);//查看更多
		}
	}
	
	//设置与底部的距离
	public void setBottomMargin(int height) {
		if (height < 0) return ;//若参数小于0,不设置
		//获取父布局信息
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)mContentView.getLayoutParams();
		lp.bottomMargin = height;//设置父布局与底部的距离
		mContentView.setLayoutParams(lp);//设置Footer与底部的距离
	}

	//获取与底部的距离
	public int getBottomMargin() {
		//获取父布局信息
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)mContentView.getLayoutParams();
		return lp.bottomMargin;//返回父布局与底部的距离
	}


	//normal状态
	public void normal() {
		mHintView.setVisibility(View.VISIBLE);
		mProgressBar.setVisibility(View.GONE);
	}


	//loading状态
	public void loading() {
		mHintView.setVisibility(View.GONE);//设置mHintView不可见
		mProgressBar.setVisibility(View.VISIBLE);//设置mProgressBar可见
	}

	//hide状态（当不加载更多时）
	public void hide() {
		//获取父布局信息
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)mContentView.getLayoutParams();
		//设置高度为0
		lp.height = 0;
		mContentView.setLayoutParams(lp);//根据信息调整位置
	}

	//show状态
	public void show() {
		//获取父布局信息
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)mContentView.getLayoutParams();
		//设置父布局高度自适应
		lp.height = LayoutParams.WRAP_CONTENT;
		mContentView.setLayoutParams(lp);//根据信息设置Footer
	}
	//得到mProgressBar 缓冲条的状态
	public boolean getState()
	{
		return !(mProgressBar.getVisibility()==mProgressBar.VISIBLE);
	}

	//初始化Footer
	private void initView(Context context) {
		mContext = context;
		//获取布局
		LinearLayout moreView = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.xlistview_footer, null);
		//将布局添加到本类
		addView(moreView);
		//设置添加的布局的大小时
		moreView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		
		//初始化布局的子控件
		mContentView = moreView.findViewById(R.id.xlistview_footer_content);
		mProgressBar = moreView.findViewById(R.id.xlistview_footer_progressbar);
		mHintView = (TextView)moreView.findViewById(R.id.xlistview_footer_hint_textview);
	}
	
	
}
