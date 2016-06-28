package com.chenghao.craman;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chenghao.craman.database.DataAccess;
import com.chenghao.craman.database.SqlHelper;
import com.chenghao.craman.model.Word;
import com.hanks.htextview.HTextView;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.SimpleLineIconsModule;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    //    private SqlHelper sqlHelper;
    private DataAccess dataAccess;
    private FloatingActionButton fab;
    private LinearLayout ll_learn, ll_preview, ll_refresh, ll_star, ll_test;
    private TextView tv_refresh, tv_pick_phonetic, tv_pick_meaning;
    private HTextView tv_pick_spelling, tv_progress;

    private String current_table = "book1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Iconify.with(new SimpleLineIconsModule());

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        initDatabase();
        dataAccess = new DataAccess();
        initWidgets();
        addListeners();

        File dir = new File("data/data/com.chenghao.craman/voices/");
        if (!dir.exists()) dir.mkdir();
    }

    public void initWidgets() {
        fab = (FloatingActionButton) findViewById(R.id.fab);
        ll_learn = (LinearLayout) findViewById(R.id.ll_learn);
        ll_preview = (LinearLayout) findViewById(R.id.ll_preview);
        ll_refresh = (LinearLayout) findViewById(R.id.ll_refresh);
        ll_star = (LinearLayout) findViewById(R.id.ll_star);
        ll_test = (LinearLayout) findViewById(R.id.ll_test);
        tv_refresh = (TextView) findViewById(R.id.tv_refresh);
        tv_pick_spelling = (HTextView) findViewById(R.id.tv_pick_spelling);
//        tv_pick_spelling.setAnimateType(HTextViewType.SCALE);
        tv_pick_phonetic = (TextView) findViewById(R.id.tv_pick_phonetic);
        tv_pick_meaning = (TextView) findViewById(R.id.tv_pick_meaning);
//        tv_pick_meaning.setAnimateType(HTextViewType.SCALE);
        tv_progress = (HTextView) findViewById(R.id.tv_progress);
        pickOneWord();
        updateProgress();
    }

    private void addListeners() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View search_view = getLayoutInflater().inflate(R.layout.search, null);
                final EditText et_search = (EditText) search_view.findViewById(R.id.et_search);
                final View view = v;
                new AlertDialog.Builder(MainActivity.this).setTitle("查询")
//                        .setIcon(R.mipmap.ic_search_38dp)
                        .setView(search_view)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String word = et_search.getText().toString().toLowerCase();
                                if (word.equals("")) {
                                    Snackbar.make(view, "查询内容不能为空", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                } else {
                                    String table = dataAccess.getTable(word);
                                    if (table == null)
                                        Snackbar.make(view, "对不起，暂时无法找到该单词", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                    else {
                                        Intent intent = new Intent();
                                        Bundle bundle = new Bundle();
                                        bundle.putInt("mode", WordActivity.BROWSING_MODE);
                                        bundle.putInt("content", R.layout.word_content);
                                        bundle.putString("word", word);
                                        bundle.putString("table", table);
                                        intent.putExtras(bundle);
                                        intent.setClass(MainActivity.this, WordActivity.class);
                                        startActivity(intent);
                                    }
                                }
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create().show();
            }
        });

        ll_learn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, WordActivity.class);
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putInt("mode", WordActivity.LEARNING_MODE);
                bundle.putInt("content", R.layout.word_content);
                bundle.putInt("bottom", R.layout.word_bottom);
                intent.putExtras(bundle);
                intent.setClass(MainActivity.this, WordActivity.class);
                startActivity(intent);
            }
        });

        final Animation operatingAnim = AnimationUtils.loadAnimation(this, R.anim.rotate);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);

        ll_preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putInt("mode", WordActivity.BROWSING_MODE);
                bundle.putInt("content", R.layout.word_content);
                bundle.putString("word", tv_pick_spelling.getText().toString());
                bundle.putString("table", current_table);
                intent.putExtras(bundle);
                intent.setClass(MainActivity.this, WordActivity.class);
                startActivity(intent);
            }
        });

        ll_star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dataAccess.getStarredWordsCount() == 0)
                    Snackbar.make(v, "还没有收藏过单词", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                else {
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putInt("mode", WordActivity.REVIEW_MODE);
                    bundle.putInt("content", R.layout.word_content);
                    bundle.putInt("bottom", R.layout.word_bottom);
                    intent.putExtras(bundle);
                    intent.setClass(MainActivity.this, WordActivity.class);
                    startActivity(intent);
                }
            }
        });

        ll_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putInt("content", R.layout.test);
                intent.putExtras(bundle);
                intent.setClass(MainActivity.this, TestActivity.class);
                startActivity(intent);
            }
        });

        ll_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (operatingAnim != null) {
                    tv_refresh.startAnimation(operatingAnim);
                    pickOneWord();
                }
            }
        });
    }

    public void initDatabase() {
        File dir = new File("/data/data/com.chenghao.craman/databases");
        if (!dir.exists())
            dir.mkdir();
        if (!new File(SqlHelper.DB_NAME).exists()) {
            FileOutputStream fos;
            try {
                fos = new FileOutputStream(SqlHelper.DB_NAME);

                byte[] buffer = new byte[8192];
                int count = 0;
                InputStream is = getResources().openRawResource(R.raw.craman);

                while ((count = is.read(buffer)) > 0)
                    fos.write(buffer, 0, count);

                fos.close();
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            Intent intent = new Intent(MainActivity.this, SettingActivity.class);
//            startActivity(intent);
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.about) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt("content", R.layout.about);
            intent.putExtras(bundle);
            intent.setClass(MainActivity.this, AboutActivity.class);
            startActivity(intent);
        } else if (id == R.id.setting) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt("content", R.layout.setting);
            intent.putExtras(bundle);
            intent.setClass(MainActivity.this, SettingActivity.class);
            startActivity(intent);
        } else if (id == R.id.exit) {
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateProgress();
    }

    private void pickOneWord() {
        double x = Math.random();
        if (x >= 0.67)
            current_table = "book3";
        else if (x >= 0.33)
            current_table = "book2";

        Word word = dataAccess.getRandomWord(current_table);
        tv_pick_spelling.animateText(word.getSpelling());
        tv_pick_phonetic.setText(word.getPhonetic());
        tv_pick_meaning.setText(word.getMeaning());
    }

    private void updateProgress() {
        int progress = dataAccess.getTodayLearntWordsCount();
        tv_progress.animateText(String.valueOf(progress));
    }
}
