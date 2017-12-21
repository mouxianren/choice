package com.yiqi.choose.base;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseFragment1_coupons extends Fragment {

    /**
     * Fragment当前状态是否可见
     */
    protected boolean isVisible;
    private String id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return initView(inflater, container, savedInstanceState);
    }

    public abstract View initView(LayoutInflater inflater, ViewGroup container,
                                  Bundle savedInstanceState);


    public abstract void initData();


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        initData();

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (getUserVisibleHint()) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            onInvisible();
        }
    }

    /**
     * 可见
     */
    protected void onVisible() {
        //lazyLoad(id);
    }


    /**
     * 不可见
     */
    protected void onInvisible() {
        stopbanner();
    }

    /**
     * 延迟加载
     * 子类必须重写此方法
     */
    public abstract void lazyLoad(String id);

    /**
     * 延迟加载
     * 子类必须重写此方法
     */
    protected void stopbanner() {

    }


    public void setId(String id) {
        this.id = id;
    }

    /**
     * 取当前网络状态
     *
     * @return
     */
    public boolean getNetConnection() {
        return isConnectInternet();
    }

    /**
     * 判断网络是否正常
     *
     * @return
     */
    public boolean isConnectInternet() {
        ConnectivityManager conManager = (ConnectivityManager) getActivity()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
        if (networkInfo != null) { // 注意，这个判断一定要的哦，要不然会出错
            return networkInfo.isAvailable();
        }
        return false;
    }

    public static String changeData(long date, Context context) {
        // String stringtime=shijian.trim();
        // Date d = null;
        // SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // try {
        // d = f.parse(stringtime);
        // } catch (ParseException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }

        long firstsecondOfToday;
        Time time = new Time();
        time.setToNow();
        time.hour = 0;
        time.minute = 0;
        time.second = 0;
        firstsecondOfToday = time.toMillis(false);
        // long date = d.getTime();
        // String dateStr = null;
        // if (date - firstsecondOfToday > 0
        // && date - firstsecondOfToday < DateUtils.DAY_IN_MILLIS) {
        //
        // dateStr = DateFormat.getTimeFormat(context).format(date);
        // } else {
        //
        // dateStr = DateFormat.getDateFormat(context).format(date);
        // }
        return DateFormat.getDateFormat(context).format(date);
    }

    public int getFenbianlv1() {
        Display mDisplay = getActivity().getWindowManager().getDefaultDisplay();
        int W = mDisplay.getWidth();
        int H = mDisplay.getHeight();
        return W;
    }
}
