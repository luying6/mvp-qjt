``` java
public <T> T create(final Class<T> service) {

    //检查class，如果class有继承或者不是一个接口则报异常
    Utils.validateServiceInterface(service);
    
    //判断是否需要提前验证
    if (validateEagerly) {
     // 1. 给接口中每个方法的注解进行解析并得到一个ServiceMethod对象
     // 2. 以Method为键将该对象存入LinkedHashMap集合中
     // 特别注意：如果不是提前验证则进行动态解析对应方法，得到一个ServiceMethod对象，最后存入到LinkedHashMap集合中，类似延迟加载（默认）
      eagerlyValidateMethods(service);
    }
    return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class<?>[] { service },
        new InvocationHandler() {
          private final Platform platform = Platform.get();

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


  private void eagerlyValidateMethods(Class<?> service) {
    Platform platform = Platform.get();
    for (Method method : service.getDeclaredMethods()) {
      if (!platform.isDefaultMethod(method)) {
      
      // 将传入的ServiceMethod对象加入LinkedHashMap<Method, ServiceMethod>集合
      // 使用LinkedHashMap集合的好处：lruEntries.values().iterator().next()获取到的是集合最不经常用到的元素，提供了一种Lru算法的实现
        loadServiceMethod(method);
      }
    }
  }
  
    ServiceMethod<?, ?> loadServiceMethod(Method method) {
      ServiceMethod<?, ?> result = serviceMethodCache.get(method);
      if (result != null) return result;
  
      synchronized (serviceMethodCache) {
        result = serviceMethodCache.get(method);
        if (result == null) {
          result = new ServiceMethod.Builder<>(this, method).build();
          serviceMethodCache.put(method, result);
        }
      }
      return result;
    }

```