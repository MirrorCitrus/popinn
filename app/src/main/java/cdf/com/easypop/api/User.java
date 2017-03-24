/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package cdf.com.easypop.api;

/**
 * Created by cdf on 17/3/18.
 */
public class User {

    public final String name;
    public final String age;
    public final String address;
    public final String tel;

    public User(String name, int age, String address, String tel) {
        this.name = name;
        this.age = age + "";
        this.address = address;
        this.tel = tel;
    }
}
