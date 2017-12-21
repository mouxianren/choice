package com.yiqi.choose.activity.zitypelist;

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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.yiqi.choose.R.id.all_search_home_arraw;
import static com.yiqi.choose.R.id.all_search_iv_price;
import static com.yiqi.choose.R.id.all_search_listview;
import static com.yiqi.choose.R.id.all_search_ll_nowife;
import static com.yiqi.choose.R.id.all_search_nogoods;
import static com.yiqi.choose.R.id.all_search_rl_listview;
import static com.yiqi.choose.R.id.all_search_tv_price;
import static com.yiqi.choose.R.id.all_search_tv_quanbu;
import static com.yiqi.choose.R.id.all_search_tv_seller;
import static com.yiqi.choose.R.id.cp_image;
import static com.yiqi.choose.R.id.iv_guanbi;
import static com.yiqi.choose.R.id.search_et_hint;

/**
 * Created by moumou on 17/12/15.
 */

public class ZitypeActivity extends BaseActivity {
    @BindView(R.id.ll_back)
    LinearLayout mLlBack;
    @BindView(search_et_hint)
    EditText mSearchEtHint;
    @BindView(iv_guanbi)
    ImageView mIvGuanbi;
    @BindView(R.id.youhui_tv_search)
    LinearLayout mYouhuiTvSearch;

    @BindView(all_search_tv_quanbu)
    TextView mAllSearchTvQuanbu;
    @BindView(R.id.all_search_ll_quanbu)
    LinearLayout mAllSearchLlQuanbu;
    @BindView(all_search_tv_seller)
    TextView mAllSearchTvSeller;
    @BindView(R.id.all_search_ll_seller)
    LinearLayout mAllSearchLlSeller;
    @BindView(all_search_tv_price)
    TextView mAllSearchTvPrice;
    @BindView(all_search_iv_price)
    ImageView mAllSearchIvPrice;
    @BindView(R.id.all_search_ll_price)
    LinearLayout mAllSearchLlPrice;
    @BindView(all_search_listview)
    XListView mAllSearchListview;
    @BindView(all_search_home_arraw)
    ImageView mAllSearchHomeArraw;
    @BindView(all_search_rl_listview)
    RelativeLayout mAllSearchRlListview;
    @BindView(R.id.search_tv_empty)
    TextView mSearchTvEmpty;
    @BindView(all_search_nogoods)
    LinearLayout mAllSearchNogoods;
    @BindView(R.id.all_search_tv_retro)
    TextView mAllSearchTvRetro;
    @BindView(all_search_ll_nowife)
    LinearLayout mAllSearchLlNowife;
    @BindView(cp_image)
    CircularProgressView mCpImage;
    @BindView(R.id.cp_rl_image)
    RelativeLayout mCpRlImage;
    private int adId;
    private String name;
    private int order;
    private AlibcShowParams alibcShowParams;//页面打开方式，默认，H5，Native
    private Map<String, String> exParams;//yhhpass参数
    private AlibcTaokeParams alibcTaokeParams = null;//淘客参数，包括pid，unionid，subPid
    //ClipboardManager cmb;
    private int srceenWidth=0;
    private  LinearLayout.LayoutParams params;
    private String turnGoodsId;

    private MyHandler hd;

    int page = 1;
    int maxPage = 0;

    private List<Object> goodList;
    private List<Object> ziList;
    MyAdapter adapter;
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
        setContentView(R.layout.home_zitype);
        ButterKnife.bind(this);
        Intent intent=getIntent();
        adId=intent.getIntExtra("adId",0);
        name=intent.getStringExtra("name");
        mSearchEtHint.setText(name);
        mSearchEtHint.setSelection(name.length());
        if (SplashActicity1.sdCardWritePermisson) {
            dao = HistoryDao.getInstance(ZitypeActivity.this);
        }
        order=0;
        page = 1;
        srceenWidth=AndroidUtils.getWidth(ZitypeActivity.this);

        params=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params.width=(int)(srceenWidth*0.361);
        params.height=(int)(srceenWidth*0.361);
        mIvGuanbi.setVisibility(View.GONE);
        alibcTaokeParams = new AlibcTaokeParams(SplashActicity1.mPId, "", "");
        alibcShowParams = new AlibcShowParams(OpenType.Native, false);
        alibcShowParams.setClientType("taobao_scheme");
        exParams = new HashMap<>();
        exParams.put("isv_code", "appisvcode");
        exParams.put("alibaba", "阿里巴巴");//自定义参数部分，可任意增删改
        hd = new MyHandler(ZitypeActivity.this);
        goodList = new ArrayList<Object>();
        ziList = new ArrayList<Object>();
        mSearchEtHint.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    mIvGuanbi.setVisibility(View.VISIBLE);

                } else {
                    mIvGuanbi.setVisibility(View.GONE);
                }
            }
        });
        mAllSearchListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (NetJudgeUtils.getNetConnection(ZitypeActivity.this)) {
                    CustomProgressDialog.createDialog(ZitypeActivity.this, "");
                    GoodsInfo info = (GoodsInfo) goodList.get(position - 1);
                    turnGoodsId = info.getNumIid();
                    ThreadPollFactory.getNormalPool().execute(new TurnUrlThread(info.getNumIid() + "", ZitypeActivity.this, hd));
                    //AlibcTrade.show(JinxuanActivity.this, new AlibcPage(info.getGoodsUrl()),alibcShowParams, alibcTaokeParams, exParams , new DemoTradeCallback());
                } else {
                    Toast.makeText(ZitypeActivity.this, "您的网络不给力，请检查更新!", Toast.LENGTH_LONG).show();
                }
            }
        });
        mAllSearchListview.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // TODO Auto-generated method stub
                final Picasso picasso = Picasso.with(ZitypeActivity.this);
                if (scrollState == SCROLL_STATE_IDLE || scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                    picasso.resumeTag(ZitypeActivity.this);
                } else {
                    picasso.pauseTag(ZitypeActivity.this);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

                if (firstVisibleItem > 22) {
                    mAllSearchHomeArraw.setVisibility(View.VISIBLE);
                } else {
                    mAllSearchHomeArraw.setVisibility(View.GONE);
                }
            }
        });
        //cmb = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        if(NetJudgeUtils.getNetConnection(ZitypeActivity.this)){
            mAllSearchRlListview.setVisibility(View.GONE);
            mAllSearchNogoods.setVisibility(View.GONE);
            mAllSearchLlNowife.setVisibility(View.GONE);
            mCpRlImage.setVisibility(View.VISIBLE);
            mCpImage.innerStart();
           // (String code,String keyword,int type,int order,int page)
            name=mSearchEtHint.getText().toString().trim();
            ThreadPollFactory.getNormalPool().execute(new goodsThread(SplashActicity1.mQuanId,name,adId,order,1));
        }else{
            mAllSearchRlListview.setVisibility(View.GONE);
            mAllSearchNogoods.setVisibility(View.GONE);
            mAllSearchLlNowife.setVisibility(View.VISIBLE);
            mCpRlImage.setVisibility(View.GONE);
            mCpImage.innerStop();
        }
        mAllSearchListview.setXListViewListener(new XListView.IXListViewListener() {
            public void onRefresh() {

                    if (NetJudgeUtils.getNetConnection(ZitypeActivity.this)) {
                       name=mSearchEtHint.getText().toString().trim();
                        ThreadPollFactory.getNormalPool().execute(new goodsThread(SplashActicity1.mQuanId,name,adId,order,1));
                    } else {
                        hd.sendEmptyMessage(7);
                    }


            }

            public void onLoadMore() {
                if (mAllSearchListview.getProgressState()) {

                        if (NetJudgeUtils.getNetConnection(ZitypeActivity.this)) {
                            page++;
                            ThreadPollFactory.getNormalPool().execute(new goodsThread(SplashActicity1.mQuanId,name,adId,order,page));
                        } else {
                            hd.sendEmptyMessage(8);
                        }



                }

                //

            }
        });
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("ZitypeActivity");
        MobclickAgent.onResume(this);

    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("ZitypeActivity");
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


    @OnClick({R.id.ll_back,R.id.search_et_hint,R.id.iv_guanbi,R.id.all_search_home_arraw,R.id.youhui_tv_search, R.id.all_search_ll_quanbu, R.id.all_search_ll_seller, R.id.all_search_ll_price, R.id.all_search_tv_retro})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_back:
                this.finish();
                overridePendingTransition(R.anim.in_from_left, R.anim.out_of_right);
                break;
            case R.id.search_et_hint:
                if(BooleanThread.toService){
                    if (NetJudgeUtils.getNetConnection(ZitypeActivity.this)){
                        ThreadPollFactory.getNormalPool().execute(new StatisticsThread(ZitypeActivity.this,6));
                    }
                }
                break;
            case R.id.iv_guanbi:
                mSearchEtHint.setText("");
                break;
            case R.id.all_search_home_arraw:
               mAllSearchListview.setSelection(0);
                break;
            case R.id.youhui_tv_search:
                if (TextUtils.isEmpty(mSearchEtHint.getText().toString().trim())) {
                    Toast.makeText(ZitypeActivity.this, "请输入关键词!", Toast.LENGTH_LONG).show();
                    return;
                }
                if (SplashActicity1.sdCardWritePermisson) {
                    if (dao.queryGoods(mSearchEtHint.getText().toString().trim()) <= 0) {
                        dao.saveSS(mSearchEtHint.getText().toString().trim());
                    } else {
                        dao.deteCI(mSearchEtHint.getText().toString().trim());
                        dao.saveSS(mSearchEtHint.getText().toString().trim());
                    }
                }

                AndroidUtils.hideInput(ZitypeActivity.this, mSearchEtHint);
                if(NetJudgeUtils.getNetConnection(ZitypeActivity.this)){
                    mAllSearchRlListview.setVisibility(View.GONE);
                    mAllSearchNogoods.setVisibility(View.GONE);
                    mAllSearchLlNowife.setVisibility(View.GONE);
                    mCpRlImage.setVisibility(View.VISIBLE);
                    mCpImage.innerStart();
                    name=mSearchEtHint.getText().toString().trim();
                    ThreadPollFactory.getNormalPool().execute(new goodsThread(SplashActicity1.mQuanId,name,adId,order,1));

                }else{
                    mAllSearchRlListview.setVisibility(View.GONE);
                    mAllSearchNogoods.setVisibility(View.GONE);
                    mCpRlImage.setVisibility(View.GONE);
                    mAllSearchLlNowife.setVisibility(View.VISIBLE);
                    mCpImage.innerStop();
                }
                break;
            case R.id.all_search_ll_quanbu:
                AndroidUtils.hideInput(ZitypeActivity.this, mSearchEtHint);

                order=0;
                mAllSearchTvQuanbu.setTextColor(getResources().getColor(R.color.red));
                mAllSearchTvSeller.setTextColor(getResources().getColor(R.color.newzigrey));
                mAllSearchTvPrice.setTextColor(getResources().getColor(R.color.newzigrey));
                mAllSearchIvPrice.setImageDrawable(getResources().getDrawable(R.mipmap.price_nor));
                //zhixing xincheng
                if (NetJudgeUtils.getNetConnection(ZitypeActivity.this)) {
                    //执行下一个程序
                    mAllSearchRlListview.setVisibility(View.GONE);
                    mAllSearchNogoods.setVisibility(View.GONE);
                    mAllSearchLlNowife.setVisibility(View.GONE);
                    mCpRlImage.setVisibility(View.VISIBLE);
                    mCpImage.innerStart();
                    name=mSearchEtHint.getText().toString().trim();
                    ThreadPollFactory.getNormalPool().execute(new goodsThread(SplashActicity1.mQuanId,name,adId,order,1));
                } else {
                    mAllSearchRlListview.setVisibility(View.GONE);
                    mAllSearchNogoods.setVisibility(View.GONE);
                    mCpRlImage.setVisibility(View.GONE);
                    mAllSearchLlNowife.setVisibility(View.VISIBLE);
                    mCpImage.innerStop();
                }

                break;
            case R.id.all_search_ll_seller:
                AndroidUtils.hideInput(ZitypeActivity.this, mSearchEtHint);

                order=1;
                mAllSearchTvSeller.setTextColor(getResources().getColor(R.color.red));
                mAllSearchTvQuanbu.setTextColor(getResources().getColor(R.color.newzigrey));
                mAllSearchTvPrice.setTextColor(getResources().getColor(R.color.newzigrey));
                mAllSearchIvPrice.setImageDrawable(getResources().getDrawable(R.mipmap.price_nor));
                //zhixing xincheng
                if (NetJudgeUtils.getNetConnection(ZitypeActivity.this)) {
                    //执行下一个程序
                    mAllSearchRlListview.setVisibility(View.GONE);
                    mAllSearchNogoods.setVisibility(View.GONE);
                    mAllSearchLlNowife.setVisibility(View.GONE);
                    mCpRlImage.setVisibility(View.VISIBLE);
                    mCpImage.innerStart();
                    name=mSearchEtHint.getText().toString().trim();
                    ThreadPollFactory.getNormalPool().execute(new goodsThread(SplashActicity1.mQuanId,name,adId,order,1));
                } else {
                    mAllSearchRlListview.setVisibility(View.GONE);
                    mAllSearchNogoods.setVisibility(View.GONE);
                    mCpRlImage.setVisibility(View.GONE);
                    mAllSearchLlNowife.setVisibility(View.VISIBLE);
                    mCpImage.innerStop();
                }
                break;
            case R.id.all_search_ll_price:
                AndroidUtils.hideInput(ZitypeActivity.this, mSearchEtHint);
                if (order != 3) {
                    order = 3;
                    mAllSearchTvQuanbu.setTextColor(getResources().getColor(R.color.newzigrey));
                    mAllSearchTvPrice.setTextColor(getResources().getColor(R.color.red));
                    mAllSearchTvSeller.setTextColor(getResources().getColor(R.color.newzigrey));
                    mAllSearchIvPrice.setImageDrawable(getResources().getDrawable(R.mipmap.price_up));
                } else {
                    order = 2;
                    mAllSearchTvQuanbu.setTextColor(getResources().getColor(R.color.newzigrey));
                    mAllSearchTvPrice.setTextColor(getResources().getColor(R.color.red));
                    mAllSearchTvSeller.setTextColor(getResources().getColor(R.color.newzigrey));
                    mAllSearchIvPrice.setImageDrawable(getResources().getDrawable(R.mipmap.price_down));
                }
                if (NetJudgeUtils.getNetConnection(ZitypeActivity.this)) {
                    mAllSearchRlListview.setVisibility(View.GONE);
                    mAllSearchNogoods.setVisibility(View.GONE);
                    mAllSearchLlNowife.setVisibility(View.GONE);
                    mCpRlImage.setVisibility(View.VISIBLE);
                    mCpImage.innerStart();
                    name=mSearchEtHint.getText().toString().trim();
                    ThreadPollFactory.getNormalPool().execute(new goodsThread(SplashActicity1.mQuanId,name,adId,order,1));
                } else {
                    mAllSearchRlListview.setVisibility(View.GONE);
                    mAllSearchNogoods.setVisibility(View.GONE);
                    mAllSearchLlNowife.setVisibility(View.VISIBLE);
                    mCpRlImage.setVisibility(View.GONE);
                    mCpImage.innerStop();
                }
                break;
            case R.id.all_search_tv_retro:
                AndroidUtils.hideInput(ZitypeActivity.this,mSearchEtHint);
                if (NetJudgeUtils.getNetConnection(ZitypeActivity.this)) {
                    mAllSearchRlListview.setVisibility(View.GONE);
                    mAllSearchNogoods.setVisibility(View.GONE);
                    mAllSearchLlNowife.setVisibility(View.GONE);
                    mCpRlImage.setVisibility(View.VISIBLE);
                    mCpImage.innerStart();
                    name=mSearchEtHint.getText().toString().trim();
                    ThreadPollFactory.getNormalPool().execute(new goodsThread(SplashActicity1.mQuanId,name,adId,order,1));
                } else {
                    mAllSearchRlListview.setVisibility(View.GONE);
                    mAllSearchNogoods.setVisibility(View.GONE);
                    mAllSearchLlNowife.setVisibility(View.VISIBLE);
                    mCpRlImage.setVisibility(View.GONE);
                    mCpImage.innerStop();
                }
                break;
        }
    }
    /**
     * 的到圈值接口
     */
    private class goodsThread implements Runnable {

        int page;
        String keyword;
        int order;
        String code;
        int adId;


        public goodsThread(String code,String keyword,int adId,int order,int page) {
            this.code=code;
            this.keyword = keyword;
            this.order=order;
            this.page = page;
            this.adId=adId;
        }

        @Override
        public void run() {
            try {
                //                String aa=URLEncoder.encode(keyword,"UTF-8");
                //                System.out.println("keyword==="+aa);
                //                System.out.println("aa="+ URLDecoder.decode(aa,"UTF-8"));

                String url = getResources().getString(R.string.appurltest) + "/goods/goodscategory" + "?page=" + page + "&keyword=" + URLEncoder.encode(URLEncoder.encode(keyword,"UTF-8"),"UTF-8") + "&adId=" +adId+"&timeStemp=" +""+ "&code=" +code+ "&order=" + order + "&stamp=" + UrlUtils.getTime() + "&encode=" + UrlUtils.getEncode();
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
        private WeakReference<ZitypeActivity> mWeakReference = null;

        MyHandler(ZitypeActivity activity) {
            mWeakReference = new WeakReference<ZitypeActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ZitypeActivity mainActivity = mWeakReference.get();
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
                            mAllSearchRlListview.setVisibility(View.GONE);
                            mAllSearchNogoods.setVisibility(View.VISIBLE);
                            mAllSearchLlNowife.setVisibility(View.GONE);
                            mCpRlImage.setVisibility(View.GONE);
                            mCpImage.innerStop();
                        } else {
                            goodList.clear();
                            goodList = ParseJsonCommon.parseJsonData(goods,
                                    GoodsInfo.class);

                            if (goodList.size() > 0) {
                                mAllSearchRlListview.setVisibility(View.VISIBLE);
                                mAllSearchNogoods.setVisibility(View.GONE);
                                mAllSearchLlNowife.setVisibility(View.GONE);
                                mCpRlImage.setVisibility(View.GONE);
                                mCpImage.innerStop();

                                if (maxPage > 1) {
                                    mAllSearchListview.setPullLoadEnable(true);
                                } else {
                                    mAllSearchListview.setPullLoadEnable(false);
                                }
                                adapter = new MyAdapter(mainActivity);
                                mAllSearchListview.setAdapter(adapter);
                                mAllSearchListview.stopRefresh();
                                mAllSearchListview.setRefreshTime(new Date().toLocaleString());
                            } else {
                                mAllSearchRlListview.setVisibility(View.GONE);
                                mAllSearchNogoods.setVisibility(View.VISIBLE);
                                mAllSearchLlNowife.setVisibility(View.GONE);
                                mCpRlImage.setVisibility(View.GONE);
                                mCpImage.innerStop();
                            }
                        }


                    } else {
                        mAllSearchRlListview.setVisibility(View.GONE);
                        mAllSearchNogoods.setVisibility(View.GONE);
                        mAllSearchLlNowife.setVisibility(View.VISIBLE);
                        mCpRlImage.setVisibility(View.GONE);
                        mCpImage.innerStop();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    mAllSearchRlListview.setVisibility(View.GONE);
                    mAllSearchNogoods.setVisibility(View.GONE);
                    mAllSearchLlNowife.setVisibility(View.VISIBLE);
                    mCpRlImage.setVisibility(View.GONE);
                    mCpImage.innerStop();
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
                            mAllSearchListview.setPullLoadEnable(false);
                        } else {
                            mAllSearchListview.setPullLoadEnable(true);
                        }
                        adapter.notifyDataSetChanged();// 告诉listView数据发生改变，要求listView更新显示
                        mAllSearchListview.stopRefresh();
                        mAllSearchListview.stopLoadMore();
                    } else {
                        if (page > 1) {
                            page--;
                        }
                        mAllSearchListview.stopRefresh();
                        mAllSearchListview.stopLoadMore();
                        Toast.makeText(mainActivity, "数据返回错误!", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    mAllSearchListview.stopRefresh();
                    mAllSearchListview.stopLoadMore();
                }
            }
            if (msg.what == 5) {
                mAllSearchListview.stopRefresh();
                mAllSearchListview.stopLoadMore();
                mAllSearchRlListview.setVisibility(View.GONE);
                mAllSearchNogoods.setVisibility(View.GONE);
                mAllSearchLlNowife.setVisibility(View.VISIBLE);
                mCpRlImage.setVisibility(View.GONE);
                mCpImage.innerStop();
            }
            if (msg.what == 6) {
                Toast.makeText(mainActivity, "数据返回错误!", Toast.LENGTH_SHORT).show();
                if (page > 1) {
                    page--;
                }
                mAllSearchListview.stopLoadMore();
            }
            if (msg.what == 7) {
                MyToast.show(mainActivity, 0, AndroidUtils.getWidth(mainActivity),
                        AndroidUtils.dip2px(mainActivity, 45), "您的网络不给力，请检查更新!");
                mAllSearchListview.stopRefresh();
            }
            if (msg.what == 8) {

                mAllSearchListview.stopLoadMore();

            }
            if (msg.what == 9) {
                MyToast.show(mainActivity, 0, AndroidUtils.getWidth(mainActivity),
                        AndroidUtils.dip2px(mainActivity, 45), "关键词不能为空！");
                mAllSearchListview.stopRefresh();
            }
            if (msg.what == 10) {
                Toast.makeText(mainActivity, "关键词不能为空!", Toast.LENGTH_LONG).show();
                mAllSearchListview.stopRefresh();

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
            PicassoUtils.loadImageWithHolderAndError(ZitypeActivity.this, goodsInfo.getGoodsImage(), R.mipmap.picture, R.mipmap.picture, holder.good_img);
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
