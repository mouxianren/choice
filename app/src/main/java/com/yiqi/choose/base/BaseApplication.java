package com.yiqi.choose.base;

import com.mob.MobApplication;

/**
 * Created by moumou on 17/8/2.
 */

public class BaseApplication extends MobApplication {
    public static BaseApplication application = null;
    @Override
    public void onCreate() {
        super.onCreate();
       //MobclickAgent.setDebugMode(true);
        application=this;
//        //检测内存泄漏的LeakCanary的初始化
//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            // This process is dedicated to LeakCanary for heap analysis.
//            // You should not init your app in this process.
//            return;
//        }
//        LeakCanary.install(this);

//        final OkHttpClient client = new OkHttpClient.Builder()
//                .protocols(Collections.singletonList(Protocol.HTTP_1_1))
//                .build();
//
//        final Picasso picasso = new Picasso.Builder(this)
//                .downloader(new OkHttp3Downloader(client))
//                .build();
//
//        Picasso.setSingletonInstance(picasso);


//        AlibcTradeSDK.asyncInit(this, new AlibcTradeInitCallback() {
//            @Override
//            public void onSuccess() {
//                //初始化成功，设置相关的全局配置参数
//               // System.out.println("11111");
//                // ...
//            // AlibcTradeCommon.turnOnDebug();
//            }
//
//            @Override
//            public void onFailure(int code, String msg) {
//                //初始化失败，可以根据code和msg判断失败原因，详情参见错误说明
//            }
//        });
    }
}
