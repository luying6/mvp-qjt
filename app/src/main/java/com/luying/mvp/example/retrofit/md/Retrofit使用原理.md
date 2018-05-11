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

####关注点1：```ServiceMethod serviceMethod = loadServiceMethod(method)```
    
``` java

<-- loadServiceMethod(method)方法讲解 -->
// 一个 ServiceMethod 对象对应于网络请求接口里的一个方法
// loadServiceMethod（method）负责加载 ServiceMethod：

  ServiceMethod loadServiceMethod(Method method) {
 ServiceMethod<?, ?> result = serviceMethodCache.get(method);
    if (result != null) return result;

      // 设置线程同步锁
    synchronized (serviceMethodCache) {
    
         // ServiceMethod类对象采用了单例模式进行创建
         // 即创建ServiceMethod对象前，先看serviceMethodCache有没有缓存之前创建过的网络请求实例
      result = serviceMethodCache.get(method);
     
      // 若没缓存，则通过建造者模式创建 serviceMethod 对象
      if (result == null) {
      // 下面会详细介绍ServiceMethod生成实例的过程
        result = new ServiceMethod.Builder(this, method).build();
        serviceMethodCache.put(method, result);
      }
    }
    return result;
  }
  
  // 这里就是上面说的创建实例的缓存机制：采用单例模式从而实现一个 ServiceMethod 对象对应于网络请求接口里的一个方法
  // 注：由于每次获取接口实例都是传入 class 对象
  // 而 class 对象在进程内单例的，所以获取到它的同一个方法 Method 实例也是单例的，所以这里的缓存是有效的。
```

serviceMethod实例创建过程
```
result = new ServiceMethod //步骤1
                .Builder(this, method)  //步骤2
                .build();   //步骤3
```

####步骤1:ServiceMethod类 构造函数
``` java
<-- ServiceMethod 类 -->
public final class ServiceMethod {
final okhttp3.Call.Factory callFactory;   // 网络请求工厂  
final CallAdapter<?> callAdapter;  
// 网络请求适配器工厂
// 具体创建是在new ServiceMethod.Builder(this, method).build()最后的build()中
// 下面会详细说明

private final Converter<ResponseBody, T> responseConverter; 
// Response内容转换器  
// 作用：负责把服务器返回的数据（JSON或者其他格式，由 ResponseBody 封装）转化为 T 类型的对象；

private final HttpUrl baseUrl; // 网络请求地址  
private final String relativeUrl; // 网络请求的相对地址  
private final String httpMethod;   // 网络请求的Http方法  
private final Headers headers;  // 网络请求的http请求头 键值对  
private final MediaType contentType; // 网络请求的http报文body的类型  

private final ParameterHandler<?>[] parameterHandlers;  
  // 方法参数处理器
  // 作用：负责解析 API 定义时每个方法的参数，并在构造 HTTP 请求时设置参数；
  // 下面会详细说明

// 说明：从上面的成员变量可以看出，ServiceMethod对象包含了访问网络的所有基本信息

<-- ServiceMethod 类的构造函数 -->
// 作用：传入各种网络请求参数
ServiceMethod(Builder<T> builder) {

    this.callFactory = builder.retrofit.callFactory();  
    this.callAdapter = builder.callAdapter;   
    this.responseConverter = builder.responseConverter;   

    this.baseUrl = builder.retrofit.baseUrl();   
    this.relativeUrl = builder.relativeUrl;   
    this.httpMethod = builder.httpMethod;  
    this.headers = builder.headers;  
    this.contentType = builder.contentType; .  
    this.hasBody = builder.hasBody; y  
    this.isFormEncoded = builder.isFormEncoded;   
    this.isMultipart = builder.isMultipart;  
    this.parameterHandlers = builder.parameterHandlers;  
}
```
#### 步骤2：ServiceMethod的Builder（）
``` java
 public Builder(Retrofit retrofit, Method method) {
      this.retrofit = retrofit;
      this.method = method;

      // 获取网络请求接口方法里的注释
      this.methodAnnotations = method.getAnnotations();
      // 获取网络请求接口方法里的参数类型       
      this.parameterTypes = method.getGenericParameterTypes();  
      //获取网络请求接口方法里的注解内容    
      this.parameterAnnotationsArray = method.getParameterAnnotations();    
    }
```
#### 步骤3：ServiceMethod的Builder（）
``` java
// 作用：控制ServiceMethod对象的生成流程

 public ServiceMethod build() {

      callAdapter = createCallAdapter();    
      // 根据网络请求接口方法的返回值和注解类型，从Retrofit对象中获取对应的网络请求适配器  -->关注点1

      responseType = callAdapter.responseType();    
     // 根据网络请求接口方法的返回值和注解类型，从Retrofit对象中获取该网络适配器返回的数据类型

      responseConverter = createResponseConverter();    
      // 根据网络请求接口方法的返回值和注解类型，从Retrofit对象中获取对应的数据转换器  -->关注点3
      // 构造 HTTP 请求时，我们传递的参数都是String
      // Retrofit 类提供 converter把传递的参数都转化为 String 
      // 其余类型的参数都利用 Converter.Factory 的stringConverter 进行转换
      // @Body 和 @Part 类型的参数利用Converter.Factory 提供的 requestBodyConverter 进行转换
      // 这三种 converter 都是通过“询问”工厂列表进行提供，而工厂列表我们可以在构造 Retrofit 对象时进行添加。


       for (Annotation annotation : methodAnnotations) {
        parseMethodAnnotation(annotation);
      }
      // 解析网络请求接口中方法的注解
      // 主要是解析获取Http请求的方法
     // 注解包括：DELETE、GET、POST、HEAD、PATCH、PUT、OPTIONS、HTTP、retrofit2.http.Headers、Multipart、FormUrlEncoded
     // 处理主要是调用方法 parseHttpMethodAndPath(String httpMethod, String value, boolean hasBody) ServiceMethod中的httpMethod、hasBody、relativeUrl、relativeUrlParamNames域进行赋值

     int parameterCount = parameterAnnotationsArray.length;
     // 获取当前方法的参数数量

      parameterHandlers = new ParameterHandler<?>[parameterCount];
      for (int p = 0; p < parameterCount; p++) {
        Type parameterType = parameterTypes[p];
        Annotation[] parameterAnnotations = parameterAnnotationsArray[p];
        // 为方法中的每个参数创建一个ParameterHandler<?>对象并解析每个参数使用的注解类型
        // 该对象的创建过程就是对方法参数中注解进行解析
        // 这里的注解包括：Body、PartMap、Part、FieldMap、Field、Header、QueryMap、Query、Path、Url 
        parameterHandlers[p] = parseParameter(p, parameterType, parameterAnnotations);
      } 
      return new ServiceMethod<>(this);

<-- 总结 -->
// 1. 根据返回值类型和方法标注从Retrofit对象的的网络请求适配器工厂集合和内容转换器工厂集合中分别获取到该方法对应的网络请求适配器和Response内容转换器；
// 2. 根据方法的标注对ServiceMethod的域进行赋值
// 3. 最后为每个方法的参数的标注进行解析，获得一个ParameterHandler<?>对象
// 该对象保存有一个Request内容转换器——根据参数的类型从Retrofit的内容转换器工厂集合中获取一个Request内容转换器或者一个String内容转换器。
    }


<-- 关注点1：createCallAdapter() -->
 private CallAdapter<?> createCallAdapter() {

      // 获取网络请求接口里方法的返回值类型
      Type returnType = method.getGenericReturnType();      

      // 获取网络请求接口接口里的注解
      // 此处使用的是@Get
      Annotation[] annotations = method.getAnnotations();       
      try {

      return retrofit.callAdapter(returnType, annotations); 
      // 根据网络请求接口方法的返回值和注解类型，从Retrofit对象中获取对应的网络请求适配器
      // 下面会详细说明retrofit.callAdapter（） -- >关注点2
      }
...


<-- 关注点2：retrofit.callAdapter()  -->
 public CallAdapter<?> callAdapter(Type returnType, Annotation[] annotations) {
    return nextCallAdapter(null, returnType, annotations);
  }

 public CallAdapter<?> nextCallAdapter(CallAdapter.Factory skipPast, Type returnType,
      Annotation[] annotations) {

    // 创建 CallAdapter 如下
    // 遍历 CallAdapter.Factory 集合寻找合适的工厂（该工厂集合在第一步构造 Retrofit 对象时进行添加（第一步时已经说明））
    // 如果最终没有工厂提供需要的 CallAdapter，将抛出异常
    for (int i = start, count = adapterFactories.size(); i < count; i++) {
      CallAdapter<?> adapter = adapterFactories.get(i).get(returnType, annotations, this);      
      if (adapter != null) {
        return adapter;
      }
    }


<--   关注点3：createResponseConverter（） -->

 private Converter<ResponseBody, T> createResponseConverter() {
      Annotation[] annotations = method.getAnnotations();
      try {

        // responseConverter 还是由 Retrofit 类提供  -->关注点4
        return retrofit.responseBodyConverter(responseType, annotations);
      } catch (RuntimeException e) { 
        throw methodError(e, "Unable to create converter for %s", responseType);
      }
    }

<--   关注点4：responseBodyConverter（） -->
  public <T> Converter<ResponseBody, T> responseBodyConverter(Type type, Annotation[] annotations) {
    return nextResponseBodyConverter(null, type, annotations);
  }

 public <T> Converter<ResponseBody, T> nextResponseBodyConverter(Converter.Factory skipPast,

    int start = converterFactories.indexOf(skipPast) + 1;
    for (int i = start, count = converterFactories.size(); i < count; i++) {

       // 获取Converter 过程：（和获取 callAdapter 基本一致）
         Converter<ResponseBody, ?> converter =
          converterFactories.get(i).responseBodyConverter(type, annotations, this); 
       // 遍历 Converter.Factory 集合并寻找合适的工厂（该工厂集合在构造 Retrofit 对象时进行添加（第一步时已经说明））
       // 由于构造Retroifit采用的是Gson解析方式，所以取出的是GsonResponseBodyConverter
       // Retrofit - Converters 还提供了 JSON，XML，ProtoBuf 等类型数据的转换功能。
       // 继续看responseBodyConverter（） -->关注点5    
    }


<--   关注点5：responseBodyConverter（） -->
@Override
public Converter<ResponseBody, ?> responseBodyConverter(Type type, 
    Annotation[] annotations, Retrofit retrofit) {


  TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
  // 根据目标类型，利用 Gson#getAdapter 获取相应的 adapter
  return new GsonResponseBodyConverter<>(gson, adapter);
}

// 做数据转换时调用 Gson 的 API 即可。
final class GsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
  private final Gson gson;
  private final TypeAdapter<T> adapter;

  GsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
    this.gson = gson;
    this.adapter = adapter;
  }

  @Override 
   public T convert(ResponseBody value) throws IOException {
    JsonReader jsonReader = gson.newJsonReader(value.charStream());
    try {
      return adapter.read(jsonReader);
    } finally {
      value.close();
    }
  }
}
```
* 当选择了RxJavaCallAdapterFactory后，RxJava通过[策略模式]()选择对应的adapter
* 具体过程是：根据网络接口方法的返回值类型来选择具体要用哪种CallAdapterFactory，然后创建具体的CallAdapter实例
* 上面提到了两种工厂：CallAdapter.Factory & Converter.Factory分别负责提供不同的功能模块
  工厂负责如何提供、提供何种功能模块
* Retrofit 只负责提供选择何种工厂的决策信息（如网络接口方法的参数、返回值类型、注解等）
* 最终配置好网络请求参数(ServiceMethod对象的创建)
这正是所谓的高内聚低耦合，[工厂模式]()
  
#### 第二行：```OkHttpCall okHttpCall = new OkHttpCall<>(serviceMethod, args);```
根据第一步配置好的ServiceMethod对象和输入的请求参数创建okHttpCall对象
```
<--OkHttpCall类 -->
public class OkHttpCall {
    private final ServiceMethod<T> serviceMethod; // 含有所有网络请求参数信息的对象  
    private final Object[] args; // 网络请求接口的参数 
    private okhttp3.Call rawCall; //实际进行网络访问的类  
    private Throwable creationFailure; //几个状态标志位  
    private boolean executed;  
    private volatile boolean canceled;  

<--OkHttpCall构造函数 -->
  public OkHttpCall(ServiceMethod<T> serviceMethod, Object[] args) {  
    // 传入了配置好的ServiceMethod对象和输入的请求参数
    this.serviceMethod = serviceMethod;  
    this.args = args;  
} 
```

#### 第三行:``` return serviceMethod.callAdapter.adapt(okHttpCall); ```
将第二步创建的OkHttpCall对象传给第一步创建的serviceMethod对象中对应的网络请求适配器工厂的adapt（）
> 返回对象类型：Android默认的是Call<>；若设置了RxJavaCallAdapterFactory，返回的则是Observable<>

```
public <R> Call<R> adapt(Call<R> call) {
        return new ExecutorCallbackCall<>(callbackExecutor, call);  
      }

   ExecutorCallbackCall(Executor callbackExecutor, Call<T> delegate) {
      this.delegate = delegate; 
      // 把上面创建并配置好参数的OkhttpCall对象交给静态代理delegate
      // 静态代理和动态代理都属于代理模式
     // 静态代理作用：代理执行被代理者的方法，且可在要执行的方法前后加入自己的动作，进行对系统功能的拓展

      this.callbackExecutor = callbackExecutor;
      // 传入上面定义的回调方法执行器
      // 用于进行线程切换   
    }
```
* 采用了装饰模式：ExecutorCallbackCall = 装饰者，而里面真正去执行网络请求的还是OkHttpCall
* 使用装饰模式的原因：希望在OkHttpCall发送请求时做一些额外操作。这里的额外操作是线程转换，即将子线程切换到主线程 
> 1.OkHttpCall的enqueue()是进行网络异步请求的：当你调用OkHttpCall.enqueue（）时，回调的callback是在子线程中，需要通过Handler转换到主线程进行回调。ExecutorCallbackCall就是用于线程回调；
> 2.当然以上是原生Retrofit使用的切换线程方式。如果你用Rxjava，那就不会用到这个ExecutorCallbackCall而是RxJava的Call，此处不过多展开

####步骤4: ```Call<JavaBean> call = NetService.getCall(); ```
* NetService对象实际上是动态代理对象Proxy.newProxyInstance（）（步骤3中已说明），并不是真正的网络请求接口创建的对象
* 当NetService对象调用getCall（）时会被动态代理对象Proxy.newProxyInstance（）拦截，然后调用自身的InvocationHandler # invoke（）
* invoke(Object proxy, Method method, Object... args)会传入3个参数：Object proxy:（代理对象）、 
  Method method（调用的getCall()） 
  Object... args（方法的参数，即getCall（*）中的*）
* 接下来利用Java反射获取到getCall（）的注解信息，配合args参数创建ServiceMethod对象。 
 
##### 最终创建并返回一个OkHttpCall类型的Call对象 
1. OkHttpCall类是OkHttp的包装类 
2. 创建了OkHttpCall类型的Call对象还不能发送网络请求，需要创建Request对象才能发送网络请求

###总结
#####Retrofit采用了 外观模式 统一调用创建网络请求接口实例和网络请求参数配置的方法，具体细节是:
- 动态创建网络请求接口的实例（代理模式 - [动态代理]()）
- 创建 serviceMethod 对象（[建造者模式]() & [单例模式]()（缓存机制））
- 对 serviceMethod 对象进行网络请求参数配置：通过解析网络请求接口方法的参数、返回值和注解类型，从Retrofit对象中获取对应的网络请求的url地址、网络请求执行器、网络请求适配器 & 数据转换器。（[策略模式]()）
- 对 serviceMethod 对象加入线程切换的操作，便于接收数据后通过Handler从子线程切换到主线程从而对返回数据结果进行处理（[装饰模式]()）
- 最终创建并返回一个OkHttpCall类型的网络请求对象

###开始网络请求
- Retrofit默认使用OkHttp，即OkHttpCall类（实现了 retrofit2.Call<T>接口） 
- OkHttpCall提供了两种网络请求方式： 
>1.同步请求 OkHttpCall.execute()
>2.异步请求 OkHttpCall.enqueue()

####同步请求OkHttpCall.execute()
#####发送请求过程
- 步骤1:对网络请求接口的方法中的每个参数利用对应ParameterHandler进行解析，再根据ServiceMethod对象创建一个OkHttp的Request对象
- 步骤2:使用OkHttp的Request发送网络请求;
- 步骤3:对返回的数据使用之前设置的数据转换器（GsonConverterFactory）解析返回的数据，最终得到一个Response<T>对象

#####具体使用
>Response<JavaBean> response = call.execute();  




