package com.grgbanking.pdamodel;

import android.content.Context;

public interface IModelFactory {
    public IModel createModel(Context context)throws IllegalAccessException;
}
