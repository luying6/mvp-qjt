package com.luying.mvp.home.presenter;
import com.luying.mvp.base.BasePresenter;
import com.luying.mvp.base.BaseView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luying on 2018/4/27.
 */

public class MergePresenter<V extends BaseView> extends BasePresenter<V> {
    private List<BasePresenter> presenters = new ArrayList<>();
    private V view;

    public MergePresenter(V view) {
        this.view = view;
    }

    public final <P extends BasePresenter<V>> void requestPresenter(P... clz) {
        for (P cl : clz) {
            cl.attachView(view);
            presenters.add(cl);
        }
    }

    @Override
    public void detachView() {
        super.detachView();
        for (BasePresenter presenter: presenters){
            presenter.detachView();
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        for (BasePresenter presenter : presenters) {
            presenter.destroy();
        }
    }
}
