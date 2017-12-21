package com.yiqi.choose.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.LayoutHelper;
import com.yiqi.choose.R;
import com.yiqi.choose.fragment.Youhui_zi_new_fragment_1;
import com.yiqi.choose.view.MyViewPage2;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by admin on 2017/5/18.
 */

public class SingleLayoutAdapter extends DelegateAdapter.Adapter{
    public Context context;
    private LayoutHelper helper;
    private LayoutInflater inflater;
    private int width;

    int pageCount;
    private ViewPagerAdapter viewPagerAdapter;
    private Activity acitivty;
    private OnItemClickListener mOnItemClickListener;
    private BannerTimerTask bannerTimerTask;
    private Timer bannerTimer;
    public MyViewPage2 vp;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            vp.setCurrentItem(msg.what);
            super.handleMessage(msg);

        }
    };
    public SingleLayoutAdapter(Context context, LayoutHelper helper,int width,Activity activity){
        this.inflater = LayoutInflater.from(context);
        this.helper = helper;
        this.context=context;
        this.width=width;

        this.acitivty=activity;
        bannerTimer = new Timer();
    }

    @Override
    public LayoutHelper onCreateLayoutHelper() {
        return this.helper;
    }

    // 暂停banner自动轮播
    public void bannerStopPlay() {

        if (bannerTimerTask != null)
            bannerTimerTask.cancel();
    }
    // 启动banner自动轮播
    public void bannerStartPlay() {
        if (bannerTimer != null) {
            if (bannerTimerTask != null)
                bannerTimerTask.cancel();
            bannerTimerTask = new BannerTimerTask();
            bannerTimer.schedule(bannerTimerTask, 5000, 5000);// 5秒后执行，每隔5秒执行一次
        }
    }
  public void noticAdapter(List<Object> bannerNew,SingleLayoutAdapter adapter){

      adapter.notifyDataSetChanged();
  }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder=new MyViewHolder(inflater.inflate(R.layout.home_index_banner,parent,false));
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)holder.fl_viewpager
                .getLayoutParams();
        // 根据图片高和宽的比例计算高度
        // 测试图宽为694 高为323
        params.width = width;
        params.height = (int)(width*(0.38889));
        holder.fl_viewpager.setLayoutParams(params);

        return holder;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final MyViewHolder myViewHolder=(MyViewHolder)holder;
        if(Youhui_zi_new_fragment_1.bannerList.size()>0){
//myViewHolder.view_1.setVisibility(View.GONE);
            myViewHolder.fl_viewpager.setVisibility(View.VISIBLE);
            pageCount = Youhui_zi_new_fragment_1.bannerList.size();// 对应小点个数
            final ImageView[] imageViews = new ImageView[pageCount];
            myViewHolder.ll_image.removeAllViews();
            for (int i = 0; i < pageCount; i++) {
                LinearLayout.LayoutParams margin = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                // 设置每个小圆点距离左边的间距
                margin.setMargins(10, 0, 0, 0);
                ImageView imageView = new ImageView(context);
                // 设置每个小圆点的宽高
                imageView.setLayoutParams(new ViewGroup.LayoutParams(15, 15));
                imageViews[i] = imageView;
                if (i == 0) {
                    // 默认选中第一张图片
                    imageViews[i]
                            .setBackgroundResource(R.drawable.page_indicator_focused);
                } else {
                    // 其他图片都设置未选中状态
                    imageViews[i]
                            .setBackgroundResource(R.drawable.page_indicator_unfocused);
                }

                myViewHolder.ll_image.addView(imageViews[i], margin);
                viewPagerAdapter = new ViewPagerAdapter(context,Youhui_zi_new_fragment_1.bannerList,acitivty);
                vp.setAdapter(viewPagerAdapter);
                vp.setOnSingleTouchListener(new MyViewPage2.OnSingleTouchListener() {
                    @Override
                    public void onSingleTouch(Float x) {
                        if(mOnItemClickListener!=null){
                            mOnItemClickListener.OnItemClick(vp.getCurrentItem());
                        }
                    }
                });
                vp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrollStateChanged(int arg0) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onPageScrolled(int arg0,
                                               float arg1, int arg2) {
                        // TODO Auto-generated method stub
                    }

                    @Override
                    public void onPageSelected(int arg0) {
                        // 当viewpager换页时 改掉下面对应的小点
                        for (int i = 0; i < imageViews.length; i++) {
                            // 设置当前的对应的小点为选中状态
                            imageViews[arg0]
                                    .setBackgroundResource(R.drawable.page_indicator_focused);
                            if (arg0 != i) {
                                // 设置为非选中状态
                                imageViews[i]
                                        .setBackgroundResource(R.drawable.page_indicator_unfocused);
                            }
                        }
                    }

                });
        }


        }else{
            myViewHolder.fl_viewpager.setVisibility(View.GONE);
          //  myViewHolder.view_1.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        if(Youhui_zi_new_fragment_1.bannerList.size()>0){
            return 1;
        }else{
            return 0;
        }


    }

    private class MyViewHolder extends RecyclerView.ViewHolder{

        private LinearLayout ll_image;
        private FrameLayout fl_viewpager;
       // private View view_1;
        public MyViewHolder(View itemView) {
            super(itemView);
            vp=(MyViewPage2)itemView.findViewById(R.id.vp);
            ll_image=(LinearLayout)itemView.findViewById(R.id.iv_image);
            fl_viewpager=(FrameLayout)itemView.findViewById(R.id.fl_viewpager);
          //  view_1=itemView.findViewById(R.id.view_1);
        }
    }
    public void setOnItemClickListenr(OnItemClickListener listener){
        this.mOnItemClickListener=listener;
    }
    public interface OnItemClickListener {
        void OnItemClick(int position);

    }
    class BannerTimerTask extends TimerTask {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            Message msg = new Message();

            if (Youhui_zi_new_fragment_1.bannerList.size() <= 1)
                return;

            if (null == vp)
                return;

            int currentIndex = vp.getCurrentItem();
            if (currentIndex == Youhui_zi_new_fragment_1.bannerList.size() - 1)
                msg.what = 0;
            else
                msg.what = currentIndex + 1;

            handler.sendMessage(msg);
        }

    }

}
