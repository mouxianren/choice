package com.yiqi.choose.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.LayoutHelper;
import com.yiqi.choose.R;

/**
 * Created by admin on 2017/5/18.
 */

public class ViewAdapter extends DelegateAdapter.Adapter{
    public Context context;
    private LayoutHelper helper;
    private LayoutInflater inflater;

    public ViewAdapter(Context context, LayoutHelper helper){
        this.inflater = LayoutInflater.from(context);
        this.helper = helper;
        this.context=context;

    }

    @Override
    public LayoutHelper onCreateLayoutHelper() {
        return this.helper;
    }




    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder=new MyViewHolder(inflater.inflate(R.layout.layout_view,parent,false));

        return holder;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
       // MyViewHolder myViewHolder=(MyViewHolder)holder;




    }

    @Override
    public int getItemCount() {

            return 1;

    }

    private class MyViewHolder extends RecyclerView.ViewHolder{



        public MyViewHolder(View itemView) {
            super(itemView);


        }
    }


}
