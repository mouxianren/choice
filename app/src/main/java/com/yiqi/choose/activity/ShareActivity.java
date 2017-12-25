package com.yiqi.choose.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.yiqi.choose.R;
import com.yiqi.choose.base.BaseActivity;
import com.yiqi.choose.factory.ThreadPollFactory;
import com.yiqi.choose.thread.StatisticsShareThread;
import com.yiqi.choose.utils.BooleanThread;
import com.yiqi.choose.utils.HttpConBase;
import com.yiqi.choose.utils.NetJudgeUtils;
import com.yiqi.choose.utils.SharedPfUtils;
import com.yiqi.choose.utils.SystemBarTintManager;
import com.yiqi.choose.utils.UrlUtils;

import org.json.JSONObject;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * Created by moumou on 17/9/26.
 */

public class ShareActivity extends BaseActivity{
    private LinearLayout back_btn;
    private TextView tv_title_4;
    private LinearLayout ll_weixin;
    private LinearLayout ll_weixin_friend;
    private LinearLayout ll_qq;
    private LinearLayout ll_qqspace;

    private RelativeLayout fragment_youhui_rl_pb;
    private String shareTitle;
    private String shareUrl;
    private String shareContent;
    private String sharePictureUrl;
    private LinearLayout ll_share;

    private LinearLayout ll_share_text;
    private LinearLayout ll_share_text_new;

    private String typeShare;
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
        setContentView(R.layout.layout_share_new);
        tv_title_4=(TextView)this.findViewById(R.id.tv_title_4);
        ll_qq=(LinearLayout)this.findViewById(R.id.ll_qq);
        ll_weixin=(LinearLayout)this.findViewById(R.id.ll_weixin);
        ll_weixin_friend=(LinearLayout)this.findViewById(R.id.ll_weixin_friend);
        ll_qqspace=(LinearLayout)this.findViewById(R.id.ll_qqspace);

        back_btn = (LinearLayout) this.findViewById(R.id.ll_back);
        ll_share=(LinearLayout)this.findViewById(R.id.ll_share);
        fragment_youhui_rl_pb=(RelativeLayout)this.findViewById(R.id.fragment_youhui_rl_pb);

        ll_share_text=(LinearLayout)this.findViewById(R.id.ll_sharetext);
        ll_share_text_new=(LinearLayout)this.findViewById(R.id.ll_share_text_new);
        Intent intent=getIntent();
        typeShare=intent.getStringExtra("typeShare");

        if(typeShare.equals("1")){
            ll_share_text_new.setVisibility(View.GONE);
            ll_share_text.setVisibility(View.VISIBLE);
            if(!TextUtils.isEmpty((String)SharedPfUtils.getData(ShareActivity.this,"newQuanId",""))){
                tv_title_4.setText((String)SharedPfUtils.getData(ShareActivity.this,"newQuanId",""));
            }else{
                tv_title_4.setText(SplashActicity1.mQuanId);
            }

        }else{
            ll_share_text.setVisibility(View.GONE);
            ll_share_text_new.setVisibility(View.VISIBLE);
            tv_title_4.setText((String)SharedPfUtils.getData(ShareActivity.this,"newQuanId",""));
        }

       initData();
        if(NetJudgeUtils.getNetConnection(ShareActivity.this)){
            fragment_youhui_rl_pb.setVisibility(View.VISIBLE);
            ThreadPollFactory.getNormalPool().execute(new getShareThread());

        }else{
            fragment_youhui_rl_pb.setVisibility(View.GONE);
            ll_share.setVisibility(View.GONE);
        }

    }

    /**
     * 的到圈值接口
     */
    private class getShareThread implements Runnable {

        @Override
        public void run() {
            try {
                String url="";
                if(!TextUtils.isEmpty((String)SharedPfUtils.getData(ShareActivity.this,"newQuanId",""))){
                    url = getResources().getString(R.string.appurl) + "/mine/share"+"?code="+SharedPfUtils.getData(ShareActivity.this,"newQuanId","")+"&stamp=" + UrlUtils.getTime() + "&encode=" + UrlUtils.getEncode();

                }else{
                    url = getResources().getString(R.string.appurl) + "/mine/share"+"?code="+SplashActicity1.mQuanId+"&stamp=" + UrlUtils.getTime() + "&encode=" + UrlUtils.getEncode();

                }
                String jsonData = HttpConBase.sendGet(url);
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
    private Handler hd = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                try {
                    JSONObject json = new JSONObject((String) msg.obj);
                    String code = json.getString("code");
                    if (code.equals("0")){
                        String data=json.getString("data");
                        if(TextUtils.isEmpty(data)){
                            ll_share.setVisibility(View.GONE);
                        }else{
                            ll_share.setVisibility(View.VISIBLE);
                            JSONObject jData = new JSONObject(data);

                            shareTitle=jData.getString("title");
                            shareUrl=jData.getString("address");
                            shareContent=jData.getString("content");
                            sharePictureUrl=jData.getString("image");


//                            sharePictureUrl="https://hmls.hfbank.com.cn/hfapp-api/9.png";
//                            shareUrl="http://www.99byh.com/?q=app/share&id=116118";
//                            shareTitle="我发现了一个很不错的购物应用";
//                            shareContent="不仅有专业买手可以为你省钱省时，每天还能领积分赚钱呢！";
                        }

                    } else {
                        ll_share.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                    ll_share.setVisibility(View.GONE);
                }finally {
                    fragment_youhui_rl_pb.setVisibility(View.GONE);
                }


            }
            if (msg.what == 2) {
                fragment_youhui_rl_pb.setVisibility(View.GONE);
                ll_share.setVisibility(View.GONE);
            }
        }
    };
    private void initData(){
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareActivity.this.finish();
                overridePendingTransition(R.anim.in_from_left,
                        R.anim.out_of_right);
            }
        });
        ll_qq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(BooleanThread.toService){
                    if (NetJudgeUtils.getNetConnection(ShareActivity.this)){
                        String rCode="";
                        if(!TextUtils.isEmpty((String)SharedPfUtils.getData(ShareActivity.this,"newQuanId",""))){
                            rCode=(String)SharedPfUtils.getData(ShareActivity.this,"newQuanId","");
                        }

                        ThreadPollFactory.getNormalPool().execute(new StatisticsShareThread(ShareActivity.this,2,rCode));
                    }
                }
                shareDemo(QQ.NAME);
            }
        });
        ll_qqspace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(BooleanThread.toService){
                    String rCode="";
                    if(!TextUtils.isEmpty((String)SharedPfUtils.getData(ShareActivity.this,"newQuanId",""))){
                        rCode=(String)SharedPfUtils.getData(ShareActivity.this,"newQuanId","");
                    }
                    if (NetJudgeUtils.getNetConnection(ShareActivity.this)){
                        ThreadPollFactory.getNormalPool().execute(new StatisticsShareThread(ShareActivity.this,2,rCode));
                    }
                }
                shareDemo(QZone.NAME);
            }
        });
        ll_weixin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(BooleanThread.toService){
                    String rCode="";
                    if(!TextUtils.isEmpty((String)SharedPfUtils.getData(ShareActivity.this,"newQuanId",""))){
                        rCode=(String)SharedPfUtils.getData(ShareActivity.this,"newQuanId","");
                    }
                    if (NetJudgeUtils.getNetConnection(ShareActivity.this)){
                        ThreadPollFactory.getNormalPool().execute(new StatisticsShareThread(ShareActivity.this,2,rCode));
                    }
                }
                shareDemo(Wechat.NAME);
            }
        });
        ll_weixin_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(BooleanThread.toService){
                    String rCode="";
                    if(!TextUtils.isEmpty((String)SharedPfUtils.getData(ShareActivity.this,"newQuanId",""))){
                        rCode=(String)SharedPfUtils.getData(ShareActivity.this,"newQuanId","");
                    }
                    if (NetJudgeUtils.getNetConnection(ShareActivity.this)){
                        ThreadPollFactory.getNormalPool().execute(new StatisticsShareThread(ShareActivity.this,2,rCode));
                    }
                }
                shareDemo(WechatMoments.NAME);
            }
        });
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
        MobclickAgent.onPageStart("ShareActivity");
        MobclickAgent.onResume(this);

    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("ShareActivity");
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
    public  void shareDemo(String name){
        Platform.ShareParams sp = new Platform.ShareParams();
        Platform plat = ShareSDK.getPlatform(name);
        if(name.equals(Wechat.NAME)||name.equals(WechatMoments.NAME)){
            sp.setShareType(Platform.SHARE_WEBPAGE);
          //  sp.setUrl("http://www.99byh.com/?q=app/share&id=116118");
            sp.setUrl(shareUrl);
        }else{
           //sp.setTitleUrl("http://www.99byh.com/?q=app/share&id=116118");
            sp.setTitleUrl(shareUrl);
        }
        sp.setTitle(shareTitle);
       // sp.setTitle("我发现了一个很不错的购物应用");
        sp.setImageUrl(sharePictureUrl);
        //sp.setImageUrl("https://hmls.hfbank.com.cn/hfapp-api/9.png");
        sp.setText(shareContent);
      //  sp.setText("不仅有专业买手可以为你省钱省时，每天还能领积分赚钱呢！");
        if(name.equals(QZone.NAME)){
            sp.setSite("挑一挑");
            sp.setSiteUrl("http://www.eachmobile.com.cn");
        }

        plat.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                Log.d("mainActivity", "onError ---->  分享失败" + throwable.getStackTrace().toString());
                Log.d("mainActivity", "onError ---->  分享失败" + throwable.getMessage());
            }

            @Override
            public void onCancel(Platform platform, int i) {
            }
        });
        plat.share(sp);
    }

}
