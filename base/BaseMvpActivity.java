package com.luying.mvp.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

/**
 * Created by luying on 2018/4/27.
 */

public abstract class BaseMvpActivity <T extends BasePresenter> extends BaseActivity implements BaseView {
    protected T mPresenter;

    @Override
    protected void init() {
        super.init();
        mPresenter = createPresenter();
        if (mPresenter != null){
            mPresenter.attachView(this);
        }
    }

    protected abstract T createPresenter();


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null){
            mPresenter.detachView();
            mPresenter.destroy();
        }
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showError(Throwable throwable) {

    }
}
