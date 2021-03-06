package com.example.bisca;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Typeface.createFromAsset(getAssets(),  "fonts/abc.ttf")
        final EditText ednumgioc = findViewById(R.id.ETNumGioc); //2 <= x <= 13
        final EditText ednummani = findViewById(R.id.ETNumMani); //3 <= x <= 20
        final EditText ednumvite = findViewById(R.id.ETNumVite);
        Button bnnuova = (Button) findViewById(R.id.BNnuovaPartita);
        bnnuova.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/montserratmedium.ttf"));
        bnnuova.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ednumgioc.getText().toString().isEmpty() || ednummani.getText().toString().isEmpty() || ednumvite.getText().toString().isEmpty())
                    Toast.makeText(MainActivity.this, "Inserire i valori", Toast.LENGTH_SHORT).show();
                else {
                    int gioc = Integer.parseInt(ednumgioc.getText().toString());
                    int mani = Integer.parseInt(ednummani.getText().toString());
                    int vite = Integer.parseInt(ednumvite.getText().toString());
                    if (2 <= gioc && gioc <= 13 && 3 <= mani && mani <= 20 && mani*gioc <= 40 && vite >= 1){
                        SharedPreferences sh = getSharedPreferences("InfoGenerali", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sh.edit();
                        editor.putInt("Numero Giocatori", gioc);
                        editor.putInt("Numero Mani",  mani);
                        editor.putInt("Numero Vite", vite);
                        editor.putInt("Mani Attuali", mani);
                        editor.putInt("Giocatori Attuali", gioc);
                        editor.commit();
                        Intent cio = new Intent(getApplicationContext(), Players.class);
                        startActivity(cio);
                        finish();
                    } else
                        Toast.makeText(MainActivity.this, "I valori inseriti non sono corretti", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menumain, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.menuinfo) {
            Dialog dialog = new AlertDialog.Builder(MainActivity.this).setMessage(Html.fromHtml("Icons by Icons8 - <b> https://icons8.com </b>")).setCancelable(true).setTitle("Info")
                    .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    }).create();
            dialog.show();
        } else if (item.getItemId() == R.id.menuregolamento) {
            Intent intent = new Intent(getApplicationContext(), Regolamento.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(getApplicationContext(), Vincitore.class);
            intent.putExtra("Nome", "Albo");
            intent.putExtra("Albo", "No");
            startActivity(intent);
        }
        return true;
    }

}
