package com.yiqi.choose.view;


import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.yiqi.choose.R;


public class XListView extends ListView implements OnScrollListener {

	private float mLastY = -1; // 保存Y事件
	private Scroller mScroller; // 滚轮回调
	private OnScrollListener mScrollListener; // 监听滚轮事件

	// 实现刷新和加载更多的监听事件
	private IXListViewListener mListViewListener;

	// -- header view
	private XListViewHeader mHeaderView;
	// 头部主体, 计算头部高度.无法刷新时隐藏
	private RelativeLayout mHeaderViewContent;
	private TextView mHeaderTimeView;//设置上次刷新时间
	private int mHeaderViewHeight; // 头部控件的高度
	private boolean mEnablePullRefresh = true;//设置是否刷新
	private boolean mPullRefreshing = false; //是否正在刷新

	// -- footer view
	private XListViewFooter mFooterView;//脚部主体
	private boolean mEnablePullLoad;//设置是否加载更多
	private boolean mPullLoading;//是否正在加载
	private boolean mIsFooterReady = false;//是否已准备好加载更多操作
	
	private int mTotalItemCount;//item总数，用于计算listview的底部位置

	private int mScrollBack;//重置头部和脚部的滚轮位置
	private final static int SCROLLBACK_HEADER = 0;//重置头部
	private final static int SCROLLBACK_FOOTER = 1;//重置脚部

	private final static int SCROLL_DURATION = 400; //重置滚轮速度
	private final static int PULL_LOAD_MORE_DELTA = 50; //当拉起高度超过50时，加载更多
	private final static float OFFSET_RADIO = 1.8f; // support iOS like pull
													// feature.

	/**
	 * @param context
	 */
	public XListView(Context context) {
		super(context);
		initWithContext(context);//初始化listview主体
	}

	public XListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initWithContext(context);//初始化listview主体
	}

	public XListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initWithContext(context);//初始化listview主体
	}

	private void initWithContext(Context context) {
		mScroller = new Scroller(context, new DecelerateInterpolator());
		//listview需要这个滚轮事件，将信息传递给监听器
		super.setOnScrollListener(this);//设置滚轮监听器

		// 初始化头部view
		mHeaderView = new XListViewHeader(context);
		mHeaderViewContent = (RelativeLayout) mHeaderView
				.findViewById(R.id.xlistview_header_content);
		mHeaderTimeView = (TextView) mHeaderView
				.findViewById(R.id.xlistview_header_time);
		addHeaderView(mHeaderView);//将头部view放到当前listview中

		// 初始化脚部view
		mFooterView = new XListViewFooter(context);

		// 初始化头部高度
		mHeaderView.getViewTreeObserver().addOnGlobalLayoutListener(
		//注册一个回调函数，当在一个视图树中全局布局发生改变或者视图树中的某个视图的可视状态发生改变时调用这个回调函数。
				new OnGlobalLayoutListener() {
					public void onGlobalLayout() {
						mHeaderViewHeight = mHeaderViewContent.getHeight();//设置头部控件的高度为头部主体的高度
						getViewTreeObserver()
								.removeGlobalOnLayoutListener(this);//移除之前已经注册的全局布局回调函数。
					}
				});
	}

	//listview设置适配器时将调用
	public void setAdapter(ListAdapter adapter) {
		//确认footer是最后一个footer view ，只增加一次
		if (mIsFooterReady == false) {//如果footer未准备好加载更多
			mIsFooterReady = true;//设置footer已准备好加载更多
			addFooterView(mFooterView);//添加footer到当前listView
		}
		super.setAdapter(adapter);//将传递过来的适配器绑定
	}

	//设置是否可刷新的状态，由调用者设置
	public void setPullRefreshEnable(boolean enable) {
		mEnablePullRefresh = enable;//将传递过来的值设置在变量中保存
		if (!mEnablePullRefresh) { //如果传递过来的值为false，隐藏头部主体
			mHeaderViewContent.setVisibility(View.INVISIBLE);
		} else {
			mHeaderViewContent.setVisibility(View.VISIBLE);//如果为true则显示头部view
		}
	}

	//设置是否可以加载更多的状态值
	public void setPullLoadEnable(boolean enable) {
		mEnablePullLoad = enable;//将传递过来的值设置在变量中保存
		if (!mEnablePullLoad) {//如果传递过来的值为false
			mFooterView.hide();//隐藏脚部主体
			mFooterView.setOnClickListener(null);//设置点击事件的监听器为空
		} else {//如果传递过来的值未true
			mPullLoading = false;//设置正在加载的状态为false
			mFooterView.show();//显示脚部主体
			mFooterView.setState(XListViewFooter.STATE_NORMAL);//设置脚部主体的状态为NORMAL
			//拉起或者点击都将会加载数据（调用LoadMore函数）
			mFooterView.setOnClickListener(new OnClickListener() {//设置点击事件
				public void onClick(View v) {
					startLoadMore();
				}
			});
		}
	}

	//停止刷新
	public void stopRefresh() {
		if (mPullRefreshing == true) {//如果头部主体的状态为正在刷新
			mPullRefreshing = false;//将其状态改变为false
			resetHeaderHeight();//复位header的位置
		}
	}

	//停止加载更多
	public void stopLoadMore() {
		if (mPullLoading == true) {//如果Footer的状态为正在加载更多
			mPullLoading = false;//将其状态改变为false
			mFooterView.setState(XListViewFooter.STATE_NORMAL);//设置Footer的状态为NORMAL
		}
	}
	
	public boolean getProgressState(){
		
		return mFooterView.getState();
	}

	//设置最后的刷新时间
	public void setRefreshTime(String time) {
		mHeaderTimeView.setText(time);//将传递过来的参数设置到Header中显示
	}

	//当滚轮滚动时计算高度
	private void invokeOnScrolling() {
		if (mScrollListener instanceof OnXScrollListener) {//判断左边对象是否为右边对象的实例
			OnXScrollListener l = (OnXScrollListener) mScrollListener;//如果是 则强制转换
			l.onXScrolling(this);//当滚动时将当前View（这里是listview）传递出去
		}
	}

	//根据传回的值，更新头部位置
	private void updateHeaderHeight(float delta) {
		mHeaderView.setVisiableHeight((int) delta
				+ mHeaderView.getVisiableHeight());//设置头部的位置为头部的高度加上传回的值
		if (mEnablePullRefresh && !mPullRefreshing) { // 未处于刷新状态，更新箭头
			if (mHeaderView.getVisiableHeight() > mHeaderViewHeight) {//当头部被拉起的高度大于头部的高度时
				mHeaderView.setState(XListViewHeader.STATE_READY);//设置头部的状态为READY
			} else {
				mHeaderView.setState(XListViewHeader.STATE_NORMAL);//设置头部的状态为NORMAL
			}
		}
		setSelection(0); //滚轮回转到顶部
	}

	//重置头部的位置
	private void resetHeaderHeight() {
		int height = mHeaderView.getVisiableHeight();//获得头部的当前位置
		if (height == 0) //当头部隐藏时，直接返回
			return;
		//当头部的状态为正在刷新，并且未完全显示时，直接返回
		if (mPullRefreshing && height <= mHeaderViewHeight) {
			return;
		}
		
		int finalHeight = 0; //其他状态，滚轮回滚到顶部，头部隐藏
		//正在刷新并且头部 的位置大于头部 的高度时，滚轮回滚到显示整个头部的位置
		if (mPullRefreshing && height > mHeaderViewHeight) {
			finalHeight = mHeaderViewHeight;
		}
		mScrollBack = SCROLLBACK_HEADER;//滚轮回滚的位置为HEADER
		mScroller.startScroll(0, height, 0, finalHeight - height,
				SCROLL_DURATION);//滚轮开始回滚
		// trigger computeScroll
		invalidate();//刷新view
	}

	//更新脚部的高度
	private void updateFooterHeight(float delta) {
		int height = mFooterView.getBottomMargin() + (int) delta;//获得脚部当前被拉起的高度（与底部的距离+传递过来的值）
		if (mEnablePullLoad && !mPullLoading) {//当(加载更多且未正在加载)或者(未加载更多且正在加载)时
			if (height > PULL_LOAD_MORE_DELTA) { //当拉到足够的高度时，加载更多
				mFooterView.setState(XListViewFooter.STATE_READY);//设置底部的状态为READY
			} else {
				mFooterView.setState(XListViewFooter.STATE_NORMAL);//设置底部的状态为NORMAL
			}
		}
		mFooterView.setBottomMargin(height);//设置脚部的位置为脚部 的高度

//		setSelection(mTotalItemCount - 1); // scroll to bottom
	}

	//重置脚部的位置
	private void resetFooterHeight() {
		int bottomMargin = mFooterView.getBottomMargin();//获得脚部距离屏幕底部的位置
		if (bottomMargin > 0) {//如果大于0，说明脚部被拉起
			mScrollBack = SCROLLBACK_FOOTER;//设置滚轮回滚的位置为SCROLLBACK_FOOTER
			mScroller.startScroll(0, bottomMargin, 0, -bottomMargin,
					SCROLL_DURATION);//开始回滚
			invalidate();//刷新view
		}
	}

	//开始加载更多
	private void startLoadMore() {
		mPullLoading = true;//设置底部状态为正在加载更多
		if (mListViewListener != null) {
			mListViewListener.onLoadMore();//执行加载
		}
		mFooterView.setState(XListViewFooter.STATE_LOADING);//加载状态，此时为正在加载更多
	}

	//触摸事件
	public boolean onTouchEvent(MotionEvent ev) {
		if (mLastY == -1) {//如果mLastY为初始状态
			mLastY = ev.getRawY();//设置为相对屏幕的位置
		}

		switch (ev.getAction()) {//捕捉触摸事件的动作
		case MotionEvent.ACTION_DOWN://按下动作
			mLastY = ev.getRawY();//设置为相对屏幕的位置
			break;
		case MotionEvent.ACTION_MOVE://移动
			final float deltaY = ev.getRawY() - mLastY;//屏幕显示位置为触摸位置-相对屏幕的位置
			mLastY = ev.getRawY();//设置为相对屏幕的位置（不断改变）
			if (getFirstVisiblePosition() == 0//指向第一个ListItem
					&& (mHeaderView.getVisiableHeight() > 0 || deltaY > 0)) {//Header可见时
				//当第一个item可见时，header显示或者隐藏
				updateHeaderHeight(deltaY / OFFSET_RADIO);//更新Header的位置
				invokeOnScrolling();//调用方法 计算滚轮 的位置
			} else if (getLastVisiblePosition() == mTotalItemCount - 1//指向最后一个listItem
					&& (mFooterView.getBottomMargin() > 0 || deltaY < 0)) {//Footer可见时
				// 当最后一个item被放下或者拉起时
				updateFooterHeight(-deltaY / OFFSET_RADIO);//更新Footer的我只
			}
			break;
		default://即放开屏幕时
			mLastY = -1; // 复位
			if (getFirstVisiblePosition() == 0) {//当第一个item为header时
				// invoke refresh
				if (mEnablePullRefresh//可刷新
						&& mHeaderView.getVisiableHeight() > mHeaderViewHeight) {//Header显示的高度大于Header的高度时
					mPullRefreshing = true;//设置刷新状态为正在刷新
					mHeaderView.setState(XListViewHeader.STATE_REFRESHING);//设置刷新状态为正在刷新
					if (mListViewListener != null) {//监听事件不为空时
						mListViewListener.onRefresh();//调用监听事件的刷新方法
					}
				}
				resetHeaderHeight();//复位Header的位置
			} else if (getLastVisiblePosition() == mTotalItemCount - 1) {//当最后一个item为footer时
				// invoke load more.
				if (mEnablePullLoad//可加载更多
						&& mFooterView.getBottomMargin() > PULL_LOAD_MORE_DELTA) {//footer显示的高度大于footer的高度时
					startLoadMore();//开始加载更多
				}
				resetFooterHeight();//复位Footer的位置
			}
			break;
		}
		return super.onTouchEvent(ev);
	}

	//计算滚轮位置
	public void computeScroll() {
		//当想要知道新的位置时，调用此函数。如果返回true，表示动画还没有结束。位置改变以提供一个新的位置。
		if (mScroller.computeScrollOffset()) {
			if (mScrollBack == SCROLLBACK_HEADER) {//是否重置滚轮位置(此处为重置滚轮位置到header的位置)
				mHeaderView.setVisiableHeight(mScroller.getCurrY());//设置header的位置为滚轮与Y坐标的偏移量
			} else {
				mFooterView.setBottomMargin(mScroller.getCurrY());//设置footer的位置为滚轮与Y坐标的偏移量
			}
			postInvalidate();//可以直接在线程中更新界面
			invokeOnScrolling();//计算滚轮位置（将当前的listview传递出去）
		}
		super.computeScroll();
	}

	//设置滚轮监听事件，将监听器传递出去
	public void setOnScrollListener(OnScrollListener l) {
		mScrollListener = l;
	}

	//滚轮状态改变时
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (mScrollListener != null) {//若监听器不为空时
			mScrollListener.onScrollStateChanged(view, scrollState);//将参数传递出去（scrollState为滚轮位置<顶部，底部，其他>）
		}
	}

	//
	public void onScroll(AbsListView view, int firstVisibleItem,
						 int visibleItemCount, int totalItemCount) {
		// firstVisibleItem表示在现时屏幕第一个ListItem(部分显示的ListItem也算)在整个ListView的位置（下标从0开始）
		// visibleItemCount表示在现时屏幕可以见到的ListItem(部分显示的ListItem也算)总数 
		// totalItemCount表示ListView的ListItem总数 
		// 将参数传递到我自己的监听器上
		mTotalItemCount = totalItemCount;
		if (mScrollListener != null) {
			mScrollListener.onScroll(view, firstVisibleItem, visibleItemCount,
					totalItemCount);
		}
	}

	//将监听器传递出去
	public void setXListViewListener(IXListViewListener l) {
		mListViewListener = l;
	}

	//以此监听滚轮事件，在header/footer回滚时调用
	public interface OnXScrollListener extends OnScrollListener {
		public void onXScrolling(View view);
	}

	//实现此接口以获得刷新/加载更多的事件
	public interface IXListViewListener {
		public void onRefresh();

		public void onLoadMore();
	}
}
