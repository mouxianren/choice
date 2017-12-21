package com.yiqi.choose.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
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
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;
import com.yiqi.choose.R;
import com.yiqi.choose.activity.MainAcitivity1;
import com.yiqi.choose.activity.SplashActicity1;
import com.yiqi.choose.activity.WebActivity;
import com.yiqi.choose.activity.fiveitem.JiukuaijiuActivity;
import com.yiqi.choose.activity.fiveitem.JuhuasuanActivity;
import com.yiqi.choose.activity.zitypelist.ZitypeActivity;
import com.yiqi.choose.alibabaCallback.DemoTradeCallback;
import com.yiqi.choose.base.BaseFragment1_coupons;
import com.yiqi.choose.base.BaseMap;
import com.yiqi.choose.factory.ThreadPollFactory;
import com.yiqi.choose.model.BannerInfo;
import com.yiqi.choose.model.GoodsInfo;
import com.yiqi.choose.utils.AndroidUtils;
import com.yiqi.choose.utils.CustomProgressDialog;
import com.yiqi.choose.utils.HttpConBase;
import com.yiqi.choose.utils.MyToast;
import com.yiqi.choose.utils.NetJudgeUtils;
import com.yiqi.choose.utils.ParseJsonCommon;
import com.yiqi.choose.utils.PicassoUtils;
import com.yiqi.choose.utils.SharedPfUtils;
import com.yiqi.choose.utils.StringUtils;
import com.yiqi.choose.utils.UrlUtils;
import com.yiqi.choose.view.XListView;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by moumou on 17/8/15.
 */

public class Youhui_zi_fragment extends BaseFragment1_coupons {
    private XListView listView;
    private LinearLayout home_zi_nogoods;
    private LinearLayout home_zi_nowifi;
    private TextView home_zi_retro;
    private RelativeLayout home_zi_pb;


    private String typeId = "";
    List<Object> bannerList;
    private BannerTimerTask bannerTimerTask;
    private Timer bannerTimer;
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
    ViewPager vp;
    ViewGroup ll_image;

   // View bannerView;
    View typeView;
    FrameLayout fl_viewpager;
    int pageCount;
    ViewPagerAdapter viewPagerAdapter;
    int number = 0;
    int typeNumber=0;
    ImageView home_arraw;
    boolean coupon_createbanenr = false;
    boolean advertisetun = false;
    private RelativeLayout home_rl_arrow;
    private int screenWidth;
    private  int screenHeight;
    private AlibcShowParams alibcShowParams;//页面打开方式，默认，H5，Native
    private Map<String, String> exParams;//yhhpass参数
    private AlibcTaokeParams alibcTaokeParams = null;//淘客参数，包括pid，unionid，subPid
//ClipboardManager cmb;
private LinearLayout youhui_type;
    private ImageView youhui_renqi,youhui_tqh,youhui_jhs;

    private String turnGoodsId;
    private MyHandler hd;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            vp.setCurrentItem(msg.what);
            super.handleMessage(msg);

        }
    };

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.youhui_zi, null);
       // bannerView = View.inflate(getActivity(), R.layout.youhui_banner_coupons, null);
         typeView=View.inflate(getActivity(),R.layout.youhui_addheads,null);
        youhui_type=(LinearLayout)typeView.findViewById(R.id.youhui_type);
        vp = (ViewPager) typeView.findViewById(R.id.vp);
        ll_image = (ViewGroup) typeView.findViewById(R.id.iv_image);
        alibcTaokeParams = new AlibcTaokeParams(SplashActicity1.mPId,"","");
        alibcShowParams = new AlibcShowParams(OpenType.Native, false);
        alibcShowParams.setClientType("taobao_scheme");
        exParams = new HashMap<>();
        exParams.put("isv_code", "appisvcode");
        exParams.put("alibaba", "阿里巴巴");//自定义参数部分，可任意增删改
            hd=new MyHandler((MainAcitivity1)getActivity());
        fl_viewpager = (FrameLayout) typeView.findViewById(R.id.fl_viewpager);
        youhui_renqi=(ImageView)typeView.findViewById(R.id.youhui_renqi);
        youhui_tqh=(ImageView)typeView.findViewById(R.id.youhui_tqh);
        youhui_jhs=(ImageView)typeView.findViewById(R.id.youhui_jhs);
     //cmb = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);

        home_arraw = (ImageView) view.findViewById(R.id.home_arraw);
        home_rl_arrow = (RelativeLayout) view.findViewById(R.id.home_rl_arrow);
        bannerTimer = new Timer();
        screenWidth = AndroidUtils.getWidth(getActivity());
        screenHeight=AndroidUtils.getHeight(getActivity());
        mHasLoadedOnce = false;
        number = 0;
        typeNumber=0;
        coupon_createbanenr = false;
        advertisetun = false;
        listView = (XListView) view.findViewById(R.id.home_zi_listView);
        home_zi_nogoods = (LinearLayout) view.findViewById(R.id.home_zi_nogoods);
        home_zi_nowifi = (LinearLayout) view.findViewById(R.id.home_zi_nowefi);
        home_zi_retro = (TextView) view.findViewById(R.id.home_fragment_tv_retro);
        home_zi_pb = (RelativeLayout) view.findViewById(R.id.home_zi_pb);
        bannerList = new ArrayList<Object>();
        listView.setOverScrollMode(View.OVER_SCROLL_NEVER);
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
                    if (typeId.equals("0")) {
                        ThreadPollFactory.getNormalPool().execute(new bannerThread());
                    }
                    ThreadPollFactory.getNormalPool().execute(new goodsThread(SplashActicity1.mQuanId, "", 1, "", typeId));
                } else {
                    home_zi_pb.setVisibility(View.GONE);
                    home_zi_nowifi.setVisibility(View.VISIBLE);
                    home_zi_nogoods.setVisibility(View.GONE);
                    listView.setVisibility(View.GONE);
                    home_rl_arrow.setVisibility(View.GONE);
                }
            }
        });
        listView.setXListViewListener(new XListView.IXListViewListener() {

            public void onRefresh() {
                if (getNetConnection()) {
                    //savePreferences("couponssuccess"+TypeId, "chenggong");
                    if (typeId.equals("0")) {
                        ThreadPollFactory.getNormalPool().execute(new bannerThread());
                    }
                    ThreadPollFactory.getNormalPool().execute(new goodsThread(SplashActicity1.mQuanId, "", 1, "", typeId));

                } else {
                    hd.sendEmptyMessage(7);
                }
            }

            public void onLoadMore() {

                if (listView.getProgressState()) {
                    if (getNetConnection()) {
                        page++;
                        ThreadPollFactory.getNormalPool().execute(new goodsThread(SplashActicity1.mQuanId, (String) SharedPfUtils.getData(getActivity(), typeId + "youhuizitime", ""), page, "", typeId));
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
                if (NetJudgeUtils.getNetConnection(getActivity())) {
                    CustomProgressDialog.createDialog(getActivity(), "");
                    if (typeId.equals("0")) {
                        GoodsInfo info = (GoodsInfo) goodList.get(position - 2);
                        turnGoodsId=info.getNumIid();
                        ThreadPollFactory.getNormalPool().execute(new TurnUrlThread(info.getNumIid() + "", getActivity()));
                    } else {
                        GoodsInfo info = (GoodsInfo) goodList.get(position - 1);
                        turnGoodsId=info.getNumIid();
                        ThreadPollFactory.getNormalPool().execute(new TurnUrlThread(info.getNumIid() + "", getActivity()));
                    }
                } else {
                    Toast.makeText(getActivity(), R.string.net, Toast.LENGTH_LONG).show();
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
        youhui_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        youhui_jhs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),
                        JuhuasuanActivity.class);
                startActivity(intent);
             getActivity().overridePendingTransition(
                        R.anim.to_right, R.anim.to_left);

            }
        });
        youhui_renqi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),
                        ZitypeActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(
                        R.anim.to_right, R.anim.to_left);
            }
        });
        youhui_tqh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),
                        JiukuaijiuActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(
                        R.anim.to_right, R.anim.to_left);
            }
        });
    }

    @Override
    public void lazyLoad(String id) {
        if (!isPrepared || mHasLoadedOnce || home_zi_pb.getVisibility() == View.VISIBLE) {
            return;
        }

        typeId = id;
        if (typeId.equals("0") && typeNumber < 1) {
//            DisplayMetrics dm = new DisplayMetrics();
//            getActivity().getWindowManager().getDefaultDisplay()
//                    .getMetrics(dm);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)youhui_type
                    .getLayoutParams();
            // 根据图片高和宽的比例计算高度
            // 测试图宽为694 高为323
            params.width = screenWidth;
            params.height = screenWidth*576/1080;
            youhui_type.setLayoutParams(params);
            adapter = new MyAdapter(getActivity());
            listView.addHeaderView(typeView);
            typeNumber = 1;
        }
        if (NetJudgeUtils.getNetConnection(getActivity())) {
            home_zi_pb.setVisibility(View.VISIBLE);
            home_zi_nowifi.setVisibility(View.GONE);
            home_zi_nogoods.setVisibility(View.GONE);
            listView.setVisibility(View.GONE);
            home_rl_arrow.setVisibility(View.GONE);
            if (typeId.equals("0")) {
                //banner
                ThreadPollFactory.getNormalPool().execute(new bannerThread());
            }
            ThreadPollFactory.getNormalPool().execute(new goodsThread(SplashActicity1.mQuanId, "", 1, "", typeId));
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
        MobclickAgent.onPageStart("Youhui_zi_fragment");
        if (typeId.equals("0") && getUserVisibleHint() && bannerList.size() > 0 && coupon_createbanenr) {
            bannerStartPlay();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("Youhui_zi_fragment");
        if (typeId.equals("0") && getUserVisibleHint() && bannerList.size() > 0 && coupon_createbanenr) {
            bannerStopPlay();
        }
    }

    /**
     * 的到圈值接口
     */
    private class bannerThread implements Runnable {

        @Override
        public void run() {
            try {
                String url = getResources().getString(R.string.appurl) + "/advertisement"+"?stamp=" + UrlUtils.getTime() + "&encode=" + UrlUtils.getEncode();
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
        int page;
        String keyword;
        String categoryId;

        public goodsThread(String code, String timeStemp, int page, String keyword, String categoryId) {
            this.code = code;
            this.timeStemp = timeStemp;
            this.page = page;
            this.keyword = keyword;
            this.categoryId = categoryId;
        }

        @Override
        public void run() {
            try {
                String url = getResources().getString(R.string.appurl) + "/goods/discount" + "?code=" + code + "&timeStemp=" + timeStemp + "&page=" + page + "&keyword=" + keyword + "&categoryId=" + categoryId+"&stamp="+ UrlUtils.getTime()+"&encode="+UrlUtils.getEncode();
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
        private WeakReference<MainAcitivity1> mWeakReference = null;

        MyHandler(MainAcitivity1 activity) {
            mWeakReference = new WeakReference<MainAcitivity1>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            MainAcitivity1 mainActivity = mWeakReference.get();
            if(mainActivity==null){
                return;
            }

            if(Build.VERSION.SDK_INT >= 17) {
                if (mainActivity == null || mainActivity.isDestroyed() || mainActivity.isFinishing()) {
                    return;
                }
            }else {
                if (mainActivity== null || mainActivity.isFinishing()) {

                    return;
                }
            }
            if (msg.what == 1) {
                try {
                    JSONObject json = new JSONObject((String) msg.obj);
                    String code = json.getString("code");
                    if (code.equals("0")) {
                        String bannerData = json.getString("data");
                        if (bannerData.equals("[]") || null == bannerData || bannerData.equals("null")) {
                            fl_viewpager.setVisibility(View.GONE);
                        } else {
                            bannerList.clear();
                            bannerList = ParseJsonCommon.parseJsonData(bannerData,
                                    BannerInfo.class);
                            //                            BannerInfo info=new BannerInfo();
                            //                            info.setUrl("www.baidu.com");
                            //                            info.setId(1);
                            //                            info.setImage("https://image.baidu.com/search/detail?ct=503316480&z=0&ipn=d&word=网络图片&step_word=&hs=0&pn=7&spn=0&di=54933880210&pi=0&rn=1&tn=baiduimagedetail&is=0%2C0&istype=0&ie=utf-8&oe=utf-8&in=&cl=2&lm=-1&st=undefined&cs=1307201232%2C1825368047&os=295060668%2C2682231843&simid=3319380264%2C431246799&adpicid=0&lpn=0&ln=1974&fr=&fmq=1506340618134_R&fm=&ic=undefined&s=undefined&se=&sme=&tab=0&width=undefined&height=undefined&face=undefined&ist=&jit=&cg=&bdtype=0&oriquery=&objurl=http%3A%2F%2Fpic.58pic.com%2F58pic%2F15%2F67%2F62%2F70E58PICYGb_1024.jpg&fromurl=ippr_z2C%24qAzdH3FAzdH3Fooo_z%26e3Bcbrtv_z%26e3Bv54AzdH3Ffitstwg2p7AzdH3F8cm0md0a_z%26e3Bip4s&gsm=0&rpstart=0&rpnum=0");
                            //                            info.setName("ass");
                            //                            bannerList.add(info);
                            if (bannerList.size() > 0) {
                                //                                if (typeId.equals("0") && number < 1) {
                                //                                    adapter = new MyAdapter(getActivity());
                                //                                    listView.addHeaderView(bannerView);
                                //                                    number = 1;
                                //                                }
                                fl_viewpager.setVisibility(View.VISIBLE);
                                //                                DisplayMetrics dm = new DisplayMetrics();
                                //                                getActivity().getWindowManager().getDefaultDisplay()
                                //                                        .getMetrics(dm);
                                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) fl_viewpager
                                        .getLayoutParams();
                                // 根据图片高和宽的比例计算高度
                                // 测试图宽为694 高为323
                                params.width = screenWidth;
                                params.height = screenWidth * 280 / 720;
                                fl_viewpager.setLayoutParams(params);
                                pageCount = bannerList.size();// 对应小点个数
                                final ImageView[] imageViews = new ImageView[pageCount];
                                ll_image.removeAllViews();
                                for (int i = 0; i < pageCount; i++) {
                                    LinearLayout.LayoutParams margin = new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.WRAP_CONTENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT);
                                    // 设置每个小圆点距离左边的间距
                                    margin.setMargins(10, 0, 0, 0);
                                    ImageView imageView = new ImageView(mainActivity);
                                    // 设置每个小圆点的宽高
                                    imageView.setLayoutParams(new ViewGroup.LayoutParams(15, 15));
                                    imageViews[i] = imageView;
                                    if (i == 0) {
                                        // 默认选中第一张图片
                                        imageViews[i]
                                                .setBackgroundResource(R.drawable.page_indicator_focused);
                                    } else {
                                        // 其他图片都设置未选中状态
                                        imageViews[i]
                                                .setBackgroundResource(R.drawable.page_indicator_unfocused);
                                    }

                                    ll_image.addView(imageViews[i], margin);
                                    viewPagerAdapter = new ViewPagerAdapter(mainActivity);
                                    vp.setAdapter(viewPagerAdapter);
                                    if (!advertisetun) {
                                        bannerStartPlay();
                                        advertisetun = true;
                                    }
                                    coupon_createbanenr = true;
                                    bannerStartPlay();

                                    vp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                                        @Override
                                        public void onPageScrollStateChanged(int arg0) {
                                            // TODO Auto-generated method stub

                                        }

                                        @Override
                                        public void onPageScrolled(int arg0,
                                                                   float arg1, int arg2) {
                                            // TODO Auto-generated method stub
                                        }

                                        @Override
                                        public void onPageSelected(int arg0) {
                                            // 当viewpager换页时 改掉下面对应的小点
                                            for (int i = 0; i < imageViews.length; i++) {
                                                // 设置当前的对应的小点为选中状态
                                                imageViews[arg0]
                                                        .setBackgroundResource(R.drawable.page_indicator_focused);
                                                if (arg0 != i) {
                                                    // 设置为非选中状态
                                                    imageViews[i]
                                                            .setBackgroundResource(R.drawable.page_indicator_unfocused);
                                                }
                                            }
                                        }

                                    });

                                }
                            } else {
                                fl_viewpager.setVisibility(View.GONE);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    fl_viewpager.setVisibility(View.GONE);
                }
            }
            if (msg.what == 2) {
                fl_viewpager.setVisibility(View.GONE);
            }
            if (msg.what == 3) {
                try {
                    JSONObject j = new JSONObject((String) msg.obj);
                    // System.out.println("j==="+j.toString());
                    String code = j.getString("code");
                    if (code.equals("0")) {
                        page = 1;
                        String data = j.getString("data");
                        JSONObject jData = new JSONObject(data);
                        String goods = jData.getString("goods");
                        maxPage = jData.getInt("totalpage");
                        time = jData.getString("timeStemp");
                        SharedPfUtils.saveStringData(getActivity(), typeId + "youhuizitime", time);
                        if (goods.equals("[]") || null == goods || goods.equals("null")) {
                            if (!typeId.equals("0")) {
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
                                adapter = new MyAdapter(mainActivity);
                                listView.setAdapter(adapter);
                                listView.setPullLoadEnable(false);
                            }

                        } else {
                            goodList.clear();
                            goodList = ParseJsonCommon.parseJsonData(goods,
                                    GoodsInfo.class);
                            if (!typeId.equals("0")) {
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
                                    adapter = new MyAdapter(mainActivity);

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
                                adapter = new MyAdapter(mainActivity);
                                listView.setAdapter(adapter);


                                listView.stopRefresh();
                                listView.setRefreshTime(new Date().toLocaleString());

                            }

                        }
                        mHasLoadedOnce = true;

                    } else {

                        if (!typeId.equals("0")) {
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
                            adapter = new MyAdapter(mainActivity);
                            listView.setAdapter(adapter);
                            listView.setPullLoadEnable(false);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                    if (!typeId.equals("0")) {
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
                        adapter = new MyAdapter(mainActivity);
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
                        Toast.makeText(mainActivity, "数据返回错误!", Toast.LENGTH_SHORT).show();
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
                if (!typeId.equals("0")) {
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
                Toast.makeText(mainActivity, "数据返回错误!", Toast.LENGTH_SHORT).show();
                if (page > 1) {
                    page--;
                }
                listView.stopLoadMore();
            }
            if (msg.what == 7) {
                MyToast.show(getActivity(), 0, getFenbianlv1(),
                        AndroidUtils.dip2px(mainActivity, 45), "您的网络不给力，请检查更新!");
                listView.stopRefresh();
            }
            if (msg.what == 8) {

                listView.stopLoadMore();

            }


            if(msg.what==11||msg.what==12){
                if(MainAcitivity1.choosePosition!=0){
                    CustomProgressDialog.stopProgressDialog();
                    return;
                }
                if(!AndroidUtils.isTopActivity(mainActivity,"MainAcitivity1")){
                    CustomProgressDialog.stopProgressDialog();
                    return;
                }
                if(!getUserVisibleHint()){
                    CustomProgressDialog.stopProgressDialog();
                    return;
                }
            }







            if (msg.what == 11) {
                try {
                    JSONObject j = new JSONObject((String) msg.obj);
                    //System.out.println("j==="+j.toString());
                    String code = j.getString("code");
                    if (code.equals("0")) {
                        String data = j.getString("data");

//
//                                               if(TextUtils.isEmpty(data)){
//                                                   cmb.setText("复制的地址为空");
//                                               }else{
//                                                   cmb.setText("首页优惠券地址为："+data);
//                                               }
//                                               Toast.makeText(getActivity(), "优惠券地址已粘贴!", Toast.LENGTH_LONG).show();


                        // System.out.println("data优惠="+data);
                        if(NetJudgeUtils.getNetConnection(mainActivity)){
                            if(TextUtils.isEmpty(data)){
                                AlibcTrade.show(mainActivity, new AlibcDetailPage(StringUtils.getID(turnGoodsId)), alibcShowParams, alibcTaokeParams, exParams , new DemoTradeCallback());

                            }else{
                                AlibcTrade.show(mainActivity, new AlibcPage(data), alibcShowParams, null, exParams, new DemoTradeCallback());
                                //AlibaPageDetails
                            }
                        }else{
                            Toast.makeText(mainActivity, "您的网络不给力，请检查更新!", Toast.LENGTH_LONG).show();

                        }


                        // AlibcTrade.show(getActivity(), new AlibcDetailPage(StringUtils.getID(turnGoodsId)), alibcShowParams, alibcTaokeParams, exParams , new DemoTradeCallback());


                    } else {
                      //Toast.makeText(mainActivity, "本地转链!", Toast.LENGTH_LONG).show();

                        if(NetJudgeUtils.getNetConnection(mainActivity)){
                            AlibcTrade.show(mainActivity, new AlibcDetailPage(StringUtils.getID(turnGoodsId)), alibcShowParams, alibcTaokeParams, exParams , new DemoTradeCallback());

                        }else{
                            Toast.makeText(mainActivity, "您的网络不给力，请检查更新!", Toast.LENGTH_LONG).show();

                        }

                        //  Toast.makeText(getActivity(), "服务器错误!", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if(NetJudgeUtils.getNetConnection(getActivity())){
                        AlibcTrade.show(mainActivity, new AlibcDetailPage(StringUtils.getID(turnGoodsId)), alibcShowParams, alibcTaokeParams, exParams , new DemoTradeCallback());
                    }else{
                        Toast.makeText(mainActivity, "您的网络不给力，请检查更新!", Toast.LENGTH_LONG).show();

                    }
                } finally {
                    CustomProgressDialog.stopProgressDialog();
                }
            }
            if (msg.what == 12) {
                CustomProgressDialog.stopProgressDialog();
                if(NetJudgeUtils.getNetConnection(mainActivity)){
                    AlibcTrade.show(mainActivity, new AlibcDetailPage(StringUtils.getID(turnGoodsId)), alibcShowParams, alibcTaokeParams, exParams , new DemoTradeCallback());
                }else{
                    Toast.makeText(mainActivity, "您的网络不给力，请检查更新!", Toast.LENGTH_LONG).show();

                }
                // Toast.makeText(getActivity(), "服务器错误!", Toast.LENGTH_LONG).show();
            }

        }
    };

//    private Handler hd = new Handler() {
//        public void handleMessage(Message msg) {
//
//        }
//    };

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

                params = BaseMap.getMapAll(params);
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
                holder.home_temai_shopname = (TextView) view.findViewById(R.id.home_temai_shopname);
                holder.home_temai_discount = (TextView) view.findViewById(R.id.home_temai_discount);
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
           // System.out.println("ggg==="+goodsInfo.getGoodsImage());
            holder.goods_desc.setText(goodsInfo.getTitle());
            holder.goods_discount_price.setText(goodsInfo.getPrice());
            holder.goods_ori_price.setText("￥" + goodsInfo.getOldPrice());
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
        TextView goods_ori_price;
        TextView tv_chengji;
        TextView home_temai_shopname;
        TextView home_temai_discount;

    }

    class ViewPagerAdapter extends PagerAdapter {
        // private AsyncLoaderImage loader;
        private LayoutInflater inflater;
        private Context context;

        public ViewPagerAdapter(Context context) {
            this.context = context;
            inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        }

        @Override
        public int getCount() {

            return bannerList.size();
        }

        ;

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            // container.removeView(bannerList.get(position));
            ((ViewPager) container).removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {

            View layout = inflater.inflate(R.layout.layout_banner_item, null);
            ImageView imageView = (ImageView) layout
                    .findViewById(R.id.iv_banner_item);
            BannerInfo info = new BannerInfo();
            info = (BannerInfo) bannerList.get(position);
            PicassoUtils.loadImageWithHolderAndError(getActivity(), info.getImage(), R.mipmap.bannerpt, R.mipmap.bannerpt, imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BannerInfo info = (BannerInfo) bannerList.get(position);
                    if (!TextUtils.isEmpty(info.getUrl())) {
                        Intent intent = new Intent(getActivity(),
                                WebActivity.class);
                        intent.putExtra("title", info.getName());
                        intent.putExtra("url", info.getUrl());
                        startActivity(intent);
                        getActivity().overridePendingTransition(
                                R.anim.to_right, R.anim.to_left);
                    }else{
                       if(!TextUtils.isEmpty(info.getGoodsId())) {
                           if(NetJudgeUtils.getNetConnection(getActivity())){
                               CustomProgressDialog.createDialog(getActivity(), "");
                               turnGoodsId=info.getGoodsId();
                               ThreadPollFactory.getNormalPool().execute(new TurnUrlThread(info.getGoodsId() + "", getActivity()));
                           }else{
                               Toast.makeText(getActivity(),"您的网络不给力，请检查更新！",Toast.LENGTH_LONG).show();
                           }
                       }
                    }

                }
            });

            ((ViewPager) container).addView(layout, 0);
            //imageView.setOnClickListener(new OnClickListener() {});
            return layout;
        }

    }

    // 暂停banner自动轮播
    public void bannerStopPlay() {

        if (bannerTimerTask != null)
            bannerTimerTask.cancel();
    }

    // 启动banner自动轮播
    public void bannerStartPlay() {
        if (bannerTimer != null) {
            if (bannerTimerTask != null)
                bannerTimerTask.cancel();
            bannerTimerTask = new BannerTimerTask();
            bannerTimer.schedule(bannerTimerTask, 5000, 5000);// 5秒后执行，每隔5秒执行一次
        }
    }

    class BannerTimerTask extends TimerTask {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            Message msg = new Message();

            if (bannerList.size() <= 1)
                return;

            if (null == vp)
                return;

            int currentIndex = vp.getCurrentItem();
            if (currentIndex == bannerList.size() - 1)
                msg.what = 0;
            else
                msg.what = currentIndex + 1;

            handler.sendMessage(msg);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CustomProgressDialog.stopProgressDialog();
    }
}
