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
首先看步骤3：``` Api NetService = retrofit.create(AccessApi.class); ```

``` java
public <T> T create(final Class<T> service) {
    Utils.validateServiceInterface(service);
    if (validateEagerly) {
    // 判断是否需要提前验证
    //1.给接口中每个方法的注解进行解析并得到一个ServiceMethod对象
    //2.以Method为键将该对象存入LinkedHashMap集合中
    如果不是提前验证则进行动态解析对应方法，得到一个ServiceMethod对象，最后存入到LinkedHashMap集合中，类似延迟加载（默认）
      eagerlyValidateMethods(service);
    }
    
    
    
    //创建了网络请求接口的动态代理对象，即通过动态代理创建网络接口的实例(并最终返回)
    //该动态代理是为了拿到网络请求借口实例上所有注解
    return (T) Proxy.newProxyInstance(
        service.getClassLoader(),       //动态生成接口的实现类
        new Class<?>[] { service },     //动态创建实例
        new InvocationHandler() {       //将代理类的实现交给InvocationHandler类作为具体的实现
          private final Platform platform = Platform.get();

            //在InvocationHandler类的invoke()实现中，除了执行真正的逻辑（如再次转发给真正的实现类对象）,还可以进行一些有用的操作
            //如统计执行时间、进行初始化清理、对接口调用进行检查
          @Override public Object invoke(Object proxy, Method method, @Nullable Object[] args)
              throws Throwable {
            // If the method is a method from Object then defer to normal invocation.
            if (method.getDeclaringClass() == Object.class) {
              return method.invoke(this, args);
            }
            if (platform.isDefaultMethod(method)) {
              return platform.invokeDefaultMethod(method, service, proxy, args);
            }
            ServiceMethod<Object, Object> serviceMethod =
                (ServiceMethod<Object, Object>) loadServiceMethod(method);
            OkHttpCall<Object> okHttpCall = new OkHttpCall<>(serviceMethod, args);
            return serviceMethod.adapt(okHttpCall);
          }
        });
  }
```
使用动态代理的好处
* 当NetService调用getCall()接口中的方法进行拦截，调用都会集中转发到InvocationHandler的invoke()中，可集中进行处理
* 获得网络请求接口实例上的所有注解
* 更方便封装ServiceMethod

###源码分析
InvocationHandler类中invoke()方法的具体实现

``` java
new InvocationHandler() {   
          private final Platform platform = Platform.get();

  @Override 
           public Object invoke(Object proxy, Method method, Object... args)
              throws Throwable {

            // 关注点1
            // 作用：读取网络请求接口里的方法，并根据前面配置好的属性配置serviceMethod对象
            ServiceMethod serviceMethod = loadServiceMethod(method);     

            // 关注点2
            // 作用：根据配置好的serviceMethod对象创建okHttpCall对象 
            OkHttpCall okHttpCall = new OkHttpCall<>(serviceMethod, args);

            // 关注点3
            // 作用：调用OkHttp，并根据okHttpCall返回rejava的Observe对象或者返回Call
            return serviceMethod.callAdapter.adapt(okHttpCall);
          }

```

