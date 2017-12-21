package com.yiqi.choose.factory;

import android.support.v4.util.SparseArrayCompat;

import com.yiqi.choose.base.BaseFragment1_coupons;
import com.yiqi.choose.fragment.Youhui_zi_new_fragment_1;
import com.yiqi.choose.fragment.Youhui_zi_new_fragment_2;

/**
 * Created by moumou on 17/8/15.
 */

public class FragmentFactoryYouhui {
    public static SparseArrayCompat<BaseFragment1_coupons> cachesFragment=new SparseArrayCompat<BaseFragment1_coupons>();
    public static BaseFragment1_coupons getFragment(int position, String id){
        BaseFragment1_coupons fragment=null;
        BaseFragment1_coupons temFragment=cachesFragment.get(position);
        if(temFragment!=null){
            fragment=temFragment;
            return fragment;
        }
        if(position==0){
            fragment =new Youhui_zi_new_fragment_1();
        }else{
            fragment =new Youhui_zi_new_fragment_2();
        }
        cachesFragment.put(position, fragment);
        return fragment;
    }




}
