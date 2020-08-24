package com.example.bisca;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;

public class MyListAdapterPlayers extends ArrayAdapter<Giocatore> {

        public MyListAdapterPlayers(@NonNull Context context, int resource, @NonNull List<Giocatore> objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_list_players, null);
            TextView nomegioc = convertView.findViewById(R.id.TVItemGiocPlayers);
            TextView esclam = convertView.findViewById(R.id.TVItemGiocEsclam);
            ImageView imggioc = convertView.findViewById(R.id.IMPlayers);
            nomegioc.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/montserratregular.ttf"));
            Giocatore c = getItem(position);
            Log.d("ADAPTERNAME", c.getName());
            nomegioc.setText(c.getName());
            esclam.setText('"' + c.getFrase() + '"');
            if (c.getVite() == 0)
                imggioc.setVisibility(View.INVISIBLE);
            else
                imggioc.setVisibility(View.VISIBLE);
            return convertView;
        }

}
