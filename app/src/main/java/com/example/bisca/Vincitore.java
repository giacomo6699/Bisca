package com.example.bisca;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Vincitore extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vincitore);
        String nome = getIntent().getExtras().getString("Nome");
        TextView tv = findViewById(R.id.TVVincitore);
        getSupportActionBar().hide();
        if (nome.equals("Pareggio")){
            //è finita in pareggio
            tv.setText("La partità è finita in pareggio");
        } else {
            //abbiamo un vincitore
            tv.setText(Html.fromHtml("<b>" + nome + "</b>" + " ha vinto!"));
        }
        Button BNNuova = findViewById(R.id.BNVincNuova);
        Button BNRipeti = findViewById(R.id.BNRipeti);
        BNNuova.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        BNRipeti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //ci devo pensare cavoli
            }
        });
    }
}
