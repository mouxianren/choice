package com.yiqi.choose.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yiqi.choose.R;
import com.yiqi.choose.model.GuanjianciInfo;
import com.yiqi.choose.utils.PicassoUtils;

import java.util.ArrayList;
import java.util.List;


public class Pro_type_adapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<GuanjianciInfo.ziCategorys> list;
    private Context context;
    private int height;

    public Pro_type_adapter(Context context, ArrayList<GuanjianciInfo.ziCategorys> lists,int height) {
        mInflater = LayoutInflater.from(context);

        this.context = context;
        this.height=height;
        if(lists!=null){
            this.list=lists;
        }else{
            list=new ArrayList<GuanjianciInfo.ziCategorys>();
        }
    }

    @Override
    public int getCount() {
        if (list != null && list.size() > 0)
            return list.size();
        else
            return 0;
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final MyView view;
        if (convertView == null) {
            view = new MyView();
            convertView = mInflater.inflate(R.layout.list_pro_type_item, null);
            view.icon = (ImageView) convertView.findViewById(R.id.typeicon);
            view.name = (TextView) convertView.findViewById(R.id.typename);
            convertView.setTag(view);
        } else {
            view = (MyView) convertView.getTag();
        }
        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.height=height;
        view.icon.setLayoutParams(params);

        if (list != null && list.size() > 0) {
           GuanjianciInfo.ziCategorys ziCategorys=list.get(position);
            view.name.setText(ziCategorys.getName());
            PicassoUtils.loadImageWithHolderAndError(context,ziCategorys.getImage(), R.mipmap.picture, R.mipmap.picture, view.icon);
        }

        return convertView;
    }


    private class MyView {
        private ImageView icon;
        private TextView name;
    }


}
