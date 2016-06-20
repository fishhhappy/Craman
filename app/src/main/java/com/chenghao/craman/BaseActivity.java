package com.chenghao.craman;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;

import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.TypiconsModule;

public class BaseActivity extends AppCompatActivity {

    private Toolbar toolbar;
    protected View content;
    protected Bundle bundle;
    protected CoordinatorLayout cl_root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Iconify.with(new TypiconsModule());

        setContentView(R.layout.activity_base);

        bundle = this.getIntent().getExtras();
        int content_layout = bundle.getInt("content");
        if (content_layout != 0)
            setContent(content_layout);

        initWidgets();
        addListeners();
    }

    public void initWidgets() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        cl_root = (CoordinatorLayout) findViewById(R.id.cl_root);
        setTitle("");
    }

    public void addListeners() {

    }

    public void setTitle(String title) {
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void setContent(int id) {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.content_base);
        content = getLayoutInflater().inflate(id, null);
        linearLayout.addView(content);
    }

}
