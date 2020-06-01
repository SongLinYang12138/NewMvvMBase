package com.bondex.photo.utils;

/**
 * date: 2020/5/28
 *
 * @Author: ysl
 * description:
 */
public interface RecyclerListener<T> {

    void onItem(int postion, T item);
}
