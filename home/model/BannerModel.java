package com.luying.mvp.home.model;

import io.reactivex.Observer;

/**
 * Created by luying on 2018/4/27.
 */

public interface BannerModel {
    void loadBanner(Observer<Integer> disposableObserver);
}
