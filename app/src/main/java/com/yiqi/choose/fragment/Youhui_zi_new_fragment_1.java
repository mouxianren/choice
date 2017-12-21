package com.yiqi.choose.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.alibaba.baichuan.android.trade.AlibcTrade;
import com.alibaba.baichuan.android.trade.model.AlibcShowParams;
import com.alibaba.baichuan.android.trade.model.OpenType;
import com.alibaba.baichuan.android.trade.page.AlibcDetailPage;
import com.alibaba.baichuan.android.trade.page.AlibcPage;
import com.alibaba.baichuan.trade.biz.core.taoke.AlibcTaokeParams;
import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.chanven.lib.cptr.circle.CircularProgressView;
import com.chanven.lib.cptr.loadmore.OnLoadMoreListener;
import com.chanven.lib.cptr.recyclerview.RecyclerAdapterWithHF;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;
import com.yiqi.choose.R;
import com.yiqi.choose.activity.MainAcitivity1;
import com.yiqi.choose.activity.SplashActicity1;
import com.yiqi.choose.activity.WebActivity;
import com.yiqi.choose.activity.fiveitem.HotActivity;
import com.yiqi.choose.activity.fiveitem.JiukuaijiuActivity;
import com.yiqi.choose.activity.fiveitem.JuhuasuanActivity;
import com.yiqi.choose.activity.fiveitem.TaoqianhuoActivity;
import com.yiqi.choose.activity.fiveitem.TopOneActivity;
import com.yiqi.choose.activity.zitypelist.ItemtypeActivity;
import com.yiqi.choose.adapter.DelegateRecyclerAdapter2;
import com.yiqi.choose.adapter.SingleLayoutAdapter;
import com.yiqi.choose.adapter.SingleLayoutTypeAdapter;
import com.yiqi.choose.alibabaCallback.DemoTradeCallback;
import com.yiqi.choose.base.BaseFragment1_coupons;
import com.yiqi.choose.factory.ThreadPollFactory;
import com.yiqi.choose.model.BannerInfoNew;
import com.yiqi.choose.model.GoodsInfo;
import com.yiqi.choose.thread.BannerThread;
import com.yiqi.choose.thread.StatisticsThread;
import com.yiqi.choose.thread.TurnUrlThread;
import com.yiqi.choose.utils.AndroidUtils;
import com.yiqi.choose.utils.BooleanThread;
import com.yiqi.choose.utils.CustomProgressDialog;
import com.yiqi.choose.utils.HttpConBase;
import com.yiqi.choose.utils.MyToast;
import com.yiqi.choose.utils.NetJudgeUtils;
import com.yiqi.choose.utils.ParseJsonCommon;
import com.yiqi.choose.utils.SharedPfUtils;
import com.yiqi.choose.utils.StringUtils;
import com.yiqi.choose.utils.UrlUtils;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.yiqi.choose.R.id.home_rl_arrow;
import static com.yiqi.choose.R.id.home_zi_nogoods;
import static com.yiqi.choose.R.id.recyclerview;

/**
 * Created by moumou on 17/12/18.
 */

public class Youhui_zi_new_fragment_1 extends BaseFragment1_coupons {
    @BindView(recyclerview)
    RecyclerView mRecyclerview;
    @BindView(R.id.home_arraw)
    ImageView mHomeArraw;
    @BindView(home_rl_arrow)
    RelativeLayout mHomeRlArrow;
    @BindView(R.id.test_recycler_view_frame)
    PtrClassicFrameLayout mTestRecyclerViewFrame;
    @BindView(home_zi_nogoods)
    LinearLayout mHomeZiNogoods;
    @BindView(R.id.home_fragment_tv_retro)
    TextView mHomeFragmentTvRetro;
    @BindView(R.id.home_zi_nowefi)
    LinearLayout mHomeZiNowefi;
    @BindView(R.id.cp_image)
    CircularProgressView mCpImage;
    @BindView(R.id.cp_rl_image)
    RelativeLayout mCpRlImage;
    Unbinder unbinder;

    /**
     * 标志位，标志已经初始化完成
     */
    private boolean isPrepared;
    /**
     * 是否已被加载过一次，第二次就不再去请求数据了
     */
    private boolean mHasLoadedOnce;

    private String typeId = "";
    public static List<Object> bannerList;

    private int screenWidth;
    private RecyclerAdapterWithHF mAdapter;
    private MyHandler hd;
    private DelegateAdapter adapter;
    private SingleLayoutAdapter singleLayoutAdapter;
    private SingleLayoutTypeAdapter mSingleLayoutTypeAdapter;
    private DelegateRecyclerAdapter2 goodsAdapter;
    Handler handler = new Handler();
    private String turnGoodsId;
    private AlibcShowParams alibcShowParams;//页面打开方式，默认，H5，Native
    private Map<String, String> exParams;//yhhpass参数
    private AlibcTaokeParams alibcTaokeParams = null;//淘客参数，包括pid，unionid，subPid
    int page = 1;
    int maxPage = 0;
    String time = "";

    private int srceenWidth = 0;
    private LinearLayout.LayoutParams params;

    public List<Object> goodList;
    public  List<Object> ziList;

    private int ziTypeNumber;


    private boolean hasBanner;

    private boolean hasLoadMoreFinish = true;



    private int position = 0;


    private String keyword;
    private int orders = 0;


    @Override
    public View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.youhui_zi_new, null);
        unbinder = ButterKnife.bind(this, view);
        isPrepared = true;
        mHasLoadedOnce = false;
        typeId = getArguments().getString("typeId");
        ziTypeNumber = getArguments().getInt("ziTypeNumber");
        keyword = getArguments().getString("keyword");
        orders = 0;
        position = getArguments().getInt("position");
        hd = new MyHandler((MainAcitivity1) getActivity());
        bannerList = new ArrayList<Object>();
        screenWidth = AndroidUtils.getWidth(getActivity());
        RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
        viewPool.setMaxRecycledViews(0, 1);
        mRecyclerview.setRecycledViewPool(viewPool);
        mRecyclerview.setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        VirtualLayoutManager manager = new VirtualLayoutManager(getActivity());
        mRecyclerview.setLayoutManager(manager);
        adapter = new DelegateAdapter(manager, false);
        goodList = new ArrayList<Object>();
        ziList = new ArrayList<Object>();
        mTestRecyclerViewFrame.disableWhenHorizontalMove(true);
        mTestRecyclerViewFrame.setLoadMoreEnable(false);
        hasBanner = false;
        hasLoadMoreFinish = true;
        alibcTaokeParams = new AlibcTaokeParams(SplashActicity1.mPId, "", "");
        alibcShowParams = new AlibcShowParams(OpenType.Native, false);
        alibcShowParams.setClientType("taobao_scheme");
        exParams = new HashMap<>();
        exParams.put("isv_code", "appisvcode");
        exParams.put("alibaba", "阿里巴巴");//自定义参数部分，可任意增删改

        goodsAdapter=null;

        srceenWidth = AndroidUtils.getWidth(getActivity());
        params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.width = (int) (srceenWidth * 0.361);
        params.height = (int) (srceenWidth * 0.361);


        return view;
    }

    @Override
    public void initData() {
        initListener();
        mRecyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                final Picasso picasso = Picasso.with(getActivity());
                if (newState == 0 || newState == 1) {
                    picasso.resumeTag(getActivity());
                } else {
                    picasso.pauseTag(getActivity());
                }
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                //判断是当前layoutManager是否为LinearLayoutManager
                // 只有LinearLayoutManager才有查找第一个和最后一个可见view位置的方法
                if (layoutManager instanceof LinearLayoutManager) {
                    LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
                    //获取最后一个可见view的位置
                    int lastItemPosition = linearManager.findLastVisibleItemPosition();
                    //获取第一个可见view的位置
                    int firstItemPosition = linearManager.findFirstVisibleItemPosition();
                    if (firstItemPosition > 18) {
                        mHomeArraw.setVisibility(View.VISIBLE);
                    } else {
                        mHomeArraw.setVisibility(View.GONE);
                    }

                } else if (layoutManager instanceof GridLayoutManager) {
                    GridLayoutManager linearManager = (GridLayoutManager) layoutManager;
                    //获取最后一个可见view的位置
                    int lastItemPosition = linearManager.findLastVisibleItemPosition();
                    //获取第一个可见view的位置
                    int firstItemPosition = linearManager.findFirstVisibleItemPosition();

                    if (firstItemPosition > 18) {
                        mHomeArraw.setVisibility(View.VISIBLE);

                    } else {
                        mHomeArraw.setVisibility(View.GONE);
                    }

                }
            }
        });
    }

    private void initListener() {

        mTestRecyclerViewFrame.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                if (getNetConnection()) {
                    if (hasLoadMoreFinish) {
                        if (typeId.equals("0")) {
                            ThreadPollFactory.getNormalPool().execute(new BannerThread(getActivity(), hd));
                        }
                        ThreadPollFactory.getNormalPool().execute(new goodsThread(SplashActicity1.mQuanId, "", 1, keyword, typeId, orders));
                    } else {
                        hd.sendEmptyMessage(20);
                    }

                } else {
                    hd.sendEmptyMessage(7);
                }
            }


        });
        mTestRecyclerViewFrame.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void loadMore() {

                if (getNetConnection()) {
                    if (hasLoadMoreFinish) {
                        page++;
                        ThreadPollFactory.getNormalPool().execute(new goodsThread(SplashActicity1.mQuanId, (String) SharedPfUtils.getData(getActivity(), typeId + "youhuizitime", ""), page, keyword, typeId, orders));
                    } else {
                        hd.sendEmptyMessage(19);
                    }

                } else {
                    hd.sendEmptyMessage(8);
                }


            }
        });
    }

    public void initGoodsListener() {
        goodsAdapter.setOnItemClickListenr(new DelegateRecyclerAdapter2.OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {
                if (NetJudgeUtils.getNetConnection(getActivity())) {
                    CustomProgressDialog.createDialog(getActivity(), "");
                    GoodsInfo info = (GoodsInfo) goodList.get(position);
                    turnGoodsId=info.getNumIid();
                    ThreadPollFactory.getNormalPool().execute(new TurnUrlThread(info.getNumIid() + "", getActivity(),hd));

                } else {
                    Toast.makeText(getActivity(), R.string.net, Toast.LENGTH_LONG).show();
                }
            }
        });

    }


    public void initBannerListener() {
        singleLayoutAdapter.setOnItemClickListenr(new SingleLayoutAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {
                BannerInfoNew info = (BannerInfoNew) bannerList.get(position);
                if (!info.getType().equals("3")) {
                    if (!TextUtils.isEmpty(info.getUrl())) {
                        Intent intent = new Intent(getActivity(),
                                WebActivity.class);
                        intent.putExtra("title", info.getName());
                        intent.putExtra("url", info.getUrl());
                        startActivity(intent);
                        getActivity().overridePendingTransition(
                                R.anim.to_right, R.anim.to_left);
                    } else {
                        if (!TextUtils.isEmpty(info.getGoodsId())) {
                            if (NetJudgeUtils.getNetConnection(getActivity())) {
                                CustomProgressDialog.createDialog(getActivity(), "");
                                turnGoodsId = info.getGoodsId();
                                ThreadPollFactory.getNormalPool().execute(new TurnUrlThread(info.getGoodsId() + "", getActivity(), hd));
                            } else {
                                Toast.makeText(getActivity(), "您的网络不给力，请检查更新！", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                } else {
                    Intent intent = new Intent(getActivity(),
                            ItemtypeActivity.class);
                    intent.putExtra("adId", info.getId());
                    intent.putExtra("name", info.getName());
                    startActivity(intent);
                    getActivity().overridePendingTransition(
                            R.anim.to_right, R.anim.to_left);
                }
            }
        });
        mSingleLayoutTypeAdapter.setOnItemClickListenr(new SingleLayoutTypeAdapter.OnItemClickListener() {
            @Override
            public void onTopOneClick() {
                if(BooleanThread.toService){
                    if (getNetConnection()){
                        ThreadPollFactory.getNormalPool().execute(new StatisticsThread(getActivity(),2));
                    }
                }
                Intent intent = new Intent(getActivity(),
                        TopOneActivity.class);

                startActivity(intent);
                getActivity().overridePendingTransition(
                        R.anim.to_right, R.anim.to_left);
            }

            @Override
            public void hotClick() {
                if(BooleanThread.toService){
                    if (getNetConnection()){
                        ThreadPollFactory.getNormalPool().execute(new StatisticsThread(getActivity(),3));
                    }
                }
                Intent intent = new Intent(getActivity(),
                        HotActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(
                        R.anim.to_right, R.anim.to_left);
            }

            @Override
            public void taoqhClick() {
                if(BooleanThread.toService){
                    if (getNetConnection()){
                        ThreadPollFactory.getNormalPool().execute(new StatisticsThread(getActivity(),4));
                    }
                }
                Intent intent = new Intent(getActivity(),
                        TaoqianhuoActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(
                        R.anim.to_right, R.anim.to_left);
            }

            @Override
            public void jkjClick() {
                if(BooleanThread.toService){
                    if (getNetConnection()){
                        ThreadPollFactory.getNormalPool().execute(new StatisticsThread(getActivity(),5));
                    }
                }
                Intent intent = new Intent(getActivity(),
                        JiukuaijiuActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(
                        R.anim.to_right, R.anim.to_left);
            }

            @Override
            public void jhsClick() {
                if(BooleanThread.toService){
                    if (getNetConnection()){
                        ThreadPollFactory.getNormalPool().execute(new StatisticsThread(getActivity(),1));
                    }
                }
                Intent intent = new Intent(getActivity(),
                        JuhuasuanActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(
                        R.anim.to_right, R.anim.to_left);
            }
        });


    }

    @Override
    public void lazyLoad(String id) {
        if (!isPrepared || mHasLoadedOnce || mCpRlImage.getVisibility() == View.VISIBLE) {
            return;
        }
        typeId = id;
        initTypeFive(getActivity(), screenWidth);
        adapter.addAdapter(mSingleLayoutTypeAdapter);
        mAdapter = new RecyclerAdapterWithHF(adapter);
        mRecyclerview.setAdapter(mAdapter);
        loadingData();

    }

    private void loadingData() {
        if (NetJudgeUtils.getNetConnection(getActivity())) {
            mCpRlImage.setVisibility(View.VISIBLE);
            mHomeZiNowefi.setVisibility(View.GONE);
            mHomeZiNogoods.setVisibility(View.GONE);
            mHomeArraw.setVisibility(View.GONE);
            mHomeRlArrow.setVisibility(View.GONE);
            mCpImage.innerStart();
            if (typeId.equals("0")) {
                //banner
                ThreadPollFactory.getNormalPool().execute(new BannerThread(getActivity(), hd));
            }
            ThreadPollFactory.getNormalPool().execute(new goodsThread(SplashActicity1.mQuanId, "",1, keyword, typeId, orders));

        } else {
            invisbableView1();
        }
    }

    private void invisableView() {
        mHomeRlArrow.setVisibility(View.VISIBLE);
        mHomeZiNowefi.setVisibility(View.GONE);
        mCpRlImage.setVisibility(View.GONE);
        mHomeZiNogoods.setVisibility(View.GONE);
        mHomeArraw.setVisibility(View.GONE);
        mCpImage.innerStop();
    }

    private void invisbableView1() {
        mHomeRlArrow.setVisibility(View.GONE);
        mHomeZiNowefi.setVisibility(View.VISIBLE);
        mCpRlImage.setVisibility(View.GONE);
        mHomeZiNogoods.setVisibility(View.GONE);
        mHomeArraw.setVisibility(View.GONE);
        mCpImage.innerStop();
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
            if (msg.what == 1) {
                try {
                    JSONObject json = new JSONObject((String) msg.obj);
                    String code = json.getString("code");
                    if (code.equals("0")) {
                        String bannerData = json.getString("data");
                        if (bannerData.equals("[]") || null == bannerData || bannerData.equals("null")) {

                        } else {
                            bannerList.clear();
                            bannerList = ParseJsonCommon.parseJsonData(bannerData,
                                    BannerInfoNew.class);
                            invisableView();
                            if (bannerList.size() > 0) {
                                if (hasBanner) {
                                    singleLayoutAdapter.notifyDataSetChanged();
                                } else {
                                    initBannerList(getActivity(), screenWidth, (MainAcitivity1) getActivity());
                                    adapter.addAdapter(0, singleLayoutAdapter);
                                    singleLayoutAdapter.bannerStartPlay();
                                    initBannerListener();
                                    hasBanner = true;
                                }
                            } else {
                                if (hasBanner) {
                                    singleLayoutAdapter.notifyDataSetChanged();
                                }
                            }

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (msg.what == 2) {
                mTestRecyclerViewFrame.refreshComplete();
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
                        SharedPfUtils.saveStringData(getActivity(), typeId + "youhuizitime", time);
                        if (goods.equals("[]") || null == goods || goods.equals("null")) {
                            invisableView();
                        } else {
                            goodList.clear();
                            goodList = ParseJsonCommon.parseJsonData(goods,
                                    GoodsInfo.class);
                            invisableView();
                            if (maxPage > 1) {
                                mTestRecyclerViewFrame.setLoadMoreEnable(true);
                            } else {
                                mTestRecyclerViewFrame.setLoadMoreEnable(false);
                            }


                        }
                        mHasLoadedOnce = true;
                    } else {
                        invisbableView1();
                        mTestRecyclerViewFrame.setLoadMoreEnable(false);

                    }
                } catch (Exception e) {
                    e.printStackTrace();

                    invisbableView1();
                    mTestRecyclerViewFrame.setLoadMoreEnable(false);


                } finally {
                    if (goodsAdapter != null) {
                        goodsAdapter.noticeAdapter(goodList,0);
                        initGoodsListener();
                    } else {
                        initGoodsAdapter(getActivity(), screenWidth);
                        adapter.addAdapter(goodsAdapter);
                        initGoodsListener();
                    }
                    mTestRecyclerViewFrame.refreshComplete();

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
                        //goodList.addAll(ziList);

                        if (page >= maxPage) {
                            mTestRecyclerViewFrame.setLoadMoreEnable(false);
                            mTestRecyclerViewFrame.loadMoreComplete(false);
                        } else {
                            mTestRecyclerViewFrame.setLoadMoreEnable(true);
                            mTestRecyclerViewFrame.loadMoreComplete(true);
                        }
                        goodsAdapter.noticeAdapterNext(ziList,0);// 告诉listView数据发生改变，要求listView更新显示
                        mTestRecyclerViewFrame.refreshComplete();

                    } else {
                        if (page > 1) {
                            page--;
                        }
                        mTestRecyclerViewFrame.refreshComplete();
                        mTestRecyclerViewFrame.setLoadMoreEnable(false);
                        mTestRecyclerViewFrame.loadMoreComplete(false);
                        Toast.makeText(mainActivity, "数据返回错误!", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    mTestRecyclerViewFrame.refreshComplete();
                    mTestRecyclerViewFrame.setLoadMoreEnable(false);
                    mTestRecyclerViewFrame.loadMoreComplete(false);
                } finally {
                    hasLoadMoreFinish = true;
                }
            }
            if (msg.what == 5) {
                mTestRecyclerViewFrame.refreshComplete();
                mTestRecyclerViewFrame.setLoadMoreEnable(false);
                mTestRecyclerViewFrame.loadMoreComplete(false);
                if (!typeId.equals("0")) {
                    if (ziTypeNumber > 0) {
                        invisableView();
                    } else {
                        invisbableView1();
                    }
                } else {
                    invisbableView1();
                }
            }
            if (msg.what == 6) {
                Toast.makeText(mainActivity, "数据返回错误!", Toast.LENGTH_SHORT).show();
                if (page > 1) {
                    page--;
                }
                hasLoadMoreFinish = true;
                mTestRecyclerViewFrame.setLoadMoreEnable(false);
                mTestRecyclerViewFrame.loadMoreComplete(false);
            }
            if (msg.what == 7) {
                MyToast.show(getActivity(), 0, getFenbianlv1(),
                        AndroidUtils.dip2px(mainActivity, 45), "您的网络不给力，请检查更新!");
                mTestRecyclerViewFrame.refreshComplete();
            }
            if (msg.what == 8) {
                if (page >= maxPage) {
                    mTestRecyclerViewFrame.loadMoreComplete(false);
                } else {
                    mTestRecyclerViewFrame.loadMoreComplete(true);
                }


            }
            if (msg.what == 19) {
                if (page >= maxPage) {
                    mTestRecyclerViewFrame.loadMoreComplete(false);
                } else {
                    mTestRecyclerViewFrame.loadMoreComplete(true);
                }
            }
            if (msg.what == 19) {
                mTestRecyclerViewFrame.refreshComplete();
            }


            if (msg.what == 11) {
                try {
                    JSONObject j = new JSONObject((String) msg.obj);
                    String code = j.getString("code");
                    if (code.equals("0")) {
                        String data = j.getString("data");
                        if (NetJudgeUtils.getNetConnection(mainActivity)) {
                            if (TextUtils.isEmpty(data)) {
                                AlibcTrade.show(mainActivity, new AlibcDetailPage(StringUtils.getID(turnGoodsId)), alibcShowParams, alibcTaokeParams, exParams, new DemoTradeCallback());

                            } else {
                                AlibcTrade.show(mainActivity, new AlibcPage(data), alibcShowParams, null, exParams, new DemoTradeCallback());
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

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("Youhui_zi_new_fragment_1");
        if (bannerList.size() > 0 && typeId.equals("0") && getUserVisibleHint()) {
            if (singleLayoutAdapter != null) {
                singleLayoutAdapter.bannerStartPlay();
            }
        }


    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("Youhui_zi_new_fragment_1");
        if (bannerList.size() > 0 && typeId.equals("0") && getUserVisibleHint()) {
            if (singleLayoutAdapter != null) {
                singleLayoutAdapter.bannerStartPlay();
            }
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        CustomProgressDialog.stopProgressDialog();

    }

    @OnClick({R.id.home_arraw, R.id.home_fragment_tv_retro})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.home_arraw:
                mRecyclerview.scrollToPosition(0);
                mHomeArraw.setVisibility(View.GONE);
                break;
            case R.id.home_fragment_tv_retro:
                loadingData();
                break;
        }
    }

    public void initBannerList(Context context, int wid, Activity activity) {
        LinearLayoutHelper singleLayoutHelper = new LinearLayoutHelper();
        //设置间距
        singleLayoutAdapter = new SingleLayoutAdapter(context, singleLayoutHelper, wid, activity);
    }

    public void initTypeFive(Context context, int wid) {
        LinearLayoutHelper linearLayoutHelper = new LinearLayoutHelper();
        //设置间距
        mSingleLayoutTypeAdapter = new SingleLayoutTypeAdapter(context, linearLayoutHelper, wid);
    }

    public void initGoodsAdapter(Context context, int wid) {
        LinearLayoutHelper linearLayoutHelper = new LinearLayoutHelper();
        //设置间距
        goodsAdapter = new DelegateRecyclerAdapter2(context, linearLayoutHelper,params,goodList);
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
        int order;

        public goodsThread(String code, String timeStemp, int page, String keyword, String categoryId, int order) {
            this.code = code;
            this.timeStemp = timeStemp;
            this.page = page;
            this.keyword = keyword;
            this.categoryId = categoryId;
            this.order = order;
        }

        @Override
        public void run() {
            try {
                String url = getResources().getString(R.string.appurltest) + "/goods/goodscategory" + "?code=" + code + "&timeStemp=" + timeStemp + "&page=" + page + "&order=" + order + "&keyword=" + keyword + "&categoryId=" + categoryId + "&stamp=" + UrlUtils.getTime() + "&encode=" + UrlUtils.getEncode();
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
}
