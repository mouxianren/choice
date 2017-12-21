package com.yiqi.choose.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.yiqi.choose.R;
import com.yiqi.choose.base.BaseActivity;
import com.yiqi.choose.base.BaseMap;
import com.yiqi.choose.factory.ThreadPollFactory;
import com.yiqi.choose.utils.HttpConBase;
import com.yiqi.choose.utils.NetJudgeUtils;
import com.yiqi.choose.utils.SystemBarTintManager;
import com.yiqi.choose.utils.UrlUtils;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by moumou on 17/10/27.
 */

public class Invite_makemoneyActivity extends BaseActivity {
    private LinearLayout back_btn;
    private TextView tv_apply;
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
        setContentView(R.layout.invite_makemoeny);

        back_btn = (LinearLayout) this.findViewById(R.id.ll_back);
        tv_apply = (TextView) this.findViewById(R.id.tv_apply);
        hd = new MyHandler(Invite_makemoneyActivity.this);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Invite_makemoneyActivity.this.finish();
                overridePendingTransition(R.anim.in_from_left,
                        R.anim.out_of_right);
            }
        });
        tv_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(NetJudgeUtils.getNetConnection(Invite_makemoneyActivity.this)){
                    if(tv_apply.getText().toString().trim().equals("申请参加活动")){
                        tv_apply.setText("提交申请中...");
                        ThreadPollFactory.getNormalPool().execute(new CheckNumber());
                    }else{
                        Toast.makeText(Invite_makemoneyActivity.this, "正在申请中，不要重复提交申请！", Toast.LENGTH_LONG).show();
                    }

                } else {
                    Toast.makeText(Invite_makemoneyActivity.this, "请检查网络连接！", Toast.LENGTH_LONG).show();
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

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("Invite_makemoneyActivity");
        MobclickAgent.onResume(this);

    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("Invite_makemoneyActivity");
        MobclickAgent.onPause(this);
    }

    /**
     * 的到圈值接口
     */
    private class CheckNumber implements Runnable {

        @Override
        public void run() {
            try {
                Map<String, String> params = new HashMap<>();
                params = BaseMap.getMapAll(params);
                String url = "";
                if (getResources().getString(R.string.appurl).contains("120.26.244.207")) {
                    url = getResources().getString(R.string.appurl) + "/activity/pid" + "?stamp=" + UrlUtils.getTime() + "&encode=" + UrlUtils.getEncode();
                } else {
                    url = getResources().getString(R.string.appurl) + "/activity/pid" + "?stamp=" + UrlUtils.getTime() + "&encode=" + UrlUtils.getEncode();
                }
                String jsonData = HttpConBase.getjsonByPost(url, params, "utf-8");
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

    private class MyHandler extends Handler {
        private WeakReference<Invite_makemoneyActivity> mWeakReference = null;

        MyHandler(Invite_makemoneyActivity activity) {
            mWeakReference = new WeakReference<Invite_makemoneyActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Invite_makemoneyActivity invite_makemoneyActivity = mWeakReference.get();
            if (invite_makemoneyActivity == null) {
                return;
            }
            if (Build.VERSION.SDK_INT >= 17) {
                if (invite_makemoneyActivity == null || invite_makemoneyActivity.isDestroyed() || invite_makemoneyActivity.isFinishing()) {
                    return;
                }
            } else {
                if (invite_makemoneyActivity == null || invite_makemoneyActivity.isFinishing()) {
                    return;
                }
            }
            if (msg.what == 1) {
                try {
                    JSONObject json = new JSONObject((String) msg.obj);
                    String code = json.getString("code");
                    if (code.equals("0")) {
                        //  Toast.makeText(PhoneActivity.this,"",Toast.LENGTH_LONG).show();
                        String data = json.getString("data");
                        if (data.equals("0")) {
                            Intent intent = new Intent(Invite_makemoneyActivity.this,
                                    PhoneActivity.class);
                            startActivity(intent);
                            Invite_makemoneyActivity.this.finish();
                            overridePendingTransition(
                                    R.anim.to_right, R.anim.to_left);
                        } else {
                            Toast.makeText(Invite_makemoneyActivity.this, "活动太火爆，请稍后再申请!", Toast.LENGTH_LONG).show();

                        }
                        // SharedPfUtils.saveData(PhoneActivity.this,"invitemark",data);

                    } else {
                        Toast.makeText(Invite_makemoneyActivity.this, json.getString("errorMsg"), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(Invite_makemoneyActivity.this, "服务器错误!", Toast.LENGTH_LONG).show();

                }finally {
                    tv_apply.setText("申请参加活动");
                }
            }
            if (msg.what == 2) {
                Toast.makeText(Invite_makemoneyActivity.this, "服务器错误!", Toast.LENGTH_LONG).show();
                tv_apply.setText("申请参加活动");
            }


        }
    }

    ;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

            this.finish();
            overridePendingTransition(R.anim.in_from_left, R.anim.out_of_right);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
