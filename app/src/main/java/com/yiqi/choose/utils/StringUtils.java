package com.yiqi.choose.utils;

import android.text.TextUtils;

/**
 * Created by moumou on 17/9/26.
 */

public class StringUtils {
    public static String getID(String numId){
        if(TextUtils.isEmpty(numId)){
            return "";
        }
        String[] arr=numId.split("_");
        return arr[0];
    }
}
