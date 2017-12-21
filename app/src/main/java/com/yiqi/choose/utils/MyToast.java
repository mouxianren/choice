package com.yiqi.choose.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yiqi.choose.R;


/**
 * 
 * @author Roy_se7en
 *
 */
public final class MyToast {
	
	/**
	 * @param context 当前上下文
	 */
//	public final static void show(Context context, String message) {
//		show(context, message, -1, Toast.LENGTH_SHORT);
//	}
//
//	/**
//	 * @param context 当前上下文
//	 * @param message 显示信息
//	 * @param color   显示信息的文字颜色
//	 */
//	public final static void show(Context context, String message, int color) {
//		show(context, message, color, Toast.LENGTH_LONG);
//	}
	
	public final static void show(Context context, int duration, int x, int height, String title) {

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View toastRoot = inflater.inflate(R.layout.toast, null);
		TextView tvMessage = (TextView) toastRoot.findViewById(R.id.message);
		tvMessage.setText(title);
//		if (color != -1) {
//			tvMessage.setTextColor(color);
//		}
		LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(x, LayoutParams.WRAP_CONTENT);
	//	LinearLayout ll=(LinearLayout) toastRoot.findViewById(R.id.tv_ll);
		tvMessage.setLayoutParams(layoutParams);
		Toast toastStart = new Toast(context);
		toastStart.setGravity(Gravity.TOP,0,height);
		toastStart.setDuration(duration);
		toastStart.setView(toastRoot);
		toastStart.show();

	}
	
	
//	public final static void show1(Context context, int duration, int x, int height, String title) {
//
//		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//		View toastRoot = inflater.inflate(R.layout.toast1, null);
//		TextView tvMessage = (TextView) toastRoot.findViewById(R.id.message);
//		tvMessage.setText(title);
//		LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(x, LayoutParams.WRAP_CONTENT);
//		tvMessage.setLayoutParams(layoutParams);
//		Toast toastStart = new Toast(context);
//		toastStart.setGravity(Gravity.TOP,0,height);
//		toastStart.setDuration(duration);
//		toastStart.setView(toastRoot);
//		toastStart.show();
//		//execToast(toastStart, 10);
//
//	}
	
//	public static void execToast(final Toast toast,int count) {
//        Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//               @Override
//               public void run() {
//                  while(count>0){  
//                     toast.show(); 
//                     count--;
//                  }
//               }
//        }, 1000);
//	}
//
//	/**
//	 * @param context 当前上下文
//	 * @param message 显示信息
//	 * @param color   显示信息的文字颜色(-1表示使用默认颜色)
//	 * @param duration消息停留时间
//	 */
//	public final static void show(Context context, int duration,int x) {
//
//		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//		View toastRoot = inflater.inflate(R.layout.toast, null);
//		TextView tvMessage = (TextView) toastRoot.findViewById(R.id.message);
//		//tvMessage.setText(message);
////		if (color != -1) {
////			tvMessage.setTextColor(color);
////		}
//		LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(x,LayoutParams.WRAP_CONTENT);
//	//	LinearLayout ll=(LinearLayout) toastRoot.findViewById(R.id.tv_ll);
//		tvMessage.setLayoutParams(layoutParams);
//		Toast toastStart = new Toast(context);
//		toastStart.setGravity(Gravity.BOTTOM, 0, 10);
//		toastStart.setDuration(duration);
//		toastStart.setView(toastRoot);
//		toastStart.show();
//
//	}
}
