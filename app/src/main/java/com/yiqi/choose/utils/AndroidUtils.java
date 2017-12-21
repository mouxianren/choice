package com.yiqi.choose.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * Created by moumou on 17/8/10.
 */

public class AndroidUtils {
    public static String getAppVersion(Context context) {
        PackageManager packageManager = context.getPackageManager();
        String version = "1.0";
        try {
            PackageInfo packInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            version = packInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            version = "1.0";
        }
        return version;
    }
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
    public static void hideInput(Context context, EditText mMessageEditText){
        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {

            imm.hideSoftInputFromWindow(mMessageEditText.getWindowToken(), 0);//隐藏(mMessageEditText.getWindowToken(), 0);//显示

        }
    }
    //是否包含空格
    public static boolean hasBlank(String str){
        if(str.startsWith(" ")||str.endsWith(" ")){
            return true;
        }else{
            String s[] = str.split(" +");
            if(s.length==1){
                return false;
            }else{
                return true;
            }
        }

    }
    public static boolean isTopActivity(Context context,String activity)
    {
        ActivityManager am = (ActivityManager)context.getSystemService(ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        return cn.getClassName().contains(activity);
    }

    // 手机的mck值
    public static String getIeme(Context context) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            String imei = telephonyManager.getDeviceId();
            String m_szAndroidID = Settings.Secure.getString(
                    context.getContentResolver(), Settings.Secure.ANDROID_ID);
            if (imei.equals("") || imei.equals("Unknown") || null == imei) {
                return m_szAndroidID;
            } else {
                return imei;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        }

    }
    public static int getWidth(Context context){
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        return width;
    }
    public static int getHeight(Context context){
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        return height;
    }

    public static void printlnMap(Map<String,String> hashmap){

        Set set=hashmap.entrySet();

        Iterator iterator=set.iterator();

        while (iterator.hasNext()){

            Map.Entry  mapentry = (Map.Entry) iterator.next();


        }
    }

    public  static  String getAndroidId(Context context){

        String m_szAndroidID = Settings.Secure.getString(
                context.getContentResolver(), Settings.Secure.ANDROID_ID);
        return m_szAndroidID;
    }
    /**
     * 获取application中指定的meta-data
     * @return 如果没有获取成功(没有对应值，或者异常)，则返回值为空
     */
    public static String getAppMetaData(Context ctx, String key) {
        if (ctx == null || TextUtils.isEmpty(key)) {
            return null;
        }
        String resultData = null;
        try {
            PackageManager packageManager = ctx.getPackageManager();
            if (packageManager != null) {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
                if (applicationInfo != null) {
                    if (applicationInfo.metaData != null) {

                        resultData =applicationInfo.metaData.getString(key);
                    }
                }

            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return resultData;
    }
}
