package com.yiqi.choose.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStripextends;
import com.yiqi.choose.R;
import com.yiqi.choose.activity.SearchYouhuiActivity;
import com.yiqi.choose.base.BaseFragment;
import com.yiqi.choose.base.BaseFragment1_coupons;
import com.yiqi.choose.factory.FragmentFactory;
import com.yiqi.choose.factory.ThreadPollFactory;
import com.yiqi.choose.model.categoryInfo;
import com.yiqi.choose.utils.AndroidUtils;
import com.yiqi.choose.utils.HttpConBase;
import com.yiqi.choose.utils.NetJudgeUtils;
import com.yiqi.choose.utils.ParseJsonCommon;
import com.yiqi.choose.utils.UrlUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: yaoyongchao
 * @date: 2016/3/28 15:18
 * @description:
 */
public class HomeFragment_new extends BaseFragment {

    LinearLayout fragment_goods_ll_search;

    private PagerSlidingTabStripextends mTabs;
    private ViewPager mViewPager;

    private RelativeLayout home_fragment_rl_retro;
    private LinearLayout home_fragment_nowife;
    private LinearLayout home_fragment_pagersliding;
    private TextView home_fragment_tv_retro;

    private List<Object> goodsTypeList;

    private MainFragmentStatePagerAdapter mMainFragmentStatePagerAdapter;
    //private EditText search_et_hint;

    /**
     * 标志位，标志已经初始化完成
     */
    private boolean homeisPrepared;
    /**
     * 是否已被加载过一次，第二次就不再去请求数据了
     */
    private boolean homeHasLoadedOnce;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      View view=inflater.inflate(R.layout.fragment_home, container, false);
        fragment_goods_ll_search=(LinearLayout) view.findViewById(R.id.fragment_goods_ll_search);
        mTabs = (PagerSlidingTabStripextends) view.findViewById(R.id.home_tabs);
        mViewPager = (ViewPager) view.findViewById(R.id.home_viewpager);
        home_fragment_rl_retro=(RelativeLayout)view.findViewById(R.id.home_fragment_rl_retro);
        home_fragment_nowife=(LinearLayout)view.findViewById(R.id.home_fragment_nowife);
        home_fragment_pagersliding=(LinearLayout)view.findViewById(R.id.home_fragment_pagersliding);
       home_fragment_tv_retro=(TextView)view.findViewById(R.id.home_fragment_tv_retro);
       // search_et_hint=(EditText)view.findViewById(search_et_hint);
        homeisPrepared=true;
        homeHasLoadedOnce=false;
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        search_et_hint.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(),
//                        SearchYouhuiActivity.class);
//                intent.putExtra("searchType","1");
//                startActivity(intent);
//                getActivity().overridePendingTransition(
//                        R.anim.to_right, R.anim.to_left);
//            }
//        });
        fragment_goods_ll_search.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(getActivity(),
                       SearchYouhuiActivity.class);
               intent.putExtra("searchType","1");
               startActivity(intent);
               getActivity().overridePendingTransition(
                       R.anim.to_right, R.anim.to_left);
           }
       });
        goodsTypeList= new ArrayList<Object>();

        home_fragment_tv_retro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取关键词
                if(NetJudgeUtils.getNetConnection(getActivity())){
                    home_fragment_pagersliding.setVisibility(View.GONE);
                    home_fragment_nowife.setVisibility(View.GONE);
                    home_fragment_rl_retro.setVisibility(View.VISIBLE);
                    ThreadPollFactory.getNormalPool().execute(new guanjianciThread());
                }else{
                    home_fragment_pagersliding.setVisibility(View.GONE);
                    home_fragment_nowife.setVisibility(View.VISIBLE);
                    home_fragment_rl_retro.setVisibility(View.GONE);
                }
            }
        });
        mTabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                categoryInfo info = (categoryInfo) goodsTypeList.get(position);
                BaseFragment1_coupons fragment = (BaseFragment1_coupons) FragmentFactory.getFragment(position, info.getId() + "");

                if (fragment != null) {
                    fragment.lazyLoad(info.getId() + "");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void lazyLoad(){

        //假如可见 且第一次 而且加载没有成功过
        if (!homeisPrepared || homeHasLoadedOnce) {
            return;
        }
        //获取关键词
        if(NetJudgeUtils.getNetConnection(getActivity())){
            home_fragment_pagersliding.setVisibility(View.GONE);
            home_fragment_nowife.setVisibility(View.GONE);
            home_fragment_rl_retro.setVisibility(View.VISIBLE);
            ThreadPollFactory.getNormalPool().execute(new guanjianciThread());
        }else{
            home_fragment_pagersliding.setVisibility(View.GONE);
            home_fragment_nowife.setVisibility(View.VISIBLE);
            home_fragment_rl_retro.setVisibility(View.GONE);
        }

    }

    /**
     * 的到圈值接口
     */
    private class guanjianciThread implements Runnable {

        @Override
        public void run() {
            try {
                String url = getResources().getString(R.string.appurl) + "/home/category"+"?stamp=" + UrlUtils.getTime() + "&encode=" + UrlUtils.getEncode();
                String jsonData = HttpConBase.sendGet(url);
                Message msg = Message.obtain();
                msg.what = 1;
                msg.obj = jsonData;
                hd.sendMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
                hd.sendEmptyMessage(2);
            }

        }
    }

    private Handler hd = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                try {
                    JSONObject json = new JSONObject((String) msg.obj);
                   String code = json.getString("code");
                    if (code.equals("0")) {
                        String data=json.getString("data");
                        if (data.equals("[]") || null == data || data.equals("null")) {
                            home_fragment_pagersliding.setVisibility(View.GONE);
                            home_fragment_nowife.setVisibility(View.VISIBLE);
                            home_fragment_rl_retro.setVisibility(View.GONE);

                        }
                        goodsTypeList.clear();
                        try {
                            goodsTypeList = ParseJsonCommon.parseJsonData(data,
                                    categoryInfo.class);
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        if (goodsTypeList.size() > 0) {
                            home_fragment_pagersliding.setVisibility(View.VISIBLE);
                            home_fragment_nowife.setVisibility(View.GONE);
                            home_fragment_rl_retro.setVisibility(View.GONE);
                            if (goodsTypeList.size() > 1) {
                                mViewPager.setOffscreenPageLimit(goodsTypeList
                                        .size() - 1);
                            } else {
                                mViewPager.setOffscreenPageLimit(1);
                            }

                            mMainFragmentStatePagerAdapter = new MainFragmentStatePagerAdapter(getActivity().getSupportFragmentManager());
                            // mViewPager.setOffscreenPageLimit(titles.length);
                            mViewPager.setAdapter(mMainFragmentStatePagerAdapter);
                            //  mTabs.setDefaultTabLayoutParams(540);
                            //        mViewPager.setCurrentItem(3);
                            mTabs.setBackgroundResource(R.color.white);
                            mTabs.setUnderlineHeight(AndroidUtils.dip2px(getActivity(),1));
                            mTabs.setUnderlineColor(getResources().getColor(R.color.background));
                            mTabs.setIndicatorHeight(8);
                            mTabs.setDividerColor(Color.TRANSPARENT);
                            mTabs.setViewPager(mViewPager);
                            homeHasLoadedOnce=true;

                        }else{
                            home_fragment_pagersliding.setVisibility(View.GONE);
                            home_fragment_nowife.setVisibility(View.VISIBLE);
                            home_fragment_rl_retro.setVisibility(View.GONE);
                        }
                    } else {
                        home_fragment_pagersliding.setVisibility(View.GONE);
                        home_fragment_nowife.setVisibility(View.VISIBLE);
                        home_fragment_rl_retro.setVisibility(View.GONE);
                        Toast.makeText(getActivity(),json.getString("errorMsg"), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
            if (msg.what == 2) {
                home_fragment_pagersliding.setVisibility(View.GONE);
                home_fragment_nowife.setVisibility(View.VISIBLE);
                home_fragment_rl_retro.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "服务器错误！", Toast.LENGTH_LONG).show();

            }
        }
    };
    class MainFragmentStatePagerAdapter extends FragmentStatePagerAdapter {
        public MainFragmentStatePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            categoryInfo info = (categoryInfo) goodsTypeList
                    .get(position);
            return FragmentFactory.getFragment(position, info.getId()+"");
        }

        @Override
        public int getCount() {
            if (goodsTypeList != null) {
                return goodsTypeList.size();
            }
            return 0;
        }



        @Override
        public CharSequence getPageTitle(int position) {
            return ((categoryInfo) goodsTypeList.get(position)).getName();

            //return titles[position];
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            //super.destroyItem(container, position, object);
        }
    }

}
