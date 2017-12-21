package com.yiqi.choose.base;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;

/**
 * Created by moumou on 17/8/10.
 */

public class BaseActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        keepFontSize();
        BaseMap.init(this);
    }
    private void keepFontSize(){
        Resources res = getResources();
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
    }

    //        int tabCount = mTabLayout.getTabCount();
    //        for (int i = 0; i < tabCount; i++) {
    //            final TabLayout.Tab tab = mTabLayout.getTabAt(i);
    //
    //            if (tab == null) {
    //                continue;
    //            }
    //            //这里使用到反射，拿到Tab对象后获取Class
    //            Class c = tab.getClass();
    //            try {
    //                //Filed “字段、属性”的意思，c.getDeclaredField 获取私有属性。
    //                //“mView”是Tab的私有属性名称，类型是 TabView ，TabLayout私有内部类。
    //                Field field = c.getDeclaredField("mView");
    //                if (field == null) {
    //                    continue;
    //                }
    //                field.setAccessible(true);
    //                final View view = (View) field.get(tab);
    //                if (view == null) {
    //                    continue;
    //                }
    //                view.setTag(i);
    //                view.setOnClickListener(new View.OnClickListener() {
    //                    @Override
    //                    public void onClick(View v) {
    //                        int position = (int) view.getTag();
    //                        //((ImageView)view).setImageResource(R.mipmap.cart_sel);
    //                       int number= ((ViewGroup)view).getChildCount();
    //                       for(int i=number-1;i>=0;i--){
    //                           System.out.println("11111");
    //                           if(((ViewGroup)view).getChildAt(i) instanceof FrameLayout){
    //                               System.out.println(((ViewGroup)view).getChildAt(i));
    //                               System.out.println("22222");
    //                               int numberzi=((FrameLayout) ((ViewGroup)view).getChildAt(i)).getChildCount();
    //                               System.out.println("333333");
    //                               for(int j=0;j<numberzi;j++){
    //                                   System.out.println(((FrameLayout) ((ViewGroup)view).getChildAt(i)).getChildAt(j));
    //                                   if(((FrameLayout) ((ViewGroup)view).getChildAt(i)).getChildAt(j) instanceof ImageView){
    //                                       ((ImageView) ((FrameLayout) ((ViewGroup)view).getChildAt(i)).getChildAt(j)).setImageResource(R.mipmap.cart);
    //                                       System.out.println("5555");
    //                                   break;
    //                                   }
    //                               }
    //                               break;
    //                           }
    //
    //                       }
    //                       // view.setBackgroundDrawable(getResources().getDrawable(R.mipmap.cart_sel));
    //
    //                        //((ImageView)((ViewGroup)view).getChildAt(2)).setImageResource(R.mipmap.cart_sel);
    //
    //                        System.out.println("position====" + ((ViewGroup)view).getChildCount());
    //                    }
    //                });
    //            } catch (NoSuchFieldException e) {
    //                e.printStackTrace();
    //            } catch (IllegalAccessException e) {
    //                e.printStackTrace();
    //            }
    //
    //        }

}
