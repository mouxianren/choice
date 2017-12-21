package com.yiqi.choose.utils;

import com.alibaba.baichuan.trade.biz.login.AlibcLogin;

/**
 * Created by moumou on 17/8/11.
 */

public class JudgeLoginTaobao {
    //判断淘宝是否登录
    public static boolean isLogin(){
        if(AlibcLogin.getInstance().isLogin()){
            return true;
        }else{
            return false;
        }
//        if((null== AlibcLogin.getInstance().getSession()
//                ||"null".equals(AlibcLogin.getInstance().getSession())
//                ||"".equals(AlibcLogin.getInstance().getSession()))
//                ||(null==AlibcLogin.getInstance().getSession().openId
//                ||"null".equals(AlibcLogin.getInstance().getSession().openId)
//                ||"".equals(AlibcLogin.getInstance().getSession().openId))){
//            return false;
//        }else {
//           return true;
//       }
    }
}
