package com.chenghao.craman.adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.chenghao.craman.R;
import com.chenghao.craman.model.Word;

import java.util.ArrayList;

/**
 * Created by Hao on 16/7/4.
 */
public class WordAdaptor extends ArrayAdapter<Word> {
    public WordAdaptor(Context context, ArrayList<Word> values) {
        super(context, R.layout.row_word, values);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.row_word, parent, false);

        TextView tv_row_spelling = (TextView) view.findViewById(R.id.tv_row_spelling);
        TextView tv_row_meaning = (TextView) view.findViewById(R.id.tv_row_meaning);

        Word word = getItem(position);
        tv_row_spelling.setText(word.getSpelling());
        tv_row_meaning.setText(word.getMeaning());

        return view;
    }
}
