package com.chenghao.craman.adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.chenghao.craman.R;
import com.chenghao.craman.model.Sentence;

import java.util.ArrayList;

/**
 * Created by Hao on 16/6/27.
 */
public class SentenceAdaptor extends ArrayAdapter<Sentence> {
    public SentenceAdaptor(Context context, ArrayList<Sentence> values) {
        super(context, R.layout.row_sentence, values);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.row_sentence, parent, false);

        TextView tv_sentence = (TextView) view.findViewById(R.id.tv_sentence);
        TextView tv_translation = (TextView) view.findViewById(R.id.tv_translation);

        Sentence sentence = getItem(position);
        tv_sentence.setText(sentence.getOriginal());
        tv_translation.setText(sentence.getTranslation());

        return view;
    }
}
