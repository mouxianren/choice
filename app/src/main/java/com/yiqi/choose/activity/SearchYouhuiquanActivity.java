package com.yiqi.choose.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
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
import com.yiqi.choose.base.BaseMap;
import com.yiqi.choose.factory.ThreadPollFactory;
import com.yiqi.choose.model.GoodsInfo;
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

import org.json.JSONException;
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

public class SearchYouhuiquanActivity extends BaseActivity {
    EditText search_et_hint;
    LinearLayout ll_back;
    //LinearLayout youhui_serch_ll_type;
    LinearLayout youhui_tv_search;
    TextView youhui_search_bt_retryweb;
    private LinearLayout youhui_search_typeci;

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
        setContentView(R.layout.youhui_search_another);
        alibcTaokeParams = new AlibcTaokeParams(SplashActicity1.mPId, "", "");
        alibcShowParams = new AlibcShowParams(OpenType.Native, false);
        alibcShowParams.setClientType("taobao_scheme");
        exParams = new HashMap<>();
        exParams.put("isv_code", "appisvcode");
        exParams.put("alibaba", "阿里巴巴");//自定义参数部分，可任意增删改

        Intent intent = getIntent();
        order = 0;
        searchType = intent.getStringExtra("searchType");
        home_arraw=(ImageView)findViewById(R.id.home_arraw);
        home_search_rl=(RelativeLayout)findViewById(R.id.home_search_rl);
     //   cmb = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        search_tv_empty = (TextView) findViewById(R.id.search_tv_empty);
        search_et_hint = (EditText) findViewById(R.id.search_et_hint);
        ll_back = (LinearLayout) this.findViewById(R.id.ll_back);
       // youhui_serch_ll_type = (LinearLayout) this.findViewById(R.id.youhui_serch_ll_type);

        youhui_search_typeci=(LinearLayout)this.findViewById(R.id.youhui_search_typeci);
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
        goodList = new ArrayList<Object>();
        ziList = new ArrayList<Object>();
        if (searchType.equals("1")) {//goods
            search_et_hint.setHint("搜索商品");
            search_tv_empty.setText("未找到相关商品");
        } else if (searchType.equals("2")) {//youhui
            search_et_hint.setHint("搜索优惠券");
            search_tv_empty.setText("未找到相关优惠券");
        }


        ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchYouhuiquanActivity.this.finish();
                overridePendingTransition(R.anim.in_from_left, R.anim.out_of_right);
            }
        });
        //addTags();


        youhui_search_bt_retryweb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetJudgeUtils.getNetConnection(SearchYouhuiquanActivity.this)) {
                    youhui_search_typeci.setVisibility(View.GONE);
                    home_search_rl.setVisibility(View.GONE);
                    search_ll_nogoods.setVisibility(View.GONE);
                    youhui_search_ll_nowife.setVisibility(View.GONE);
                    youhui_search_rl_pb.setVisibility(View.VISIBLE);
                    ThreadPollFactory.getNormalPool().execute(new goodsThread(SplashActicity1.mQuanId, "", 1,keyword));

                }
            }
        });

        youhui_tv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(search_et_hint.getText().toString().trim())) {
                    Toast.makeText(SearchYouhuiquanActivity.this, "请输入关键词!", Toast.LENGTH_LONG).show();
                    return;
                }

                //执行线程
                if (NetJudgeUtils.getNetConnection(SearchYouhuiquanActivity.this)) {

                    AndroidUtils.hideInput(SearchYouhuiquanActivity.this, search_et_hint);
                    //bianhuan
                    youhui_search_typeci.setVisibility(View.GONE);
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
                    ThreadPollFactory.getNormalPool().execute(new goodsThread(SplashActicity1.mQuanId, "",1,keyword));
                } else {
                    Toast.makeText(SearchYouhuiquanActivity.this, R.string.net, Toast.LENGTH_LONG).show();
                }
            }
        });
        listView.setXListViewListener(new XListView.IXListViewListener() {
            public void onRefresh() {
                if (NetJudgeUtils.getNetConnection(SearchYouhuiquanActivity.this)) {
                    ThreadPollFactory.getNormalPool().execute(new goodsThread(SplashActicity1.mQuanId, "", 1,keyword));
                } else {
                    hd.sendEmptyMessage(7);
                }
            }

            public void onLoadMore() {
                if (listView.getProgressState()) {
                    if (NetJudgeUtils.getNetConnection(SearchYouhuiquanActivity.this)) {
                        page++;
                        ThreadPollFactory.getNormalPool().execute(new goodsThread(SplashActicity1.mQuanId, (String) SharedPfUtils.getData(SearchYouhuiquanActivity.this, "youhuisearchtime", ""),  page, keyword));
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
                if(NetJudgeUtils.getNetConnection(SearchYouhuiquanActivity.this)){
                    CustomProgressDialog.createDialog(SearchYouhuiquanActivity.this,"");
                    GoodsInfo info=(GoodsInfo)goodList.get(position-1);
                    ThreadPollFactory.getNormalPool().execute(new TurnUrlThread(info.getNumIid()+"",SearchYouhuiquanActivity.this));
                }else{
                    Toast.makeText(SearchYouhuiquanActivity.this,"您的网络不给力，请检查更新!",Toast.LENGTH_LONG).show();
                }

            }
        });
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // TODO Auto-generated method stub
                final Picasso picasso = Picasso.with(SearchYouhuiquanActivity.this);
                if (scrollState == SCROLL_STATE_IDLE || scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                    picasso.resumeTag(SearchYouhuiquanActivity.this);
                } else {
                    picasso.pauseTag(SearchYouhuiquanActivity.this);
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
        String keyword;

        public goodsThread(String code, String timeStemp, int page, String keyword) {
            this.code = code;
            this.timeStemp = timeStemp;
            this.keyword = keyword;
            this.page = page;
        }

        @Override
        public void run() {
            try {
                String url = getResources().getString(R.string.appurl) + "/goods/discount" + "?code=" + code + "&timeStemp=" + timeStemp + "&page=" + page+ "&keyword=" + URLEncoder.encode(keyword, "UTF-8")+"&stamp="+ UrlUtils.getTime()+"&encode="+UrlUtils.getEncode();

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
                        SharedPfUtils.saveStringData(SearchYouhuiquanActivity.this, "youhuisearchtime", time);
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
                                adapter = new MyAdapter(SearchYouhuiquanActivity.this);

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
                        Toast.makeText(SearchYouhuiquanActivity.this, "数据返回错误!", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(SearchYouhuiquanActivity.this, "数据返回错误!", Toast.LENGTH_SHORT).show();
                if (page > 1) {
                    page--;
                }
                listView.stopLoadMore();
            }
            if (msg.what == 7) {
                MyToast.show(SearchYouhuiquanActivity.this, 0, AndroidUtils.getWidth(SearchYouhuiquanActivity.this),
                        AndroidUtils.dip2px(SearchYouhuiquanActivity.this, 45), "您的网络不给力，请检查更新!");
                listView.stopRefresh();
            }
            if (msg.what == 8) {

                listView.stopLoadMore();

            }
            if(msg.what==11){
                try {
                    JSONObject j = new JSONObject((String) msg.obj);
                    String code = j.getString("code");
                    if (code.equals("0")) {
                        String data = j.getString("data");
//                        System.out.println("data==="+data);
//                                                  if(TextUtils.isEmpty(data)){
//                                                       cmb.setText("复制的地址为空");
//                                                  }else{
//                                                       cmb.setText("商品地址为："+data);
//                                                   }
//                        Toast.makeText(SearchYouhuiquanActivity.this, "优惠券地址已粘贴!", Toast.LENGTH_LONG).show();

                        AlibcTrade.show(SearchYouhuiquanActivity.this, new AlibcPage(data),alibcShowParams, null, exParams , new DemoTradeCallback());

                    }else{
                        Toast.makeText(SearchYouhuiquanActivity.this,"服务器错误!",Toast.LENGTH_LONG).show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(SearchYouhuiquanActivity.this,"服务器错误!",Toast.LENGTH_LONG).show();
                } finally {
                    CustomProgressDialog.stopProgressDialog();
                }
            }
            if(msg.what==12){
                CustomProgressDialog.stopProgressDialog();
                Toast.makeText(SearchYouhuiquanActivity.this,"服务器错误!",Toast.LENGTH_LONG).show();
            }
        }
    };

    public class TurnUrlThread implements Runnable {
        String goodsId;
        Context context;

        public TurnUrlThread(String goodsId,Context context){

            this.goodsId=goodsId;
            this.context=context;
        }


        @Override
        public void run() {
            try {
                Map<String, String> params = new HashMap<>();
                params.put("code", SplashActicity1.mQuanId);
                params.put("goodsId",goodsId);
                params = BaseMap.getMapAll(params);
                String url = context.getResources().getString(R.string.appurl)+"/goods/turnUrl"+"?stamp=" + UrlUtils.getTime() + "&encode=" + UrlUtils.getEncode();
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
                view = inflater.inflate(R.layout.layout_youhui_item, null);
                holder = new GoodHodler();
                holder.good_img = (ImageView) view.findViewById(R.id.good_img);
                holder.goods_desc = (TextView) view.findViewById(R.id.goods_desc);
                holder.goods_discount_price = (TextView) view.findViewById(R.id.goods_discount_price);
                holder.goods_ori_price = (TextView) view.findViewById(R.id.goods_ori_price);
                holder.tv_chengji = (TextView) view.findViewById(R.id.tv_chengji);
                holder.home_temai_shopname=(TextView)view.findViewById(R.id.home_temai_shopname);
                holder.home_temai_discount=(TextView)view.findViewById(R.id.home_temai_discount);
                holder.goods_ori_price.getPaint().setFlags(
                        Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
                view.setTag(holder);
            } else {
                view = convertView;
                holder = (GoodHodler) convertView.getTag();
            }
            final GoodsInfo goodsInfo;
            goodsInfo = (GoodsInfo) goodList.get(position);
            PicassoUtils.loadImageWithHolderAndError(SearchYouhuiquanActivity.this, goodsInfo.getGoodsImage(), R.mipmap.picture, R.mipmap.picture, holder.good_img);
            holder.goods_desc.setText(goodsInfo.getTitle());
            holder.goods_discount_price.setText(goodsInfo.getPrice());
            holder.goods_ori_price.setText("￥" + goodsInfo.getOldPrice());
            holder.tv_chengji.setText("已售" + goodsInfo.getSellCount() + "件");
            holder.home_temai_discount.setText("省"+goodsInfo.getSavePrice()+"元");
            holder.home_temai_shopname.setText(goodsInfo.getGoodsShop());
            return view;
        }
    }

    class GoodHodler {
        ImageView good_img;
        TextView goods_desc;
        TextView goods_discount_price;
        TextView goods_ori_price;
        TextView tv_chengji;
        TextView home_temai_shopname;
        TextView home_temai_discount;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CustomProgressDialog.stopProgressDialog();
    }
}
