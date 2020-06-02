package com.bondex.library.base;

/**
 * date: 2020/6/2
 *
 * @Author: ysl
 * description:
 */
public class BaseModel<T extends BaseBack> {

    protected T resultBack;

    public BaseModel(T resultBack) {
        this.resultBack = resultBack;
    }


}
