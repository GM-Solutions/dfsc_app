package com.fsc.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import com.fsc.Activity.Login;
import com.fsc.Modal.LanguageGetSet;
import com.fsc.R;

/**
 * Created by Admin on 1/11/2018.
 */

public class LanguageAdapter extends BaseAdapter {
    Context context;
    ArrayList<LanguageGetSet> list;
    LayoutInflater inflter;

    public LanguageAdapter(Context context, ArrayList<LanguageGetSet> list) {
        this.context = context;
        this.list = list;
        inflter = (LayoutInflater.from(context));
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.spinner_item, null);

        TextView names = (TextView) view.findViewById(R.id.name);

        names.setText(list.get(i).value);

        return view;
    }
}