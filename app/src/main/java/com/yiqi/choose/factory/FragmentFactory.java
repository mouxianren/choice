package com.yiqi.choose.factory;

import android.support.v4.app.Fragment;
import android.support.v4.util.SparseArrayCompat;

import com.yiqi.choose.base.BaseFragment1_coupons;
import com.yiqi.choose.fragment.Home_zi_fragment;

/**
 * Created by moumou on 17/8/15.
 */

public class FragmentFactory {
    public static SparseArrayCompat<BaseFragment1_coupons> cachesFragment=new SparseArrayCompat<BaseFragment1_coupons>();
    public static Fragment getFragment(int position, String id){
        BaseFragment1_coupons fragment=null;
        BaseFragment1_coupons temFragment=cachesFragment.get(position);
        if(temFragment!=null){
            fragment=temFragment;
            return fragment;
        }

        fragment =new Home_zi_fragment();

        cachesFragment.put(position, fragment);
        return fragment;
    }




}
