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
import com.umeng.analytics.MobclickAgent;
import com.yiqi.choose.R;
import com.yiqi.choose.activity.MainAcitivity1;
import com.yiqi.choose.activity.search.SearchWordActivity;
import com.yiqi.choose.base.BaseFragment;
import com.yiqi.choose.base.BaseFragment1_coupons;
import com.yiqi.choose.factory.FragmentFactoryYouhui;
import com.yiqi.choose.factory.ThreadPollFactory;
import com.yiqi.choose.model.GuanjianciInfo;
import com.yiqi.choose.thread.AllGoodsNumbersThread;
import com.yiqi.choose.thread.StatisticsTypeThread;
import com.yiqi.choose.utils.AndroidUtils;
import com.yiqi.choose.utils.BooleanThread;
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
public class YouhuiFragment extends BaseFragment {
private LinearLayout home_fragment_search;


    private LinearLayout fragment_youhui_ll_nogoods;
    private LinearLayout fragment_youhui_ll_nowife;
    private TextView fragment_youhui_bt_retryweb;
    private RelativeLayout fragment_youhui_rl_pb;


    private PagerSlidingTabStripextends mTabs;
    private ViewPager mViewPager;

    private TextView mTvNumber;


    public static List<Object> goodsTypeList;
    private LinearLayout home_fragment_pagersliding;
    private MainFragmentStatePagerAdapter mMainFragmentStatePagerAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_youhui_home, container, false);
        home_fragment_search=(LinearLayout) view.findViewById(R.id.home_fragment_search);
        mTabs=(PagerSlidingTabStripextends)view.findViewById(R.id.youhui_tabs);
        mViewPager=(ViewPager)view.findViewById(R.id.youhui_viewpager);
         fragment_youhui_ll_nogoods=(LinearLayout)view.findViewById(R.id.fragment_youhui_ll_nogoods);
         fragment_youhui_ll_nowife=(LinearLayout)view.findViewById(R.id.fragment_youhui_ll_nowife);
        fragment_youhui_bt_retryweb=(TextView)view.findViewById(R.id.home_fragment_tv_retro);;
        fragment_youhui_rl_pb=(RelativeLayout)view.findViewById(R.id.fragment_youhui_rl_pb);
        home_fragment_pagersliding=(LinearLayout)view.findViewById(R.id.home_fragment_pagersliding);
        mTvNumber=(TextView)view.findViewById(R.id.tv_allnumber);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        goodsTypeList= new ArrayList<Object>();
        home_fragment_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),
                        SearchWordActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(
                        R.anim.to_right, R.anim.to_left);
            }
        });

        //获取关键词
        if(NetJudgeUtils.getNetConnection(getActivity())){
            home_fragment_pagersliding.setVisibility(View.GONE);
            fragment_youhui_rl_pb.setVisibility(View.VISIBLE);
            fragment_youhui_ll_nowife.setVisibility(View.GONE);
            fragment_youhui_ll_nogoods.setVisibility(View.GONE);
            ThreadPollFactory.getNormalPool().execute(new guanjianciThread());
        }else{
            fragment_youhui_rl_pb.setVisibility(View.GONE);
            fragment_youhui_ll_nowife.setVisibility(View.VISIBLE);
            fragment_youhui_ll_nogoods.setVisibility(View.GONE);
            home_fragment_pagersliding.setVisibility(View.GONE);
        }
        fragment_youhui_bt_retryweb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetJudgeUtils.getNetConnection(getActivity())) {
                    home_fragment_pagersliding.setVisibility(View.GONE);
                    fragment_youhui_rl_pb.setVisibility(View.VISIBLE);
                    fragment_youhui_ll_nowife.setVisibility(View.GONE);
                    fragment_youhui_ll_nogoods.setVisibility(View.GONE);

                    ThreadPollFactory.getNormalPool().execute(new guanjianciThread());
                } else {
                    home_fragment_pagersliding.setVisibility(View.GONE);
                    fragment_youhui_rl_pb.setVisibility(View.GONE);
                    fragment_youhui_ll_nowife.setVisibility(View.VISIBLE);
                    fragment_youhui_ll_nogoods.setVisibility(View.GONE);

                }
            }
        });
        mTabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                GuanjianciInfo info = (GuanjianciInfo) goodsTypeList.get(position);
                BaseFragment1_coupons fragment = (BaseFragment1_coupons) FragmentFactoryYouhui.getFragment(position, info.getId() + "");
                if(BooleanThread.toService){
                    if (NetJudgeUtils.getNetConnection(getActivity())){
                        ThreadPollFactory.getNormalPool().execute(new StatisticsTypeThread(getActivity(),info.getId(),info.getName()));
                    }
                }
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
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("YouhuiFragment");
        if(MainAcitivity1.allGoodsNumbers>0){
            mTvNumber.setText("搜索商品,共"+MainAcitivity1.allGoodsNumbers+"款商品");
        }else{
            mTvNumber.setText("搜索商品");

        }
        if(NetJudgeUtils.getNetConnection(getActivity())||getUserVisibleHint()){
            ThreadPollFactory.getNormalPool().execute(new AllGoodsNumbersThread(hd,getActivity()));
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("YouhuiFragment");
    }
    @Override
    protected void lazyLoad(){

//        if(NetJudgeUtils.getNetConnection(getActivity())){
//           home_search_rl.setVisibility(View.GONE);
//            fragment_youhui_ll_nogoods.setVisibility(View.GONE);
//            fragment_youhui_ll_nowife.setVisibility(View.GONE);
//            fragment_youhui_rl_pb.setVisibility(View.VISIBLE);
//            ThreadPollFactory.getNormalPool().execute(new goodsThread(SplashActicity1.mQuanId, "",1,""));
//
//        }else{
//           home_search_rl.setVisibility(View.GONE);
//            fragment_youhui_ll_nogoods.setVisibility(View.GONE);
//            fragment_youhui_ll_nowife.setVisibility(View.VISIBLE);
//            fragment_youhui_rl_pb.setVisibility(View.GONE);
//        }
    }

    /**
     * 的到圈值接口
     */
    private class guanjianciThread implements Runnable {

        @Override
        public void run() {
            try {
                String url = getResources().getString(R.string.appurltest) + "/home/category2"+"?stamp=" + UrlUtils.getTime() + "&encode=" + UrlUtils.getEncode();
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
                            fragment_youhui_rl_pb.setVisibility(View.GONE);
                            fragment_youhui_ll_nowife.setVisibility(View.VISIBLE);
                            fragment_youhui_ll_nogoods.setVisibility(View.GONE);
                        }

                        if(!(goodsTypeList.size()>0)){
                            goodsTypeList.clear();
                            try {
                                goodsTypeList = ParseJsonCommon.parseJsonData(data,
                                        GuanjianciInfo.class);


//       goodsTypeList.addAll(goodsTypeList);
//      goodsTypeList.addAll(goodsTypeList);
//      goodsTypeList.addAll(goodsTypeList);
//        goodsTypeList.addAll(goodsTypeList);


                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }


                        if (goodsTypeList.size() > 0) {
                            home_fragment_pagersliding.setVisibility(View.VISIBLE);
                            fragment_youhui_rl_pb.setVisibility(View.GONE);
                            fragment_youhui_ll_nowife.setVisibility(View.GONE);
                            fragment_youhui_ll_nogoods.setVisibility(View.GONE);
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

                        }else{
                            home_fragment_pagersliding.setVisibility(View.GONE);
                            fragment_youhui_rl_pb.setVisibility(View.GONE);
                            fragment_youhui_ll_nowife.setVisibility(View.VISIBLE);
                            fragment_youhui_ll_nogoods.setVisibility(View.GONE);
                        }
                    } else {
                        home_fragment_pagersliding.setVisibility(View.GONE);
                        fragment_youhui_rl_pb.setVisibility(View.GONE);
                        fragment_youhui_ll_nowife.setVisibility(View.VISIBLE);
                        fragment_youhui_ll_nogoods.setVisibility(View.GONE);
                        Toast.makeText(getActivity(),json.getString("errorMsg"), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    home_fragment_pagersliding.setVisibility(View.GONE);
                    fragment_youhui_rl_pb.setVisibility(View.GONE);
                    fragment_youhui_ll_nowife.setVisibility(View.VISIBLE);
                    fragment_youhui_ll_nogoods.setVisibility(View.GONE);
                }


            }
            if (msg.what == 2) {
                home_fragment_pagersliding.setVisibility(View.GONE);
                fragment_youhui_rl_pb.setVisibility(View.GONE);
                fragment_youhui_ll_nowife.setVisibility(View.VISIBLE);
                fragment_youhui_ll_nogoods.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "服务器错误！", Toast.LENGTH_LONG).show();

            }
            if(msg.what==999){
                try {
                    JSONObject json = new JSONObject((String) msg.obj);
                    String code = json.getString("code");
                    if (code.equals("0")) {
                        MainAcitivity1.allGoodsNumbers=json.getInt("data");
                        if(MainAcitivity1.allGoodsNumbers>0){
                            mTvNumber.setText("搜索商品,共"+MainAcitivity1.allGoodsNumbers+"款商品");
                        }else{
                            mTvNumber.setText("搜索商品");

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };


    class MainFragmentStatePagerAdapter extends FragmentStatePagerAdapter {
        public MainFragmentStatePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            GuanjianciInfo info = (GuanjianciInfo) goodsTypeList
                    .get(position);
            BaseFragment1_coupons fragment=FragmentFactoryYouhui.getFragment(position, info.getId()+"");
            Bundle bundle = new Bundle();
            String str = info.getId();
            bundle.putString("typeId", str);
            bundle.putString("keyword",info.getName());
            if(null!=info.getCategorys()){
                bundle.putInt("ziTypeNumber",info.getCategorys().size());
            }else{
                bundle.putInt("ziTypeNumber",0);
            }
            bundle.putInt("position",position);

            fragment.setArguments(bundle);
            return fragment;
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
            return ((GuanjianciInfo) goodsTypeList.get(position)).getName();

            //return titles[position];
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            //super.destroyItem(container, position, object);
        }
    }


}
