package com.yiqi.choose.thread;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.yiqi.choose.R;
import com.yiqi.choose.activity.SplashActicity1;
import com.yiqi.choose.base.BaseMap;
import com.yiqi.choose.utils.HttpConBase;
import com.yiqi.choose.utils.UrlUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by moumou on 17/8/17.
 */

public class ClickThread implements Runnable {
    String goodsId;
    Context context;
    Handler handler;

  public ClickThread(String goodsId, Context context, Handler handler){

      this.goodsId=goodsId;
      this.context=context;
      this.handler=handler;
  }


    @Override
    public void run() {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("code", SplashActicity1.mQuanId);
            params.put("goodsId",goodsId);
            params = BaseMap.getMapAll(params);
            String url = context.getResources().getString(R.string.appurl)+"/goods/click"+"?stamp=" + UrlUtils.getTime() + "&encode=" + UrlUtils.getEncode();
            String jsonData = HttpConBase.getjsonByPost(url, params, "utf-8");
            Message msg = Message.obtain();
            msg.what=100;
            msg.obj = jsonData;
            handler.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
            handler.sendEmptyMessage(101);
        }

    }
}
