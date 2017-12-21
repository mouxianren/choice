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
import com.yiqi.choose.model.GoodsInfo;
import com.yiqi.choose.utils.PicassoUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2017/5/16.
 */

public class DelegateRecyclerAdapter3 extends DelegateAdapter.Adapter{
    public Context context;
    private LayoutHelper helper;
    private LayoutInflater inflater;
    private LinearLayout.LayoutParams params;
    private OnItemClickListener mOnItemClickListener;
    private List<Object> list;
    List<Object> ziGoodList;
    public DelegateRecyclerAdapter3(Context context, LayoutHelper helper, LinearLayout.LayoutParams params, List<Object> lists){
        this.inflater = LayoutInflater.from(context);
        this.helper = helper;
        this.context=context;
        this.params=params;
        this.list=lists;

         ziGoodList=new ArrayList<Object>();
    }

    @Override
    public LayoutHelper onCreateLayoutHelper() {
        return this.helper;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(inflater.inflate(R.layout.goodnew_item_1,parent,false));
    }
    public void noticeAdapter(List<Object> goodsList){
        this.list.clear();
        this.list.addAll(goodsList);
        this.notifyDataSetChanged();
    }
    public void noticeAdapterNext(List<Object> goodsList){
        this.ziGoodList.clear();
        this.ziGoodList=goodsList;
        this.list.addAll(ziGoodList);
        this.notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        MyViewHolder myViewHolder=(MyViewHolder)holder;
        if(list.size()>0){
            final GoodsInfo goodsInfo;
            goodsInfo = (GoodsInfo) list.get(position);

            myViewHolder.good_img.setLayoutParams(params);
            PicassoUtils.loadImageWithHolderAndError(context, goodsInfo.getGoodsImage(), R.mipmap.picture, R.mipmap.picture, myViewHolder.good_img);
            myViewHolder.goods_desc.setText(goodsInfo.getTitle());
            myViewHolder.goods_discount_price.setText(goodsInfo.getPrice());
            // holder.goods_ori_price.setText("￥" + goodsInfo.getOldPrice());
            myViewHolder.tv_chengji.setText("已售" + goodsInfo.getSellCount() + "件");
            myViewHolder.home_temai_discount.setText(goodsInfo.getSavePrice());
            myViewHolder.home_temai_shopname.setText(goodsInfo.getGoodsShop());
            myViewHolder.ll_all_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mOnItemClickListener!=null){
                        mOnItemClickListener.OnItemClick(position);
                    }
                }
            });
            myViewHolder.ll_all_item.setVisibility(View.VISIBLE);
            myViewHolder.ll_item.setVisibility(View.GONE);
        }else{
            myViewHolder.ll_all_item.setVisibility(View.GONE);
            myViewHolder.ll_item.setVisibility(View.VISIBLE);
        }


    }

    @Override
    public int getItemCount() {
        if(list.size()>0){
            return list.size();
        }else{
            return 1;
        }


    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView good_img;
        TextView goods_desc;
        TextView goods_discount_price;
        //    TextView goods_ori_price;
        TextView tv_chengji;
        TextView home_temai_shopname;
        TextView home_temai_discount;
        LinearLayout ll_all_item;
        LinearLayout ll_item;

        public MyViewHolder(View itemView) {
            super(itemView);
           good_img = (ImageView) itemView.findViewById(R.id.good_img);
           goods_desc = (TextView) itemView.findViewById(R.id.goods_desc);
           goods_discount_price = (TextView) itemView.findViewById(R.id.goods_discount_price);
            // holder.goods_ori_price = (TextView) view.findViewById(R.id.goods_ori_price);
           tv_chengji = (TextView) itemView.findViewById(R.id.tv_chengji);
           home_temai_shopname = (TextView) itemView.findViewById(R.id.home_temai_shopname);
           home_temai_discount = (TextView) itemView.findViewById(R.id.home_temai_discount);
            ll_all_item=(LinearLayout)itemView.findViewById(R.id.ll_all_item);
            //                holder.goods_ori_price.getPaint().setFlags(

        ll_item=(LinearLayout)itemView.findViewById(R.id.ll_item);
        }
    }
    public void setOnItemClickListenr(OnItemClickListener listener){
        this.mOnItemClickListener=listener;
    }
    public interface OnItemClickListener {
        void OnItemClick(int position);

    }

}
