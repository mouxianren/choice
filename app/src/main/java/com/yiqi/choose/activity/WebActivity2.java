package com.yiqi.choose.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.yiqi.choose.R;
import com.yiqi.choose.base.BaseActivity;
import com.yiqi.choose.factory.ThreadPollFactory;
import com.yiqi.choose.utils.AndroidUtils;
import com.yiqi.choose.utils.HttpConBase;
import com.yiqi.choose.utils.NetJudgeUtils;
import com.yiqi.choose.utils.SharedPfUtils;
import com.yiqi.choose.utils.SystemBarTintManager;
import com.yiqi.choose.utils.UrlUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import static com.yiqi.choose.base.BaseMap.app_version;

public class WebActivity2 extends BaseActivity {
    private WebView mWebView;
    private String url1;
    private ImageView iv_close;
    private View btn_back;
    private CookieManager cookieManager;// Cookie管理器

    private TextView appTitle_txt;
    // 关于的网址链接
    private ProgressBar progressBar1;
    // private LinearLayout ll_webView;
    private String title;


    private LinearLayout home_jinxuan_ll_noweb;
    private TextView home_jixuan_tv_try;
    private RelativeLayout home_jinxuan_rl_pg;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        // 取消标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.topcolorred);//通知栏所需颜色
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_web);
        Intent intent = this.getIntent();
        url1 = intent.getStringExtra("url");
        title = intent.getStringExtra("title");
        try {
            title= URLDecoder.decode(title,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            title="手机淘宝";
        }

        iv_close = (ImageView) this.findViewById(R.id.close);
        progressBar1 = (ProgressBar) this.findViewById(R.id.progressBar1);
        appTitle_txt = (TextView) findViewById(R.id.appTitle_txt);
        appTitle_txt.setText("粉丝福利购");
        btn_back = this.findViewById(R.id.back_btn);
        home_jinxuan_ll_noweb = (LinearLayout) this.findViewById(R.id.home_jinxuan_ll_noweb);
        home_jixuan_tv_try = (TextView) this.findViewById(R.id.home_fragment_tv_retro);
        home_jinxuan_rl_pg = (RelativeLayout) this.findViewById(R.id.home_jinxuan_rl_pg);


        iv_close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WebActivity2.this, MainAcitivity1.class);
                startActivity(intent);
                WebActivity2.this.finish();
                overridePendingTransition(R.anim.in_from_left,
                        R.anim.out_of_right);


            }
        });
        initView();
        initSetting();
        initData();


        if (NetJudgeUtils.getNetConnection(WebActivity2.this)) {
            mWebView.setVisibility(View.GONE);
            home_jinxuan_ll_noweb.setVisibility(View.GONE);
            home_jinxuan_rl_pg.setVisibility(View.VISIBLE);
            ThreadPollFactory.getNormalPool().execute(new TurnUrlThread(url1, WebActivity2.this));

        } else {
            mWebView.setVisibility(View.GONE);
            home_jinxuan_ll_noweb.setVisibility(View.VISIBLE);
            home_jinxuan_rl_pg.setVisibility(View.GONE);
        }
        home_jinxuan_rl_pg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetJudgeUtils.getNetConnection(WebActivity2.this)) {
                    mWebView.setVisibility(View.GONE);
                    home_jinxuan_ll_noweb.setVisibility(View.GONE);
                    home_jinxuan_rl_pg.setVisibility(View.VISIBLE);
                    ThreadPollFactory.getNormalPool().execute(new TurnUrlThread(url1, WebActivity2.this));

                } else {
                    mWebView.setVisibility(View.GONE);
                    home_jinxuan_ll_noweb.setVisibility(View.VISIBLE);
                    home_jinxuan_rl_pg.setVisibility(View.GONE);
                }
            }
        });


        btn_back.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (mWebView.canGoBack()) {
                    // if (surUrl.equals(firsturl)) {
                    // MainActivity.this.finish();
                    // overridePendingTransition(R.anim.in_from_left,R.anim.out_of_right);
                    // }
                    mWebView.goBack();
                } else {
                    Intent intent = new Intent(WebActivity2.this, MainAcitivity1.class);
                    startActivity(intent);
                    WebActivity2.this.finish();
                    overridePendingTransition(R.anim.in_from_left,
                            R.anim.out_of_right);


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

    /**
     * 初始化UI组件
     */
    public void initView() {
        mWebView = (WebView) findViewById(R.id.webviewContent);
    }

    // // Press the back button in mobile phone
    // @Override
    // public void onBackPressed() {
    // super.onBackPressed();
    // overridePendingTransition(0, R.anim.base_slide_right_out);
    // }
    // 友盟统计的时候
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("WebActivity2");
        MobclickAgent.onResume(this);

    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("WebActivity2");
        MobclickAgent.onPause(this);
    }

    /**
     * UI组件配置(包括事件监听等)
     */
    public void initSetting() {

        // btnClose.setOnClickListener(btnClickListener);

        mWebView.requestFocus();
        // mWebView.setBackgroundColor(Color.TRANSPARENT);
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        BrowserClient mBorwserClient = new BrowserClient();
       mWebView.setWebViewClient(mBorwserClient);
        mWebView.setDownloadListener(new BrowserDownloadListener());
        WebSettings mWebSetting = mWebView.getSettings();
        mWebSetting.setSavePassword(false);
        mWebSetting.setSaveFormData(false);
        mWebSetting.setSupportZoom(true);
        // mWebSetting.setBlockNetworkImage(true); //要不要加载图片在webView中
        mWebSetting.setJavaScriptEnabled(true);
        mWebSetting.setJavaScriptCanOpenWindowsAutomatically(true);
        // mWebSetting.setJavaScriptCanOpenWindowsAutomatically(false);
        mWebSetting.setBuiltInZoomControls(false);// 设置WebView可触摸放大缩小
        mWebSetting.setUseWideViewPort(false);// WebView双击变大，再双击后变小，当手动放大后，双击可以恢复到原始大小
        mWebSetting.setDomStorageEnabled(true);

    }

    /**
     * 数据内容加载
     */
    public void initData() {

        CookieSyncManager.createInstance(this);
        cookieManager = CookieManager.getInstance();
        String cookieValue = cookieManager.getCookie(url1);
        cookieManager.setCookie(url1, cookieValue);
        CookieSyncManager.getInstance().sync();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Intent intent = new Intent(WebActivity2.this, MainAcitivity1.class);
            startActivity(intent);
            WebActivity2.this.finish();
            overridePendingTransition(R.anim.in_from_left,
                    R.anim.out_of_right);


            return true;
        }

        return super.onKeyDown(keyCode, event);
    }


    /**
     * 浏览器代理类
     *
     * @author Alawn
     */
    private class BrowserClient extends WebViewClient {
        private static final String TAG = "BrowserClient";

        /**
         * 点击新链接时在当前页打开，而不去新的浏览器打开
         */
        // @Override
        // public boolean shouldOverrideUrlLoading(WebView view, String url) {
        // // Log.d(TAG, "加载新链接");
        // // if (url.contains("rtsp")) {
        // // AndroidUtils.turnToFlowVideo(MainActivity.this, url);
        // //// CustomProgressDialog.stopProgressDialog();
        // // // BrowserActivity.this.closeLoading();
        // // } else {
        // view.loadUrl(url);
        // // }
        // return true;
        // }
        //

        /**
         * 监控页面开始加载，可开始加载进度条效果
         */
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // Log.d(TAG, "加载开始");
            // CustomProgressDialog.createDialog(MainActivity.this, "内容加载中...");
            progressBar1.setVisibility(View.VISIBLE);
            super.onPageStarted(view, url, favicon);
        }

        /**
         * 监控页面加载是否完成，可结束加载进度条效果
         */
        @Override
        public void onPageFinished(WebView view, String url) {
            // Log.d(TAG, "加载结束");
            // CustomProgressDialog.stopProgressDialog();
            progressBar1.setVisibility(View.INVISIBLE);
            // BrowserActivity.this.closeLoading();
            // BrowserActivity.this.btnGoForward.setEnabled(view.canGoForward());
            // MainActivity.this.btnGoForward.setEnabled(view.canGoForward());
            // MainActivity.this.btnGoBack.setEnabled(view.canGoBack());

        }

        /**
         * 错误处理
         */
        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            // Log.d(TAG, "加载出错");
            // CustomProgressDialog.stopProgressDialog();
            // BrowserActivity.this.closeLoading();
            //  Toast.makeText(WebActivity.this, "网络加载出错~", Toast.LENGTH_SHORT).show();
            super.onReceivedError(view, errorCode, description, failingUrl);
        }

    }

    /**
     * url 下载地址 userAgent 代理 contentDisposition mimetype contentLength 下载文件大小
     *
     * @author Administrator
     */
    private class BrowserDownloadListener implements DownloadListener {
        public void onDownloadStart(String url, String userAgent,
                                    String contentDisposition, String mimetype, long contentLength) {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
            overridePendingTransition(R.anim.to_right, R.anim.to_left);
        }
    }

    public class TurnUrlThread implements Runnable {
        String goodsId;
        Context context;

        public TurnUrlThread(String goodsId, Context context) {

            this.goodsId = goodsId;
            this.context = context;
        }


        @Override
        public void run() {
            try {
                Map<String, String> params = new HashMap<>();
                params.put("code", SplashActicity1.mQuanId);
                params.put("goodsId", goodsId);
                params.put("client_id", SplashActicity1.client_id);
                params.put("device", "2");
                params.put("channel", AndroidUtils.getAppVersion(context));
                params.put("app_version",app_version);
                params.put("api_version","1.0");
                params.put("token",(String) SharedPfUtils.getData(context,"token",""));
                String url = context.getResources().getString(R.string.appurl) + "/goods/turnUrl"+"?stamp=" + UrlUtils.getTime() + "&encode=" + UrlUtils.getEncode();
                String jsonData = HttpConBase.getjsonByPost(url, params, "utf-8");
                Message msg = Message.obtain();
                msg.what = 11;
                msg.obj = jsonData;
                hd.sendMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
                hd.sendEmptyMessage(12);
            }

        }
    }

    private Handler hd = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 11) {
                try {
                    JSONObject j = new JSONObject((String) msg.obj);
                    String code = j.getString("code");
                    if (code.equals("0")) {
                        String data = j.getString("data");
                        mWebView.setVisibility(View.VISIBLE);
                        home_jinxuan_ll_noweb.setVisibility(View.GONE);
                        home_jinxuan_rl_pg.setVisibility(View.GONE);
                         mWebView.loadUrl(data);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(WebActivity2.this, "服务器错误!", Toast.LENGTH_LONG).show();
                    mWebView.setVisibility(View.VISIBLE);
                    home_jinxuan_ll_noweb.setVisibility(View.GONE);
                    home_jinxuan_rl_pg.setVisibility(View.GONE);
                }
            }
            if (msg.what == 12) {
                Toast.makeText(WebActivity2.this, "服务器错误!", Toast.LENGTH_LONG).show();
                mWebView.setVisibility(View.VISIBLE);
                home_jinxuan_ll_noweb.setVisibility(View.GONE);
                home_jinxuan_rl_pg.setVisibility(View.GONE);


            }
        }
    };
    // /**
    // * 按钮点击事件
    // *
    // * @author Alawn
    // */
    // private class ButtonClickListener implements OnClickListener {
    //
    // public void onClick(View v) {
    // if (v == btnGoBack) {
    // if (mWebView.canGoBack()) {
    // mWebView.goBack();
    // btnGoForward.setEnabled(mWebView.canGoForward());
    // btnGoBack.setEnabled(mWebView.canGoBack());
    // }
    // } else if (v == btnGoForward) {
    // if (mWebView.canGoForward()) {
    // mWebView.goForward();
    // btnGoForward.setEnabled(mWebView.canGoForward());
    // btnGoBack.setEnabled(mWebView.canGoBack());
    // }
    // } else if (v == btnRefresh) {
    // mWebView.reload();
    // btnGoForward.setEnabled(mWebView.canGoForward());
    // }
    // }
    //
    // }


}
