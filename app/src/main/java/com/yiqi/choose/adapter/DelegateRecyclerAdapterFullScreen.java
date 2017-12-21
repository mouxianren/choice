package com.yiqi.choose.adapter;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.LayoutHelper;
import com.yiqi.choose.R;

/**
 * Created by admin on 2017/5/16.
 */

public class DelegateRecyclerAdapterFullScreen extends DelegateAdapter.Adapter{
    public Context context;
    private LayoutHelper helper;
    private LayoutInflater inflater;
    private int height;

    public DelegateRecyclerAdapterFullScreen(Context context, LayoutHelper helper,int height){
        this.inflater = LayoutInflater.from(context);
        this.helper = helper;
        this.context=context;
        this.height=height;
    }

    @Override
    public LayoutHelper onCreateLayoutHelper() {
        return this.helper;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder=new MyViewHolder(inflater.inflate(R.layout.layout_item_fullscreen,parent,false));
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)holder.ll_item
                .getLayoutParams();
       params.height=height;
        holder.itemView.setLayoutParams(params);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    MyViewHolder myViewHolder=(MyViewHolder)holder;
        AnimationDrawable animationDrawable = (AnimationDrawable) myViewHolder.iv.getBackground();
        animationDrawable.start();

    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView name;
        public LinearLayout ll_item;
        private ImageView iv;
        public MyViewHolder(View itemView) {
            super(itemView);
            ll_item=(LinearLayout)itemView.findViewById(R.id.ll_item);
            iv=(ImageView)itemView.findViewById(R.id.iv);
        }
    }

}
