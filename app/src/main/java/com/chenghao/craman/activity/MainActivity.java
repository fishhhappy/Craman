package com.chenghao.craman.activity;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chenghao.craman.R;
import com.chenghao.craman.adaptor.TestAdaptor;
import com.chenghao.craman.adaptor.WordAdaptor;
import com.chenghao.craman.database.DataAccess;
import com.chenghao.craman.database.SqlHelper;
import com.chenghao.craman.model.Word;
import com.chenghao.craman.view.CustomListView;
import com.hanks.htextview.HTextView;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.SimpleLineIconsModule;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private DataAccess dataAccess;
    private FloatingActionButton fab;
    private TextView tv_pick_phonetic, tv_pick_meaning, tv_progress, tv_test;
    private HTextView tv_pick_spelling;
    private Button btn_refresh, btn_detail, btn_learn, btn_star, btn_test;
    private ImageView iv_expand_learnt_word, iv_expand_starred_word;
    private RelativeLayout rl_expand_learnt_word, rl_expand_starred_word;
    private CustomListView lv_learnt_word, lv_starred_word, lv_test;

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

        updateLatestTests();
    }

    public void initWidgets() {
        fab = (FloatingActionButton) findViewById(R.id.fab);
        tv_pick_spelling = (HTextView) findViewById(R.id.tv_pick_spelling);
        tv_pick_phonetic = (TextView) findViewById(R.id.tv_pick_phonetic);
        tv_pick_meaning = (TextView) findViewById(R.id.tv_pick_meaning);
        tv_progress = (TextView) findViewById(R.id.tv_progress);
        tv_test = (TextView) findViewById(R.id.tv_test);
        btn_refresh = (Button) findViewById(R.id.btn_refresh);
        btn_detail = (Button) findViewById(R.id.btn_detail);
        btn_learn = (Button) findViewById(R.id.btn_learn);
        btn_star = (Button) findViewById(R.id.btn_star);
        btn_test = (Button) findViewById(R.id.btn_test);
        iv_expand_learnt_word = (ImageView) findViewById(R.id.iv_expand_learnt_word);
        iv_expand_starred_word = (ImageView) findViewById(R.id.iv_expand_starred_word);
        rl_expand_learnt_word = (RelativeLayout) findViewById(R.id.rl_expand_learnt_word);
        rl_expand_starred_word = (RelativeLayout) findViewById(R.id.rl_expand_starred_word);
        lv_learnt_word = (CustomListView) findViewById(R.id.lv_learnt_word);
        lv_starred_word = (CustomListView) findViewById(R.id.lv_starred_word);
        lv_test = (CustomListView) findViewById(R.id.lv_test);

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

        btn_learn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

        btn_detail.setOnClickListener(new View.OnClickListener() {
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

        btn_star.setOnClickListener(new View.OnClickListener() {
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

        btn_test.setOnClickListener(new View.OnClickListener() {
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

        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickOneWord();
            }
        });

        rl_expand_learnt_word.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 展开
                if (lv_learnt_word.getVisibility() == View.GONE) {
                    if (!updateTodayLearntWords()) {
                        Snackbar.make(v, "今日还没有学习，快去背单词吧", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                }
                // 收起
                else {
                    Animator animator = AnimatorInflater.loadAnimator(MainActivity.this, R.animator.rotate_counterclockwise);
                    animator.setTarget(iv_expand_learnt_word);
                    animator.start();

                    lv_learnt_word.setVisibility(View.GONE);
                }
            }
        });

        rl_expand_starred_word.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 展开
                if (lv_starred_word.getVisibility() == View.GONE) {
                    if (!updateStarredWords()) {
                        Snackbar.make(v, "还没有收藏过单词", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                }
                // 收起
                else {
                    Animator animator = AnimatorInflater.loadAnimator(MainActivity.this, R.animator.rotate_counterclockwise);
                    animator.setTarget(iv_expand_starred_word);
                    animator.start();

                    lv_starred_word.setVisibility(View.GONE);
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
        updateLatestTests();

        if (lv_learnt_word.getVisibility() == View.VISIBLE) {
            if (!updateTodayLearntWords()) {
                Animator animator = AnimatorInflater.loadAnimator(MainActivity.this, R.animator.rotate_counterclockwise);
                animator.setTarget(iv_expand_learnt_word);
                animator.start();

                lv_learnt_word.setVisibility(View.GONE);
            }
        }

        if (lv_starred_word.getVisibility() == View.VISIBLE) {
            if (!updateStarredWords()) {
                Animator animator = AnimatorInflater.loadAnimator(MainActivity.this, R.animator.rotate_counterclockwise);
                animator.setTarget(iv_expand_starred_word);
                animator.start();

                lv_starred_word.setVisibility(View.GONE);
            }
        }
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
        tv_progress.setText("今日已学 " + String.valueOf(progress) + " 个");
    }

    public void updateLatestTests() {
        SharedPreferences mySharedPreferences = getSharedPreferences("test", MODE_PRIVATE);
        ArrayList<String> tests = new ArrayList<String>();

        String last1 = mySharedPreferences.getString("last1", "");
        if (!last1.equals("")) {
            tests.add(last1);
        }

        String last2 = mySharedPreferences.getString("last2", "");
        if (!last2.equals("")) {
            tests.add(last2);
        }

        String last3 = mySharedPreferences.getString("last3", "");
        if (!last3.equals("")) {
            tests.add(last3);
        }

        if (tests.size() > 0) {
            TestAdaptor testAdaptor = new TestAdaptor(this, tests);
            lv_test.setAdapter(testAdaptor);
            lv_test.setVisibility(View.VISIBLE);

            if (tests.size() == 1) {
                tv_test.setText("最近一次测试的正确率");
            } else if (tests.size() == 2) {
                tv_test.setText("最近两次测试的正确率");
            } else if (tests.size() == 3) {
                tv_test.setText("最近三次测试的正确率");
            }
        } else {
            tv_test.setText("最近还没有参加过测试");
            lv_test.setVisibility(View.GONE);
        }
    }

    public boolean updateStarredWords() {
        final ArrayList<Word> words = dataAccess.getStarredWords();

        if (words.size() > 0) {
            Animator animator = AnimatorInflater.loadAnimator(MainActivity.this, R.animator.rotate_clockwise);
            animator.setTarget(iv_expand_starred_word);
            animator.start();

            WordAdaptor wordAdaptor = new WordAdaptor(MainActivity.this, words);
            lv_starred_word.setAdapter(wordAdaptor);
            lv_starred_word.setVisibility(View.INVISIBLE);

            ObjectAnimator fadeIn = ObjectAnimator.ofFloat(lv_starred_word, "alpha", 0f, 1f);
            fadeIn.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    lv_starred_word.setVisibility(View.VISIBLE);
                }
            });
            fadeIn.setDuration(500);
            fadeIn.setInterpolator(new AccelerateDecelerateInterpolator());
            fadeIn.start();

            lv_starred_word.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String word = words.get(position).getSpelling();
                    String table = dataAccess.getTable(word);
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
            });
        } else {
            return false;
        }

        return true;
    }

    public boolean updateTodayLearntWords() {
        final ArrayList<Word> words = dataAccess.getTodayLearntWords();

        if (words.size() > 0) {
            Animator animator = AnimatorInflater.loadAnimator(MainActivity.this, R.animator.rotate_clockwise);
            animator.setTarget(iv_expand_learnt_word);
            animator.start();

            WordAdaptor wordAdaptor = new WordAdaptor(MainActivity.this, words);
            lv_learnt_word.setAdapter(wordAdaptor);
            lv_learnt_word.setVisibility(View.INVISIBLE);

            ObjectAnimator fadeIn = ObjectAnimator.ofFloat(lv_learnt_word, "alpha", 0f, 1f);
            fadeIn.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    lv_learnt_word.setVisibility(View.VISIBLE);
                }
            });
            fadeIn.setDuration(500);
            fadeIn.setInterpolator(new AccelerateDecelerateInterpolator());
            fadeIn.start();

            lv_learnt_word.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String word = words.get(position).getSpelling();
                    String table = dataAccess.getTable(word);
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
            });
        } else {
            return false;
        }

        return true;
    }
}
