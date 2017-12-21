package com.yiqi.choose.utils;


import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.widget.TextView;

import com.yiqi.choose.R;


public class CustomProgressDialog extends Dialog {
	private Context context = null;
	private static CustomProgressDialog customProgressDialog = null;
	
	public CustomProgressDialog(Context context){
		super(context);
		this.context = context;
	}
	public CustomProgressDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
    }
	
	public static void createDialog(Context context, int stringId){
		if(customProgressDialog == null)
		customProgressDialog = new CustomProgressDialog(context, R.style.MyProgressTheme);
		customProgressDialog.setCanceledOnTouchOutside(false);
		//customProgressDialog.setCancelable(false);
		customProgressDialog.setContentView(R.layout.customprogressdialog);
		customProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
		TextView tvMsg = (TextView)customProgressDialog.findViewById(R.id.id_tv_loadingmsg);
    	if (tvMsg != null){
    		tvMsg.setText(stringId);
    	}
		customProgressDialog.show();
	}
	public static boolean isShowing1(){
		  if (customProgressDialog != null){ 
			  if(customProgressDialog.isShowing()){
				  return true;
			  }else{
				  return false;
			  }
		  }
		return false;
	}
	public static void createDialog(Context context, String str){
		if(customProgressDialog == null)
		
			customProgressDialog = new CustomProgressDialog(context,R.style.MyProgressTheme);
		customProgressDialog.setCanceledOnTouchOutside(false);
		customProgressDialog.setContentView(R.layout.customprogressdialog);
		customProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
		TextView tvMsg = (TextView)customProgressDialog.findViewById(R.id.id_tv_loadingmsg);
    	if (tvMsg != null){
    		if(str == null)
    			tvMsg.setText("正在加载中...");
    		else
    			tvMsg.setText(str);
    	}
		customProgressDialog.show();
	}
	
	public static void stopProgressDialog(){  
        if (customProgressDialog != null){  
        	customProgressDialog.dismiss();  
        	customProgressDialog = null;  
        }  
    }
 
    public void onWindowFocusChanged(boolean hasFocus){
    	
    	if (customProgressDialog == null){
    		return;
    	}
//        ImageView imageView = (ImageView) customProgressDialog.findViewById(R.id.loadingImageView);
//        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getBackground();
//        animationDrawable.start();
    }
 
    /**
     * 
     * [Summary]
     *       setTitile 标题
     * @param strTitle
     * @return
     *
     */
    public CustomProgressDialog setTitile(String strTitle){
    	return customProgressDialog;
    }
    
    /**
     * 
     * [Summary]
     *       setMessage 提示内容
     * @param strMessage
     * @return
     *
     */
    public CustomProgressDialog setMessage(String strMessage){
    	TextView tvMsg = (TextView)customProgressDialog.findViewById(R.id.id_tv_loadingmsg);
    	
    	if (tvMsg != null){
    		tvMsg.setText(strMessage);
    	}
    	
    	return customProgressDialog;
    }
}
