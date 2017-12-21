package com.yiqi.choose.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.yiqi.choose.R;
import com.yiqi.choose.activity.WebActivity;
import com.yiqi.choose.model.BannerInfoNew;
import com.yiqi.choose.utils.CustomProgressDialog;
import com.yiqi.choose.utils.NetJudgeUtils;
import com.yiqi.choose.utils.PicassoUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by moumou on 17/12/18.
 */

public class ViewPagerAdapter extends PagerAdapter {
    // private AsyncLoaderImage loader;
    private LayoutInflater inflater;
    private Context context;
    private List<Object> banners;
    private Activity activity;

    public ViewPagerAdapter(Context context,List<Object> banner,Activity activity) {
        this.context = context;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(banner!=null){
            this.banners=banner;
        }else{
            banners=new ArrayList<Object>();
        }
        this.activity=activity;


    }

    @Override
    public int getCount() {

        return banners.size();
    }

    ;

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // container.removeView(bannerList.get(position));
        ((ViewPager) container).removeView((View) object);
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {

        View layout = inflater.inflate(R.layout.layout_banner_item, null);
        ImageView imageView = (ImageView) layout
                .findViewById(R.id.iv_banner_item);
        BannerInfoNew info = new BannerInfoNew();
        info = (BannerInfoNew) banners.get(position);
        PicassoUtils.loadImageWithHolderAndError(context, info.getImage(), R.mipmap.bannerpt, R.mipmap.bannerpt, imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BannerInfoNew info = (BannerInfoNew) banners.get(position);
                if(!(info.getType().equals("3"))){
                    if (!TextUtils.isEmpty(info.getUrl())) {
                        Intent intent = new Intent(context,
                                WebActivity.class);
                        intent.putExtra("title", info.getName());
                        intent.putExtra("url", info.getUrl());
                        context.startActivity(intent);
                        activity.overridePendingTransition(
                                R.anim.to_right, R.anim.to_left);
                    }else{
                        if(!TextUtils.isEmpty(info.getGoodsId())) {
                            if(NetJudgeUtils.getNetConnection(context)){
                                CustomProgressDialog.createDialog(context, "");
                                //                            turnGoodsId=info.getGoodsId();
                                //                            ThreadPollFactory.getNormalPool().execute(new TurnUrlThread(info.getGoodsId() + "", getActivity()));
                            }else{
                                // Toast.makeText(getActivity(),"您的网络不给力，请检查更新！",Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }else{
                    //tiao dao
                }


            }
        });

        ((ViewPager) container).addView(layout, 0);
        //imageView.setOnClickListener(new OnClickListener() {});
        return layout;
    }
}
