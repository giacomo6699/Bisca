package com.example.bisca;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.mobeta.android.dslv.DragSortListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Random;

public class Partita extends AppCompatActivity {
    int d = 1;
    int e = 1;
    ListAdapter myadapter;
    int giocatori;
    int mani;
    int vite;
    GiocatoriList giocatorilist;
    int IDMAZZCHOSEN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partita);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SharedPreferences sharedPreferences = getSharedPreferences("InfoGenerali", MODE_PRIVATE);
        giocatori = sharedPreferences.getInt("Numero Giocatori", 0);
        mani = sharedPreferences.getInt("Numero Mani", 0);
        vite = sharedPreferences.getInt("Numero Vite", 0);
        giocatorilist = new GiocatoriList();
        final DragSortListView lista = findViewById(R.id.listView);
        int playersnumber = Integer.parseInt(getIntent().getExtras().getString("PlayersNumber"));
        for (int i = 1; i <= playersnumber; i++){
            String name = getIntent().getExtras().getString("Player" + i);
            String frase = getIntent().getExtras().getString(name);
            giocatorilist.add(new Giocatore(name, vite, frase));
        }
        myadapter = new MyListAdapter(getApplicationContext(), R.layout.item_list, giocatorilist.getList());
        lista.setAdapter(myadapter);
        setTextMazziere();
        FloatingActionButton fab = findViewById(R.id.fab);
        ImageView imgfine = findViewById(R.id.BNFineRound);
        imgfine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final SharedPreferences sharedPreferences = getSharedPreferences("InfoGenerali", MODE_PRIVATE);
                final SharedPreferences shgioc = getSharedPreferences("InfoGiocatori", MODE_PRIVATE);
                final SharedPreferences.Editor editorgen = sharedPreferences.edit();
                final SharedPreferences.Editor editorgioc = shgioc.edit();

                AlertDialog.Builder builder = new AlertDialog.Builder(Partita.this);
                builder.setTitle("Chi ha perso una vita?");

                final String[] players = new String[ingioco()];
                final boolean[] checkedItems = new boolean[ingioco()];
                int counter = 0;
                for (int i = 0; i < giocatorilist.getSize(); i++){
                    if (giocatorilist.get(i).getVite() > 0){
                        players[counter] = giocatorilist.get(i).getName();
                        checkedItems[counter] = false;
                        counter++;
                    }
                }
                final ArrayList<Giocatore> checkedplayers = new ArrayList<>();
                builder.setMultiChoiceItems(players, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (isChecked)
                            checkedplayers.add(giocatorilist.getFromName(players[which]));
                        else
                            checkedplayers.remove(giocatorilist.getFromName(players[which]));
                    }
                });
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (int i = 0; i < checkedplayers.size(); i++){
                            String n = checkedplayers.get(i).getName();
                            int vitegioc = checkedplayers.get(i).getVite();
                            if (vitegioc == 1)
                                Toast.makeText(Partita.this, n + " è stato eliminato.", Toast.LENGTH_SHORT).show();
                            if (vitegioc == 2) {
                                //Toast.makeText(Partita.this, checkedplayers.get(i).getFrase(), Toast.LENGTH_LONG).show();
                                /*LayoutInflater inflater = getLayoutInflater();

                                View layout = inflater.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.custom_toast_layout_id));

                                TextView text = (TextView) layout.findViewById(R.id.TVToast);
                                text.setText(checkedplayers.get(i).getFrase());

                                Toast toast = new Toast(getApplicationContext());
                                toast.setGravity(Gravity.BOTTOM, 0, 350);
                                toast.setDuration(Toast.LENGTH_LONG);
                                toast.setView(layout);
                                toast.show();*/
                                checkedplayers.get(i).setEsclamazione(true);
                                myadapter = new MyListAdapter(getApplicationContext(), R.layout.item_list, giocatorilist.getList());
                                lista.setAdapter(myadapter);
                                Handler handler = new Handler();
                                final int index = i;
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        checkedplayers.get(index).setEsclamazione(false);
                                        //myadapter = new MyListAdapter(getApplicationContext(), R.layout.item_list, giocatorilist.getList());
                                        //lista.setAdapter(myadapter);
                                    }
                                }, 3000);
                            }
                            checkedplayers.get(i).decrementaVite();
                            myadapter = new MyListAdapter(getApplicationContext(), R.layout.item_list, giocatorilist.getList());
                            lista.setAdapter(myadapter);
                        }
                        AggiornamentoGenerali();
                    }
                });
                builder.setNegativeButton("Cancel", null);
                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });

        lista.setDropListener(new DragSortListView.DropListener() {
            @Override public void drop(int from, int to) {
                int fromindex = from;
                int toindex = to;
                Giocatore movedItem = giocatorilist.get(from);
                giocatorilist.remove(from);
                if (from > to) --from;
                giocatorilist.add(to, movedItem);
                myadapter = new MyListAdapter(getApplicationContext(), R.layout.item_list, giocatorilist.getList());
                lista.setAdapter(myadapter);

                final SharedPreferences sharedPreferences = getSharedPreferences("InfoGenerali", MODE_PRIVATE);
                final SharedPreferences.Editor editorgen = sharedPreferences.edit();
                int idmazziere = sharedPreferences.getInt("ID Mazziere", 0);
                Log.d("IDMAZZIERE", "" + idmazziere + " " + fromindex + " " + toindex);
                if (fromindex == idmazziere - 1){
                    idmazziere = toindex + 1;
                    Log.d("IDMAZZDROP1", "" + idmazziere);
                } else if (toindex > idmazziere - 1 && fromindex < idmazziere - 1){
                    Log.d("IDMAZZDROP2", "" + idmazziere);
                    idmazziere--;
                } else if (toindex <= idmazziere - 1 && fromindex > idmazziere - 1){
                    Log.d("IDMAZZDROP3", "" + idmazziere);
                    idmazziere++;
                }
                editorgen.putInt("ID Mazziere", idmazziere);
                editorgen.commit();
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
                final EditText edvite = layout.findViewById(R.id.ETImpostaVite);
                final EditText edname = layout.findViewById(R.id.ETImpostaNome);
                edvite.setText("" + vite);
                edname.setText(nome);
                Dialog dialog = new AlertDialog.Builder(Partita.this).setCancelable(true).setView(layout).setTitle(Html.fromHtml("<b> Imposta Dati di " + nome + "</b>"))
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (!edvite.getText().toString().isEmpty() && !edname.getText().toString().isEmpty()){
                                    giocatorilist.get(index).setVite(Integer.parseInt(edvite.getText().toString()));
                                    giocatorilist.get(index).setName(edname.getText().toString().toUpperCase());
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
        /*lista.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int index = i;
                Dialog dialog = new AlertDialog.Builder(Partita.this).setMessage("Vuoi davvero che sia " + giocatorilist.get(index).getName() + " a fare le carte?").setCancelable(true).setTitle(Html.fromHtml("<b> Mazziere </b>"))
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                int maniatt = getSharedPreferences("InfoGenerali", MODE_PRIVATE).getInt("Mani Attuali", 0);
                                getSharedPreferences("InfoGenerali", MODE_PRIVATE).edit().putInt("ID Mazziere", index + 1).commit();
                                getSharedPreferences("InfoGiocatori", MODE_PRIVATE).edit().putInt("Mazziere" + (index + 1), maniatt).commit();
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
        });*/
        TextView tvmazziere = findViewById(R.id.TVMazziere);
        tvmazziere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Partita.this);
                builder.setTitle("Chi deve fare le carte?");

                final String[] players = new String[ingioco()];
                int counter = 0;
                for (int i = 0; i < giocatorilist.getSize(); i++){
                    if (giocatorilist.get(i).getVite() > 0){
                        players[counter] = giocatorilist.get(i).getName();
                        counter++;
                    }
                }
                int id = getSharedPreferences("InfoGenerali", MODE_PRIVATE).getInt("ID Mazziere", 0) - 1;
                builder.setSingleChoiceItems(players, id, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        IDMAZZCHOSEN = giocatorilist.indexOf(giocatorilist.getFromName(players[which])) + 1;
                    }
                });
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int maniatt = getSharedPreferences("InfoGenerali", MODE_PRIVATE).getInt("Mani Attuali", 0);
                        getSharedPreferences("InfoGenerali", MODE_PRIVATE).edit().putInt("ID Mazziere", IDMAZZCHOSEN).commit();
                        getSharedPreferences("InfoGiocatori", MODE_PRIVATE).edit().putInt("Mazziere" + IDMAZZCHOSEN, maniatt).commit();
                        TextView tvMazziere = findViewById(R.id.TVMazziere);
                        TextView tvNumMani = findViewById(R.id.TVNumCarte);
                        String nomemazziere = giocatorilist.get(IDMAZZCHOSEN - 1).getName();
                        String carte = "<b>" + maniatt + "</b>" + " carte a testa";
                        tvNumMani.setText(Html.fromHtml(carte));
                        if (nomemazziere.trim().toLowerCase().equals("bomber"))
                            nomemazziere = "IL " + nomemazziere;
                        nomemazziere = "Fa le carte " + "<b>" + nomemazziere + "</b> ";
                        tvMazziere.setText(Html.fromHtml(nomemazziere));
                        for (int e = 1; e <= giocatori; e++){
                            if (e != IDMAZZCHOSEN)
                                getSharedPreferences("InfoGiocatori", MODE_PRIVATE).edit().putInt("Mazziere" + e, 0).commit();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        /*ImageView imginfamate = findViewById(R.id.ImgInfamate);
        imginfamate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int red= 255;
                int green= 0;
                int blue= 0;
                ThemeColors.setNewThemeColor(Partita.this, red, green, blue);
            }
        });*/
    }

    public void setTextMazziere(){
        Random r = new Random();
        int randomint = r.nextInt(giocatori); //+1
        getSharedPreferences("InfoGenerali", MODE_PRIVATE).edit().putInt("ID Mazziere", randomint + 1).commit();
        //getSharedPreferences("InfoGiocatori", MODE_PRIVATE).edit().putInt("Mazziere" + randomint, mani).commit();
        giocatorilist.get(randomint).setCartemesc(mani);
        TextView tvMazziere = findViewById(R.id.TVMazziere);
        TextView tvNumMani = findViewById(R.id.TVNumCarte);
        String nomemazziere = giocatorilist.get(randomint).getName();
        String carte = "<b>" + mani + "</b>" + " carte a testa";
        tvNumMani.setText(Html.fromHtml(carte));
        if (nomemazziere.trim().toLowerCase().equals("bomber"))
            nomemazziere = "IL " + nomemazziere;
        nomemazziere = "Fa le carte " + "<b>" + nomemazziere + "</b> ";
        tvMazziere.setText(Html.fromHtml(nomemazziere));
    }


    void AggiornamentoGenerali(){
        final SharedPreferences sharedPreferences = getSharedPreferences("InfoGenerali", MODE_PRIVATE);
        //final SharedPreferences shgioc = getSharedPreferences("InfoGiocatori", MODE_PRIVATE);
        final SharedPreferences.Editor editorgen = sharedPreferences.edit();
        //final SharedPreferences.Editor editorgioc = shgioc.edit();
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
        //Log.d("MANIMAZZIERE", "" + shgioc.getInt("Mazziere" + idmazziere, 0));
        //Log.d("MAZZIEREID", "Mazziere" + idmazziere);
            if (giocatorilist.get(idmazziere - 1).getCartemesc() == maniattuali && maniattuali == 1){
                giocatorilist.get(idmazziere - 1).setCartemesc(0);
                if (idmazziere == gioc)
                    idmazziere = 1;
                else
                    idmazziere++;
                Log.d("terzo", "si");
                while (giocatorilist.get(idmazziere - 1).getVite() == 0 && !isFinished()) {
                    if (idmazziere == gioc)
                        idmazziere = 1;
                    else
                        idmazziere++;
                }
                giocatorilist.get(idmazziere - 1).setCartemesc(maniattuali);
            } else {
                giocatorilist.get(idmazziere - 1).setCartemesc(maniattuali);
            }
            String s = giocatorilist.get(idmazziere-1).getName();
            if (s.toLowerCase().trim().equals("bomber")){
                ((TextView) findViewById(R.id.TVMazziere)).setText(Html.fromHtml("Fa le carte IL " + "<b>" + s + "</b>"));
            } else {
                ((TextView) findViewById(R.id.TVMazziere)).setText(Html.fromHtml("Fa le carte " + "<b>" + s + "</b>"));
            }

            editorgen.putInt("ID Mazziere", idmazziere);
            editorgen.commit();

            int count = ingioco();
            if (count > 1){
                //non è ancora finita
            } else if (count == 1){
                Dialog dialog = new AlertDialog.Builder(Partita.this).setMessage("Vuoi che il risultato di questa partita venga aggiunto all'albo dei vincitori?").setCancelable(false).setTitle(Html.fromHtml("<b> Albo </b>"))
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(getApplicationContext(), Vincitore.class);
                                intent.putExtra("Nome", getNomeVincitore());
                                intent.putExtra("Albo", "Si");
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(getApplicationContext(), Vincitore.class);
                                intent.putExtra("Nome", getNomeVincitore());
                                intent.putExtra("Albo", "No");
                                startActivity(intent);
                                finish();
                            }
                        })
                        .create();
                dialog.show();
                //vincitore
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menupartita, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.menuadd) {
            LayoutInflater mInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            final View layout = mInflater.inflate(R.layout.dialogvite, null, false);
            Dialog dialog = new AlertDialog.Builder(Partita.this).setView(layout).setCancelable(true).setTitle("Nuovo Giocatore")
                    .setPositiveButton("Aggiungi", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            EditText ednome = (EditText) layout.findViewById(R.id.ETImpostaNome);
                            EditText edvite = (EditText) layout.findViewById(R.id.ETImpostaVite);
                            String nome = ednome.getText().toString().toUpperCase();
                            Log.d("Vite nuovo giocatore", edvite.getText().toString());
                            int vite = Integer.parseInt(edvite.getText().toString());
                            giocatori++;
                            if (2 <= giocatori && giocatori <= 13 && 3 <= mani && mani <= 20 && mani*giocatori <= 40 && vite >= 1) {
                                //aggiungi giocatore
                                SharedPreferences sh = getSharedPreferences("InfoGenerali", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sh.edit();
                                editor.putInt("Numero Giocatori", giocatori);
                                editor.commit();

                                giocatorilist.add(new Giocatore(nome, vite));
                                myadapter = new MyListAdapter(getApplicationContext(), R.layout.item_list, giocatorilist.getList());
                                ListView lista = (ListView) findViewById(R.id.listView);
                                lista.setAdapter(myadapter);
                            } else {
                                giocatori--;
                                Toast.makeText(Partita.this, "Il giocatore non può essere aggiunto, limite raggiunto.", Toast.LENGTH_LONG).show();
                            }
                        }
                    })
                    .setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    }).create();
            dialog.show();
        }
        return true;
    }

}
