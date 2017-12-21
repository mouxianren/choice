package com.yiqi.choose.adapter;

import android.content.Context;
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
import com.yiqi.choose.model.GuanjianciInfo;
import com.yiqi.choose.utils.PicassoUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2017/5/16.
 */

public class GridRecyclerAdapter extends DelegateAdapter.Adapter{
    public Context context;
    private LayoutHelper helper;
    private LayoutInflater inflater;
    private int height;

    private LinearLayout.LayoutParams params;
    private OnItemClickListener mOnItemClickListener;
    private List<GuanjianciInfo.ziCategorys> list;

    public GridRecyclerAdapter(Context context, LayoutHelper helper,int height,List<GuanjianciInfo.ziCategorys> lists){
        this.inflater = LayoutInflater.from(context);
        this.helper = helper;
        this.height=height;
        if(lists!=null){
            this.list=lists;
        }else{
            list=new ArrayList<GuanjianciInfo.ziCategorys>();
        }

    }

    @Override
    public LayoutHelper onCreateLayoutHelper() {
        return this.helper;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(inflater.inflate(R.layout.list_pro_type_item1,parent,false));
    }
    public void noticeAdapter(List<GuanjianciInfo.ziCategorys> list){

        this.notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {


        MyViewHolder myViewHolder=(MyViewHolder)holder;

        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.height=height;
        myViewHolder.icon.setLayoutParams(params);

        if (list != null && list.size() > 0) {
             GuanjianciInfo.ziCategorys ziCategorys=list.get(position);
            myViewHolder.name.setText(ziCategorys.getName());
            PicassoUtils.loadImageWithHolderAndError(context, ziCategorys.getImage(), R.mipmap.picture, R.mipmap.picture, myViewHolder.icon);
        }
        myViewHolder.ll_all_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnItemClickListener!=null){
                    mOnItemClickListener.OnItemClick(position);
                }
            }
        });
//        myViewHolder.ll_all_item.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                System.out.println("11111111111");
//                if(mOnItemClickListener!=null){
//                    mOnItemClickListener.OnItemClick(position);
//                }
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
            private ImageView icon;
            private TextView name;
        private LinearLayout ll_all_in;

        public MyViewHolder(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.typeicon);
            name = (TextView) itemView.findViewById(R.id.typename);
           ll_all_in=(LinearLayout)itemView.findViewById(R.id.ll_all_in);
        }
    }
    public void setOnItemClickListenr(OnItemClickListener listener){
        this.mOnItemClickListener=listener;
    }
    public interface OnItemClickListener {
        void OnItemClick(int position);

    }

}
