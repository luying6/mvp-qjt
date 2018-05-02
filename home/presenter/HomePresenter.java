package com.luying.mvp.home.presenter;

import android.util.Log;

import com.luying.mvp.base.BasePresenter;
import com.luying.mvp.entity.Home;
import com.luying.mvp.home.model.BannerModel;
import com.luying.mvp.home.model.BannerModelImpl;
import com.luying.mvp.home.model.HomeModel;
import com.luying.mvp.home.model.HomeModelImpl;
import com.luying.mvp.home.view.HomeView;

import io.reactivex.observers.DisposableObserver;

/**
 * Created by luying on 2018/4/27.
 */

public class HomePresenter extends BasePresenter<HomeView>{
    private HomeModel model;

    public HomePresenter() {
        this.model = new HomeModelImpl();
    }

    public void loadHome(){
        model.loadHome(new DisposableObserver<Integer>() {
            @Override
            public void onNext(Integer integer) {
                Home home = new Home();
                home.setNum(integer);
                mView.getHomeShow(home);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

}
