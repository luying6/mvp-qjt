package com.luying.mvp.base;

/**
 * Created by luying on 2018/4/27.
 */

public interface PresenterLifecycle<V extends BaseView> {
    void destroy();
    void attachView(V view);
    void detachView();
}
