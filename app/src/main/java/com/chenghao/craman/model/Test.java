package com.chenghao.craman.model;

/**
 * Created by Hao on 16/7/5.
 */
public class Test {
    private int total, correct;

    public Test(int total, int correct) {
        this.total = total;
        this.correct = correct;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getCorrect() {
        return correct;
    }

    public void setCorrect(int correct) {
        this.correct = correct;
    }
}
