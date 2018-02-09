package ascent.com.dfsc.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import ascent.com.dfsc.Activity.Login;
import ascent.com.dfsc.R;

/**
 * Created by Admin on 1/11/2018.
 */

public class CountryAdapter extends BaseAdapter {
    Context context;
    ArrayList<Login.Countries> list;
    LayoutInflater inflter;

    public CountryAdapter(Context context, ArrayList<Login.Countries> list) {
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
        view = inflter.inflate(R.layout.country_spinner_item, null);

        ImageView icon = (ImageView) view.findViewById(R.id.flag);
        TextView names = (TextView) view.findViewById(R.id.name);



        if(list.get(i).id.matches("0")){
            icon.setVisibility(View.GONE);
            names.setText(list.get(i).value);
        }else{
            Picasso.with(context).load(list.get(i).flag).into(icon);
            names.setText(list.get(i).value);
        }

        return view;
    }
}