package com.luying.mvp.base;

import io.reactivex.observers.DisposableObserver;

/**
 * 错误处理类
 * Created by luying on 2018/4/28.
 */

public abstract class BaseDisposableObserver<T> extends DisposableObserver<T>{
    @Override
    public void onNext(T t) {

    }

    @Override
    public void onError(Throwable e) {

    }

    @Override
    public void onComplete() {

    }

}
