package com.example.bisca;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;

public class MyListAdapterAlbo extends ArrayAdapter<Giocatore> {

        public MyListAdapterAlbo(@NonNull Context context, int resource, @NonNull List<Giocatore> objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.albo_item_list, null);
            TextView nomegioc = convertView.findViewById(R.id.tvAlbo);
            nomegioc.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/montserratregular.ttf"));
            nomegioc.setText("" + getItem(position).getVite() + "  -  " + getItem(position).getName());
            return convertView;
        }

}
