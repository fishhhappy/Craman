package com.chenghao.craman.model;

/**
 * Created by Hao on 16/6/8.
 */
public class Word {
    private String spelling;
    private String meaning;
    private String phonetic;
    private int familiarity;
    private int starred;

    public Word(String spelling, String meaning, String phonetic, int familiarity, int starred) {
        this.spelling = spelling;
        this.meaning = meaning;
        this.phonetic = phonetic;
        this.familiarity = familiarity;
        this.starred = starred;
    }

    public Word() {

    }

    public void setSpelling(String spelling) {
        this.spelling = spelling;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public void setPhonetic(String phonetic) {
        this.phonetic = phonetic;
    }

    public void setFamiliarity(int familiarity) {
        this.familiarity = familiarity;
    }

    public void setStarred(int starred) {
        this.starred = starred;
    }

    public String getSpelling() {
        return spelling;
    }

    public String getMeaning() {
        return meaning;
    }

    public String getPhonetic() {
        return phonetic;
    }

    public int getFamiliarity() {
        return familiarity;
    }

    public int getStarred() {
        return starred;
    }
}
