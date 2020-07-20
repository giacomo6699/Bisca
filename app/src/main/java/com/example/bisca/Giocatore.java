package com.example.bisca;

public class Giocatore {
    private String nome;
    private int vite;

    public Giocatore(String n, int v){
        nome = n;
        vite = v;
    }

    String getName(){
        return nome;
    }

    void setName(String n){
        if (n.equals(""))
            throw new IllegalArgumentException();
        nome = n;
    }

    int getVite(){
        return vite;
    }

    void setVite(int v){
        vite = v;
    }

    void decrementaVite(){
        if (vite != 0){
            vite--;
        }
    }

}
