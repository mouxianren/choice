package com.yiqi.choose.thread;

import android.content.Context;

import com.yiqi.choose.R;
import com.yiqi.choose.base.BaseMap;
import com.yiqi.choose.utils.HttpConBase;
import com.yiqi.choose.utils.UrlUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by moumou on 17/12/21.
 */

public class StatisticsTypeThread implements Runnable{
    /**
     *1：9.9包邮
     2：Top100
     3：热销榜
     4：淘抢货
     5：巨划算
     6：搜索栏
     7：分类
     */
    private Context context;
    private String classifyId;
    private String classifyName;
    public StatisticsTypeThread(Context context, String classifyId,String classifyName){
        this.context=context;
        this.classifyId=classifyId;
        this.classifyName=classifyName;
    }
    @Override
    public void run() {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("classifyId",classifyId+"");
            params.put("classifyName",classifyName+"");
            params = BaseMap.getMapAll(params);
            String url = "";
            url = context.getString(R.string.appurltest) + "/count/classifyclick" + "?stamp=" + UrlUtils.getTime() + "&encode=" + UrlUtils.getEncode();
            String jsonData = HttpConBase.getjsonByPost(url, params, "utf-8");
            System.out.println("StatisticsTypeThread="+jsonData+"---classifyId="+classifyId+"--name="+classifyName);
        } catch (Exception e) {
            e.printStackTrace();

        }

    }
}
