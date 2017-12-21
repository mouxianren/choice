package com.yiqi.choose.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.baichuan.android.trade.AlibcTrade;
import com.alibaba.baichuan.android.trade.model.AlibcShowParams;
import com.alibaba.baichuan.android.trade.model.OpenType;
import com.alibaba.baichuan.android.trade.page.AlibcBasePage;
import com.alibaba.baichuan.android.trade.page.AlibcMyCartsPage;
import com.alibaba.baichuan.trade.biz.core.taoke.AlibcTaokeParams;
import com.umeng.analytics.MobclickAgent;
import com.yiqi.choose.R;
import com.yiqi.choose.activity.SplashActicity1;
import com.yiqi.choose.alibabaCallback.DemoTradeCallback;
import com.yiqi.choose.base.BaseFragment;
import com.yiqi.choose.utils.JudgeLoginTaobao;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: yaoyongchao
 * @date: 2016/3/28 15:18
 * @description:
 */
public class CartFragment extends BaseFragment {
    private TextView cartfragment_tv_gocart;
    private AlibcShowParams alibcShowParams;//页面打开方式，默认，H5，Native
    private Map<String, String> exParams;//yhhpass参数
    private AlibcTaokeParams alibcTaokeParams = null;//淘客参数，包括pid，unionid，subPid

    private LinearLayout cart_fragment_nologin;
     private LinearLayout cart_fragment_login;
    private TextView cartfragment_tv_gocart_sucess;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_cart, container, false);
        cartfragment_tv_gocart=(TextView)view.findViewById(R.id.cartfragment_tv_gocart);
        cart_fragment_nologin=(LinearLayout)view.findViewById(R.id.cart_fragment_nologin);
        cart_fragment_login=(LinearLayout)view.findViewById(R.id.cart_fragment_nologin);
        cartfragment_tv_gocart_sucess=(TextView)view.findViewById(R.id.cartfragment_tv_gocart_sucess);
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            if(JudgeLoginTaobao.isLogin()){
                cart_fragment_login.setVisibility(View.VISIBLE);
                cart_fragment_nologin.setVisibility(View.GONE);
            }else{
                cart_fragment_login.setVisibility(View.GONE);
                cart_fragment_nologin.setVisibility(View.VISIBLE);
            }
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        alibcTaokeParams = new AlibcTaokeParams(SplashActicity1.mPId, "", "");
        alibcShowParams = new AlibcShowParams(OpenType.H5, false);
        exParams = new HashMap<>();
        exParams.put("isv_code", "appisvcode");
        exParams.put("alibaba", "阿里巴巴");//自定义参数部分，可任意增删改
        cartfragment_tv_gocart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             showCart();
            }
        });
        cartfragment_tv_gocart_sucess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCart();
            }
        });
    }





    /**
     *
     * 显示我的购物车
     */
    public void showCart() {
        AlibcBasePage alibcBasePage = new AlibcMyCartsPage();
        AlibcTrade.show(getActivity(), alibcBasePage, alibcShowParams, alibcTaokeParams, exParams, new DemoTradeCallback());
    }
    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("CartFragment");
        if(JudgeLoginTaobao.isLogin()){
            cart_fragment_login.setVisibility(View.VISIBLE);
            cart_fragment_nologin.setVisibility(View.GONE);
        }else{
            cart_fragment_login.setVisibility(View.GONE);
            cart_fragment_nologin.setVisibility(View.VISIBLE);
        }


    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("CartFragment");
    }

    @Override
    protected void lazyLoad(){

        //假如可见 且第一次 而且加载没有成功过

    }


}
