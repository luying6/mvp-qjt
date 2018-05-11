package com.luying.mvp;

import android.util.Log;

import com.luying.mvp.base.BaseMvpActivity;
import com.luying.mvp.entity.Banner;
import com.luying.mvp.entity.Home;
import com.luying.mvp.home.presenter.BannerPresenter;
import com.luying.mvp.home.presenter.HomePresenter;
import com.luying.mvp.home.presenter.MergePresenter;
import com.luying.mvp.home.view.BannerView;
import com.luying.mvp.home.view.HomeView;

/**
 * Created by luying on 2018/4/27.
 */

public class HomeActivity extends BaseMvpActivity<MergePresenter> implements BannerView, HomeView{
    private BannerPresenter bannerPresenter;
    private HomePresenter homePresenter;

    @Override
    protected void initData() {
        bannerPresenter.loadBanner();
        homePresenter.loadHome();
    }

    @Override
    protected MergePresenter createPresenter() {
        MergePresenter presenter = new MergePresenter<>(this);
        bannerPresenter = new BannerPresenter();
        homePresenter = new HomePresenter();
        presenter.requestPresenter(bannerPresenter, homePresenter);
        return presenter;
    }



    @Override
    public int setLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void getBannerShow(Banner banner) {
    }

    @Override
    public void getHomeShow(Home home) {

    }

    @Override
    public void isRefresh(Boolean refresh) {

    }
}
