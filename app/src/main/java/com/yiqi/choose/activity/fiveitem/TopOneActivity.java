package com.yiqi.choose.activity.fiveitem;

import android.annotation.TargetApi;
import android.content.Context;
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
import com.alibaba.baichuan.android.trade.page.AlibcDetailPage;
import com.alibaba.baichuan.android.trade.page.AlibcPage;
import com.alibaba.baichuan.trade.biz.core.taoke.AlibcTaokeParams;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;
import com.yiqi.choose.R;
import com.yiqi.choose.activity.SplashActicity1;
import com.yiqi.choose.alibabaCallback.DemoTradeCallback;
import com.yiqi.choose.base.BaseActivity;
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
import com.yiqi.choose.utils.SystemBarTintManager;
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

public class TopOneActivity extends BaseActivity {
    private LinearLayout back_btn;

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
    private String turnGoodsId;
    private AlibcShowParams alibcShowParams;//页面打开方式，默认，H5，Native
    private Map<String, String> exParams;//yhhpass参数
    private AlibcTaokeParams alibcTaokeParams = null;//淘客参数，包括pid，unionid，subPid
    private MyHandler hd;

    private LinearLayout.LayoutParams params;
    private int srceenWidth;

    //ClipboardManager cmb;
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
        setContentView(R.layout.youhui_topone);
        alibcTaokeParams = new AlibcTaokeParams(SplashActicity1.mPId, "", "");
        alibcShowParams = new AlibcShowParams(OpenType.Native, false);
        alibcShowParams.setClientType("taobao_scheme");
        exParams = new HashMap<>();
        exParams.put("isv_code", "appisvcode");
        exParams.put("alibaba", "阿里巴巴");//自定义参数部分，可任意增删改
        hd=new MyHandler(TopOneActivity.this);
        home_jinxuan_listView = (XListView) this.findViewById(R.id.home_jinxuan_listview);
        home_jinxuan_nogoods = (LinearLayout) this.findViewById(R.id.home_jinxuan_nogoods);
        home_jinxuan_ll_noweb = (LinearLayout) this.findViewById(R.id.home_jinxuan_ll_noweb);
        home_jixuan_tv_try = (TextView) this.findViewById(R.id.home_fragment_tv_retro);
        home_jinxuan_rl_pg = (RelativeLayout) this.findViewById(R.id.home_jinxuan_rl_pg);
        home_arraw=(ImageView)this.findViewById(R.id.home_arraw);
        goodList = new ArrayList<Object>();
        ziList = new ArrayList<Object>();
      //  cmb = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
        srceenWidth = AndroidUtils.getWidth(TopOneActivity.this);

        params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.width = (int) (srceenWidth * 0.361);
        params.height = (int) (srceenWidth * 0.361);

        back_btn = (LinearLayout) this.findViewById(R.id.ll_back);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TopOneActivity.this.finish();
                overridePendingTransition(R.anim.in_from_left,
                        R.anim.out_of_right);
            }
        });
        if(NetJudgeUtils.getNetConnection(TopOneActivity.this)){
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
        home_jixuan_tv_try.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(NetJudgeUtils.getNetConnection(TopOneActivity.this)){
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
                if (NetJudgeUtils.getNetConnection(TopOneActivity.this)) {
                    ThreadPollFactory.getNormalPool().execute(new goodsThread(1));
                } else {
                    hd.sendEmptyMessage(7);
                }
            }

            public void onLoadMore() {
                if (home_jinxuan_listView.getProgressState()) {
                    if (NetJudgeUtils.getNetConnection(TopOneActivity.this)) {
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

                if(NetJudgeUtils.getNetConnection(TopOneActivity.this)){
                    CustomProgressDialog.createDialog(TopOneActivity.this,"");
                    GoodsInfo info=(GoodsInfo)goodList.get(position-1);
                    turnGoodsId=info.getNumIid();
                    ThreadPollFactory.getNormalPool().execute(new TurnUrlThread(info.getNumIid()+"",TopOneActivity.this,hd));
                    //AlibcTrade.show(JinxuanActivity.this, new AlibcPage(info.getGoodsUrl()),alibcShowParams, alibcTaokeParams, exParams , new DemoTradeCallback());

                }else{
                    Toast.makeText(TopOneActivity.this,"您的网络不给力，请检查更新!",Toast.LENGTH_LONG).show();
                }


            }
        });
        home_jinxuan_listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // TODO Auto-generated method stub
                final Picasso picasso = Picasso.with(TopOneActivity.this);
                if (scrollState == SCROLL_STATE_IDLE || scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                    picasso.resumeTag(TopOneActivity.this);
                } else {
                    picasso.pauseTag(TopOneActivity.this);
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

        int page;


        public goodsThread(int page) {
            this.page = page;
        }

        @Override
        public void run() {
            try {
                String url = getResources().getString(R.string.appurl) + "/goods2/top100" + "?page=" + page+"&stamp="+ UrlUtils.getTime()+"&encode="+UrlUtils.getEncode();
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
        private WeakReference<TopOneActivity> mWeakReference = null;

        MyHandler(TopOneActivity activity){
            mWeakReference = new WeakReference<TopOneActivity>(activity);
        }
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            TopOneActivity topOneActivity = mWeakReference.get();
            if(topOneActivity==null){
                return;
            }
            if(Build.VERSION.SDK_INT >= 17) {
                if (topOneActivity== null || topOneActivity.isDestroyed() || topOneActivity.isFinishing()) {
                    return;
                }
            }else {
                if (topOneActivity== null ||topOneActivity.isFinishing()) {
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
                                adapter = new MyAdapter(topOneActivity);

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
                        Toast.makeText(topOneActivity, "数据返回错误!", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(topOneActivity, "数据返回错误!", Toast.LENGTH_SHORT).show();
                if (page > 1) {
                    page--;
                }
                home_jinxuan_listView.stopLoadMore();
            }
            if (msg.what == 7) {
                MyToast.show(topOneActivity, 0,AndroidUtils.getWidth(topOneActivity),
                        AndroidUtils.dip2px(topOneActivity, 45), "您的网络不给力，请检查更新!");
                home_jinxuan_listView.stopRefresh();
            }
            if (msg.what == 8) {

                home_jinxuan_listView.stopLoadMore();

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
//                                                   cmb.setText("TopOne优惠券地址为："+data);
//                                               }
//                                               Toast.makeText(TopOneActivity.this, "优惠券地址已粘贴!", Toast.LENGTH_LONG).show();


                        // System.out.println("data优惠="+data);
                        if(NetJudgeUtils.getNetConnection(topOneActivity)){
                            if(TextUtils.isEmpty(data)){
                                AlibcTrade.show(topOneActivity, new AlibcDetailPage(StringUtils.getID(turnGoodsId)), alibcShowParams, alibcTaokeParams, exParams , new DemoTradeCallback());

                            }else{
                                AlibcTrade.show(topOneActivity, new AlibcPage(data), alibcShowParams, null, exParams, new DemoTradeCallback());
                                //AlibaPageDetails
                            }
                        }else{
                            Toast.makeText(topOneActivity, "您的网络不给力，请检查更新!", Toast.LENGTH_LONG).show();

                        }


                        // AlibcTrade.show(JuhuasuanActivity.this, new AlibcDetailPage(StringUtils.getID(turnGoodsId)), alibcShowParams, alibcTaokeParams, exParams , new DemoTradeCallback());


                    } else {
                        if(NetJudgeUtils.getNetConnection(topOneActivity)){
                            AlibcTrade.show(topOneActivity, new AlibcDetailPage(StringUtils.getID(turnGoodsId)), alibcShowParams, alibcTaokeParams, exParams , new DemoTradeCallback());

                        }else{
                            Toast.makeText(topOneActivity, "您的网络不给力，请检查更新!", Toast.LENGTH_LONG).show();

                        }

                        //  Toast.makeText(JuhuasuanActivity.this, "服务器错误!", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if(NetJudgeUtils.getNetConnection(topOneActivity)){
                        AlibcTrade.show(topOneActivity, new AlibcDetailPage(StringUtils.getID(turnGoodsId)), alibcShowParams, alibcTaokeParams, exParams , new DemoTradeCallback());
                    }else{
                        Toast.makeText(topOneActivity, "您的网络不给力，请检查更新!", Toast.LENGTH_LONG).show();

                    }
                } finally {
                    CustomProgressDialog.stopProgressDialog();
                }
            }
            if (msg.what == 12) {
                CustomProgressDialog.stopProgressDialog();
                if(NetJudgeUtils.getNetConnection(topOneActivity)){
                    AlibcTrade.show(topOneActivity, new AlibcDetailPage(StringUtils.getID(turnGoodsId)), alibcShowParams, alibcTaokeParams, exParams , new DemoTradeCallback());
                }else{
                    Toast.makeText(topOneActivity, "您的网络不给力，请检查更新!", Toast.LENGTH_LONG).show();

                }
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
        MobclickAgent.onPageStart("JuhuasuanActivity");
        MobclickAgent.onResume(this);

    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("JuhuasuanActivity");
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
            PicassoUtils.loadImageWithHolderAndError(context, goodsInfo.getGoodsImage(), R.mipmap.picture, R.mipmap.picture, holder.good_img);
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
}
