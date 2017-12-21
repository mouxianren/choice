package com.yiqi.choose.view;

import android.content.Context;
import android.graphics.PointF;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class MyViewPage2 extends ViewPager{
    /** 触摸时按下的点 **/
    PointF downP = new PointF();
    /** 触摸时当前的点 **/  
    PointF curP = new PointF();
    OnSingleTouchListener onSingleTouchListener;  
  
    public MyViewPage2(Context context, AttributeSet attrs) {
        super(context, attrs);  
        // TODO Auto-generated constructor stub  
    }  
  
    public MyViewPage2(Context context) {
        super(context);  
        // TODO Auto-generated constructor stub  
    }  
  
    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        // TODO Auto-generated method stub  
        //当拦截触摸事件到达此位置的时候，返回true，  
        //说明将onTouch拦截在此控件，进而执行此控件的onTouchEvent  
        return true;  
    }  
  
    @Override
    public boolean onTouchEvent(MotionEvent arg0) {
        // TODO Auto-generated method stub  
        //每次进行onTouch事件都记录当前的按下的坐标  
        curP.x = arg0.getX();  
        curP.y = arg0.getY();
        if(arg0.getAction() == MotionEvent.ACTION_DOWN){
            //记录按下时候的坐标  
            //切记不可用 downP = curP ，这样在改变curP的时候，downP也会改变  
            downP.x = arg0.getX();  
            downP.y = arg0.getY();  
            //此句代码是为了通知他的父ViewPager现在进行的是本控件的操作，不要对我的操作进行干扰  
            getParent().requestDisallowInterceptTouchEvent(true);

          //  (getParent().getParent().getParent().getParent().getParent()).requestDisallowInterceptTouchEvent(true);
        }  
  
        if(arg0.getAction() == MotionEvent.ACTION_MOVE){
            //此句代码是为了通知他的父ViewPager现在进行的是本控件的操作，不要对我的操作进行干扰  
           // getParent().requestDisallowInterceptTouchEvent(true); 
        	float dx = Math.abs(arg0.getX()-downP.x);
        	float dy = Math.abs(arg0.getY()-downP.y);
        	
        	if (dy<dx)
        	{
                //此句代码是为了通知他的父ViewPager现在进行的是本控件的操作，不要对我的操作进行干扰
	            getParent().requestDisallowInterceptTouchEvent(true);



        	}
        	else
        	{
                getParent().requestDisallowInterceptTouchEvent(false);

                //(getParent().getParent().getParent().getParent().getParent()).requestDisallowInterceptTouchEvent(false);
        	}
        	
        }  
  
        if(arg0.getAction() == MotionEvent.ACTION_UP){
            //在up时判断是否按下和松手的坐标为一个点  
            //如果是一个点，将执行点击事件，这是我自己写的点击事件，而不是onclick  
            if(downP.x==curP.x && downP.y==curP.y){  
            	System.out.println("downP.x==============="+downP.x);
                onSingleTouch(downP.x);  
                return true;  
            }  
        }  
  
        return super.onTouchEvent(arg0);  
    }  

        /** 
     * 单击 
     */  
    public void onSingleTouch(Float x) {
        if (onSingleTouchListener!= null) {  
  
            onSingleTouchListener.onSingleTouch(x);  
        }  
    }  
  
    /** 
     * 创建点击事件接口 
     * @author wanpg 
     * 
     */  
    public interface OnSingleTouchListener {  
        public void onSingleTouch(Float x);
    }  
  
	public void setOnSingleTouchListener(OnSingleTouchListener onSingleTouchListener) {  
        this.onSingleTouchListener = onSingleTouchListener;  
    }  
  
} 