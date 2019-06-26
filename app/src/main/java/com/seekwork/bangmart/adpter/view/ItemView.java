package com.seekwork.bangmart.adpter.view;


import com.seekwork.bangmart.adpter.model.Item;

public interface ItemView {
    /**
     * 初始化view
     */
    void prepareItemView();

    /**
     * 初始化数据
     *
     * @param item
     */
    void setObject(Item item, int positon);
}
