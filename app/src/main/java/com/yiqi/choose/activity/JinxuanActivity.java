package com.yiqi.choose.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.baichuan.android.trade.AlibcTrade;
import com.alibaba.baichuan.android.trade.model.AlibcShowParams;
import com.alibaba.baichuan.android.trade.model.OpenType;
import com.alibaba.baichuan.android.trade.page.AlibcPage;
import com.alibaba.baichuan.trade.biz.core.taoke.AlibcTaokeParams;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;
import com.yiqi.choose.R;
import com.yiqi.choose.alibabaCallback.DemoTradeCallback;
import com.yiqi.choose.base.BaseActivity;
import com.yiqi.choose.factory.ThreadPollFactory;
import com.yiqi.choose.model.GoodsInfo;
import com.yiqi.choose.thread.ClickThread;
import com.yiqi.choose.utils.AndroidUtils;
import com.yiqi.choose.utils.CustomProgressDialog;
import com.yiqi.choose.utils.HttpConBase;
import com.yiqi.choose.utils.MyToast;
import com.yiqi.choose.utils.NetJudgeUtils;
import com.yiqi.choose.utils.ParseJsonCommon;
import com.yiqi.choose.utils.PicassoUtils;
import com.yiqi.choose.utils.SharedPfUtils;
import com.yiqi.choose.utils.SystemBarTintManager;
import com.yiqi.choose.utils.UrlUtils;
import com.yiqi.choose.view.XListView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by moumou on 17/8/16.
 */

public class JinxuanActivity extends BaseActivity {
    private LinearLayout back_btn;
    //////////////
    LinearLayout home_jinxuan_ll_zonghe;
    TextView home_jinxuan_tv_zonghe;
    LinearLayout home_jinxuan_ll_xiaoliang;
    TextView home_jinxuan_tv_xiaoliang;
    LinearLayout home_jinxuan_ll_price;
    TextView home_jinxuan_tv_price;
    ImageView home_jinxuan_iv_price;
    XListView home_jinxuan_listView;
    LinearLayout home_jinxuan_nogoods;
    LinearLayout home_jinxuan_ll_noweb;
    TextView home_jixuan_tv_try;
    RelativeLayout home_jinxuan_rl_pg;

    private int priceLevel=0;
    private int order=0;
    private List<Object> goodList;
    private List<Object> ziList;
    int page = 1;
    int maxPage = 0;
    String time = "";
    MyAdapter adapter;
    ImageView home_arraw;

    private AlibcShowParams alibcShowParams;//页面打开方式，默认，H5，Native
    private Map<String, String> exParams;//yhhpass参数
    private AlibcTaokeParams alibcTaokeParams = null;//淘客参数，包括pid，unionid，subPid
    private String clickUrl="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.topcolorred);//通知栏所需颜色
        }
        setContentView(R.layout.home_jingxuan);

        alibcTaokeParams = new AlibcTaokeParams(SplashActicity1.mPId, "", "");
        alibcShowParams = new AlibcShowParams(OpenType.Native, false);
        alibcShowParams.setClientType("taobao_scheme");
        exParams = new HashMap<>();
        exParams.put("isv_code", "appisvcode");
        exParams.put("alibaba", "阿里巴巴");//自定义参数部分，可任意增删改

        order=0;
        home_jinxuan_ll_zonghe = (LinearLayout) this.findViewById(R.id.home_jinxuan_ll_zonghe);
        home_jinxuan_tv_zonghe = (TextView) this.findViewById(R.id.home_jinxuan_tv_zonghe);
        home_jinxuan_ll_xiaoliang = (LinearLayout) this.findViewById(R.id.home_jinxuan_ll_xiaoliang);
        home_jinxuan_tv_xiaoliang = (TextView) this.findViewById(R.id.home_jinxuan_tv_xiaoliang);
        home_jinxuan_ll_price = (LinearLayout) this.findViewById(R.id.home_jinxuan_ll_price);
        home_jinxuan_tv_price = (TextView) this.findViewById(R.id.home_jinxuan_tv_price);
        home_jinxuan_iv_price = (ImageView) this.findViewById(R.id.home_jinxuan_iv_prive);
        home_jinxuan_listView = (XListView) this.findViewById(R.id.home_jinxuan_listview);
        home_jinxuan_nogoods = (LinearLayout) this.findViewById(R.id.home_jinxuan_nogoods);
        home_jinxuan_ll_noweb = (LinearLayout) this.findViewById(R.id.home_jinxuan_ll_noweb);
        home_jixuan_tv_try = (TextView) this.findViewById(R.id.home_fragment_tv_retro);
        home_jinxuan_rl_pg = (RelativeLayout) this.findViewById(R.id.home_jinxuan_rl_pg);
        home_arraw=(ImageView)this.findViewById(R.id.home_arraw);
        goodList = new ArrayList<Object>();
        ziList = new ArrayList<Object>();

        clickUrl="";
        back_btn = (LinearLayout) this.findViewById(R.id.ll_back);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JinxuanActivity.this.finish();
                overridePendingTransition(R.anim.in_from_left,
                        R.anim.out_of_right);
            }
        });
        if(NetJudgeUtils.getNetConnection(JinxuanActivity.this)){
            home_jinxuan_listView.setVisibility(View.GONE);
            home_jinxuan_nogoods.setVisibility(View.GONE);
            home_jinxuan_ll_noweb.setVisibility(View.GONE);
            home_jinxuan_rl_pg.setVisibility(View.VISIBLE);
            ThreadPollFactory.getNormalPool().execute(new goodsThread(SplashActicity1.mQuanId,"",order+"","2",1));
        }else{
            home_jinxuan_listView.setVisibility(View.GONE);
            home_jinxuan_nogoods.setVisibility(View.GONE);
            home_jinxuan_ll_noweb.setVisibility(View.VISIBLE);
            home_jinxuan_rl_pg.setVisibility(View.GONE);
        }
        home_jixuan_tv_try.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(NetJudgeUtils.getNetConnection(JinxuanActivity.this)){
                    home_jinxuan_listView.setVisibility(View.GONE);
                    home_jinxuan_nogoods.setVisibility(View.GONE);
                    home_jinxuan_ll_noweb.setVisibility(View.GONE);
                    home_jinxuan_rl_pg.setVisibility(View.VISIBLE);
                    ThreadPollFactory.getNormalPool().execute(new goodsThread(SplashActicity1.mQuanId,"",order+"","2",1));
                }else{
                    home_jinxuan_listView.setVisibility(View.GONE);
                    home_jinxuan_nogoods.setVisibility(View.GONE);
                    home_jinxuan_ll_noweb.setVisibility(View.VISIBLE);
                    home_jinxuan_rl_pg.setVisibility(View.GONE);
                }
            }
        });
        home_jinxuan_ll_zonghe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(order!=0){
                    home_jinxuan_tv_zonghe.setTextColor(getResources().getColor(R.color.red));
                    home_jinxuan_tv_price.setTextColor(getResources().getColor(R.color.ziblack));
                    home_jinxuan_iv_price.setImageResource(R.mipmap.price_nor);
                    home_jinxuan_tv_xiaoliang.setTextColor(getResources().getColor(R.color.ziblack));
                    priceLevel=0;
                    order=0;
                    if(NetJudgeUtils.getNetConnection(JinxuanActivity.this)){
                        home_jinxuan_listView.setVisibility(View.GONE);
                        home_jinxuan_nogoods.setVisibility(View.GONE);
                        home_jinxuan_ll_noweb.setVisibility(View.GONE);
                        home_jinxuan_rl_pg.setVisibility(View.VISIBLE);
                        ThreadPollFactory.getNormalPool().execute(new goodsThread(SplashActicity1.mQuanId,"",order+"","2",1));
                    }else{
                        home_jinxuan_listView.setVisibility(View.GONE);
                        home_jinxuan_nogoods.setVisibility(View.GONE);
                        home_jinxuan_ll_noweb.setVisibility(View.VISIBLE);
                        home_jinxuan_rl_pg.setVisibility(View.GONE);
                    }

                }

            }
        });
        home_jinxuan_ll_xiaoliang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(order!=1){
                    home_jinxuan_tv_xiaoliang.setTextColor(getResources().getColor(R.color.red));
                    home_jinxuan_tv_price.setTextColor(getResources().getColor(R.color.ziblack));
                    home_jinxuan_iv_price.setImageResource(R.mipmap.price_nor);
                    home_jinxuan_tv_zonghe.setTextColor(getResources().getColor(R.color.ziblack));
                    priceLevel=0;
                    order=1;
                    if(NetJudgeUtils.getNetConnection(JinxuanActivity.this)){
                        home_jinxuan_listView.setVisibility(View.GONE);
                        home_jinxuan_nogoods.setVisibility(View.GONE);
                        home_jinxuan_ll_noweb.setVisibility(View.GONE);
                        home_jinxuan_rl_pg.setVisibility(View.VISIBLE);
                        ThreadPollFactory.getNormalPool().execute(new goodsThread(SplashActicity1.mQuanId,"",order+"","2",1));
                    }else{
                        home_jinxuan_listView.setVisibility(View.GONE);
                        home_jinxuan_nogoods.setVisibility(View.GONE);
                        home_jinxuan_ll_noweb.setVisibility(View.VISIBLE);
                        home_jinxuan_rl_pg.setVisibility(View.GONE);
                    }
                }

            }
        });
        home_jinxuan_ll_price.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    home_jinxuan_tv_xiaoliang.setTextColor(getResources().getColor(R.color.ziblack));
                    home_jinxuan_tv_price.setTextColor(getResources().getColor(R.color.red));

                    home_jinxuan_tv_zonghe.setTextColor(getResources().getColor(R.color.ziblack));
                    if(priceLevel==0||priceLevel==2){
                        priceLevel=1;
                        home_jinxuan_iv_price.setImageResource(R.mipmap.price_up);
                        order=3;
                    }else{
                        priceLevel=2;
                        home_jinxuan_iv_price.setImageResource(R.mipmap.price_down);
                        order=2;
                    }
                    if(NetJudgeUtils.getNetConnection(JinxuanActivity.this)){
                        home_jinxuan_listView.setVisibility(View.GONE);
                        home_jinxuan_nogoods.setVisibility(View.GONE);
                        home_jinxuan_ll_noweb.setVisibility(View.GONE);
                        home_jinxuan_rl_pg.setVisibility(View.VISIBLE);
                        ThreadPollFactory.getNormalPool().execute(new goodsThread(SplashActicity1.mQuanId,"",order+"","2",1));
                    }else{
                        home_jinxuan_listView.setVisibility(View.GONE);
                        home_jinxuan_nogoods.setVisibility(View.GONE);
                        home_jinxuan_ll_noweb.setVisibility(View.VISIBLE);
                        home_jinxuan_rl_pg.setVisibility(View.GONE);
                    }



            }
        });
        home_jinxuan_listView.setXListViewListener(new XListView.IXListViewListener() {
            public void onRefresh() {
                if (NetJudgeUtils.getNetConnection(JinxuanActivity.this)) {
                    ThreadPollFactory.getNormalPool().execute(new goodsThread(SplashActicity1.mQuanId,"",order+"","2",1));
                } else {
                    hd.sendEmptyMessage(7);
                }
            }

            public void onLoadMore() {
                if (home_jinxuan_listView.getProgressState()) {
                    if (NetJudgeUtils.getNetConnection(JinxuanActivity.this)) {
                        page++;
                        ThreadPollFactory.getNormalPool().execute(new goodsThread(SplashActicity1.mQuanId,(String) SharedPfUtils.getData(JinxuanActivity.this, "jinxuantime", ""),order+"","3",page));
                    } else {
                        hd.sendEmptyMessage(8);
                    }


                }

                //

            }
        });
        home_jinxuan_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(NetJudgeUtils.getNetConnection(JinxuanActivity.this)){
                    CustomProgressDialog.createDialog(JinxuanActivity.this,"");
                    GoodsInfo info=(GoodsInfo)goodList.get(position-1);
                    clickUrl=info.getGoodsUrl();
                    ThreadPollFactory.getNormalPool().execute(new ClickThread(info.getNumIid()+"",JinxuanActivity.this,hd));
                    //AlibcTrade.show(JinxuanActivity.this, new AlibcPage(info.getGoodsUrl()),alibcShowParams, alibcTaokeParams, exParams , new DemoTradeCallback());

                }else{
                    Toast.makeText(JinxuanActivity.this,"您的网络不给力，请检查更新!",Toast.LENGTH_LONG).show();
                }


            }
        });
        home_jinxuan_listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // TODO Auto-generated method stub
                final Picasso picasso = Picasso.with(JinxuanActivity.this);
                if (scrollState == SCROLL_STATE_IDLE || scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                    picasso.resumeTag(JinxuanActivity.this);
                } else {
                    picasso.pauseTag(JinxuanActivity.this);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

                if (firstVisibleItem > 22) {
                    home_arraw.setVisibility(View.VISIBLE);
                } else {
                    home_arraw.setVisibility(View.GONE);
                }
            }
        });
        home_arraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                home_jinxuan_listView.setSelection(0);
            }
        });
    }
    /**
     * 的到圈值接口
     */
    private class goodsThread implements Runnable {
        String code;
        String timeStemp;
        int page;
        String order;
        String type;

        public goodsThread(String code, String timeStemp,String order,String type, int page) {
            this.code = code;
            this.timeStemp = timeStemp;
            this.type = type;
            this.page = page;
            this.order=order;
        }

        @Override
        public void run() {
            try {
                String url = getResources().getString(R.string.appurl) + "/goods/other" + "?code=" + code + "&timeStemp=" + timeStemp + "&order=" + order + "&page=" + page+ "&type=" + type+"&stamp="+ UrlUtils.getTime()+"&encode="+UrlUtils.getEncode();
                String jsonData = HttpConBase.sendGet(url);
                Message msg = Message.obtain();
                if (page <= 1) {
                    msg.what = 3;
                    msg.obj = jsonData;
                    hd.sendMessage(msg);
                } else {
                    msg.what = 4;
                    msg.obj = jsonData;
                    hd.sendMessage(msg);
                }

            } catch (Exception e) {
                e.printStackTrace();
                if (page == 1) {
                    hd.sendEmptyMessage(5);
                } else {
                    hd.sendEmptyMessage(6);
                }

            }

        }
    }
    private Handler hd = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 3) {
                try {
                    JSONObject j = new JSONObject((String) msg.obj);
                    String code = j.getString("code");
                    if (code.equals("0")) {
                        page = 1;
                        String data = j.getString("data");
                        JSONObject jData = new JSONObject(data);
                        String goods = jData.getString("goods");
                        maxPage = jData.getInt("totalpage");
                        time = jData.getString("timeStemp");
                        SharedPfUtils.saveStringData(JinxuanActivity.this,"jinxuantime", time);
                        if (goods.equals("[]") || null == goods || goods.equals("null")) {
                          home_jinxuan_listView.setVisibility(View.GONE);
                            home_jinxuan_nogoods.setVisibility(View.VISIBLE);
                           home_jinxuan_ll_noweb.setVisibility(View.GONE);
                            home_jinxuan_rl_pg.setVisibility(View.GONE);
                        } else {
                            goodList.clear();
                            goodList = ParseJsonCommon.parseJsonData(goods,
                                    GoodsInfo.class);

                                if (goodList.size() > 0) {
                                    home_jinxuan_listView.setVisibility(View.VISIBLE);
                                    home_jinxuan_nogoods.setVisibility(View.GONE);
                                    home_jinxuan_ll_noweb.setVisibility(View.GONE);
                                    home_jinxuan_rl_pg.setVisibility(View.GONE);
                                    if (maxPage > 1) {
                                        home_jinxuan_listView.setPullLoadEnable(true);
                                    } else {
                                        home_jinxuan_listView.setPullLoadEnable(false);
                                    }
                                    adapter = new MyAdapter(JinxuanActivity.this);

                                    home_jinxuan_listView.setAdapter(adapter);
                                    home_jinxuan_listView.stopRefresh();
                                    home_jinxuan_listView.setRefreshTime(new Date().toLocaleString());
                                } else {
                                    home_jinxuan_listView.setVisibility(View.GONE);
                                    home_jinxuan_nogoods.setVisibility(View.VISIBLE);
                                    home_jinxuan_ll_noweb.setVisibility(View.GONE);
                                    home_jinxuan_rl_pg.setVisibility(View.GONE);
                                }
                            }



                    } else {

                        home_jinxuan_listView.setVisibility(View.GONE);
                        home_jinxuan_nogoods.setVisibility(View.GONE);
                        home_jinxuan_ll_noweb.setVisibility(View.VISIBLE);
                        home_jinxuan_rl_pg.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    home_jinxuan_listView.setVisibility(View.GONE);
                    home_jinxuan_nogoods.setVisibility(View.GONE);
                    home_jinxuan_ll_noweb.setVisibility(View.VISIBLE);
                    home_jinxuan_rl_pg.setVisibility(View.GONE);
                }
            }
            if (msg.what == 4) {
                try {
                    JSONObject j = new JSONObject((String) msg.obj);
                    String code = j.getString("code");
                    if (code.equals("0")) {
                        String data = j.getString("data");
                        JSONObject jData = new JSONObject(data);
                        String goods = jData.getString("goods");

                        ziList.clear();
                        ziList = ParseJsonCommon.parseJsonData(goods,
                                GoodsInfo.class);
                        goodList.addAll(ziList);

                        if (page >= maxPage) {
                            home_jinxuan_listView.setPullLoadEnable(false);
                        } else {
                            home_jinxuan_listView.setPullLoadEnable(true);
                        }
                        adapter.notifyDataSetChanged();// 告诉listView数据发生改变，要求listView更新显示
                        home_jinxuan_listView.stopRefresh();
                        home_jinxuan_listView.stopLoadMore();
                    } else {
                        if (page > 1) {
                            page--;
                        }
                        home_jinxuan_listView.stopRefresh();
                        home_jinxuan_listView.stopLoadMore();
                        Toast.makeText(JinxuanActivity.this, "数据返回错误!", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    home_jinxuan_listView.stopRefresh();
                    home_jinxuan_listView.stopLoadMore();
                }
            }
            if (msg.what == 5) {
                home_jinxuan_listView.stopRefresh();
                home_jinxuan_listView.stopLoadMore();
                home_jinxuan_listView.setVisibility(View.GONE);
                home_jinxuan_nogoods.setVisibility(View.GONE);
                home_jinxuan_ll_noweb.setVisibility(View.VISIBLE);
                home_jinxuan_rl_pg.setVisibility(View.GONE);
            }
            if (msg.what == 6) {
                Toast.makeText(JinxuanActivity.this, "数据返回错误!", Toast.LENGTH_SHORT).show();
                if (page > 1) {
                    page--;
                }
                home_jinxuan_listView.stopLoadMore();
            }
            if (msg.what == 7) {
                MyToast.show(JinxuanActivity.this, 0, AndroidUtils.getWidth(JinxuanActivity.this),
                        AndroidUtils.dip2px(JinxuanActivity.this, 45), "您的网络不给力，请检查更新!");
                home_jinxuan_listView.stopRefresh();
            }
            if (msg.what == 8) {

                home_jinxuan_listView.stopLoadMore();

            }
            if(msg.what==100){
                try {
                    JSONObject j = new JSONObject((String) msg.obj);
                    String code = j.getString("code");
                    if (code.equals("0")) {
                        String data = j.getString("data");
                        if(TextUtils.isEmpty(data)){
                            if(NetJudgeUtils.getNetConnection(JinxuanActivity.this)){
                            //    Toast.makeText(JinxuanActivity.this,"本地转链地址!",Toast.LENGTH_LONG).show();
                                AlibcTrade.show(JinxuanActivity.this, new AlibcPage(clickUrl),alibcShowParams, alibcTaokeParams, exParams , new DemoTradeCallback());
                            }else{
                                Toast.makeText(JinxuanActivity.this,"您的网络不给力，请检查更新!",Toast.LENGTH_LONG).show();
                            }
                        }else{
                            if(NetJudgeUtils.getNetConnection(JinxuanActivity.this)){
                                //Toast.makeText(JinxuanActivity.this,"后台转链地址!",Toast.LENGTH_LONG).show();
                                AlibcTrade.show(JinxuanActivity.this, new AlibcPage(data),alibcShowParams, null, exParams , new DemoTradeCallback());
                            }else{
                                Toast.makeText(JinxuanActivity.this,"您的网络不给力，请检查更新!",Toast.LENGTH_LONG).show();
                            }
                        }


                    }else{
                        if(NetJudgeUtils.getNetConnection(JinxuanActivity.this)){
                           // Toast.makeText(JinxuanActivity.this,"本地转链地址!",Toast.LENGTH_LONG).show();
                            AlibcTrade.show(JinxuanActivity.this, new AlibcPage(clickUrl),alibcShowParams, alibcTaokeParams, exParams , new DemoTradeCallback());
                        }else{
                            Toast.makeText(JinxuanActivity.this,"您的网络不给力，请检查更新!",Toast.LENGTH_LONG).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if(NetJudgeUtils.getNetConnection(JinxuanActivity.this)){
                       // Toast.makeText(JinxuanActivity.this,"本地转链地址!",Toast.LENGTH_LONG).show();
                        AlibcTrade.show(JinxuanActivity.this, new AlibcPage(clickUrl),alibcShowParams, alibcTaokeParams, exParams , new DemoTradeCallback());
                    }else{
                        Toast.makeText(JinxuanActivity.this,"您的网络不给力，请检查更新!",Toast.LENGTH_LONG).show();
                    }

                } finally {
                    CustomProgressDialog.stopProgressDialog();
                }
            }
            if(msg.what==101){
                if(NetJudgeUtils.getNetConnection(JinxuanActivity.this)){
                   // Toast.makeText(JinxuanActivity.this,"本地转链地址!",Toast.LENGTH_LONG).show();
                    AlibcTrade.show(JinxuanActivity.this, new AlibcPage(clickUrl),alibcShowParams, alibcTaokeParams, exParams , new DemoTradeCallback());
                }else{
                    Toast.makeText(JinxuanActivity.this,"您的网络不给力，请检查更新!",Toast.LENGTH_LONG).show();
                }
                CustomProgressDialog.stopProgressDialog();

            }
        }
    };
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

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("JinxuanActivity");
        MobclickAgent.onResume(this);


    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("JinxuanActivity");
        MobclickAgent.onPause(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

            this.finish();
            overridePendingTransition(R.anim.in_from_left, R.anim.out_of_right);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
    public class MyAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private Context context;

        public MyAdapter(Context context) {
            this.context = context;
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {


            return goodList.size();


        }

        @Override
        public Object getItem(int position) {

            return goodList.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub

            return position;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            View view = null;
            GoodHodler holder = null;
            if (convertView == null || convertView.getTag() == null) {
                view = inflater.inflate(R.layout.layout_main_item_small, null);
                holder = new GoodHodler();
                holder.good_img = (ImageView) view.findViewById(R.id.good_img);
                holder.goods_desc = (TextView) view.findViewById(R.id.goods_desc);
                holder.goods_discount_price = (TextView) view.findViewById(R.id.goods_discount_price);
                holder.goods_ori_price = (TextView) view.findViewById(R.id.goods_ori_price);
                holder.tv_chengji = (TextView) view.findViewById(R.id.tv_chengji);
                holder.goods_ori_price.getPaint().setFlags(
                        Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
                view.setTag(holder);
            } else {
                view = convertView;
                holder = (GoodHodler) convertView.getTag();
            }
            final GoodsInfo goodsInfo;
            goodsInfo = (GoodsInfo) goodList.get(position);
            PicassoUtils.loadImageWithHolderAndError(JinxuanActivity.this, goodsInfo.getGoodsImage(), R.mipmap.picture, R.mipmap.picture, holder.good_img);
            holder.goods_desc.setText(goodsInfo.getTitle());
            holder.goods_discount_price.setText("￥" + goodsInfo.getPrice());
            holder.goods_ori_price.setText("￥" + goodsInfo.getOldPrice());
            holder.tv_chengji.setText("已售" + goodsInfo.getSellCount() + "件");

            return view;
        }

    }
    class GoodHodler {
        ImageView good_img;
        TextView goods_desc;
        TextView goods_discount_price;
        TextView goods_ori_price;
        TextView tv_chengji;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CustomProgressDialog.stopProgressDialog();
    }
}
