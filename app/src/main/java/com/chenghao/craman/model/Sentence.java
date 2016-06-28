package com.chenghao.craman.model;

/**
 * Created by Hao on 16/6/27.
 */
public class Sentence {
    private String original;
    private String translation;

    public Sentence(String original, String translation) {
        this.original = original;
        this.translation = translation;
    }

    public String getOriginal() {
        return original;
    }

    public String getTranslation() {
        return translation;
    }
}
