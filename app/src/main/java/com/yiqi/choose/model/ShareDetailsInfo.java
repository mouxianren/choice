package com.yiqi.choose.model;

/**
 * Created by moumou on 17/12/11.
 */

public class ShareDetailsInfo {
//    5.1  info
//    5.2  date
//    5.3  money
//    5.4  type 1:结算；2:提现
//
    private String info;
    private String date;
    private String money;
    private String type;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
