package com.example.bisca;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Vincitore extends AppCompatActivity {
    DatabaseReference DATARef;
    DataSnapshot DATABASE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vincitore);
        DATARef = FirebaseDatabase.getInstance().getReference();
        DATARef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DATABASE = dataSnapshot;
                int size = Integer.parseInt(DATABASE.child("Vincitori").child("Size").getValue().toString());
                Iterable<DataSnapshot> children = DATABASE.child("Vincitori").getChildren();
                for (DataSnapshot data : children){
                    if (!data.getKey().equals("Size")){
                        //allora aggiungi alla lista il nome del giocatore (data.getKey()) e le sue vincite (data.getValue())
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
