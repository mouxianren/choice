package com.yiqi.choose.thread;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.yiqi.choose.R;
import com.yiqi.choose.utils.HttpConBase;
import com.yiqi.choose.utils.UrlUtils;

/**
 * Created by moumou on 17/12/12.
 */

public class BannerThread implements Runnable{
private Handler handler;
    private Context context;
    public BannerThread(Context context, Handler handler){
        this.handler=handler;
        this.context=context;
    }

        @Override
        public void run() {
            try {
                String url = context.getResources().getString(R.string.appurltest) + "/advertisement"+"?stamp=" + UrlUtils.getTime() + "&encode=" + UrlUtils.getEncode();
                String jsonData = HttpConBase.sendGet(url);

                Message msg = Message.obtain();
                msg.what = 1;
                msg.obj = jsonData;
                handler.sendMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
                handler.sendEmptyMessage(2);
            }


    }
}
