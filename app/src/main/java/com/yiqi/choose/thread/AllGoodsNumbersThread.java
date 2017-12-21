package com.yiqi.choose.thread;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.yiqi.choose.R;
import com.yiqi.choose.base.BaseMap;
import com.yiqi.choose.utils.HttpConBase;
import com.yiqi.choose.utils.UrlUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by moumou on 17/12/11.
 */

public class AllGoodsNumbersThread implements Runnable {
    private Handler handler;
    private Context context;

    public AllGoodsNumbersThread(Handler handler, Context context){
        this.handler=handler;
        this.context=context;

    }

    @Override
    public void run() {
        try {
            Map<String, String> params = new HashMap<>();
            params = BaseMap.getMapAll(params);
            String url = context.getResources().getString(R.string.appurltest) + "/count/goodscount"+"?stamp=" + UrlUtils.getTime() + "&encode=" + UrlUtils.getEncode();
            String jsonData = HttpConBase.getjsonByPost(url, params, "utf-8");
            Message msg = Message.obtain();
            msg.what =999;
            msg.obj = jsonData;
            handler.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();

        }
    }
}
