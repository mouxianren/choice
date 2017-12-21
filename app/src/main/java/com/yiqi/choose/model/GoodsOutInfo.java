package com.yiqi.choose.model;

/**
 * Created by moumou on 17/8/15.
 */

public class GoodsOutInfo {
  private int goodsId;
    private String numIid;
    private String title;
    private String oldPrice;
   private String price;
    private String sellCount;
   private String goodsImage;
    private String goodsUrl;// 链接地址
    private String goodsShop;//店铺名称
    private String savePrice;//省钱
    private String discount;//折扣
    private String goodsType;


    public String getGoodsType() {
        return goodsType;
    }

    public void setGoodsType(String goodsType) {
        this.goodsType = goodsType;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public int getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(int goodsId) {
        this.goodsId = goodsId;
    }

    public String getGoodsImage() {
        return goodsImage;
    }

    public void setGoodsImage(String goodsImage) {
        this.goodsImage = goodsImage;
    }

    public String getGoodsShop() {
        return goodsShop;
    }

    public void setGoodsShop(String goodsShop) {
        this.goodsShop = goodsShop;
    }

    public String getGoodsUrl() {
        return goodsUrl;
    }

    public void setGoodsUrl(String goodsUrl) {
        this.goodsUrl = goodsUrl;
    }

    public String getNumIid() {
        return numIid;
    }

    public void setNumIid(String  numIid) {
        this.numIid = numIid;
    }

    public String getOldPrice() {
        return oldPrice;
    }

    public void setOldPrice(String oldPrice) {
        this.oldPrice = oldPrice;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSavePrice() {
        return savePrice;
    }

    public void setSavePrice(String savePrice) {
        this.savePrice = savePrice;
    }

    public String getSellCount() {
        return sellCount;
    }

    public void setSellCount(String sellCount) {
        this.sellCount = sellCount;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
