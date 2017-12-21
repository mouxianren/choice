package com.yiqi.choose.activity.shareincome;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.yiqi.choose.R;
import com.yiqi.choose.base.BaseActivity;
import com.yiqi.choose.factory.ThreadPollFactory;
import com.yiqi.choose.model.ShareIncome_tgInfo;
import com.yiqi.choose.thread.ShareComeDetailsMoreThread;
import com.yiqi.choose.thread.ShareTuiguanThread;
import com.yiqi.choose.utils.AndroidUtils;
import com.yiqi.choose.utils.MyToast;
import com.yiqi.choose.utils.NetJudgeUtils;
import com.yiqi.choose.utils.ParseJsonCommon;
import com.yiqi.choose.utils.SharedPfUtils;
import com.yiqi.choose.utils.SystemBarTintManager;
import com.yiqi.choose.view.XListView;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.yiqi.choose.R.id.tv_invite_sucess;
import static com.yiqi.choose.R.id.tv_lastmonth_mkmoney;
import static com.yiqi.choose.R.id.tv_month_mkmoney;

/**
 * Created by moumou on 17/12/11.
 */

public class ShareIncomeDetails extends BaseActivity {

    @BindView(R.id.ll_back)
    LinearLayout mLlBack;
    @BindView(R.id.listview)
    XListView mListview;

    View view;
    TextView mTvInviteSucess;
    TextView mTvYestodayMkmoeny;
    TextView mTvLastmonthMkmoney;
    TextView mTvMonthMkmoney;

    List<Object> list;
    List<Object> ziList;

        MyAdapter adapter;
    private MyHandler hd;
    int page=1;
    int maxPage;
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

        setContentView(R.layout.shareincomedetails);

        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        adapter=new MyAdapter(ShareIncomeDetails.this);
        list=new ArrayList<Object>();
        ziList=new ArrayList<Object>();
        hd=new MyHandler(ShareIncomeDetails.this);
        view = View.inflate(ShareIncomeDetails.this, R.layout.shareincome_head, null);
        mTvInviteSucess=(TextView) view.findViewById(tv_invite_sucess);
        mTvYestodayMkmoeny=(TextView) view.findViewById(R.id.tv_yestoday_mkmoeny);
        mTvLastmonthMkmoney=(TextView) view.findViewById(tv_lastmonth_mkmoney);
        mTvMonthMkmoney=(TextView) view.findViewById(tv_month_mkmoney);
        mListview.addHeaderView(view);
        mListview.setAdapter(adapter);
        mListview.setPullLoadEnable(false);
        if(NetJudgeUtils.getNetConnection(ShareIncomeDetails.this)){
            ThreadPollFactory.getNormalPool().execute(new ShareComeDetailsMoreThread(hd,ShareIncomeDetails.this,(String) SharedPfUtils.getData(ShareIncomeDetails.this, "newQuanId", "")));
            ThreadPollFactory.getNormalPool().execute(new ShareTuiguanThread(hd,ShareIncomeDetails.this,(String) SharedPfUtils.getData(ShareIncomeDetails.this, "newQuanId", ""),1));

        }
        mListview.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                if(NetJudgeUtils.getNetConnection(ShareIncomeDetails.this)){
                    ThreadPollFactory.getNormalPool().execute(new ShareComeDetailsMoreThread(hd,ShareIncomeDetails.this,(String) SharedPfUtils.getData(ShareIncomeDetails.this, "newQuanId", "")));
                    ThreadPollFactory.getNormalPool().execute(new ShareTuiguanThread(hd,ShareIncomeDetails.this,(String) SharedPfUtils.getData(ShareIncomeDetails.this, "newQuanId", ""),1));
                }else{
                    hd.sendEmptyMessage(10);
                }
            }

            @Override
            public void onLoadMore() {
                if (mListview.getProgressState()) {
                    if (NetJudgeUtils.getNetConnection(ShareIncomeDetails.this)) {
                        page++;
                        ThreadPollFactory.getNormalPool().execute(new ShareTuiguanThread(hd,ShareIncomeDetails.this,(String) SharedPfUtils.getData(ShareIncomeDetails.this, "newQuanId", ""),page));
                    } else {
                        hd.sendEmptyMessage(11);
                    }


                }
            }
        });

    }
    private class MyHandler extends Handler {
        private WeakReference<ShareIncomeDetails> mWeakReference = null;

        MyHandler(ShareIncomeDetails activity) {
            mWeakReference = new WeakReference<ShareIncomeDetails>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            ShareIncomeDetails mShareIncomeDetails = mWeakReference.get();
            if (mShareIncomeDetails == null) {
                return;
            }

            if (Build.VERSION.SDK_INT >= 17) {
                if (mShareIncomeDetails == null || mShareIncomeDetails.isDestroyed() || mShareIncomeDetails.isFinishing()) {
                    return;
                }
            } else {
                if (mShareIncomeDetails == null || mShareIncomeDetails.isFinishing()) {

                    return;
                }
            }
            if(msg.what==3) {
                try {
                    JSONObject json = new JSONObject((String) msg.obj);
                    String code = json.getString("code");
                    if (code.equals("0")) {
                        String data = json.getString("data");
                        JSONObject dataDetails = new JSONObject(data);
                        mTvLastmonthMkmoney.setText(dataDetails.getString("last"));
                        mTvInviteSucess.setText(dataDetails.getString("appcount"));
                        mTvYestodayMkmoeny.setText(dataDetails.getString("yesterday"));
                        mTvMonthMkmoney.setText(dataDetails.getString("current"));
                        adapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if(msg.what==4){

            }
            if(msg.what==5){
                try {
                    JSONObject j = new JSONObject((String) msg.obj);
                    String code = j.getString("code");
                    if (code.equals("0")) {
                        page = 1;
                        String data = j.getString("data");
                        JSONObject jData = new JSONObject(data);
                        String goods = jData.getString("records");
                        maxPage = jData.getInt("totalpage");
                        if (goods.equals("[]") || null == goods || goods.equals("null")) {

                        } else {
                            list.clear();
                            list = ParseJsonCommon.parseJsonData(goods,
                                    ShareIncome_tgInfo.class);

                            if (list.size() > 0) {
                                if (maxPage > 1) {
                                    mListview.setPullLoadEnable(true);
                                } else {
                                    mListview.setPullLoadEnable(false);
                                }
                               adapter.notifyDataSetChanged();

                                mListview.stopRefresh();
                                mListview.setRefreshTime(new Date().toLocaleString());
                            }
                        }



                    } else {
                        Toast.makeText(mShareIncomeDetails,j.getString("errorMsg"),Toast.LENGTH_LONG).show();

                    }
                } catch (Exception e) {
                    e.printStackTrace();

                }
            }
            if(msg.what==6){
                mListview.stopRefresh();
                mListview.stopLoadMore();
            }
            if(msg.what==7){

                    try {
                        JSONObject j = new JSONObject((String) msg.obj);
                        String code = j.getString("code");
                        if (code.equals("0")) {
                            String data = j.getString("data");
                            JSONObject jData = new JSONObject(data);
                            String goods = jData.getString("records");
                            maxPage = jData.getInt("totalpage");
                            ziList.clear();
                            ziList = ParseJsonCommon.parseJsonData(goods,
                                    ShareIncome_tgInfo.class);
                            list.addAll(ziList);

                            if (page >= maxPage) {
                                mListview.setPullLoadEnable(false);
                            } else {
                                mListview.setPullLoadEnable(true);
                            }
                            adapter.notifyDataSetChanged();// 告诉listView数据发生改变，要求listView更新显示
                            mListview.stopRefresh();
                            mListview.stopLoadMore();
                        } else {
                            if (page > 1) {
                                page--;
                            }
                            mListview.stopRefresh();
                            mListview.stopLoadMore();
                            Toast.makeText(mShareIncomeDetails, j.getString("errorMsg"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        mListview.stopRefresh();
                        mListview.stopLoadMore();
                    }

            }
            if(msg.what==8){
                Toast.makeText(mShareIncomeDetails, "数据返回错误!", Toast.LENGTH_SHORT).show();
                if (page > 1) {
                    page--;
                }
                mListview.stopLoadMore();
            }
            if (msg.what == 10) {
                MyToast.show(mShareIncomeDetails, 0, AndroidUtils.getWidth(mShareIncomeDetails),
                        AndroidUtils.dip2px(mShareIncomeDetails, 45), "您的网络不给力，请检查更新!");
                mListview.stopRefresh();
            }
            if (msg.what == 11) {
                mListview.stopLoadMore();

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
        MobclickAgent.onPageStart("ShareIncomeDetails");
        MobclickAgent.onResume(this);

    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("ShareIncomeDetails");
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


    @OnClick(R.id.ll_back)
    public void onViewClicked() {
        ShareIncomeDetails.this.finish();
        overridePendingTransition(R.anim.in_from_left,
                R.anim.out_of_right);
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


            return list.size();


        }

        @Override
        public Object getItem(int position) {

            return list.get(position);
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
                view = inflater.inflate(R.layout.shareincome_details_item, null);
                holder = new GoodHodler();
                holder.iv_ruzhang = (ImageView) view.findViewById(R.id.iv_ruzhang);
                holder.tv_ruzhang = (TextView) view.findViewById(R.id.tv_ruzhang);
                holder.tv_list_ruzhang_money = (TextView) view.findViewById(R.id.tv_list_ruzhang_money);
                holder.tv_list_ruzhang_time = (TextView) view.findViewById(R.id.tv_list_ruzhang_time);
            } else {
                view = convertView;
                holder = (GoodHodler) convertView.getTag();
            }
            final ShareIncome_tgInfo goodsInfo;
            goodsInfo = (ShareIncome_tgInfo) list.get(position);
            holder.iv_ruzhang.setImageResource(R.mipmap.tuiguangyugu);
            holder.tv_ruzhang.setText(goodsInfo.getInfo());
            holder.tv_list_ruzhang_money.setText("+"+goodsInfo.getMoney());
            holder.tv_list_ruzhang_time.setText(goodsInfo.getDate());



            return view;
        }

    }
    class GoodHodler {
        ImageView iv_ruzhang;
        TextView  tv_ruzhang;
        TextView tv_list_ruzhang_money;
        TextView tv_list_ruzhang_time;


    }
}
