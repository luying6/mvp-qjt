package com.luying.mvp.example.retrofit;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * 创建人:luying
 * 创建时间:2018/5/4.
 */

public interface Api {

        // 注解GET：采用Get方法发送网络请求
        // Retrofit把网络请求的URL分成了2部分：1部分baseurl放在创建Retrofit对象时设置；另一部分在网络请求接口设置（即这里）
        // 如果接口里的URL是一个完整的网址，那么放在创建Retrofit对象时设置的部分可以不设置
        @GET("openapi.do?keyfrom=Yanzhikai&key=2032414398&type=data&doctype=json&version=1.1&q=car")
        // 接受网络请求数据的方法
        Call<JavaBean> getCall();
        // 返回类型为Call<*>，*是解析得到的数据类型，即JavaBean
}



