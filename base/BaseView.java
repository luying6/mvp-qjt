package com.luying.mvp.base;

/**
 * Created by luying on 2018/4/27.
 */

public interface BaseView {
    void showLoading();
    void hideLoading();
    void showError(Throwable throwable);
}
