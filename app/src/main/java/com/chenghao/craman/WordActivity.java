package com.chenghao.craman;

import android.content.DialogInterface;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.chenghao.craman.database.DataAccess;
import com.chenghao.craman.model.Word;
import com.chenghao.craman.util.DownloadVoiceThread;
import com.chenghao.craman.util.XMLRequest;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Vector;

/**
 * Created by zhongya on 16/6/1.
 */
public class WordActivity extends BaseScrollingActivity {
    private Vector<Word> words = new Vector<Word>();
    private DataAccess dataAccess;
    private String current_table = "book3";
    private Word current_word;
    private Vector<Cursor> cursors;
    private Cursor cursor_1, cursor_2, cursor_3;

    public static final int LEARNING_MODE = 1;
    public static final int REVIEW_MODE = 2;
    public static final int BROWSING_MODE = 3;

    private TextView tv_meaning, tv_phonetic, tv_familiarity;
    //    private RatingBar rb_familiarity;
    private LinearLayout ll_phonetic, ll_familiarity, ll_meaning, ll_familiarity_background;
    private Button btn_left;
    private Button btn_right;

    private RequestQueue mQueue;
    private MediaPlayer player;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataAccess = new DataAccess();
        if (mode == LEARNING_MODE) {
            updateWord();
        } else if (mode == BROWSING_MODE) {
            String word = bundle.getString("word");
            String table = bundle.getString("table");
            updateWord(table, word);
        } else if (mode == REVIEW_MODE) {
            updateStarredWord();
        }
        mQueue = Volley.newRequestQueue(this);
        player = new MediaPlayer();
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    @Override
    public void initWidgets() {
        super.initWidgets();

        tv_meaning = (TextView) (content.findViewById(R.id.tv_meaning));
        tv_phonetic = (TextView) (content.findViewById(R.id.tv_phonetic));
        tv_familiarity = (TextView) (content.findViewById(R.id.tv_familiarity));
        ll_phonetic = (LinearLayout) (content.findViewById(R.id.ll_phonetic));
        ll_familiarity = (LinearLayout) (content.findViewById(R.id.ll_familiarity));
        ll_meaning = (LinearLayout) (content.findViewById(R.id.ll_meaning));
        ll_familiarity_background = (LinearLayout) (content.findViewById(R.id.ll_familiarity_background));
//        rb_familiarity = (RatingBar) (content.findViewById(R.id.rb_familiarity));

        if (mode == LEARNING_MODE) {
            btn_left = (Button) (bottom.findViewById(R.id.btn_left));
            btn_right = (Button) (bottom.findViewById(R.id.btn_right));
        } else if (mode == BROWSING_MODE) {
            ll_meaning.setVisibility(View.VISIBLE);
        } else if (mode == REVIEW_MODE) {
            btn_left = (Button) (bottom.findViewById(R.id.btn_left));
            btn_right = (Button) (bottom.findViewById(R.id.btn_right));
            btn_left.setText("显示释义");
            btn_right.setText("下一个");
        }
    }

    @Override
    public void addListeners() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (current_word.getStarred() == 0) {
                    Snackbar.make(rl_root, "已收藏", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    fab.setImageResource(R.mipmap.ic_star_yellow_38dp);
                    dataAccess.updateStarred(current_table, current_word.getSpelling(), 1);
                    current_word.setStarred(1);
                } else {
                    Snackbar.make(rl_root, "已取消收藏", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    fab.setImageResource(R.mipmap.ic_star_white_38dp);
                    dataAccess.updateStarred(current_table, current_word.getSpelling(), 0);
                    current_word.setStarred(0);
                }
            }
        });

        final TranslateAnimation mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                -1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        mShowAction.setDuration(400);

        if (mode == LEARNING_MODE) {
            btn_left.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (btn_left.getText().equals("不认识")) {
                        ll_familiarity_background.startAnimation(mShowAction);
                        ll_meaning.startAnimation(mShowAction);
                        ll_meaning.setVisibility(View.VISIBLE);
                        btn_right.setVisibility(View.GONE);
                        btn_left.setText("下一个");
                        ll_familiarity.setClickable(true);
                    } else { // 记错了（先选了认识）或者下一个（先选了不认识）
                        ll_meaning.setVisibility(View.GONE);
                        btn_right.setVisibility(View.VISIBLE);
                        dataAccess.updateWordFamiliarity(current_table, current_word.getSpelling(), current_word.getFamiliarity() - 1, false);
                        updateWord();
                        btn_left.setText("不认识");
                        btn_right.setText("认识");
                        ll_familiarity.setClickable(false);
                    }
                }
            });

            btn_right.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (btn_right.getText().equals("认识")) {
                        ll_familiarity_background.startAnimation(mShowAction);
                        ll_meaning.startAnimation(mShowAction);
                        ll_meaning.setVisibility(View.VISIBLE);
                        btn_left.setText("记错了");
                        btn_right.setText("记对了");
                        ll_familiarity.setClickable(true);
                    } else { // 记对了
                        ll_meaning.setVisibility(View.GONE);
                        dataAccess.updateWordFamiliarity(current_table, current_word.getSpelling(), current_word.getFamiliarity() + 1, true);
                        updateWord();
                        btn_left.setText("不认识");
                        btn_right.setText("认识");
                        ll_familiarity.setClickable(false);
                    }
                }
            });
        } else if (mode == REVIEW_MODE) {
            btn_left.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ll_familiarity_background.startAnimation(mShowAction);
                    ll_meaning.startAnimation(mShowAction);
                    ll_meaning.setVisibility(View.VISIBLE);
                    btn_left.setEnabled(false);
                }
            });

            btn_right.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!updateStarredWord())
                        Snackbar.make(rl_root, "已经是最后一个了", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    else btn_left.setEnabled(true);
                }
            });
        }

        ll_familiarity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getLayoutInflater().inflate(R.layout.change_familiarity, null);
                final RatingBar ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);

                new AlertDialog.Builder(WordActivity.this)
                        .setTitle("修改熟悉度")
                        .setView(view)
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                float familiarity = ratingBar.getRating();
                                // 更新数据库
                                if (familiarity != current_word.getFamiliarity()) {
                                    dataAccess.updateWordFamiliarity(current_table, current_word.getSpelling(), (int) familiarity, true);
//                                    rb_familiarity.setRating((int) familiarity);
                                    updateFamiliarityView((int) familiarity);
                                    current_word.setFamiliarity((int) familiarity);
                                }
                                dialog.dismiss();
                            }
                        })
                        .setNeutralButton("什么是熟悉度", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new AlertDialog.Builder(WordActivity.this)
                                        .setTitle("什么是熟悉度")
                                        .setMessage("熟悉度是Craman衡量你对单词掌握程度的指标。\n" +
                                                "每认对一次单词，该单词的熟悉度加一。\n" +
                                                "熟悉度低的单词在学习时出现频率更高，而熟悉度高的单词出现频率相对较低。\n")
                                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        }).create().show();
                            }
                        }).create().show();
            }
        });

        if (mode == LEARNING_MODE)
            ll_familiarity.setClickable(false);

        ll_phonetic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "http://dict-co.iciba.com/api/dictionary.php?w=" + current_word.getSpelling() + "&key=0EAE08A016D6688F64AB3EBB2337BFB0";
                XMLRequest xmlRequest = new XMLRequest(
                        url,
                        new Response.Listener<XmlPullParser>() {
                            @Override
                            public void onResponse(XmlPullParser response) {
                                try {
                                    int eventType = response.getEventType();
                                    int count = 0;
                                    while (eventType != XmlPullParser.END_DOCUMENT) {
                                        switch (eventType) {
                                            case XmlPullParser.START_TAG:
                                                String nodeName = response.getName();
                                                if (nodeName.equals("pron")) {
                                                    count++;
                                                    if (count == 2) {
                                                        if (response.next() == XmlPullParser.TEXT) {
                                                            Toast.makeText(getApplicationContext(), response.getText(), Toast.LENGTH_LONG);
                                                            String pronunciationUrl = response.getText();
                                                            Log.i("Pronunciation", pronunciationUrl);
                                                            new DownloadVoiceThread(getApplicationContext(), pronunciationUrl, player).start();
                                                        }
                                                        break;
                                                    }
                                                }
                                        }
                                        eventType = response.next();
                                    }
                                } catch (XmlPullParserException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("TAG", error.getMessage(), error);
                    }
                });
                xmlRequest.setTag("Pronunciation");
                mQueue.add(xmlRequest);
            }
        });
    }

    public void updateWord() {
        // 初始化words
        if (words.size() == 0) {
            float book1_unlearned = 0, book2_unlearned = 0, book3_unlearned = 0;

            book1_unlearned = dataAccess.getUnlearnedWordProportion("book1");
            book2_unlearned = dataAccess.getUnlearnedWordProportion("book2");
            book3_unlearned = dataAccess.getUnlearnedWordProportion("book3");

            if (book1_unlearned <= book2_unlearned && book1_unlearned <= book3_unlearned) {
                current_table = "book1";
            } else if (book2_unlearned <= book3_unlearned) {
                current_table = "book2";
            }

            words = dataAccess.getRandomLearningWords(current_table, 24);
        }

        current_word = words.get(0);
        updateView();
        words.remove(0);
        Log.i("Update Word", words.size() + " left");
    }

    public void updateWord(String table, String word) {
        current_table = table;
        current_word = dataAccess.getWord(table, word);
        updateView();
    }

    public boolean updateStarredWord() {
        if (cursors == null) {
            cursors = dataAccess.getStarredWordsCursor();
            cursor_1 = cursors.get(0);
            cursor_2 = cursors.get(1);
            cursor_3 = cursors.get(2);
        }
        if (current_word == null)
            current_word = new Word();
        if (cursor_1 != null && cursor_1.getCount() > 0) {
            current_word.setSpelling(cursor_1.getString(1));
            current_word.setMeaning(cursor_1.getString(2));
            current_word.setPhonetic(cursor_1.getString(3));
            current_word.setFamiliarity(Integer.parseInt(cursor_1.getString(5)));
            current_word.setStarred(Integer.parseInt(cursor_1.getString(8)));
            Log.i("Starred Word", current_word.getSpelling());
            current_table = dataAccess.getTable(current_word.getSpelling());
            if (!cursor_1.moveToNext()) cursor_1 = null;
        } else if (cursor_2 != null && cursor_2.getCount() > 0) {
            current_word.setSpelling(cursor_2.getString(1));
            current_word.setMeaning(cursor_2.getString(2));
            current_word.setPhonetic(cursor_2.getString(3));
            current_word.setFamiliarity(Integer.parseInt(cursor_2.getString(5)));
            current_word.setStarred(Integer.parseInt(cursor_2.getString(8)));
            Log.i("Starred Word", current_word.getSpelling());
            current_table = dataAccess.getTable(current_word.getSpelling());
            if (!cursor_2.moveToNext()) cursor_2 = null;
        } else if (cursor_3 != null && cursor_3.getCount() > 0) {
            current_word.setSpelling(cursor_3.getString(1));
            current_word.setMeaning(cursor_3.getString(2));
            current_word.setPhonetic(cursor_3.getString(3));
            current_word.setFamiliarity(Integer.parseInt(cursor_3.getString(5)));
            current_word.setStarred(Integer.parseInt(cursor_3.getString(8)));
            Log.i("Starred Word", current_word.getSpelling());
            current_table = dataAccess.getTable(current_word.getSpelling());
            if (!cursor_3.moveToNext()) cursor_3 = null;
        } else return false;
        updateView();
        return true;
    }

    public void updateView() {
        setTitle(current_word.getSpelling());
        Log.i("Update Word", current_word.getSpelling());
        tv_meaning.setText(current_word.getMeaning());
        tv_phonetic.setText(current_word.getPhonetic());
        if (current_word.getStarred() == 1)
            fab.setImageResource(R.mipmap.ic_star_yellow_38dp);
        else
            fab.setImageResource(R.mipmap.ic_star_white_38dp);
//        rb_familiarity.setRating(current_word.getFamiliarity());
        updateFamiliarityView(current_word.getFamiliarity());
    }

    private void updateFamiliarityView(int familiarity) {
        String s = " ";
        for (int i = 1; i <= familiarity; i++) {
            s += "{icon-star} ";
        }
        for (int i = 1; i <= 7 - familiarity; i++) {
            s += "{icon-star #B6B6B6} ";
        }
        s += " ";
        tv_familiarity.setText(s);
    }

    @Override
    protected void onDestroy() {
        if (mQueue != null) {
            mQueue.cancelAll("Pronunciation");
        }
        if (player.isPlaying()) {
            player.stop();
        }
        player.release();
        super.onDestroy();
    }
}
