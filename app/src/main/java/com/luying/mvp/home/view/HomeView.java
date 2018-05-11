package com.luying.mvp.home.view;

import com.luying.mvp.base.BaseView;
import com.luying.mvp.entity.Home;

/**
 * Created by luying on 2018/4/27.
 */

public interface HomeView extends BaseView{
    void getHomeShow(Home home);
    void isRefresh(Boolean refresh);
}
