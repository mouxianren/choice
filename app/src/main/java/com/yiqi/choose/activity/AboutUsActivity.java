package com.yiqi.choose.activity;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.yiqi.choose.R;
import com.yiqi.choose.base.BaseActivity;
import com.yiqi.choose.utils.AndroidUtils;
import com.yiqi.choose.utils.SystemBarTintManager;

/**
 * Created by moumou on 17/8/11.
 */

public class AboutUsActivity extends BaseActivity {
    private LinearLayout back_btn;
   private TextView me_abus_tv_version;
    //private ImageView iv_tiao;
    private int screenWidth;

//    private LinearLayout about_tiao_h;
//    private LinearLayout ll_out_h;

    private int thigh;
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
        setContentView(R.layout.me_aboutus);
        screenWidth = AndroidUtils.getWidth(AboutUsActivity.this);
        //System.out.println("s==="+screenWidth);
//        about_tiao_h=(LinearLayout)this.findViewById(about_tiao_h);
//        ll_out_h=(LinearLayout)this.findViewById(R.id.ll_out_h);
//        iv_tiao=(ImageView)this.findViewById(R.id.iv_tiao);
//        DisplayMetrics dm = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay()
//                .getMetrics(dm);
//        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)iv_tiao
//                .getLayoutParams();
//        // 根据图片高和宽的比例计算高度
//        // 测试图宽为694 高为323
//        params.width = screenWidth/2;
//        params.height = screenWidth/26;
//        iv_tiao.setLayoutParams(params);

//        ViewTreeObserver vto = about_tiao_h.getViewTreeObserver();
//        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                about_tiao_h.getViewTreeObserver().removeGlobalOnLayoutListener(this);
//                about_tiao_h.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        thigh = about_tiao_h.getHeight();
//                        System.out.println("th==="+thigh);
//                      runOnUiThread(runable);
//                    }
//                }, 300);
//            }
//
//        });



        back_btn = (LinearLayout) this.findViewById(R.id.ll_back);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AboutUsActivity.this.finish();
                overridePendingTransition(R.anim.in_from_left,
                        R.anim.out_of_right);
            }
        });
        me_abus_tv_version=(TextView)this.findViewById(R.id.me_abus_tv_version);
        me_abus_tv_version.setText("V"+AndroidUtils.getAppVersion(AboutUsActivity.this));
    }
//    Runnable runable=new Runnable() {
//        @Override
//        public void run() {
//            LinearLayout.LayoutParams out = (LinearLayout.LayoutParams)ll_out_h
//                    .getLayoutParams();
//            out.height=thigh;
//            ll_out_h.setLayoutParams(out);
//        }
//    };
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
        MobclickAgent.onPageStart("AboutUsActivity");
        MobclickAgent.onResume(this);

    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("AboutUsActivity");
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



}
