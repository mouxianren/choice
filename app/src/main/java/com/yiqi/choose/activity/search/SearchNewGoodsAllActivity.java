package com.yiqi.choose.activity.search;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.baichuan.android.trade.AlibcTrade;
import com.alibaba.baichuan.android.trade.model.AlibcShowParams;
import com.alibaba.baichuan.android.trade.model.OpenType;
import com.alibaba.baichuan.android.trade.page.AlibcDetailPage;
import com.alibaba.baichuan.android.trade.page.AlibcPage;
import com.alibaba.baichuan.trade.biz.core.taoke.AlibcTaokeParams;
import com.chanven.lib.cptr.circle.CircularProgressView;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;
import com.yiqi.choose.R;
import com.yiqi.choose.activity.SplashActicity1;
import com.yiqi.choose.alibabaCallback.DemoTradeCallback;
import com.yiqi.choose.base.BaseActivity;
import com.yiqi.choose.dao.HistoryDao;
import com.yiqi.choose.factory.ThreadPollFactory;
import com.yiqi.choose.model.GoodsOutInfo;
import com.yiqi.choose.thread.ClickThread;
import com.yiqi.choose.thread.StatisticsThread;
import com.yiqi.choose.thread.TurnUrlThread;
import com.yiqi.choose.utils.AndroidUtils;
import com.yiqi.choose.utils.BooleanThread;
import com.yiqi.choose.utils.CustomProgressDialog;
import com.yiqi.choose.utils.HttpConBase;
import com.yiqi.choose.utils.NetJudgeUtils;
import com.yiqi.choose.utils.ParseJsonCommon;
import com.yiqi.choose.utils.PicassoUtils;
import com.yiqi.choose.utils.StringUtils;
import com.yiqi.choose.utils.SystemBarTintManager;
import com.yiqi.choose.utils.UrlUtils;
import com.yiqi.choose.view.UISwitchButton1;
import com.yiqi.choose.view.XListView;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by moumou on 17/9/29.
 */

public class SearchNewGoodsAllActivity extends BaseActivity {

    LinearLayout ll_back;
    LinearLayout youhui_tv_search;
    EditText search_et_hint;
    RelativeLayout all_search_rl_listview;
    ImageView all_search_home_arraw;
    XListView all_search_listview;

    LinearLayout all_search_nogoods;

    LinearLayout all_search_ll_nowife;
    TextView all_search_tv_retro;

    RelativeLayout all_search_rl_pb;
    private List<Object> goodList;

    private List<Object> youhuiList;
    private MyHandler hd;

    MyAdapter adapter;
    //ClipboardManager cmb;

    private String turnGoodsId;
    private AlibcShowParams alibcShowParams;//页面打开方式，默认，H5，Native
    private Map<String, String> exParams;//yhhpass参数
    private AlibcTaokeParams alibcTaokeParams = null;//淘客参数，包括pid，unionid，subPid

    private String keyword;
    private ImageView iv_guanbi;
    private CircularProgressView cp_image;

    private LinearLayout.LayoutParams params;
    private int srceenWidth;
    int type;
    int order;

    LinearLayout all_search_ll_seller;
    TextView all_search_tv_seller;

    LinearLayout all_search_ll_price;
    TextView all_search_tv_price;
    ImageView all_search_iv_price;
    UISwitchButton1 splitxiaoxi;
    int visiteType;
    private String clickUrl="";
    private HistoryDao dao;

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
        setContentView(R.layout.searchnew_out);

        type = 3;
        order = 0;
        splitxiaoxi = (UISwitchButton1) this.findViewById(R.id.splitxiaoxi);
        splitxiaoxi.setChecked(true);
        ll_back = (LinearLayout) this.findViewById(R.id.ll_back);
        youhui_tv_search = (LinearLayout) this.findViewById(R.id.youhui_tv_search);
        search_et_hint = (EditText) this.findViewById(R.id.search_et_hint);
        Intent intent = getIntent();
        keyword = intent.getStringExtra("keyword");
        search_et_hint.setText(keyword);
        search_et_hint.setSelection(keyword.length());
        if (SplashActicity1.sdCardWritePermisson) {
            dao = HistoryDao.getInstance(SearchNewGoodsAllActivity.this);
        }
        all_search_rl_listview = (RelativeLayout) this.findViewById(R.id.all_search_rl_listview);
        all_search_home_arraw = (ImageView) this.findViewById(R.id.all_search_home_arraw);
        all_search_listview = (XListView) this.findViewById(R.id.all_search_listview);
        cp_image = (CircularProgressView) this.findViewById(R.id.cp_image);
        // cmb = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
        iv_guanbi = (ImageView) this.findViewById(R.id.iv_guanbi);
        all_search_listview.setPullLoadEnable(false);
        all_search_listview.setPullRefreshEnable(false);

        all_search_ll_seller = (LinearLayout) this.findViewById(R.id.all_search_ll_seller);
        all_search_tv_seller = (TextView) this.findViewById(R.id.all_search_tv_seller);

        all_search_ll_price = (LinearLayout) this.findViewById(R.id.all_search_ll_price);
        all_search_tv_price = (TextView) this.findViewById(R.id.all_search_tv_price);
        all_search_iv_price = (ImageView) this.findViewById(R.id.all_search_iv_price);


        all_search_nogoods = (LinearLayout) this.findViewById(R.id.all_search_nogoods);

        all_search_ll_nowife = (LinearLayout) this.findViewById(R.id.all_search_ll_nowife);
        all_search_tv_retro = (TextView) this.findViewById(R.id.all_search_tv_retro);

        all_search_rl_pb = (RelativeLayout) this.findViewById(R.id.cp_rl_image);

        goodList = new ArrayList<Object>();

        hd = new MyHandler(SearchNewGoodsAllActivity.this);
        alibcTaokeParams = new AlibcTaokeParams(SplashActicity1.mPId, "", "");
        alibcShowParams = new AlibcShowParams(OpenType.Native, false);
        alibcShowParams.setClientType("taobao_scheme");
        exParams = new HashMap<>();
        exParams.put("isv_code", "appisvcode");
        exParams.put("alibaba", "阿里巴巴");//自定义参数部分，可任意增删改
        srceenWidth = AndroidUtils.getWidth(SearchNewGoodsAllActivity.this);

        params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.width = (int) (srceenWidth * 0.361);
        params.height = (int) (srceenWidth * 0.361);


        iv_guanbi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_et_hint.setText("");
            }
        });
        ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchNewGoodsAllActivity.this.finish();
                overridePendingTransition(R.anim.in_from_left, R.anim.out_of_right);
            }
        });
        visiteType = 2;
        if (NetJudgeUtils.getNetConnection(SearchNewGoodsAllActivity.this)) {
            all_search_rl_listview.setVisibility(View.GONE);
            all_search_nogoods.setVisibility(View.GONE);
            all_search_ll_nowife.setVisibility(View.GONE);
            all_search_rl_pb.setVisibility(View.VISIBLE);
            cp_image.innerStart();
            ThreadPollFactory.getNormalPool().execute(new goodsThread(search_et_hint.getText().toString().trim(), type, order));
        } else {
            all_search_rl_listview.setVisibility(View.GONE);
            all_search_nogoods.setVisibility(View.GONE);
            all_search_ll_nowife.setVisibility(View.VISIBLE);
            all_search_rl_pb.setVisibility(View.GONE);
            cp_image.innerStop();
        }
        all_search_listview.setOverScrollMode(View.OVER_SCROLL_NEVER);
        youhuiList = new ArrayList<Object>();

        splitxiaoxi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == false) {
                    //仅显示优惠券

                    visiteType = 1;
                    if (NetJudgeUtils.getNetConnection(SearchNewGoodsAllActivity.this)) {
                        if (youhuiList.size() > 0) {
                            if (adapter != null) {
                                adapter.notifyDataSetChanged();
                            } else {
                                adapter = new MyAdapter(SearchNewGoodsAllActivity.this);
                                all_search_listview.setAdapter(adapter);
                            }
                            all_search_rl_listview.setVisibility(View.VISIBLE);
                            all_search_nogoods.setVisibility(View.GONE);
                            all_search_ll_nowife.setVisibility(View.GONE);
                            all_search_rl_pb.setVisibility(View.GONE);
                            cp_image.innerStop();
                        } else {
                            all_search_rl_listview.setVisibility(View.GONE);
                            all_search_nogoods.setVisibility(View.VISIBLE);
                            all_search_ll_nowife.setVisibility(View.GONE);
                            all_search_rl_pb.setVisibility(View.GONE);
                            cp_image.innerStop();
                        }

                    } else {
                        all_search_rl_listview.setVisibility(View.GONE);
                        all_search_nogoods.setVisibility(View.GONE);
                        all_search_ll_nowife.setVisibility(View.VISIBLE);
                        all_search_rl_pb.setVisibility(View.GONE);
                        cp_image.innerStop();
                    }


                } else if (isChecked == true) {
                    //全部显示
                    visiteType = 2;
                    if (NetJudgeUtils.getNetConnection(SearchNewGoodsAllActivity.this)) {
                        if (goodList.size() > 0) {
                            if (adapter != null) {
                                adapter.notifyDataSetChanged();
                            } else {
                                adapter = new MyAdapter(SearchNewGoodsAllActivity.this);
                                all_search_listview.setAdapter(adapter);
                            }
                            all_search_rl_listview.setVisibility(View.VISIBLE);
                            all_search_nogoods.setVisibility(View.GONE);
                            all_search_ll_nowife.setVisibility(View.GONE);
                            all_search_rl_pb.setVisibility(View.GONE);
                            cp_image.innerStop();
                        } else {
                            all_search_rl_listview.setVisibility(View.GONE);
                            all_search_nogoods.setVisibility(View.VISIBLE);
                            all_search_ll_nowife.setVisibility(View.GONE);
                            all_search_rl_pb.setVisibility(View.GONE);
                            cp_image.innerStop();
                        }

                    } else {
                        all_search_rl_listview.setVisibility(View.GONE);
                        all_search_nogoods.setVisibility(View.GONE);
                        all_search_ll_nowife.setVisibility(View.VISIBLE);
                        all_search_rl_pb.setVisibility(View.GONE);
                        cp_image.innerStop();
                    }

                }
            }
        });
        youhui_tv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(search_et_hint.getText().toString().trim())) {
                    Toast.makeText(SearchNewGoodsAllActivity.this, "请输入关键词!", Toast.LENGTH_LONG).show();
                    return;
                }
                if (SplashActicity1.sdCardWritePermisson) {
                    if (dao.queryGoods(search_et_hint.getText().toString().trim()) <= 0) {
                        dao.saveSS(search_et_hint.getText().toString().trim());
                    } else {
                        dao.deteCI(search_et_hint.getText().toString().trim());
                        dao.saveSS(search_et_hint.getText().toString().trim());
                    }
                }
                AndroidUtils.hideInput(SearchNewGoodsAllActivity.this, search_et_hint);
                if (NetJudgeUtils.getNetConnection(SearchNewGoodsAllActivity.this)) {
                    all_search_rl_listview.setVisibility(View.GONE);
                    all_search_nogoods.setVisibility(View.GONE);
                    all_search_ll_nowife.setVisibility(View.GONE);
                    all_search_rl_pb.setVisibility(View.VISIBLE);
                    cp_image.innerStart();
                    ThreadPollFactory.getNormalPool().execute(new goodsThread(search_et_hint.getText().toString().trim(), type, order));
                } else {
                    all_search_rl_listview.setVisibility(View.GONE);
                    all_search_nogoods.setVisibility(View.GONE);
                    all_search_ll_nowife.setVisibility(View.VISIBLE);
                    all_search_rl_pb.setVisibility(View.GONE);
                    cp_image.innerStop();
                }
            }
        });
        all_search_tv_retro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(search_et_hint.getText().toString().trim())) {
                    Toast.makeText(SearchNewGoodsAllActivity.this, "请输入关键词!", Toast.LENGTH_LONG).show();
                    return;
                }
                AndroidUtils.hideInput(SearchNewGoodsAllActivity.this, search_et_hint);
                if (NetJudgeUtils.getNetConnection(SearchNewGoodsAllActivity.this)) {
                    all_search_rl_listview.setVisibility(View.GONE);
                    all_search_nogoods.setVisibility(View.GONE);
                    all_search_ll_nowife.setVisibility(View.GONE);
                    all_search_rl_pb.setVisibility(View.VISIBLE);
                    cp_image.innerStart();
                    ThreadPollFactory.getNormalPool().execute(new goodsThread(search_et_hint.getText().toString().trim(), 3, order));
                } else {
                    all_search_rl_listview.setVisibility(View.GONE);
                    all_search_nogoods.setVisibility(View.GONE);
                    all_search_ll_nowife.setVisibility(View.VISIBLE);
                    all_search_rl_pb.setVisibility(View.GONE);
                    cp_image.innerStop();
                }
            }
        });
        all_search_ll_seller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(search_et_hint.getText().toString().trim())) {
                    Toast.makeText(SearchNewGoodsAllActivity.this, "请输入关键词!", Toast.LENGTH_LONG).show();
                    return;
                }
                AndroidUtils.hideInput(SearchNewGoodsAllActivity.this, search_et_hint);

                order = 0;
                all_search_tv_price.setTextColor(getResources().getColor(R.color.newzigrey));
                all_search_tv_seller.setTextColor(getResources().getColor(R.color.red));
                all_search_iv_price.setImageDrawable(getResources().getDrawable(R.mipmap.price_nor));
                if (NetJudgeUtils.getNetConnection(SearchNewGoodsAllActivity.this)) {
                    all_search_rl_listview.setVisibility(View.GONE);
                    all_search_nogoods.setVisibility(View.GONE);
                    all_search_ll_nowife.setVisibility(View.GONE);
                    all_search_rl_pb.setVisibility(View.VISIBLE);
                    cp_image.innerStart();
                    ThreadPollFactory.getNormalPool().execute(new goodsThread(search_et_hint.getText().toString().trim(), 3, order));
                } else {
                    all_search_rl_listview.setVisibility(View.GONE);
                    all_search_nogoods.setVisibility(View.GONE);
                    all_search_ll_nowife.setVisibility(View.VISIBLE);
                    all_search_rl_pb.setVisibility(View.GONE);
                    cp_image.innerStop();

                }
            }

        });
        all_search_ll_price.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(search_et_hint.getText().toString().trim())) {
                    Toast.makeText(SearchNewGoodsAllActivity.this, "请输入关键词!", Toast.LENGTH_LONG).show();
                    return;
                }
                AndroidUtils.hideInput(SearchNewGoodsAllActivity.this, search_et_hint);

                if (order != 2) {
                    order = 2;
                    all_search_tv_price.setTextColor(getResources().getColor(R.color.red));
                    all_search_tv_seller.setTextColor(getResources().getColor(R.color.newzigrey));
                    all_search_iv_price.setImageDrawable(getResources().getDrawable(R.mipmap.price_up));

                } else {
                    order = 1;
                    all_search_tv_price.setTextColor(getResources().getColor(R.color.red));
                    all_search_tv_seller.setTextColor(getResources().getColor(R.color.newzigrey));
                    all_search_iv_price.setImageDrawable(getResources().getDrawable(R.mipmap.price_down));
                }
                if (NetJudgeUtils.getNetConnection(SearchNewGoodsAllActivity.this)) {
                    all_search_rl_listview.setVisibility(View.GONE);
                    all_search_nogoods.setVisibility(View.GONE);
                    all_search_ll_nowife.setVisibility(View.GONE);
                    all_search_rl_pb.setVisibility(View.VISIBLE);
                    cp_image.innerStart();
                    ThreadPollFactory.getNormalPool().execute(new goodsThread(search_et_hint.getText().toString().trim(), 3, order));
                } else {
                    all_search_rl_listview.setVisibility(View.GONE);
                    all_search_nogoods.setVisibility(View.GONE);
                    all_search_ll_nowife.setVisibility(View.VISIBLE);
                    all_search_rl_pb.setVisibility(View.GONE);
                    cp_image.innerStop();
                }
            }
        });
        all_search_listview.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // TODO Auto-generated method stub
                final Picasso picasso = Picasso.with(SearchNewGoodsAllActivity.this);
                if (scrollState == SCROLL_STATE_IDLE || scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                    picasso.resumeTag(SearchNewGoodsAllActivity.this);
                } else {
                    picasso.pauseTag(SearchNewGoodsAllActivity.this);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

                if (firstVisibleItem > 22) {
                    all_search_home_arraw.setVisibility(View.VISIBLE);
                } else {
                    all_search_home_arraw.setVisibility(View.GONE);
                }
            }
        });
        all_search_home_arraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                all_search_listview.setSelection(0);
            }
        });
        all_search_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (NetJudgeUtils.getNetConnection(SearchNewGoodsAllActivity.this)) {
                    CustomProgressDialog.createDialog(SearchNewGoodsAllActivity.this, "");
                    GoodsOutInfo info = (GoodsOutInfo) goodList.get(position - 1);
                    turnGoodsId = info.getNumIid();
                    if(info.getGoodsType().equals("1")){

                        ThreadPollFactory.getNormalPool().execute(new TurnUrlThread(info.getNumIid() + "", SearchNewGoodsAllActivity.this, hd));
                    }else{
                        clickUrl=info.getGoodsUrl();
                        ThreadPollFactory.getNormalPool().execute(new ClickThread(info.getNumIid()+"",SearchNewGoodsAllActivity.this,hd));
                    }
                    //AlibcTrade.show(JinxuanActivity.this, new AlibcPage(info.getGoodsUrl()),alibcShowParams, alibcTaokeParams, exParams , new DemoTradeCallback());

                } else {
                    Toast.makeText(SearchNewGoodsAllActivity.this, "您的网络不给力，请检查更新!", Toast.LENGTH_LONG).show();
                }


            }
        });
        search_et_hint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(BooleanThread.toService){
                    if (NetJudgeUtils.getNetConnection(SearchNewGoodsAllActivity.this)){
                        ThreadPollFactory.getNormalPool().execute(new StatisticsThread(SearchNewGoodsAllActivity.this,6));
                    }
                }
            }
        });

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

    /**
     * 的到圈值接口
     */
    private class goodsThread implements Runnable {

        String keyword;
        int type;
        int order;

        public goodsThread(String keyword, int type, int order) {

            this.keyword = keyword;
            this.type = type;
            this.order = order;
        }

        @Override
        public void run() {
            try {
                String url = getResources().getString(R.string.appurltest) + "/goods2/outside" + "?keyword=" + URLEncoder.encode(URLEncoder.encode(keyword, "UTF-8"), "UTF-8") + "&code=" + SplashActicity1.mQuanId + "&type=" + type + "&order=" + order + "&stamp=" + UrlUtils.getTime() + "&encode=" + UrlUtils.getEncode();

                String jsonData = HttpConBase.sendGet(url);
                Message msg = Message.obtain();
                msg.what = 3;
                msg.obj = jsonData;
                hd.sendMessage(msg);


            } catch (Exception e) {
                e.printStackTrace();

                hd.sendEmptyMessage(5);

            }

        }
    }

    private class MyHandler extends Handler {
        private WeakReference<SearchNewGoodsAllActivity> mWeakReference = null;

        MyHandler(SearchNewGoodsAllActivity activity) {
            mWeakReference = new WeakReference<SearchNewGoodsAllActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SearchNewGoodsAllActivity mainActivity = mWeakReference.get();
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


            if (msg.what == 3) {
                try {
                    JSONObject j = new JSONObject((String) msg.obj);
                    String code = j.getString("code");
                    if (code.equals("0")) {
                        String data = j.getString("data");
                        JSONObject jData = new JSONObject(data);
                        String goods = jData.getString("goods");
                        if (goods.equals("[]") || null == goods || goods.equals("null")) {
                            all_search_rl_listview.setVisibility(View.GONE);
                            all_search_nogoods.setVisibility(View.VISIBLE);
                            all_search_ll_nowife.setVisibility(View.GONE);
                            all_search_rl_pb.setVisibility(View.GONE);
                            cp_image.innerStop();
                        } else {
                            goodList.clear();
                            goodList = ParseJsonCommon.parseJsonData(goods,
                                    GoodsOutInfo.class);

                            youhuiList.clear();
                            for (int i = 0; i<goodList.size(); i++) {
                                GoodsOutInfo info1 = (GoodsOutInfo) goodList.get(i);
                                if (info1.getGoodsType().equals("1")) {
                                    youhuiList.add(info1);
                                }

                            }
                            if (visiteType == 2) {
                                if (goodList.size() > 0) {
                                    all_search_rl_listview.setVisibility(View.VISIBLE);
                                    all_search_nogoods.setVisibility(View.GONE);
                                    all_search_ll_nowife.setVisibility(View.GONE);
                                    all_search_rl_pb.setVisibility(View.GONE);
                                    cp_image.innerStop();
                                    adapter = new MyAdapter(mainActivity);
                                    all_search_listview.setAdapter(adapter);
                                } else {
                                    all_search_rl_listview.setVisibility(View.GONE);
                                    all_search_nogoods.setVisibility(View.VISIBLE);
                                    all_search_ll_nowife.setVisibility(View.GONE);
                                    all_search_rl_pb.setVisibility(View.GONE);
                                    cp_image.innerStop();
                                }
                            } else {
                                if (youhuiList.size() > 0) {
                                    all_search_rl_listview.setVisibility(View.VISIBLE);
                                    all_search_nogoods.setVisibility(View.GONE);
                                    all_search_ll_nowife.setVisibility(View.GONE);
                                    all_search_rl_pb.setVisibility(View.GONE);
                                    cp_image.innerStop();
                                    adapter = new MyAdapter(mainActivity);
                                    all_search_listview.setAdapter(adapter);
                                } else {
                                    all_search_rl_listview.setVisibility(View.GONE);
                                    all_search_nogoods.setVisibility(View.VISIBLE);
                                    all_search_ll_nowife.setVisibility(View.GONE);
                                    all_search_rl_pb.setVisibility(View.GONE);
                                    cp_image.innerStop();
                                }
                            }


                        }


                    } else {
                        all_search_rl_listview.setVisibility(View.GONE);
                        all_search_nogoods.setVisibility(View.GONE);
                        all_search_ll_nowife.setVisibility(View.VISIBLE);
                        all_search_rl_pb.setVisibility(View.GONE);
                        cp_image.innerStop();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    all_search_rl_listview.setVisibility(View.GONE);
                    all_search_nogoods.setVisibility(View.GONE);
                    all_search_ll_nowife.setVisibility(View.VISIBLE);
                    all_search_rl_pb.setVisibility(View.GONE);
                    cp_image.innerStop();
                }
            }

            if (msg.what == 5) {
                all_search_rl_listview.setVisibility(View.GONE);
                all_search_nogoods.setVisibility(View.GONE);
                all_search_ll_nowife.setVisibility(View.VISIBLE);
                all_search_rl_pb.setVisibility(View.GONE);
                cp_image.innerStop();
            }

            if (msg.what == 11) {
                try {
                    JSONObject j = new JSONObject((String) msg.obj);
                    String code = j.getString("code");
                    if (code.equals("0")) {
                        String data = j.getString("data");
                        //  data="http:www.host.com";
                        //
                        //                                              if(TextUtils.isEmpty(data)){
                        //                                                   cmb.setText("复制的地址为空");
                        //                                               }else{
                        //                                                   cmb.setText("栈外优惠券地址为："+data);
                        //                                               }
                        //                                               Toast.makeText(SearchYouhuiNewAllActivity.this, "优惠券地址已粘贴!", Toast.LENGTH_LONG).show();


                        // System.out.println("data优惠="+data);
                        if (NetJudgeUtils.getNetConnection(mainActivity)) {
                            if (TextUtils.isEmpty(data)) {
                                AlibcTrade.show(mainActivity, new AlibcDetailPage(StringUtils.getID(turnGoodsId)), alibcShowParams, alibcTaokeParams, exParams, new DemoTradeCallback());

                            } else {
                                AlibcTrade.show(mainActivity, new AlibcPage(data), alibcShowParams, null, exParams, new DemoTradeCallback());
                                //AlibaPageDetails
                            }
                        } else {
                            Toast.makeText(mainActivity, "您的网络不给力，请检查更新!", Toast.LENGTH_LONG).show();

                        }


                        // AlibcTrade.show(JuhuasuanActivity.this, new AlibcDetailPage(StringUtils.getID(turnGoodsId)), alibcShowParams, alibcTaokeParams, exParams , new DemoTradeCallback());


                    } else {
                        if (NetJudgeUtils.getNetConnection(mainActivity)) {
                            AlibcTrade.show(mainActivity, new AlibcDetailPage(StringUtils.getID(turnGoodsId)), alibcShowParams, alibcTaokeParams, exParams, new DemoTradeCallback());

                        } else {
                            Toast.makeText(mainActivity, "您的网络不给力，请检查更新!", Toast.LENGTH_LONG).show();

                        }

                        //  Toast.makeText(JuhuasuanActivity.this, "服务器错误!", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (NetJudgeUtils.getNetConnection(mainActivity)) {
                        AlibcTrade.show(mainActivity, new AlibcDetailPage(StringUtils.getID(turnGoodsId)), alibcShowParams, alibcTaokeParams, exParams, new DemoTradeCallback());
                    } else {
                        Toast.makeText(mainActivity, "您的网络不给力，请检查更新!", Toast.LENGTH_LONG).show();

                    }
                } finally {
                    CustomProgressDialog.stopProgressDialog();
                }
            }
            if (msg.what == 12) {
                CustomProgressDialog.stopProgressDialog();
                if (NetJudgeUtils.getNetConnection(mainActivity)) {
                    AlibcTrade.show(mainActivity, new AlibcDetailPage(StringUtils.getID(turnGoodsId)), alibcShowParams, alibcTaokeParams, exParams, new DemoTradeCallback());
                } else {
                    Toast.makeText(mainActivity, "您的网络不给力，请检查更新!", Toast.LENGTH_LONG).show();

                }
            }
            if(msg.what==100){
                try {
                    JSONObject j = new JSONObject((String) msg.obj);
                    String code = j.getString("code");
                    if (code.equals("0")) {
                        String data = j.getString("data");
                        if(TextUtils.isEmpty(data)){
                            if(NetJudgeUtils.getNetConnection(mainActivity)){
                                //    Toast.makeText(JinxuanActivity.this,"本地转链地址!",Toast.LENGTH_LONG).show();
                                AlibcTrade.show(mainActivity, new AlibcPage(clickUrl),alibcShowParams, alibcTaokeParams, exParams , new DemoTradeCallback());
                            }else{
                                Toast.makeText(mainActivity,"您的网络不给力，请检查更新!",Toast.LENGTH_LONG).show();
                            }
                        }else{
                            if(NetJudgeUtils.getNetConnection(mainActivity)){
                                //Toast.makeText(JinxuanActivity.this,"后台转链地址!",Toast.LENGTH_LONG).show();
                                AlibcTrade.show(mainActivity, new AlibcPage(data),alibcShowParams, null, exParams , new DemoTradeCallback());
                            }else{
                                Toast.makeText(mainActivity,"您的网络不给力，请检查更新!",Toast.LENGTH_LONG).show();
                            }
                        }


                    }else{
                        if(NetJudgeUtils.getNetConnection(mainActivity)){
                            // Toast.makeText(JinxuanActivity.this,"本地转链地址!",Toast.LENGTH_LONG).show();
                            AlibcTrade.show(mainActivity, new AlibcPage(clickUrl),alibcShowParams, alibcTaokeParams, exParams , new DemoTradeCallback());
                        }else{
                            Toast.makeText(mainActivity,"您的网络不给力，请检查更新!",Toast.LENGTH_LONG).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if(NetJudgeUtils.getNetConnection(mainActivity)){
                        // Toast.makeText(JinxuanActivity.this,"本地转链地址!",Toast.LENGTH_LONG).show();
                        AlibcTrade.show(mainActivity, new AlibcPage(clickUrl),alibcShowParams, alibcTaokeParams, exParams , new DemoTradeCallback());
                    }else{
                        Toast.makeText(mainActivity,"您的网络不给力，请检查更新!",Toast.LENGTH_LONG).show();
                    }

                } finally {
                    CustomProgressDialog.stopProgressDialog();
                }
            }
            if(msg.what==101){
                if(NetJudgeUtils.getNetConnection(mainActivity)){
                    // Toast.makeText(JinxuanActivity.this,"本地转链地址!",Toast.LENGTH_LONG).show();
                    AlibcTrade.show(mainActivity, new AlibcPage(clickUrl),alibcShowParams, alibcTaokeParams, exParams , new DemoTradeCallback());
                }else{
                    Toast.makeText(mainActivity,"您的网络不给力，请检查更新!",Toast.LENGTH_LONG).show();
                }
                CustomProgressDialog.stopProgressDialog();

            }
        }
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
            if (visiteType == 2) {
                return goodList.size();
            } else {
                return youhuiList.size();
            }


        }

        @Override
        public Object getItem(int position) {
            if (visiteType == 2) {
                return goodList.get(position);
            } else {
                return youhuiList.get(position);
            }

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
                view = inflater.inflate(R.layout.goodnew_item, null);
                holder = new GoodHodler();
                holder.good_img = (ImageView) view.findViewById(R.id.good_img);
                holder.goods_desc = (TextView) view.findViewById(R.id.goods_desc);
                holder.goods_discount_price = (TextView) view.findViewById(R.id.goods_discount_price);
                //     holder.goods_ori_price = (TextView) view.findViewById(R.id.goods_ori_price);
                holder.tv_chengji = (TextView) view.findViewById(R.id.tv_chengji);
                holder.home_temai_shopname = (TextView) view.findViewById(R.id.home_temai_shopname);
                holder.home_temai_discount = (TextView) view.findViewById(R.id.home_temai_discount);
                holder.iv_dis_pic = (ImageView) view.findViewById(R.id.iv_dis_pic);
                holder.ll_discount = (LinearLayout) view.findViewById(R.id.ll_discount);
                holder.tv_fuhao = (TextView) view.findViewById(R.id.tv_fuhao);
                //                holder.goods_ori_price.getPaint().setFlags(
                //                        Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
                view.setTag(holder);
            } else {
                view = convertView;
                holder = (GoodHodler) convertView.getTag();
            }
            final GoodsOutInfo goodsInfo;
            if (visiteType == 2) {
                goodsInfo = (GoodsOutInfo) goodList.get(position);
            } else {
                goodsInfo = (GoodsOutInfo) youhuiList.get(position);
            }
            holder.good_img.setLayoutParams(params);
            PicassoUtils.loadImageWithHolderAndError(SearchNewGoodsAllActivity.this, goodsInfo.getGoodsImage(), R.mipmap.picture, R.mipmap.picture, holder.good_img);
            if (goodsInfo.getGoodsType().equals("1")) {
                holder.iv_dis_pic.setVisibility(View.VISIBLE);
                holder.ll_discount.setVisibility(View.VISIBLE);
                holder.home_temai_discount.setText(goodsInfo.getSavePrice());
                holder.goods_discount_price.setTextColor(getResources().getColor(R.color.red));
                holder.tv_fuhao.setTextColor(getResources().getColor(R.color.red));
            } else {
                holder.iv_dis_pic.setVisibility(View.GONE);
                holder.ll_discount.setVisibility(View.GONE);
                holder.goods_discount_price.setTextColor(getResources().getColor(R.color.newzigrey));
                holder.tv_fuhao.setTextColor(getResources().getColor(R.color.newzigrey));
            }

            holder.goods_desc.setText(goodsInfo.getTitle());
            holder.goods_discount_price.setText(goodsInfo.getPrice());
            // holder.goods_ori_price.setText("￥" + goodsInfo.getOldPrice());
            holder.tv_chengji.setText("已售" + goodsInfo.getSellCount() + "件");

            // holder.home_temai_shopname.setText(goodsInfo.getGoodsShop());
            return view;
        }

    }

    class GoodHodler {
        ImageView good_img;
        TextView goods_desc;
        TextView goods_discount_price;
        // TextView goods_ori_price;
        TextView tv_chengji;
        TextView home_temai_shopname;
        TextView home_temai_discount;
        ImageView iv_dis_pic;
        LinearLayout ll_discount;
        TextView tv_fuhao;

    }

    public void onResume() {
        super.onResume();
        AndroidUtils.hideInput(SearchNewGoodsAllActivity.this, search_et_hint);
        MobclickAgent.onPageStart("SearchNewGoodsAllActivity");
        MobclickAgent.onResume(this);

    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("SearchNewGoodsAllActivity");
        MobclickAgent.onPause(this);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        CustomProgressDialog.stopProgressDialog();
    }
}
