#### Retrofit 使用建造者模式通过Builder类建立了一个Retrofit实例，具体创建细节是配置了：
* 平台类型对象（Platform - Android
* 网络请求的url地址（baseUrl)
* 网络请求工厂（callFactory)--》默认使用OkHttpCall
* 网络请求适配器工厂的集合（adapterFactories）--》本质是配置了网络请求适配器工厂- 默认是ExecutorCallAdapterFactory 
* 数据转换器工厂的集合（converterFactories）--》本质是配置了数据转换器工厂
* 回调方法执行器(callbackExecutor)-->默认回调方法执行器作用是:切换线程(子线程-主线程)，异步


### 创建网络请求接口的实例
#### 使用步骤
``` java
public interface Api {
    // 注解GET：采用Get方法发送网络请求
    // Retrofit把网络请求的URL分成了2部分：1部分baseurl放在创建Retrofit对象时设置；另一部分在网络请求接口设置（即这里）
    // 如果接口里的URL是一个完整的网址，那么放在创建Retrofit对象时设置的部分可以不设置
    @GET("openapi.do?keyfrom=Yanzhikai&key=2032414398&type=data&doctype=json&version=1.1&q=car")

    // 接受网络请求数据的方法
    Call<JavaBean> getCall();
    // 返回类型为Call<*>，*是解析得到的数据类型，即JavaBean
}

<-- 步骤3：在MainActivity创建接口类实例  -->
AccessApi NetService = retrofit.create(AccessApi.class);

<-- 步骤4：对发送请求的url进行封装，即生成最终的网络请求对象  --> 
        Call<JavaBean> call = NetService.getCall();
        
```
#### 源码分析
* Retrofit是通过[外观模式]()&[代理模式]() 使用create()方法创建网络请求接口的实例（同时，通过网络请求接口里设置的注解进行了网络请求参数的配置)
首先看步骤3：``` AccessApi NetService = retrofit.create(AccessApi.class); ```
