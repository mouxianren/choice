package com.yiqi.choose.model;

/**
 * Created by moumou on 17/12/11.
 */

public class InviteBanner {
//    1.1.id      广告id
//    1.2.name   广告名称
//    1.3.image  图片地址
//    1.4.url     跳转地址跳转地址
            String id;
    String name;
    String image;
    String url;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
