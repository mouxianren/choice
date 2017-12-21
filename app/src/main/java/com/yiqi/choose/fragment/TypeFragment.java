package com.yiqi.choose.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.chanven.lib.cptr.circle.CircularProgressView;
import com.umeng.analytics.MobclickAgent;
import com.yiqi.choose.R;
import com.yiqi.choose.activity.MainAcitivity1;
import com.yiqi.choose.activity.search.SearchWordActivity;
import com.yiqi.choose.base.BaseFragment;
import com.yiqi.choose.factory.ThreadPollFactory;
import com.yiqi.choose.model.GuanjianciInfo;
import com.yiqi.choose.thread.AllGoodsNumbersThread;
import com.yiqi.choose.thread.GuanjianciThread;
import com.yiqi.choose.utils.NetJudgeUtils;
import com.yiqi.choose.utils.ParseJsonCommon;
import com.yiqi.choose.view.NoScrollViewPager;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.view.View.VISIBLE;
import static com.yiqi.choose.R.id.fragment_youhui_ll_nowife;
import static com.yiqi.choose.fragment.YouhuiFragment.goodsTypeList;

/**
 * Created by moumou on 17/12/8.
 * 分类的主页面 1.1.4版本以上拥有
 */

public class TypeFragment extends BaseFragment {
    @BindView(R.id.home_fragment_search)
    LinearLayout mHomeFragmentSearch;
    @BindView(R.id.view_f4)
    View mViewF4;
    @BindView(R.id.tools)
    LinearLayout mTools;
    @BindView(R.id.tools_scrlllview)
    ScrollView mToolsScrlllview;
    @BindView(R.id.goods_pager)
    NoScrollViewPager mGoodsPager;
    @BindView(R.id.ll_havawife)
    LinearLayout mLlHavawife;
    @BindView(R.id.home_fragment_tv_retro)
    TextView mHomeFragmentTvRetro;
    @BindView(fragment_youhui_ll_nowife)
    LinearLayout mFragmentYouhuiLlNowife;
    @BindView(R.id.cp_image)
    CircularProgressView mCpImage;
    @BindView(R.id.ll_cp_image)
    RelativeLayout mLlCpImage;
    Unbinder unbinder;
    @BindView(R.id.tv_number)
    TextView mTvNumber;
    @BindView(R.id.fragment_type_title)
    LinearLayout mFragmentTypeTitle;
    private TextView toolsTextViews[];
    private View views[];
    private View viewRed[];
    private ImageView imageLine[];
    private LayoutInflater inflater;

    private int scrllViewWidth = 0, scrollViewMiddle = 0;
    private int currentItem = 0;
    private ShopAdapter shopAdapter;
    private MyHandler hd;

    /**
     * 标志位，标志已经初始化完成
     */
    private boolean isPrepared;
    /**
     * 是否已被加载过一次，第二次就不再去请求数据了
     */
    private boolean mHasLoadedOnce;
    public static  ArrayList<Object> typeList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_type, container, false);
        unbinder = ButterKnife.bind(this, view);
        isPrepared = true;
        mHasLoadedOnce = false;
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        inflater = LayoutInflater.from(getActivity());
        shopAdapter = new ShopAdapter(getFragmentManager());
        // initData();
        hd = new MyHandler((MainAcitivity1) getActivity());
        typeList=new ArrayList<Object>();
        // initPager();
    }

    private void showToolsView() {
        toolsTextViews = new TextView[typeList.size()];
        views = new View[typeList.size()];
        viewRed = new View[typeList.size()];
        imageLine = new ImageView[typeList.size()];
        for (int i = 0; i < typeList.size(); i++) {
            View view = inflater.inflate(R.layout.item_b_top_nav_layout, null);
            view.setId(i);
            view.setOnClickListener(toolsItemListener);
            TextView textView = (TextView) view.findViewById(R.id.text);
            View view_red = view.findViewById(R.id.view_red);
            ImageView line = (ImageView) view.findViewById(R.id.line);
            textView.setText(((GuanjianciInfo) typeList.get(i)).getName());
            mTools.addView(view);
            toolsTextViews[i] = textView;
            imageLine[i] = line;
            views[i] = view;
            viewRed[i] = view_red;
        }
        // changeTextColor(0);

    }

    /**
     * initPager<br/>
     * 初始化ViewPager控件相关内容
     */
    private void initPager(){
        typeList.clear();
        typeList.addAll(goodsTypeList);
        if(goodsTypeList.size()>0){
            typeList.remove(0);
        }
        showToolsView();
        changeBackGround(0);

        //        YouhuiFragment.goodsTypeList.addAll(YouhuiFragment.goodsTypeList);
        //        YouhuiFragment.goodsTypeList.addAll(YouhuiFragment.goodsTypeList);
        //        YouhuiFragment.goodsTypeList.addAll(YouhuiFragment.goodsTypeList);
        //        YouhuiFragment.goodsTypeList.addAll(YouhuiFragment.goodsTypeList);


        mGoodsPager.setAdapter(shopAdapter);
        mGoodsPager.setOnPageChangeListener(onPageChangeListener);
    }

    /**
     * 改变栏目位置
     *
     * @param clickPosition
     */
    private void changeTextLocation(int clickPosition) {

        int x = (views[clickPosition].getTop() - getScrollViewMiddle() + (getViewheight(views[clickPosition]) / 2));
        mToolsScrlllview.smoothScrollTo(0, x);
    }

    /**
     * 返回scrollview的中间位置
     *
     * @return
     */
    private int getScrollViewMiddle() {
        if (scrollViewMiddle == 0)
            scrollViewMiddle = getScrollViewheight() / 2;
        return scrollViewMiddle;
    }

    /**
     * 返回ScrollView的宽度
     *
     * @return
     */
    private int getScrollViewheight() {
        if (scrllViewWidth == 0)
            scrllViewWidth = mToolsScrlllview.getBottom() - mToolsScrlllview.getTop();
        return scrllViewWidth;
    }

    /**
     * 返回view的宽度
     *
     * @param view
     * @return
     */
    private int getViewheight(View view) {
        return view.getBottom() - view.getTop();
    }

    /**
     * 改变textView的颜色
     *
     * @param id
     */
    private void changeTextColor(int id) {
        for (int i = 0; i < toolsTextViews.length; i++) {
            if (i != id) {
                toolsTextViews[i].setBackgroundResource(android.R.color.transparent);
                toolsTextViews[i].setTextColor(0xff000000);
            }
        }
        toolsTextViews[id].setBackgroundResource(android.R.color.white);
        toolsTextViews[id].setTextColor(0xffff5d5e);
    }

    private void changeBackGround(int id) {
        for (int i = 0; i < toolsTextViews.length; i++) {
            if (i != id) {
                views[i].setBackgroundResource(R.color.background);
                viewRed[i].setVisibility(View.INVISIBLE);
                imageLine[i].setVisibility(VISIBLE);

                if (i == id - 1 && i >= 0) {
                    imageLine[i].setVisibility(View.GONE);
                }
            }
        }
        imageLine[id].setVisibility(View.GONE);
        views[id].setBackgroundResource(android.R.color.white);
        viewRed[id].setVisibility(VISIBLE);
    }

    private void initData() {
        mHasLoadedOnce = true;

        if (NetJudgeUtils.getNetConnection(getActivity())) {
            if (goodsTypeList.size() >1) {
                mLlHavawife.setVisibility(VISIBLE);
                mLlCpImage.setVisibility(View.GONE);
                mCpImage.innerStop();
                mFragmentYouhuiLlNowife.setVisibility(View.GONE);
                initPager();
            } else {
                mLlHavawife.setVisibility(View.GONE);
                mLlCpImage.setVisibility(VISIBLE);
                mCpImage.innerStart();
                mFragmentYouhuiLlNowife.setVisibility(View.GONE);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (YouhuiFragment.goodsTypeList.size() >1) {
                            hd.sendEmptyMessage(3);

                        } else {
                            if (NetJudgeUtils.getNetConnection(getActivity())) {
                                ThreadPollFactory.getNormalPool().execute(new GuanjianciThread(getActivity(), hd));
                            } else {
                                mLlHavawife.setVisibility(View.GONE);
                                mLlCpImage.setVisibility(View.GONE);
                                mCpImage.innerStop();
                                mFragmentYouhuiLlNowife.setVisibility(VISIBLE);
                            }
                        }

                    }
                }, 350);

            }
        } else {
            mLlHavawife.setVisibility(View.GONE);
            mLlCpImage.setVisibility(View.GONE);
            mCpImage.innerStop();
            mFragmentYouhuiLlNowife.setVisibility(VISIBLE);
        }

    }


    /**
     * OnPageChangeListener<br/>
     * 监听ViewPager选项卡变化事的事件
     */

    private ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageSelected(int arg0) {
            if (mGoodsPager.getCurrentItem() != arg0)
                mGoodsPager.setCurrentItem(arg0);
            if (currentItem != arg0) {
                // changeTextColor(arg0);
                changeTextLocation(arg0);
                changeBackGround(arg0);
            }
            currentItem = arg0;
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    };


    /**
     * ViewPager 加载选项卡
     *
     * @author Administrator
     */
    private class ShopAdapter extends FragmentPagerAdapter {
        public ShopAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int arg0) {
            Fragment fragment = new Fragment_pro_type();
            Bundle bundle = new Bundle();
            String str = ((GuanjianciInfo) typeList.get(arg0)).getName();
            bundle.putString("typename", str);
            bundle.putInt("position", arg0);
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public int getCount() {
            return typeList.size();
        }
    }


    private View.OnClickListener toolsItemListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mGoodsPager.setCurrentItem(v.getId(), false);
        }
    };

    @Override
    protected void lazyLoad() {
        if (!isPrepared || mHasLoadedOnce) {
            return;
        }
        initData();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("TypeFragment");
        if (MainAcitivity1.allGoodsNumbers > 0) {
            mTvNumber.setText("搜索商品,共" + MainAcitivity1.allGoodsNumbers + "款商品");
        } else {
            mTvNumber.setText("搜索商品");

        }
        if (NetJudgeUtils.getNetConnection(getActivity()) || getUserVisibleHint()) {
            ThreadPollFactory.getNormalPool().execute(new AllGoodsNumbersThread(hd, getActivity()));
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("TypeFragment");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.home_fragment_tv_retro,R.id.fragment_type_title})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.home_fragment_tv_retro:
                if (NetJudgeUtils.getNetConnection(getActivity())) {
                    mLlHavawife.setVisibility(View.GONE);
                    mLlCpImage.setVisibility(VISIBLE);
                    mCpImage.innerStart();
                    mFragmentYouhuiLlNowife.setVisibility(View.GONE);

                    if (YouhuiFragment.goodsTypeList.size() >1) {
                        mLlHavawife.setVisibility(VISIBLE);
                        mLlCpImage.setVisibility(View.GONE);
                        mCpImage.innerStop();
                        mFragmentYouhuiLlNowife.setVisibility(View.GONE);
                        initPager();
                    } else {
                        ThreadPollFactory.getNormalPool().execute(new GuanjianciThread(getActivity(), hd));

                    }
                } else {
                    mLlHavawife.setVisibility(View.GONE);
                    mLlCpImage.setVisibility(View.GONE);
                    mCpImage.innerStop();
                    mFragmentYouhuiLlNowife.setVisibility(VISIBLE);
                }
                break;
            case R.id.fragment_type_title:
                Intent intent = new Intent(getActivity(),
                        SearchWordActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(
                        R.anim.to_right, R.anim.to_left);
                break;
        }



        //开始线程
    }

    private class MyHandler extends Handler {
        private WeakReference<MainAcitivity1> mWeakReference = null;

        MyHandler(MainAcitivity1 activity) {
            mWeakReference = new WeakReference<MainAcitivity1>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            MainAcitivity1 mainActivity = mWeakReference.get();
            if (mainActivity == null) {
                return;
            }

            if (Build.VERSION.SDK_INT >= 17) {
                if (mainActivity == null || mainActivity.isDestroyed() || mainActivity.isFinishing()) {
                    return;
                }
            } else {
                if (mainActivity == null || mainActivity.isFinishing()) {

                    return;
                }
            }
            if (msg.what == 1) {
                try {
                    JSONObject json = new JSONObject((String) msg.obj);
                    String code = json.getString("code");
                    if (!(YouhuiFragment.goodsTypeList.size() > 1)) {
                        if (code.equals("0")) {
                            String data = json.getString("data");
                            if (data.equals("[]") || null == data || data.equals("null")) {
                                mLlHavawife.setVisibility(View.GONE);
                                mLlCpImage.setVisibility(View.GONE);
                                mCpImage.innerStop();
                                mFragmentYouhuiLlNowife.setVisibility(VISIBLE);
                            }
                            if (!(YouhuiFragment.goodsTypeList.size() >1)) {
                                goodsTypeList.clear();
                                try {
                                    goodsTypeList = ParseJsonCommon.parseJsonData(data,
                                            GuanjianciInfo.class);
                                } catch (Exception e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }

                            if (goodsTypeList.size() > 1) {
                                mLlHavawife.setVisibility(VISIBLE);
                                mLlCpImage.setVisibility(View.GONE);
                                mCpImage.innerStop();
                                mFragmentYouhuiLlNowife.setVisibility(View.GONE);
                                initPager();

                            } else {
                                mLlHavawife.setVisibility(View.GONE);
                                mLlCpImage.setVisibility(View.GONE);
                                mCpImage.innerStop();
                                mFragmentYouhuiLlNowife.setVisibility(VISIBLE);
                            }
                        } else {
                            mLlHavawife.setVisibility(View.GONE);
                            mLlCpImage.setVisibility(View.GONE);
                            mCpImage.innerStop();
                            mFragmentYouhuiLlNowife.setVisibility(VISIBLE);
                            Toast.makeText(getActivity(), json.getString("errorMsg"), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        mLlHavawife.setVisibility(VISIBLE);
                        mLlCpImage.setVisibility(View.GONE);
                        mCpImage.innerStop();
                        mFragmentYouhuiLlNowife.setVisibility(View.GONE);
                        initPager();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    mLlHavawife.setVisibility(View.GONE);
                    mLlCpImage.setVisibility(View.GONE);
                    mCpImage.innerStop();
                    mFragmentYouhuiLlNowife.setVisibility(VISIBLE);
                }


            }
            if (msg.what == 2) {
                mLlHavawife.setVisibility(View.GONE);
                mLlCpImage.setVisibility(View.GONE);
                mCpImage.innerStop();
                mFragmentYouhuiLlNowife.setVisibility(VISIBLE);
                Toast.makeText(getActivity(), "服务器错误！", Toast.LENGTH_LONG).show();

            }
            if (msg.what == 3) {
                mLlHavawife.setVisibility(VISIBLE);
                mLlCpImage.setVisibility(View.GONE);
                mCpImage.innerStop();
                mFragmentYouhuiLlNowife.setVisibility(View.GONE);
               initPager();
            }
            if (msg.what == 999) {
                try {
                    JSONObject json = new JSONObject((String) msg.obj);
                    String code = json.getString("code");
                    if (code.equals("0")) {
                        MainAcitivity1.allGoodsNumbers = json.getInt("data");
                        if (MainAcitivity1.allGoodsNumbers > 0) {
                            mTvNumber.setText("搜索商品,共" + MainAcitivity1.allGoodsNumbers + "款商品");
                        } else {
                            mTvNumber.setText("搜索商品");

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
