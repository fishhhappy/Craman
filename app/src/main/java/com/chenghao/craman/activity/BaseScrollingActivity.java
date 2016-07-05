package com.chenghao.craman.activity;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.chenghao.craman.R;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.SimpleLineIconsModule;

public class BaseScrollingActivity extends AppCompatActivity {
    protected Toolbar toolbar;
    protected CollapsingToolbarLayout toolbarLayout;
    protected FloatingActionButton fab;
    protected RelativeLayout rl_root;
    protected View content;
    protected View bottom;
    protected int mode = WordActivity.LEARNING_MODE;
    protected Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Iconify.with(new SimpleLineIconsModule());

        setContentView(R.layout.activity_base_scrolling);

        bundle = this.getIntent().getExtras();
        int content_layout = bundle.getInt("content");
        int bottom_layout = bundle.getInt("bottom");
        mode = bundle.getInt("mode");
        Log.i("Mode", String.valueOf(mode));
        if (content_layout != 0)
            setContent(content_layout);
        if (bottom_layout != 0)
            setBottom(bottom_layout);

        initWidgets();
        addListeners();
    }

    public void initWidgets() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        setTitle("");
        fab = (FloatingActionButton) findViewById(R.id.fab);
        rl_root = (RelativeLayout) findViewById(R.id.rl_root);
    }

    public void addListeners() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    public void setTitle(String title) {
        toolbarLayout.setTitle(title);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        Log.i("Set Title", title);
    }

    public void setContent(int id) {
        NestedScrollView nestedScrollView = (NestedScrollView) findViewById(R.id.content_base_scrolling);
        content = getLayoutInflater().inflate(id, null);
        nestedScrollView.addView(content);
    }

    public void setBottom(int id) {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.bottom_base_scrolling);
        bottom = getLayoutInflater().inflate(id, null);
        linearLayout.addView(bottom);
    }

}
