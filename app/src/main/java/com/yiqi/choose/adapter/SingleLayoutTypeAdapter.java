package com.yiqi.choose.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.LayoutHelper;
import com.yiqi.choose.R;

/**
 * Created by admin on 2017/5/18.
 */

public class SingleLayoutTypeAdapter extends DelegateAdapter.Adapter{
    public Context context;
    private LayoutHelper helper;
    private LayoutInflater inflater;
    private int width;
    private OnItemClickListener mOnItemClickListener;

    public SingleLayoutTypeAdapter(Context context, LayoutHelper helper, int width){
        this.inflater = LayoutInflater.from(context);
        this.helper = helper;
        this.context=context;
        this.width=width;

    }

    @Override
    public LayoutHelper onCreateLayoutHelper() {
        return this.helper;
    }




    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder=new MyViewHolder(inflater.inflate(R.layout.home_index_fivetype,parent,false));
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)holder.ll_type_three
                .getLayoutParams();
        LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams)holder.ll_type_two
                .getLayoutParams();
        // 根据图片高和宽的比例计算高度
        // 测试图宽为694 高为323
        params.width = width;
        params.height = (int)(width*(0.4278));
        holder.ll_type_three.setLayoutParams(params);

        params1.width=width;
        params1.height=(int)(width*0.2139);
        holder.ll_type_two.setLayoutParams(params1);
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
        private LinearLayout ll_type_three;
        private LinearLayout ll_type_two;
        private ImageView youhui_renqi;
        private ImageView youhui_taoqh;
        private ImageView youhui_jhs;
        private ImageView iv_chaozhi;
        private ImageView iv_rexiao;


        public MyViewHolder(View itemView) {
            super(itemView);
            ll_type_three=(LinearLayout)itemView.findViewById(R.id.ll_type_three);
            ll_type_two=(LinearLayout)itemView.findViewById(R.id.ll_type_two);
            youhui_renqi=(ImageView)itemView.findViewById(R.id.youhui_renqi);
            youhui_taoqh=(ImageView)itemView.findViewById(R.id.youhui_tqh);
            youhui_jhs=(ImageView)itemView.findViewById(R.id.youhui_jhs);
            iv_chaozhi=(ImageView)itemView.findViewById(R.id.iv_chaozhi);
            iv_rexiao=(ImageView)itemView.findViewById(R.id.iv_rexiao);


          iv_chaozhi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mOnItemClickListener!=null){
                        mOnItemClickListener.jkjClick();
                    }
                }
            });
          youhui_jhs.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mOnItemClickListener!=null){
                        mOnItemClickListener.jhsClick();
                    }
                }
            });
           youhui_taoqh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mOnItemClickListener!=null){
                        mOnItemClickListener.taoqhClick();
                    }
                }
            });
          iv_rexiao.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mOnItemClickListener!=null){
                        mOnItemClickListener.hotClick();
                    }
                }
            });
            youhui_renqi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mOnItemClickListener!=null){
                        mOnItemClickListener.onTopOneClick();
                    }
                }
            });

        }
    }
    public void setOnItemClickListenr(OnItemClickListener listener){
        this.mOnItemClickListener=listener;
    }
    public interface OnItemClickListener {
        void onTopOneClick();
        void hotClick();
        void taoqhClick();
        void jkjClick();
        void jhsClick();

    }

}
