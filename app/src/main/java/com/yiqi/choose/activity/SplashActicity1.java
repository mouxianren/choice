package com.yiqi.choose.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.igexin.sdk.PushManager;
import com.umeng.analytics.MobclickAgent;
import com.yiqi.choose.R;
import com.yiqi.choose.Service.DemoIntentService;
import com.yiqi.choose.Service.DemoPushService;
import com.yiqi.choose.base.BaseActivity;
import com.yiqi.choose.base.BaseMap;
import com.yiqi.choose.dao.SSDao;
import com.yiqi.choose.factory.ThreadPollFactory;
import com.yiqi.choose.utils.AndroidUtils;
import com.yiqi.choose.utils.CustomProgressDialog;
import com.yiqi.choose.utils.HttpConBase;
import com.yiqi.choose.utils.NetJudgeUtils;
import com.yiqi.choose.utils.SharedPfUtils;
import com.yiqi.choose.utils.UrlUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by moumou on 17/8/10.
 */

public class SplashActicity1 extends BaseActivity {
    private TextView splash_tv_time;
    private Dialog dialog;

    int timeWait = 3;
    int h = 0;//时间判断值
    private Handler mHandler = new Handler();// 全局handler
    public static String client_id;//ieme
    /**
     * 双击退出函数
     */
    private static Boolean isExit = false;
    private TextView dialog_tv_makesure;
    private TextView dialog_tv_skip;

    private SSDao dao;
//

    public static String mPId="";
    public static String mQuanId="";
    public static String data="";

    public static String GTgoodsId="";

    private boolean pemissonStorge=true;
    public static  boolean isGTSent=false;

    public  static boolean isInvite=false;
    public  static boolean isGetInvite=false;
    public static boolean sdCardWritePermisson=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

            setContentView(R.layout.splash);
//        if (Build.VERSION.SDK_INT >= 21) {
//            View decorView = getWindow().getDecorView();
//            int option = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_FULLSCREEN;
//            decorView.setSystemUiVisibility(option);
//        }

     //  Toast.makeText(SplashActicity1.this,"个推透传值："+data,Toast.LENGTH_LONG).show();
        isGTSent=false;
           // createDeskShortCut();
            PackageManager pkgManager = getPackageManager();
            // 读写 sd card 权限非常重要, android6.0默认禁止的, 建议初始化之前就弹窗让用户赋予该权限
        sdCardWritePermisson =
                    pkgManager.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, getPackageName()) == PackageManager.PERMISSION_GRANTED;

            // read phone state用于获取 imei 设备信息
            boolean phoneSatePermission =
                    pkgManager.checkPermission(Manifest.permission.READ_PHONE_STATE, getPackageName()) == PackageManager.PERMISSION_GRANTED;
        splash_tv_time = (TextView) this.findViewById(R.id.splash_tv_time);
        client_id = AndroidUtils.getIeme(SplashActicity1.this);


            //初始化个推组件
            if (Build.VERSION.SDK_INT >= 23 && !sdCardWritePermisson || !phoneSatePermission) {
                requestPermission();
            }else{
                sdCardWritePermisson=true;
                PushManager.getInstance().initialize(this.getApplicationContext(), DemoPushService.class);
                dao=SSDao.getInstance(SplashActicity1.this);
                PushManager.getInstance().registerPushIntentService(this.getApplicationContext(), DemoIntentService.class);
                if (SharedPfUtils.getData(SplashActicity1.this, "token", "").equals("") || SharedPfUtils.getData(SplashActicity1.this, "token", "").equals("null")) {
                    SharedPfUtils.saveStringData(SplashActicity1.this, "token", PushManager.getInstance().getClientid(SplashActicity1.this));
                }
                if (SharedPfUtils.getData(SplashActicity1.this, "quanId", "").equals("")){
                    if(dao.queryPid().equals("")||dao.queryQuanId().equals("")){
                        if(AndroidUtils.getAppMetaData(SplashActicity1.this, "UMENG_CHANNEL").equals("gt302373")||AndroidUtils.getAppMetaData(SplashActicity1.this, "UMENG_CHANNEL").equals("gt356968")||AndroidUtils.getAppMetaData(SplashActicity1.this, "UMENG_CHANNEL").equals("gt422005")){
                            if(AndroidUtils.getAppMetaData(SplashActicity1.this, "UMENG_CHANNEL").equals("gt302373")){
                                splash_tv_time.setText("3");
                                splash_tv_time.setVisibility(View.VISIBLE);
                                //保存圈值
                                SharedPfUtils.saveData(SplashActicity1.this, "quanId","302373");
                                SharedPfUtils.saveData(SplashActicity1.this, "pid","mm_124863698_36816155_132796741");
                                mPId=(String)SharedPfUtils.getData(SplashActicity1.this, "pid", "");
                                mQuanId=(String)SharedPfUtils.getData(SplashActicity1.this, "quanId", "");
                                if(pemissonStorge){
                                    dao.deteAllGoods();
                                    dao.saveSS(mPId,mQuanId);
                                }
                                if(NetJudgeUtils.getNetConnection(SplashActicity1.this)){
                                    ThreadPollFactory.getNormalPool().execute(new getQuanCode1(mQuanId));
                                }
                                //正常逻辑 定时两秒 到达主页
                                new Thread(new TimeCount()).start();// 开启线程
                            }
                            if(AndroidUtils.getAppMetaData(SplashActicity1.this, "UMENG_CHANNEL").equals("gt356968")){
                                splash_tv_time.setText("3");
                                splash_tv_time.setVisibility(View.VISIBLE);
                                //保存圈值
                                SharedPfUtils.saveData(SplashActicity1.this, "quanId","356968");
                                SharedPfUtils.saveData(SplashActicity1.this, "pid","mm_124863698_36816155_132796770");
                                mPId=(String)SharedPfUtils.getData(SplashActicity1.this, "pid", "");
                                mQuanId=(String)SharedPfUtils.getData(SplashActicity1.this, "quanId", "");
                                if(pemissonStorge){
                                    dao.deteAllGoods();
                                    dao.saveSS(mPId,mQuanId);
                                }
                                if(NetJudgeUtils.getNetConnection(SplashActicity1.this)){
                                    ThreadPollFactory.getNormalPool().execute(new getQuanCode1(mQuanId));
                                }
                                //正常逻辑 定时两秒 到达主页
                                new Thread(new TimeCount()).start();// 开启线程
                            }
                            if(AndroidUtils.getAppMetaData(SplashActicity1.this, "UMENG_CHANNEL").equals("gt422005")){
                                splash_tv_time.setText("3");
                                splash_tv_time.setVisibility(View.VISIBLE);
                                //保存圈值
                                SharedPfUtils.saveData(SplashActicity1.this, "quanId","422005");
                                SharedPfUtils.saveData(SplashActicity1.this, "pid","mm_124863698_36816155_132808228");
                                mPId=(String)SharedPfUtils.getData(SplashActicity1.this, "pid", "");
                                mQuanId=(String)SharedPfUtils.getData(SplashActicity1.this, "quanId", "");
                                if(pemissonStorge){
                                    dao.deteAllGoods();
                                    dao.saveSS(mPId,mQuanId);
                                }
                                if(NetJudgeUtils.getNetConnection(SplashActicity1.this)){
                                    ThreadPollFactory.getNormalPool().execute(new getQuanCode1(mQuanId));
                                }
                                //正常逻辑 定时两秒 到达主页
                                new Thread(new TimeCount()).start();// 开启线程
                            }


                        }else{
                            splash_tv_time.setVisibility(View.GONE);
                            //跳出弹框
                            showInputQuanIdDialog();
                        }

                    }else{
                        mPId=dao.queryPid();
                        mQuanId=dao.queryQuanId();
                        //保存圈值
                        SharedPfUtils.saveData(SplashActicity1.this, "quanId",mQuanId);
                        SharedPfUtils.saveData(SplashActicity1.this, "pid", mPId);
                        splash_tv_time.setText("3");
                        splash_tv_time.setVisibility(View.VISIBLE);
                        if(NetJudgeUtils.getNetConnection(SplashActicity1.this)){
                            ThreadPollFactory.getNormalPool().execute(new getQuanCode1(mQuanId));
                        }
                        //正常逻辑 定时两秒 到达主页
                        new Thread(new TimeCount()).start();// 开启线程
                    }

                } else {
                    splash_tv_time.setText("3");
                    splash_tv_time.setVisibility(View.VISIBLE);
                    mPId=(String)SharedPfUtils.getData(SplashActicity1.this, "pid", "");
                    mQuanId=(String)SharedPfUtils.getData(SplashActicity1.this, "quanId", "");
                    if(NetJudgeUtils.getNetConnection(SplashActicity1.this)){

                        ThreadPollFactory.getNormalPool().execute(new getQuanCode1(mQuanId));

                    }
                    //正常逻辑 定时两秒 到达主页
                    new Thread(new TimeCount()).start();// 开启线程
                }
            }


        }



    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE},
                0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if ((grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                PushManager.getInstance().initialize(this.getApplicationContext(),DemoPushService.class);
                pemissonStorge=true;
                sdCardWritePermisson=true;
                dao=SSDao.getInstance(SplashActicity1.this);

                PushManager.getInstance().registerPushIntentService(this.getApplicationContext(), DemoIntentService.class);
                if (SharedPfUtils.getData(SplashActicity1.this, "token", "").equals("") || SharedPfUtils.getData(SplashActicity1.this, "token", "").equals("null")) {
                    SharedPfUtils.saveStringData(SplashActicity1.this, "token", PushManager.getInstance().getClientid(SplashActicity1.this));
                }
                if (SharedPfUtils.getData(SplashActicity1.this, "quanId", "").equals("")) {
                    if(dao.queryPid().equals("")||dao.queryQuanId().equals("")){
                        if(AndroidUtils.getAppMetaData(SplashActicity1.this, "UMENG_CHANNEL").equals("gt302373")||AndroidUtils.getAppMetaData(SplashActicity1.this, "UMENG_CHANNEL").equals("gt356968")||AndroidUtils.getAppMetaData(SplashActicity1.this, "UMENG_CHANNEL").equals("gt422005")){//gt422005
                            if(AndroidUtils.getAppMetaData(SplashActicity1.this, "UMENG_CHANNEL").equals("gt302373")){
                                splash_tv_time.setText("3");
                                splash_tv_time.setVisibility(View.VISIBLE);
                                //保存圈值
                                SharedPfUtils.saveData(SplashActicity1.this, "quanId","302373");
                                SharedPfUtils.saveData(SplashActicity1.this, "pid","mm_124863698_36816155_132796741");
                                mPId=(String)SharedPfUtils.getData(SplashActicity1.this, "pid", "");
                                mQuanId=(String)SharedPfUtils.getData(SplashActicity1.this, "quanId", "");
                                if(pemissonStorge){
                                    dao.deteAllGoods();
                                    dao.saveSS(mPId,mQuanId);
                                }
                                if(NetJudgeUtils.getNetConnection(SplashActicity1.this)){
                                    ThreadPollFactory.getNormalPool().execute(new getQuanCode1(mQuanId));
                                }
                                //正常逻辑 定时两秒 到达主页
                                new Thread(new TimeCount()).start();// 开启线程
                            }
                            if(AndroidUtils.getAppMetaData(SplashActicity1.this, "UMENG_CHANNEL").equals("gt356968")){
                                splash_tv_time.setText("3");
                                splash_tv_time.setVisibility(View.VISIBLE);
                                //保存圈值
                                SharedPfUtils.saveData(SplashActicity1.this, "quanId","356968");
                                SharedPfUtils.saveData(SplashActicity1.this, "pid","mm_124863698_36816155_132796770");
                                mPId=(String)SharedPfUtils.getData(SplashActicity1.this, "pid", "");
                                mQuanId=(String)SharedPfUtils.getData(SplashActicity1.this, "quanId", "");
                                if(pemissonStorge){
                                    dao.deteAllGoods();
                                    dao.saveSS(mPId,mQuanId);
                                }
                                if(NetJudgeUtils.getNetConnection(SplashActicity1.this)){
                                    ThreadPollFactory.getNormalPool().execute(new getQuanCode1(mQuanId));
                                }
                                //正常逻辑 定时两秒 到达主页
                                new Thread(new TimeCount()).start();// 开启线程
                            }
                            if(AndroidUtils.getAppMetaData(SplashActicity1.this, "UMENG_CHANNEL").equals("gt422005")){
                                splash_tv_time.setText("3");
                                splash_tv_time.setVisibility(View.VISIBLE);
                                //保存圈值
                                SharedPfUtils.saveData(SplashActicity1.this, "quanId","422005");
                                SharedPfUtils.saveData(SplashActicity1.this, "pid","mm_124863698_36816155_132808228");
                                mPId=(String)SharedPfUtils.getData(SplashActicity1.this, "pid", "");
                                mQuanId=(String)SharedPfUtils.getData(SplashActicity1.this, "quanId", "");
                                if(pemissonStorge){
                                    dao.deteAllGoods();
                                    dao.saveSS(mPId,mQuanId);
                                }
                                if(NetJudgeUtils.getNetConnection(SplashActicity1.this)){
                                    ThreadPollFactory.getNormalPool().execute(new getQuanCode1(mQuanId));
                                }
                                //正常逻辑 定时两秒 到达主页
                                new Thread(new TimeCount()).start();// 开启线程
                            }


                        }else{
                            splash_tv_time.setVisibility(View.GONE);
                            //跳出弹框
                            showInputQuanIdDialog();
                        }
                    }else{
                        mPId=dao.queryPid();
                        mQuanId=dao.queryQuanId();
                        //保存圈值
                        SharedPfUtils.saveData(SplashActicity1.this, "quanId",mQuanId);
                        SharedPfUtils.saveData(SplashActicity1.this, "pid", mPId);

                        splash_tv_time.setText("3");
                        splash_tv_time.setVisibility(View.VISIBLE);
                        if(NetJudgeUtils.getNetConnection(SplashActicity1.this)){
                            ThreadPollFactory.getNormalPool().execute(new getQuanCode1(mQuanId));
                        }
                        //正常逻辑 定时两秒 到达主页
                        new Thread(new TimeCount()).start();// 开启线程
                    }

                } else {
                    splash_tv_time.setText("3");
                    splash_tv_time.setVisibility(View.VISIBLE);
                    mPId=(String)SharedPfUtils.getData(SplashActicity1.this, "pid", "");
                    mQuanId=(String)SharedPfUtils.getData(SplashActicity1.this, "quanId", "");
                    if(NetJudgeUtils.getNetConnection(SplashActicity1.this)){

                        ThreadPollFactory.getNormalPool().execute(new getQuanCode1(mQuanId));

                    }
                    //正常逻辑 定时两秒 到达主页
                    new Thread(new TimeCount()).start();// 开启线程
                }

            } else {
                pemissonStorge=false;
                sdCardWritePermisson=false;
                Log.e("GetuiSplashActivity", "We highly recommend that you need to grant the special permissions before initializing the SDK, otherwise some "
                        + "functions will not work");
                PushManager.getInstance().initialize(this.getApplicationContext(), DemoPushService.class);
                PushManager.getInstance().registerPushIntentService(this.getApplicationContext(), DemoIntentService.class);
                if (SharedPfUtils.getData(SplashActicity1.this, "token", "").equals("") || SharedPfUtils.getData(SplashActicity1.this, "token", "").equals("null")) {
                    SharedPfUtils.saveStringData(SplashActicity1.this, "token", PushManager.getInstance().getClientid(SplashActicity1.this));
                }
                if (SharedPfUtils.getData(SplashActicity1.this, "quanId", "").equals("")) {
                    if(AndroidUtils.getAppMetaData(SplashActicity1.this, "UMENG_CHANNEL").equals("gt302373")||AndroidUtils.getAppMetaData(SplashActicity1.this, "UMENG_CHANNEL").equals("gt356968")||AndroidUtils.getAppMetaData(SplashActicity1.this, "UMENG_CHANNEL").equals("gt422005")){
                        if(AndroidUtils.getAppMetaData(SplashActicity1.this, "UMENG_CHANNEL").equals("gt302373")){
                            splash_tv_time.setText("3");
                            splash_tv_time.setVisibility(View.VISIBLE);
                            //保存圈值
                            SharedPfUtils.saveData(SplashActicity1.this, "quanId","302373");
                            SharedPfUtils.saveData(SplashActicity1.this, "pid","mm_124863698_36816155_132796741");
                            mPId=(String)SharedPfUtils.getData(SplashActicity1.this, "pid", "");
                            mQuanId=(String)SharedPfUtils.getData(SplashActicity1.this, "quanId", "");
                            if(NetJudgeUtils.getNetConnection(SplashActicity1.this)){
                                ThreadPollFactory.getNormalPool().execute(new getQuanCode1(mQuanId));
                            }
                            //正常逻辑 定时两秒 到达主页
                            new Thread(new TimeCount()).start();// 开启线程
                        }
                        if(AndroidUtils.getAppMetaData(SplashActicity1.this, "UMENG_CHANNEL").equals("gt356968")){
                            splash_tv_time.setText("3");
                            splash_tv_time.setVisibility(View.VISIBLE);
                            //保存圈值
                            SharedPfUtils.saveData(SplashActicity1.this, "quanId","356968");
                            SharedPfUtils.saveData(SplashActicity1.this, "pid","mm_124863698_36816155_132796770");
                            mPId=(String)SharedPfUtils.getData(SplashActicity1.this, "pid", "");
                            mQuanId=(String)SharedPfUtils.getData(SplashActicity1.this, "quanId", "");
                            if(pemissonStorge){
                                dao.deteAllGoods();
                                dao.saveSS(mPId,mQuanId);
                            }
                            if(NetJudgeUtils.getNetConnection(SplashActicity1.this)){
                                ThreadPollFactory.getNormalPool().execute(new getQuanCode1(mQuanId));
                            }
                            //正常逻辑 定时两秒 到达主页
                            new Thread(new TimeCount()).start();// 开启线程
                        }
                        if(AndroidUtils.getAppMetaData(SplashActicity1.this, "UMENG_CHANNEL").equals("gt422005")){
                            splash_tv_time.setText("3");
                            splash_tv_time.setVisibility(View.VISIBLE);
                            //保存圈值
                            SharedPfUtils.saveData(SplashActicity1.this, "quanId","422005");
                            SharedPfUtils.saveData(SplashActicity1.this, "pid","mm_124863698_36816155_132808228");
                            mPId=(String)SharedPfUtils.getData(SplashActicity1.this, "pid", "");
                            mQuanId=(String)SharedPfUtils.getData(SplashActicity1.this, "quanId", "");
                            if(pemissonStorge){
                                dao.deteAllGoods();
                                dao.saveSS(mPId,mQuanId);
                            }
                            if(NetJudgeUtils.getNetConnection(SplashActicity1.this)){
                                ThreadPollFactory.getNormalPool().execute(new getQuanCode1(mQuanId));
                            }
                            //正常逻辑 定时两秒 到达主页
                            new Thread(new TimeCount()).start();// 开启线程
                        }



                    }else{
                        splash_tv_time.setVisibility(View.GONE);
                        //跳出弹框
                        showInputQuanIdDialog();
                    }

                } else {
                    splash_tv_time.setText("3");
                    splash_tv_time.setVisibility(View.VISIBLE);
                    mPId=(String)SharedPfUtils.getData(SplashActicity1.this, "pid", "");
                    mQuanId=(String)SharedPfUtils.getData(SplashActicity1.this, "quanId", "");
                    if(NetJudgeUtils.getNetConnection(SplashActicity1.this)){
                        ThreadPollFactory.getNormalPool().execute(new getQuanCode1(mQuanId));
                    }
                    //正常逻辑 定时两秒 到达主页
                    new Thread(new TimeCount()).start();// 开启线程
                }

            }
        } else {
            sdCardWritePermisson=false;
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    class TimeCount implements Runnable {
        @Override
        public void run() {
            while (timeWait > 0)// 整个倒计时执行的循环
            {
                timeWait--;
                mHandler.post(new Runnable() // 通过它在UI主线程中修改显示的剩余时间
                {
                    public void run() {
                        // System.out.println(i);
                        splash_tv_time.setText(timeWait + "");
                    }
                });
                if (timeWait > 0) {
                    try {
                        Thread.sleep(1000);// 线程休眠一秒钟 这个就是倒计时的间隔时间
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
            // 下面是倒计时结束逻辑
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (h == 0) {
                        if(!TextUtils.isEmpty(data)){
                            try {
                                JSONObject j = new JSONObject(data);
                                //System.out.println("j.---"+j.toString());
                                String status=j.getString("status");
                                if(status.equals("1")){
                                    String type=j.getString("type");
                                    if(type.equals("1")){
                                        String goodsId=j.getString("goodsId");
                                        GTgoodsId=goodsId;
                                        Intent intent = new Intent(SplashActicity1.this,
                                                MainAcitivity1.class);
                                        startActivity(intent);
                                        SplashActicity1.this.finish();
                                        SplashActicity1.this.overridePendingTransition(
                                                R.anim.to_right, R.anim.to_left);

//                                        String title=j.getString("title");
//                                        //跳到首页
//                                        Intent intent = new Intent(SplashActicity1.this,
//                                                WebActivity2.class);
//                                        intent.putExtra("url",goodsId);
//                                        intent.putExtra("title",title);
//                                        startActivity(intent);
//                                        SplashActicity1.this.finish();
//                                        SplashActicity1.this.overridePendingTransition(
//                                                R.anim.to_right, R.anim.to_left);
                                    }else{
                                        Intent intent = new Intent(SplashActicity1.this,
                                                MainAcitivity1.class);
                                        startActivity(intent);
                                        SplashActicity1.this.finish();
                                        SplashActicity1.this.overridePendingTransition(
                                                R.anim.to_right, R.anim.to_left);
                                    }
                                }else{
                                    Intent intent = new Intent(SplashActicity1.this,
                                            MainAcitivity1.class);
                                    startActivity(intent);
                                    SplashActicity1.this.finish();
                                    SplashActicity1.this.overridePendingTransition(
                                            R.anim.to_right, R.anim.to_left);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                Intent intent = new Intent(SplashActicity1.this,
                                        MainAcitivity1.class);
                                startActivity(intent);
                                SplashActicity1.this.finish();
                                SplashActicity1.this.overridePendingTransition(
                                        R.anim.to_right, R.anim.to_left);
                            }

                        }else{
                            Intent intent = new Intent(SplashActicity1.this,
                                    MainAcitivity1.class);
                            startActivity(intent);
                            SplashActicity1.this.finish();
                            SplashActicity1.this.overridePendingTransition(
                                    R.anim.to_right, R.anim.to_left);
                        }



                        h = 1;

                    }

                }
            });
        }
    }

    private void showInputQuanIdDialog() {
        dialog = new Dialog(SplashActicity1.this, R.style.FullHeightDialog);
        // dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        if (!dialog.isShowing()) {
            View view = LayoutInflater.from(SplashActicity1.this).inflate(
                    R.layout.input_invite_dialog, null);
            dialog.setContentView(
                    view,
                    new ViewGroup.LayoutParams(AndroidUtils.dip2px(
                            SplashActicity1.this, 300), LinearLayout.LayoutParams.WRAP_CONTENT));
            final EditText et_hint = (EditText) view.findViewById(R.id.et_hint_invite);
            dialog_tv_makesure = (TextView) view.findViewById(R.id.dialog_tv_makesure);
            dialog_tv_skip=(TextView)view.findViewById(R.id.dialog_tv_skip);
            dialog_tv_skip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //保存圈值
                    SharedPfUtils.saveData(SplashActicity1.this, "quanId","302373");
                    SharedPfUtils.saveData(SplashActicity1.this, "pid","mm_124863698_36816155_132796741");
                    mPId=(String)SharedPfUtils.getData(SplashActicity1.this, "pid", "");
                    mQuanId=(String)SharedPfUtils.getData(SplashActicity1.this, "quanId", "");
                    if(pemissonStorge){
                        dao.deteAllGoods();
                        dao.saveSS(mPId,mQuanId);
                    }
                    if(NetJudgeUtils.getNetConnection(SplashActicity1.this)){
                        ThreadPollFactory.getNormalPool().execute(new getQuanCode1(mQuanId));
                    }
                    dialog.dismiss();
                    Intent intent = new Intent(SplashActicity1.this,
                            MainAcitivity1.class);
                    startActivity(intent);
                    SplashActicity1.this.finish();
                    SplashActicity1.this.overridePendingTransition(
                            R.anim.to_right, R.anim.to_left);

                }
            });

            dialog_tv_makesure.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (TextUtils.isEmpty(et_hint.getText().toString().trim())) {
                        Toast.makeText(SplashActicity1.this, "请先输入邀请码！", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (NetJudgeUtils.getNetConnection(SplashActicity1.this)) {
                        //splash_dialog_tv.setText("正在提交邀请码...");
                        CustomProgressDialog.createDialog(SplashActicity1.this,"");
                        AndroidUtils.hideInput(SplashActicity1.this,et_hint);
                        ThreadPollFactory.getNormalPool().execute(new getQuanCode(et_hint.getText().toString().trim()));

                    } else {
                        Toast.makeText(SplashActicity1.this, "请检查网络连接！", Toast.LENGTH_LONG).show();
                    }

                }
            });
            dialog.show();
        }

    }

    /**
     * 创建快捷方式
     */
    public void createDeskShortCut() {

        Log.i("coder", "------createShortCut--------");
        // 创建快捷方式的Intent
        Intent shortcutIntent = new Intent(
                "com.android.launcher.action.INSTALL_SHORTCUT");
        // 不允许重复创建
        shortcutIntent.putExtra("duplicate", false);
        // 需要现实的名称
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME,
                getString(R.string.app_name));

        // 快捷图片
        Parcelable icon = Intent.ShortcutIconResource.fromContext(
                getApplicationContext(), R.mipmap.ic_launcher);

        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);

        Intent intent = new Intent(getApplicationContext(), SplashActicity1.class);
        // 下面两个属性是为了当应用程序卸载时桌面 上的快捷方式会删除
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");
        // 点击快捷图片，运行的程序主入口
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
        // 发送广播。OK
        sendBroadcast(shortcutIntent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        //        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
        //
        //                exitBy2Click(); //调用双击退出函数
        //            return true;
        //            }
        //
        //
        h = 1;
        timeWait = 0;
        SplashActicity1.this.finish();


        return super.onKeyDown(keyCode, event);
    }

    //点击两次跳出程序
    private void exitBy2Click() {
        Timer tExit = null;
        if (isExit == false) {
            isExit = true; // 准备退出
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false; // 取消退出
                }
            }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务

        } else {
            SplashActicity1.this.finish();

        }
    }

    /**
     * 的到圈值接口
     */
    private class getQuanCode implements Runnable {
        String inviteCode;
        public getQuanCode(String inviteCode) {
            this.inviteCode = inviteCode;
        }
        @Override
        public void run() {
            try {
                Map<String, String> params = new HashMap<>();
                params.put("code", inviteCode);
                params = BaseMap.getMapAll(params);
                String url="";
                if(getResources().getString(R.string.appurl).contains("120.26.244.207")){
                    url = getResources().getString(R.string.appurl) + "/home/invite"+"?stamp=" + UrlUtils.getTime() + "&encode=" + UrlUtils.getEncode();
                }else{
                    url = getResources().getString(R.string.appurl) + "/home/invite"+"?stamp=" + UrlUtils.getTime() + "&encode=" + UrlUtils.getEncode();
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

    /**
     * 的到圈值接口
     */
    private class getQuanCode1 implements Runnable {
        String inviteCode;

        public getQuanCode1(String inviteCode) {
            this.inviteCode = inviteCode;

        }

        @Override
        public void run() {
            try {
                Map<String, String> params = new HashMap<>();
                params.put("code", inviteCode);
                params = BaseMap.getMapAll(params);
                String url="";
                if(getResources().getString(R.string.appurl).contains("120.26.244.207")){
                    url = getResources().getString(R.string.appurl) + "/home/invite"+"?stamp=" + UrlUtils.getTime() + "&encode=" + UrlUtils.getEncode();
                }else{
                    url = getResources().getString(R.string.appurl) + "/home/invite"+"?stamp=" + UrlUtils.getTime() + "&encode=" + UrlUtils.getEncode();
                }
                String jsonData = HttpConBase.getjsonByPost(url, params, "utf-8");
                Message msg = Message.obtain();
                msg.what = 3;
                msg.obj = jsonData;
                hd.sendMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
    private Handler hd = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                try {
                    JSONObject json = new JSONObject((String) msg.obj);
                    String code = json.getString("code");
                    if (code.equals("0")) {
                        String sdata=json.getString("data");
                        JSONObject jData = new JSONObject(sdata);
                        String pid=jData.getString("pid");
                        String quanId=jData.getString("code");

                        String iszhuan=jData.getString("iszhuan");
                        if(iszhuan.equals("1")){
                           isInvite=true;
                        }else{
                            isInvite=false;
                        }
                        isGetInvite=true;

                        mPId=pid;
                       mQuanId=quanId;
                        //保存圈值
                        SharedPfUtils.saveData(SplashActicity1.this, "quanId",quanId);
                        SharedPfUtils.saveData(SplashActicity1.this, "pid", pid);
if(pemissonStorge){
    dao.deteAllGoods();
    dao.saveSS(pid,quanId);
}

//

                        if(dialog!=null&&dialog.isShowing()){
                            dialog.dismiss();
                        }
                        if(!TextUtils.isEmpty(data)){
                            try {
                                JSONObject j = new JSONObject(data);
                                String status=j.getString("status");
                                if(status.equals("1")){
                                    String type=j.getString("type");
                                    if(type.equals("1")){
                                        String goodsId=j.getString("goodsId");
                                        GTgoodsId=goodsId;
                                        Intent intent = new Intent(SplashActicity1.this,
                                                MainAcitivity1.class);
                                        startActivity(intent);
                                        SplashActicity1.this.finish();
                                        SplashActicity1.this.overridePendingTransition(
                                                R.anim.to_right, R.anim.to_left);
                                    }else{
                                        Intent intent = new Intent(SplashActicity1.this,
                                                MainAcitivity1.class);
                                        startActivity(intent);
                                        SplashActicity1.this.finish();
                                        SplashActicity1.this.overridePendingTransition(
                                                R.anim.to_right, R.anim.to_left);
                                    }
                                }else{
                                    Intent intent = new Intent(SplashActicity1.this,
                                            MainAcitivity1.class);
                                    startActivity(intent);
                                    SplashActicity1.this.finish();
                                    SplashActicity1.this.overridePendingTransition(
                                            R.anim.to_right, R.anim.to_left);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                Intent intent = new Intent(SplashActicity1.this,
                                        MainAcitivity1.class);
                                startActivity(intent);
                                SplashActicity1.this.finish();
                                SplashActicity1.this.overridePendingTransition(
                                        R.anim.to_right, R.anim.to_left);
                            }

                        }else{
                            //跳到首页
                            Intent intent = new Intent(SplashActicity1.this,
                                    MainAcitivity1.class);

                            startActivity(intent);
                            SplashActicity1.this.finish();
                            SplashActicity1.this.overridePendingTransition(
                                    R.anim.to_right, R.anim.to_left);
                        }


                    } else {
                        Toast.makeText(SplashActicity1.this, json.getString("errorMsg"), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                  //  splash_dialog_tv.setText("加入圈子");
                    CustomProgressDialog.stopProgressDialog();
                }


            }
            if (msg.what == 2) {
                Toast.makeText(SplashActicity1.this, "服务器错误！", Toast.LENGTH_LONG).show();
               // splash_dialog_tv.setText("加入圈子");
                CustomProgressDialog.stopProgressDialog();
            }
            if (msg.what ==3) {
                try {
                    JSONObject json = new JSONObject((String) msg.obj);
                    String code = json.getString("code");
                    if (code.equals("0")) {
                        String sdata=json.getString("data");
                        JSONObject jData = new JSONObject(sdata);
                        String pid=jData.getString("pid");
                        String quanId=jData.getString("code");
                        String iszhuan=jData.getString("iszhuan");
                        if(iszhuan.equals("1")){
                            isInvite=true;
                        }else{
                            isInvite=false;
                        }
                        isGetInvite=true;
                        if((!TextUtils.isEmpty(pid))&&(!TextUtils.isEmpty(quanId))){
                            mPId=pid;
                            mQuanId=quanId;

                            if(!SharedPfUtils.getData(SplashActicity1.this,"quanId","").equals(mQuanId)){
                                isGTSent=true;
                            }else{
                                isGTSent=false;
                            }

                            SharedPfUtils.saveData(SplashActicity1.this, "pid", pid);
                            //保存圈值
                            SharedPfUtils.saveData(SplashActicity1.this, "quanId",quanId);

                            if(pemissonStorge){
                                dao.deteAllGoods();
                                dao.saveSS(pid,quanId);
                            }
                        }



                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    };

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("SplashActivity1");
        MobclickAgent.onResume(this);

    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("SplashActivity1");
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CustomProgressDialog.stopProgressDialog();
        data="";
    }
}
