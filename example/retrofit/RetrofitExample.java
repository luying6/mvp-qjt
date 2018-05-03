package com.luying.mvp.example.retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 创建人:luying
 * 创建时间:2018/5/3.
 * 网络请求：开始--》配置请求参数--》创建网络请求对象--》发送网络请求--》服务器返回数据--》解析数据--》处理返回的数据--》结束
 * Retrofit网络请求：1.配置请求参数                  [接口-注解(Build Request)]
 *                  2.创建网络请求对象              [网络请求执行器 (Call)]
 *                  3.适配到具体的Call             [网络请求适配器(CallAdapter)]
 *                  4.发送网络请求                 [网络请求执行器(Call)]
 *                  5.解析数据                    [数据转换器(Converter)]
 *                  6.切换线程(子线程-》主线程)     [回调执行器(Executor)]
 *
 *                  1和2使用了(建造者模式，工厂方法模式， 外观模式，代理模式，单例模式，策略模式，装饰模式，代理模式)
 *                  3使用了适配器模式
 *                  4使用了代理模式
 *                  6使用了适配器模式和装饰模式
 */

public class RetrofitExample {
    public void RetrofitUse(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://fanyi.youdao.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

    }
}
