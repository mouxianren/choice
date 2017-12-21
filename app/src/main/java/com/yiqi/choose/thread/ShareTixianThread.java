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

public class ShareTixianThread implements Runnable {
    private Handler handler;
    private Context context;
    private String code;
    private int page;
    public ShareTixianThread(Handler handler, Context context, String code, int page){
        this.handler=handler;
        this.context=context;
        this.code=code;
        this.page=page;
    }

    @Override
    public void run() {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("code",code);
            params.put("order","1");
            params.put("page",page+"");
            params = BaseMap.getMapAll(params);
            String url = context.getResources().getString(R.string.appurltest) + "/activity/tixian"+"?stamp=" + UrlUtils.getTime() + "&encode=" + UrlUtils.getEncode();
            String jsonData = HttpConBase.getjsonByPost(url, params, "utf-8");
            Message msg = Message.obtain();
            if (page <= 1) {
                msg.what =5;
                msg.obj = jsonData;
                handler.sendMessage(msg);
            }else{
                msg.what =7;
                msg.obj = jsonData;
                handler.sendMessage(msg);
            }

        } catch (Exception e) {
            e.printStackTrace();
            if (page == 1) {
                handler.sendEmptyMessage(6);
            } else {
                handler.sendEmptyMessage(8);
            }
        }
    }
}
