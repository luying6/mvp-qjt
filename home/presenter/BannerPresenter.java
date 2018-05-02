package com.luying.mvp.home.presenter;

import android.util.Log;

import com.luying.mvp.base.BaseObserver;
import com.luying.mvp.base.BasePresenter;
import com.luying.mvp.entity.Banner;
import com.luying.mvp.home.model.BannerModel;
import com.luying.mvp.home.model.BannerModelImpl;
import com.luying.mvp.home.view.BannerView;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

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

        bannerModel.loadBanner(new BaseObserver<Integer>(mCompositeDisposable) {
            @Override
            protected void onCompleted() {

            }

            @Override
            protected void onFail() {

            }

            @Override
            public void onNext(Integer integer) {
                Banner banner = new Banner();
                banner.setNumber(integer);
                mView.getBannerShow(banner);
            }
        });
    }

}
