package com.yiqi.choose.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.baichuan.android.trade.AlibcTrade;
import com.alibaba.baichuan.android.trade.model.AlibcShowParams;
import com.alibaba.baichuan.android.trade.model.OpenType;
import com.alibaba.baichuan.android.trade.page.AlibcBasePage;
import com.alibaba.baichuan.android.trade.page.AlibcMyOrdersPage;
import com.alibaba.baichuan.trade.biz.core.taoke.AlibcTaokeParams;
import com.alibaba.baichuan.trade.biz.login.AlibcLogin;
import com.alibaba.baichuan.trade.biz.login.AlibcLoginCallback;
import com.igexin.sdk.PushManager;
import com.umeng.analytics.MobclickAgent;
import com.yiqi.choose.R;
import com.yiqi.choose.activity.AboutUsActivity;
import com.yiqi.choose.activity.InviteMoneyDetailsActivity;
import com.yiqi.choose.activity.Invite_makemoneyActivity;
import com.yiqi.choose.activity.MainAcitivity1;
import com.yiqi.choose.activity.ShareActivity;
import com.yiqi.choose.activity.SplashActicity1;
import com.yiqi.choose.activity.WodeFankuiActivity;
import com.yiqi.choose.alibabaCallback.DemoTradeCallback;
import com.yiqi.choose.base.BaseFragment;
import com.yiqi.choose.base.BaseMap;
import com.yiqi.choose.dao.SSDao;
import com.yiqi.choose.factory.ThreadPollFactory;
import com.yiqi.choose.utils.AndroidUtils;
import com.yiqi.choose.utils.HttpConBase;
import com.yiqi.choose.utils.JudgeLoginTaobao;
import com.yiqi.choose.utils.NetJudgeUtils;
import com.yiqi.choose.utils.PicassoUtils;
import com.yiqi.choose.utils.SharedPfUtils;
import com.yiqi.choose.utils.UrlUtils;
import com.yiqi.choose.view.CircularImage;
import com.yiqi.choose.view.UISwitchButton1;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import static com.yiqi.choose.activity.SplashActicity1.isGetInvite;
import static com.yiqi.choose.activity.SplashActicity1.isInvite;
import static com.yiqi.choose.activity.SplashActicity1.mPId;
import static com.yiqi.choose.activity.SplashActicity1.mQuanId;

/**
 * Created by moumou on 17/9/26.
 */

public class WodeFragmentNew extends BaseFragment{
    CircularImage wo_headImage;
    TextView wo_name;
    private UISwitchButton1 sbxx = null;
    LinearLayout wo_dingdan;
    private Dialog dialog;
    private LinearLayout wo_quite;
    private AlibcShowParams alibcShowParams;//页面打开方式，默认，H5，Native
    private Map<String, String> exParams;//yhhpass参数
    private AlibcTaokeParams alibcTaokeParams = null;//淘客参数，包括pid，unionid，subPid
    private LinearLayout ll_qqkf;

    private String QQNumber;
    private String isQQShow;
    private LinearLayout wo_fankui;
    private LinearLayout wo_share;
    private LinearLayout ll_about_us;

    /**
     * 标志位，标志已经初始化完成
     */
    private boolean isPrepared;
    /**
     * 是否已被加载过一次，第二次就不再去请求数据了
     */
    private boolean mHasLoadedOnce;
    private Handler hd;

    private TextView tv_wode;

    //新增功能 wo_ll_invite
    private LinearLayout wo_ll_invite;

  private SSDao dao;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view=inflater.inflate(R.layout.me_new,null);
        wo_headImage=(CircularImage)view.findViewById(R.id.wo_headImage);
        wo_name=(TextView)view.findViewById(R.id.wo_name);
        sbxx = (UISwitchButton1) view.findViewById(R.id.splitxiaoxi);
        wo_dingdan=(LinearLayout)view.findViewById(R.id.wo_dingdan);
        wo_quite=(LinearLayout)view.findViewById(R.id.wo_quite);
        ll_qqkf=(LinearLayout)view.findViewById(R.id.ll_qqkf);
        wo_fankui=(LinearLayout)view.findViewById(R.id.wo_fankui);
        wo_share=(LinearLayout)view.findViewById(R.id.wo_share);
        ll_about_us=(LinearLayout)view.findViewById(R.id.ll_about_us);
        tv_wode=(TextView)view.findViewById(R.id.tv_wode);
        wo_ll_invite=(LinearLayout)view.findViewById(R.id.wo_ll_invite);
        if (SharedPfUtils.getData(getActivity(),"xiaoxi","").equals("2")) {
            sbxx.setChecked(true);
        } else {
            sbxx.setChecked(false);
        }
        isPrepared=true;
        mHasLoadedOnce=false;
        hd=new MyHandler((MainAcitivity1)getActivity());
        return view;


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        alibcTaokeParams = new AlibcTaokeParams(mPId, "", "");
        alibcShowParams = new AlibcShowParams(OpenType.H5, false);
        exParams = new HashMap<>();
        exParams.put("isv_code", "appisvcode");
        exParams.put("alibaba", "阿里巴巴");//自定义参数部分，可任意增删改

        if(isInvite){
            wo_ll_invite.setVisibility(View.GONE);
        }else{
            if(isGetInvite){
                wo_ll_invite.setVisibility(View.GONE);
            }else{
                //重新执行一遍线程
                wo_ll_invite.setVisibility(View.GONE);
//                if (NetJudgeUtils.getNetConnection(getActivity())) {
//                    ThreadPollFactory.getNormalPool().execute(new getQuanCode1(SplashActicity1.mQuanId));
//                }
            }
        }

        sbxx.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == false) {
                    PushManager.getInstance().turnOnPush(getActivity());
                    SharedPfUtils.saveData(getActivity(),"xiaoxi","1");
                    Toast.makeText(getActivity(),"消息开关已开启!",Toast.LENGTH_LONG).show();
                } else if (isChecked == true) {
                    PushManager.getInstance().turnOffPush(getActivity());
                    SharedPfUtils.saveData(getActivity(),"xiaoxi","2");
                    Toast.makeText(getActivity(),"消息开关已关闭!",Toast.LENGTH_LONG).show();

                }
            }
        });

        wo_headImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!JudgeLoginTaobao.isLogin()) {
                    login();
                }
            }
        });
        wo_dingdan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(JudgeLoginTaobao.isLogin()){
                    checkOrder();
                }else{
                    login();
                }

            }
        });
        wo_quite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(JudgeLoginTaobao.isLogin()){
                    showLogoutDialog();
                }
            }
        });
        ll_qqkf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳到QQ客服
                if (checkApkExist(getActivity(), "com.tencent.mobileqq")){
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("mqqwpa://im/chat?chat_type=wpa&uin="+QQNumber+"&version=1")));
                }else{
                    Toast.makeText(getActivity(),"本机未安装QQ应用",Toast.LENGTH_SHORT).show();
                }
            }
        });
        wo_fankui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),
                        WodeFankuiActivity.class);
                startActivity(intent);
               getActivity().overridePendingTransition(
                        R.anim.to_right, R.anim.to_left);
            }
        });
        wo_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),
                        ShareActivity.class);
                intent.putExtra("typeShare","1");
                startActivity(intent);
                getActivity().overridePendingTransition(
                        R.anim.to_right, R.anim.to_left);
            }
        });
        ll_about_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),
                        AboutUsActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(
                        R.anim.to_right, R.anim.to_left);
            }
        });
        wo_ll_invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty((String)SharedPfUtils.getData(getActivity(),"newQuanId",""))){
                    Intent intent = new Intent(getActivity(),
                            Invite_makemoneyActivity.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(
                            R.anim.to_right, R.anim.to_left);
                }else{
                    Intent intent = new Intent(getActivity(),
                            InviteMoneyDetailsActivity.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(
                            R.anim.to_right, R.anim.to_left);
                }

            }
        });
//        wo_name.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                                dao=SSDao.getInstance(getActivity());
//                                dao.deteAllGoods();
//                                SharedPfUtils.saveData(getActivity(), "quanId","");
//                                SharedPfUtils.saveData(getActivity(), "pid", "");
//                                Toast.makeText(getActivity(),"清除的圈值"+SplashActicity1.mQuanId+("pid=="+SplashActicity1.mPId),Toast.LENGTH_LONG).show();
//                                SharedPfUtils.saveStringData(getActivity(), "sendgetui", "");
//                                SharedPfUtils.saveStringData(getActivity(), "gettuitoken","");
//                                SharedPfUtils.saveStringData(getActivity(), "mainlogintaobaoId", "");
//                                SharedPfUtils.saveStringData(getActivity(), "maintoken","");
//            }
//        });
    }
    public boolean checkApkExist(Context context, String packageName) {
        if (packageName == null || "".equals(packageName))
            return false;
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(packageName,
                    PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
    private void showLogoutDialog() {
        dialog = new Dialog(getActivity(), R.style.FullHeightDialog);
        // dialog.setCancelable(false);
        // dialog.setCanceledOnTouchOutside(false);
        if (!dialog.isShowing()) {
            View view = LayoutInflater.from(getActivity()).inflate(
                    R.layout.logout_dialog, null);
            dialog.setContentView(
                    view,
                    new ViewGroup.LayoutParams(AndroidUtils.dip2px(
                            getActivity(), 300), LinearLayout.LayoutParams.WRAP_CONTENT));
            TextView tv_yes = (TextView) view.findViewById(R.id.ok);
            TextView tv_no = (TextView) view.findViewById(R.id.no);
            tv_no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            tv_yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    logout();
                    dialog.dismiss();
                }
            });

            dialog.show();
        }

    }
    /**
     * 退出登录
     */
    public void logout() {

        AlibcLogin alibcLogin = AlibcLogin.getInstance();

        alibcLogin.logout(new AlibcLoginCallback() {
            @Override
            public void onSuccess(int i) {
                SharedPfUtils.saveStringData(getActivity(), "nick", "");
                SharedPfUtils.saveStringData(getActivity(), "avatarUrl", "");
                SharedPfUtils.saveStringData(getActivity(), "openId", "");
                SharedPfUtils.saveStringData(getActivity(), "openSid", "");
                judgeTv();
            }

            @Override
            public void onFailure(int i, String s) {
                Toast.makeText(getActivity(), "退出失败！", Toast.LENGTH_LONG).show();
            }
        });
    }
    public void checkOrder() {
        /**
         * 我的订单页面
         *
         * @param status   默认跳转页面；填写：0：全部；1：待付款；2：待发货；3：待收货；4：待评价
         * @param allOrder false 进行订单分域（只展示通过当前app下单的订单），true 显示所有订单
         */
        AlibcBasePage alibcBasePage = new AlibcMyOrdersPage(0, true);
        AlibcTrade.show(getActivity(), alibcBasePage, alibcShowParams, alibcTaokeParams, exParams, new DemoTradeCallback());
    }
    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("WodeFragment");
        judgeTv();


    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("WodeFragment");
    }

    @Override
    protected void lazyLoad(){
        if (!isPrepared || mHasLoadedOnce) {
            return;
        }
        if (NetJudgeUtils.getNetConnection(getActivity())) {
            ThreadPollFactory.getNormalPool().execute(new QQThread());
        } else {
            ll_qqkf.setVisibility(View.GONE);
        }
    }
    /**
     * 的到圈值接口
     */
    private class QQThread implements Runnable {

        @Override
        public void run() {
            try {
                String url = getResources().getString(R.string.appurl) + "/mine/qq"+"?code=" + mQuanId+"&stamp=" + UrlUtils.getTime() + "&encode=" + UrlUtils.getEncode();

                String jsonData = HttpConBase.sendGet(url);
                Message msg = Message.obtain();
                msg.what = 1;
                msg.obj = jsonData;
                hd.sendMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
                hd.sendEmptyMessage(2);
            }

        }
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
            if(mainActivity==null){
                return;
            }

            if(Build.VERSION.SDK_INT >= 17) {
                if (mainActivity == null || mainActivity.isDestroyed() || mainActivity.isFinishing()) {
                    return;
                }
            }else {
                if (mainActivity== null || mainActivity.isFinishing()) {

                    return;
                }
            }
            if(msg.what==1){
                try {
                    JSONObject j = new JSONObject((String) msg.obj);
                    String code = j.getString("code");
                    if(code.equals("0")){
                        String data=j.getString("data");
                        JSONObject jData = new JSONObject(data);
                        isQQShow=jData.getString("show");
                        QQNumber=jData.getString("number");
                        if(isQQShow.equals("1")){
                            ll_qqkf.setVisibility(View.VISIBLE);
                        }else{
                            ll_qqkf.setVisibility(View.GONE);
                        }
                        mHasLoadedOnce=true;

                    }else{
                        ll_qqkf.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ll_qqkf.setVisibility(View.GONE);

                }
            }
            if(msg.what==2){
                ll_qqkf.setVisibility(View.GONE);
            }
            if(msg.what==10){
                try {
                    JSONObject json = new JSONObject((String) msg.obj);
                    String code = json.getString("code");
                    if (code.equals("0")) {
                        String sdata=json.getString("data");
                        JSONObject jData = new JSONObject(sdata);
                        String iszhuan=jData.getString("iszhuan");
                        if(iszhuan.equals("1")){
                            SplashActicity1.isInvite=true;
                            wo_ll_invite.setVisibility(View.GONE);
                        }else{
                            SplashActicity1.isInvite=false;
                            wo_ll_invite.setVisibility(View.GONE);
                        }
                        isGetInvite=true;





                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        }
    };
    public void login() {
        AlibcLogin alibcLogin = AlibcLogin.getInstance();
        alibcLogin.showLogin(new AlibcLoginCallback() {
            @Override
            public void onSuccess(int i) {
                judgeTv();
                SharedPfUtils.saveStringData(getActivity(), "nick", AlibcLogin.getInstance().getSession().nick);
                SharedPfUtils.saveStringData(getActivity(), "avatarUrl", AlibcLogin.getInstance().getSession().avatarUrl);
                SharedPfUtils.saveStringData(getActivity(), "openId", AlibcLogin.getInstance().getSession().openId);
                SharedPfUtils.saveStringData(getActivity(), "openSid", AlibcLogin.getInstance().getSession().openSid);
                //                if (NetJudgeUtils.getNetConnection(getActivity())) {
                //                    ThreadPollFactory.getNormalPool()
                //                            .execute(new sendLogin(mQuanId,
                //                                    (String) SharedPfUtils.getData(getActivity(), "nick", ""),
                //                                    (String) SharedPfUtils.getData(getActivity(), "avatarUrl", ""),
                //                                    (String) SharedPfUtils.getData(getActivity(), "openId", ""),
                //                                    (String) SharedPfUtils.getData(getActivity(), "openSid", "")));
                //                }
            }

            @Override
            public void onFailure(int code, String msg) {

            }
        });


    }
    public void judgeTv() {
        if (JudgeLoginTaobao.isLogin()) {
            PicassoUtils.loadImageWithHolderAndError(getActivity(),AlibcLogin.getInstance().getSession().avatarUrl, R.mipmap.wo_head_grey, R.mipmap.wo_head_grey, wo_headImage);
            wo_name.setText(AlibcLogin.getInstance().getSession().nick);
            wo_quite.setVisibility(View.VISIBLE);

        } else {
            wo_headImage.setImageResource(R.mipmap.wo_head_grey);
            wo_name.setText("请登录淘宝账号");
            wo_quite.setVisibility(View.GONE);
        }

    }
    /**
     * 的到圈值接口
     */
    private class getQuanCode1 implements Runnable {
        String inviteCode;

        public getQuanCode1(String inviteCode) {
            this.inviteCode = inviteCode;

        }

        @Override
        public void run() {
            try {
                Map<String, String> params = new HashMap<>();
                params.put("code", inviteCode);
                params = BaseMap.getMapAll(params);
                String url="";
                if(getResources().getString(R.string.appurl).contains("120.26.244.207")){
                    url = getResources().getString(R.string.appurl) + "/home/invite"+"?stamp=" + UrlUtils.getTime() + "&encode=" + UrlUtils.getEncode();
                }else{
                    url = getResources().getString(R.string.appurl) + "/home/invite"+"?stamp=" + UrlUtils.getTime() + "&encode=" + UrlUtils.getEncode();
                }
                String jsonData = HttpConBase.getjsonByPost(url, params, "utf-8");
                Message msg = Message.obtain();
                msg.what = 10;
                msg.obj = jsonData;
                hd.sendMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
