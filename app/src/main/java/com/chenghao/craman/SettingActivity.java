package com.chenghao.craman;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.io.File;
import java.math.BigDecimal;

/**
 * Created by Hao on 16/6/11.
 */
public class SettingActivity extends BaseActivity {
    private SharedPreferences mySharedPreferences;
    private RadioGroup rg_pronunciation;
    private RadioButton rbtn_english, rbtn_american;
    private TextView tv_cache;
    private LinearLayout ll_cache;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("设置");
    }

    @Override
    public void initWidgets() {
        super.initWidgets();
        rg_pronunciation = (RadioGroup) content.findViewById(R.id.rg_pronunciation);
        rbtn_english = (RadioButton) content.findViewById(R.id.rbtn_english);
        rbtn_american = (RadioButton) content.findViewById(R.id.rbtn_american);
        mySharedPreferences = getSharedPreferences("config", Activity.MODE_PRIVATE);
        String pronunciation = mySharedPreferences.getString("pronunciation", "american");
        if (pronunciation.equals("american")) {
            ((RadioButton) rg_pronunciation.getChildAt(1)).setChecked(true);
        } else
            ((RadioButton) rg_pronunciation.getChildAt(0)).setChecked(true);
        tv_cache = (TextView) content.findViewById(R.id.tv_cache);
        statistics();
        ll_cache = (LinearLayout) content.findViewById(R.id.ll_cache);
    }

    @Override
    public void addListeners() {
        rg_pronunciation.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == rbtn_english.getId()) {
                    mySharedPreferences.edit().putString("pronunciation", "english").commit();
                } else {
                    mySharedPreferences.edit().putString("pronunciation", "american").commit();
                }
            }
        });

        ll_cache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!tv_cache.equals("0个文件  0B")) {
                    deleteFiles();
                    statistics();
                }
                Snackbar.make(v, "缓存已清空", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void statistics() {
        File file = new File("data/data/com.chenghao.craman/voices");
        long size = 0;
        File[] fileList = file.listFiles();
        for (int i = 0; i < fileList.length; i++) {
            if (fileList[i].isFile())
                size += fileList[i].length();
        }
        tv_cache.setText(fileList.length + "个文件  " + getFormattedSize(size));
    }

    private void deleteFiles() {
        File file = new File("data/data/com.chenghao.craman/voices");
        File[] fileList = file.listFiles();
        for (int i = 0; i < fileList.length; i++) {
            if (fileList[i].isFile())
                fileList[i].delete();
        }
    }

    private static String getFormattedSize(long size) {
        double kiloByte = size / 1024.0;
        if (kiloByte < 1)
            return size + "B";

        double megaByte = kiloByte / 1024.0;
        if (megaByte < 1) {
            BigDecimal result = new BigDecimal(Double.toString(kiloByte));
            return result.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB";
        }

        double gigaByte = megaByte / 1024.0;
        if (gigaByte < 1) {
            BigDecimal result = new BigDecimal(Double.toString(megaByte));
            return result.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB";
        }

        BigDecimal result = new BigDecimal(Double.toString(gigaByte));
        return result.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB";
    }
}
