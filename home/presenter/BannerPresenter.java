package com.luying.mvp.home.presenter;

import android.util.Log;

import com.luying.mvp.base.BasePresenter;
import com.luying.mvp.entity.Banner;
import com.luying.mvp.home.model.BannerModel;
import com.luying.mvp.home.model.BannerModelImpl;
import com.luying.mvp.home.view.BannerView;

import io.reactivex.observers.DisposableObserver;

/**
 * Created by luying on 2018/4/27.
 */

public class BannerPresenter extends BasePresenter<BannerView>{
    private BannerModel bannerModel;

    public BannerPresenter() {
        bannerModel = new BannerModelImpl();
    }


    public void loadBanner(){
        mView.showLoading();
        bannerModel.loadBanner(new DisposableObserver<Integer>() {
            @Override
            public void onNext(Integer o) {
                Banner banner = new Banner();
                banner.setNumber(o);
                mView.getBannerShow(banner);
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
