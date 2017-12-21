package com.yiqi.choose.activity;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.yiqi.choose.R;
import com.yiqi.choose.base.BaseActivity;
import com.yiqi.choose.base.BaseMap;
import com.yiqi.choose.factory.ThreadPollFactory;
import com.yiqi.choose.fragment.ShareIncomeFragment;
import com.yiqi.choose.utils.AndroidUtils;
import com.yiqi.choose.utils.HttpConBase;
import com.yiqi.choose.utils.NetJudgeUtils;
import com.yiqi.choose.utils.SharedPfUtils;
import com.yiqi.choose.utils.SystemBarTintManager;
import com.yiqi.choose.utils.UrlUtils;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by moumou on 17/10/27.
 */

public class PhoneActivity extends BaseActivity {
    private LinearLayout back_btn;
    private EditText et_hint_yz;
    private EditText et_hint_phone;
    private TextView tv_get_yz;
    private TextView tv_tijiao;
    int count = 0;
    private TimerTask timerTask;
    private Timer timer;
    private MyHandler hd;

    String mark;
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
        setContentView(R.layout.invite_phone);
        initView();
        initData();
    }
    private void initView(){
        back_btn = (LinearLayout) this.findViewById(R.id.ll_back);
        et_hint_phone=(EditText)this.findViewById(R.id.et_hint_phone);
        et_hint_yz=(EditText) this.findViewById(R.id.et_hint_yz);
        tv_get_yz=(TextView)this.findViewById(R.id.tv_get_yz);
        tv_tijiao=(TextView)this.findViewById(R.id.tv_tijiao);
        hd=new MyHandler(PhoneActivity.this);

    }
    private void initData(){
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhoneActivity.this.finish();
                overridePendingTransition(R.anim.in_from_left,
                        R.anim.out_of_right);
            }
        });
        tv_get_yz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetJudgeUtils.getNetConnection(PhoneActivity.this)) {
                    if (TextUtils.isEmpty(et_hint_phone.getText().toString().trim())) {
                        Toast.makeText(PhoneActivity.this,"请先输入手机号码",Toast.LENGTH_LONG).show();
                    return;
                    }
                    if (AndroidUtils.hasBlank(et_hint_phone.getText().toString().trim())) {
                        Toast.makeText(PhoneActivity.this,"您输入的手机号有误",Toast.LENGTH_LONG).show();

                        return;
                    }
                    if (et_hint_phone.getText().toString().trim().length() < 11) {
                        Toast.makeText(PhoneActivity.this,"您输入的手机号有误",Toast.LENGTH_LONG).show();
                        return;
                    }
                        if (tv_get_yz.getText().toString().trim().equals("获取验证码")) {
                            count = 60;
                            startCount();
                            ThreadPollFactory.getNormalPool().execute(new GetYz(et_hint_phone.getText().toString().trim()));

                        }

                } else {
                    Toast.makeText(PhoneActivity.this, "请检查网络连接！", Toast.LENGTH_LONG).show();
                }

            }
        });
        tv_tijiao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetJudgeUtils.getNetConnection(PhoneActivity.this)) {
                    if (TextUtils.isEmpty(et_hint_phone.getText().toString().trim())) {
                        Toast.makeText(PhoneActivity.this,"请先输入手机号码",Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (AndroidUtils.hasBlank(et_hint_phone.getText().toString().trim())) {
                        Toast.makeText(PhoneActivity.this,"您输入的手机号有误",Toast.LENGTH_LONG).show();

                        return;
                    }
                    if (et_hint_phone.getText().toString().trim().length() < 11) {
                        Toast.makeText(PhoneActivity.this,"您输入的手机号有误",Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (TextUtils.isEmpty(et_hint_yz.getText().toString().trim())) {
                        Toast.makeText(PhoneActivity.this,"验证码不能为空",Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (tv_tijiao.getText().toString().trim().equals("提交申请")) {
                        tv_tijiao.setText("提交申请中...");
                        ThreadPollFactory.getNormalPool().execute(new SendPhone(et_hint_phone.getText().toString().trim(),et_hint_yz.getText().toString().trim(),mark));
                    }

                }else {
                    Toast.makeText(PhoneActivity.this, "请检查网络连接！", Toast.LENGTH_LONG).show();
                }


            }

        });
    }
    /**
     * 的到圈值接口
     */
    private class SendPhone implements Runnable {
        String phone;
        String yazhen;
        String mark;
        public SendPhone(String phone,String yanzhen,String mark) {
            this.phone = phone;
            this.yazhen=yanzhen;
            this.mark=mark;
        }
        @Override
        public void run() {
            try {
                Map<String, String> params = new HashMap<>();
                params.put("mobile", phone);
                params.put("checkcode",yazhen);
                params.put("mark",mark);
                params.put("code",SplashActicity1.mQuanId);
                params = BaseMap.getMapAll(params);



                String url="";
                if(getResources().getString(R.string.appurl).contains("120.26.244.207")){
                    url = getResources().getString(R.string.appurltest) + "/activity/joinin"+"?stamp=" + UrlUtils.getTime() + "&encode=" + UrlUtils.getEncode();
                }else{
                    url = getResources().getString(R.string.appurl) + "/activity/joinin"+"?stamp=" + UrlUtils.getTime() + "&encode=" + UrlUtils.getEncode();
                }
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
    private class GetYz implements Runnable {
        String phone;
        public GetYz(String phone) {
            this.phone = phone;
        }
        @Override
        public void run() {
            try {
                Map<String, String> params = new HashMap<>();
                params.put("mobile", phone);
                params.put("type","1");
                params = BaseMap.getMapAll(params);
                String url="";
                if(getResources().getString(R.string.appurl).contains("120.26.244.207")){
                    url = getResources().getString(R.string.appurl) + "/activity/sendcode"+"?stamp=" + UrlUtils.getTime() + "&encode=" + UrlUtils.getEncode();
                }else{
                    url = getResources().getString(R.string.appurl) + "/activity/sendcode"+"?stamp=" + UrlUtils.getTime() + "&encode=" + UrlUtils.getEncode();
                }                String jsonData = HttpConBase.getjsonByPost(url, params, "utf-8");
                //AndroidUtils.printlnMap(params);
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
    public void startCount() {
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                if (count > 0) {
                    hd.sendEmptyMessage(9);

                } else {
                    hd.sendEmptyMessage(10);

                }
                count--;

            }
        };
        timer.schedule(timerTask, 0, 1000);
    }
    @TargetApi(19)
    private void setTranslucentStatus(boolean on){
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
        MobclickAgent.onPageStart("PhoneActivity");
        MobclickAgent.onResume(this);

    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("PhoneActivity");
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

    private class MyHandler extends Handler {
        private WeakReference<PhoneActivity> mWeakReference = null;

        MyHandler(PhoneActivity activity){
            mWeakReference = new WeakReference<PhoneActivity>(activity);
        }
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            PhoneActivity phoneActivity = mWeakReference.get();
            if(phoneActivity==null){
                return;
            }
            if(Build.VERSION.SDK_INT >= 17) {
                if (phoneActivity == null || phoneActivity.isDestroyed() || phoneActivity.isFinishing()) {
                    return;
                }
            }else {
                if (phoneActivity == null || phoneActivity.isFinishing()) {
                    return;
                }
            }
            if(msg.what==1){
                try {
                    JSONObject json = new JSONObject((String) msg.obj);
                    String code = json.getString("code");
                    if (code.equals("0")) {
                      //  Toast.makeText(PhoneActivity.this,"",Toast.LENGTH_LONG).show();
                         mark=json.getString("data");

                       // SharedPfUtils.saveData(PhoneActivity.this,"invitemark",data);

                    }else{
                        Toast.makeText(PhoneActivity.this,json.getString("errorMsg"),Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(PhoneActivity.this,"服务器错误!",Toast.LENGTH_LONG).show();

                }
            }
            if(msg.what==2){
                Toast.makeText(PhoneActivity.this,"服务器错误!",Toast.LENGTH_LONG).show();
            }
            if(msg.what==3){
                //活动太火爆，请稍后再申请
                try {
                    JSONObject json = new JSONObject((String) msg.obj);

                    String code = json.getString("code");
                    if(code.equals("0")){
                        String newQuanId=json.getString("data");
                        //Toast.makeText(PhoneActivity.this,"新圈号："+newQuanId,Toast.LENGTH_LONG);
                        SharedPfUtils.saveData(PhoneActivity.this,"newQuanId",newQuanId);
                      ShareIncomeFragment.isPhoneCome=true;
                        //跳到详情页
                        PhoneActivity.this.finish();

                    }else{
                        Toast.makeText(PhoneActivity.this,json.getString("errorMsg"),Toast.LENGTH_LONG).show();
                    }
//                    else if(code.equals("1")){
//                        Toast.makeText(PhoneActivity.this,"验证码错误!",Toast.LENGTH_LONG).show();
//
//                    }else if(code.equals("2")){
//                        Toast.makeText(PhoneActivity.this,"活动太火爆，请稍后再申请!",Toast.LENGTH_LONG).show();
//                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    tv_tijiao.setText("提交申请");
                }
            }
            if(msg.what==4){
                Toast.makeText(PhoneActivity.this,"服务器错误!",Toast.LENGTH_LONG).show();
                tv_tijiao.setText("提交申请");

            }

            if (msg.what == 9) {
                tv_get_yz.setText(count + "秒");
            }
            if (msg.what == 10) {
                tv_get_yz.setText("获取验证码");
                timerTask.cancel();
            }

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        count=0;
    }
}
