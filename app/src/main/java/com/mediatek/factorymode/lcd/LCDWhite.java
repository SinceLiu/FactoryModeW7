
package com.mediatek.factorymode.lcd;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import com.mediatek.factorymode.BaseTestActivity;
import com.mediatek.factorymode.R;

public class LCDWhite extends BaseTestActivity {
    private TextView mText1 = null;

    SharedPreferences mSp;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSp = getSharedPreferences("FactoryMode", Context.MODE_PRIVATE);
        setContentView(R.layout.lcd);
        mText1 = (TextView) findViewById(R.id.test_color_text1);
        mText1.setBackgroundColor(Color.WHITE);

    }
}
