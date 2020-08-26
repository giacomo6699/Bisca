package com.example.bisca;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Map;

public class Players extends AppCompatActivity {
    DatabaseReference DATARef;
    DataSnapshot DATABASE;
    GiocatoriList giocatoriList;
    GiocatoriList chosenList;
    ListView lista;
    MyListAdapterPlayers adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_players);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        chosenList = new GiocatoriList();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater mInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                final View layout = mInflater.inflate(R.layout.dialogfrase, null, false);

                Dialog dialog = new AlertDialog.Builder(Players.this).setView(layout).setCancelable(true).setTitle(Html.fromHtml("<b> Nuovo Giocatore </b>"))
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                EditText ed = layout.findViewById(R.id.ETDialogNome);
                                EditText edfrase = layout.findViewById(R.id.ETDialogFrase);
                                String frase;
                                if (edfrase.getText().toString().isEmpty())
                                    frase = "A PEZZI";
                                else
                                    frase = edfrase.getText().toString();
                                String name;
                                if (!ed.getText().toString().isEmpty()) {
                                    name = ed.getText().toString().toUpperCase();
                                    Map<String, Object> mapvalues = (Map<String, Object>) DATABASE.child("Giocatori").getValue();
                                    if (mapvalues.containsKey(name)){
                                        Toast.makeText(Players.this, "Giocatore già esistente", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Giocatore g = new Giocatore(name, 0, frase);
                                        giocatoriList.add(g);
                                        adapter = new MyListAdapterPlayers(getApplicationContext(), R.layout.item_list_players, giocatoriList.getList());
                                        lista.setAdapter(adapter);
                                        mapvalues.put(name.toUpperCase(), frase);
                                        DATARef.child("Giocatori").updateChildren(mapvalues);
                                    }
                                    //AGGIUNGI ANCHE SUL DATABASE
                                }
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        }).create();
                dialog.show();
            }
        });
        lista = (ListView) findViewById(R.id.PlayersList);
        giocatoriList = new GiocatoriList();
        DATARef = FirebaseDatabase.getInstance().getReference();
        DATARef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DATABASE = dataSnapshot;
                giocatoriList.clear();
                Iterable<DataSnapshot> children = DATABASE.child("Giocatori").getChildren();
                for (DataSnapshot data : children) {
                    giocatoriList.add(new Giocatore(data.getKey(), 0, data.getValue().toString()));
                    Log.d("IDDATABASE", data.getKey());
                    Log.d("VALUEDATABASE", "" + data.getValue());
                }
                adapter = new MyListAdapterPlayers(getApplicationContext(), R.layout.item_list_players, giocatoriList.getList());
                lista.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (giocatoriList.get(i).getVite() == 0) {
                    giocatoriList.get(i).setVite(1);
                    chosenList.add(giocatoriList.get(i));
                } else {
                    giocatoriList.get(i).setVite(0);
                    chosenList.remove(giocatoriList.get(i));
                }
                adapter = new MyListAdapterPlayers(getApplicationContext(), R.layout.item_list_players, giocatoriList.getList());
                lista.setAdapter(adapter);
            }
        });
        lista.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int index = i;
                LayoutInflater mInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                final View layout = mInflater.inflate(R.layout.dialogfrase, null, false);
                final EditText ed = layout.findViewById(R.id.ETDialogFrase);
                final EditText ednome = layout.findViewById(R.id.ETDialogNome);
                ed.setHint("Esclamazione");
                ed.setText(giocatoriList.get(i).getFrase());
                ednome.setText(giocatoriList.get(i).getName());
                final String frase = giocatoriList.get(i).getFrase();
                final String nome = giocatoriList.get(i).getName();
                Dialog dialog = new AlertDialog.Builder(Players.this).setView(layout).setCancelable(true).setTitle(Html.fromHtml("Esclamazione di " + nome))
                        .setPositiveButton("Salva", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Map<String, Object> mapvalues = (Map<String, Object>) DATABASE.child("Giocatori").getValue();
                                if (nome.equals(ednome.getText().toString())){
                                    mapvalues.put(nome, ed.getText().toString());
                                    DATARef.child("Giocatori").updateChildren(mapvalues);
                                } else {
                                    Map<String, Object> mapwins = (Map<String, Object>) DATABASE.child("Vincitori").getValue();
                                    int vittorie = Integer.parseInt(mapwins.get(nome).toString());
                                    DATABASE.child("Vincitori").child(nome).getRef().removeValue();
                                    DATABASE.child("Giocatori").child(nome).getRef().removeValue();
                                    mapvalues.put(ednome.getText().toString(), ed.getText().toString());
                                    mapvalues.remove(nome);
                                    mapwins.put(ednome.getText().toString(), "" + vittorie);
                                    mapwins.remove(nome);
                                    DATARef.child("Giocatori").updateChildren(mapvalues);
                                    DATARef.child("Vincitori").updateChildren(mapwins);
                                }
                            }
                        })
                        .setNegativeButton("Elimina Giocatore", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                DATABASE.child("Vincitori").child(nome).getRef().removeValue();
                                DATABASE.child("Giocatori").child(nome).getRef().removeValue();
                            }
                        })
                        .create();
                dialog.show();
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menuplayers, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        int counter = 1;
        if (item.getItemId() == R.id.menuavanti) {
            Intent intent = new Intent(getApplicationContext(), Partita.class);
            for (int i = 0; i < chosenList.getSize(); i++) {
                intent.putExtra("Player" + counter, chosenList.get(i).getName());
                intent.putExtra(chosenList.get(i).getName(), chosenList.get(i).getFrase());
                counter++;
            }
            counter--;
            if (counter == getSharedPreferences("InfoGenerali", MODE_PRIVATE).getInt("Numero Giocatori", 0)) {
                intent.putExtra("PlayersNumber", "" + counter);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Il numero di giocatori deve essere " + getSharedPreferences("InfoGenerali", MODE_PRIVATE).getInt("Numero Giocatori", 0), Toast.LENGTH_SHORT).show();
            }
        }
        return true;
    }


}
