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
import com.squareup.picasso.Picasso;
import com.yiqi.choose.R;
import com.yiqi.choose.alibabaCallback.DemoTradeCallback;
import com.yiqi.choose.base.BaseActivity;
import com.yiqi.choose.factory.ThreadPollFactory;
import com.yiqi.choose.model.GoodsInfo;
import com.yiqi.choose.thread.TurnUrlThread;
import com.yiqi.choose.utils.AndroidUtils;
import com.yiqi.choose.utils.CustomProgressDialog;
import com.yiqi.choose.utils.HttpConBase;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by moumou on 17/9/29.
 */

public class SearchYouhuiNewAllActivity extends BaseActivity {

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
    private MyHandler hd;

MyAdapter adapter;
    //ClipboardManager cmb;

    private String turnGoodsId;
    private AlibcShowParams alibcShowParams;//页面打开方式，默认，H5，Native
    private Map<String, String> exParams;//yhhpass参数
    private AlibcTaokeParams alibcTaokeParams = null;//淘客参数，包括pid，unionid，subPid

    private String keyword;
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
        setContentView(R.layout.all_out_search);
        ll_back = (LinearLayout) this.findViewById(R.id.ll_back);
        youhui_tv_search = (LinearLayout) this.findViewById(R.id.youhui_tv_search);
        search_et_hint = (EditText) this.findViewById(R.id.search_et_hint);
        Intent intent=getIntent();
        keyword=intent.getStringExtra("keyword");
        search_et_hint.setText(keyword);

        all_search_rl_listview = (RelativeLayout) this.findViewById(R.id.all_search_rl_listview);
        all_search_home_arraw = (ImageView) this.findViewById(R.id.all_search_home_arraw);
        all_search_listview = (XListView) this.findViewById(R.id.all_search_listview);
       // cmb = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);

        all_search_listview.setPullLoadEnable(false);
        all_search_listview.setPullRefreshEnable(false);

        all_search_nogoods = (LinearLayout) this.findViewById(R.id.all_search_nogoods);

        all_search_ll_nowife = (LinearLayout) this.findViewById(R.id.all_search_ll_nowife);
        all_search_tv_retro = (TextView) this.findViewById(R.id.all_search_tv_retro);

        all_search_rl_pb = (RelativeLayout) this.findViewById(R.id.all_search_rl_pb);

        goodList = new ArrayList<Object>();

        hd=new MyHandler(SearchYouhuiNewAllActivity.this);
        alibcTaokeParams = new AlibcTaokeParams(SplashActicity1.mPId, "", "");
        alibcShowParams = new AlibcShowParams(OpenType.Native, false);
        alibcShowParams.setClientType("taobao_scheme");
        exParams = new HashMap<>();
        exParams.put("isv_code", "appisvcode");
        exParams.put("alibaba", "阿里巴巴");//自定义参数部分，可任意增删改

        ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchYouhuiNewAllActivity.this.finish();
                overridePendingTransition(R.anim.in_from_left, R.anim.out_of_right);
            }
        });
        if(NetJudgeUtils.getNetConnection(SearchYouhuiNewAllActivity.this)){
            all_search_rl_listview.setVisibility(View.GONE);
            all_search_nogoods.setVisibility(View.GONE);
            all_search_ll_nowife.setVisibility(View.GONE);
            all_search_rl_pb.setVisibility(View.VISIBLE);
            keyword=search_et_hint.getText().toString().trim();
            ThreadPollFactory.getNormalPool().execute(new goodsThread(keyword));
        }else{
            all_search_rl_listview.setVisibility(View.GONE);
            all_search_nogoods.setVisibility(View.GONE);
            all_search_ll_nowife.setVisibility(View.VISIBLE);
            all_search_rl_pb.setVisibility(View.GONE);
        }
        all_search_listview.setOverScrollMode(View.OVER_SCROLL_NEVER);
        youhui_tv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(search_et_hint.getText().toString().trim())) {
                    Toast.makeText(SearchYouhuiNewAllActivity.this, "请输入关键词!", Toast.LENGTH_LONG).show();
                    return;
                }
                AndroidUtils.hideInput(SearchYouhuiNewAllActivity.this, search_et_hint);
                if(NetJudgeUtils.getNetConnection(SearchYouhuiNewAllActivity.this)){
                    all_search_rl_listview.setVisibility(View.GONE);
                    all_search_nogoods.setVisibility(View.GONE);
                    all_search_ll_nowife.setVisibility(View.GONE);
                    all_search_rl_pb.setVisibility(View.VISIBLE);
                    ThreadPollFactory.getNormalPool().execute(new goodsThread(search_et_hint.getText().toString().trim()));
                }else{
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
                    Toast.makeText(SearchYouhuiNewAllActivity.this, "请输入关键词!", Toast.LENGTH_LONG).show();
                    return;
                }
                AndroidUtils.hideInput(SearchYouhuiNewAllActivity.this, search_et_hint);
                if(NetJudgeUtils.getNetConnection(SearchYouhuiNewAllActivity.this)){
                    all_search_rl_listview.setVisibility(View.GONE);
                    all_search_nogoods.setVisibility(View.GONE);
                    all_search_ll_nowife.setVisibility(View.GONE);
                    all_search_rl_pb.setVisibility(View.VISIBLE);
                    ThreadPollFactory.getNormalPool().execute(new goodsThread(search_et_hint.getText().toString().trim()));
                }else{
                    all_search_rl_listview.setVisibility(View.GONE);
                    all_search_nogoods.setVisibility(View.GONE);
                    all_search_ll_nowife.setVisibility(View.VISIBLE);
                    all_search_rl_pb.setVisibility(View.GONE);
                }
            }
        });

        all_search_listview.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // TODO Auto-generated method stub
                final Picasso picasso = Picasso.with(SearchYouhuiNewAllActivity.this);
                if (scrollState == SCROLL_STATE_IDLE || scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                    picasso.resumeTag(SearchYouhuiNewAllActivity.this);
                } else {
                    picasso.pauseTag(SearchYouhuiNewAllActivity.this);
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

                if(NetJudgeUtils.getNetConnection(SearchYouhuiNewAllActivity.this)){
                    CustomProgressDialog.createDialog(SearchYouhuiNewAllActivity.this,"");
                    GoodsInfo info=(GoodsInfo)goodList.get(position-1);
                    turnGoodsId=info.getNumIid();
                    ThreadPollFactory.getNormalPool().execute(new TurnUrlThread(info.getNumIid()+"",SearchYouhuiNewAllActivity.this,hd));
                    //AlibcTrade.show(JinxuanActivity.this, new AlibcPage(info.getGoodsUrl()),alibcShowParams, alibcTaokeParams, exParams , new DemoTradeCallback());

                }else{
                    Toast.makeText(SearchYouhuiNewAllActivity.this,"您的网络不给力，请检查更新!",Toast.LENGTH_LONG).show();
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


        public goodsThread(String keyword) {

            this.keyword=keyword;
        }

        @Override
        public void run() {
            try {
                String url = getResources().getString(R.string.appurl) + "/goods2/outside" + "?keyword=" + URLEncoder.encode(URLEncoder.encode(keyword,"UTF-8"),"UTF-8")+"&code="+SplashActicity1.mQuanId+"&stamp="+ UrlUtils.getTime()+"&encode="+UrlUtils.getEncode();

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
        private WeakReference<SearchYouhuiNewAllActivity> mWeakReference = null;

        MyHandler(SearchYouhuiNewAllActivity activity){
            mWeakReference = new WeakReference<SearchYouhuiNewAllActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SearchYouhuiNewAllActivity mainActivity = mWeakReference.get();
            if(mainActivity==null){
                return;
            }
            if(Build.VERSION.SDK_INT >= 17) {
                if (mainActivity == null || mainActivity.isDestroyed() || mainActivity.isFinishing()) {
                    return;
                }
            }else {
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
                        } else {
                            goodList.clear();
                            goodList = ParseJsonCommon.parseJsonData(goods,
                                    GoodsInfo.class);




                            if (goodList.size() > 0) {
                                all_search_rl_listview.setVisibility(View.VISIBLE);
                                all_search_nogoods.setVisibility(View.GONE);
                                all_search_ll_nowife.setVisibility(View.GONE);
                                all_search_rl_pb.setVisibility(View.GONE);
                                adapter = new MyAdapter(mainActivity);
                                all_search_listview.setAdapter(adapter);
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

            if (msg.what == 5) {
                all_search_rl_listview.setVisibility(View.GONE);
                all_search_nogoods.setVisibility(View.GONE);
                all_search_ll_nowife.setVisibility(View.VISIBLE);
                all_search_rl_pb.setVisibility(View.GONE);
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


                        // AlibcTrade.show(JuhuasuanActivity.this, new AlibcDetailPage(StringUtils.getID(turnGoodsId)), alibcShowParams, alibcTaokeParams, exParams , new DemoTradeCallback());


                    } else {
                        if(NetJudgeUtils.getNetConnection(mainActivity)){
                            AlibcTrade.show(mainActivity, new AlibcDetailPage(StringUtils.getID(turnGoodsId)), alibcShowParams, alibcTaokeParams, exParams , new DemoTradeCallback());

                        }else{
                            Toast.makeText(mainActivity, "您的网络不给力，请检查更新!", Toast.LENGTH_LONG).show();

                        }

                        //  Toast.makeText(JuhuasuanActivity.this, "服务器错误!", Toast.LENGTH_LONG).show();
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
            PicassoUtils.loadImageWithHolderAndError(SearchYouhuiNewAllActivity.this, goodsInfo.getGoodsImage(), R.mipmap.picture, R.mipmap.picture, holder.good_img);
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
    protected void onDestroy() {
        super.onDestroy();
        CustomProgressDialog.stopProgressDialog();
    }
}
