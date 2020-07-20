package com.example.bisca;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Random;

public class Partita extends AppCompatActivity {
    int d = 1;
    int e = 1;
    ListAdapter myadapter;
    int giocatori;
    int mani;
    int vite;
    GiocatoriList giocatorilist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partita);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView tvmazz = findViewById(R.id.TVMazziere);
        TextView tvcarte = findViewById(R.id.TVNumCarte);
        SharedPreferences sharedPreferences = getSharedPreferences("InfoGenerali", MODE_PRIVATE);
        giocatori = sharedPreferences.getInt("Numero Giocatori", 0);
        mani = sharedPreferences.getInt("Numero Mani", 0);
        vite = sharedPreferences.getInt("Numero Vite", 0);
        giocatorilist = new GiocatoriList();
        final ListView lista = findViewById(R.id.listView);
        for (int i = giocatori; i > 0; i--){
            LayoutInflater mInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            final View view = mInflater.inflate(R.layout.dialog, null, false);

            Dialog dialog = new AlertDialog.Builder(Partita.this).setView(view).setCancelable(false).setTitle(Html.fromHtml("<b> Giocatore" + i + "</b>"))
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            EditText ed = view.findViewById(R.id.ETDialog);
                            String name;
                            if (ed.getText().toString().isEmpty()){
                                name = "Giocatore" + d;
                            } else {
                                name = ed.getText().toString().toUpperCase();
                            }
                            Giocatore g = new Giocatore(name, vite);
                            giocatorilist.add(g);
                            myadapter = new MyListAdapter(getApplicationContext(), R.layout.item_list, giocatorilist.getList());
                            lista.setAdapter(myadapter);
                            d++;
                            if (d > giocatori) {
                                setTextMazziere();
                            }
                        }
                    }).create();
                    dialog.show();
        }
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final SharedPreferences sharedPreferences = getSharedPreferences("InfoGenerali", MODE_PRIVATE);
                final SharedPreferences shgioc = getSharedPreferences("InfoGiocatori", MODE_PRIVATE);
                final SharedPreferences.Editor editorgen = sharedPreferences.edit();
                final SharedPreferences.Editor editorgioc = shgioc.edit();

                for (int i = giocatori; i > 0; i--){
                    String nome = giocatorilist.get(i-1).getName();
                    if (giocatorilist.get(i-1).getVite() != 0) {
                        Dialog dialog = new AlertDialog.Builder(Partita.this).setCancelable(false).setMessage(Html.fromHtml("<b>" + nome + "</b>" + " ha perso una vita?")).setTitle(Html.fromHtml("<b> Fine Round </b>"))
                                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        String n = giocatorilist.get(e - 1).getName();
                                        int vitegioc = giocatorilist.get(e - 1).getVite();
                                        if (vitegioc == 1)
                                            Toast.makeText(Partita.this, n + " è stato eliminato.", Toast.LENGTH_SHORT).show();
                                        if (vitegioc == 2)
                                            Toast.makeText(Partita.this, n + " sta a pezzi", Toast.LENGTH_SHORT).show();
                                        giocatorilist.getFromName(n).decrementaVite();
                                        myadapter = new MyListAdapter(getApplicationContext(), R.layout.item_list, giocatorilist.getList());
                                        lista.setAdapter(myadapter);
                                        e++;
                                        if (e > giocatori){
                                            e = 1;
                                            AggiornamentoGenerali();
                                        }
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        e++;
                                        if (e > giocatori) {
                                            e = 1;
                                            AggiornamentoGenerali();
                                        }
                                    }
                                }).create();
                        dialog.show();
                    } else {
                        Dialog dialog = new AlertDialog.Builder(Partita.this).setCancelable(false).setMessage(Html.fromHtml("Regalare una vita a " +"<b>" + nome + "</b>")).setTitle(Html.fromHtml("<b> Fine Round </b>"))
                                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //ridai una vita
                                        giocatorilist.get(e-1).setVite(1);
                                        myadapter = new MyListAdapter(getApplicationContext(), R.layout.item_list, giocatorilist.getList());
                                        lista.setAdapter(myadapter);
                                        e++;
                                        if (e > giocatori) {
                                            e = 1;
                                            AggiornamentoGenerali();
                                        }
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        e++;
                                        if (e > giocatori) {
                                            e = 1;
                                            AggiornamentoGenerali();
                                        }
                                    }
                                }).create();
                        dialog.show();
                    }
                }

            }
        });

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int index = i;
                String nome = giocatorilist.get(i).getName();
                int vite = giocatorilist.get(i).getVite();
                LayoutInflater mInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                final View layout = mInflater.inflate(R.layout.dialogvite, null, false);
                final EditText ed = layout.findViewById(R.id.ETImpostaVite);
                ed.setText("" + vite);
                Dialog dialog = new AlertDialog.Builder(Partita.this).setCancelable(true).setView(layout).setTitle(Html.fromHtml("<b> Imposta Vite di " + nome + "</b>"))
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (!ed.getText().toString().isEmpty()){
                                    giocatorilist.get(index).setVite(Integer.parseInt(ed.getText().toString()));
                                    myadapter = new MyListAdapter(getApplicationContext(), R.layout.item_list, giocatorilist.getList());
                                    lista.setAdapter(myadapter);
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
        lista.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int index = i;
                Dialog dialog = new AlertDialog.Builder(Partita.this).setMessage("Vuoi davvero che sia " + giocatorilist.get(index).getName() + " a fare le carte?").setCancelable(true).setTitle(Html.fromHtml("<b> Mazziere </b>"))
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                int maniatt = getSharedPreferences("InfoGenerali", MODE_PRIVATE).getInt("Mani Attuali", 0);
                                getSharedPreferences("InfoGenerali", MODE_PRIVATE).edit().putInt("ID Mazziere", index + 1).commit();
                                getSharedPreferences("InfoGiocatori", MODE_PRIVATE).edit().putInt("Mazziere" + index + 1, maniatt).commit();
                                TextView tvMazziere = findViewById(R.id.TVMazziere);
                                TextView tvNumMani = findViewById(R.id.TVNumCarte);
                                String nomemazziere = giocatorilist.get(index).getName();
                                String carte = "<b>" + maniatt + "</b>" + " carte a testa";
                                tvNumMani.setText(Html.fromHtml(carte));
                                if (nomemazziere.trim().toLowerCase().equals("bomber"))
                                    nomemazziere = "IL " + nomemazziere;
                                nomemazziere = "Fa le carte " + "<b>" + nomemazziere + "</b> ";
                                tvMazziere.setText(Html.fromHtml(nomemazziere));
                                for (int e = 1; e <= giocatori; e++){
                                    if (e != index + 1)
                                        getSharedPreferences("InfoGiocatori", MODE_PRIVATE).edit().putInt("Mazziere" + e, 0).commit();
                                }
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .create();
                dialog.show();
                return true;
            }
        });
    }

    public void setTextMazziere(){
        Random r = new Random();
        int randomint = r.nextInt(giocatori) + 1;
        getSharedPreferences("InfoGenerali", MODE_PRIVATE).edit().putInt("ID Mazziere", randomint).commit();
        getSharedPreferences("InfoGiocatori", MODE_PRIVATE).edit().putInt("Mazziere" + randomint, mani).commit();
        TextView tvMazziere = findViewById(R.id.TVMazziere);
        TextView tvNumMani = findViewById(R.id.TVNumCarte);
        String nomemazziere = giocatorilist.get(randomint - 1).getName();
        String carte = "<b>" + mani + "</b>" + " carte a testa";
        tvNumMani.setText(Html.fromHtml(carte));
        if (nomemazziere.trim().toLowerCase().equals("bomber"))
            nomemazziere = "IL " + nomemazziere;
        nomemazziere = "Fa le carte " + "<b>" + nomemazziere + "</b> ";
        tvMazziere.setText(Html.fromHtml(nomemazziere));
    }


    void AggiornamentoGenerali(){
        final SharedPreferences sharedPreferences = getSharedPreferences("InfoGenerali", MODE_PRIVATE);
        final SharedPreferences shgioc = getSharedPreferences("InfoGiocatori", MODE_PRIVATE);
        final SharedPreferences.Editor editorgen = sharedPreferences.edit();
        final SharedPreferences.Editor editorgioc = shgioc.edit();
            int maniattuali = sharedPreferences.getInt("Mani Attuali", 0);
            int manitotali = sharedPreferences.getInt("Numero Mani", 0);
            int gioc = sharedPreferences.getInt("Numero Giocatori", 0);
            if (maniattuali == 1)
                maniattuali = manitotali;
            else
                maniattuali--;
            editorgen.putInt("Mani Attuali", maniattuali);
            String text = "<b>" + maniattuali + "</b>" + " carte a testa";
            if (maniattuali == 1)
                text = "<b>" + maniattuali + "</b>" + " carta a testa";
            ((TextView) findViewById(R.id.TVNumCarte)).setText(Html.fromHtml(text));
            editorgen.commit();


            int idmazziere = sharedPreferences.getInt("ID Mazziere", 0);
            if (idmazziere == gioc) {
                Log.d("primo", "si");
                idmazziere = 1;
            }
            else
                idmazziere++;
            while (giocatorilist.get(idmazziere - 1).getVite() == 0 && !isFinished()) {
                Log.d("secondo", "si");
                    if (idmazziere == gioc)
                        idmazziere = 1;
                    else
                        idmazziere++;
            }
            Log.d("MANIATTUALI", "" + maniattuali);
        Log.d("MANIMAZZIERE", "" + shgioc.getInt("Mazziere" + idmazziere, 0));
        Log.d("MAZZIEREID", "Mazziere" + idmazziere);
            if (shgioc.getInt("Mazziere" + idmazziere, 0) == maniattuali){
                Log.d("terzo", "si");
                if (idmazziere == gioc)
                    idmazziere = 1;
                else
                    idmazziere++;
                editorgioc.putInt("Mazziere" + idmazziere, maniattuali);
            } else {
                editorgioc.putInt("Mazziere" + idmazziere, maniattuali);
            }
            String s = giocatorilist.get(idmazziere-1).getName();
            if (s.toLowerCase().trim().equals("bomber")){
                ((TextView) findViewById(R.id.TVMazziere)).setText(Html.fromHtml("Fa le carte IL " + "<b>" + s + "</b>"));
            } else {
                ((TextView) findViewById(R.id.TVMazziere)).setText(Html.fromHtml("Fa le carte " + "<b>" + s + "</b>"));
            }

            editorgen.putInt("ID Mazziere", idmazziere);
            editorgen.commit();
            editorgioc.commit();

            int count = ingioco();
            if (count > 1){
                //non è ancora finita
            } else if (count == 1){
                //vincitore
                Intent intent = new Intent(getApplicationContext(), Vincitore.class);
                intent.putExtra("Nome", getNomeVincitore());
                startActivity(intent);
                finish();
            } else {
                //nessuno ha vinto! pareggio!
                Intent intent = new Intent(getApplicationContext(), Vincitore.class);
                intent.putExtra("Nome", "Pareggio");
                startActivity(intent);
                finish();
            }
    }

    public int ingioco(){
        int counter = 0;
        for (int i = 0; i < giocatorilist.getSize(); i++){
            if (giocatorilist.get(i).getVite() > 0){
                counter++;
            }
        }
        return counter;

    }

    public String getNomeVincitore(){
        String nome = "";
        for (int i = 0; i < giocatorilist.getSize(); i++){
            if (giocatorilist.get(i).getVite() > 0){
                nome = giocatorilist.get(i).getName();
            }
        }
        return nome;
    }

    public boolean isFinished(){
        for (int i = 0; i < giocatorilist.getSize(); i++){
            if (giocatorilist.get(i).getVite() > 0){
                return false;
            }
        }
        return true;
    }

}
