package com.yiqi.choose.thread;

import android.content.Context;

import com.yiqi.choose.R;
import com.yiqi.choose.activity.SplashActicity1;
import com.yiqi.choose.base.BaseMap;
import com.yiqi.choose.utils.HttpConBase;
import com.yiqi.choose.utils.UrlUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by moumou on 17/12/21.
 */

public class StatisticsShareThread implements Runnable{
//    1：分享赚点击
//    2：app分享数
    private Context context;
    private int type;

    public StatisticsShareThread(Context context,int type){
        this.context=context;
        this.type=type;
    }
    @Override
    public void run() {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("type",type+"");
            params.put("code", SplashActicity1.mQuanId);
            params = BaseMap.getMapAll(params);
            String url = "";
            url = context.getString(R.string.appurltest) + "/count/actionclick" + "?stamp=" + UrlUtils.getTime() + "&encode=" + UrlUtils.getEncode();
            String jsonData = HttpConBase.getjsonByPost(url, params, "utf-8");
            System.out.println("StatisticsShareThread="+jsonData+"---type="+type);
        } catch (Exception e) {
            e.printStackTrace();

        }

    }
}
