package com.yiqi.choose.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
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
import com.yiqi.choose.model.HotInfo;
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
import com.yiqi.choose.view.AutoNewLineLayout;
import com.yiqi.choose.view.XListView;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by moumou on 17/8/14.
 */

public class SearchYouhuiActivity extends BaseActivity {
    AutoNewLineLayout autoNewLineLayout;
    EditText search_et_hint;
    LinearLayout ll_back;
    LinearLayout youhui_search_guanjianci;
    LinearLayout youhui_serch_ll_type;
    List<Object> hotci;
    LinearLayout youhui_tv_search;
    TextView youhui_search_bt_retryweb;

    //搜索定标蓝
    LinearLayout youhui_search_ll_zonghe;
    TextView youhui_search_tv_zonghe;
    LinearLayout youhui_search_ll_seller;
    TextView youhui_search_tv_seller;
    LinearLayout youhui_search_ll_price;
    TextView youhui_search_tv_price;
    ImageView youhui_search_iv_price;

    private int priceLevel = 0;
    private int type = 0;
    private int order = 0;
    //goods list
    XListView listView;
    LinearLayout search_ll_nogoods;
    LinearLayout youhui_search_ll_nowife;
    RelativeLayout youhui_search_rl_pb;

    private String searchType = "0";
    private TextView search_tv_empty;
    int page = 1;
    int maxPage = 0;
    String time = "";
    private List<Object> goodList;
    private List<Object> ziList;
    private MyAdapter adapter;

    private String keyword = "";

    private RelativeLayout home_search_rl;
    private ImageView home_arraw;

    private AlibcShowParams alibcShowParams;//页面打开方式，默认，H5，Native
    private Map<String, String> exParams;//yhhpass参数
    private AlibcTaokeParams alibcTaokeParams = null;//淘客参数，包括pid，unionid，subPid
    private String clickUrl="";
   // ClipboardManager cmb;
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
        setContentView(R.layout.youhui_search);

        alibcTaokeParams = new AlibcTaokeParams(SplashActicity1.mPId, "", "");
        alibcShowParams = new AlibcShowParams(OpenType.Native, false);
        alibcShowParams.setClientType("taobao_scheme");
        exParams = new HashMap<>();
        exParams.put("isv_code", "appisvcode");
        exParams.put("alibaba", "阿里巴巴");//自定义参数部分，可任意增删改
        clickUrl="";
        Intent intent = getIntent();
        order = 0;
        searchType = intent.getStringExtra("searchType");
        hotci = new ArrayList<Object>();
        home_arraw=(ImageView)findViewById(R.id.home_arraw);
        home_search_rl=(RelativeLayout)findViewById(R.id.home_search_rl);


        search_tv_empty = (TextView) findViewById(R.id.search_tv_empty);
        autoNewLineLayout = (AutoNewLineLayout) findViewById(R.id.search_tag);
        youhui_search_guanjianci = (LinearLayout) findViewById(R.id.youhui_search_guanjianci);
        search_et_hint = (EditText) findViewById(R.id.search_et_hint);
        search_et_hint.addTextChangedListener(watcher);
        ll_back = (LinearLayout) this.findViewById(R.id.ll_back);
        youhui_serch_ll_type = (LinearLayout) this.findViewById(R.id.youhui_serch_ll_type);
        youhui_search_ll_zonghe = (LinearLayout) this.findViewById(R.id.youhui_search_ll_zonghe);
        youhui_search_tv_zonghe = (TextView) this.findViewById(R.id.youhui_search_tv_zonghe);
        youhui_search_ll_seller = (LinearLayout) this.findViewById(R.id.youhui_search_ll_seller);
        youhui_search_tv_seller = (TextView) this.findViewById(R.id.youhui_search_tv_seller);
        youhui_search_ll_price = (LinearLayout) this.findViewById(R.id.youhui_search_ll_price);
        youhui_search_tv_price = (TextView) this.findViewById(R.id.youhui_search_tv_price);
        youhui_search_iv_price = (ImageView) this.findViewById(R.id.youhui_search_iv_price);
        youhui_tv_search = (LinearLayout) this.findViewById(R.id.youhui_tv_search);
        youhui_search_bt_retryweb = (TextView) this.findViewById(R.id.home_fragment_tv_retro);
        listView = (XListView) this.findViewById(R.id.search_youhui_listview);
        search_ll_nogoods = (LinearLayout) this.findViewById(R.id.search_nogoods);
        youhui_search_ll_nowife = (LinearLayout) this.findViewById(R.id.youhui_search_ll_nowife);
        youhui_search_rl_pb = (RelativeLayout) this.findViewById(R.id.youhui_search_rl_pb);
      //  cmb = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        goodList = new ArrayList<Object>();
        ziList = new ArrayList<Object>();
        if (searchType.equals("1")) {//goods
            search_et_hint.setHint("搜索商品");
            search_tv_empty.setText("未找到相关商品");
            if (NetJudgeUtils.getNetConnection(SearchYouhuiActivity.this)) {
                youhui_serch_ll_type.setVisibility(View.GONE);
                youhui_search_guanjianci.setVisibility(View.GONE);
                home_search_rl.setVisibility(View.GONE);
                search_ll_nogoods.setVisibility(View.GONE);
                youhui_search_ll_nowife.setVisibility(View.GONE);
                youhui_search_rl_pb.setVisibility(View.VISIBLE);
                ThreadPollFactory.getNormalPool().execute(new hotThread());
            } else {
                youhui_serch_ll_type.setVisibility(View.GONE);
                youhui_search_guanjianci.setVisibility(View.GONE);
                home_search_rl.setVisibility(View.GONE);
                search_ll_nogoods.setVisibility(View.GONE);
                youhui_search_ll_nowife.setVisibility(View.GONE);
                youhui_search_rl_pb.setVisibility(View.GONE);
            }
        } else if (searchType.equals("2")) {//youhui
            search_et_hint.setHint("搜索商品");
            search_tv_empty.setText("未找到相关优惠券");
        }


        ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchYouhuiActivity.this.finish();
                overridePendingTransition(R.anim.in_from_left, R.anim.out_of_right);
            }
        });
        //addTags();
        autoNewLineLayout.setOnTouchSubprojectListener(new AutoNewLineLayout.OnTouchSubprojectListener() {
            @Override
            public void onTouchSubproject(int position) {
                //执行线程
                if (NetJudgeUtils.getNetConnection(SearchYouhuiActivity.this)) {
                    HotInfo info = (HotInfo) hotci.get(position);
                    search_et_hint.setText(info.getKeyword());
                    search_et_hint.setSelection(info.getKeyword().length());

                    AndroidUtils.hideInput(SearchYouhuiActivity.this, search_et_hint);
                    //bianhuan
                    youhui_serch_ll_type.setVisibility(View.VISIBLE);
                    youhui_search_guanjianci.setVisibility(View.GONE);
                    home_search_rl.setVisibility(View.GONE);
                    search_ll_nogoods.setVisibility(View.GONE);
                    youhui_search_ll_nowife.setVisibility(View.GONE);
                    youhui_search_rl_pb.setVisibility(View.VISIBLE);
                    type = 0;
                    priceLevel = 0;
                    order = 0;
                    keyword = info.getKeyword();
                    ThreadPollFactory.getNormalPool().execute(new goodsThread(SplashActicity1.mQuanId, "", order + "", keyword, 1));

                } else {
                    Toast.makeText(SearchYouhuiActivity.this, R.string.net, Toast.LENGTH_LONG).show();
                }

            }
        });

        youhui_search_bt_retryweb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetJudgeUtils.getNetConnection(SearchYouhuiActivity.this)) {
                    youhui_serch_ll_type.setVisibility(View.VISIBLE);
                    youhui_search_guanjianci.setVisibility(View.GONE);
                    home_search_rl.setVisibility(View.GONE);
                    search_ll_nogoods.setVisibility(View.GONE);
                    youhui_search_ll_nowife.setVisibility(View.GONE);
                    youhui_search_rl_pb.setVisibility(View.VISIBLE);
                    ThreadPollFactory.getNormalPool().execute(new goodsThread(SplashActicity1.mQuanId, "", order + "", keyword, 1));

                }
            }
        });
        youhui_search_ll_zonghe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetJudgeUtils.getNetConnection(SearchYouhuiActivity.this)){
                    if (order != 0) {
                        youhui_search_tv_zonghe.setTextColor(getResources().getColor(R.color.red));
                        youhui_search_tv_price.setTextColor(getResources().getColor(R.color.ziblack));
                        youhui_search_iv_price.setImageResource(R.mipmap.price_nor);
                        youhui_search_tv_seller.setTextColor(getResources().getColor(R.color.ziblack));
                        priceLevel = 0;
                        order = 0;
                        if (NetJudgeUtils.getNetConnection(SearchYouhuiActivity.this)) {
                            youhui_serch_ll_type.setVisibility(View.VISIBLE);
                            youhui_search_guanjianci.setVisibility(View.GONE);
                            home_search_rl.setVisibility(View.GONE);
                            search_ll_nogoods.setVisibility(View.GONE);
                            youhui_search_ll_nowife.setVisibility(View.GONE);
                            youhui_search_rl_pb.setVisibility(View.VISIBLE);
                            ThreadPollFactory.getNormalPool().execute(new goodsThread(SplashActicity1.mQuanId, "", order + "", keyword, 1));
                        } else {
                            youhui_serch_ll_type.setVisibility(View.VISIBLE);
                            youhui_search_guanjianci.setVisibility(View.GONE);
                            home_search_rl.setVisibility(View.GONE);
                            search_ll_nogoods.setVisibility(View.GONE);
                            youhui_search_ll_nowife.setVisibility(View.VISIBLE);
                            youhui_search_rl_pb.setVisibility(View.GONE);
                        }
                    }
                }




            }
        });
        youhui_search_ll_seller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (order != 1) {
                    youhui_search_tv_seller.setTextColor(getResources().getColor(R.color.red));
                    youhui_search_tv_price.setTextColor(getResources().getColor(R.color.ziblack));
                    youhui_search_iv_price.setImageResource(R.mipmap.price_nor);
                    youhui_search_tv_zonghe.setTextColor(getResources().getColor(R.color.ziblack));
                    priceLevel = 0;
                    order = 1;
                    if (NetJudgeUtils.getNetConnection(SearchYouhuiActivity.this)) {
                        youhui_serch_ll_type.setVisibility(View.VISIBLE);
                        youhui_search_guanjianci.setVisibility(View.GONE);
                        home_search_rl.setVisibility(View.GONE);
                        search_ll_nogoods.setVisibility(View.GONE);
                        youhui_search_ll_nowife.setVisibility(View.GONE);
                        youhui_search_rl_pb.setVisibility(View.VISIBLE);
                        ThreadPollFactory.getNormalPool().execute(new goodsThread(SplashActicity1.mQuanId, "", order + "", keyword, 1));
                    } else {
                        youhui_serch_ll_type.setVisibility(View.VISIBLE);
                        youhui_search_guanjianci.setVisibility(View.GONE);
                        home_search_rl.setVisibility(View.GONE);
                        search_ll_nogoods.setVisibility(View.GONE);
                        youhui_search_ll_nowife.setVisibility(View.VISIBLE);
                        youhui_search_rl_pb.setVisibility(View.GONE);
                    }
                }

            }
        });
        youhui_search_ll_price.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                youhui_search_tv_seller.setTextColor(getResources().getColor(R.color.ziblack));
                youhui_search_tv_price.setTextColor(getResources().getColor(R.color.red));
                youhui_search_tv_zonghe.setTextColor(getResources().getColor(R.color.ziblack));
                if (priceLevel == 0 || priceLevel == 2) {
                    priceLevel = 1;
                    order = 3;
                    youhui_search_iv_price.setImageResource(R.mipmap.price_up);
                } else {
                    order = 2;
                    priceLevel = 2;
                    youhui_search_iv_price.setImageResource(R.mipmap.price_down);
                }
                if (NetJudgeUtils.getNetConnection(SearchYouhuiActivity.this)) {
                    youhui_serch_ll_type.setVisibility(View.VISIBLE);
                    youhui_search_guanjianci.setVisibility(View.GONE);
                    home_search_rl.setVisibility(View.GONE);
                    search_ll_nogoods.setVisibility(View.GONE);
                    youhui_search_ll_nowife.setVisibility(View.GONE);
                    youhui_search_rl_pb.setVisibility(View.VISIBLE);
                    ThreadPollFactory.getNormalPool().execute(new goodsThread(SplashActicity1.mQuanId, "", order + "", keyword, 1));
                } else {
                    youhui_serch_ll_type.setVisibility(View.VISIBLE);
                    youhui_search_guanjianci.setVisibility(View.GONE);
                    home_search_rl.setVisibility(View.GONE);
                    search_ll_nogoods.setVisibility(View.GONE);
                    youhui_search_ll_nowife.setVisibility(View.VISIBLE);
                    youhui_search_rl_pb.setVisibility(View.GONE);
                }

            }
        });
        youhui_tv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(search_et_hint.getText().toString().trim())) {
                    Toast.makeText(SearchYouhuiActivity.this, "请输入关键词!", Toast.LENGTH_LONG).show();
                    return;
                }

                //执行线程
                if (NetJudgeUtils.getNetConnection(SearchYouhuiActivity.this)) {

                    AndroidUtils.hideInput(SearchYouhuiActivity.this, search_et_hint);
                    //bianhuan
                    youhui_serch_ll_type.setVisibility(View.VISIBLE);
                    youhui_search_guanjianci.setVisibility(View.GONE);
                    youhui_search_tv_zonghe.setTextColor(getResources().getColor(R.color.red));
                    youhui_search_tv_price.setTextColor(getResources().getColor(R.color.ziblack));
                    youhui_search_iv_price.setImageResource(R.mipmap.price_nor);
                    youhui_search_tv_seller.setTextColor(getResources().getColor(R.color.ziblack));
                    priceLevel = 0;
                    order = 0;
                    home_search_rl.setVisibility(View.GONE);
                    search_ll_nogoods.setVisibility(View.GONE);
                    youhui_search_ll_nowife.setVisibility(View.GONE);
                    youhui_search_rl_pb.setVisibility(View.VISIBLE);
                    keyword = search_et_hint.getText().toString().trim();
                    ThreadPollFactory.getNormalPool().execute(new goodsThread(SplashActicity1.mQuanId, "", order + "",keyword, 1));
                } else {
                    Toast.makeText(SearchYouhuiActivity.this, R.string.net, Toast.LENGTH_LONG).show();
                }
            }
        });
        listView.setXListViewListener(new XListView.IXListViewListener() {
            public void onRefresh() {
                if (NetJudgeUtils.getNetConnection(SearchYouhuiActivity.this)) {
                    ThreadPollFactory.getNormalPool().execute(new goodsThread(SplashActicity1.mQuanId, "", order + "", keyword, 1));
                } else {
                    hd.sendEmptyMessage(7);
                }
            }

            public void onLoadMore() {
                if (listView.getProgressState()) {
                    if (NetJudgeUtils.getNetConnection(SearchYouhuiActivity.this)) {
                        page++;
                        ThreadPollFactory.getNormalPool().execute(new goodsThread(SplashActicity1.mQuanId, (String) SharedPfUtils.getData(SearchYouhuiActivity.this, "goodssearchtime", ""), order + "", keyword, page));
                    } else {
                        hd.sendEmptyMessage(8);
                    }


                }

                //

            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(NetJudgeUtils.getNetConnection(SearchYouhuiActivity.this)){
                    CustomProgressDialog.createDialog(SearchYouhuiActivity.this,"");
                    GoodsInfo info=(GoodsInfo)goodList.get(position-1);
                    clickUrl=info.getGoodsUrl();
                    ThreadPollFactory.getNormalPool().execute(new ClickThread(info.getNumIid()+"",SearchYouhuiActivity.this,hd));
                    //AlibcTrade.show(JinxuanActivity.this, new AlibcPage(info.getGoodsUrl()),alibcShowParams, alibcTaokeParams, exParams , new DemoTradeCallback());

                }else{
                    Toast.makeText(SearchYouhuiActivity.this,"您的网络不给力，请检查更新!",Toast.LENGTH_LONG).show();
                }




            }
        });
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // TODO Auto-generated method stub
                final Picasso picasso = Picasso.with(SearchYouhuiActivity.this);
                if (scrollState == SCROLL_STATE_IDLE || scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                    picasso.resumeTag(SearchYouhuiActivity.this);
                } else {
                    picasso.pauseTag(SearchYouhuiActivity.this);
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
                listView.setSelection(0);
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
        String keyword;

        public goodsThread(String code, String timeStemp, String order, String keyword, int page) {
            this.code = code;
            this.timeStemp = timeStemp;
            this.keyword = keyword;
            this.page = page;
            this.order = order;
        }

        @Override
        public void run() {
            try {
                String url = getResources().getString(R.string.appurl) + "/goods/search" + "?code=" + code + "&timeStemp=" + timeStemp + "&order=" + order + "&page=" + page + "&keyword=" + URLEncoder.encode(keyword, "UTF-8")+"&stamp="+ UrlUtils.getTime()+"&encode="+UrlUtils.getEncode();
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
            if (msg.what == 0) {
                try {
                    JSONObject json = new JSONObject((String) msg.obj);
                    String code = json.getString("code");
                    if (code.equals("0")) {
                        String data = json.getString("data");
                        hotci.clear();
                        hotci = ParseJsonCommon.parseJsonData(data,
                                HotInfo.class);
                        addTags();
                        youhui_serch_ll_type.setVisibility(View.GONE);
                        youhui_search_guanjianci.setVisibility(View.VISIBLE);
                        home_search_rl.setVisibility(View.GONE);
                        search_ll_nogoods.setVisibility(View.GONE);
                        youhui_search_ll_nowife.setVisibility(View.GONE);
                        youhui_search_rl_pb.setVisibility(View.GONE);

                    } else {
                        youhui_serch_ll_type.setVisibility(View.GONE);
                        youhui_search_guanjianci.setVisibility(View.GONE);
                        home_search_rl.setVisibility(View.GONE);
                        search_ll_nogoods.setVisibility(View.GONE);
                        youhui_search_ll_nowife.setVisibility(View.GONE);
                        youhui_search_rl_pb.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    youhui_serch_ll_type.setVisibility(View.GONE);
                    youhui_search_guanjianci.setVisibility(View.GONE);
                    home_search_rl.setVisibility(View.GONE);
                    search_ll_nogoods.setVisibility(View.GONE);
                    youhui_search_ll_nowife.setVisibility(View.GONE);
                    youhui_search_rl_pb.setVisibility(View.GONE);
                }


            }
            if (msg.what == 10) {
                youhui_serch_ll_type.setVisibility(View.GONE);
                youhui_search_guanjianci.setVisibility(View.GONE);
                home_search_rl.setVisibility(View.GONE);
                search_ll_nogoods.setVisibility(View.GONE);
                youhui_search_ll_nowife.setVisibility(View.GONE);
                youhui_search_rl_pb.setVisibility(View.GONE);
            }
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
                        SharedPfUtils.saveStringData(SearchYouhuiActivity.this, "goodssearchtime", time);
                        if (goods.equals("[]") || null == goods || goods.equals("null")) {
                            home_search_rl.setVisibility(View.GONE);
                            search_ll_nogoods.setVisibility(View.VISIBLE);
                            youhui_search_ll_nowife.setVisibility(View.GONE);
                            youhui_search_rl_pb.setVisibility(View.GONE);
                        } else {
                            goodList.clear();
                            goodList = ParseJsonCommon.parseJsonData(goods,
                                    GoodsInfo.class);

                            if (goodList.size() > 0) {
                                home_search_rl.setVisibility(View.VISIBLE);
                                search_ll_nogoods.setVisibility(View.GONE);
                                youhui_search_ll_nowife.setVisibility(View.GONE);
                                youhui_search_rl_pb.setVisibility(View.GONE);
                                if (maxPage > 1) {
                                    listView.setPullLoadEnable(true);
                                } else {
                                    listView.setPullLoadEnable(false);
                                }
                                adapter = new MyAdapter(SearchYouhuiActivity.this);

                                listView.setAdapter(adapter);
                                listView.stopRefresh();
                                listView.setRefreshTime(new Date().toLocaleString());
                            } else {
                                home_search_rl.setVisibility(View.GONE);
                                search_ll_nogoods.setVisibility(View.VISIBLE);
                                youhui_search_ll_nowife.setVisibility(View.GONE);
                                youhui_search_rl_pb.setVisibility(View.GONE);
                            }
                        }


                    } else {

                        home_search_rl.setVisibility(View.GONE);
                        search_ll_nogoods.setVisibility(View.GONE);
                        youhui_search_ll_nowife.setVisibility(View.VISIBLE);
                        youhui_search_rl_pb.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    home_search_rl.setVisibility(View.GONE);
                    search_ll_nogoods.setVisibility(View.GONE);
                    youhui_search_ll_nowife.setVisibility(View.VISIBLE);
                    youhui_search_rl_pb.setVisibility(View.GONE);
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
                            listView.setPullLoadEnable(false);
                        } else {
                            listView.setPullLoadEnable(true);
                        }
                        adapter.notifyDataSetChanged();// 告诉listView数据发生改变，要求listView更新显示
                        listView.stopRefresh();
                        listView.stopLoadMore();
                    } else {
                        if (page > 1) {
                            page--;
                        }
                        listView.stopRefresh();
                        listView.stopLoadMore();
                        Toast.makeText(SearchYouhuiActivity.this, "数据返回错误!", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    listView.stopRefresh();
                    listView.stopLoadMore();
                }
            }
            if (msg.what == 5) {
                listView.stopRefresh();
                listView.stopLoadMore();
                home_search_rl.setVisibility(View.GONE);
                search_ll_nogoods.setVisibility(View.GONE);
                youhui_search_ll_nowife.setVisibility(View.VISIBLE);
                youhui_search_rl_pb.setVisibility(View.GONE);
            }
            if (msg.what == 6) {
                Toast.makeText(SearchYouhuiActivity.this, "数据返回错误!", Toast.LENGTH_SHORT).show();
                if (page > 1) {
                    page--;
                }
                listView.stopLoadMore();
            }
            if (msg.what == 7) {
                MyToast.show(SearchYouhuiActivity.this, 0, AndroidUtils.getWidth(SearchYouhuiActivity.this),
                        AndroidUtils.dip2px(SearchYouhuiActivity.this, 45), "您的网络不给力，请检查更新!");
                listView.stopRefresh();
            }
            if (msg.what == 8) {

                listView.stopLoadMore();

            }
            if(msg.what==100){
                try {
                    JSONObject j = new JSONObject((String) msg.obj);
                    String code = j.getString("code");
                    if (code.equals("0")) {
                        String data = j.getString("data");
//                        if(TextUtils.isEmpty(data)){
//                            cmb.setText("复制的地址为空");
//                        }else{
//                            cmb.setText("商品地址为："+data);
//                        }
//                        Toast.makeText(SearchYouhuiActivity.this, "优惠券地址已粘贴!", Toast.LENGTH_LONG).show();

                        if(TextUtils.isEmpty(data)){
                            if(NetJudgeUtils.getNetConnection(SearchYouhuiActivity.this)){
                                //Toast.makeText(SearchYouhuiActivity.this,"本地转链地址!",Toast.LENGTH_LONG).show();

                                AlibcTrade.show(SearchYouhuiActivity.this, new AlibcPage(clickUrl),alibcShowParams, alibcTaokeParams, exParams , new DemoTradeCallback());
                            }else{
                                Toast.makeText(SearchYouhuiActivity.this,"您的网络不给力，请检查更新!",Toast.LENGTH_LONG).show();
                            }
                        }else{
                            if(NetJudgeUtils.getNetConnection(SearchYouhuiActivity.this)){
                              //  Toast.makeText(SearchYouhuiActivity.this,"后台转链地址!",Toast.LENGTH_LONG).show();
                                AlibcTrade.show(SearchYouhuiActivity.this, new AlibcPage(data),alibcShowParams, null, exParams , new DemoTradeCallback());
                            }else{
                                Toast.makeText(SearchYouhuiActivity.this,"您的网络不给力，请检查更新!",Toast.LENGTH_LONG).show();
                            }
                        }


                    }else{
                        if(NetJudgeUtils.getNetConnection(SearchYouhuiActivity.this)){
                            //Toast.makeText(SearchYouhuiActivity.this,"本地转链地址!",Toast.LENGTH_LONG).show();
                            AlibcTrade.show(SearchYouhuiActivity.this, new AlibcPage(clickUrl),alibcShowParams, alibcTaokeParams, exParams , new DemoTradeCallback());
                        }else{
                            Toast.makeText(SearchYouhuiActivity.this,"您的网络不给力，请检查更新!",Toast.LENGTH_LONG).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if(NetJudgeUtils.getNetConnection(SearchYouhuiActivity.this)){
                        //Toast.makeText(SearchYouhuiActivity.this,"本地转链地址!",Toast.LENGTH_LONG).show();
                        AlibcTrade.show(SearchYouhuiActivity.this, new AlibcPage(clickUrl),alibcShowParams, alibcTaokeParams, exParams , new DemoTradeCallback());
                    }else{
                        Toast.makeText(SearchYouhuiActivity.this,"您的网络不给力，请检查更新!",Toast.LENGTH_LONG).show();
                    }

                } finally {
                    CustomProgressDialog.stopProgressDialog();
                }
            }
            if(msg.what==101){
                if(NetJudgeUtils.getNetConnection(SearchYouhuiActivity.this)){
                   // Toast.makeText(SearchYouhuiActivity.this,"本地转链地址!",Toast.LENGTH_LONG).show();
                    AlibcTrade.show(SearchYouhuiActivity.this, new AlibcPage(clickUrl),alibcShowParams, alibcTaokeParams, exParams , new DemoTradeCallback());
                }else{
                    Toast.makeText(SearchYouhuiActivity.this,"您的网络不给力，请检查更新!",Toast.LENGTH_LONG).show();
                }
                CustomProgressDialog.stopProgressDialog();

            }
        }
    };

    /**
     * 的到圈值接口
     */
    private class hotThread implements Runnable {
        @Override
        public void run() {
            try {
                String url = getResources().getString(R.string.appurl) + "/home/keyword"+"?stamp=" + UrlUtils.getTime() + "&encode=" + UrlUtils.getEncode();
                String jsonData = HttpConBase.sendGet(url);
                Message msg = Message.obtain();

                msg.what = 0;
                msg.obj = jsonData;
                hd.sendMessage(msg);


            } catch (Exception e) {
                e.printStackTrace();
                hd.sendEmptyMessage(10);


            }

        }
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

    private TextWatcher watcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // TODO Auto-generated method stub

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub

        }

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub
            if (s.length() == 0) {
                youhui_serch_ll_type.setVisibility(View.GONE);
                youhui_search_guanjianci.setVisibility(View.VISIBLE);

                priceLevel = 0;
                type = 0;

            }

        }
    };

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("SearchYouhuiActivity");
        MobclickAgent.onResume(this);

    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("SearchYouhuiActivity");
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

    private void addTags() {
        for (int i = 0; i < hotci.size(); i++) {
            View view = View.inflate(SearchYouhuiActivity.this, R.layout.searchtext, null);
            TextView tv = (TextView) view.findViewById(R.id.search_text);
            HotInfo info = (HotInfo) hotci.get(i);
            tv.setText(info.getKeyword());
            autoNewLineLayout.addView(tv);
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
            PicassoUtils.loadImageWithHolderAndError(SearchYouhuiActivity.this, goodsInfo.getGoodsImage(), R.mipmap.picture, R.mipmap.picture, holder.good_img);
            holder.goods_desc.setText(goodsInfo.getTitle());
            holder.goods_discount_price.setText(goodsInfo.getPrice());
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
