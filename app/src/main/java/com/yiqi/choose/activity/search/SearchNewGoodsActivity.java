package com.yiqi.choose.activity.search;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
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
import com.yiqi.choose.model.GoodsInfo;
import com.yiqi.choose.thread.StatisticsThread;
import com.yiqi.choose.thread.TurnUrlThread;
import com.yiqi.choose.utils.AndroidUtils;
import com.yiqi.choose.utils.BooleanThread;
import com.yiqi.choose.utils.CustomProgressDialog;
import com.yiqi.choose.utils.HttpConBase;
import com.yiqi.choose.utils.MyToast;
import com.yiqi.choose.utils.NetJudgeUtils;
import com.yiqi.choose.utils.ParseJsonCommon;
import com.yiqi.choose.utils.PicassoUtils;
import com.yiqi.choose.utils.StringUtils;
import com.yiqi.choose.utils.SystemBarTintManager;
import com.yiqi.choose.utils.UrlUtils;
import com.yiqi.choose.view.XListView;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by moumou on 17/9/28.
 */

public class SearchNewGoodsActivity extends BaseActivity {
    LinearLayout ll_back;
    LinearLayout youhui_tv_search;
    EditText search_et_hint;

    LinearLayout all_search_ll_out;
    TextView all_search_tv_out;

    LinearLayout all_search_ll_quanbu;
    TextView all_search_tv_quanbu;

    LinearLayout all_search_ll_seller;
    TextView all_search_tv_seller;

    LinearLayout all_search_ll_price;
    TextView all_search_tv_price;
    ImageView all_search_iv_price;

    RelativeLayout all_search_rl_listview;
    ImageView all_search_home_arraw;
    XListView all_search_listview;

    LinearLayout all_search_nogoods;

    LinearLayout all_search_ll_nowife;
    TextView all_search_tv_retro;

    RelativeLayout all_search_rl_pb;
    CircularProgressView cp_image;
   //private String[] names = {"新品", "巨划算", "淘抢货", "巨划算／淘抢货"};
    int goodsType;
    int order;

//    private PopuAdapter popuAdapter;
//    private PopupWindow pop;
    private MyHandler hd;

    int page = 1;
    int maxPage = 0;

    private List<Object> goodList;
    private List<Object> ziList;
    MyAdapter adapter;

    private String turnGoodsId;
    private AlibcShowParams alibcShowParams;//页面打开方式，默认，H5，Native
    private Map<String, String> exParams;//yhhpass参数
    private AlibcTaokeParams alibcTaokeParams = null;//淘客参数，包括pid，unionid，subPid
    //ClipboardManager cmb;
    private String keyword;
    private ImageView iv_guanbi;
    private int srceenWidth=0;
    private  LinearLayout.LayoutParams params;
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
        setContentView(R.layout.searchnew);
        goodsType = 1;
        order = 0;

        ll_back = (LinearLayout) this.findViewById(R.id.ll_back);

        youhui_tv_search = (LinearLayout) this.findViewById(R.id.youhui_tv_search);
        search_et_hint = (EditText) this.findViewById(R.id.search_et_hint);
        if (SplashActicity1.sdCardWritePermisson) {
            dao = HistoryDao.getInstance(SearchNewGoodsActivity.this);
        }
        iv_guanbi=(ImageView)this.findViewById(R.id.iv_guanbi);

        all_search_ll_out = (LinearLayout) this.findViewById(R.id.all_search_ll_out);
        all_search_tv_out = (TextView) this.findViewById(R.id.all_search_tv_out);
        all_search_ll_quanbu = (LinearLayout) this.findViewById(R.id.all_search_ll_quanbu);
        all_search_tv_quanbu = (TextView) this.findViewById(R.id.all_search_tv_quanbu);

        all_search_ll_seller = (LinearLayout) this.findViewById(R.id.all_search_ll_seller);
        all_search_tv_seller = (TextView) this.findViewById(R.id.all_search_tv_seller);

        all_search_ll_price = (LinearLayout) this.findViewById(R.id.all_search_ll_price);
        all_search_tv_price = (TextView) this.findViewById(R.id.all_search_tv_price);
        all_search_iv_price = (ImageView) this.findViewById(R.id.all_search_iv_price);
        all_search_rl_listview = (RelativeLayout) this.findViewById(R.id.all_search_rl_listview);
        all_search_home_arraw = (ImageView) this.findViewById(R.id.all_search_home_arraw);
        all_search_listview = (XListView) this.findViewById(R.id.all_search_listview);

        all_search_nogoods = (LinearLayout) this.findViewById(R.id.all_search_nogoods);
        all_search_ll_nowife = (LinearLayout) this.findViewById(R.id.all_search_ll_nowife);
        all_search_tv_retro = (TextView) this.findViewById(R.id.all_search_tv_retro);

        all_search_rl_pb = (RelativeLayout) this.findViewById(R.id.cp_rl_image);
        cp_image=(CircularProgressView)this.findViewById(R.id.cp_image);
        //cmb = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
        Intent intent=getIntent();
        keyword=intent.getStringExtra("keyword");
        search_et_hint.setText(keyword);
        search_et_hint.setSelection(keyword.length());
        iv_guanbi.setVisibility(View.VISIBLE);
        goodList = new ArrayList<Object>();
        ziList = new ArrayList<Object>();
        srceenWidth=AndroidUtils.getWidth(SearchNewGoodsActivity.this);
        params=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params.width=(int)(srceenWidth*0.361);
        params.height=(int)(srceenWidth*0.361);


         initData();
    }



    private void initData() {
        hd = new MyHandler(SearchNewGoodsActivity.this);
        all_search_listview.setOverScrollMode(View.OVER_SCROLL_NEVER);
        alibcTaokeParams = new AlibcTaokeParams(SplashActicity1.mPId, "", "");
        alibcShowParams = new AlibcShowParams(OpenType.Native, false);
        alibcShowParams.setClientType("taobao_scheme");
        exParams = new HashMap<>();
        exParams.put("isv_code", "appisvcode");
        exParams.put("alibaba", "阿里巴巴");//自定义参数部分，可任意增删改
        AndroidUtils.hideInput(SearchNewGoodsActivity.this, search_et_hint);

        ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchNewGoodsActivity.this.finish();
                overridePendingTransition(R.anim.in_from_left, R.anim.out_of_right);
            }
        });
        all_search_tv_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(search_et_hint.getText().toString().trim())) {
                    Toast.makeText(SearchNewGoodsActivity.this, "关键词不能为空!", Toast.LENGTH_LONG).show();
                    return;
                }
                Intent intent = new Intent(SearchNewGoodsActivity.this,
                        SearchNewGoodsAllActivity.class);
                intent.putExtra("keyword", search_et_hint.getText().toString().trim());
                startActivity(intent);
                overridePendingTransition(
                        R.anim.to_right, R.anim.to_left);
            }
        });

        search_et_hint.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    iv_guanbi.setVisibility(View.VISIBLE);

                } else {
                    iv_guanbi.setVisibility(View.GONE);
                }
            }
        });
        iv_guanbi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_et_hint.setText("");
            }
        });
        //执行线程
        if (NetJudgeUtils.getNetConnection(SearchNewGoodsActivity.this)) {
            AndroidUtils.hideInput(SearchNewGoodsActivity.this, search_et_hint);
            all_search_rl_listview.setVisibility(View.GONE);
            all_search_nogoods.setVisibility(View.GONE);
            all_search_ll_nowife.setVisibility(View.GONE);
            all_search_rl_pb.setVisibility(View.VISIBLE);
            cp_image.innerStart();
            ThreadPollFactory.getNormalPool().execute(new goodsThread(keyword, 1));
        } else {
            all_search_rl_listview.setVisibility(View.GONE);
            all_search_nogoods.setVisibility(View.GONE);
            all_search_ll_nowife.setVisibility(View.VISIBLE);
            all_search_rl_pb.setVisibility(View.GONE);
            cp_image.innerStop();
        }
        youhui_tv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(search_et_hint.getText().toString().trim())) {
                    Toast.makeText(SearchNewGoodsActivity.this, "请输入关键词!", Toast.LENGTH_LONG).show();
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
                //执行线程
                if (NetJudgeUtils.getNetConnection(SearchNewGoodsActivity.this)) {
                    AndroidUtils.hideInput(SearchNewGoodsActivity.this, search_et_hint);
                    all_search_rl_listview.setVisibility(View.GONE);
                    all_search_nogoods.setVisibility(View.GONE);
                    all_search_ll_nowife.setVisibility(View.GONE);
                    all_search_rl_pb.setVisibility(View.VISIBLE);
                    cp_image.innerStart();
                    keyword=search_et_hint.getText().toString().trim();
                    ThreadPollFactory.getNormalPool().execute(new goodsThread(keyword, 1));
                } else {
                    all_search_rl_listview.setVisibility(View.GONE);
                    all_search_nogoods.setVisibility(View.GONE);
                    all_search_ll_nowife.setVisibility(View.VISIBLE);
                    all_search_rl_pb.setVisibility(View.GONE);
                    cp_image.innerStop();
                }


            }
        });
        all_search_ll_quanbu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(search_et_hint.getText().toString().trim())) {
                    Toast.makeText(SearchNewGoodsActivity.this, "请输入关键词!", Toast.LENGTH_LONG).show();
                    return;
                }
                AndroidUtils.hideInput(SearchNewGoodsActivity.this, search_et_hint);
                changeType();
            }
        });
        all_search_ll_seller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(search_et_hint.getText().toString().trim())) {
                    Toast.makeText(SearchNewGoodsActivity.this, "请输入关键词!", Toast.LENGTH_LONG).show();
                    return;
                }
                AndroidUtils.hideInput(SearchNewGoodsActivity.this, search_et_hint);

                order = 1;
                all_search_tv_quanbu.setTextColor(getResources().getColor(R.color.newzigrey));
                all_search_tv_price.setTextColor(getResources().getColor(R.color.newzigrey));
                all_search_tv_seller.setTextColor(getResources().getColor(R.color.red));
                all_search_iv_price.setImageDrawable(getResources().getDrawable(R.mipmap.price_nor));
                if (NetJudgeUtils.getNetConnection(SearchNewGoodsActivity.this)) {

                    all_search_rl_listview.setVisibility(View.GONE);
                    all_search_nogoods.setVisibility(View.GONE);
                    all_search_ll_nowife.setVisibility(View.GONE);
                    all_search_rl_pb.setVisibility(View.VISIBLE);
                    cp_image.innerStart();
                    keyword=search_et_hint.getText().toString().trim();
                    ThreadPollFactory.getNormalPool().execute(new goodsThread(keyword, 1));

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
                    Toast.makeText(SearchNewGoodsActivity.this, "请输入关键词!", Toast.LENGTH_LONG).show();
                    return;
                }
                AndroidUtils.hideInput(SearchNewGoodsActivity.this, search_et_hint);

                if (order != 3) {
                    order = 3;
                    all_search_tv_quanbu.setTextColor(getResources().getColor(R.color.newzigrey));
                    all_search_tv_price.setTextColor(getResources().getColor(R.color.red));
                    all_search_tv_seller.setTextColor(getResources().getColor(R.color.newzigrey));
                    all_search_iv_price.setImageDrawable(getResources().getDrawable(R.mipmap.price_up));

                } else {
                    order = 2;
                    all_search_tv_quanbu.setTextColor(getResources().getColor(R.color.newzigrey));
                    all_search_tv_price.setTextColor(getResources().getColor(R.color.red));
                    all_search_tv_seller.setTextColor(getResources().getColor(R.color.newzigrey));
                    all_search_iv_price.setImageDrawable(getResources().getDrawable(R.mipmap.price_down));
                }
                if (NetJudgeUtils.getNetConnection(SearchNewGoodsActivity.this)) {
                    all_search_rl_listview.setVisibility(View.GONE);
                    all_search_nogoods.setVisibility(View.GONE);
                    all_search_ll_nowife.setVisibility(View.GONE);
                    all_search_rl_pb.setVisibility(View.VISIBLE);
                    keyword=search_et_hint.getText().toString().trim();
                    ThreadPollFactory.getNormalPool().execute(new goodsThread(keyword, 1));
                } else {
                    all_search_rl_listview.setVisibility(View.GONE);
                    all_search_nogoods.setVisibility(View.GONE);
                    all_search_ll_nowife.setVisibility(View.VISIBLE);
                    all_search_rl_pb.setVisibility(View.GONE);
                }
            }
        });
        all_search_tv_retro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(search_et_hint.getText().toString().trim())) {
                    Toast.makeText(SearchNewGoodsActivity.this, "请输入关键词!", Toast.LENGTH_LONG).show();
                    return;
                }
                AndroidUtils.hideInput(SearchNewGoodsActivity.this, search_et_hint);
                if (NetJudgeUtils.getNetConnection(SearchNewGoodsActivity.this)) {
                    all_search_rl_listview.setVisibility(View.GONE);
                    all_search_nogoods.setVisibility(View.GONE);
                    all_search_ll_nowife.setVisibility(View.GONE);
                    all_search_rl_pb.setVisibility(View.VISIBLE);
                    cp_image.innerStart();
                    keyword=search_et_hint.getText().toString().trim();
                    ThreadPollFactory.getNormalPool().execute(new goodsThread(keyword, 1));
                } else {
                    all_search_rl_listview.setVisibility(View.GONE);
                    all_search_nogoods.setVisibility(View.GONE);
                    all_search_ll_nowife.setVisibility(View.VISIBLE);
                    all_search_rl_pb.setVisibility(View.GONE);
                    cp_image.innerStop();

                }
            }
        });
        all_search_listview.setXListViewListener(new XListView.IXListViewListener() {
            public void onRefresh() {
                if (TextUtils.isEmpty(search_et_hint.getText().toString().trim())) {
                    Toast.makeText(SearchNewGoodsActivity.this, "请输入关键词!", Toast.LENGTH_LONG).show();
                    hd.sendEmptyMessage(9);
                } else {
                    if (NetJudgeUtils.getNetConnection(SearchNewGoodsActivity.this)) {
                       keyword= search_et_hint.getText().toString().trim();
                        ThreadPollFactory.getNormalPool().execute(new goodsThread(keyword, 1));
                    } else {
                        hd.sendEmptyMessage(7);
                    }
                }

            }

            public void onLoadMore() {
                if (all_search_listview.getProgressState()) {
                    if (TextUtils.isEmpty(search_et_hint.getText().toString().trim())) {
                        Toast.makeText(SearchNewGoodsActivity.this, "请输入关键词!", Toast.LENGTH_LONG).show();
                        hd.sendEmptyMessage(10);
                    } else {
                        if (NetJudgeUtils.getNetConnection(SearchNewGoodsActivity.this)) {
                            page++;
                            ThreadPollFactory.getNormalPool().execute(new goodsThread(keyword, page));
                        } else {
                            hd.sendEmptyMessage(8);
                        }
                    }


                }

                //

            }
        });
        all_search_listview.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // TODO Auto-generated method stub
                final Picasso picasso = Picasso.with(SearchNewGoodsActivity.this);
                if (scrollState == SCROLL_STATE_IDLE || scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                    picasso.resumeTag(SearchNewGoodsActivity.this);
                } else {
                    picasso.pauseTag(SearchNewGoodsActivity.this);
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

                if (NetJudgeUtils.getNetConnection(SearchNewGoodsActivity.this)) {
                    CustomProgressDialog.createDialog(SearchNewGoodsActivity.this, "");
                    GoodsInfo info = (GoodsInfo) goodList.get(position - 1);
                    turnGoodsId = info.getNumIid();
                    ThreadPollFactory.getNormalPool().execute(new TurnUrlThread(info.getNumIid() + "", SearchNewGoodsActivity.this, hd));
                    //AlibcTrade.show(JinxuanActivity.this, new AlibcPage(info.getGoodsUrl()),alibcShowParams, alibcTaokeParams, exParams , new DemoTradeCallback());

                } else {
                    Toast.makeText(SearchNewGoodsActivity.this, "您的网络不给力，请检查更新!", Toast.LENGTH_LONG).show();
                }


            }
        });
        search_et_hint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(BooleanThread.toService){
                    if (NetJudgeUtils.getNetConnection(SearchNewGoodsActivity.this)){
                        ThreadPollFactory.getNormalPool().execute(new StatisticsThread(SearchNewGoodsActivity.this,6));
                    }
                }
            }
        });
    }


    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("SearchNewGoodsActivity");
        MobclickAgent.onResume(this);
        AndroidUtils.hideInput(SearchNewGoodsActivity.this, search_et_hint);

    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("SearchNewGoodsActivity");
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

//    private class PopuAdapter extends BaseAdapter {
//        private String[] names;
//
//        public PopuAdapter(String[] names) {
//            this.names = names;
//        }
//
//        public int getCount() {
//            // TODO Auto-generated method stub
//            return names.length;
//        }
//
//        public Object getItem(int position) {
//            // TODO Auto-generated method stub
//            return names[position];
//        }
//
//        public long getItemId(int position) {
//            // TODO Auto-generated method stub
//            return position;
//        }
//
//        public View getView(final int position, View convertView,
//                            ViewGroup parent) {
//
//            View view = View.inflate(SearchNewActivity.this, R.layout.poptext,
//                    null);
//            final TextView tv_name = (TextView) view.findViewById(R.id.tv_type);
//
//
//            tv_name.setText(names[position]);
//
//            if (goodsType == 1) {
//                if (position == 0) {
//                    tv_name.setTextColor(getResources().getColor(R.color.red));
//                } else {
//                    tv_name.setTextColor(getResources().getColor(R.color.newzigrey));
//                }
//            }
//            if (goodsType == 2) {
//                if (position == 3) {
//                    tv_name.setTextColor(getResources().getColor(R.color.red));
//                } else {
//                    tv_name.setTextColor(getResources().getColor(R.color.newzigrey));
//                }
//            }
//            if (goodsType == 3) {
//                if (position == 2) {
//                    tv_name.setTextColor(getResources().getColor(R.color.red));
//                } else {
//                    tv_name.setTextColor(getResources().getColor(R.color.newzigrey));
//                }
//            }
//            if (goodsType == 4) {
//                if (position == 1) {
//                    tv_name.setTextColor(getResources().getColor(R.color.red));
//                } else {
//                    tv_name.setTextColor(getResources().getColor(R.color.newzigrey));
//                }
//            }
//
//            tv_name.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (TextUtils.isEmpty(search_et_hint.getText().toString().trim())) {
//                        pop.dismiss();
//                        Toast.makeText(SearchNewActivity.this, "请输入关键词!", Toast.LENGTH_LONG).show();
//                        return;
//                    }
//                    if (goodsType == 1) {
//                        if (!tv_name.getText().toString().trim().equals("全部")) {
//                            //   private String[] names = {"全部", "巨划算", "淘抢货","巨划算／淘抢货"};
//                            if (tv_name.getText().toString().trim().equals("巨划算")) {
//                                goodsType = 4;
//                            }
//                            if (tv_name.getText().toString().trim().equals("淘抢货")) {
//                                goodsType = 3;
//                            }
//                            if (tv_name.getText().toString().trim().equals("巨划算／淘抢货")) {
//                                goodsType = 2;
//                            }
//                            pop.dismiss();
//                            changeType();
//                        }
//
//                    }
//                    if (goodsType == 2) {
//                        if (!tv_name.getText().toString().trim().equals("巨划算／淘抢货")) {
//                            //   private String[] names = {"全部", "巨划算", "淘抢货","巨划算／淘抢货"};
//                            if (tv_name.getText().toString().trim().equals("巨划算")) {
//                                goodsType = 4;
//                            }
//                            if (tv_name.getText().toString().trim().equals("淘抢货")) {
//                                goodsType = 3;
//                            }
//                            if (tv_name.getText().toString().trim().equals("全部")) {
//                                goodsType = 1;
//                            }
//                            pop.dismiss();
//                            changeType();
//                        }
//
//                    }
//                    if (goodsType == 3) {
//                        if (!tv_name.getText().toString().trim().equals("淘抢货")) {
//                            //   private String[] names = {"全部", "巨划算", "淘抢货","巨划算／淘抢货"};
//                            if (tv_name.getText().toString().trim().equals("巨划算")) {
//                                goodsType = 4;
//                            }
//                            if (tv_name.getText().toString().trim().equals("全部")) {
//                                goodsType = 1;
//                            }
//                            if (tv_name.getText().toString().trim().equals("巨划算／淘抢货")) {
//                                goodsType = 2;
//                            }
//                            pop.dismiss();
//                            changeType();
//                        }
//
//                    }
//                    if (goodsType == 4) {
//                        if (!tv_name.getText().toString().trim().equals("巨划算")) {
//                            //   private String[] names = {"全部", "巨划算", "淘抢货","巨划算／淘抢货"};
//                            if (tv_name.getText().toString().trim().equals("全部")) {
//                                goodsType = 1;
//                            }
//                            if (tv_name.getText().toString().trim().equals("淘抢货")) {
//                                goodsType = 3;
//                            }
//                            if (tv_name.getText().toString().trim().equals("巨划算／淘抢货")) {
//                                goodsType = 2;
//                            }
//                            pop.dismiss();
//                            changeType();
//                        }
//
//                    }
//
//
//                }
//            });
//            return view;
//        }
//
//
//    }

    private void changeType() {

        order = 0;

        all_search_tv_quanbu.setTextColor(getResources().getColor(R.color.red));
        all_search_tv_price.setTextColor(getResources().getColor(R.color.newzigrey));
        all_search_tv_seller.setTextColor(getResources().getColor(R.color.newzigrey));
        all_search_iv_price.setImageDrawable(getResources().getDrawable(R.mipmap.price_nor));

        AndroidUtils.hideInput(SearchNewGoodsActivity.this, search_et_hint);

        //zhixing xincheng
        if (NetJudgeUtils.getNetConnection(SearchNewGoodsActivity.this)) {
            //执行下一个程序
            all_search_rl_listview.setVisibility(View.GONE);
            all_search_nogoods.setVisibility(View.GONE);
            all_search_ll_nowife.setVisibility(View.GONE);
            all_search_rl_pb.setVisibility(View.VISIBLE);
            cp_image.innerStart();
            keyword=search_et_hint.getText().toString().trim();
            ThreadPollFactory.getNormalPool().execute(new goodsThread(keyword, 1));
        } else {
            all_search_rl_listview.setVisibility(View.GONE);
            all_search_nogoods.setVisibility(View.GONE);
            all_search_ll_nowife.setVisibility(View.VISIBLE);
            all_search_rl_pb.setVisibility(View.GONE);
            cp_image.innerStop();
        }
    }

    /**
     * 的到圈值接口
     */
    private class goodsThread implements Runnable {

        int page;
        String keyword;


        public goodsThread(String keyword, int page) {

            this.keyword = keyword;
            this.page = page;
        }

        @Override
        public void run() {
            try {
//                String aa=URLEncoder.encode(keyword,"UTF-8");
//                System.out.println("keyword==="+aa);
//                System.out.println("aa="+ URLDecoder.decode(aa,"UTF-8"));


                String url = getResources().getString(R.string.appurl) + "/goods2/searchinall" + "?page=" + page + "&keyword=" + URLEncoder.encode(URLEncoder.encode(keyword,"UTF-8"),"UTF-8") + "&goodsType=" + 1 + "&order=" + order + "&stamp=" + UrlUtils.getTime() + "&encode=" + UrlUtils.getEncode();

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

    private class MyHandler extends Handler {
        private WeakReference<SearchNewGoodsActivity> mWeakReference = null;

        MyHandler(SearchNewGoodsActivity activity) {
            mWeakReference = new WeakReference<SearchNewGoodsActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SearchNewGoodsActivity mainActivity = mWeakReference.get();
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
                        page = 1;
                        String data = j.getString("data");
                        JSONObject jData = new JSONObject(data);
                        String goods = jData.getString("goods");
                        maxPage = jData.getInt("totalpage");
                        if (goods.equals("[]") || null == goods || goods.equals("null")) {
                            all_search_rl_listview.setVisibility(View.GONE);
                            all_search_nogoods.setVisibility(View.VISIBLE);
                            all_search_ll_nowife.setVisibility(View.GONE);
                            all_search_rl_pb.setVisibility(View.GONE);
                        } else {
                            goodList.clear();
                            goodList = ParseJsonCommon.parseJsonData(goods,
                                    GoodsInfo.class);

                            if (goodList.size() > 0) {
                                all_search_rl_listview.setVisibility(View.VISIBLE);
                                all_search_nogoods.setVisibility(View.GONE);
                                all_search_ll_nowife.setVisibility(View.GONE);
                                all_search_rl_pb.setVisibility(View.GONE);
                                if (maxPage > 1) {
                                    all_search_listview.setPullLoadEnable(true);
                                } else {
                                    all_search_listview.setPullLoadEnable(false);
                                }
                                adapter = new MyAdapter(mainActivity);

                                all_search_listview.setAdapter(adapter);
                                all_search_listview.stopRefresh();
                                all_search_listview.setRefreshTime(new Date().toLocaleString());
                            } else {
                                all_search_rl_listview.setVisibility(View.GONE);
                                all_search_nogoods.setVisibility(View.VISIBLE);
                                all_search_ll_nowife.setVisibility(View.GONE);
                                all_search_rl_pb.setVisibility(View.GONE);
                            }
                        }


                    } else {
                        all_search_rl_listview.setVisibility(View.GONE);
                        all_search_nogoods.setVisibility(View.GONE);
                        all_search_ll_nowife.setVisibility(View.VISIBLE);
                        all_search_rl_pb.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    all_search_rl_listview.setVisibility(View.GONE);
                    all_search_nogoods.setVisibility(View.GONE);
                    all_search_ll_nowife.setVisibility(View.VISIBLE);
                    all_search_rl_pb.setVisibility(View.GONE);
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
                        maxPage = jData.getInt("totalpage");
                        ziList.clear();
                        ziList = ParseJsonCommon.parseJsonData(goods,
                                GoodsInfo.class);
                        goodList.addAll(ziList);

                        if (page >= maxPage) {
                            all_search_listview.setPullLoadEnable(false);
                        } else {
                            all_search_listview.setPullLoadEnable(true);
                        }
                        adapter.notifyDataSetChanged();// 告诉listView数据发生改变，要求listView更新显示
                        all_search_listview.stopRefresh();
                        all_search_listview.stopLoadMore();
                    } else {
                        if (page > 1) {
                            page--;
                        }
                        all_search_listview.stopRefresh();
                        all_search_listview.stopLoadMore();
                        Toast.makeText(mainActivity, "数据返回错误!", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    all_search_listview.stopRefresh();
                    all_search_listview.stopLoadMore();
                }
            }
            if (msg.what == 5) {
                all_search_listview.stopRefresh();
                all_search_listview.stopLoadMore();
                all_search_rl_listview.setVisibility(View.GONE);
                all_search_nogoods.setVisibility(View.GONE);
                all_search_ll_nowife.setVisibility(View.VISIBLE);
                all_search_rl_pb.setVisibility(View.GONE);
            }
            if (msg.what == 6) {
                Toast.makeText(mainActivity, "数据返回错误!", Toast.LENGTH_SHORT).show();
                if (page > 1) {
                    page--;
                }
                all_search_listview.stopLoadMore();
            }
            if (msg.what == 7) {
                MyToast.show(mainActivity, 0, AndroidUtils.getWidth(mainActivity),
                        AndroidUtils.dip2px(mainActivity, 45), "您的网络不给力，请检查更新!");
                all_search_listview.stopRefresh();
            }
            if (msg.what == 8) {

                all_search_listview.stopLoadMore();

            }
            if (msg.what == 9) {
                MyToast.show(mainActivity, 0, AndroidUtils.getWidth(mainActivity),
                        AndroidUtils.dip2px(mainActivity, 45), "关键词不能为空！");
                all_search_listview.stopRefresh();
            }
            if (msg.what == 10) {
                Toast.makeText(mainActivity, "关键词不能为空!", Toast.LENGTH_LONG).show();
                all_search_listview.stopRefresh();

            }
            if (msg.what == 11) {
                try {
                    JSONObject j = new JSONObject((String) msg.obj);
                    String code = j.getString("code");
                    if (code.equals("0")) {
                        String data = j.getString("data");
                        //  data="http:www.host.com";
                        //
//                                               if(TextUtils.isEmpty(data)){
//                                                   cmb.setText("复制的地址为空");
//                                               }else{
//                                                   cmb.setText("栈内优惠券地址为："+data);
//                                               }
//                                               Toast.makeText(SearchYouhuiNewActivity.this, "优惠券地址已粘贴!", Toast.LENGTH_LONG).show();


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
                view = inflater.inflate(R.layout.goodnew_item, null);
                holder = new GoodHodler();
                holder.good_img = (ImageView) view.findViewById(R.id.good_img);
                holder.goods_desc = (TextView) view.findViewById(R.id.goods_desc);
                holder.goods_discount_price = (TextView) view.findViewById(R.id.goods_discount_price);
               // holder.goods_ori_price = (TextView) view.findViewById(R.id.goods_ori_price);
                holder.tv_chengji = (TextView) view.findViewById(R.id.tv_chengji);
                holder.home_temai_shopname = (TextView) view.findViewById(R.id.home_temai_shopname);
                holder.home_temai_discount = (TextView) view.findViewById(R.id.home_temai_discount);
//                holder.goods_ori_price.getPaint().setFlags(
//                        Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
                view.setTag(holder);
            } else {
                view = convertView;
                holder = (GoodHodler) convertView.getTag();
            }

            final GoodsInfo goodsInfo;
            goodsInfo = (GoodsInfo) goodList.get(position);
            holder.good_img.setLayoutParams(params);
            PicassoUtils.loadImageWithHolderAndError(SearchNewGoodsActivity.this, goodsInfo.getGoodsImage(), R.mipmap.picture, R.mipmap.picture, holder.good_img);
            holder.goods_desc.setText(goodsInfo.getTitle());
            holder.goods_discount_price.setText(goodsInfo.getPrice());
           // holder.goods_ori_price.setText("￥" + goodsInfo.getOldPrice());
            holder.tv_chengji.setText("已售" + goodsInfo.getSellCount() + "件");
            holder.home_temai_discount.setText(goodsInfo.getSavePrice());
            holder.home_temai_shopname.setText(goodsInfo.getGoodsShop());
            return view;
        }

    }

    class GoodHodler {
        ImageView good_img;
        TextView goods_desc;
        TextView goods_discount_price;
    //    TextView goods_ori_price;
        TextView tv_chengji;
        TextView home_temai_shopname;
        TextView home_temai_discount;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CustomProgressDialog.stopProgressDialog();
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
}
