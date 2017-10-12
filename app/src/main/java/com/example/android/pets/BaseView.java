package com.example.android.pets;

/**
 * BaseView 接口
 */

public interface BaseView<T> {
    //绑定 Presenter
    void setPresenter(T presenter);
}
