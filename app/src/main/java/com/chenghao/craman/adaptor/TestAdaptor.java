package com.chenghao.craman.adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.chenghao.craman.R;
import com.chenghao.craman.model.Test;

import java.util.ArrayList;

/**
 * Created by Hao on 16/7/5.
 */
public class TestAdaptor extends ArrayAdapter<Test> {
    public TestAdaptor(Context context, ArrayList<Test> values) {
        super(context, R.layout.row_test, values);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.row_test, parent, false);

        TextView tv_row_number = (TextView) view.findViewById(R.id.tv_row_number);
        TextView tv_row_result = (TextView) view.findViewById(R.id.tv_row_result);

        Test test = getItem(position);
        tv_row_number.setText(String.valueOf(position));

        int total = test.getTotal();
        int correct = test.getCorrect();
        int rate = (int) Math.round((double) correct / total * 100);
        tv_row_result.setText(correct + "/" + total + "  " + rate + "%");

        return view;
    }
}
