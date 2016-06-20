package com.chenghao.craman.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.chenghao.craman.model.Word;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

/**
 * Created by Hao on 16/6/8.
 */
public class DataAccess {
    private SqlHelper sqlHelper;

    private final int BOOK1_WORD_COUNT = 2027;
    private final int BOOK2_WORD_COUNT = 1954;
    private final int BOOK3_WORD_COUNT = 2296;

    private final double NEW_WORD_RATE = 0.17;
    private final int MAX_FAMILIARITY = 7;
    private final int MIN_FAMILIARITY = 0;

    public DataAccess() {
        sqlHelper = new SqlHelper();
    }

    public float getUnlearnedWordProportion(String table) {
        Cursor cursor = sqlHelper.query(table, new String[]{"count()"}, "FAMILIARITY = 0", null, null, null, null, null);
        int count = 0;
        if (cursor != null && cursor.moveToFirst()) {
            count = Integer.parseInt(cursor.getString(0));
        }
        if (table.equalsIgnoreCase("book1")) {
            return (float) count / BOOK1_WORD_COUNT;
        } else if (table.equalsIgnoreCase("book2")) {
            return (float) count / BOOK2_WORD_COUNT;
        } else if (table.equalsIgnoreCase("book3")) {
            return (float) count / BOOK3_WORD_COUNT;
        }
        if (cursor != null)
            cursor.close();
        return 0;
    }

    public Word getRandomWord(String table) {
        Word word = new Word();

        Cursor cursor = sqlHelper.query(table, null, null, null, null, null, "RANDOM()", "1");
        if (cursor != null && cursor.moveToFirst()) {
            do {
                word.setSpelling(cursor.getString(1));
                word.setMeaning(cursor.getString(2));
                word.setPhonetic(cursor.getString(3));
                word.setFamiliarity(Integer.parseInt(cursor.getString(5)));
                Log.i("Random Word", word.getSpelling());
            } while (cursor.moveToNext());
        }

        if (cursor != null)
            cursor.close();
        return word;
    }

    // 学习模式获取用于学习的单词
    public Vector<Word> getRandomLearningWords(String table, int count) {
        Vector<Word> words = new Vector<Word>();
        int new_word_count = (int) Math.round(count * NEW_WORD_RATE);
        int learnt_word_count = count - 2 * new_word_count;
        int mastered_word_count = new_word_count;

        Cursor cursor = sqlHelper.query(table, null, "FAMILIARITY = 0", null, null, null, "RANDOM()", String.valueOf(new_word_count));
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Word word = new Word();
                word.setSpelling(cursor.getString(1));
                word.setMeaning(cursor.getString(2));
                word.setPhonetic(cursor.getString(3));
                word.setFamiliarity(Integer.parseInt(cursor.getString(5)));
                word.setStarred(Integer.parseInt(cursor.getString(8)));
                words.add(word);
                Log.i("Random Word", word.getSpelling());
            } while (cursor.moveToNext());
        }

        cursor = sqlHelper.query(table, null, "(LAST_CORRECT is not date('now', 'localtime') and LAST_LEARNT <= date('now', 'localtime'))" +
                " and FAMILIARITY >= 0 and FAMILIARITY < 6", null, null, null, "RANDOM()", String.valueOf(learnt_word_count));
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Word word = new Word();
                word.setSpelling(cursor.getString(1));
                word.setMeaning(cursor.getString(2));
                word.setPhonetic(cursor.getString(3));
                word.setFamiliarity(Integer.parseInt(cursor.getString(5)));
                word.setStarred(Integer.parseInt(cursor.getString(8)));
                words.add(word);
                Log.i("Random Word", word.getSpelling());
            } while (cursor.moveToNext());
        }

        cursor = sqlHelper.query(table, null, "(LAST_CORRECT is not date('now', 'localtime') and LAST_LEARNT <= date('now', 'localtime'))" +
                " and FAMILIARITY >= 6", null, null, null, "RANDOM()", String.valueOf(learnt_word_count));
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Word word = new Word();
                word.setSpelling(cursor.getString(1));
                word.setMeaning(cursor.getString(2));
                word.setPhonetic(cursor.getString(3));
                word.setFamiliarity(Integer.parseInt(cursor.getString(5)));
                word.setStarred(Integer.parseInt(cursor.getString(8)));
                words.add(word);
                Log.i("Random Word", word.getSpelling());
            } while (cursor.moveToNext());
        }

        if (cursor != null)
            cursor.close();
        return words;
    }

    // 测试模式获取用于测试的单词
    public Vector<Word> getRandomTestingWords(int count) {
        Vector<Word> words = new Vector<Word>();
        int count_each[] = new int[3];
        count_each[0] = (int) (count / 3.0);
        count_each[1] = count_each[0];
        count_each[2] = count - count_each[0] - count_each[1];
        for (int i = 1; i <= 3; i++) {
            Cursor cursor = sqlHelper.query("book" + i, null, null, null, null, null, "RANDOM()", String.valueOf(count_each[i - 1]));
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Word word = new Word();
                    word.setSpelling(cursor.getString(1));
                    word.setMeaning(cursor.getString(2));
                    word.setPhonetic(cursor.getString(3));
                    word.setFamiliarity(Integer.parseInt(cursor.getString(5)));
                    word.setStarred(Integer.parseInt(cursor.getString(8)));
                    words.add(word);
                    Log.i("Random Word", word.getSpelling());
                } while (cursor.moveToNext());
                if (cursor != null)
                    cursor.close();
            }
        }

        return words;
    }

    public Word getWord(String table, String spelling) {
        Cursor cursor = sqlHelper.query(table, null, "SPELLING = '" + spelling + "'", null, null, null, null, null);

        Word word = new Word();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                word.setSpelling(cursor.getString(1));
                word.setMeaning(cursor.getString(2));
                word.setPhonetic(cursor.getString(3));
                word.setFamiliarity(Integer.parseInt(cursor.getString(5)));
                word.setStarred(Integer.parseInt(cursor.getString(8)));
                Log.i("Random Word", word.getSpelling());
            } while (cursor.moveToNext());
        }

        if (cursor != null)
            cursor.close();
        return word;
    }

    public void updateWordFamiliarity(String table, String word, int familiarity, boolean correct) {
        ContentValues updatedValues = new ContentValues();
        if (familiarity < MIN_FAMILIARITY) familiarity = MIN_FAMILIARITY;
        if (familiarity > MAX_FAMILIARITY) familiarity = MAX_FAMILIARITY;
        updatedValues.put("FAMILIARITY", familiarity);
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        updatedValues.put("LAST_LEARNT", f.format(date));
        if (correct)
            updatedValues.put("LAST_CORRECT", f.format(date));
        String whereClause = "SPELLING = '" + word + "'";
        sqlHelper.update(table, updatedValues, whereClause, null);
    }

    public void updateStarred(String table, String word, int starred) {
        ContentValues updatedValues = new ContentValues();
        updatedValues.put("STARRED", starred);
        String whereClause = "SPELLING = '" + word + "'";
        sqlHelper.update(table, updatedValues, whereClause, null);
    }

    public void updateLastLearntDate(String table, String word) {
        ContentValues updatedValues = new ContentValues();
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        updatedValues.put("LAST_LEARNT", f.format(date));
        String whereClause = "SPELLING = '" + word + "'";
        sqlHelper.update(table, updatedValues, whereClause, null);
    }

    public int getTodayLearntWordsCount() {
        int count = 0;
        for (int i = 1; i <= 3; i++) {
            Cursor cursor = sqlHelper.query("book" + i, new String[]{"count()"}, "LAST_CORRECT is date('now','localtime')", null, null, null, null, "1");
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    count += Integer.parseInt(cursor.getString(0));
                } while (cursor.moveToNext());
            }
        }
        return count;
    }

    public int getStarredWordsCount() {
        int count = 0;
        for (int i = 1; i <= 3; i++) {
            Cursor cursor = sqlHelper.query("book" + i, new String[]{"count()"}, "STARRED = 1", null, null, null, null, "1");
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    count += Integer.parseInt(cursor.getString(0));
                } while (cursor.moveToNext());
            }
        }
        return count;
    }

    public Vector<Cursor> getStarredWordsCursor() {
        Vector<Cursor> cursors = new Vector<Cursor>();
        for (int i = 1; i <= 3; i++) {
            Cursor cursor = sqlHelper.query("book" + i, null, "STARRED = 1", null, null, null, null, null);
            if (cursor != null) cursor.moveToFirst();
            cursors.add(cursor);
        }
        return cursors;
    }

    public String getTable(String spelling) {
        for (int i = 1; i <= 3; i++) {
            Cursor cursor = sqlHelper.query("book" + i, null, "SPELLING = '" + spelling + "'", null, null, null, null, null);
            if (cursor != null && cursor.getCount() > 0)
                return "book" + i;
        }
        return null;
    }

    public Vector<String> getRandomMeanings() {
        Vector<String> meanings = new Vector<String>();
        String table = "book1";
        double x = Math.random();
        if (x >= 0.67)
            table = "book3";
        else if (x >= 0.33)
            table = "book2";
        Cursor cursor = sqlHelper.query(table, new String[]{"MEANING"}, null, null, null, null, "RANDOM()", "3");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                meanings.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        if (cursor != null)
            cursor.close();
        return meanings;
    }

    public void closeDatabase() {
        sqlHelper.closeDatabase();
    }
}
