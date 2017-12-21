package com.yiqi.choose.activity.search;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.yiqi.choose.R;
import com.yiqi.choose.activity.SplashActicity1;
import com.yiqi.choose.activity.zitypelist.ZitypeActivity;
import com.yiqi.choose.base.BaseActivity;
import com.yiqi.choose.dao.HistoryDao;
import com.yiqi.choose.factory.ThreadPollFactory;
import com.yiqi.choose.model.HotInfo;
import com.yiqi.choose.thread.HotwordThread;
import com.yiqi.choose.thread.StatisticsThread;
import com.yiqi.choose.utils.AndroidUtils;
import com.yiqi.choose.utils.BooleanThread;
import com.yiqi.choose.utils.NetJudgeUtils;
import com.yiqi.choose.utils.ParseJsonCommon;
import com.yiqi.choose.utils.SystemBarTintManager;
import com.yiqi.choose.view.AutoNewLineLayout;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by moumou on 17/12/13.
 */

public class SearchWordActivity extends BaseActivity {
    @BindView(R.id.ll_back)
    LinearLayout mLlBack;
    @BindView(R.id.searchnew_et_hint)
    EditText mSearchnewEtHint;
    @BindView(R.id.searchnew_tv_search)
    LinearLayout mSearchnewTvSearch;
    @BindView(R.id.searchnew_hot)
    AutoNewLineLayout mSearchnewHot;
    @BindView(R.id.searchnew_ll_hot)
    LinearLayout mSearchnewLlHot;
    @BindView(R.id.searchnew_history)
    AutoNewLineLayout mSearchnewHistory;
    @BindView(R.id.searchnew_ll_history)
    LinearLayout mSearchnewLlHistory;
    @BindView(R.id.iv_guanbi)
    ImageView mIvGuanbi;
    @BindView(R.id.searchnew_ll_clear)
    LinearLayout mSearchnewLlClear;
    @BindView(R.id.searchnew_sv)
    ScrollView mSearchnewSv;

    private MyHandler hd;
    private List<Object> hotci;
    int maxWidth = 0;

    private List<String> historyCi;
    private HistoryDao dao;
    private boolean isFirst;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.topcolorred);//通知栏所需颜色
        }
        setContentView(R.layout.searchnew_hotword);
        hd = new MyHandler(SearchWordActivity.this);
        ButterKnife.bind(this);
        isFirst=false;
        initData();
        initListener();
    }

    private void initData() {
        hotci = new ArrayList<Object>();
        historyCi = new ArrayList<String>();
        if (SplashActicity1.sdCardWritePermisson) {
            dao = HistoryDao.getInstance(SearchWordActivity.this);
            historyCi.clear();
            historyCi = dao.queryGoods();
            if (dao.getAllNumber() > 20) {
                dao.deteAllGoods();
                for (int i = historyCi.size() - 1; i >= 0; i--) {
                    dao.saveSS(historyCi.get(i));
                }
            }
            mSearchnewSv.setOverScrollMode(View.OVER_SCROLL_NEVER);
        }


        maxWidth = AndroidUtils.getWidth(SearchWordActivity.this) / 2;

        if (historyCi.size() <= 0) {
            mSearchnewLlHistory.setVisibility(View.GONE);
        } else {
            mSearchnewLlHistory.setVisibility(View.VISIBLE);
            addTagsHistory();
        }


        if (NetJudgeUtils.getNetConnection(SearchWordActivity.this)) {
            ThreadPollFactory.getNormalPool().execute(new HotwordThread(SearchWordActivity.this, hd));

        } else {
            mSearchnewLlHot.setVisibility(View.GONE);
        }
        mSearchnewEtHint.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    mIvGuanbi.setVisibility(View.VISIBLE);

                } else {
                    mIvGuanbi.setVisibility(View.GONE);
                }
            }
        });

    }

    private void initListener() {
        mSearchnewHot.setOnTouchSubprojectListener(new AutoNewLineLayout.OnTouchSubprojectListener() {
            @Override
            public void onTouchSubproject(int position) {
                Intent intent = new Intent(SearchWordActivity.this,
                        SearchNewGoodsActivity.class);
                intent.putExtra("keyword",((HotInfo)hotci.get(position)).getKeyword());
                startActivity(intent);

            }
        });
        mSearchnewHistory.setOnTouchSubprojectListener(new AutoNewLineLayout.OnTouchSubprojectListener() {
            @Override
            public void onTouchSubproject(int position) {
                try {
                    String keyword=historyCi.get(position);
                    dao.deteCI(historyCi.get(position));
                    dao.saveSS(historyCi.get(position));
                    historyCi.clear();
                    historyCi = dao.queryGoods();
                    addTagsHistory();
                    mSearchnewLlHistory.setVisibility(View.VISIBLE);
                    Intent intent = new Intent(SearchWordActivity.this,
                            SearchNewGoodsActivity.class);
                    intent.putExtra("keyword",keyword);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(SearchWordActivity.this,"请开启允许访问SD卡权限",Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    private class MyHandler extends Handler {
        private WeakReference<SearchWordActivity> mWeakReference = null;

        MyHandler(SearchWordActivity activity) {
            mWeakReference = new WeakReference<SearchWordActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            SearchWordActivity mShareIncomeDetails = mWeakReference.get();
            if (mShareIncomeDetails == null) {
                return;
            }

            if (Build.VERSION.SDK_INT >= 17) {
                if (mShareIncomeDetails == null || mShareIncomeDetails.isDestroyed() || mShareIncomeDetails.isFinishing()) {
                    return;
                }
            } else {
                if (mShareIncomeDetails == null || mShareIncomeDetails.isFinishing()) {

                    return;
                }
            }
            if (msg.what == 1) {
                try {
                    JSONObject json = new JSONObject((String) msg.obj);
                    String code = json.getString("code");
                    if (code.equals("0")) {
                        String data = json.getString("data");
                        hotci.clear();
                        hotci = ParseJsonCommon.parseJsonData(data,
                                HotInfo.class);
                        addTags();
                        mSearchnewLlHot.setVisibility(View.VISIBLE);

                    } else {
                        mSearchnewLlHot.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    mSearchnewLlHot.setVisibility(View.GONE);
                }
            }
            if (msg.what == 2) {
                mSearchnewLlHot.setVisibility(View.GONE);
            }
        }
    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("SearchWordActivity");
        MobclickAgent.onResume(this);


        if(isFirst){
            if (SplashActicity1.sdCardWritePermisson) {
                historyCi.clear();
                historyCi = dao.queryGoods();
                if(historyCi.size()>0){
                    addTagsHistory();
                    mSearchnewLlHistory.setVisibility(View.VISIBLE);
                }else{
                    mSearchnewLlHistory.setVisibility(View.GONE);
                }
            }
        }

        isFirst=true;



    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("SearchWordActivity");
        MobclickAgent.onPause(this);
    }

    private void addTags() {
        for (int i = 0; i < hotci.size(); i++) {
            View view = View.inflate(SearchWordActivity.this, R.layout.searchtext, null);
            TextView tv = (TextView) view.findViewById(R.id.search_text);
            tv.setMaxWidth(maxWidth);
            HotInfo info = (HotInfo) hotci.get(i);
            tv.setText(info.getKeyword());
            mSearchnewHot.addView(tv);
        }
    }

    private void addTagsHistory() {
        mSearchnewHistory.removeAllViews();
        for (int i = 0; i < historyCi.size(); i++) {
            View view = View.inflate(SearchWordActivity.this, R.layout.searchtext, null);
            TextView tv = (TextView) view.findViewById(R.id.search_text);
            tv.setMaxWidth(maxWidth);
            String info = historyCi.get(i);
            tv.setText(info);
            mSearchnewHistory.addView(tv);
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

            this.finish();
            overridePendingTransition(R.anim.in_from_left, R.anim.out_of_right);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @OnClick({R.id.ll_back, R.id.searchnew_tv_search, R.id.iv_guanbi, R.id.searchnew_ll_clear,R.id.searchnew_et_hint})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_back:
                SearchWordActivity.this.finish();
                overridePendingTransition(R.anim.in_from_left,
                        R.anim.out_of_right);
                break;
            case R.id.searchnew_tv_search:
                String historyOneCi = mSearchnewEtHint.getText().toString().trim();
                if (TextUtils.isEmpty(historyOneCi)) {
                    Toast.makeText(SearchWordActivity.this, "请输入关键词", Toast.LENGTH_LONG).show();
                    return;
                }
                if (SplashActicity1.sdCardWritePermisson) {
                    if (dao.queryGoods(historyOneCi) <= 0) {
                        dao.saveSS(historyOneCi);
                    } else {
                        dao.deteCI(historyOneCi);
                        dao.saveSS(historyOneCi);
                    }
                    historyCi.clear();
                    historyCi = dao.queryGoods();
                    addTagsHistory();
                    mSearchnewLlHistory.setVisibility(View.VISIBLE);
                }

                Intent intent = new Intent(SearchWordActivity.this,
                        SearchNewGoodsActivity.class);
                intent.putExtra("keyword",historyOneCi);
                startActivity(intent);

                SearchWordActivity.this.overridePendingTransition(
                        R.anim.to_right, R.anim.to_left);
                break;
            case R.id.iv_guanbi:
                mSearchnewEtHint.setText("");
                break;
            case R.id.searchnew_ll_clear:
                if (SplashActicity1.sdCardWritePermisson) {
                    dao.deteAllGoods();
                    historyCi.clear();
                    mSearchnewLlHistory.setVisibility(View.GONE);
                }
                break;
            case R.id.searchnew_et_hint:
                if(BooleanThread.toService){
                    if (NetJudgeUtils.getNetConnection(SearchWordActivity.this)){
                        ThreadPollFactory.getNormalPool().execute(new StatisticsThread(SearchWordActivity.this,6));
                    }
                }
             break;
        }

    }
}
