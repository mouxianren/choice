package com.yiqi.choose.activity;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Intent;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.yiqi.choose.R;
import com.yiqi.choose.base.BaseActivity;
import com.yiqi.choose.base.BaseMap;
import com.yiqi.choose.factory.ThreadPollFactory;
import com.yiqi.choose.model.RecordInfo;
import com.yiqi.choose.utils.AndroidUtils;
import com.yiqi.choose.utils.CustomProgressDialog;
import com.yiqi.choose.utils.HttpConBase;
import com.yiqi.choose.utils.NetJudgeUtils;
import com.yiqi.choose.utils.ParseJsonCommon;
import com.yiqi.choose.utils.SharedPfUtils;
import com.yiqi.choose.utils.SystemBarTintManager;
import com.yiqi.choose.utils.UrlUtils;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by moumou on 17/10/28.
 */

public class InviteMoneyDetailsActivity extends BaseActivity {
    private LinearLayout back_btn;
    private ScrollView sv_invite;
    private TextView tv_all_money;

    private TextView tv_share;
    private TextView tv_modie_alipy;

    private LinearLayout ll_modie_alipy;
    private TextView tv_alipy;
    private TextView tv_invite_sucess;

    private TextView tv_yestoday_mkmoney;
    private TextView tv_lastmonth_mkmoney;

    private TextView tv_month_mkmoney;

    private LinearLayout ll_list;
    private LinearLayout ll_list_ruzhang;
    private TextView tv_list_ruzhang_money;
    private TextView tv_list_ruzhang_time;
    private ImageView iv_ruzhang;
    private TextView tv_ruzhang;

    private LinearLayout ll_list_chuzhang;
    private TextView tv_list_chuzhang_money;
    private TextView tv_list_chuzhang_time;
    private ImageView iv_tixian;
    private TextView tv_tixian;

    private MyHandler hd;

    private String alipay;
    private Dialog dialog;

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
        setContentView(R.layout.invite_money_details);


        sv_invite = (ScrollView) this.findViewById(R.id.sv_invite);

        tv_share = (TextView) this.findViewById(R.id.tv_share);
        tv_modie_alipy = (TextView) this.findViewById(R.id.tv_modie_alipy);
        ll_modie_alipy = (LinearLayout) this.findViewById(R.id.ll_modie_alipy);
        tv_alipy = (TextView) this.findViewById(R.id.tv_alipy);
        tv_yestoday_mkmoney = (TextView) this.findViewById(R.id.tv_yestoday_mkmoeny);
        tv_lastmonth_mkmoney = (TextView) this.findViewById(R.id.tv_lastmonth_mkmoney);
        tv_month_mkmoney = (TextView) this.findViewById(R.id.tv_month_mkmoney);
        ll_list = (LinearLayout) this.findViewById(R.id.ll_list);
        tv_list_ruzhang_money = (TextView) this.findViewById(R.id.tv_list_ruzhang_money);
        tv_list_ruzhang_time = (TextView) this.findViewById(R.id.tv_list_ruzhang_time);
        ll_list_ruzhang = (LinearLayout) this.findViewById(R.id.ll_list_ruzhang);
        tv_invite_sucess=(TextView)this.findViewById(R.id.tv_invite_sucess);
iv_ruzhang=(ImageView)this.findViewById(R.id.iv_ruzhang);
        tv_ruzhang=(TextView)this.findViewById(R.id.tv_ruzhang);
        iv_tixian=(ImageView)this.findViewById(R.id.iv_tixian);
        tv_tixian=(TextView)this.findViewById(R.id.tv_tixian);

        tv_list_chuzhang_money = (TextView) this.findViewById(R.id.tv_list_chuzhang_money);
        tv_list_chuzhang_time = (TextView) this.findViewById(R.id.tv_list_chuzhang_time);
        ll_list_chuzhang = (LinearLayout) this.findViewById(R.id.ll_list_chuzhang);

        hd = new MyHandler(InviteMoneyDetailsActivity.this);

        sv_invite.setOverScrollMode(View.OVER_SCROLL_NEVER);
        tv_all_money = (TextView) this.findViewById(R.id.tv_all_money);
        back_btn = (LinearLayout) this.findViewById(R.id.ll_back);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InviteMoneyDetailsActivity.this.finish();
                overridePendingTransition(R.anim.in_from_left,
                        R.anim.out_of_right);
            }
        });
        tv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InviteMoneyDetailsActivity.this,
                        ShareActivity.class);
                intent.putExtra("typeShare", "2");
                startActivity(intent);
                overridePendingTransition(
                        R.anim.to_right, R.anim.to_left);
            }
        });
        if(NetJudgeUtils.getNetConnection(InviteMoneyDetailsActivity.this)){
            ThreadPollFactory.getNormalPool().execute(new getIncome((String)SharedPfUtils.getData(InviteMoneyDetailsActivity.this,"newQuanId","")));

        }else{
            Toast.makeText(InviteMoneyDetailsActivity.this, "请检查网络连接！", Toast.LENGTH_LONG).show();

        }
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
        MobclickAgent.onPageStart("InviteMoneyDetailsActivity");
        MobclickAgent.onResume(this);

    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("InviteMoneyDetailsActivity");
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

    /**
     * 的到圈值接口
     */
    private class getIncome implements Runnable {
        String newCode;


        public getIncome(String newCode) {
            this.newCode = newCode;

        }

        @Override
        public void run() {
            try {
                Map<String, String> params = new HashMap<>();
                params.put("code", newCode);
                params = BaseMap.getMapAll(params);
                String url = getResources().getString(R.string.appurl) + "/activity/income" + "?stamp=" + UrlUtils.getTime() + "&encode=" + UrlUtils.getEncode();
                String jsonData = HttpConBase.getjsonByPost(url, params, "utf-8");
                // AndroidUtils.printlnMap(params);
                Message msg = Message.obtain();
                msg.what = 3;
                msg.obj = jsonData;
                hd.sendMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
                hd.sendEmptyMessage(4);
            }

        }
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
                params.put("code",(String)SharedPfUtils.getData(InviteMoneyDetailsActivity.this,"newQuanId",""));
                params = BaseMap.getMapAll(params);
                   String url = getResources().getString(R.string.appurl) + "/activity/alipay"+"?stamp=" + UrlUtils.getTime() + "&encode=" + UrlUtils.getEncode();
                String jsonData = HttpConBase.getjsonByPost(url, params, "utf-8");
                // AndroidUtils.printlnMap(params);
                Message msg = Message.obtain();
                msg.what = 5;
                msg.obj = jsonData;
                hd.sendMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
                hd.sendEmptyMessage(6);
            }

        }
    }

    private class MyHandler extends Handler {
        private WeakReference<InviteMoneyDetailsActivity> mWeakReference = null;

        MyHandler(InviteMoneyDetailsActivity activity) {
            mWeakReference = new WeakReference<InviteMoneyDetailsActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            InviteMoneyDetailsActivity inviteMoneyDetailsActivity = mWeakReference.get();
            if (inviteMoneyDetailsActivity == null) {
                return;
            }
            if (Build.VERSION.SDK_INT >= 17) {
                if (inviteMoneyDetailsActivity == null || inviteMoneyDetailsActivity.isDestroyed() || inviteMoneyDetailsActivity.isFinishing()) {
                    return;
                }
            } else {
                if (inviteMoneyDetailsActivity == null || inviteMoneyDetailsActivity.isFinishing()) {
                    return;
                }
            }


            if (msg.what == 3) {
                //活动太火爆，请稍后再申请
                try {
                    JSONObject json = new JSONObject((String) msg.obj);
                    String code = json.getString("code");
                    if (code.equals("0")) {
                        String data = json.getString("data");
                        JSONObject dataDetails = new JSONObject(data);
                        tv_all_money.setText(dataDetails.getString("jiesuan"));
                        tv_invite_sucess.setText(dataDetails.getString("appcount"));
                        tv_yestoday_mkmoney.setText(dataDetails.getString("yesterday"));
                        tv_lastmonth_mkmoney.setText(dataDetails.getString("last"));
                        tv_month_mkmoney.setText(dataDetails.getString("current"));
                        alipay=dataDetails.getString("alipay");
                        if(TextUtils.isEmpty(alipay)){
                            ll_modie_alipy.setVisibility(View.GONE);
                            tv_modie_alipy.setVisibility(View.VISIBLE);

                        }else{
                            tv_alipy.setText(alipay);
                            ll_modie_alipy.setVisibility(View.VISIBLE);
                            tv_modie_alipy.setVisibility(View.GONE);
                        }
                        String record = dataDetails.getString("records");
                        if (record.equals("[]")) {
                            ll_list.setVisibility(View.GONE);
                        } else {
                            List<Object> lists = new ArrayList<Object>();
                            lists = ParseJsonCommon.parseJsonData(record, RecordInfo.class);

                            if (lists.size() > 0) {
                                ll_list.setVisibility(View.VISIBLE);
                                if (lists.size() == 1) {
                                    RecordInfo info = (RecordInfo) lists.get(0);
                                    if (info.getType().equals("1")) {
                                        ll_list_ruzhang.setVisibility(View.VISIBLE);
                                        ll_list_chuzhang.setVisibility(View.GONE);
                                        tv_list_ruzhang_time.setText(info.getDate());
                                        tv_list_ruzhang_money.setText("+" + info.getMoney());

                                    } else {
                                        ll_list_ruzhang.setVisibility(View.GONE);
                                        ll_list_chuzhang.setVisibility(View.VISIBLE);
                                        tv_list_chuzhang_time.setText(info.getDate());
                                        tv_list_chuzhang_money.setText("-" + info.getMoney());
                                    }
                                }
                                if (lists.size() > 1) {
                                    ll_list_ruzhang.setVisibility(View.VISIBLE);
                                    ll_list_chuzhang.setVisibility(View.VISIBLE);
                                    RecordInfo info = (RecordInfo) lists.get(0);
                                    if (info.getType().equals("1")) {
                                        tv_list_ruzhang_time.setText(info.getDate());
                                        tv_list_ruzhang_money.setText("+" + info.getMoney());
                                        tv_ruzhang.setText("入账");
                                        iv_ruzhang.setImageResource(R.mipmap.ruzhang);

                                    } else {
                                        tv_list_ruzhang_time.setText(info.getDate());
                                        tv_list_ruzhang_money.setText("-" + info.getMoney());
                                        tv_ruzhang.setText("提现");
                                        iv_ruzhang.setImageResource(R.mipmap.tixian);
                                    }
                                    RecordInfo info1 = (RecordInfo) lists.get(1);
                                    if (info1.getType().equals("1")) {
                                        tv_list_chuzhang_time.setText(info1.getDate());
                                        tv_list_chuzhang_money.setText("+" + info1.getMoney());
                                        tv_tixian.setText("入账");
                                        iv_tixian.setImageResource(R.mipmap.ruzhang);
                                    } else {
                                        tv_list_chuzhang_time.setText(info1.getDate());
                                        tv_list_chuzhang_money.setText("-" + info1.getMoney());
                                        tv_tixian.setText("提现");
                                        iv_tixian.setImageResource(R.mipmap.tixian);

                                    }

                                }

                            }
                        }

                        //跳到详情页

                    } else {
                        Toast.makeText(InviteMoneyDetailsActivity.this, json.getString("errorMsg"), Toast.LENGTH_LONG).show();
                    }
                    //                    else if(code.equals("1")){
                    //                        Toast.makeText(PhoneActivity.this,"验证码错误!",Toast.LENGTH_LONG).show();
                    //
                    //                    }else if(code.equals("2")){
                    //                        Toast.makeText(PhoneActivity.this,"活动太火爆，请稍后再申请!",Toast.LENGTH_LONG).show();
                    //                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (msg.what == 4) {
                Toast.makeText(InviteMoneyDetailsActivity.this, "服务器错误!", Toast.LENGTH_LONG).show();

            }
            if(msg.what==5){
                try {
                    JSONObject json = new JSONObject((String) msg.obj);
                    String code = json.getString("code");
                    if(code.equals("0")){
                        alipay=alipaynew;
                        dialog.dismiss();
                        tv_alipy.setText(alipaynew);
                        tv_modie_alipy.setVisibility(View.GONE);
                        ll_modie_alipy.setVisibility(View.VISIBLE);

                    } else {
                        Toast.makeText(InviteMoneyDetailsActivity.this, json.getString("errorMsg"), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    CustomProgressDialog.stopProgressDialog();

                }
            }
            if(msg.what==6){
                CustomProgressDialog.stopProgressDialog();
                Toast.makeText(InviteMoneyDetailsActivity.this, "提交失败!", Toast.LENGTH_LONG).show();
            }


        }

    }
    private void showAlipayDialog() {
        dialog = new Dialog(InviteMoneyDetailsActivity.this, R.style.FullHeightDialog);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        if (!dialog.isShowing()) {
            View view = LayoutInflater.from(InviteMoneyDetailsActivity.this).inflate(
                    R.layout.alipaydialog, null);
            dialog.setContentView(
                    view,
                    new ViewGroup.LayoutParams(AndroidUtils.dip2px(
                            InviteMoneyDetailsActivity.this, 300), LinearLayout.LayoutParams.WRAP_CONTENT));
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
                        Toast.makeText(InviteMoneyDetailsActivity.this,"支付宝不能为空",Toast.LENGTH_LONG).show();
                        return;
                    }
                    if(NetJudgeUtils.getNetConnection(InviteMoneyDetailsActivity.this)){
                        CustomProgressDialog.createDialog(InviteMoneyDetailsActivity.this,"正在提交中...");
                        alipaynew=et_alipay.getText().toString().trim();
                        ThreadPollFactory.getNormalPool().execute(new SendAlipay(alipaynew));



                    }else{
                        Toast.makeText(InviteMoneyDetailsActivity.this, "请检查网络连接！", Toast.LENGTH_LONG).show();

                    }
                }
            });

            dialog.show();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CustomProgressDialog.stopProgressDialog();
    }
}
