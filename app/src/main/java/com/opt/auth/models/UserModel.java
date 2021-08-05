package com.opt.auth.models;

import java.util.ArrayList;
import java.util.List;

public class UserModel {
    String email;
    String password;
    String fullName;
    //String phone;
    List<String> url;
    public UserModel(String email, String password, String fullName) {
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.url=new ArrayList();
    }

    public List<String> getUrl() {
        return url;
    }

    public void setUrl(List<String> url) {
        this.url = url;
    }
}
