package com.yiqi.choose;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ali.auth.third.ui.context.CallbackContext;
import com.alibaba.baichuan.android.trade.AlibcTrade;
import com.alibaba.baichuan.android.trade.AlibcTradeSDK;
import com.alibaba.baichuan.android.trade.model.AlibcShowParams;
import com.alibaba.baichuan.android.trade.model.OpenType;
import com.alibaba.baichuan.android.trade.page.AlibcBasePage;
import com.alibaba.baichuan.android.trade.page.AlibcMyCartsPage;
import com.alibaba.baichuan.android.trade.page.AlibcMyOrdersPage;
import com.alibaba.baichuan.android.trade.page.AlibcPage;
import com.alibaba.baichuan.trade.biz.core.taoke.AlibcTaokeParams;
import com.alibaba.baichuan.trade.biz.login.AlibcLogin;
import com.alibaba.baichuan.trade.biz.login.AlibcLoginCallback;
import com.umeng.analytics.MobclickAgent;
import com.yiqi.choose.alibabaCallback.DemoTradeCallback;

import java.util.HashMap;
import java.util.Map;

public class MainAA extends AppCompatActivity {
    public static  final String TAG="MainActivity";
    private Button bt_login;
    private Button bt_dingdan;
    private Button bt_card;
    private Button bt_details;
    private Button bt_youhui;
    private AlibcShowParams alibcShowParams;//页面打开方式，默认，H5，Native
    private Map<String, String> exParams;//yhhpass参数
    private AlibcTaokeParams alibcTaokeParams = null;//淘客参数，包括pid，unionid，subPid
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //        PushManager.getInstance().initialize(this.getApplicationContext(),DemoPushService.class);
        //        PushManager.getInstance().registerPushIntentService(this.getApplicationContext(),DemoIntentService.class);
        //        System.out.println("clientid="+PushManager.getInstance().getClientid(MainActivity.this));
        alibcTaokeParams = new AlibcTaokeParams("mm_124863698_0_0", "", "");
        alibcShowParams = new AlibcShowParams(OpenType.H5, false);

        exParams = new HashMap<>();
        exParams.put("isv_code", "appisvcode");
        exParams.put("alibaba", "阿里巴巴");//自定义参数部分，可任意增删改

        bt_youhui=(Button)this.findViewById(R.id.bt_youhui);
        bt_login=(Button)this.findViewById(R.id.bt_login);
        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        bt_details=(Button)this.findViewById(R.id.bt_details);
        bt_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUrl("https://s.click.taobao.com/rQyiBew");
            }
        });

        bt_dingdan=(Button)this.findViewById(R.id.bt_dingdan);
        bt_dingdan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkOrder();
            }
        });
        bt_card=(Button)this.findViewById(R.id.bt_card);
        bt_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCart();
            }
        });
        bt_youhui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUrl("https://taoquan.taobao.com/coupon/unify_apply.htm?sellerId=3315893032&activityId=a5816ef978c54b138164f6767a4e4933");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(MainAA.this);

        //System.out.println(AlibcLogin.getInstance().getSession().toString().length());
        //判断是否登录
        if((null==AlibcLogin.getInstance().getSession()||"null".equals(AlibcLogin.getInstance().getSession())||"".equals(AlibcLogin.getInstance().getSession()))||(null==AlibcLogin.getInstance().getSession().openId||"null".equals(AlibcLogin.getInstance().getSession().openId)||"".equals(AlibcLogin.getInstance().getSession().openId))){
        }

        //      if(null==AlibcLogin.getInstance().getSession().openId||AlibcLogin.getInstance().getSession().openId.equals("")||AlibcLogin.getInstance().getSession().openId.equals("null")){
        //          System.out.println("未登录");
        //      }
        //        System.out.println(AlibcLogin.getInstance().getSession().nick+"");
        //        System.out.println(AlibcLogin.getInstance().getSession().avatarUrl+"");
        //        System.out.println(AlibcLogin.getInstance().getSession().openId+"");
        //        System.out.println(AlibcLogin.getInstance().getSession().openSid+"");
    }

    public void login() {
        AlibcLogin alibcLogin = AlibcLogin.getInstance();

        alibcLogin.showLogin(new AlibcLoginCallback() {
            @Override
            public void onSuccess(int i) {
                Toast.makeText(MainAA.this, "登录成功 ",
                        Toast.LENGTH_LONG).show();
                Log.i(TAG, "获取淘宝用户信息: "+AlibcLogin.getInstance().getSession());
                // nick = 牟仙仁, ava = https://wwc.alicdn.com/avatar/getAvatar.do?userId=137215469&width=160&height=160&type=sns , openId=AAFVx_qiAFMnOt5ikrvl3AF9, openSid=69e19bc18cd073ef7135c5b87b3a651ae6bc6e934cbeb27d54909213be47889122089da0b47377c335d1d4d038942d80


            }

            @Override
            public void onFailure(int code, String msg) {
                Toast.makeText(MainAA.this, "登录失败 ",
                        Toast.LENGTH_LONG).show();
            }
        });


    }
    /**
     *
     * 打开指定链接
     */
    public void showUrl(String text) {
        if(TextUtils.isEmpty(text)) {
            Toast.makeText(MainAA.this, "URL为空",
                    Toast.LENGTH_SHORT).show();
            return;
        };

        AlibcTrade.show(this, new AlibcPage(text), alibcShowParams,alibcTaokeParams, exParams , new DemoTradeCallback());
    }
    /**
     *
     * 显示我的购物车
     */
    public void showCart() {

        AlibcBasePage alibcBasePage = new AlibcMyCartsPage();
        AlibcTrade.show(this, alibcBasePage, alibcShowParams, alibcTaokeParams, exParams, new DemoTradeCallback());
    }
    public void checkOrder(){
        /**
         * 我的订单页面
         *
         * @param status   默认跳转页面；填写：0：全部；1：待付款；2：待发货；3：待收货；4：待评价
         * @param allOrder false 进行订单分域（只展示通过当前app下单的订单），true 显示所有订单
         */
        AlibcBasePage alibcBasePage = new AlibcMyOrdersPage(0, true);
        AlibcTrade.show(this, alibcBasePage, alibcShowParams, alibcTaokeParams, exParams, new DemoTradeCallback());
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(MainAA.this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AlibcTradeSDK.destory();
    }

    //登录须重写onActivityResult方法
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        CallbackContext.onActivityResult(requestCode, resultCode, data);
    }
}
