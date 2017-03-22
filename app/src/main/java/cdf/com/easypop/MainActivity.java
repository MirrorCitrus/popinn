/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package cdf.com.easypop;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Toast;

import cdf.com.easypop.api.PopServiceWrapper;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static ViewGroup container;
    private CheckBox mCheckBox;
//    private EditText mEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCheckBox = (CheckBox) findViewById(R.id.night_mode_checkbox);
        container = (ViewGroup) findViewById(R.id.test_case_container);
        for (int i = 0; i < container.getChildCount(); i++) {
            container.getChildAt(i).setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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
            default:
                break;
        }
    }

    public static View getTestView() {
        return container;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PopServiceWrapper.closePopupWindow();
    }
}
