package com.yiqi.choose.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.yiqi.choose.R;
import com.yiqi.choose.activity.zitypelist.ZitypeActivity;
import com.yiqi.choose.adapter.Pro_type_adapter;
import com.yiqi.choose.factory.ThreadPollFactory;
import com.yiqi.choose.model.GuanjianciInfo;
import com.yiqi.choose.thread.StatisticsTypeThread;
import com.yiqi.choose.utils.AndroidUtils;
import com.yiqi.choose.utils.BooleanThread;
import com.yiqi.choose.utils.MetricsUtils;
import com.yiqi.choose.utils.NetJudgeUtils;

import java.util.ArrayList;


public class Fragment_pro_type extends Fragment {
	private ImageView hint_img;
	private GridView listView;
	private Pro_type_adapter adapter;
	private String typename;
	private int position;
	private ArrayList<GuanjianciInfo.ziCategorys> list;
	private ArrayList<GuanjianciInfo.ziCategorys> list1;
private int pictureHeight=0;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_type_right, null);
		listView = (GridView) view.findViewById(R.id.listView);
		typename=getArguments().getString("typename");
		position=getArguments().getInt("position");
		list=new ArrayList<GuanjianciInfo.ziCategorys>();
		((TextView)view.findViewById(R.id.toptype)).setText(typename);
		list=((GuanjianciInfo)TypeFragment.typeList.get(position)).getCategorys();




		pictureHeight=(int)((AndroidUtils.getWidth(getActivity())*0.75- (double)(MetricsUtils.getDensity(getActivity())*40)));
		pictureHeight=(int)(pictureHeight*0.234568);

		adapter=new Pro_type_adapter(getActivity(),list,pictureHeight+1);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
//				System.out.println("posiont==="+arg2);
				GuanjianciInfo.ziCategorys info=list.get(arg2);
				Intent intent = new Intent(getActivity(),
						ZitypeActivity.class);
				try {
					intent.putExtra("adId",Integer.parseInt(info.getId()));
				} catch (NumberFormatException e) {
					e.printStackTrace();
					intent.putExtra("adId",0);
				}
				intent.putExtra("name",info.getName());

				if(BooleanThread.toService){
					if (NetJudgeUtils.getNetConnection(getActivity())){
						ThreadPollFactory.getNormalPool().execute(new StatisticsTypeThread(getActivity(),info.getId(),info.getName()));
					}
				}
				startActivity(intent);
				getActivity().overridePendingTransition(
						R.anim.to_right, R.anim.to_left);
			}
		});
		
		return view;
	}
	
	

}
