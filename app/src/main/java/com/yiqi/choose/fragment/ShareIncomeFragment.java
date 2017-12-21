package com.yiqi.choose.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.chanven.lib.cptr.circle.CircularProgressView;
import com.umeng.analytics.MobclickAgent;
import com.yiqi.choose.R;
import com.yiqi.choose.activity.MainAcitivity1;
import com.yiqi.choose.activity.PhoneActivity;
import com.yiqi.choose.activity.ShareActivity;
import com.yiqi.choose.activity.WebActivity;
import com.yiqi.choose.activity.shareincome.ShareIncomeDetails;
import com.yiqi.choose.activity.shareincome.ShareIncome_tixianActivity;
import com.yiqi.choose.base.BaseFragment;
import com.yiqi.choose.factory.ThreadPollFactory;
import com.yiqi.choose.model.InviteBanner;
import com.yiqi.choose.model.PictureInfo;
import com.yiqi.choose.thread.CheckNumberThread;
import com.yiqi.choose.thread.GetPictureThread;
import com.yiqi.choose.thread.ShareComeDetailsThread;
import com.yiqi.choose.utils.AndroidUtils;
import com.yiqi.choose.utils.MetricsUtils;
import com.yiqi.choose.utils.NetJudgeUtils;
import com.yiqi.choose.utils.ParseJsonCommon;
import com.yiqi.choose.utils.PicassoUtils;
import com.yiqi.choose.utils.SharedPfUtils;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.yiqi.choose.R.id.fl_viewpager;
import static com.yiqi.choose.R.id.tv_apply;
import static com.yiqi.choose.R.id.vp;

/**
 * Created by moumou on 17/12/11.
 */

public class ShareIncomeFragment extends BaseFragment {
    @BindView(R.id.tv_title)
    TextView mTv_title;
    @BindView(R.id.iv_down)
    ImageView mIvDown;
    @BindView(R.id.cp_image)
    CircularProgressView mCpImage;
    @BindView(tv_apply)
    TextView mTvApply;
    @BindView(R.id.ll_nophone)
    LinearLayout mLlNophone;
    Unbinder unbinder;
    @BindView(vp)
    ViewPager mVp;
    @BindView(R.id.iv_image)
    LinearLayout mIvImage;
    @BindView(fl_viewpager)
    FrameLayout mFlViewpager;
    @BindView(R.id.tv_all_money)
    TextView mTvAllMoney;
    @BindView(R.id.tv_getmoney)
    TextView mTvGetmoney;
    @BindView(R.id.tv_lastmonth_mkmoney)
    TextView mTvLastmonthMkmoney;
    @BindView(R.id.tv_month_mkmoney)
    TextView mTvMonthMkmoney;
    @BindView(R.id.ll_more)
    LinearLayout mLlMore;
    @BindView(R.id.tv_toshare)
    TextView mTvToshare;


    @BindView(R.id.sv_invite)
    ScrollView mSvInvite;
    @BindView(R.id.home_fragment_tv_retro)
    TextView mHomeFragmentTvRetro;
    @BindView(R.id.home_zi_nowefi)
    LinearLayout mHomeZiNowefi;

    private MyHandler hd;
    private List<Object> pictureList;
    private List<Object> bannerList;


    private int screenWidth;
    private int screenHeight;

    int pageCount;

    ViewPagerAdapter mViewPagerAdapter;
    private BannerTimerTask bannerTimerTask;
    private Timer bannerTimer;

    public static boolean isPhoneCome=false;
    public static String alipay="";

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            mVp.setCurrentItem(msg.what);
            super.handleMessage(msg);

        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_shareincome, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        hd = new MyHandler((MainAcitivity1) getActivity());
        pictureList = new ArrayList<Object>();
        bannerList = new ArrayList<Object>();
        screenWidth = AndroidUtils.getWidth(getActivity());
        screenHeight = AndroidUtils.getHeight(getActivity());
        bannerTimer = new Timer();
        initData();
    }
private void initData(){
    if (TextUtils.isEmpty((String) SharedPfUtils.getData(getActivity(), "newQuanId", ""))) {
        mTv_title.setText("分享赚");
        if (NetJudgeUtils.getNetConnection(getActivity())) {
            mLlNophone.setVisibility(View.VISIBLE);
            mSvInvite.setVisibility(View.GONE);
            mHomeZiNowefi.setVisibility(View.GONE);
            mCpImage.setVisibility(View.VISIBLE);
            mIvDown.setVisibility(View.GONE);
            mCpImage.innerStart();

            ThreadPollFactory.getNormalPool().execute(new GetPictureThread(hd, getActivity()));
        } else {
            mLlNophone.setVisibility(View.GONE);
            mSvInvite.setVisibility(View.GONE);
            mHomeZiNowefi.setVisibility(View.VISIBLE);
        }
    } else {
        mTv_title.setText("分享赚");
        if (NetJudgeUtils.getNetConnection(getActivity())) {
            mLlNophone.setVisibility(View.GONE);
            mSvInvite.setVisibility(View.VISIBLE);
            mHomeZiNowefi.setVisibility(View.GONE);
            ThreadPollFactory.getNormalPool().execute(new ShareComeDetailsThread(hd, getActivity(),(String) SharedPfUtils.getData(getActivity(), "newQuanId", "")));
        } else {
            mLlNophone.setVisibility(View.GONE);
            mSvInvite.setVisibility(View.GONE);
            mHomeZiNowefi.setVisibility(View.VISIBLE);
        }

    }
}
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);



    }

    private class MyHandler extends Handler {
        private WeakReference<MainAcitivity1> mWeakReference = null;

        MyHandler(MainAcitivity1 activity) {
            mWeakReference = new WeakReference<MainAcitivity1>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            MainAcitivity1 mainActivity = mWeakReference.get();
            if (mainActivity == null) {
                return;
            }

            if (Build.VERSION.SDK_INT >= 17) {
                if (mainActivity == null || mainActivity.isDestroyed() || mainActivity.isFinishing()) {
                    return;
                }
            } else {
                if (mainActivity == null || mainActivity.isFinishing()) {

                    return;
                }
            }
            if (msg.what == 1) {
                try {
                    JSONObject json = new JSONObject((String) msg.obj);
                    String code = json.getString("code");
                    if (code.equals("0")) {
                        String data = json.getString("data");
                        pictureList.clear();
                        pictureList = ParseJsonCommon.parseJsonData(data,
                                PictureInfo.class);
                        if (pictureList.size() > 0) {
                            int type = MetricsUtils.getType(getActivity());
                            String url = "";
                            for (int i = 0; i < pictureList.size(); i++) {
                                if (((PictureInfo) pictureList.get(i)).getImageType().equals(type + "")) {
                                    url = ((PictureInfo) pictureList.get(i)).getImageUrl();
                                    PicassoUtils.loadImageWithHodler1(getActivity(), url, mIvDown);
                                    break;
                                }
                            }
                        }
                        mLlNophone.setVisibility(View.VISIBLE);
                        mSvInvite.setVisibility(View.GONE);
                        mHomeZiNowefi.setVisibility(View.GONE);
                        mCpImage.setVisibility(View.GONE);
                        mIvDown.setVisibility(View.VISIBLE);

                    } else {
                        String errorMsg = json.getString("errorMsg");
                        Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_LONG).show();
                        mLlNophone.setVisibility(View.GONE);
                        mSvInvite.setVisibility(View.GONE);
                        mHomeZiNowefi.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "服务器错误", Toast.LENGTH_LONG).show();
                    mLlNophone.setVisibility(View.GONE);
                    mSvInvite.setVisibility(View.GONE);
                    mHomeZiNowefi.setVisibility(View.VISIBLE);

                } finally {
                    mCpImage.innerStop();
                }

            }
            if (msg.what == 2) {
                Toast.makeText(getActivity(), "服务器错误", Toast.LENGTH_LONG).show();
                mCpImage.setVisibility(View.GONE);
                mIvDown.setVisibility(View.VISIBLE);
                mCpImage.innerStop();
                mLlNophone.setVisibility(View.GONE);
                mSvInvite.setVisibility(View.GONE);
                mHomeZiNowefi.setVisibility(View.VISIBLE);
            }
            if (msg.what == 3) {
                try {
                    JSONObject json = new JSONObject((String) msg.obj);
                    String code = json.getString("code");
                    if (code.equals("0")) {
                        String data = json.getString("data");
                        JSONObject dataDetails = new JSONObject(data);
                        String banner = dataDetails.getString("banner");
                        bannerList.clear();
                        bannerList = ParseJsonCommon.parseJsonData(banner,
                                InviteBanner.class);

                        if (bannerList.size() > 0) {
                            mFlViewpager.setVisibility(View.VISIBLE);
                            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mFlViewpager
                                    .getLayoutParams();
                            // 根据图片高和宽的比例计算高度
                            // 测试图宽为694 高为323
                            params.width = screenWidth;
                            params.height = screenWidth * 280 / 720;
                            mFlViewpager.setLayoutParams(params);
                            pageCount = bannerList.size();// 对应小点个数
                            final ImageView[] imageViews = new ImageView[pageCount];
                            mIvImage.removeAllViews();
                            for (int i = 0; i < pageCount; i++) {
                                LinearLayout.LayoutParams margin = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.WRAP_CONTENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT);
                                // 设置每个小圆点距离左边的间距
                                margin.setMargins(10, 0, 0, 0);
                                ImageView imageView = new ImageView(mainActivity);
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

                                mIvImage.addView(imageViews[i], margin);
                                mViewPagerAdapter = new ViewPagerAdapter(mainActivity);
                                mVp.setAdapter(mViewPagerAdapter);
                                if (bannerList.size() > 1) {
                                    bannerStartPlay();
                                }


                                mVp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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
                        } else {
                            mFlViewpager.setVisibility(View.GONE);
                        }

                        mTvAllMoney.setText(dataDetails.getString("money"));
                        mTvLastmonthMkmoney.setText(dataDetails.getString("last"));
                        //current
                        mTvMonthMkmoney.setText(dataDetails.getString("current"));
                        alipay=dataDetails.getString("alipay");

                        mLlNophone.setVisibility(View.GONE);
                        mSvInvite.setVisibility(View.VISIBLE);
                        mHomeZiNowefi.setVisibility(View.GONE);

                    } else {
                        String errorMsg = json.getString("errorMsg");
                        Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_LONG).show();
                        mLlNophone.setVisibility(View.GONE);
                        mSvInvite.setVisibility(View.GONE);
                        mHomeZiNowefi.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "服务器错误", Toast.LENGTH_LONG).show();
                    mLlNophone.setVisibility(View.GONE);
                    mSvInvite.setVisibility(View.GONE);
                    mHomeZiNowefi.setVisibility(View.VISIBLE);

                }

            }
            if (msg.what == 4) {
                Toast.makeText(getActivity(), "服务器错误", Toast.LENGTH_LONG).show();
                mLlNophone.setVisibility(View.GONE);
                mSvInvite.setVisibility(View.GONE);
                mHomeZiNowefi.setVisibility(View.VISIBLE);
            }
            if (msg.what == 20) {
                try {
                    JSONObject json = new JSONObject((String) msg.obj);
                    String code = json.getString("code");
                    if (code.equals("0")) {
                        //  Toast.makeText(PhoneActivity.this,"",Toast.LENGTH_LONG).show();
                        String data = json.getString("data");
                        if (data.equals("0")) {
                            Intent intent = new Intent(getActivity(),
                                    PhoneActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getActivity(), "活动太火爆，请稍后再申请!", Toast.LENGTH_LONG).show();

                        }
                        // SharedPfUtils.saveData(PhoneActivity.this,"invitemark",data);

                    } else {
                        Toast.makeText(getActivity(), json.getString("errorMsg"), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "服务器错误!", Toast.LENGTH_LONG).show();

                }finally {
                    mTvApply.setText("申请参加活动");
                }
            }
            if (msg.what == 21) {
                Toast.makeText(getActivity(), "服务器错误!", Toast.LENGTH_LONG).show();
                mTvApply.setText("申请参加活动");
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("ShareIncomeFragment");
        if (getUserVisibleHint() && bannerList.size() > 1 && !TextUtils.isEmpty((String) SharedPfUtils.getData(getActivity(), "newQuanId", ""))) {
            bannerStartPlay();
        }
        System.out.println("onResume==="+getUserVisibleHint());
        if(getUserVisibleHint()&&isPhoneCome){
                isPhoneCome=false;
                initData();

        }
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("ShareIncomeFragment");
        if (getUserVisibleHint() && bannerList.size() > 1 && !TextUtils.isEmpty((String) SharedPfUtils.getData(getActivity(), "newQuanId", ""))) {
            bannerStopPlay();
        }
    }

    @Override
    protected void lazyLoad() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @OnClick({R.id.tv_getmoney, R.id.ll_more, R.id.tv_toshare, tv_apply, R.id.home_fragment_tv_retro})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_getmoney:
                Intent tiIntent = new Intent(getActivity(),
                        ShareIncome_tixianActivity.class);
                startActivity(tiIntent);
                getActivity().overridePendingTransition(
                        R.anim.to_right, R.anim.to_left);
                break;
            case R.id.ll_more:
                Intent shareIntent = new Intent(getActivity(),
                        ShareIncomeDetails.class);
                startActivity(shareIntent);
                getActivity().overridePendingTransition(
                        R.anim.to_right, R.anim.to_left);
                break;
            case R.id.tv_toshare:
                Intent intent = new Intent(getActivity(),
                        ShareActivity.class);
                intent.putExtra("typeShare", "2");
                startActivity(intent);
                getActivity().overridePendingTransition(
                        R.anim.to_right, R.anim.to_left);
                break;
            case tv_apply:
                if(NetJudgeUtils.getNetConnection(getActivity())){
                    if(mTvApply.getText().toString().trim().equals("申请参加活动")){
                        mTvApply.setText("提交申请中...");
                        ThreadPollFactory.getNormalPool().execute(new CheckNumberThread(hd,getActivity()));
                    }else{
                        Toast.makeText(getActivity(), "正在申请中，不要重复提交申请！", Toast.LENGTH_LONG).show();
                    }

                } else {
                    Toast.makeText(getActivity(), "请检查网络连接！", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.home_fragment_tv_retro:
                initData();
                break;
        }
    }

    class ViewPagerAdapter extends PagerAdapter {
        // private AsyncLoaderImage loader;
        private LayoutInflater inflater;
        private Context context;

        public ViewPagerAdapter(Context context) {
            this.context = context;
            inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        }

        @Override
        public int getCount() {

            return bannerList.size();
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
        public Object instantiateItem(ViewGroup container, final int position) {

            View layout = inflater.inflate(R.layout.layout_banner_item, null);
            ImageView imageView = (ImageView) layout
                    .findViewById(R.id.iv_banner_item);
            InviteBanner info = new InviteBanner();
            info = (InviteBanner) bannerList.get(position);
            PicassoUtils.loadImageWithHolderAndError(getActivity(), info.getImage(), R.mipmap.bannerpt, R.mipmap.bannerpt, imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    InviteBanner info = (InviteBanner) bannerList.get(position);
                    if (!TextUtils.isEmpty(info.getUrl())) {
                        Intent intent = new Intent(getActivity(),
                                WebActivity.class);
                        intent.putExtra("title", info.getName());
                        intent.putExtra("url", info.getUrl());
                        startActivity(intent);
                        getActivity().overridePendingTransition(
                                R.anim.to_right, R.anim.to_left);
                    }

                }
            });

            ((ViewPager) container).addView(layout, 0);
            //imageView.setOnClickListener(new OnClickListener() {});
            return layout;
        }

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

    class BannerTimerTask extends TimerTask {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            Message msg = new Message();

            if (bannerList.size() <= 1)
                return;

            if (null == mVp)
                return;

            int currentIndex = mVp.getCurrentItem();
            if (currentIndex == bannerList.size() - 1)
                msg.what = 0;
            else
                msg.what = currentIndex + 1;

            handler.sendMessage(msg);
        }

    }
}
