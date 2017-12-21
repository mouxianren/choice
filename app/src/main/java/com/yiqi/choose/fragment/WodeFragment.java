package com.yiqi.choose.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
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
import com.yiqi.choose.activity.MainAcitivity1;
import com.yiqi.choose.activity.SplashActicity1;
import com.yiqi.choose.alibabaCallback.DemoTradeCallback;
import com.yiqi.choose.base.BaseFragment;
import com.yiqi.choose.base.BaseMap;
import com.yiqi.choose.dao.SSDao;
import com.yiqi.choose.utils.AndroidUtils;
import com.yiqi.choose.utils.HttpConBase;
import com.yiqi.choose.utils.JudgeLoginTaobao;
import com.yiqi.choose.utils.SharedPfUtils;
import com.yiqi.choose.utils.UrlUtils;
import com.yiqi.choose.view.UISwitchButton1;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import static com.yiqi.choose.activity.SplashActicity1.mPId;

/**
 * @author: yaoyongchao
 * @date: 2016/3/28 15:18
 * @description:
 */
public class WodeFragment extends BaseFragment {
    private LinearLayout me_logintaobao;
    private LinearLayout me_taobao_dingdan;
    private LinearLayout me_wode;
    private TextView me_tv_tuichu;
    private Dialog dialog;
    private UISwitchButton1 sbxx = null;
    private AlibcShowParams alibcShowParams;//页面打开方式，默认，H5，Native
    private Map<String, String> exParams;//yhhpass参数
    private AlibcTaokeParams alibcTaokeParams = null;//淘客参数，包括pid，unionid，subPid
    private LinearLayout frgment_test;
    private SSDao dao;
private Handler hd;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.me, container, false);
        me_logintaobao = (LinearLayout) view.findViewById(R.id.me_login_taobao);
        me_taobao_dingdan = (LinearLayout) view.findViewById(R.id.me_taobao_dingdan);
        me_wode = (LinearLayout) view.findViewById(R.id.me_aboutus);
        me_tv_tuichu = (TextView) view.findViewById(R.id.me_tv_tuichu);
        frgment_test=(LinearLayout)view.findViewById(R.id.frgment_test);
        hd=new MyHandler((MainAcitivity1)getActivity());
        frgment_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dao=SSDao.getInstance(getActivity());
                dao.deteAllGoods();
                SharedPfUtils.saveData(getActivity(), "quanId","");
                SharedPfUtils.saveData(getActivity(), "pid", "");
                Toast.makeText(getActivity(),"清除的圈值"+SplashActicity1.mQuanId+("pid=="+SplashActicity1.mPId),Toast.LENGTH_LONG).show();
                SharedPfUtils.saveStringData(getActivity(), "sendgetui", "");
                SharedPfUtils.saveStringData(getActivity(), "gettuitoken","");
                SharedPfUtils.saveStringData(getActivity(), "mainlogintaobaoId", "");
                SharedPfUtils.saveStringData(getActivity(), "maintoken","");
            }
        });
        sbxx = (UISwitchButton1) view.findViewById(R.id.splitxiaoxi);


                    if (SharedPfUtils.getData(getActivity(),"xiaoxi","").equals("2")) {
                        sbxx.setChecked(true);
                    } else {
                        sbxx.setChecked(false);
                    }

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

        me_logintaobao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (JudgeLoginTaobao.isLogin()) {
                    showLogoutDialog();
                } else {
                    login();
                }

            }
        });
        me_taobao_dingdan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkOrder();
            }
        });
        me_wode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),
                        AboutUsActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(
                        R.anim.to_right, R.anim.to_left);
            }
        });
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
        //判断是否登录 来做不同的字体反应
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

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("WodeFragment");
        judgeTv();


    }

    public void judgeTv() {
        if (JudgeLoginTaobao.isLogin()) {
            me_tv_tuichu.setText("退出登录");
        } else {
            me_tv_tuichu.setText("未登录");
        }
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("WodeFragment");

    }

    @Override
    protected void lazyLoad() {

        //假如可见 且第一次 而且加载没有成功过

    }

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

    /**
     * 的到圈值接口
     */
    private class sendLogin implements Runnable {
        String inviteCode;
        String buyerToken;
        String buyerAntor;
        String buyerName;
        String buyerId;

        public sendLogin(String inviteCode, String buyerName,String buyerAntor, String buyerId, String buyerToken) {
            this.inviteCode = inviteCode;
            this.buyerAntor = buyerAntor;
            this.buyerId = buyerId;
            this.buyerName = buyerName;
            this.buyerToken = buyerToken;


        }

        @Override
        public void run() {
            try {
                Map<String, String> params = new HashMap<>();
                params.put("code", inviteCode);
                params.put("buyerToken", buyerToken);
                params.put("buyerAntor", buyerAntor);
                params.put("buyerName", buyerName);
                params.put("buyerId", buyerId);

                params = BaseMap.getMapAll(params);

                //System.out.println(inviteCode+"ss"+buyerName+"bb");
                String url = getResources().getString(R.string.appurl) + "/buyer/login"+"?stamp=" + UrlUtils.getTime() + "&encode=" + UrlUtils.getEncode();
                String jsonData = HttpConBase.getjsonByPost(url, params, "utf-8");
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


            if (msg.what == 1) {
                try {
                    JSONObject json = new JSONObject((String) msg.obj);
                    String code = json.getString("code");
                    if (code.equals("0")) {
                        SharedPfUtils.saveStringData(mainActivity, "logintaobao", "1");
                        SharedPfUtils.saveStringData(mainActivity, "logintaobaoId", AlibcLogin.getInstance().getSession().openId);
                    } else {
                        Toast.makeText(mainActivity, json.getString("errorMsg"), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
            if (msg.what == 2) {
                Toast.makeText(mainActivity, "服务器错误！", Toast.LENGTH_LONG).show();

            }
        }
    };
}
