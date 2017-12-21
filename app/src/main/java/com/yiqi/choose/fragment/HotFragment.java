package com.yiqi.choose.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
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
import com.alibaba.baichuan.android.trade.page.AlibcDetailPage;
import com.alibaba.baichuan.android.trade.page.AlibcPage;
import com.alibaba.baichuan.trade.biz.core.taoke.AlibcTaokeParams;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;
import com.yiqi.choose.R;
import com.yiqi.choose.activity.MainAcitivity1;
import com.yiqi.choose.activity.SearchYouhuiNewActivityCopy;
import com.yiqi.choose.activity.SplashActicity1;
import com.yiqi.choose.alibabaCallback.DemoTradeCallback;
import com.yiqi.choose.base.BaseFragment;
import com.yiqi.choose.factory.ThreadPollFactory;
import com.yiqi.choose.model.GoodsInfo;
import com.yiqi.choose.thread.TurnUrlThread;
import com.yiqi.choose.utils.AndroidUtils;
import com.yiqi.choose.utils.CustomProgressDialog;
import com.yiqi.choose.utils.HttpConBase;
import com.yiqi.choose.utils.MyToast;
import com.yiqi.choose.utils.NetJudgeUtils;
import com.yiqi.choose.utils.ParseJsonCommon;
import com.yiqi.choose.utils.PicassoUtils;
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


/**
 * Created by moumou on 17/8/16.
 */

public class HotFragment extends BaseFragment {
    private LinearLayout home_fragment_search;
    XListView home_jinxuan_listView;
    LinearLayout home_jinxuan_nogoods;
    LinearLayout home_jinxuan_ll_noweb;
    TextView home_jixuan_tv_try;
    RelativeLayout home_jinxuan_rl_pg;


    private List<Object> goodList;
    private List<Object> ziList;
    int page = 1;
    int maxPage = 0;
    MyAdapter adapter;
    ImageView home_arraw;
    private AlibcShowParams alibcShowParams;//页面打开方式，默认，H5，Native
    private Map<String, String> exParams;//yhhpass参数
    private AlibcTaokeParams alibcTaokeParams = null;//淘客参数，包括pid，unionid，subPid
    /**
     * 标志位，标志已经初始化完成
     */
    private boolean isPrepared;
    /**
     * 是否已被加载过一次，第二次就不再去请求数据了
     */
    private boolean mHasLoadedOnce;
    private String turnGoodsId;
    private MyHandler hd;

    //ClipboardManager cmb;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.hot_temai,null);
        home_jinxuan_listView = (XListView) view.findViewById(R.id.home_jinxuan_listview);
        home_jinxuan_nogoods = (LinearLayout) view.findViewById(R.id.home_jinxuan_nogoods);
        home_jinxuan_ll_noweb = (LinearLayout) view.findViewById(R.id.home_jinxuan_ll_noweb);
        home_jixuan_tv_try = (TextView) view.findViewById(R.id.home_fragment_tv_retro);
        home_jinxuan_rl_pg = (RelativeLayout) view.findViewById(R.id.home_jinxuan_rl_pg);
        home_arraw=(ImageView)view.findViewById(R.id.home_arraw);
        home_fragment_search=(LinearLayout) view.findViewById(R.id.home_fragment_search);

        isPrepared = true;
        mHasLoadedOnce = false;
        home_jinxuan_listView.setVisibility(View.GONE);
        home_jinxuan_nogoods.setVisibility(View.GONE);
        home_jinxuan_ll_noweb.setVisibility(View.GONE);
        home_jinxuan_rl_pg.setVisibility(View.GONE);
        home_arraw.setVisibility(View.GONE);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        alibcTaokeParams = new AlibcTaokeParams(SplashActicity1.mPId, "", "");
        alibcShowParams = new AlibcShowParams(OpenType.Native, false);
        alibcShowParams.setClientType("taobao_scheme");
        exParams = new HashMap<>();
        exParams.put("isv_code", "appisvcode");
        exParams.put("alibaba", "阿里巴巴");//自定义参数部分，可任意增删改
        hd=new MyHandler((MainAcitivity1)getActivity());
        goodList = new ArrayList<Object>();
        ziList = new ArrayList<Object>();

        //cmb = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);

        home_jixuan_tv_try.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(NetJudgeUtils.getNetConnection(getActivity())){
                    home_jinxuan_listView.setVisibility(View.GONE);
                    home_jinxuan_nogoods.setVisibility(View.GONE);
                    home_jinxuan_ll_noweb.setVisibility(View.GONE);
                    home_jinxuan_rl_pg.setVisibility(View.VISIBLE);
                    ThreadPollFactory.getNormalPool().execute(new goodsThread(1));
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
                if (NetJudgeUtils.getNetConnection(getActivity())) {
                    ThreadPollFactory.getNormalPool().execute(new goodsThread(1));
                } else {
                    hd.sendEmptyMessage(7);
                }
            }

            public void onLoadMore() {
                if (home_jinxuan_listView.getProgressState()) {
                    if (NetJudgeUtils.getNetConnection(getActivity())) {
                        page++;
                        ThreadPollFactory.getNormalPool().execute(new goodsThread(page));
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
                if(NetJudgeUtils.getNetConnection(getActivity())){
                    CustomProgressDialog.createDialog(getActivity(),"");
                    GoodsInfo info=(GoodsInfo)goodList.get(position-1);
                    turnGoodsId=info.getNumIid();
                    ThreadPollFactory.getNormalPool().execute(new TurnUrlThread(info.getNumIid() + "", getActivity(),hd));
                    //AlibcTrade.show(JinxuanActivity.this, new AlibcPage(info.getGoodsUrl()),alibcShowParams, alibcTaokeParams, exParams , new DemoTradeCallback());

                }else{
                    Toast.makeText(getActivity(),"您的网络不给力，请检查更新!",Toast.LENGTH_LONG).show();
                }


            }
        });
        home_jinxuan_listView.setOnScrollListener(new AbsListView.OnScrollListener() {
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
                home_jinxuan_listView.setSelection(0);
            }
        });

        home_fragment_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),
                       SearchYouhuiNewActivityCopy.class);
                startActivity(intent);
                getActivity().overridePendingTransition(
                        R.anim.to_right, R.anim.to_left);
            }
        });

    }

    @Override
    protected void lazyLoad() {
        if (!isPrepared || mHasLoadedOnce || home_jinxuan_rl_pg.getVisibility() == View.VISIBLE) {
            return;
        }
        if(NetJudgeUtils.getNetConnection(getActivity())){
            home_jinxuan_listView.setVisibility(View.GONE);
            home_jinxuan_nogoods.setVisibility(View.GONE);
            home_jinxuan_ll_noweb.setVisibility(View.GONE);
            home_jinxuan_rl_pg.setVisibility(View.VISIBLE);
            ThreadPollFactory.getNormalPool().execute(new goodsThread(1));
        }else{
            home_jinxuan_listView.setVisibility(View.GONE);
            home_jinxuan_nogoods.setVisibility(View.GONE);
            home_jinxuan_ll_noweb.setVisibility(View.VISIBLE);
            home_jinxuan_rl_pg.setVisibility(View.GONE);
        }
    }



    /**
     * 的到圈值接口
     */
    private class goodsThread implements Runnable {

        int page;


        public goodsThread(int page) {

            this.page = page;
        }

        @Override
        public void run() {
            try {
                String url = getResources().getString(R.string.appurl) + "/goods2/hot" + "?page=" + page+"&stamp="+ UrlUtils.getTime()+"&encode="+UrlUtils.getEncode();
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
                                adapter = new MyAdapter(mainActivity);

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

                        mHasLoadedOnce=true;

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
                        maxPage = jData.getInt("totalpage");
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
                        Toast.makeText(mainActivity, "数据返回错误!", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(mainActivity, "数据返回错误!", Toast.LENGTH_SHORT).show();
                if (page > 1) {
                    page--;
                }
                home_jinxuan_listView.stopLoadMore();
            }
            if (msg.what == 7) {
                MyToast.show(mainActivity, 0,AndroidUtils.getWidth(mainActivity),
                        AndroidUtils.dip2px(mainActivity, 45), "您的网络不给力，请检查更新!");
                home_jinxuan_listView.stopRefresh();
            }
            if (msg.what == 8) {

                home_jinxuan_listView.stopLoadMore();

            }
            if(msg.what==11||msg.what==12){
                if(MainAcitivity1.choosePosition!=1){
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
                    String code = j.getString("code");
                    if (code.equals("0")) {
                        String data = j.getString("data");
                        //  data="http:www.host.com";
                        //
//                                               if(TextUtils.isEmpty(data)){
//                                                   cmb.setText("复制的地址为空");
//                                               }else{
//                                                   cmb.setText("热门优惠券地址为："+data);
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
                        if(NetJudgeUtils.getNetConnection(mainActivity)){
                            AlibcTrade.show(mainActivity, new AlibcDetailPage(StringUtils.getID(turnGoodsId)), alibcShowParams, alibcTaokeParams, exParams , new DemoTradeCallback());

                        }else{
                            Toast.makeText(mainActivity, "您的网络不给力，请检查更新!", Toast.LENGTH_LONG).show();

                        }

                        //  Toast.makeText(getActivity(), "服务器错误!", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if(NetJudgeUtils.getNetConnection(mainActivity)){
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


    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("HotFragment");

    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("HotFragment");
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
            PicassoUtils.loadImageWithHolderAndError(getActivity(), goodsInfo.getGoodsImage(), R.mipmap.picture, R.mipmap.picture, holder.good_img);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        CustomProgressDialog.stopProgressDialog();
    }
}
