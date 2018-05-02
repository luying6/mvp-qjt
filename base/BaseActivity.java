package com.luying.mvp.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by luying on 2018/4/27.
 */

public abstract class BaseActivity extends AppCompatActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(setLayout());

        init();
        initView();
        initData();
    }

    protected  void initData(){

    };

    protected  void initView(){

    };

    protected  void init(){

    };

    public abstract int setLayout();

}
