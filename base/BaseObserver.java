package com.luying.mvp.base;

import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by luying on 2018/4/28.
 */

public abstract class BaseObserver<T> implements Observer<T> {
    private CompositeDisposable compositeDisposable;
    private Disposable disposable;

    public BaseObserver(CompositeDisposable compositeDisposable){
        this.compositeDisposable = compositeDisposable;
    }


    @Override
    public void onSubscribe(Disposable d) {
        this.disposable = d;
        this.compositeDisposable.add(d);
    }

    @Override
    public void onError(Throwable e) {
        unBind();
        onFail();
    }

    @Override
    public void onComplete() {
        unBind();
        onCompleted();
    }

    protected abstract void onCompleted();

    protected abstract void onFail();

    private void unBind(){
        this.compositeDisposable.remove(disposable);
    }
}
