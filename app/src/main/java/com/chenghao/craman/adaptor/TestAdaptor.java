package com.chenghao.craman.adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.chenghao.craman.R;

import java.util.ArrayList;

/**
 * Created by Hao on 16/7/5.
 */
public class TestAdaptor extends ArrayAdapter<String> {
    public TestAdaptor(Context context, ArrayList<String> values) {
        super(context, R.layout.row_test, values);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.row_test, parent, false);

        TextView tv_row_number = (TextView) view.findViewById(R.id.tv_row_number);
        TextView tv_row_result = (TextView) view.findViewById(R.id.tv_row_result);

        String test = getItem(position);
        tv_row_number.setText(String.valueOf(position + 1));

        tv_row_result.setText(test);

        return view;
    }
}
