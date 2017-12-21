package com.yiqi.choose.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by moumou on 17/8/10.
 */

public class NetJudgeUtils {
    /**
     * 取当前网络状态
     *
     * @return
     */
    public static boolean getNetConnection(Context context) {
        return isConnectInternet(context);
    }

    /**
     * 判断网络是否正常
     *
     * @return
     */
    public static boolean isConnectInternet(Context context) {
        ConnectivityManager conManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
        if (networkInfo != null) { // 注意，这个判断一定要的哦，要不然会出错
            return networkInfo.isAvailable();
        }
        return false;
    }
}
