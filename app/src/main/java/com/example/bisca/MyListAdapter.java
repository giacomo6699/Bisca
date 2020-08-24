package com.example.bisca;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

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
            TextView tvescl = convertView.findViewById(R.id.TVEsclamazione);
            ImageView imgpunt = convertView.findViewById(R.id.message_tail);
            ImageView imgspazio = convertView.findViewById(R.id.IMSpazioinPiu);
            ConstraintLayout vignetta = convertView.findViewById(R.id.costraintVignetta);
            nomegioc.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/montserratregular.ttf"));
            vitegioc.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/montserratmedium.ttf"));
            Giocatore c = getItem(position);

            Animation fadeIn = new AlphaAnimation(0, 1);
            fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
            fadeIn.setDuration(1000);

            Animation fadeOut = new AlphaAnimation(1, 0);
            fadeOut.setInterpolator(new AccelerateInterpolator()); //and this
            fadeOut.setStartOffset(3000);
            fadeOut.setDuration(1000);

            AnimationSet animation = new AnimationSet(false); //change to false

            if ((position % 2) == 0){
                linear.setBackgroundResource(R.drawable.rectanglecolor);
                nomegioc.setTextColor(Color.parseColor("#FFFFFFFF"));
                tvescl.setBackgroundResource(R.drawable.rectangle);
                tvescl.setTextColor(Color.parseColor("#EA252424"));
                imgpunt.setImageResource(R.drawable.puntinabianca);
                imgspazio.setImageResource(R.drawable.rectangle);
            }

            tvescl.setText(c.getFrase());
            if (c.getEsclazione()){
                /*ObjectAnimator animator = ObjectAnimator.ofFloat(vignetta, "translationX", 100f);
                animator.setDuration(2000);
                animator.start();*/
                animation.addAnimation(fadeIn);
                animation.addAnimation(fadeOut);
                vignetta.setAnimation(animation);
                vignetta.setVisibility(View.INVISIBLE);
            } else {
                vignetta.setVisibility(View.INVISIBLE);
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
