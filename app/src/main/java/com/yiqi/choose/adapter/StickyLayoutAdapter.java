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

/**
 * Created by admin on 2017/5/17.
 */

public class StickyLayoutAdapter extends DelegateAdapter.Adapter {
    public Context context;
    private LayoutHelper helper;
    private LayoutInflater inflater;
    private OnItemClickListener mOnItemClickListener;
    private int order;

    public StickyLayoutAdapter(Context context, LayoutHelper helper) {
        this.inflater = LayoutInflater.from(context);
        this.helper = helper;
        this.context = context;
        order=0;
    }

    @Override
    public LayoutHelper onCreateLayoutHelper() {
        return this.helper;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(inflater.inflate(R.layout.home_index_type, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MyViewHolder holder2 = (MyViewHolder) holder;
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView mAllSearchTvQuanbu;
        LinearLayout mAllSearchLlQuanbu;
        TextView mAllSearchTvSeller;
        LinearLayout mAllSearchLlSeller;
        TextView mAllSearchTvPrice;
        ImageView mAllSearchIvPrice;
        LinearLayout mAllSearchLlPrice;

        public MyViewHolder(View itemView) {
            super(itemView);
            mAllSearchTvQuanbu = (TextView) itemView.findViewById(R.id.all_search_tv_quanbu);
            mAllSearchLlQuanbu = (LinearLayout) itemView.findViewById(R.id.all_search_ll_quanbu);
            mAllSearchTvSeller = (TextView) itemView.findViewById(R.id.all_search_tv_seller);
            mAllSearchLlSeller = (LinearLayout) itemView.findViewById(R.id.all_search_ll_seller);
            mAllSearchTvPrice = (TextView) itemView.findViewById(R.id.all_search_tv_price);
            mAllSearchIvPrice = (ImageView) itemView.findViewById(R.id.all_search_iv_price);
            mAllSearchLlPrice = (LinearLayout) itemView.findViewById(R.id.all_search_ll_price);

            mAllSearchLlQuanbu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mAllSearchTvQuanbu.setTextColor(context.getResources().getColor(R.color.red));
                    mAllSearchTvSeller.setTextColor(context.getResources().getColor(R.color.newzigrey));
                    mAllSearchTvPrice.setTextColor(context.getResources().getColor(R.color.newzigrey));
                    mAllSearchIvPrice.setImageDrawable(context.getResources().getDrawable(R.mipmap.price_nor));
                    order=0;
if(mOnItemClickListener!=null)
                mOnItemClickListener.quanbuClick(order);
                }
            });
            mAllSearchLlSeller.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    order=1;
                    mAllSearchTvSeller.setTextColor(context.getResources().getColor(R.color.red));
                    mAllSearchTvQuanbu.setTextColor(context.getResources().getColor(R.color.newzigrey));
                    mAllSearchTvPrice.setTextColor(context.getResources().getColor(R.color.newzigrey));
                    mAllSearchIvPrice.setImageDrawable(context.getResources().getDrawable(R.mipmap.price_nor));
                    if(mOnItemClickListener!=null)
                    mOnItemClickListener.sellerClick(order);
                }
            });
            mAllSearchLlPrice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (order != 3) {
                        order = 3;
                        mAllSearchTvQuanbu.setTextColor(context.getResources().getColor(R.color.newzigrey));
                        mAllSearchTvPrice.setTextColor(context.getResources().getColor(R.color.red));
                        mAllSearchTvSeller.setTextColor(context.getResources().getColor(R.color.newzigrey));
                        mAllSearchIvPrice.setImageDrawable(context.getResources().getDrawable(R.mipmap.price_up));
                    } else {
                        order = 2;
                        mAllSearchTvQuanbu.setTextColor(context.getResources().getColor(R.color.newzigrey));
                        mAllSearchTvPrice.setTextColor(context.getResources().getColor(R.color.red));
                        mAllSearchTvSeller.setTextColor(context.getResources().getColor(R.color.newzigrey));
                        mAllSearchIvPrice.setImageDrawable(context.getResources().getDrawable(R.mipmap.price_down));
                    }
                    if(mOnItemClickListener!=null)
                    mOnItemClickListener.priceClick(order);
                }
            });
        }
    }

    public interface OnItemClickListener {
      void quanbuClick(int order);
        void sellerClick(int order);
        void priceClick(int order);
    }
}
