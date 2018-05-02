package com.luying.mvp.util;

import io.reactivex.Flowable;
import io.reactivex.processors.FlowableProcessor;
import io.reactivex.processors.PublishProcessor;

/**
 * Created by luying on 2018/5/2.
 */

public class RxBus {
    private final FlowableProcessor<Object> bus = PublishProcessor.create().toSerialized();
    private static volatile RxBus mRxBus = null;//volatile保证RxBus的执行顺序，但是它是非原子性的，因此要和synchronized连用。

    private RxBus(){
    }


    public static synchronized RxBus getDefault(){
        if (mRxBus == null){
            synchronized (RxBus.class){
                if (mRxBus == null){
                    mRxBus = new RxBus();
                }
            }
        }
        return mRxBus;
    }


    public void post(Object o){
        bus.onNext(o);
    }

    public <T> Flowable<T> toFlowable(Class<T> clazz){
        return bus.ofType(clazz);
    }
}
