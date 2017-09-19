/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package cdf.com.easypop;

import com.citrus.popinn.PopGlobalCallback;
import com.citrus.popinn.PopHandle;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import cdf.com.easypop.api.PopServiceWrapper;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, PopGlobalCallback {

    private ViewGroup sContainer;
    private CheckBox mCheckBox;
    private Spinner mSpinner;

    int[] mGravities = {
            Gravity.CENTER,
            Gravity.CENTER_HORIZONTAL,
            Gravity.CENTER_VERTICAL,
            Gravity.START | Gravity.TOP,
            Gravity.END | Gravity.TOP,
            Gravity.START | Gravity.BOTTOM,
            Gravity.END | Gravity.BOTTOM,
            Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM,
            Gravity.CENTER_VERTICAL | Gravity.START,
    };
    String[] items = {
            "Gravity.CENTER",
            "Gravity.CENTER_HORIZONTAL",
            "Gravity.CENTER_VERTICAL",
            "Gravity.LEFT | Gravity.TOP",
            "Gravity.RIGHT | Gravity.TOP",
            "Gravity.LEFT | Gravity.BOTTOM",
            "Gravity.RIGHT | Gravity.BOTTOM",
            "Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM",
            "Gravity.CENTER_VERTICAL | Gravity.LEFT"
    };
    private PopHandle mPopHandle;
    private CheckBox mGlobalPopShowCheckbox;
    private TextView mGlobalPopTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mCheckBox = (CheckBox) findViewById(R.id.night_mode_checkbox);
        mSpinner = (Spinner) findViewById(R.id.spinner);
        initSpinner();
        mGlobalPopShowCheckbox = (CheckBox) findViewById(R.id.global_pop_show_cb);
        mGlobalPopTag = (TextView) findViewById(R.id.global_pop_tag);

        sContainer = (ViewGroup) findViewById(R.id.test_case_container);
        for (int i = 0; i < sContainer.getChildCount(); i++) {
            sContainer.getChildAt(i).setOnClickListener(this);
        }
        Button btn = (Button) findViewById(R.id.global_pop_close);
        btn.setOnClickListener(this);

        PopServiceWrapper.atatch(this, sContainer);
        PopServiceWrapper.addGlobalPopCallback(this);
    }

    private void initSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,
                items);
        mSpinner.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {

        int gravity;
        switch (v.getId()) {
            case R.id.global_pop_close:
                PopServiceWrapper.closePopupWindow();
                break;
            case R.id.main_button_1:
                PopServiceWrapper.showGeneralPop();
                break;
            case R.id.main_button_2:
                PopServiceWrapper.showGeneralPop2();
                break;
            case R.id.main_button_3:
                PopServiceWrapper.showAnimatedPop();
                break;
            case R.id.main_button_4:
                PopServiceWrapper.showNightModePop(mCheckBox.isChecked());
                break;
            case R.id.main_button_5:
                float scale = 0.5f;
                try {
                    PopServiceWrapper.showScaledPop(scale);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "parse float error", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.main_button_6:
                PopServiceWrapper.showNightModePop2(mCheckBox.isChecked());
                break;
            case R.id.main_button_7:
                PopServiceWrapper.showPopWithDelegate();
                break;
            case R.id.main_button_8:
                PopServiceWrapper.showPopWithDataBinding();
                break;
            case R.id.main_button_9:
                gravity = mGravities[mSpinner.getSelectedItemPosition()];
                PopServiceWrapper.showPopWithGravity(gravity);
                break;
            case R.id.main_button_10:
                mPopHandle = PopServiceWrapper.showPopWithoutAutoDismiss();
                break;
            case R.id.main_button_11:
                if (mPopHandle != null) {
                    Toast.makeText(this, "previous pop is showing:" + mPopHandle.isShowing(), Toast.LENGTH_SHORT).show();
                    mPopHandle.dismiss();
                }
                break;
            case R.id.main_button_12:
                PopServiceWrapper.showClickBinding(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (v instanceof Button) {
                            Toast.makeText(MainActivity.this, "Button " + ((Button) v).getText() + " is clicked!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
            case R.id.main_button_13:
                PopServiceWrapper.showCheckboxDataBinding();
                break;
            default:
                break;
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        PopServiceWrapper.removeGlobalCallback(this);
        PopServiceWrapper.closePopupWindow(); // 任意地方可以关闭当前popupWindow
        PopServiceWrapper.release(); // release目前封装了closeWindow+detatch功能
        
    }

    @Override
    public void onPopShow(Object tag) {
        updateGlobalPopState();
    }

    @Override
    public void onPopClosed(Object tag) {
        updateGlobalPopState();
    }

    /**
     * 更新全局浮层状态。用来测试全局方法是否运行正常。
     */
    private void updateGlobalPopState() {
        mGlobalPopShowCheckbox.setChecked(PopServiceWrapper.isShowing());
        if (PopServiceWrapper.isShowing()) {
            Object tag = PopServiceWrapper.getCurrentPopTag();
            if (tag != null) {
                mGlobalPopTag.setText(tag.toString());
            } else {
                mGlobalPopTag.setText("tag::null");
            }
        } else {
            mGlobalPopTag.setText("");
        }
    }
}
