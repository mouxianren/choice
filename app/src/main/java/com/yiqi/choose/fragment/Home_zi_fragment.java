package com.yiqi.choose.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.yiqi.choose.activity.JinxuanActivity;
import com.yiqi.choose.activity.SplashActicity1;
import com.yiqi.choose.activity.TemaiActivity;
import com.yiqi.choose.alibabaCallback.DemoTradeCallback;
import com.yiqi.choose.base.BaseFragment1_coupons;
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
import com.yiqi.choose.utils.UrlUtils;
import com.yiqi.choose.view.XListView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by moumou on 17/8/15.
 */

public class Home_zi_fragment extends BaseFragment1_coupons {
    private XListView listView;
    private LinearLayout home_zi_nogoods;
    private LinearLayout home_zi_nowifi;
    private TextView home_zi_retro;
    private RelativeLayout home_zi_pb;

    private String typeId = "";
    List<Object> bannerList;
    /**
     * 标志位，标志已经初始化完成
     */
    private boolean isPrepared;
    /**
     * 是否已被加载过一次，第二次就不再去请求数据了
     */
    private boolean mHasLoadedOnce;

    int page = 1;
    int maxPage = 0;
    String time = "";
    private List<Object> goodList;
    private List<Object> ziList;

    MyAdapter adapter;


    ImageView home_temai;
    ImageView home_baoyou;
    View bannerView;

    int number = 0;
    ImageView home_arraw;
   boolean coupon_createbanenr = false;
    boolean advertisetun = false;
    private RelativeLayout home_rl_arrow;
    private int screenWidth;
    private LinearLayout home_type;
    private LinearLayout home_ll_text;

    private AlibcShowParams alibcShowParams;//页面打开方式，默认，H5，Native
    private Map<String, String> exParams;//yhhpass参数
    private AlibcTaokeParams alibcTaokeParams = null;//淘客参数，包括pid，unionid，subPid

    private String clickUrl="";
   // ClipboardManager cmb;

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_zi, null);
        bannerView = View.inflate(getActivity(), R.layout.layout_banner_coupons, null);
        home_temai = (ImageView) bannerView.findViewById(R.id.home_temai);
        home_baoyou = (ImageView) bannerView.findViewById(R.id.home_baoyou);
        home_type=(LinearLayout)bannerView.findViewById(R.id.home_type);
        alibcTaokeParams = new AlibcTaokeParams(SplashActicity1.mPId, "", "");
        alibcShowParams = new AlibcShowParams(OpenType.Native, false);
        alibcShowParams.setClientType("taobao_scheme");
        exParams = new HashMap<>();
        exParams.put("isv_code", "appisvcode");
        exParams.put("alibaba", "阿里巴巴");//自定义参数部分，可任意增删改
        home_ll_text=(LinearLayout)bannerView.findViewById(R.id.home_ll_text);
      // cmb = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        home_arraw=(ImageView)view.findViewById(R.id.home_arraw);
        home_rl_arrow=(RelativeLayout)view.findViewById(R.id.home_rl_arrow);
        screenWidth=AndroidUtils.getWidth(getActivity());
        mHasLoadedOnce=false;
        number=0;
        clickUrl="";
        coupon_createbanenr = false;
        advertisetun=false;
        listView = (XListView) view.findViewById(R.id.home_zi_listView);
        home_zi_nogoods = (LinearLayout) view.findViewById(R.id.home_zi_nogoods);
        home_zi_nowifi = (LinearLayout) view.findViewById(R.id.home_zi_nowefi);
        home_zi_retro = (TextView) view.findViewById(R.id.home_fragment_tv_retro);
        home_zi_pb = (RelativeLayout) view.findViewById(R.id.home_zi_pb);
        bannerList = new ArrayList<Object>();

        goodList = new ArrayList<Object>();
        ziList = new ArrayList<Object>();
        home_zi_pb.setVisibility(View.GONE);
        home_zi_nowifi.setVisibility(View.GONE);
        home_zi_nogoods.setVisibility(View.GONE);
        listView.setVisibility(View.GONE);
        home_rl_arrow.setVisibility(View.GONE);
        isPrepared = true;
        return view;
    }

    @Override
    public void initData() {
        home_zi_retro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetJudgeUtils.getNetConnection(getActivity())) {
                    home_zi_pb.setVisibility(View.VISIBLE);
                    home_zi_nowifi.setVisibility(View.GONE);
                    home_zi_nogoods.setVisibility(View.GONE);
                    listView.setVisibility(View.GONE);
                    home_rl_arrow.setVisibility(View.GONE);
                    if (typeId.equals("1")) {
                        ThreadPollFactory.getNormalPool().execute(new bannerThread());
                    }
                    ThreadPollFactory.getNormalPool().execute(new goodsThread(SplashActicity1.mQuanId, "", typeId, 1));
                } else {
                    home_zi_pb.setVisibility(View.GONE);
                    home_zi_nowifi.setVisibility(View.VISIBLE);
                    home_zi_nogoods.setVisibility(View.GONE);
                    listView.setVisibility(View.GONE);
                    home_rl_arrow.setVisibility(View.GONE);
                }
            }
        });
        home_temai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),
                        TemaiActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(
                        R.anim.to_right, R.anim.to_left);
            }
        });
        home_baoyou.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),
                        JinxuanActivity.class);
                startActivity(intent);
               getActivity().overridePendingTransition(
                        R.anim.to_right, R.anim.to_left);
            }
        });
        home_ll_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        listView.setXListViewListener(new XListView.IXListViewListener() {

            public void onRefresh() {
                if (getNetConnection()) {
                    //savePreferences("couponssuccess"+TypeId, "chenggong");
                    if (typeId.equals("1")) {
                        ThreadPollFactory.getNormalPool().execute(new bannerThread());
                    }
                    ThreadPollFactory.getNormalPool().execute(new goodsThread(SplashActicity1.mQuanId, "", typeId, 1));

                } else {
                    hd.sendEmptyMessage(7);
                }
            }

            public void onLoadMore() {

                if (listView.getProgressState()) {
                    if (getNetConnection()) {
                        page++;
                        ThreadPollFactory.getNormalPool().execute(new goodsThread(SplashActicity1.mQuanId, (String) SharedPfUtils.getData(getActivity(), typeId + "time", ""), typeId, page));
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
                if(NetJudgeUtils.getNetConnection(getActivity())){
                    CustomProgressDialog.createDialog(getActivity(),"");
                    if(typeId.equals("1")){
                        GoodsInfo info=(GoodsInfo)goodList.get(position-2);
                        clickUrl=info.getGoodsUrl();
                       //AlibcTrade.show(getActivity(), new AlibcPage(info.getGoodsUrl()),alibcShowParams, alibcTaokeParams, exParams , new DemoTradeCallback());
                        ThreadPollFactory.getNormalPool().execute(new ClickThread(info.getNumIid()+"",getActivity(),hd));


                    }else{
                        GoodsInfo info=(GoodsInfo)goodList.get(position-1);
                        clickUrl=info.getGoodsUrl();
                       // AlibcTrade.show(getActivity(), new AlibcPage(info.getGoodsUrl()),alibcShowParams, alibcTaokeParams, exParams , new DemoTradeCallback());
                        ThreadPollFactory.getNormalPool().execute(new ClickThread(info.getNumIid()+"",getActivity(),hd));
                    }
                }else{
                    Toast.makeText(getActivity(),R.string.net,Toast.LENGTH_LONG).show();
                }


            }
        });
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // TODO Auto-generated method stub
                final Picasso picasso = Picasso.with(getActivity());
                if (scrollState == SCROLL_STATE_IDLE || scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                    picasso.resumeTag(getActivity());
                } else {
                    picasso.pauseTag(getActivity());
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

    @Override
    public void lazyLoad(String id) {
        listView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        if (!isPrepared || mHasLoadedOnce||home_zi_pb.getVisibility()==View.VISIBLE) {
            return;
        }
        typeId = id;
        if (typeId.equals("1") && number < 1) {
                    DisplayMetrics dm = new DisplayMetrics();
                    getActivity().getWindowManager().getDefaultDisplay()
                            .getMetrics(dm);
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)home_type
                            .getLayoutParams();
                    // 根据图片高和宽的比例计算高度
                    // 测试图宽为694 高为323
                    params.width = screenWidth;
                    params.height = screenWidth*232/1080;
                    home_type.setLayoutParams(params);
            adapter = new MyAdapter(getActivity());
            listView.addHeaderView(bannerView);
            number = 1;
        }
        if (NetJudgeUtils.getNetConnection(getActivity())) {
       
            home_zi_pb.setVisibility(View.VISIBLE);
            home_zi_nowifi.setVisibility(View.GONE);
            home_zi_nogoods.setVisibility(View.GONE);
            listView.setVisibility(View.GONE);
            home_rl_arrow.setVisibility(View.GONE);
            ThreadPollFactory.getNormalPool().execute(new goodsThread(SplashActicity1.mQuanId, "", typeId, 1));
        } else {

            home_zi_pb.setVisibility(View.GONE);
            home_zi_nowifi.setVisibility(View.VISIBLE);
            home_zi_nogoods.setVisibility(View.GONE);
            listView.setVisibility(View.GONE);
            home_rl_arrow.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        MobclickAgent.onPageStart("Home_zi_fragment");

    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("Home_zi_fragment");

    }

    /**
     * 的到圈值接口
     */
    private class bannerThread implements Runnable {

        @Override
        public void run() {
            try {
                String url = getResources().getString(R.string.appurl) + "/home/advertise"+"?stamp=" + UrlUtils.getTime() + "&encode=" + UrlUtils.getEncode();
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

    /**
     * 的到圈值接口
     */
    private class goodsThread implements Runnable {
        String code;
        String timeStemp;
        String categoryId;
        int page;

        public goodsThread(String code, String timeStemp, String categoryId, int page) {
            this.code = code;
            this.timeStemp = timeStemp;
            this.categoryId = categoryId;
            this.page = page;
        }

        @Override
        public void run() {
            try {
                String url = getResources().getString(R.string.appurl) + "/goods/index" + "?code=" + code + "&timeStemp=" + timeStemp + "&categoryId=" + categoryId + "&page=" + page+"&stamp="+ UrlUtils.getTime()+"&encode="+UrlUtils.getEncode();
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
                        SharedPfUtils.saveStringData(getActivity(), typeId + "time", time);


                       // goods="[]";


                        if (goods.equals("[]") || null == goods || goods.equals("null")) {
                            if (!typeId.equals("1")) {
                                home_zi_pb.setVisibility(View.GONE);
                                home_zi_nowifi.setVisibility(View.GONE);
                                home_zi_nogoods.setVisibility(View.VISIBLE);
                                listView.setVisibility(View.GONE);
                                home_rl_arrow.setVisibility(View.GONE);
                            } else {
                                home_zi_pb.setVisibility(View.GONE);
                                home_zi_nowifi.setVisibility(View.GONE);
                                home_zi_nogoods.setVisibility(View.GONE);
                                listView.setVisibility(View.VISIBLE);
                                home_rl_arrow.setVisibility(View.VISIBLE);
                                adapter = new MyAdapter(getActivity());
                                listView.setAdapter(adapter);
                                listView.setPullLoadEnable(false);
                            }

                        } else {
                            goodList.clear();
                            goodList = ParseJsonCommon.parseJsonData(goods,
                                    GoodsInfo.class);
                            if (!typeId.equals("1")) {
                                if (goodList.size() > 0) {
                                    home_zi_pb.setVisibility(View.GONE);
                                    home_zi_nowifi.setVisibility(View.GONE);
                                    home_zi_nogoods.setVisibility(View.GONE);
                                    listView.setVisibility(View.VISIBLE);
                                    home_rl_arrow.setVisibility(View.VISIBLE);
                                    if (maxPage > 1) {
                                        listView.setPullLoadEnable(true);
                                    } else {
                                        listView.setPullLoadEnable(false);
                                    }
                                    adapter = new MyAdapter(getActivity());

                                    listView.setAdapter(adapter);

                                    listView.stopRefresh();
                                    listView.setRefreshTime(new Date().toLocaleString());
                                } else {
                                    home_zi_pb.setVisibility(View.GONE);
                                    home_zi_nowifi.setVisibility(View.GONE);
                                    home_zi_nogoods.setVisibility(View.VISIBLE);
                                    listView.setVisibility(View.GONE);
                                    home_rl_arrow.setVisibility(View.GONE);
                                }
                            } else {
                                home_zi_pb.setVisibility(View.GONE);
                                home_zi_nowifi.setVisibility(View.GONE);
                                home_zi_nogoods.setVisibility(View.GONE);
                                listView.setVisibility(View.VISIBLE);
                                home_rl_arrow.setVisibility(View.VISIBLE);
                                if (maxPage > 1) {
                                    listView.setPullLoadEnable(true);
                                } else {
                                    listView.setPullLoadEnable(false);
                                }
                                adapter = new MyAdapter(getActivity());
                                listView.setAdapter(adapter);


                                listView.stopRefresh();
                                listView.setRefreshTime(new Date().toLocaleString());

                            }

                        }
                        mHasLoadedOnce = true;

                    } else {

                        if (!typeId.equals("1")) {
                            home_zi_pb.setVisibility(View.GONE);
                            home_zi_nowifi.setVisibility(View.VISIBLE);
                            home_zi_nogoods.setVisibility(View.GONE);
                            listView.setVisibility(View.GONE);
                            home_rl_arrow.setVisibility(View.GONE);
                        } else {
                            home_zi_pb.setVisibility(View.GONE);
                            home_zi_nowifi.setVisibility(View.GONE);
                            home_zi_nogoods.setVisibility(View.GONE);
                            listView.setVisibility(View.VISIBLE);
                            home_rl_arrow.setVisibility(View.VISIBLE);
                            adapter = new MyAdapter(getActivity());
                            listView.setAdapter(adapter);
                            listView.setPullLoadEnable(false);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                    if (!typeId.equals("1")) {
                        home_zi_pb.setVisibility(View.GONE);
                        home_zi_nowifi.setVisibility(View.VISIBLE);
                        home_zi_nogoods.setVisibility(View.GONE);
                        listView.setVisibility(View.GONE);
                        home_rl_arrow.setVisibility(View.GONE);
                    } else {
                        home_zi_pb.setVisibility(View.GONE);
                        home_zi_nowifi.setVisibility(View.GONE);
                        home_zi_nogoods.setVisibility(View.GONE);
                        listView.setVisibility(View.VISIBLE);
                        home_rl_arrow.setVisibility(View.VISIBLE);
                        adapter = new MyAdapter(getActivity());
                        listView.setAdapter(adapter);
                        listView.setPullLoadEnable(false);
                    }
                } finally {
                    listView.stopRefresh();
                    listView.stopLoadMore();
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
                        Toast.makeText(getActivity(), "数据返回错误!", Toast.LENGTH_SHORT).show();
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
                if (!typeId.equals("1")) {
                    home_zi_pb.setVisibility(View.GONE);
                    home_zi_nowifi.setVisibility(View.VISIBLE);
                    home_zi_nogoods.setVisibility(View.GONE);
                    listView.setVisibility(View.GONE);
                    home_rl_arrow.setVisibility(View.GONE);
                } else {
                    home_zi_pb.setVisibility(View.GONE);
                    home_zi_nowifi.setVisibility(View.GONE);
                    home_zi_nogoods.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);
                    home_rl_arrow.setVisibility(View.VISIBLE);
                }
            }
            if (msg.what == 6) {
                Toast.makeText(getActivity(), "数据返回错误!", Toast.LENGTH_SHORT).show();
                if (page > 1) {
                    page--;
                }
                listView.stopLoadMore();
            }
            if (msg.what == 7) {
                MyToast.show(getActivity(), 0, getFenbianlv1(),
                        AndroidUtils.dip2px(getActivity(), 45), "您的网络不给力，请检查更新!");
                listView.stopRefresh();
            }
            if (msg.what == 8) {
                listView.stopLoadMore();

            }
            if(msg.what==100){
                if(getUserVisibleHint()){
                    try {
                        JSONObject j = new JSONObject((String) msg.obj);
                        String code = j.getString("code");
                        if (code.equals("0")) {
                            String data = j.getString("data");


//                          if(TextUtils.isEmpty(data)){
//                               cmb.setText("复制的地址为空");
//                          }else{
//                               cmb.setText("商品地址为："+data);
//                           }
//                           Toast.makeText(getActivity(), "商品地址已粘贴!", Toast.LENGTH_LONG).show();

                            if(TextUtils.isEmpty(data)){
                                if(NetJudgeUtils.getNetConnection(getActivity())){
                                    //Toast.makeText(getActivity(),"本地转链地址!",Toast.LENGTH_LONG).show();
                                    AlibcTrade.show(getActivity(), new AlibcPage(clickUrl),alibcShowParams, alibcTaokeParams, exParams , new DemoTradeCallback());
                                }else{
                                    Toast.makeText(getActivity(),"您的网络不给力，请检查更新!",Toast.LENGTH_LONG).show();
                                }
                            }else{
                                if(NetJudgeUtils.getNetConnection(getActivity())){
                                   // Toast.makeText(getActivity(),"后台转链地址!",Toast.LENGTH_LONG).show();
                                    AlibcTrade.show(getActivity(), new AlibcPage(data),alibcShowParams, null, exParams , new DemoTradeCallback());
                                }else{
                                    Toast.makeText(getActivity(),"您的网络不给力，请检查更新!",Toast.LENGTH_LONG).show();
                                }
                            }


                        }else{
                            if(NetJudgeUtils.getNetConnection(getActivity())){
                              //  Toast.makeText(getActivity(),"本地转链地址!",Toast.LENGTH_LONG).show();
                                AlibcTrade.show(getActivity(), new AlibcPage(clickUrl),alibcShowParams, alibcTaokeParams, exParams , new DemoTradeCallback());
                            }else{
                                Toast.makeText(getActivity(),"您的网络不给力，请检查更新!",Toast.LENGTH_LONG).show();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        if(NetJudgeUtils.getNetConnection(getActivity())){
                          //  Toast.makeText(getActivity(),"本地转链地址!",Toast.LENGTH_LONG).show();
                            AlibcTrade.show(getActivity(), new AlibcPage(clickUrl),alibcShowParams, alibcTaokeParams, exParams , new DemoTradeCallback());
                        }else{
                            Toast.makeText(getActivity(),"您的网络不给力，请检查更新!",Toast.LENGTH_LONG).show();
                        }

                    } finally {
                        CustomProgressDialog.stopProgressDialog();
                    }
                }

            }
            if(msg.what==101){
                if(getUserVisibleHint()){
                    if(NetJudgeUtils.getNetConnection(getActivity())){
                       // Toast.makeText(getActivity(),"本地转链地址!",Toast.LENGTH_LONG).show();
                        AlibcTrade.show(getActivity(), new AlibcPage(clickUrl),alibcShowParams, alibcTaokeParams, exParams , new DemoTradeCallback());
                    }else{
                        Toast.makeText(getActivity(),"您的网络不给力，请检查更新!",Toast.LENGTH_LONG).show();
                    }
                    CustomProgressDialog.stopProgressDialog();
                }


            }

        }
    };



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

            PicassoUtils.loadImageWithHolderAndError(getActivity(), goodsInfo.getGoodsImage(), R.mipmap.picture, R.mipmap.picture, holder.good_img);
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
    public void onDestroy() {
        super.onDestroy();
        CustomProgressDialog.stopProgressDialog();
    }
}
