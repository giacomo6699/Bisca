package com.example.bisca;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;

public class MyListAdapter extends ArrayAdapter<Giocatore> {

        public MyListAdapter(@NonNull Context context, int resource, @NonNull List<Giocatore> objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_list, null);
            LinearLayout linear = convertView.findViewById(R.id.LLItemGioc);
            TextView nomegioc = convertView.findViewById(R.id.TVItemGioc);
            TextView vitegioc = convertView.findViewById(R.id.TVItemVite);
            nomegioc.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/montserratregular.ttf"));
            vitegioc.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/montserratmedium.ttf"));
            Giocatore c = getItem(position);
            if ((position % 2) == 0){
                linear.setBackgroundResource(R.drawable.rectanglecolor);
                nomegioc.setTextColor(Color.parseColor("#FFFFFFFF"));
                TextView tvescl = convertView.findViewById(R.id.TVEsclamazione);
                ImageView imgpunt = convertView.findViewById(R.id.message_tail);
                ImageView imgspazio = convertView.findViewById(R.id.IMSpazioinPiu);
                tvescl.setBackgroundResource(R.drawable.rectangle);
                tvescl.setTextColor(Color.parseColor("#EA252424"));
                tvescl.setText(c.getFrase());
                imgpunt.setImageResource(R.drawable.puntinabianca);
                imgspazio.setImageResource(R.drawable.rectangle);
            }

            SharedPreferences sh = getContext().getSharedPreferences("InfoGenerali", getContext().MODE_PRIVATE);
            int vitetotali = sh.getInt("Numero Vite", 0);
            int vite = c.getVite();
            if (vite == vitetotali){
                vitegioc.setTextColor(getContext().getResources().getColor(R.color.viteverde));
            } else if (vite <= (vitetotali/2)){
                vitegioc.setTextColor(getContext().getResources().getColor(R.color.viterosso));
            } else if (vite <= (vitetotali/2*2)){
                vitegioc.setTextColor(getContext().getResources().getColor(R.color.vitearancio));
            } else {
                vitegioc.setTextColor(getContext().getResources().getColor(R.color.viteverde));
            }
            nomegioc.setText(c.getName());
            vitegioc.setText("" + c.getVite());
            return convertView;
        }

}
