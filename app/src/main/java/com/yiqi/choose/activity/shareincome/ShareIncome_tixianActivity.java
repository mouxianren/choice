package com.yiqi.choose.activity.shareincome;

import android.annotation.TargetApi;
import android.app.Dialog;
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
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.yiqi.choose.R;
import com.yiqi.choose.base.BaseActivity;
import com.yiqi.choose.base.BaseMap;
import com.yiqi.choose.factory.ThreadPollFactory;
import com.yiqi.choose.fragment.ShareIncomeFragment;
import com.yiqi.choose.model.ShareDetailsInfo;
import com.yiqi.choose.thread.ShareTixianThread;
import com.yiqi.choose.utils.AndroidUtils;
import com.yiqi.choose.utils.CustomProgressDialog;
import com.yiqi.choose.utils.HttpConBase;
import com.yiqi.choose.utils.MyToast;
import com.yiqi.choose.utils.NetJudgeUtils;
import com.yiqi.choose.utils.ParseJsonCommon;
import com.yiqi.choose.utils.SharedPfUtils;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.yiqi.choose.fragment.ShareIncomeFragment.alipay;

/**
 * Created by moumou on 17/12/11.
 */

public class ShareIncome_tixianActivity extends BaseActivity {
    @BindView(R.id.ll_back)
    LinearLayout mLlBack;
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.listview)
    XListView mListview;


    View view;

    private LinearLayout ll_modie_alipy;
    private TextView tv_modie_alipy;
    private TextView tv_alipy;

  MyAdapter adapter;
    private MyHandler hd;
    int page=1;
    int maxPage;

    List<Object> list;
    List<Object> ziList;
    Dialog dialog;
    private String alipaynew;
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
        view = View.inflate(ShareIncome_tixianActivity.this, R.layout.sharetixianhead, null);
        tv_modie_alipy = (TextView) view.findViewById(R.id.tv_modie_alipy);
        ll_modie_alipy = (LinearLayout) view.findViewById(R.id.ll_modie_alipy);
        tv_alipy = (TextView) view.findViewById(R.id.tv_alipy);
         hd=new MyHandler(ShareIncome_tixianActivity.this);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        mTvTitle.setText("提现");
        list=new ArrayList<Object>();
        ziList=new ArrayList<Object>();
        adapter=new MyAdapter(ShareIncome_tixianActivity.this);

        if (TextUtils.isEmpty(alipay)) {
           tv_modie_alipy.setVisibility(View.VISIBLE);
            ll_modie_alipy.setVisibility(View.GONE);
        } else {
            tv_modie_alipy.setVisibility(View.GONE);
            ll_modie_alipy.setVisibility(View.VISIBLE);
            tv_alipy.setText(alipay);
        }
        mListview.addHeaderView(view);
        mListview.setAdapter(adapter);
        mListview.setPullLoadEnable(false);
        if(NetJudgeUtils.getNetConnection(ShareIncome_tixianActivity.this)){
            ThreadPollFactory.getNormalPool().execute(new ShareTixianThread(hd,ShareIncome_tixianActivity.this,(String) SharedPfUtils.getData(ShareIncome_tixianActivity.this, "newQuanId", ""),1));

        }
        mListview.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                if(NetJudgeUtils.getNetConnection(ShareIncome_tixianActivity.this)){

                    ThreadPollFactory.getNormalPool().execute(new ShareTixianThread(hd,ShareIncome_tixianActivity.this,(String) SharedPfUtils.getData(ShareIncome_tixianActivity.this, "newQuanId", ""),1));

                }else{
                    hd.sendEmptyMessage(10);
                }
            }

            @Override
            public void onLoadMore() {
                if (mListview.getProgressState()) {
                    if (NetJudgeUtils.getNetConnection(ShareIncome_tixianActivity.this)) {
                        page++;
                        ThreadPollFactory.getNormalPool().execute(new ShareTixianThread(hd,ShareIncome_tixianActivity.this,(String) SharedPfUtils.getData(ShareIncome_tixianActivity.this, "newQuanId", ""),page));
                    } else {
                        hd.sendEmptyMessage(11);
                    }


                }
            }
        });
        ll_modie_alipy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlipayDialog();
            }
        });
        tv_modie_alipy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlipayDialog();
            }
        });
    }
    private class MyHandler extends Handler {
        private WeakReference<ShareIncome_tixianActivity> mWeakReference = null;

        MyHandler(ShareIncome_tixianActivity activity) {
            mWeakReference = new WeakReference<ShareIncome_tixianActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            ShareIncome_tixianActivity mShareIncomeDetails = mWeakReference.get();
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
                                    ShareDetailsInfo.class);
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
                                ShareDetailsInfo.class);
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
            if(msg.what==15){
                try {
                    JSONObject json = new JSONObject((String) msg.obj);
                    String code = json.getString("code");
                    if(code.equals("0")){
                        ShareIncomeFragment.alipay=alipaynew;
                        dialog.dismiss();
                        tv_alipy.setText(alipaynew);

                        tv_modie_alipy.setVisibility(View.GONE);
                        ll_modie_alipy.setVisibility(View.VISIBLE);

                    } else {
                        Toast.makeText(ShareIncome_tixianActivity.this, json.getString("errorMsg"), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    CustomProgressDialog.stopProgressDialog();

                }
            }
            if(msg.what==16){
                CustomProgressDialog.stopProgressDialog();
                Toast.makeText(ShareIncome_tixianActivity.this, "提交失败!", Toast.LENGTH_LONG).show();
            }

        }
    }
    private void showAlipayDialog() {
        dialog = new Dialog(ShareIncome_tixianActivity.this, R.style.FullHeightDialog);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        if (!dialog.isShowing()) {
            View view = LayoutInflater.from(ShareIncome_tixianActivity.this).inflate(
                    R.layout.alipaydialog, null);
            dialog.setContentView(
                    view,
                    new ViewGroup.LayoutParams(AndroidUtils.dip2px(
                            ShareIncome_tixianActivity.this, 300), LinearLayout.LayoutParams.WRAP_CONTENT));
            final EditText et_alipay=(EditText)view.findViewById(R.id.et_hint_alipay);
            et_alipay.setText(tv_alipy.getText().toString().trim());
            et_alipay.setSelection(et_alipay.length());
            final TextView tv_yes = (TextView) view.findViewById(R.id.ok);
            TextView tv_no = (TextView) view.findViewById(R.id.no);
            tv_no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            tv_yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (TextUtils.isEmpty(et_alipay.getText().toString().trim())) {
                        Toast.makeText(ShareIncome_tixianActivity.this,"支付宝不能为空",Toast.LENGTH_LONG).show();
                        return;
                    }
                    if(NetJudgeUtils.getNetConnection(ShareIncome_tixianActivity.this)){
                        CustomProgressDialog.createDialog(ShareIncome_tixianActivity.this,"正在提交中...");
                        alipaynew=et_alipay.getText().toString().trim();
                        ThreadPollFactory.getNormalPool().execute(new SendAlipay(alipaynew));



                    }else{
                        Toast.makeText(ShareIncome_tixianActivity.this, "请检查网络连接！", Toast.LENGTH_LONG).show();

                    }
                }
            });

            dialog.show();
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
        MobclickAgent.onPageStart("ShareIncome_tixianActivity");
        MobclickAgent.onResume(this);

    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("ShareIncome_tixianActivity");
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
        ShareIncome_tixianActivity.this.finish();
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
            final ShareDetailsInfo goodsInfo;
            goodsInfo = (ShareDetailsInfo) list.get(position);
            if(goodsInfo.getType().equals("1")){
                holder.iv_ruzhang.setImageResource(R.mipmap.ruzhang);
                holder.tv_ruzhang.setText("结算");
                holder.tv_list_ruzhang_money.setText("+"+goodsInfo.getMoney());
            }else{
                holder.iv_ruzhang.setImageResource(R.mipmap.tixian);
                holder.tv_ruzhang.setText("提现");
                holder.tv_list_ruzhang_money.setText("－"+goodsInfo.getMoney());
            }


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
    /**
     * 的到圈值接口
     */
    private class SendAlipay implements Runnable {
        String apliaycount;

        public SendAlipay(String apliaycount) {
            this.apliaycount = apliaycount;

        }
        @Override
        public void run() {
            try {
                Map<String, String> params = new HashMap<>();
                params.put("apliaycount", apliaycount);
                params.put("code",(String)SharedPfUtils.getData(ShareIncome_tixianActivity.this,"newQuanId",""));
                params = BaseMap.getMapAll(params);
                String url = getResources().getString(R.string.appurl) + "/activity/alipay"+"?stamp=" + UrlUtils.getTime() + "&encode=" + UrlUtils.getEncode();
                String jsonData = HttpConBase.getjsonByPost(url, params, "utf-8");
                // AndroidUtils.printlnMap(params);
                Message msg = Message.obtain();
                msg.what = 15;
                msg.obj = jsonData;
                hd.sendMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
                hd.sendEmptyMessage(16);
            }

        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        CustomProgressDialog.stopProgressDialog();
    }
}
