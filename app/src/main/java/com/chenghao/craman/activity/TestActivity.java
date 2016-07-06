package com.chenghao.craman.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chenghao.craman.R;
import com.chenghao.craman.animator.FadeOutAndIn;
import com.chenghao.craman.database.DataAccess;
import com.chenghao.craman.model.Word;
import com.hanks.htextview.HTextView;

import java.util.Vector;

/**
 * Created by Hao on 16/6/11.
 */
public class TestActivity extends BaseActivity {
    private LinearLayout ll_test_word, ll_a_background, ll_b_background, ll_c_background, ll_d_background, ll_a, ll_b, ll_c, ll_d;
    private HTextView tv_test_spelling;
    private TextView tv_progress, tv_test_phonetic, tv_meaning_a, tv_meaning_b, tv_meaning_c, tv_meaning_d;
    private ProgressBar pb_progress;
    private com.joanzapata.iconify.widget.IconTextView tv_a, tv_b, tv_c, tv_d;
    private Button btn_next;

    private Vector<Word> words = new Vector<Word>();
    private Word current_word;
    private int answer = 1, chosen = 1, count = 0, correct = 0;
    private int max = 20;
    private DataAccess dataAccess;

    private FadeOutAndIn fadeOutAndIn_a, fadeOutAndIn_b, fadeOutAndIn_c, fadeOutAndIn_d;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("测试");
        dataAccess = new DataAccess();

        fadeOutAndIn_a = new FadeOutAndIn(tv_meaning_a);
        fadeOutAndIn_b = new FadeOutAndIn(tv_meaning_b);
        fadeOutAndIn_c = new FadeOutAndIn(tv_meaning_c);
        fadeOutAndIn_d = new FadeOutAndIn(tv_meaning_d);

        updateWord();
    }

    @Override
    public void initWidgets() {
        super.initWidgets();

        ll_test_word = (LinearLayout) content.findViewById(R.id.ll_test_word);
        ll_a_background = (LinearLayout) content.findViewById(R.id.ll_a_background);
        ll_b_background = (LinearLayout) content.findViewById(R.id.ll_b_background);
        ll_c_background = (LinearLayout) content.findViewById(R.id.ll_c_background);
        ll_d_background = (LinearLayout) content.findViewById(R.id.ll_d_background);
        ll_a = (LinearLayout) content.findViewById(R.id.ll_a);
        ll_b = (LinearLayout) content.findViewById(R.id.ll_b);
        ll_c = (LinearLayout) content.findViewById(R.id.ll_c);
        ll_d = (LinearLayout) content.findViewById(R.id.ll_d);
        tv_test_spelling = (HTextView) content.findViewById(R.id.tv_test_spelling);
        tv_progress = (TextView) content.findViewById(R.id.tv_progress);
        tv_test_phonetic = (TextView) content.findViewById(R.id.tv_test_phonetic);
        tv_meaning_a = (TextView) content.findViewById(R.id.tv_meaning_a);
        tv_meaning_b = (TextView) content.findViewById(R.id.tv_meaning_b);
        tv_meaning_c = (TextView) content.findViewById(R.id.tv_meaning_c);
        tv_meaning_d = (TextView) content.findViewById(R.id.tv_meaning_d);
        pb_progress = (ProgressBar) content.findViewById(R.id.pb_progress);
        tv_a = (com.joanzapata.iconify.widget.IconTextView) content.findViewById(R.id.tv_a);
        tv_b = (com.joanzapata.iconify.widget.IconTextView) content.findViewById(R.id.tv_b);
        tv_c = (com.joanzapata.iconify.widget.IconTextView) content.findViewById(R.id.tv_c);
        tv_d = (com.joanzapata.iconify.widget.IconTextView) content.findViewById(R.id.tv_d);
        btn_next = (Button) content.findViewById(R.id.btn_next);
    }

    @Override
    public void addListeners() {
        super.addListeners();

        ll_test_word.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String word = current_word.getSpelling();
                String table = dataAccess.getTable(word);
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putInt("mode", WordActivity.BROWSING_MODE);
                bundle.putInt("content", R.layout.word_content);
                bundle.putString("word", word);
                bundle.putString("table", table);
                intent.putExtras(bundle);
                intent.setClass(TestActivity.this, WordActivity.class);
                startActivity(intent);
            }
        });

        ll_a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chosen = 1;
                if (answer == 1) {
                    ll_a_background.setBackgroundColor(getResources().getColor(R.color.colorRightLight));
                    correct++;
                } else {
                    ll_a_background.setBackgroundColor(getResources().getColor(R.color.colorWrongLight));
                }
                displayAnswer();
            }
        });

        ll_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chosen = 2;
                if (answer == 2) {
                    ll_b_background.setBackgroundColor(getResources().getColor(R.color.colorRightLight));
                    correct++;
                } else {
                    ll_b_background.setBackgroundColor(getResources().getColor(R.color.colorWrongLight));
                }
                displayAnswer();
            }
        });

        ll_c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chosen = 3;
                if (answer == 3) {
                    ll_c_background.setBackgroundColor(getResources().getColor(R.color.colorRightLight));
                    correct++;
                } else {
                    ll_c_background.setBackgroundColor(getResources().getColor(R.color.colorWrongLight));
                }
                displayAnswer();
            }
        });

        ll_d.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chosen = 4;
                if (answer == 4) {
                    ll_d_background.setBackgroundColor(getResources().getColor(R.color.colorRightLight));
                    correct++;
                } else {
                    ll_d_background.setBackgroundColor(getResources().getColor(R.color.colorWrongLight));
                }
                displayAnswer();
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btn_next.getText().equals("下一个")) {
                    updateWord();
                } else if (btn_next.getText().equals("提交")) {
                    Snackbar.make(cl_root, "本次做对了" + correct + "道题，正确率" + correctRate(), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    SharedPreferences mySharedPreferences = getSharedPreferences("test", MODE_PRIVATE);
                    String last1 = mySharedPreferences.getString("last1", "");
                    String last2 = mySharedPreferences.getString("last2", "");
                    if (!last1.equals("")) {
                        mySharedPreferences.edit().putString("last2", last1).commit();
                    }
                    if (!last2.equals("")) {
                        mySharedPreferences.edit().putString("last3", last2).commit();
                    }
                    mySharedPreferences.edit().putString("last1", correct + "/" + max + "  " + correctRate()).commit();
                }
            }
        });
    }

    /*
    如果没有单词先取测试单词
    然后更新显示
     */
    private void updateWord() {
        if (count == 0) {
            words = dataAccess.getRandomTestingWords(max);
        }

        current_word = words.get(0);
        updateView();
        words.remove(0);
        count++;
        tv_progress.setText(count + "/" + max);
        pb_progress.setProgress((int) ((double) count / max * 100));
        Log.i("Update Word", words.size() + " left");
    }

    /*
    更新显示的单词和选项
     */
    private void updateView() {
        initializeView();

        tv_test_spelling.animateText(current_word.getSpelling());
        tv_test_phonetic.setText(current_word.getPhonetic());

        answer = (int) Math.ceil(Math.random() * 4);
        Vector<String> meanings = dataAccess.getRandomMeanings();

        if (answer == 1) {
            fadeOutAndIn_a.setText(current_word.getMeaning());
            fadeOutAndIn_a.start();

            fadeOutAndIn_b.setText(meanings.get(0));
            fadeOutAndIn_b.start();

            fadeOutAndIn_c.setText(meanings.get(1));
            fadeOutAndIn_c.start();

            fadeOutAndIn_d.setText(meanings.get(2));
            fadeOutAndIn_d.start();
        } else if (answer == 2) {
            fadeOutAndIn_a.setText(meanings.get(0));
            fadeOutAndIn_a.start();

            fadeOutAndIn_b.setText(current_word.getMeaning());
            fadeOutAndIn_b.start();

            fadeOutAndIn_c.setText(meanings.get(1));
            fadeOutAndIn_c.start();

            fadeOutAndIn_d.setText(meanings.get(2));
            fadeOutAndIn_d.start();
        } else if (answer == 3) {
            fadeOutAndIn_a.setText(meanings.get(0));
            fadeOutAndIn_a.start();

            fadeOutAndIn_b.setText(meanings.get(1));
            fadeOutAndIn_b.start();

            fadeOutAndIn_c.setText(current_word.getMeaning());
            fadeOutAndIn_c.start();

            fadeOutAndIn_d.setText(meanings.get(2));
            fadeOutAndIn_d.start();
        } else if (answer == 4) {
            fadeOutAndIn_a.setText(meanings.get(0));
            fadeOutAndIn_a.start();

            fadeOutAndIn_b.setText(meanings.get(1));
            fadeOutAndIn_b.start();

            fadeOutAndIn_c.setText(meanings.get(2));
            fadeOutAndIn_c.start();

            fadeOutAndIn_d.setText(current_word.getMeaning());
            fadeOutAndIn_d.start();
        }
    }

    /*
    先把之前对界面颜色之类的改动还原
     */
    private void initializeView() {
        ll_test_word.setClickable(false);
        btn_next.setEnabled(false);
        if (answer == 1) {
            tv_a.setText("A");
            tv_a.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        } else if (answer == 2) {
            tv_b.setText("B");
            tv_b.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        } else if (answer == 3) {
            tv_c.setText("C");
            tv_c.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        } else if (answer == 4) {
            tv_d.setText("D");
            tv_d.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        if (chosen == 1) {
            ll_a_background.setBackgroundColor(getResources().getColor(R.color.colorWhite));
        } else if (chosen == 2) {
            ll_b_background.setBackgroundColor(getResources().getColor(R.color.colorWhite));
        } else if (chosen == 3) {
            ll_c_background.setBackgroundColor(getResources().getColor(R.color.colorWhite));
        } else if (chosen == 4) {
            ll_d_background.setBackgroundColor(getResources().getColor(R.color.colorWhite));
        }
        ll_a.setClickable(true);
        ll_b.setClickable(true);
        ll_c.setClickable(true);
        ll_d.setClickable(true);
    }

    private void displayAnswer() {
        ll_a.setClickable(false);
        ll_b.setClickable(false);
        ll_c.setClickable(false);
        ll_d.setClickable(false);
        if (answer == 1) {
            tv_a.setText(" {typcn-tick-outline spin} ");
            tv_a.setTextColor(getResources().getColor(R.color.colorRightDark));
        } else if (answer == 2) {
            tv_b.setText(" {typcn-tick-outline spin} ");
            tv_b.setTextColor(getResources().getColor(R.color.colorRightDark));
        } else if (answer == 3) {
            tv_c.setText(" {typcn-tick-outline spin} ");
            tv_c.setTextColor(getResources().getColor(R.color.colorRightDark));
        } else if (answer == 4) {
            tv_d.setText(" {typcn-tick-outline spin} ");
            tv_d.setTextColor(getResources().getColor(R.color.colorRightDark));
        }
        if (count == max) {
            btn_next.setText("提交");
        }
        btn_next.setEnabled(true);
        ll_test_word.setClickable(true);
    }

    private String correctRate() {
        int rate = (int) Math.round((double) correct / max * 100);
        return rate + "%";
    }
}
