package com.example.bisca;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class Vincitore extends AppCompatActivity {
    DatabaseReference DATARef;
    DataSnapshot DATABASE;
    boolean datadone = false;
    boolean albo = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vincitore);
        final String nome = getIntent().getExtras().getString("Nome");
        String alboextra = getIntent().getExtras().getString("Albo");
        if (alboextra.equals("Si"))
            albo = true;
        else
            albo = false;
        DATARef = FirebaseDatabase.getInstance().getReference();
        DATARef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!datadone) {
                    DATABASE = dataSnapshot;
                    Map<String, Object> mapvalues = (Map<String, Object>) dataSnapshot.child("Vincitori").getValue();
                    Iterable<DataSnapshot> children = DATABASE.child("Vincitori").getChildren();
                    GiocatoriList albogiocatori = new GiocatoriList();
                    boolean done = false;
                    for (DataSnapshot data : children) {
                        Log.d("IDDATABASE", data.getKey());
                        Log.d("VALUEDATABASE", "" + data.getValue());
                        if (!data.getKey().equals("Size")) {
                            if (data.getKey().toLowerCase().equals(nome.toLowerCase()) && albo) {
                                //bisogna solo incrementare di uno il contatore delle vincite del giocatore
                                done = true;
                                int counter = Integer.parseInt(data.getValue().toString()) + 1;
                                mapvalues.put(data.getKey(), "" + counter);
                                albogiocatori.add(new Giocatore(data.getKey(), counter));
                            } else {
                                int counter = Integer.parseInt(data.getValue().toString());
                                albogiocatori.add(new Giocatore(data.getKey(), counter));
                            }
                            //allora aggiungi alla lista il nome del giocatore (data.getKey()) e le sue vincite (data.getValue())
                        }
                    }
                    if (!done && albo) {
                        //aggiungere un nuovo giocatore nell'albo, impostare il suo contatore a 1 e incrementare Size
                        int size = Integer.parseInt(DATABASE.child("Vincitori").child("Size").getValue().toString()) + 1;
                        mapvalues.put(nome, "1");
                        mapvalues.put("Size", "" + size);
                        albogiocatori.add(new Giocatore(nome, 1));
                    }
                    albogiocatori.sortByLives();
                    GiocatoriList alboordinato = new GiocatoriList();
                    for (int i = albogiocatori.getSize() - 1; i >= 0; i--) {
                        alboordinato.add(albogiocatori.get(i));
                    }
                    if (albo)
                        DATARef.child("Vincitori").updateChildren(mapvalues);
                    TextView tv1posto = (TextView) findViewById(R.id.PrimoPosto);
                    TextView tv2posto = (TextView) findViewById(R.id.SecondoPosto);
                    TextView tv3posto = (TextView) findViewById(R.id.TerzoPosto);
                    if (alboordinato.getSize() > 0) {
                        tv1posto.setText(alboordinato.get(0).getName() + ", " + alboordinato.get(0).getVite());
                        alboordinato.remove(0);
                    }
                    if (alboordinato.getSize() > 0) {
                        tv2posto.setText(alboordinato.get(0).getName() + ", " + alboordinato.get(0).getVite());
                        alboordinato.remove(0);
                    }
                    if (alboordinato.getSize() > 0) {
                        tv3posto.setText(alboordinato.get(0).getName() + ", " + alboordinato.get(0).getVite());
                        alboordinato.remove(0);
                    }
                    ListView listView = (ListView) findViewById(R.id.listaAlbo);
                    MyListAdapterAlbo adapter = new MyListAdapterAlbo(getApplicationContext(), R.layout.albo_item_list, alboordinato.getList());
                    listView.setAdapter(adapter);
                    datadone = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        TextView tv = findViewById(R.id.TVVincitore);
        getSupportActionBar().hide();
        if (nome.equals("Pareggio")){
            //?? finita in pareggio
            tv.setText("La partit?? ?? finita in pareggio");
        } else if (nome.equals("Albo")) {
            //si vuole vedere solo l'albo
            tv.setText("");
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
