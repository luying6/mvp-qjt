package com.luying.mvp.home.model;

import io.reactivex.Observer;
import io.reactivex.observers.DisposableObserver;

/**
 * Created by luying on 2018/4/27.
 */

public interface HomeModel {
    void loadHome(Observer<Integer> observer);
}
