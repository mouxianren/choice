package com.yiqi.choose.activity;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.baichuan.trade.biz.login.AlibcLogin;
import com.umeng.analytics.MobclickAgent;
import com.yiqi.choose.R;
import com.yiqi.choose.base.BaseActivity;
import com.yiqi.choose.base.BaseMap;
import com.yiqi.choose.factory.ThreadPollFactory;
import com.yiqi.choose.utils.HttpConBase;
import com.yiqi.choose.utils.JudgeLoginTaobao;
import com.yiqi.choose.utils.NetJudgeUtils;
import com.yiqi.choose.utils.SystemBarTintManager;
import com.yiqi.choose.utils.UrlUtils;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import static com.yiqi.choose.activity.SplashActicity1.mQuanId;

/**
 * Created by moumou on 17/9/26.
 */

public class WodeFankuiActivity extends BaseActivity{
    private LinearLayout back_btn;
    private EditText wo_hint_content;
    private TextView wo_fankui_textnumber;
    private EditText et_hint_lianxi;
    private TextView tv_tijiao;
    private MyHandler hd;
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
        setContentView(R.layout.wode_fankui);
        back_btn = (LinearLayout) this.findViewById(R.id.ll_back);

        wo_hint_content=(EditText) this.findViewById(R.id.wo_hint_content);
        wo_fankui_textnumber=(TextView)this.findViewById(R.id.wo_fankui_textnumber);
        et_hint_lianxi=(EditText)this.findViewById(R.id.et_hint_lianxi);
        tv_tijiao=(TextView)this.findViewById(R.id.tv_tijiao);
        wo_fankui_textnumber.setText("还可输入200个字");
        hd=new MyHandler(WodeFankuiActivity.this);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WodeFankuiActivity.this.finish();
                overridePendingTransition(R.anim.in_from_left,
                        R.anim.out_of_right);
            }
        });
        wo_hint_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                wo_fankui_textnumber.setText("还可输入"+(200-s.length())+"个字");
            }
        });
        tv_tijiao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(wo_hint_content.getText().toString().trim())){
                    Toast.makeText(WodeFankuiActivity.this,"内容不能为空！",Toast.LENGTH_LONG).show();
                    return;
                }

             if(tv_tijiao.getText().toString().equals("提交")){
                 if(NetJudgeUtils.getNetConnection(WodeFankuiActivity.this)){
                     tv_tijiao.setText("正在提交中...");
                                         ThreadPollFactory.getNormalPool()
                                                 .execute(new sendContent(et_hint_lianxi.getText().toString().trim(),wo_hint_content.getText().toString().trim()));


                 }else{
                     Toast.makeText(WodeFankuiActivity.this, "您的网络不给力，请检查更新！", Toast.LENGTH_LONG).show();

                 }

            }
            }
        });
    }
    /**
     * 的到圈值接口
     */
    private class sendContent implements Runnable {
        String mobile;
        String content;


        public sendContent(String mobile, String content) {
            this.mobile = mobile;
            this.content = content;


        }

        @Override
        public void run() {
            try {
                Map<String, String> params = new HashMap<>();
                params.put("code", mQuanId);
                params.put("mobile", URLEncoder.encode(mobile,"UTF-8"));
                params.put("content", URLEncoder.encode(content,"UTF-8"));
               if(JudgeLoginTaobao.isLogin()){
                   params.put("nickname",URLEncoder.encode(AlibcLogin.getInstance().getSession().nick,"UTF-8"));
               }else{
                   params.put("nickname", "");

               }
                params = BaseMap.getMapAll(params);

                //System.out.println(inviteCode+"ss"+buyerName+"bb");
                String url = getResources().getString(R.string.appurl)+"/mine/problem"+"?stamp=" + UrlUtils.getTime() + "&encode=" + UrlUtils.getEncode();
                String jsonData = HttpConBase.getjsonByPost(url, params, "utf-8");
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
private class MyHandler extends Handler {
    private WeakReference<WodeFankuiActivity> mWeakReference = null;

    MyHandler(WodeFankuiActivity activity) {
        mWeakReference = new WeakReference<WodeFankuiActivity>(activity);
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        WodeFankuiActivity wodeFankuiActivity = mWeakReference.get();
        if (wodeFankuiActivity == null) {
            return;
        }
        if (msg.what == 1) {
            try {
                JSONObject json = new JSONObject((String) msg.obj);
                String code = json.getString("code");
                if (code.equals("0")) {
                    Toast.makeText(WodeFankuiActivity.this, "提交成功！", Toast.LENGTH_LONG).show();
                    WodeFankuiActivity.this.finish();
                    overridePendingTransition(R.anim.in_from_left, R.anim.out_of_right);
                } else {
                    Toast.makeText(WodeFankuiActivity.this, json.getString("errorMsg"), Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(WodeFankuiActivity.this, "服务器错误！", Toast.LENGTH_LONG).show();
            } finally {
                tv_tijiao.setText("提交");
            }


        }
        if (msg.what == 2) {
            tv_tijiao.setText("提交");
            Toast.makeText(WodeFankuiActivity.this, "服务器错误！", Toast.LENGTH_LONG).show();

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
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            this.finish();
            overridePendingTransition(R.anim.in_from_left, R.anim.out_of_right);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("WodeFankuiActivity");
        MobclickAgent.onResume(this);


    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("WodeFankuiActivity");
        MobclickAgent.onPause(this);
    }
}
