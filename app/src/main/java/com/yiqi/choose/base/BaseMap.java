package com.yiqi.choose.base;

import android.content.Context;

import com.yiqi.choose.activity.SplashActicity1;
import com.yiqi.choose.utils.AndroidUtils;
import com.yiqi.choose.utils.SharedPfUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by moumou on 17/8/10.
 */

public class BaseMap {
    public static String  channel, app_version, token;

    public static void init(Context context) {
        channel=AndroidUtils.getAppMetaData(context,"UMENG_CHANNEL");
        app_version=AndroidUtils.getAppVersion(context);
        token=(String)SharedPfUtils.getData(context,"token","");
    }

    public static Map<String, String> getMapAll(Map<String, String> allMap) {
        Map<String, String> params = new HashMap<>();
        params.put("client_id", SplashActicity1.client_id);
        params.put("device", "2");
        params.put("channel", channel);
        params.put("app_version",app_version);
        params.put("api_version","1.0");
        params.put("token",token);
        allMap.putAll(params);
        return allMap;
    }



}
