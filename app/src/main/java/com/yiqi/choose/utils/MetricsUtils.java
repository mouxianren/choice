package com.yiqi.choose.utils;

import android.app.Activity;
import android.util.DisplayMetrics;

/**
 * Created by moumou on 17/12/11.
 */

public class MetricsUtils {
    public static int getType(Activity context){
        if(getDensity(context)==0){
            return 2;
        }
        if(getDensity(context)<=1){
            return 1;
        }else if (getDensity(context)<=2&&getDensity(context)>1){
            return 2;
        }else if(getDensity(context)>2){
            return 3;
        }else{
            return 2;
        }
    }
    public  static float getDensity(Activity context){
        DisplayMetrics metrics=new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.density;
    }
}
