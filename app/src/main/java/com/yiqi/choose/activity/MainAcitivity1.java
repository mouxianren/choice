package com.yiqi.choose.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.baichuan.android.trade.AlibcTrade;
import com.alibaba.baichuan.android.trade.AlibcTradeSDK;
import com.alibaba.baichuan.android.trade.model.AlibcShowParams;
import com.alibaba.baichuan.android.trade.model.OpenType;
import com.alibaba.baichuan.android.trade.page.AlibcBasePage;
import com.alibaba.baichuan.android.trade.page.AlibcMyCartsPage;
import com.alibaba.baichuan.android.trade.page.AlibcPage;
import com.alibaba.baichuan.trade.biz.core.taoke.AlibcTaokeParams;
import com.alibaba.baichuan.trade.biz.login.AlibcLogin;
import com.igexin.sdk.PushManager;
import com.umeng.analytics.MobclickAgent;
import com.yiqi.choose.R;
import com.yiqi.choose.alibabaCallback.DemoTradeCallback;
import com.yiqi.choose.factory.ThreadPollFactory;
import com.yiqi.choose.fragment.CartFragment;
import com.yiqi.choose.fragment.ShareIncomeFragment;
import com.yiqi.choose.fragment.TypeFragment;
import com.yiqi.choose.fragment.WodeFragmentNew;
import com.yiqi.choose.fragment.YouhuiFragment;
import com.yiqi.choose.thread.StatisticsShareThread;
import com.yiqi.choose.thread.StatisticsThread;
import com.yiqi.choose.utils.AndroidUtils;
import com.yiqi.choose.utils.BooleanThread;
import com.yiqi.choose.utils.HttpConBase;
import com.yiqi.choose.utils.JudgeLoginTaobao;
import com.yiqi.choose.utils.NetJudgeUtils;
import com.yiqi.choose.utils.SharedPfUtils;
import com.yiqi.choose.utils.SystemBarTintManager;
import com.yiqi.choose.utils.UrlUtils;
import com.yiqi.choose.view.NoScrollViewPager;

import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static com.yiqi.choose.base.BaseMap.app_version;

/**
 * Created by moumou on 17/8/10.
 */

public class MainAcitivity1 extends FragmentActivity {
    protected   MainAcitivity1 maActivity1;
    private   TabLayout mTabLayout;
    //Tab 文字
    private final int[] TAB_TITLES = new int[]{R.string.shouye, R.string.type_item, R.string.cart, R.string.me};

    //Tab 文字
    private final int[] TAB_TITLES_SHARE = new int[]{R.string.shouye, R.string.type_item, R.string.shareincom, R.string.me};
    //Tab 图片
    private final int[] TAB_IMGS = new int[]{R.mipmap.home_sel, R.mipmap.type_unsel, R.mipmap.cart, R.mipmap.me};

    //Tab 图片
    private final int[] TAB_IMGS_SHARE = new int[]{R.drawable.tab_home_selector, R.drawable.tab_type_selector, R.drawable.tab_shareincome_selector, R.drawable.tab_wode_selector};

    //Fragment 数组
    private final Fragment[] TAB_FRAGMENTS = new Fragment[]{new YouhuiFragment(), new TypeFragment(), new CartFragment(), new WodeFragmentNew()};
    //Fragment 数组
    private final Fragment[] TAB_FRAGMENTS_SHARE = new Fragment[]{new YouhuiFragment(), new TypeFragment(), new ShareIncomeFragment(), new WodeFragmentNew()};


    //Tab 数目
    private final int COUNT = TAB_TITLES.length;
    private MyViewPagerAdapter mAdapter;
    private NoScrollViewPager mViewPager;

    private int unclickPostion = 0;
    /**
     * 双击退出函数
     */
    private static Boolean isExit = false;
    private AlibcShowParams alibcShowParams;//页面打开方式，默认，H5，Native
    private Map<String, String> exParams;//yhhpass参数
    private AlibcTaokeParams alibcTaokeParams = null;//淘客参数，包括pid，unionid，subPid

    public static int choosePosition;
    public static int allGoodsNumbers=0;

    private boolean splashInvite=false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.topcolorred);//通知栏所需颜色
        }
        keepFontSize();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        splashInvite=SplashActicity1.isInvite;


        maActivity1=MainAcitivity1.this;
        initViews();


    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }
//    private int getStatusBarHeight(Context context) {
//        int result = 0;
//        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
//        if (resourceId > 0) {
//            result = context.getResources().getDimensionPixelSize(resourceId);
//        }
//        return result;
//    }
    private void keepFontSize(){
        Resources res = getResources();
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
    }
    private void initViews() {
        alibcTaokeParams = new AlibcTaokeParams(SplashActicity1.mPId, "", "");
        alibcShowParams = new AlibcShowParams(OpenType.Native, false);
        alibcShowParams.setClientType("taobao_scheme");

        exParams = new HashMap<>();
        exParams.put("isv_code", "appisvcode");
        exParams.put("alibaba", "阿里巴巴");//自定义参数部分，可任意增删改


        mTabLayout = (TabLayout) findViewById(R.id.mainactivity_tablayout);
        if(!splashInvite){
            setTabs(mTabLayout, this.getLayoutInflater(), TAB_TITLES, TAB_IMGS, 0);
        }else{
            setTabs(mTabLayout, this.getLayoutInflater(), TAB_TITLES_SHARE, TAB_IMGS_SHARE, 0);
        }

        choosePosition=0;
        mAdapter = new MyViewPagerAdapter(getSupportFragmentManager());
        mViewPager = (NoScrollViewPager) findViewById(R.id.mainactivity_viewpager);
        mViewPager.setAdapter(mAdapter);

        mViewPager.setOffscreenPageLimit(4);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));


        mTabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
        if (SharedPfUtils.getData(MainAcitivity1.this, "xiaoxi", "").equals("2")) {
            PushManager.getInstance().turnOffPush(MainAcitivity1.this);
        } else {
            PushManager.getInstance().turnOnPush(MainAcitivity1.this);
        }

    }

    /**
     * A {@link TabLayout.OnTabSelectedListener} class which contains the necessary calls back
     * to the provided {@link ViewPager} so that the tab position is kept in sync.
     */
    public class ViewPagerOnTabSelectedListener implements TabLayout.OnTabSelectedListener {
        private final ViewPager mViewPager;

        public ViewPagerOnTabSelectedListener(ViewPager viewPager) {
            mViewPager = viewPager;
        }

        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            if(!splashInvite){
                choosePosition=tab.getPosition();
                //这里使用到反射，拿到Tab对象后获取Class
                Class c = tab.getClass();
                try {
                    //Filed “字段、属性”的意思，c.getDeclaredField 获取私有属性。
                    //“mView”是Tab的私有属性名称，类型是 TabView ，TabLayout私有内部类。
                    Field field = c.getDeclaredField("mView");
                    field.setAccessible(true);
                    final View view = (View) field.get(tab);
                    int number = ((ViewGroup) view).getChildCount();
                    for (int i = number - 1; i >= 0; i--) {
                        if (((ViewGroup) view).getChildAt(i) instanceof FrameLayout) {
                            int numberzi = ((FrameLayout) ((ViewGroup) view).getChildAt(i)).getChildCount();
                            for (int j = 0; j < numberzi; j++) {
                                if (((FrameLayout) ((ViewGroup) view).getChildAt(i)).getChildAt(j) instanceof ImageView) {
                                    if (tab.getPosition() == 0) {
                                        ((ImageView) ((FrameLayout) ((ViewGroup) view).getChildAt(i)).getChildAt(j)).setImageResource(R.mipmap.home_sel);

                                    } else if (tab.getPosition() == 1) {
                                        ((ImageView) ((FrameLayout) ((ViewGroup) view).getChildAt(i)).getChildAt(j)).setImageResource(R.mipmap.type_sel);
                                    } else if (tab.getPosition() == 2) {
                                        //假如未登录
                                        //mViewPager.setCurrentItem(tab.getPosition());
                                        if (!JudgeLoginTaobao.isLogin()) {
                                            ((ImageView) ((FrameLayout) ((ViewGroup) view).getChildAt(i)).getChildAt(j)).setImageResource(R.mipmap.cart_sel);
                                        } else {
                                            TabLayout.Tab tabunSelect = mTabLayout.getTabAt(unclickPostion);
                                            Class unc = tabunSelect.getClass();
                                            try {
                                                //Filed “字段、属性”的意思，c.getDeclaredField 获取私有属性。
                                                //“mView”是Tab的私有属性名称，类型是 TabView ，TabLayout私有内部类。
                                                Field unfield = unc.getDeclaredField("mView");
                                                unfield.setAccessible(true);
                                                final View unview = (View) field.get(tabunSelect);
                                                int unnumber = ((ViewGroup) unview).getChildCount();
                                                for (int uni = unnumber - 1; uni >= 0; uni--) {
                                                    if (((ViewGroup) unview).getChildAt(uni) instanceof FrameLayout) {
                                                        int unnumberzi = ((FrameLayout) ((ViewGroup) unview).getChildAt(uni)).getChildCount();
                                                        for (int unj = 0; unj < unnumberzi; unj++) {
                                                            if (((FrameLayout) ((ViewGroup) unview).getChildAt(uni)).getChildAt(unj) instanceof ImageView) {
                                                                if (unclickPostion == 0) {
                                                                    ((ImageView) ((FrameLayout) ((ViewGroup) unview).getChildAt(i)).getChildAt(j)).setImageResource(R.mipmap.home_sel);
                                                                } else if (unclickPostion == 1) {
                                                                    ((ImageView) ((FrameLayout) ((ViewGroup) unview).getChildAt(i)).getChildAt(j)).setImageResource(R.mipmap.youhui_sel);
                                                                } else if (unclickPostion == 3) {
                                                                    ((ImageView) ((FrameLayout) ((ViewGroup) unview).getChildAt(i)).getChildAt(j)).setImageResource(R.mipmap.me_sel);
                                                                }


                                                                break;
                                                            }
                                                        }
                                                        break;
                                                    }
                                                }
                                            } catch (NoSuchFieldException e) {
                                                e.printStackTrace();
                                            } catch (IllegalAccessException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        //yidenglu
                                        //跳到购物车界面
                                    } else if (tab.getPosition() == 3) {
                                        ((ImageView) ((FrameLayout) ((ViewGroup) view).getChildAt(i)).getChildAt(j)).setImageResource(R.mipmap.me_sel);
                                    }

                                    break;
                                }
                            }
                            break;
                        }
                    }
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

                if (tab.getPosition() == 2 && JudgeLoginTaobao.isLogin()) {
                    //tiaozhuandao gouwuche
                    // mViewPager.setCurrentItem(unclickPostion);
                    mTabLayout.getTabAt(unclickPostion).select();

                    showCart();

                } else {
                    mViewPager.setCurrentItem(tab.getPosition());
                }
            }else{
                mViewPager.setCurrentItem(tab.getPosition());
            }
            if(tab.getPosition() == 1){
                if(BooleanThread.toService){
                    if (NetJudgeUtils.getNetConnection(MainAcitivity1.this)){

                        ThreadPollFactory.getNormalPool().execute(new StatisticsThread(MainAcitivity1.this,7));
                    }
                }
            }
          if(tab.getPosition()==2){
              if(splashInvite){
                  if(BooleanThread.toService){
                      if (NetJudgeUtils.getNetConnection(MainAcitivity1.this)){
                          String rCode="";
                          if(!TextUtils.isEmpty((String)SharedPfUtils.getData(MainAcitivity1.this,"newQuanId",""))){
                              rCode=(String)SharedPfUtils.getData(MainAcitivity1.this,"newQuanId","");
                          }
                          ThreadPollFactory.getNormalPool().execute(new StatisticsShareThread(MainAcitivity1.this,1,rCode));
                      }
                  }
              }
          }


        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {
            //根据已选中是否为购物车来做判断 除了购物车且已登陆状态要变 其他都是不变
            //这里使用到反射，拿到Tab对象后获取Class
            //根据已选中是否为购物车来做判断 除了购物车且已登陆状态要变 其他都是不变
            //这里使用到反射，拿到Tab对象后获取Class
            if(!splashInvite){
                unclickPostion = tab.getPosition();
                Class c = tab.getClass();
                try {
                    //Filed “字段、属性”的意思，c.getDeclaredField 获取私有属性。
                    //“mView”是Tab的私有属性名称，类型是 TabView ，TabLayout私有内部类。
                    Field field = c.getDeclaredField("mView");
                    field.setAccessible(true);
                    final View view = (View) field.get(tab);
                    int number = ((ViewGroup) view).getChildCount();
                    for (int i = number - 1; i >= 0; i--) {
                        if (((ViewGroup) view).getChildAt(i) instanceof FrameLayout) {
                            int numberzi = ((FrameLayout) ((ViewGroup) view).getChildAt(i)).getChildCount();
                            for (int j = 0; j < numberzi; j++) {
                                if (((FrameLayout) ((ViewGroup) view).getChildAt(i)).getChildAt(j) instanceof ImageView) {

                                    if (tab.getPosition() == 0) {
                                        ((ImageView) ((FrameLayout) ((ViewGroup) view).getChildAt(i)).getChildAt(j)).setImageResource(R.mipmap.home);

                                    } else if (tab.getPosition() == 1) {
                                        ((ImageView) ((FrameLayout) ((ViewGroup) view).getChildAt(i)).getChildAt(j)).setImageResource(R.mipmap.type_unsel);
                                    } else if (tab.getPosition() == 2) {
                                        //假如未登录
                                        //mViewPager.setCurrentItem(tab.getPosition());

                                        ((ImageView) ((FrameLayout) ((ViewGroup) view).getChildAt(i)).getChildAt(j)).setImageResource(R.mipmap.cart);

                                        //yidenglu
                                        //跳到购物车界面
                                    } else if (tab.getPosition() == 3) {
                                        ((ImageView) ((FrameLayout) ((ViewGroup) view).getChildAt(i)).getChildAt(j)).setImageResource(R.mipmap.me);
                                    }

                                    break;
                                }
                            }
                            break;
                        }
                    }
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {
            if(!splashInvite){
                if (JudgeLoginTaobao.isLogin() && tab.getPosition() == 2) {
                    showCart();
                }
            }

        }
    }

    /**
     * @description: 设置添加Tab
     */
    private void setTabs(TabLayout tabLayout, LayoutInflater inflater, int[] tabTitlees, int[] tabImgs, int number) {
        tabLayout.removeAllTabs();
        for (int i = 0; i < tabImgs.length; i++) {
            TabLayout.Tab tab = tabLayout.newTab();

            //            ImageView iv=new ImageView(MainAcitivity.this);
            //            iv.setScaleType(ImageView.ScaleType.FIT_XY);
            View view = inflater.inflate(R.layout.main_activity_tabcustom, null);
            tab.setCustomView(view);
            //           iv.setImageResource(tabImgs[i]);
            //            TextView tvTitle = (TextView) view.findViewById(R.id.tv_tab);
            //            tvTitle.setText(tabTitlees[i]);
            ImageView imgTab = (ImageView) view.findViewById(R.id.img_tab);

                imgTab.setImageResource(tabImgs[i]);





            if (number == i) {
                tabLayout.addTab(tab, true);
            } else {
                tabLayout.addTab(tab, false);
            }


        }


    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("MainActivity1");
        MobclickAgent.onResume(this);

        if(mTabLayout.getSelectedTabPosition()==0||mTabLayout.getSelectedTabPosition()==1){
//执行查找数字
        }

        if(!TextUtils.isEmpty(SplashActicity1.GTgoodsId)){
            if(NetJudgeUtils.getNetConnection(MainAcitivity1.this)){
                ThreadPollFactory.getNormalPool().execute(new TurnUrlThread(SplashActicity1.GTgoodsId, MainAcitivity1.this));
            }
            SplashActicity1.GTgoodsId="";
        }


        if(SplashActicity1.isGTSent){

            //SplashActicity1.isGTSent=false;
            if (NetJudgeUtils.getNetConnection(MainAcitivity1.this)) {
                if (!TextUtils.isEmpty(PushManager.getInstance().getClientid(MainAcitivity1.this))) {
                    ThreadPollFactory.getNormalPool().execute(new sendGetui());
                }
            }
        }else{

            if (SharedPfUtils.getData(MainAcitivity1.this, "sendgetui", "").equals("3")) {
                //假如保存的不一样 发送
                if (!TextUtils.isEmpty(PushManager.getInstance().getClientid(MainAcitivity1.this))) {
                    if (!PushManager.getInstance().getClientid(MainAcitivity1.this).equals(SharedPfUtils.getData(MainAcitivity1.this, "gettuitoken", ""))) {
                        if (NetJudgeUtils.getNetConnection(MainAcitivity1.this)) {
                            ThreadPollFactory.getNormalPool().execute(new sendGetui());
                        }
                    }
                }
            }
            if (SharedPfUtils.getData(MainAcitivity1.this, "sendgetui", "").equals("")) {
                if (!TextUtils.isEmpty(PushManager.getInstance().getClientid(MainAcitivity1.this))) {
                    //发送个推值
                    if (NetJudgeUtils.getNetConnection(MainAcitivity1.this)) {
                        ThreadPollFactory.getNormalPool().execute(new sendGetui());

                    }

                }
            }
        }




            //假如保存的不一样 发送
            if (JudgeLoginTaobao.isLogin()){
                if(!TextUtils.isEmpty(PushManager.getInstance().getClientid(MainAcitivity1.this))){
                   if(!SharedPfUtils.getData(MainAcitivity1.this,"mainlogintaobaoId","").equals(AlibcLogin.getInstance().getSession().openId)
                           ||!(PushManager.getInstance().getClientid(MainAcitivity1.this).equals(SharedPfUtils.getData(MainAcitivity1.this, "maintoken", "")))){
                    if (NetJudgeUtils.getNetConnection(MainAcitivity1.this)) {
                      ThreadPollFactory.getNormalPool().execute(new sendTaobao());
                    }
                }
                }
            }


//        if(JudgeLoginTaobao.isLogin()&&mTabLayout.getSelectedTabPosition()!=3){
//            if(SharedPfUtils.getData(MainAcitivity1.this, "logintaobao", "").equals("")){
//                if (NetJudgeUtils.getNetConnection(MainAcitivity1.this)) {
//                    ThreadPollFactory.getNormalPool().execute(new sendTaobao());
//
//                }
//            }
//        }


    }
    /**
     * 的到圈值接口
     */
    private class sendTaobao implements Runnable {
        @Override
        public void run() {
            try {
                Map<String, String> params = new HashMap<>();
                params.put("code", SplashActicity1.mQuanId);
                params.put("client_id", SplashActicity1.client_id);
                params.put("device", "2");
                params.put("channel", AndroidUtils.getAppMetaData(MainAcitivity1.this, "UMENG_CHANNEL"));
                params.put("app_version", AndroidUtils.getAppVersion(MainAcitivity1.this));
                params.put("api_version", "1.0");
                params.put("token", PushManager.getInstance().getClientid(MainAcitivity1.this));


                params.put("buyerToken",AlibcLogin.getInstance().getSession().openSid);
                params.put("buyAntor",AlibcLogin.getInstance().getSession().avatarUrl);
                params.put("buyerName",AlibcLogin.getInstance().getSession().nick);
                params.put("buyerId",AlibcLogin.getInstance().getSession().openId);

                String url = getResources().getString(R.string.appurl) + "/buyer/login"+"?stamp=" + UrlUtils.getTime() + "&encode=" + UrlUtils.getEncode();
                String jsonData = HttpConBase.getjsonByPost(url, params, "utf-8");
                AndroidUtils.printlnMap(params);
                Message msg = Message.obtain();
                msg.what = 2;
                msg.obj = jsonData;
                hd.sendMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();

            }

        }
    }
    /**
     * 的到圈值接口
     */
    private class sendGetui implements Runnable {
        @Override
        public void run() {
            try {
                Map<String, String> params = new HashMap<>();
                params.put("code", SplashActicity1.mQuanId);
                params.put("client_id", SplashActicity1.client_id);
                params.put("device", "2");
                params.put("channel", AndroidUtils.getAppMetaData(MainAcitivity1.this, "UMENG_CHANNEL"));
                params.put("app_version", AndroidUtils.getAppVersion(MainAcitivity1.this));
                params.put("api_version", "1.0");
                params.put("token", PushManager.getInstance().getClientid(MainAcitivity1.this));
                String url = getResources().getString(R.string.appurl) + "/home/base"+"?stamp=" + UrlUtils.getTime() + "&encode=" + UrlUtils.getEncode();
                String jsonData = HttpConBase.getjsonByPost(url, params, "utf-8");
                Message msg = Message.obtain();
                msg.what = 1;
                msg.obj = jsonData;
                hd.sendMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();

            }

        }
    }

    private Handler hd = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                try {
                    JSONObject j = new JSONObject((String) msg.obj);
                    //System.out.println("j==="+(String) msg.obj);

                    String code = j.getString("code");
                    if (code.equals("0")) {
                        SplashActicity1.isGTSent=false;
                      //  Toast.makeText(MainAcitivity1.this, "上传个推值成功！！！！！！！", Toast.LENGTH_SHORT).show();


                       // Toast.makeText(MainAcitivity1.this,"code="+SplashActicity1.mQuanId+"  个推＝"+PushManager.getInstance().getClientid(MainAcitivity1.this),Toast.LENGTH_LONG).show();


                        SharedPfUtils.saveStringData(MainAcitivity1.this, "sendgetui", "3");
                        SharedPfUtils.saveStringData(MainAcitivity1.this, "gettuitoken", PushManager.getInstance().getClientid(MainAcitivity1.this));
                    }else{
                      // Toast.makeText(MainAcitivity1.this, "上传失败", Toast.LENGTH_SHORT).show();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (msg.what == 2) {
                try {
                    JSONObject j = new JSONObject((String) msg.obj);
                    String code = j.getString("code");
                    if (code.equals("0")) {
                        SharedPfUtils.saveStringData(MainAcitivity1.this, "mainlogintaobaoId", AlibcLogin.getInstance().getSession().openId);
                        SharedPfUtils.saveStringData(MainAcitivity1.this, "maintoken", PushManager.getInstance().getClientid(MainAcitivity1.this));

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (msg.what == 11) {
                try {
                    JSONObject j = new JSONObject((String) msg.obj);
                   // System.out.println("j==="+j.toString());
                    String code = j.getString("code");
                    if (code.equals("0")) {
                        String data = j.getString("data");
                        AlibcTrade.show(MainAcitivity1.this, new AlibcPage(data),alibcShowParams, null, exParams , new DemoTradeCallback());


                    }
                } catch (Exception e) {
                    e.printStackTrace();

                }
            }
            if(msg.what==12){
               // System.out.println("111111");

            }
        }
    };

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("SplashActivity");
        MobclickAgent.onPause(this);
    }

    /**
     * @description: ViewPager 适配器
     */
    private class MyViewPagerAdapter extends FragmentPagerAdapter {
        public MyViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if(!splashInvite){
                return TAB_FRAGMENTS[position];
            }else{
                return TAB_FRAGMENTS_SHARE[position];
            }

        }

        @Override
        public int getCount() {
            return COUNT;
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            exitBy2Click();

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    //点击两次跳出程序
    private void exitBy2Click() {
        Timer tExit = null;
        if (isExit == false) {
            isExit = true; // 准备退出
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false; // 取消退出
                }
            }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务

        } else {
         //   ThreadPollFactory.canclePool();
            MainAcitivity1.this.finish();

        }
    }

    /**
     * 显示我的购物车
     */
    public void showCart() {
        AlibcBasePage alibcBasePage = new AlibcMyCartsPage();
        AlibcTrade.show(this, alibcBasePage, alibcShowParams, alibcTaokeParams, exParams, new DemoTradeCallback());
    }

    @Override
    protected void onDestroy() {
        //调用了AlibcTrade.show方法的Activity都需要调用AlibcTradeSDK.destory()
        AlibcTradeSDK.destory();
        super.onDestroy();
    }

    public class TurnUrlThread implements Runnable {
        String goodsId;
        Context context;

        public TurnUrlThread(String goodsId, Context context) {

            this.goodsId = goodsId;
            this.context = context;
        }


        @Override
        public void run() {
            try {
                Map<String, String> params = new HashMap<>();
                params.put("code", SplashActicity1.mQuanId);
                params.put("goodsId", goodsId);
                params.put("client_id", SplashActicity1.client_id);
                params.put("device", "2");
                params.put("channel", AndroidUtils.getAppVersion(context));
                params.put("app_version",app_version);
                params.put("api_version","1.0");
                params.put("token",(String) SharedPfUtils.getData(context,"token",""));
                String url = context.getResources().getString(R.string.appurl) + "/goods/turnUrl"+"?stamp=" + UrlUtils.getTime() + "&encode=" + UrlUtils.getEncode();
                String jsonData = HttpConBase.getjsonByPost(url, params, "utf-8");
                Message msg = Message.obtain();
                msg.what = 11;
                msg.obj = jsonData;
                hd.sendMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
                hd.sendEmptyMessage(12);
            }

        }
    }


}
