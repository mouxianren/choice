package com.yiqi.choose.model;

import java.util.ArrayList;

/**
 * Created by moumou on 17/12/12.
 */

public class GuanjianciInfo {
    private String id;
    private String name;
    private ArrayList<ziCategorys> categorys;

    public ArrayList<ziCategorys> getCategorys() {
        return categorys;
    }

    public void setCategorys(ArrayList<ziCategorys> categorys) {
        this.categorys = categorys;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

   public class ziCategorys{
        private String id;
        private String name;
        private String image;

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
    }
}
