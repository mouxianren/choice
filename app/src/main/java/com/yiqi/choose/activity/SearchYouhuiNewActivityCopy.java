package com.yiqi.choose.activity;

import com.yiqi.choose.base.BaseActivity;

/**
 * Created by moumou on 17/9/28.
 */

public class SearchYouhuiNewActivityCopy extends BaseActivity {
//    LinearLayout ll_back;
//    LinearLayout youhui_tv_search;
//    EditText search_et_hint;
//
//    LinearLayout all_search_ll_dosearch;
//    LinearLayout all_search_notsearch;
//
//
//    LinearLayout all_search_ll_out;
//    TextView all_search_tv_out;
//
//    LinearLayout all_search_ll_quanbu;
//    TextView all_search_tv_quanbu;
//    ImageView all_search_iv_quanbu;
//
//    LinearLayout all_search_ll_seller;
//    TextView all_search_tv_seller;
//
//    LinearLayout all_search_ll_price;
//    TextView all_search_tv_price;
//    ImageView all_search_iv_price;
//
//    RelativeLayout all_search_rl_listview;
//    ImageView all_search_home_arraw;
//    XRecyclerView all_search_listview;
//
//    LinearLayout all_search_nogoods;
//
//    LinearLayout all_search_ll_nowife;
//    TextView all_search_tv_retro;
//
//    RelativeLayout all_search_rl_pb;
//    private String[] names = {"全部", "巨划算", "淘抢货", "巨划算／淘抢货"};
//    int goodsType;
//    int order;
//
//    private PopuAdapter popuAdapter;
//    private PopupWindow pop;
//    private MyHandler hd;
//
//    int page = 1;
//    int maxPage = 0;
//
//    private List<Object> goodList;
//    private List<Object> ziList;
//    MyRecyclerViewAdapter adapter;
//
//    private String turnGoodsId;
//    private AlibcShowParams alibcShowParams;//页面打开方式，默认，H5，Native
//    private Map<String, String> exParams;//yhhpass参数
//    private AlibcTaokeParams alibcTaokeParams = null;//淘客参数，包括pid，unionid，subPid
//    RecyclerView.LayoutManager manager;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            setTranslucentStatus(true);
//            SystemBarTintManager tintManager = new SystemBarTintManager(this);
//            tintManager.setStatusBarTintEnabled(true);
//            tintManager.setStatusBarTintResource(R.color.topcolorred);//通知栏所需颜色
//        }
//        setContentView(R.layout.all_search_copy);
//
//        goodsType = 1;
//        order = 0;
//
//
//        ll_back = (LinearLayout) this.findViewById(R.id.ll_back);
//        youhui_tv_search = (LinearLayout) this.findViewById(R.id.youhui_tv_search);
//        search_et_hint = (EditText) this.findViewById(R.id.search_et_hint);
//
//        all_search_ll_dosearch = (LinearLayout) this.findViewById(R.id.all_search_ll_dosearch);
//        all_search_notsearch = (LinearLayout) this.findViewById(R.id.all_search_notsearch);
//
//
//        all_search_ll_out = (LinearLayout) this.findViewById(R.id.all_search_ll_out);
//        all_search_tv_out = (TextView) this.findViewById(R.id.all_search_tv_out);
//
//        all_search_ll_quanbu = (LinearLayout) this.findViewById(R.id.all_search_ll_quanbu);
//        all_search_tv_quanbu = (TextView) this.findViewById(R.id.all_search_tv_quanbu);
//        all_search_iv_quanbu = (ImageView) this.findViewById(R.id.all_search_iv_quanbu);
//
//        all_search_ll_seller = (LinearLayout) this.findViewById(R.id.all_search_ll_seller);
//        all_search_tv_seller = (TextView) this.findViewById(R.id.all_search_tv_seller);
//
//        all_search_ll_price = (LinearLayout) this.findViewById(R.id.all_search_ll_price);
//        all_search_tv_price = (TextView) this.findViewById(R.id.all_search_tv_price);
//        all_search_iv_price = (ImageView) this.findViewById(R.id.all_search_iv_price);
//
//        all_search_rl_listview = (RelativeLayout) this.findViewById(R.id.all_search_rl_listview);
//        all_search_home_arraw = (ImageView) this.findViewById(R.id.all_search_home_arraw);
//        all_search_listview = (XRecyclerView) this.findViewById(R.id.all_search_listview);
//        all_search_listview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
//        all_search_nogoods = (LinearLayout) this.findViewById(R.id.all_search_nogoods);
//
//        all_search_ll_nowife = (LinearLayout) this.findViewById(R.id.all_search_ll_nowife);
//        all_search_tv_retro = (TextView) this.findViewById(R.id.all_search_tv_retro);
//
//        all_search_rl_pb = (RelativeLayout) this.findViewById(R.id.all_search_rl_pb);
//
//        goodList = new ArrayList<Object>();
//        ziList = new ArrayList<Object>();
//
//        all_search_listview.setPullRefreshEnabled(false);
//        initData();
//    }
//
//    @TargetApi(19)
//    private void setTranslucentStatus(boolean on) {
//        Window win = getWindow();
//        WindowManager.LayoutParams winParams = win.getAttributes();
//        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
//        if (on) {
//            winParams.flags |= bits;
//        } else {
//            winParams.flags &= ~bits;
//        }
//        win.setAttributes(winParams);
//    }
//
//    private void initData() {
//        all_search_ll_dosearch.setVisibility(View.GONE);
//        all_search_notsearch.setVisibility(View.VISIBLE);
//        hd = new MyHandler(SearchYouhuiNewActivityCopy.this);
//
//        alibcTaokeParams = new AlibcTaokeParams(SplashActicity1.mPId, "", "");
//        alibcShowParams = new AlibcShowParams(OpenType.Native, false);
//        alibcShowParams.setClientType("taobao_scheme");
//        exParams = new HashMap<>();
//        exParams.put("isv_code", "appisvcode");
//        exParams.put("alibaba", "阿里巴巴");//自定义参数部分，可任意增删改
//        all_search_listview.setArrowImageView(R.drawable.arrow);
//        ll_back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                SearchYouhuiNewActivityCopy.this.finish();
//                overridePendingTransition(R.anim.in_from_left, R.anim.out_of_right);
//            }
//        });
//        all_search_tv_out.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (TextUtils.isEmpty(search_et_hint.getText().toString().trim())) {
//                    Toast.makeText(SearchYouhuiNewActivityCopy.this, "关键词不能为空!", Toast.LENGTH_LONG).show();
//                    return;
//                }
//                Intent intent = new Intent(SearchYouhuiNewActivityCopy.this,
//                        SearchYouhuiNewAllActivity.class);
//                intent.putExtra("keyword", search_et_hint.getText().toString().trim());
//                startActivity(intent);
//                overridePendingTransition(
//                        R.anim.to_right, R.anim.to_left);
//            }
//        });
//        youhui_tv_search.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (TextUtils.isEmpty(search_et_hint.getText().toString().trim())) {
//                    Toast.makeText(SearchYouhuiNewActivityCopy.this, "请输入关键词!", Toast.LENGTH_LONG).show();
//                    return;
//                }
//                //执行线程
//                if (NetJudgeUtils.getNetConnection(SearchYouhuiNewActivityCopy.this)) {
//                    AndroidUtils.hideInput(SearchYouhuiNewActivityCopy.this, search_et_hint);
//                    all_search_ll_dosearch.setVisibility(View.VISIBLE);
//                    all_search_notsearch.setVisibility(View.GONE);
//                    all_search_rl_listview.setVisibility(View.GONE);
//                    all_search_nogoods.setVisibility(View.GONE);
//                    all_search_ll_nowife.setVisibility(View.GONE);
//                    all_search_rl_pb.setVisibility(View.VISIBLE);
//                    ThreadPollFactory.getNormalPool().execute(new goodsThread(search_et_hint.getText().toString().trim(), 1));
//
//
//                } else {
//                    Toast.makeText(SearchYouhuiNewActivityCopy.this, R.string.net, Toast.LENGTH_LONG).show();
//                }
//
//                all_search_ll_quanbu.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        AndroidUtils.hideInput(SearchYouhuiNewActivityCopy.this, search_et_hint);
//
//                        ListView listView = new ListView(SearchYouhuiNewActivityCopy.this);
//                        listView.setBackgroundDrawable(getResources().getDrawable(
//                                R.drawable.ttg_grey_label_pp));
//                        listView.setDivider(getResources().getDrawable(R.color.newline));
//                        listView.setDividerHeight(1);
//                        popuAdapter = new PopuAdapter(names);
//                        listView.setAdapter(popuAdapter);
//                        pop = new PopupWindow(listView, all_search_ll_quanbu.getWidth(),
//                                ViewGroup.LayoutParams.WRAP_CONTENT, true);
//
//                        pop.setBackgroundDrawable(new ColorDrawable(0x00000000));
//                        pop.showAsDropDown(all_search_ll_quanbu, 0, 0);
//                    }
//                });
//                all_search_ll_seller.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (TextUtils.isEmpty(search_et_hint.getText().toString().trim())) {
//                            Toast.makeText(SearchYouhuiNewActivityCopy.this, "请输入关键词!", Toast.LENGTH_LONG).show();
//                            return;
//                        }
//                        AndroidUtils.hideInput(SearchYouhuiNewActivityCopy.this, search_et_hint);
//
//                        order = 1;
//                        all_search_tv_quanbu.setTextColor(getResources().getColor(R.color.newzigrey));
//                        all_search_iv_quanbu.setImageDrawable(getResources().getDrawable(R.mipmap.seach_grey_arrow));
//                        all_search_tv_price.setTextColor(getResources().getColor(R.color.newzigrey));
//                        all_search_tv_seller.setTextColor(getResources().getColor(R.color.red));
//                        all_search_iv_price.setImageDrawable(getResources().getDrawable(R.mipmap.price_nor));
//                        if (NetJudgeUtils.getNetConnection(SearchYouhuiNewActivityCopy.this)) {
//
//                            all_search_rl_listview.setVisibility(View.GONE);
//                            all_search_nogoods.setVisibility(View.GONE);
//                            all_search_ll_nowife.setVisibility(View.GONE);
//                            all_search_rl_pb.setVisibility(View.VISIBLE);
//
//                            ThreadPollFactory.getNormalPool().execute(new goodsThread(search_et_hint.getText().toString().trim(), 1));
//
//                        } else {
//                            all_search_rl_listview.setVisibility(View.GONE);
//                            all_search_nogoods.setVisibility(View.GONE);
//                            all_search_ll_nowife.setVisibility(View.VISIBLE);
//                            all_search_rl_pb.setVisibility(View.GONE);
//
//                        }
//                    }
//
//                });
//                all_search_ll_price.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (TextUtils.isEmpty(search_et_hint.getText().toString().trim())) {
//                            Toast.makeText(SearchYouhuiNewActivityCopy.this, "请输入关键词!", Toast.LENGTH_LONG).show();
//                            return;
//                        }
//                        AndroidUtils.hideInput(SearchYouhuiNewActivityCopy.this, search_et_hint);
//
//                        if (order != 2) {
//                            order = 2;
//                            all_search_tv_quanbu.setTextColor(getResources().getColor(R.color.newzigrey));
//                            all_search_iv_quanbu.setImageDrawable(getResources().getDrawable(R.mipmap.seach_grey_arrow));
//                            all_search_tv_price.setTextColor(getResources().getColor(R.color.red));
//                            all_search_tv_seller.setTextColor(getResources().getColor(R.color.newzigrey));
//                            all_search_iv_price.setImageDrawable(getResources().getDrawable(R.mipmap.price_down));
//                        } else {
//                            order = 3;
//                            all_search_tv_quanbu.setTextColor(getResources().getColor(R.color.newzigrey));
//                            all_search_iv_quanbu.setImageDrawable(getResources().getDrawable(R.mipmap.seach_grey_arrow));
//                            all_search_tv_price.setTextColor(getResources().getColor(R.color.red));
//                            all_search_tv_seller.setTextColor(getResources().getColor(R.color.newzigrey));
//                            all_search_iv_price.setImageDrawable(getResources().getDrawable(R.mipmap.price_up));
//                        }
//                        if (NetJudgeUtils.getNetConnection(SearchYouhuiNewActivityCopy.this)) {
//                            all_search_rl_listview.setVisibility(View.GONE);
//                            all_search_nogoods.setVisibility(View.GONE);
//                            all_search_ll_nowife.setVisibility(View.GONE);
//                            all_search_rl_pb.setVisibility(View.VISIBLE);
//                            ThreadPollFactory.getNormalPool().execute(new goodsThread(search_et_hint.getText().toString().trim(), 1));
//                        } else {
//                            all_search_rl_listview.setVisibility(View.GONE);
//                            all_search_nogoods.setVisibility(View.GONE);
//                            all_search_ll_nowife.setVisibility(View.VISIBLE);
//                            all_search_rl_pb.setVisibility(View.GONE);
//                        }
//                    }
//                });
//            }
//        });
//
//        all_search_tv_retro.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (TextUtils.isEmpty(search_et_hint.getText().toString().trim())) {
//                    Toast.makeText(SearchYouhuiNewActivityCopy.this, "请输入关键词!", Toast.LENGTH_LONG).show();
//                    return;
//                }
//                AndroidUtils.hideInput(SearchYouhuiNewActivityCopy.this, search_et_hint);
//                if (NetJudgeUtils.getNetConnection(SearchYouhuiNewActivityCopy.this)) {
//                    all_search_rl_listview.setVisibility(View.GONE);
//                    all_search_nogoods.setVisibility(View.GONE);
//                    all_search_ll_nowife.setVisibility(View.GONE);
//                    all_search_rl_pb.setVisibility(View.VISIBLE);
//                    ThreadPollFactory.getNormalPool().execute(new goodsThread(search_et_hint.getText().toString().trim(), 1));
//                } else {
//                    all_search_rl_listview.setVisibility(View.GONE);
//                    all_search_nogoods.setVisibility(View.GONE);
//                    all_search_ll_nowife.setVisibility(View.VISIBLE);
//                    all_search_rl_pb.setVisibility(View.GONE);
//                }
//            }
//        });
//        all_search_listview.setLoadingListener(new XRecyclerView.LoadingListener() {
//            @Override
//            public void onRefresh() {
//                if (TextUtils.isEmpty(search_et_hint.getText().toString().trim())) {
//                    Toast.makeText(SearchYouhuiNewActivityCopy.this, "请输入关键词!", Toast.LENGTH_LONG).show();
//                    hd.sendEmptyMessage(9);
//                } else {
//                    if (NetJudgeUtils.getNetConnection(SearchYouhuiNewActivityCopy.this)) {
//                        ThreadPollFactory.getNormalPool().execute(new goodsThread(search_et_hint.getText().toString().trim(), 1));
//                    } else {
//                        hd.sendEmptyMessage(7);
//                    }
//                }
//            }
//
//            @Override
//            public void onLoadMore() {
//
//                    if (TextUtils.isEmpty(search_et_hint.getText().toString().trim())) {
//                        Toast.makeText(SearchYouhuiNewActivityCopy.this, "请输入关键词!", Toast.LENGTH_LONG).show();
//                        hd.sendEmptyMessage(10);
//                    } else {
//                        if (NetJudgeUtils.getNetConnection(SearchYouhuiNewActivityCopy.this)) {
//                            page++;
//                            ThreadPollFactory.getNormalPool().execute(new goodsThread(search_et_hint.getText().toString().trim(), page));
//                        } else {
//                            hd.sendEmptyMessage(8);
//                        }
//                    }
//            }
//        });
//
//        all_search_listview.setOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                if(manager==null){
//                    manager  =recyclerView.getLayoutManager();
//                }
//
//                if(manager instanceof LinearLayoutManager){
//                    LinearLayoutManager linearManager = (LinearLayoutManager) manager;
//                    int positon=linearManager.findFirstVisibleItemPosition();
//                    if (positon > 22) {
//                        all_search_home_arraw.setVisibility(View.VISIBLE);
//                    } else {
//                        all_search_home_arraw.setVisibility(View.GONE);
//                    }
//                }
//
//            }
//
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                final Picasso picasso = Picasso.with(SearchYouhuiNewActivityCopy.this);
//                if (newState ==RecyclerView.SCROLL_STATE_IDLE || newState ==RecyclerView.SCROLL_STATE_DRAGGING) {
//                    picasso.resumeTag(SearchYouhuiNewActivityCopy.this);
//                } else {
//                    picasso.pauseTag(SearchYouhuiNewActivityCopy.this);
//                }
//            }
//        });
//
//        all_search_home_arraw.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//              MoveToPosition((LinearLayoutManager)all_search_listview.getLayoutManager(),all_search_listview,0);
//            }
//        });
////        all_search_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
////            @Override
////            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
////                if (NetJudgeUtils.getNetConnection(SearchYouhuiNewActivityCopy.this)) {
////                    CustomProgressDialog.createDialog(SearchYouhuiNewActivityCopy.this, "");
////                    GoodsInfo info = (GoodsInfo) goodList.get(position - 1);
////                    turnGoodsId = info.getNumIid();
////                    ThreadPollFactory.getNormalPool().execute(new TurnUrlThread(info.getNumIid() + "", SearchYouhuiNewActivityCopy.this, hd));
////                    //AlibcTrade.show(JinxuanActivity.this, new AlibcPage(info.getGoodsUrl()),alibcShowParams, alibcTaokeParams, exParams , new DemoTradeCallback());
////
////                } else {
////                    Toast.makeText(SearchYouhuiNewActivityCopy.this, "您的网络不给力，请检查更新!", Toast.LENGTH_LONG).show();
////                }
////
////
////            }
////        });
//    }
//
//
//    public void onResume() {
//        super.onResume();
//        MobclickAgent.onPageStart("SearchYouhuiNewActivity");
//        MobclickAgent.onResume(this);
//
//    }
//
//    public void onPause() {
//        super.onPause();
//        MobclickAgent.onPageEnd("SearchYouhuiNewActivity");
//        MobclickAgent.onPause(this);
//    }
//
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
//
//            this.finish();
//            overridePendingTransition(R.anim.in_from_left, R.anim.out_of_right);
//            return true;
//        }
//
//        return super.onKeyDown(keyCode, event);
//    }
//
//    private class PopuAdapter extends BaseAdapter {
//        private String[] names;
//
//        public PopuAdapter(String[] names) {
//            this.names = names;
//        }
//
//        public int getCount() {
//            // TODO Auto-generated method stub
//            return names.length;
//        }
//
//        public Object getItem(int position) {
//            // TODO Auto-generated method stub
//            return names[position];
//        }
//
//        public long getItemId(int position) {
//            // TODO Auto-generated method stub
//            return position;
//        }
//
//        public View getView(final int position, View convertView,
//                            ViewGroup parent) {
//
//            View view = View.inflate(SearchYouhuiNewActivityCopy.this, R.layout.poptext,
//                    null);
//            final TextView tv_name = (TextView) view.findViewById(R.id.tv_type);
//
//
//            tv_name.setText(names[position]);
//
//            if (goodsType == 1) {
//                if (position == 0) {
//                    tv_name.setTextColor(getResources().getColor(R.color.red));
//                } else {
//                    tv_name.setTextColor(getResources().getColor(R.color.newzigrey));
//                }
//            }
//            if (goodsType == 2) {
//                if (position == 3) {
//                    tv_name.setTextColor(getResources().getColor(R.color.red));
//                } else {
//                    tv_name.setTextColor(getResources().getColor(R.color.newzigrey));
//                }
//            }
//            if (goodsType == 3) {
//                if (position == 2) {
//                    tv_name.setTextColor(getResources().getColor(R.color.red));
//                } else {
//                    tv_name.setTextColor(getResources().getColor(R.color.newzigrey));
//                }
//            }
//            if (goodsType == 4) {
//                if (position == 1) {
//                    tv_name.setTextColor(getResources().getColor(R.color.red));
//                } else {
//                    tv_name.setTextColor(getResources().getColor(R.color.newzigrey));
//                }
//            }
//
//            tv_name.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (TextUtils.isEmpty(search_et_hint.getText().toString().trim())) {
//                        pop.dismiss();
//                        Toast.makeText(SearchYouhuiNewActivityCopy.this, "请输入关键词!", Toast.LENGTH_LONG).show();
//                        return;
//                    }
//                    if (goodsType == 1) {
//                        if (!tv_name.getText().toString().trim().equals("全部")) {
//                            //   private String[] names = {"全部", "巨划算", "淘抢货","巨划算／淘抢货"};
//                            if (tv_name.getText().toString().trim().equals("巨划算")) {
//                                goodsType = 4;
//                            }
//                            if (tv_name.getText().toString().trim().equals("淘抢货")) {
//                                goodsType = 3;
//                            }
//                            if (tv_name.getText().toString().trim().equals("巨划算／淘抢货")) {
//                                goodsType = 2;
//                            }
//                            pop.dismiss();
//                            changeType();
//                        }
//
//                    }
//                    if (goodsType == 2) {
//                        if (!tv_name.getText().toString().trim().equals("巨划算／淘抢货")) {
//                            //   private String[] names = {"全部", "巨划算", "淘抢货","巨划算／淘抢货"};
//                            if (tv_name.getText().toString().trim().equals("巨划算")) {
//                                goodsType = 4;
//                            }
//                            if (tv_name.getText().toString().trim().equals("淘抢货")) {
//                                goodsType = 3;
//                            }
//                            if (tv_name.getText().toString().trim().equals("全部")) {
//                                goodsType = 1;
//                            }
//                            pop.dismiss();
//                            changeType();
//                        }
//
//                    }
//                    if (goodsType == 3) {
//                        if (!tv_name.getText().toString().trim().equals("淘抢货")) {
//                            //   private String[] names = {"全部", "巨划算", "淘抢货","巨划算／淘抢货"};
//                            if (tv_name.getText().toString().trim().equals("巨划算")) {
//                                goodsType = 4;
//                            }
//                            if (tv_name.getText().toString().trim().equals("全部")) {
//                                goodsType = 1;
//                            }
//                            if (tv_name.getText().toString().trim().equals("巨划算／淘抢货")) {
//                                goodsType = 2;
//                            }
//                            pop.dismiss();
//                            changeType();
//                        }
//
//                    }
//                    if (goodsType == 4) {
//                        if (!tv_name.getText().toString().trim().equals("巨划算")) {
//                            //   private String[] names = {"全部", "巨划算", "淘抢货","巨划算／淘抢货"};
//                            if (tv_name.getText().toString().trim().equals("全部")) {
//                                goodsType = 1;
//                            }
//                            if (tv_name.getText().toString().trim().equals("淘抢货")) {
//                                goodsType = 3;
//                            }
//                            if (tv_name.getText().toString().trim().equals("巨划算／淘抢货")) {
//                                goodsType = 2;
//                            }
//                            pop.dismiss();
//                            changeType();
//                        }
//
//                    }
//
//
//                }
//            });
//            return view;
//        }
//
//
//    }
//
//    private void changeType() {
//
//        order = 0;
//        //执行下一个程序
//        all_search_rl_listview.setVisibility(View.GONE);
//        all_search_nogoods.setVisibility(View.GONE);
//        all_search_ll_nowife.setVisibility(View.GONE);
//        all_search_rl_pb.setVisibility(View.VISIBLE);
//        //执行现形转换
//        if (goodsType == 1) {
//            all_search_tv_quanbu.setText(names[0]);
//        }
//        if (goodsType == 2) {
//            all_search_tv_quanbu.setText("淘／巨");
//        }
//        if (goodsType == 3) {
//            all_search_tv_quanbu.setText(names[2]);
//        }
//        if (goodsType == 4) {
//            all_search_tv_quanbu.setText(names[1]);
//        }
//        all_search_tv_quanbu.setTextColor(getResources().getColor(R.color.red));
//        all_search_iv_quanbu.setImageDrawable(getResources().getDrawable(R.mipmap.seach_red_arrow));
//        all_search_tv_price.setTextColor(getResources().getColor(R.color.newzigrey));
//        all_search_tv_seller.setTextColor(getResources().getColor(R.color.newzigrey));
//        all_search_iv_price.setImageDrawable(getResources().getDrawable(R.mipmap.price_nor));
//
//        AndroidUtils.hideInput(SearchYouhuiNewActivityCopy.this, search_et_hint);
//
//        //zhixing xincheng
//        if (NetJudgeUtils.getNetConnection(SearchYouhuiNewActivityCopy.this)) {
//            ThreadPollFactory.getNormalPool().execute(new goodsThread(search_et_hint.getText().toString().trim(), 1));
//        } else {
//            all_search_rl_listview.setVisibility(View.GONE);
//            all_search_nogoods.setVisibility(View.GONE);
//            all_search_ll_nowife.setVisibility(View.VISIBLE);
//            all_search_rl_pb.setVisibility(View.GONE);
//        }
//    }
//
//    /**
//     * 的到圈值接口
//     */
//    private class goodsThread implements Runnable {
//
//        int page;
//        String keyword;
//
//
//        public goodsThread(String keyword, int page) {
//
//            this.keyword = keyword;
//            this.page = page;
//        }
//
//        @Override
//        public void run() {
//            try {
//                String url = getResources().getString(R.string.appurl) + "/goods2/searchinall" + "?page=" + page + "&keyword=" + keyword + "&goodsType=" + goodsType + "&order=" + order + "&stamp=" + UrlUtils.getTime() + "&encode=" + UrlUtils.getEncode();
//                String jsonData = HttpConBase.sendGet(url);
//                Message msg = Message.obtain();
//                if (page <= 1) {
//                    msg.what = 3;
//                    msg.obj = jsonData;
//                    hd.sendMessage(msg);
//                } else {
//                    msg.what = 4;
//                    msg.obj = jsonData;
//
//                    hd.sendMessage(msg);
//                }
//
//            } catch (Exception e) {
//                e.printStackTrace();
//                if (page == 1) {
//                    hd.sendEmptyMessage(5);
//                } else {
//                    hd.sendEmptyMessage(6);
//                }
//
//            }
//
//        }
//    }
//
//    private class MyHandler extends Handler {
//        private WeakReference<SearchYouhuiNewActivityCopy> mWeakReference = null;
//
//        MyHandler(SearchYouhuiNewActivityCopy activity) {
//            mWeakReference = new WeakReference<SearchYouhuiNewActivityCopy>(activity);
//        }
//
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            SearchYouhuiNewActivityCopy mainActivity = mWeakReference.get();
//            if (mainActivity == null) {
//                return;
//            }
//            if (Build.VERSION.SDK_INT >= 17) {
//                if (mainActivity == null || mainActivity.isDestroyed() || mainActivity.isFinishing()) {
//                    return;
//                }
//            } else {
//                if (mainActivity == null || mainActivity.isFinishing()) {
//                    return;
//                }
//            }
//
//            if (msg.what == 3) {
//                try {
//                    JSONObject j = new JSONObject((String) msg.obj);
//                    String code = j.getString("code");
//
//                    if (code.equals("0")) {
//                        page = 1;
//                        String data = j.getString("data");
//                        JSONObject jData = new JSONObject(data);
//                        String goods = jData.getString("goods");
//                        maxPage = jData.getInt("totalpage");
//                        if (goods.equals("[]") || null == goods || goods.equals("null")) {
//                            all_search_rl_listview.setVisibility(View.GONE);
//                            all_search_nogoods.setVisibility(View.VISIBLE);
//                            all_search_ll_nowife.setVisibility(View.GONE);
//                            all_search_rl_pb.setVisibility(View.GONE);
//                        } else {
//                            goodList.clear();
//                            goodList = ParseJsonCommon.parseJsonData(goods,
//                                    GoodsInfo.class);
//
//                            if (goodList.size() > 0) {
//                                all_search_rl_listview.setVisibility(View.VISIBLE);
//                                all_search_nogoods.setVisibility(View.GONE);
//                                all_search_ll_nowife.setVisibility(View.GONE);
//                                all_search_rl_pb.setVisibility(View.GONE);
//                                if (maxPage > 1) {
//                                    all_search_listview.setLoadingMoreEnabled(true);
//                                } else {
//                                    all_search_listview.setLoadingMoreEnabled(false);
//                                }
//                                adapter = new MyRecyclerViewAdapter();
//
//                               all_search_listview.setAdapter(adapter);
//                               all_search_listview.refreshComplete();
//                            } else {
//                                all_search_rl_listview.setVisibility(View.GONE);
//                                all_search_nogoods.setVisibility(View.VISIBLE);
//                                all_search_ll_nowife.setVisibility(View.GONE);
//                                all_search_rl_pb.setVisibility(View.GONE);
//                            }
//                        }
//
//
//                    } else {
//                        all_search_rl_listview.setVisibility(View.GONE);
//                        all_search_nogoods.setVisibility(View.GONE);
//                        all_search_ll_nowife.setVisibility(View.VISIBLE);
//                        all_search_rl_pb.setVisibility(View.GONE);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    all_search_rl_listview.setVisibility(View.GONE);
//                    all_search_nogoods.setVisibility(View.GONE);
//                    all_search_ll_nowife.setVisibility(View.VISIBLE);
//                    all_search_rl_pb.setVisibility(View.GONE);
//                }
//            }
//            if (msg.what == 4) {
//                try {
//                    JSONObject j = new JSONObject((String) msg.obj);
//                    String code = j.getString("code");
//                    if (code.equals("0")) {
//                        String data = j.getString("data");
//                        JSONObject jData = new JSONObject(data);
//                        String goods = jData.getString("goods");
//                        maxPage = jData.getInt("totalpage");
//                        ziList.clear();
//                        ziList = ParseJsonCommon.parseJsonData(goods,
//                                GoodsInfo.class);
//                        goodList.addAll(ziList);
//
//                        if (maxPage > 1) {
//                            all_search_listview.setLoadingMoreEnabled(true);
//                        } else {
//                            all_search_listview.setLoadingMoreEnabled(false);
//                        }
//                        adapter.notifyDataSetChanged();// 告诉listView数据发生改变，要求listView更新显示
//                      //  all_search_listview.refreshComplete();
//                    } else {
//                        if (page > 1) {
//                            page--;
//                        }
////                        all_search_listview.refreshComplete();
////                        all_search_listview.loadMoreComplete();
//                        Toast.makeText(mainActivity, "数据返回错误!", Toast.LENGTH_SHORT).show();
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
////                    all_search_listview.refreshComplete();
////                    all_search_listview.loadMoreComplete();
//                }
//            }
//            if (msg.what == 5) {
////                all_search_listview.refreshComplete();
////                all_search_listview.loadMoreComplete();
//                all_search_rl_listview.setVisibility(View.GONE);
//                all_search_nogoods.setVisibility(View.GONE);
//                all_search_ll_nowife.setVisibility(View.VISIBLE);
//                all_search_rl_pb.setVisibility(View.GONE);
//            }
//            if (msg.what == 6) {
//                Toast.makeText(mainActivity, "数据返回错误!", Toast.LENGTH_SHORT).show();
//                if (page > 1) {
//                    page--;
//                }
//               all_search_listview.loadMoreComplete();
//            }
//            if (msg.what == 7) {
//                MyToast.show(mainActivity, 0, AndroidUtils.getWidth(mainActivity),
//                        AndroidUtils.dip2px(mainActivity, 45), "您的网络不给力，请检查更新!");
//              // all_search_listview.refreshComplete();
//            }
//            if (msg.what == 8) {
//
//                all_search_listview.loadMoreComplete();
//
//            }
//            if (msg.what == 9) {
//                MyToast.show(mainActivity, 0, AndroidUtils.getWidth(mainActivity),
//                        AndroidUtils.dip2px(mainActivity, 45), "关键词不能为空！");
//                //all_search_listview.refreshComplete();
//            }
//            if (msg.what == 10) {
//                Toast.makeText(mainActivity, "关键词不能为空!", Toast.LENGTH_LONG).show();
//               // all_search_listview.refreshComplete();
//
//            }
//            if (msg.what == 11) {
//                try {
//                    JSONObject j = new JSONObject((String) msg.obj);
//                    String code = j.getString("code");
//                    if (code.equals("0")) {
//                        String data = j.getString("data");
//                        //  data="http:www.host.com";
//                        //
//                        //                       if(TextUtils.isEmpty(data)){
//                        //                           cmb.setText("复制的地址为空");
//                        //                       }else{
//                        //                           cmb.setText("优惠券地址为："+data);
//                        //                       }
//                        //                       Toast.makeText(getActivity(), "优惠券地址已粘贴!", Toast.LENGTH_LONG).show();
//
//
//                        // System.out.println("data优惠="+data);
//                        if (NetJudgeUtils.getNetConnection(mainActivity)) {
//                            if (TextUtils.isEmpty(data)) {
//                                AlibcTrade.show(mainActivity, new AlibcDetailPage(StringUtils.getID(turnGoodsId)), alibcShowParams, alibcTaokeParams, exParams, new DemoTradeCallback());
//
//                            } else {
//                                AlibcTrade.show(mainActivity, new AlibcPage(data), alibcShowParams, null, exParams, new DemoTradeCallback());
//                                //AlibaPageDetails
//                            }
//                        } else {
//                            Toast.makeText(mainActivity, "您的网络不给力，请检查更新!", Toast.LENGTH_LONG).show();
//
//                        }
//
//
//                        // AlibcTrade.show(JuhuasuanActivity.this, new AlibcDetailPage(StringUtils.getID(turnGoodsId)), alibcShowParams, alibcTaokeParams, exParams , new DemoTradeCallback());
//
//
//                    } else {
//                        if (NetJudgeUtils.getNetConnection(mainActivity)) {
//                            AlibcTrade.show(mainActivity, new AlibcDetailPage(StringUtils.getID(turnGoodsId)), alibcShowParams, alibcTaokeParams, exParams, new DemoTradeCallback());
//
//                        } else {
//                            Toast.makeText(mainActivity, "您的网络不给力，请检查更新!", Toast.LENGTH_LONG).show();
//
//                        }
//
//                        //  Toast.makeText(JuhuasuanActivity.this, "服务器错误!", Toast.LENGTH_LONG).show();
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    if (NetJudgeUtils.getNetConnection(mainActivity)) {
//                        AlibcTrade.show(mainActivity, new AlibcDetailPage(StringUtils.getID(turnGoodsId)), alibcShowParams, alibcTaokeParams, exParams, new DemoTradeCallback());
//                    } else {
//                        Toast.makeText(mainActivity, "您的网络不给力，请检查更新!", Toast.LENGTH_LONG).show();
//
//                    }
//                } finally {
//                    CustomProgressDialog.stopProgressDialog();
//                }
//            }
//            if (msg.what == 12) {
//                CustomProgressDialog.stopProgressDialog();
//                if (NetJudgeUtils.getNetConnection(mainActivity)) {
//                    AlibcTrade.show(mainActivity, new AlibcDetailPage(StringUtils.getID(turnGoodsId)), alibcShowParams, alibcTaokeParams, exParams, new DemoTradeCallback());
//                } else {
//                    Toast.makeText(mainActivity, "您的网络不给力，请检查更新!", Toast.LENGTH_LONG).show();
//
//                }
//            }
//        }
//    }
//
//
//
//
//    class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.MyViewHolder> {
//
//
//
//        public MyRecyclerViewAdapter() {
//
//        }
//
//        @Override
//        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
//            View view = inflater.inflate(R.layout.layout_youhui_item, parent, false);
//            MyViewHolder myViewHolder = new MyViewHolder(view);
//            return myViewHolder;
//        }
//
//        @Override
//        public void onBindViewHolder(MyViewHolder holder, int position) {
//            final GoodsInfo goodsInfo;
//            goodsInfo = (GoodsInfo) goodList.get(position);
//            PicassoUtils.loadImageWithHolderAndError(SearchYouhuiNewActivityCopy.this, goodsInfo.getGoodsImage(), R.mipmap.picture, R.mipmap.picture, holder.good_img);
//            holder.goods_desc.setText(goodsInfo.getTitle());
//            holder.goods_discount_price.setText(goodsInfo.getPrice());
//            holder.goods_ori_price.setText("￥" + goodsInfo.getOldPrice());
//            holder.tv_chengji.setText("已售" + goodsInfo.getSellCount() + "件");
//            holder.home_temai_discount.setText(goodsInfo.getSavePrice());
//            holder.home_temai_shopname.setText(goodsInfo.getGoodsShop());
//        }
//
//        @Override
//        public int getItemCount() {
//            return goodList.size();
//        }
//
//        public class MyViewHolder extends RecyclerView.ViewHolder {
//            ImageView good_img;
//            TextView goods_desc;
//            TextView goods_discount_price;
//            TextView goods_ori_price;
//            TextView tv_chengji;
//            TextView home_temai_shopname;
//            TextView home_temai_discount;
//            public MyViewHolder(View itemView) {
//                super(itemView);
//               good_img = (ImageView) itemView.findViewById(R.id.good_img);
//                goods_desc = (TextView) itemView.findViewById(R.id.goods_desc);
//               goods_discount_price = (TextView) itemView.findViewById(R.id.goods_discount_price);
//               goods_ori_price = (TextView) itemView.findViewById(R.id.goods_ori_price);
//               tv_chengji = (TextView) itemView.findViewById(R.id.tv_chengji);
//               home_temai_shopname = (TextView) itemView.findViewById(R.id.home_temai_shopname);
//                home_temai_discount = (TextView) itemView.findViewById(R.id.home_temai_discount);
//               goods_ori_price.getPaint().setFlags(
//                        Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
//            }
//        }
//    }
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        CustomProgressDialog.stopProgressDialog();
//    }
//
//    public static void MoveToPosition(LinearLayoutManager manager, RecyclerView mRecyclerView, int n) {
//
//
//        int firstItem = manager.findFirstVisibleItemPosition();
//        int lastItem = manager.findLastVisibleItemPosition();
//        if (n <= firstItem) {
//            mRecyclerView.scrollToPosition(n);
//        } else if (n <= lastItem) {
//            int top = mRecyclerView.getChildAt(n - firstItem).getTop();
//            mRecyclerView.scrollBy(0, top);
//        } else {
//            mRecyclerView.scrollToPosition(n);
//        }
//
//    }

}
