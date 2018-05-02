package com.luying.mvp.base;

import android.util.Log;

import java.lang.ref.WeakReference;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by luying on 2018/4/27.
 */

public abstract class BasePresenter<V extends BaseView> implements PresenterLifecycle<V> {
    protected V mView;
    protected CompositeDisposable mCompositeDisposable;


    protected Disposable addSubscribe(Disposable disposable){
        if (mCompositeDisposable == null){
            mCompositeDisposable = new CompositeDisposable();
        }
        mCompositeDisposable.add(disposable);
        return disposable;
    }
    /**
     * 在attachView里面进行初始化WeakReference，是因为使用MergePresenter的时候，普通presenter拿不到view
     * 只能通过MergePresenter的attachView方法来传递view
     *
     */
    @Override
    public void attachView(V view) {
        mView = view;
    }

    @Override
    public void detachView() {
        if (mView != null) {
            mView = null;
        }
    }

    @Override
    public void destroy() {
        if (mCompositeDisposable != null){
            mCompositeDisposable.clear();
        }
    }
}
