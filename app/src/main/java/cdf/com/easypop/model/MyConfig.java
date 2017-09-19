/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package cdf.com.easypop.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.Observable;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.view.View;

/**
 * Created by baidu on 17/8/8.
 */

public class MyConfig extends BaseObservable {
    
    public ObservableBoolean isChecked1 = new ObservableBoolean(false);
    public ObservableBoolean isChecked2 = new ObservableBoolean(false);
    public ObservableField<String> text = new ObservableField<>();
    
    public MyConfig(boolean isChecked1, boolean isChecked2, String text) {
        this.isChecked1.set(isChecked1);
        this.isChecked2.set(isChecked2);
        this.text.set(text);
    }

    @Override
    public String toString() {
        return "MyConfig{" +
                "isChecked1=" + isChecked1.get() +
                ", isChecked2=" + isChecked2.get() +
                ", text='" + text.get() + '\'' +
                '}';
    }
    
    public void toggle(View view) {
        isChecked1.set(!isChecked1.get());
        isChecked2.set(!isChecked2.get());
    }
}
